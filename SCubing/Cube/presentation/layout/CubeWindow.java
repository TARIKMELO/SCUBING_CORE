package presentation.layout;
/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.simple.SimpleFeatureSource;

import bll.aggregation_functions.AFAppend;
import bll.aggregation_functions.AFAvg;
import bll.aggregation_functions.AFCount;
import bll.aggregation_functions.AFMax;
import bll.aggregation_functions.AFMin;
import bll.aggregation_functions.AFSum;
import bll.aggregation_functions.IAggFunction;
import bll.aggregation_functions.MAFEuclideDistance;
import bll.aggregation_functions.SAFBuffer;
import bll.aggregation_functions.SAFDiference;
import bll.aggregation_functions.SAFDistance;
import bll.aggregation_functions.SAFIntersection;
import bll.aggregation_functions.SAFTouche;
import bll.aggregation_functions.SAFUnion;
import bll.aggregation_functions.SAFUnionMBR;
import bll.aggregation_functions.SAFUnionPolygon;
import bll.aggregation_functions.aggFuncFactory;
import bll.data_structures.CubeGrid;
import bll.data_structures.PerformCube;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.util.Util;
import dal.drivers.CubeColumn;
import dal.drivers.IResultSetText;
import dal.drivers.ShapeFileReader;

public class CubeWindow {

	public org.eclipse.swt.widgets.Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="71,31"

	private Button button2 = null;
	static ArrayList< Hashtable<String, String>> columnsInfo;

	private boolean isClosing = false;

	private static final String title = "Gerar Cubo";

	protected static final String ComponentPortToConnectCol = null;

	private Table tblMedidas = null;

	ArrayList<String> funcAgg;  
	ArrayList<String> funcAggGeo;  //  @jve:decl-index=0:
	ArrayList<String> funcAggStr;

	private TabFolder tabFolder = null;

	private Composite compositeRegularCube = null;



	private Table tblDimensao = null;

	private Composite compositeGridCube = null;

	private Group groupHierarquias = null;

	private Table tableGrid = null;  //  @jve:decl-index=0:visual-constraint="205,344"

	private Group groupRegarVizinhanca = null;

	private Label labelX = null;

	private Label labelY = null;

	private Text textX = null;

	private Text textY = null;

	private Button checkBoxVisualizar = null;

	private Button buttonGerarGrid = null;

	private Button buttonExportCubeXML = null;

	private Button buttonImportCubeXML = null;

	private Button buttonGerarCubo = null;

	private Group groupColXY = null;

	private Label labelNomeColunaX = null;

	private Label labelNomeColunaY = null;

	private Text textNomeColX = null;

	private Text textNomeColY = null;

	private Label labelNomeId = null;

	private Text textNomeId = null;

	/**
	 * This method initializes tabFolder	
	 *
	 */
	private void createTabFolder() {
		tabFolder = new TabFolder(sShell, SWT.NONE);
		ShapeFileReader<String> umaConexao = new ShapeFileReader<String>(featureSource,null);
		columnsInfo = umaConexao.getColumnsInfo();
		createCompositeRegularCube();
		createCompositeGridCube();
		tabFolder.setBounds(new Rectangle(16, 15, 584, 546));
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Gerar Cubo");
		tabItem1.setControl(compositeRegularCube);
		TabItem tabItem8 = new TabItem(tabFolder, SWT.NONE);
		tabItem8.setText("Cubo por Grid");
		tabItem8.setControl(compositeGridCube);
	}
	/**
	 * This method initializes compositeRegularCube	
	 *
	 */
	private void createCompositeRegularCube() {
		compositeRegularCube = new Composite(tabFolder, SWT.NONE);


		buttonGerarCubo = new Button(compositeRegularCube, SWT.NONE);
		buttonGerarCubo.setBounds(new Rectangle(459, 484, 107, 25));
		buttonGerarCubo.setText("Gerar Cubo");

		buttonExportCubeXML = new Button(compositeRegularCube, SWT.NONE);
		buttonExportCubeXML.setBounds(new Rectangle(332, 484, 107, 25));
		buttonExportCubeXML.setText("Exportar para XML");


		buttonImportCubeXML = new Button(compositeRegularCube, SWT.NONE);
		buttonImportCubeXML.setBounds(new Rectangle(205, 484, 107, 25));
		buttonImportCubeXML.setText("Importar XML");


		createGroupDimensoes();
		createGroupMedidas();

		//buttonExportCubeXML.addListener(SWT.Selection, new CubeSchemaToXmlAction(generateCubeColumns()));

		buttonExportCubeXML.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

				File file = Util.getNewFile("xml");
				Util.saveCubeColumnsInXml(file, generateCubeColumns());
			}
		});




		buttonImportCubeXML.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				//				if (validacoesGerarCubo())
				//				{
				File file = Util.getFile("xml");
				preencherTela(Util.loadCubeColumnsFromXml(file));
				//gerarCubo(Util.loadCubeColumnsInXml(file));
				//sShell.redraw();
				//gerarCubo();
				//				} 
				//				else
				//				{
				//					sShell.setActive();
				//					sShell.setFocus();
				//					
				//				}
			}
		});




		buttonGerarCubo
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					//JProgressWindow window = new JProgressWindow(null);
					//window.started();

					if (validacoesGerarCubo())
					{

						//TODO: Ajustar os parametros
						sShell.getDisplay().timerExec(10, new Runnable() {
							int i = 0;
							public void run() {
								if (progressBar.isDisposed()) return;
								progressBar.setVisible(true);
								//progressBar.setSelection(i++);
								//if (i <= progressBar.getMaximum()) sShell.getDisplay().timerExec(10, this);
							}
						});
						//countThread.start();
						//buttonGerarCubo.setEnabled(false);

						gerarCubo();
					}
					else
					{

						sShell.setFocus();
						sShell.redraw();
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} // TODO Auto-generated Event stub widgetSelected()
			}
		});
	}
	/**
	 * This method initializes compositeGridCube	
	 *
	 */
	private void createCompositeGridCube() {
		compositeGridCube = new Composite(tabFolder, SWT.NONE);
		createGroupHierarquias();
		buttonGerarGrid = new Button(compositeGridCube, SWT.NONE);
		buttonGerarGrid.setBounds(new Rectangle(424, 482, 133, 25));
		buttonGerarGrid.setText("Gerar Cubo Grid");
		createGroupGridMedidas();

		buttonGerarGrid
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				gerarCuboGrid(); // TODO Auto-generated Event stub widgetSelected()
			}
		});
	}
	/**
	 * This method initializes groupHierarquias	
	 *
	 */
	private void createGroupHierarquias() {
		groupHierarquias = new Group(compositeGridCube, SWT.NONE);
		groupHierarquias.setText("Configuraãães");
		groupHierarquias.setBounds(new Rectangle(12, 13, 551, 154));
		createGroupRegarVizinhanca();
		checkBoxVisualizar = new Button(groupHierarquias, SWT.CHECK);
		checkBoxVisualizar.setBounds(new Rectangle(20, 109, 161, 16));
		checkBoxVisualizar.setText("Visualizar");
		createGroupColXY();
	}
	/**
	 * This method initializes groupRegarVizinhanca	
	 *
	 */
	private void createGroupRegarVizinhanca() {
		groupRegarVizinhanca = new Group(groupHierarquias, SWT.NONE);
		groupRegarVizinhanca.setText("Regra de Vizinhaãa");
		groupRegarVizinhanca.setLayout(null);
		groupRegarVizinhanca.setBounds(new Rectangle(18, 19, 164, 83));
		labelX = new Label(groupRegarVizinhanca, SWT.NONE);
		labelX.setText("Eixo X:");
		labelX.setBounds(new Rectangle(8, 23, 34, 19));
		textX = new Text(groupRegarVizinhanca, SWT.BORDER);
		textX.setLocation(new Point(47, 20));
		textX.setSize(new Point(100, 21));
		labelY = new Label(groupRegarVizinhanca, SWT.NONE);
		labelY.setText("Eixo Y:");
		labelY.setBounds(new Rectangle(8, 49, 34, 21));
		textY = new Text(groupRegarVizinhanca, SWT.BORDER);
		textY.setLocation(new Point(47, 46));
		textY.setSize(new Point(100, 21));
	}
	/**
	 * This method initializes groupColXY	
	 *
	 */
	private void createGroupColXY() {
		groupColXY = new Group(groupHierarquias, SWT.NONE);
		groupColXY.setText("Configurar Colunas");
		groupColXY.setLayout(null);
		groupColXY.setBounds(new Rectangle(203, 18, 227, 116));
		labelNomeColunaX = new Label(groupColXY, SWT.NONE);
		labelNomeColunaX.setText("Nome da Coluna X:");
		labelNomeColunaX.setBounds(new Rectangle(8, 23, 103, 15));
		textNomeColX = new Text(groupColXY, SWT.BORDER);
		textNomeColX.setText("col");
		textNomeColX.setBounds(new Rectangle(119, 20, 97, 21));
		labelNomeColunaY = new Label(groupColXY, SWT.NONE);
		labelNomeColunaY.setText("Nome da Coluna Y:");
		labelNomeColunaY.setBounds(new Rectangle(8, 49, 103, 15));
		textNomeColY = new Text(groupColXY, SWT.BORDER);
		textNomeColY.setText("lin");
		textNomeColY.setBounds(new Rectangle(119, 46, 97, 21));
		labelNomeId = new Label(groupColXY, SWT.NONE);

		labelNomeId.setText("Nome Identificador:");
		labelNomeId.setBounds(new Rectangle(8, 75, 106, 15));
		textNomeId = new Text(groupColXY, SWT.BORDER);
		textNomeId.setText("id");
		textNomeId.setBounds(new Rectangle(119, 72, 97, 21));
	}
	/**
	 * This method initializes groupGridMedidas	
	 *
	 */
	private void createGroupGridMedidas() {
		groupGridMedidas = new Group(compositeGridCube, SWT.NONE);
		groupGridMedidas.setLayout(null);
		groupGridMedidas.setText("Medidas");
		groupGridMedidas.setBounds(new Rectangle(13, 178, 549, 300));
		tableGrid = new Table(groupGridMedidas, SWT.CHECK | SWT.NONE);
		tableGrid.setHeaderVisible(true);
		tableGrid.setLinesVisible(true);

		createTableGrid();
		tableGrid.setBounds(new Rectangle(9, 18, 528, 270));
	}
	/**
	 * This method initializes groupDimensoes	
	 *
	 */
	private void createGroupDimensoes() {
		groupDimensoes = new Group(compositeRegularCube, SWT.NONE);
		groupDimensoes.setLayout(null);
		groupDimensoes.setText("Dimensães");
		groupDimensoes.setBounds(new Rectangle(8, 5, 561, 228));

		tblDimensao = new Table(groupDimensoes, SWT.CHECK | SWT.NONE);
		tblDimensao.setHeaderVisible(true);
		tblDimensao.setLinesVisible(true);
		tblDimensao.setBounds(new Rectangle(8, 15, 545, 203));
		createTableDimensions();
	}
	/**
	 * This method initializes groupMedidas	
	 *
	 */
	private void createGroupMedidas() {
		groupMedidas = new Group(compositeRegularCube, SWT.NONE);
		groupMedidas.setLayout(null);
		groupMedidas.setText("Medidas");
		groupMedidas.setBounds(new Rectangle(9, 239, 561, 244));

		tblMedidas = new Table(groupMedidas,SWT.CHECK| SWT.NONE);
		tblMedidas.setHeaderVisible(true);
		tblMedidas.setLinesVisible(true);
		tblMedidas.setBounds(new Rectangle(8, 15, 547, 224));
		createTableMeasures();
	}

	//TODO:
	//String fileInput = "D:\\data\\EstadosBrasil.shp";  //  @jve:decl-index=0:

	private Group groupGridMedidas = null;

	private Group groupDimensoes = null;

	private Group groupMedidas = null;

	private ProgressBar progressBar = null;

	private SimpleFeatureSource featureSource;

	public CubeWindow(SimpleFeatureSource featureSource)
	{

		this.featureSource = featureSource;
	}

	/**
	 * This method initializes sShell
	 */
	public void createSShell() {
		sShell = new org.eclipse.swt.widgets.Shell();
		sShell.setText(title);
		sShell.setLayout(null);
		button2 = new Button(sShell, SWT.NONE);
		button2.setText("Exit");
		button2.setBounds(new Rectangle(17, 599, 77, 25));
		sShell.setSize(new Point(626, 668));


		createTabFolder();
		progressBar = new ProgressBar(sShell, SWT.INDETERMINATE);
		progressBar.setBounds(new Rectangle(16, 572, 580, 17));
		progressBar.setVisible(false);
		button2
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				doExit();
			}
		});
		sShell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				if (!isClosing) {
					e.doit = doExit();
				}
			}
		});
	}

	public void conectar()
	{
		ShapeFileReader<String> umaConexao = new ShapeFileReader<String>(featureSource,null);
		columnsInfo = umaConexao.getColumnsInfo();
		createTableDimensions();
		createTableMeasures();
		createTableGrid();
	}

	public void gerarCuboGrid() 
	{
		int x = Integer.parseInt(textX.getText());
		int y = Integer.parseInt(textY.getText());

		String nomeColX = textNomeColX.getText().trim();
		String nomeColY = textNomeColY.getText().trim();
		String nomeColId = textNomeId.getText().trim();
		int aux=0;
		HashMap<String, CubeColumn> cubeColumns = new HashMap<String, CubeColumn>();

		cubeColumns.put("geom", new CubeColumn(new SAFUnion(),true,aux,"geom"));
		aux++;

		for (TableItem measureItem : tableGrid.getItems()) {
			if(measureItem.getChecked())
			{	
				if ( aggFuncFactory.getAggFunction(measureItem.getText(3),"")!=null)
				{
					String nameColumn = measureItem.getText(1);
					IAggFunction aggFunction = aggFuncFactory.getAggFunction(measureItem.getText(3),measureItem.getText(4));
					cubeColumns.put(nameColumn, new CubeColumn(aggFunction,true,aux,nameColumn));
					aux++;
					//System.out.println(measureItem.getText(3));
				}

				else
				{
					String nameColumn = measureItem.getText(1);
					cubeColumns.put(nameColumn, new CubeColumn(null,false,aux,nameColumn));
					aux++;
				}
			}
		}

		if (cubeColumns.containsKey(nomeColX))
		{

			JOptionPane.showMessageDialog(null, "Não é possível usar uma dimensão como medida!");
			return;
		}

		if (cubeColumns.containsKey(nomeColY))
		{

			JOptionPane.showMessageDialog(null, "Não é possível usar uma dimensão como medida!");
			return;
		}

		if (cubeColumns.containsKey(nomeColId))
		{

			JOptionPane.showMessageDialog(null, "Não é possível usar uma dimensão como medida!");
			return;
		}




		cubeColumns.put(nomeColX, new CubeColumn(null,false,aux,nomeColX));
		aux++;
		cubeColumns.put(nomeColY, new CubeColumn(null,false,aux,nomeColY));
		aux++;
		cubeColumns.put(nomeColId, new CubeColumn(null,false,aux,nomeColId));

		aux++;
		try {
			//Main5 main = new Main5(cubeColumns,x ,y);

			ShapeFileReader<DimensionTypeValue> shapeFileReader = new ShapeFileReader<DimensionTypeValue>(featureSource,cubeColumns);
			//ShapeFileReader<DimensionTypeValue> shapeFileReader = new ShapeFileReader<DimensionTypeValue>("d:\\data\\Grid\\brasilGrid_pol.shp",cubeColumns);
			IResultSetText<DimensionTypeValue> rs = shapeFileReader.getData();
			CubeGrid cubeGrid = new CubeGrid(nomeColX,nomeColY,nomeColId); 
			
			long tempoInicial = System.currentTimeMillis(); 
	
			
			cubeGrid.performHierarchies(x, y, rs, shapeFileReader.getSource(),cubeColumns,0);
			
			//Cálculo do tempo de computação do cubo
			long tempoFinal = System.currentTimeMillis();  
			Util.getLogger().info("RESULTADO: Tempo total em milisegundos: "+ (tempoFinal - tempoInicial) );
			Util.getLogger().info("RESULTADO: Tempo total em segundos: "+ (tempoFinal - tempoInicial) / 1000d);

			rs.close();


			doExit();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void preencherTela(HashMap<String, CubeColumn> cubeColumns)
	{


		for (CubeColumn  cubeColumn : cubeColumns.values()) {


			if(cubeColumn.isMeasure)
			{	

				for (TableItem measureItem : tblMedidas.getItems()) {
					if(cubeColumn.getColumnName().equals(measureItem.getText(1)))
					{	
						measureItem.setChecked(true);

						((Combo)measureItem.getData()).setText(cubeColumn.getAggFunction().toString());
						measureItem.setText(3, cubeColumn.getAggFunction().toString());
					}
				}
			} 

			else
			{

				for (TableItem dimensionItem : tblDimensao.getItems()) {
					String nameColumn = dimensionItem.getText(1);
					if(cubeColumn.getColumnName().equals(nameColumn))
					{
						dimensionItem.setChecked(true);
						TableEditor editorHierarquia = (TableEditor) dimensionItem.getData();
						((Text)editorHierarquia.getEditor()).setText(cubeColumn.getHierarchy()+"");
						//						if (cubeColumn.getWhere()!=null)
						//						{
						//							dimensionItem.setText(3,cubeColumn.getWhere());
						//						}
					} 
				}

			}


			//System.out.println(dimensionItem.getChecked());
		} 
	}

	public HashMap<String, CubeColumn> generateCubeColumns()
	{

		final HashMap<String, CubeColumn> cubeColumns = new HashMap<String, CubeColumn>();

		for (TableItem measureItem : tblMedidas.getItems()) {
			if(measureItem.getChecked())
			{	
				String nameColumn = measureItem.getText(1);
				IAggFunction aggFunction = aggFuncFactory.getAggFunction(measureItem.getText(3),measureItem.getText(4));
				cubeColumns.put(nameColumn, new CubeColumn(aggFunction,true,0,nameColumn));

			}

		}

		for (TableItem dimensionItem : tblDimensao.getItems()) {
			if(dimensionItem.getChecked())
			{
				String nameColumn = dimensionItem.getText(1);
				CubeColumn cubeColumn = new CubeColumn(null,false,0,nameColumn);
				if (dimensionItem.getText(4).trim().equals(""))
				{
					cubeColumn.setHierarchy(-1);
				}
				else
				{
					cubeColumn.setHierarchy((Integer.parseInt(dimensionItem.getText(4))-1));
				}
				if (dimensionItem.getText(3).trim()!="")
					cubeColumn.setWhere(dimensionItem.getText(3).trim());
				cubeColumns.put(nameColumn, cubeColumn);

				//}
			}
			//System.out.println(dimensionItem.getChecked());
		} 
		return cubeColumns;
	}



	public boolean validacoesGerarCubo()
	{
		final HashMap<String, CubeColumn> cubeColumns = generateCubeColumns();
		//colocar as validacoes aqui
		boolean existHierachy = false;
		boolean existeMedida = false;
		boolean existeDimensao = false;

		if (cubeColumns== null || cubeColumns.values().size()<=0)
		{

			JOptionPane.showMessageDialog(null, "Nenhuma dimensão/medida selecionada.");
			return false;
		}

		for (CubeColumn  cubeColumn : cubeColumns.values()) {

			if (cubeColumn.isMeasure())
			{
				existeMedida = true;
			}

			if (!cubeColumn.isMeasure)
			{
				existeDimensao = true;
			}

			if (cubeColumn.getHierarchy()>=0)
			{
				existHierachy = true;
			}
		}

		if (!existHierachy)
		{
			JOptionPane.showMessageDialog(null, "Favor definir uma hierarquia.");
			return false;
		}

		if (!existeMedida)
		{
			JOptionPane.showMessageDialog(null, "Favor selecionar uma ou mais medidas.");
			return false;
		}

		if (!existeDimensao)
		{
			JOptionPane.showMessageDialog(null, "Favor definir um ou mais dimensões");
			return false;
		}
		return true;

	}

	public void gerarCubo() 
	{
		try
		{

			final HashMap<String, CubeColumn> cubeColumns = generateCubeColumns();


			Thread computeThread = new Thread(){
				//Deepy Copy
				HashMap<String, CubeColumn> cubeColumnsAux = new HashMap<String, CubeColumn>();
				//TODO: Modularizar isso melhor
				public void run() {
					PerformCube performCube = new PerformCube();
					performCube.gerarCubo(cubeColumns, featureSource);
					doExit();
				}
			};
			computeThread.start();	
			//testee
		}
		catch(Exception ioEx){
			//textAreaStatus.setText("Cubo gerado com sucesso!");
			ioEx.printStackTrace();
		}
	}


	public void gerarCubo(final HashMap<String, CubeColumn> cubeColumns) 
	{
		try
		{

			Thread computeThread = new Thread(){
				//Deepy Copy
				HashMap<String, CubeColumn> cubeColumnsAux = new HashMap<String, CubeColumn>();
				//TODO: Modularizar isso melhor
				public void run() {
					PerformCube performCube = new PerformCube();
					performCube.gerarCubo(cubeColumns, featureSource);
					doExit();
				}
			};
			computeThread.start();	
		}
		catch(Exception ioEx){
			//textAreaStatus.setText("Cubo gerado com sucesso!");
			ioEx.printStackTrace();
		}
	}


	public void createTableGrid()
	{
		TableColumn tableColumnSel = new TableColumn(tableGrid, SWT.NONE,0);
		tableColumnSel.setWidth(70);
		tableColumnSel.setText("Selecionar");

		TableColumn tableColumnMed = new TableColumn(tableGrid, SWT.NONE,1);
		tableColumnMed.setWidth(100);
		tableColumnMed.setText("Medida");

		TableColumn tblColTipMed = new TableColumn(tableGrid, SWT.NONE,2);
		tblColTipMed.setWidth(100);
		tblColTipMed.setText("Tipo");


		TableColumn tblColFunAgg = new TableColumn(tableGrid, SWT.NONE,3);
		tblColFunAgg.setWidth(100);
		tblColFunAgg.setText("Função de Agg.");


		TableColumn tblValor = new TableColumn(tableGrid, SWT.NONE,4);
		tblValor.setWidth(72);
		tblValor.setText("Valor");

		for (Hashtable<String, String> hashtable : columnsInfo) {
			//TODO:
			if (!hashtable.get("NOME").equals("geom") && !hashtable.get("NOME").equals(textNomeColX.getText().trim())  && !hashtable.get("NOME").equals(textNomeColY.getText().trim()) && !hashtable.get("NOME").equals(textNomeId.getText().trim()))
			{
				final TableItem tableItem=new TableItem(tableGrid,SWT.NONE);

				TableEditor editor = new TableEditor(tableGrid);
				tableItem.setText(1,hashtable.get("NOME"));
				tableItem.setText(2,hashtable.get("TIPO"));


				Combo combo = new Combo(tableGrid, SWT.READ_ONLY|SWT.DROP_DOWN|SWT.CENTER);

				combo.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						tableItem.setText(3,((Combo)e.widget).getText());
					}
				});
				//combo.setItems(new String[]{"aaa","bbb","pune"});
				//String[] teste = getFuncAggDescriptions(hashtable.get("TIPO"));

				combo.setItems(getFuncAggDescriptions(hashtable.get("TIPO")));
				combo.pack();
				editor.minimumWidth = 100;
				//editor.minimumWidth = combo.getSize ().x;
				editor.setEditor(combo, tableItem, 3);
				tableItem.setText(4,"");
			}
		}

	}

	public void createTableDimensions()
	{

		TableColumn tblColSelDim = new TableColumn(tblDimensao, SWT.NONE,0);
		tblColSelDim.setWidth(70);
		tblColSelDim.setText("Selecionar");

		TableColumn tblColDim = new TableColumn(tblDimensao, SWT.NONE,1);
		tblColDim.setWidth(100);
		//tblColDim.setWidth(tblDimensoes.getGridLineWidth()/tblDimensoes.getColumnCount());
		tblColDim.setText("Dimensão");


		TableColumn tblColTipDim = new TableColumn(tblDimensao, SWT.NONE,2);
		tblColTipDim.setWidth(100);
		tblColTipDim.setText("Tipo");


		TableColumn tblColWhere = new TableColumn(tblDimensao, SWT.NONE,3);
		tblColWhere.setWidth(100);
		tblColWhere.setText("Where");


		TableColumn tblHierarquia = new TableColumn(tblDimensao, SWT.NONE,4);
		tblHierarquia.setWidth(100);
		tblHierarquia.setText("Hierarquia");

		//tblColSelDim.setWidth(380/tblDimensao.getColumnCount());
		//tblColDim.setWidth(425/tblDimensao.getColumnCount());
		//tblColTipDim.setWidth(425/tblDimension.getColumnCount());

		for (Hashtable<String, String> hashtable : columnsInfo) {
			final TableItem  tableItem=new TableItem(tblDimensao,SWT.NONE);
			tableItem.setText(1,hashtable.get("NOME"));
			tableItem.setText(2,hashtable.get("TIPO"));
			Text text = new Text(tblDimensao, SWT.LEFT);
			final TableEditor editorHierarquia = new TableEditor(tblDimensao);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					Text text = (Text) editorHierarquia.getEditor();
					editorHierarquia.getItem().setText(4, text.getText());
				}
			});

			editorHierarquia.minimumWidth = 90;
			editorHierarquia.setEditor(text, tableItem, 4);
			tableItem.setData(editorHierarquia);
			text = new Text(tblDimensao, SWT.LEFT);
			//final TableItem tableItemWhere=new TableItem(tblDimensao,SWT.NONE);
			final TableEditor editorWhere = new TableEditor(tblDimensao);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					Text text = (Text) editorWhere.getEditor();
					editorWhere.getItem().setText(3, text.getText());
				}
			});
			editorWhere.minimumWidth = 90;
			editorWhere.setEditor(text, tableItem, 3);
			//tableItem.setText(3,"");

			/*	Text textH = new Text(tblDimensao, SWT.LEFT);

			textH.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					Text textH = (Text) editor.getEditor();
					editor.getItem().setText(4, textH.getText());
				}
			});
			 */


		}

	}

	public void createTableMeasures()
	{
		TableColumn tableColumnSel = new TableColumn(tblMedidas, SWT.NONE,0);
		tableColumnSel.setWidth(70);
		tableColumnSel.setText("Selecionar");

		TableColumn tableColumnMed = new TableColumn(tblMedidas, SWT.NONE,1);
		tableColumnMed.setWidth(100);
		tableColumnMed.setText("Medida");

		TableColumn tblColTipMed = new TableColumn(tblMedidas, SWT.NONE,2);
		tblColTipMed.setWidth(100);
		tblColTipMed.setText("Tipo");


		TableColumn tblColFunAgg = new TableColumn(tblMedidas, SWT.NONE,3);
		tblColFunAgg.setWidth(100);
		tblColFunAgg.setText("Função de Agg.");


		TableColumn tblValor = new TableColumn(tblMedidas, SWT.NONE,4);
		tblValor.setWidth(72);
		tblValor.setText("Valor");

		for (Hashtable<String, String> hashtable : columnsInfo) {

			final TableItem tableItem=new TableItem(tblMedidas,SWT.NONE);

			TableEditor editor = new TableEditor(tblMedidas);
			tableItem.setText(1,hashtable.get("NOME"));
			tableItem.setText(2,hashtable.get("TIPO"));


			Combo combo = new Combo(tblMedidas, SWT.READ_ONLY|SWT.DROP_DOWN|SWT.CENTER);

			combo.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {


					tableItem.setText(3,((Combo)e.widget).getText());
				}
			});
			//combo.setItems(new String[]{"aaa","bbb","pune"});
			//String[] teste = getFuncAggDescriptions(hashtable.get("TIPO"));

			combo.setItems(getFuncAggDescriptions(hashtable.get("TIPO")));
			combo.pack();
			editor.minimumWidth = 100;
			//editor.minimumWidth = combo.getSize ().x;
			editor.setEditor(combo, tableItem, 3);
			tableItem.setData(combo);


			Text text = new Text(tblMedidas, SWT.LEFT);
			final TableEditor editor2 = new TableEditor(tblMedidas);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					Text text = (Text) editor2.getEditor();
					editor2.getItem().setText(4, text.getText());
				}
			});
			editor2.minimumWidth = 60;
			editor2.setEditor(text, tableItem, 4);
			tableItem.setText(4,"");
		}
	}

	private String[] getFuncAggDescriptions(String type)
	{
		//TODO: Usar o factory aqui

		if (type.equals("Point") || type.equals("MultiPoint") )
		{
			if (funcAggGeo==null)
			{
				funcAggGeo = new ArrayList<String>();
				//TODO: O que  esse 1 fixo aqui
				funcAggGeo.add((new SAFDistance(1)).toString());
				funcAggGeo.add((new SAFDiference()).toString());
				funcAggGeo.add((new SAFBuffer(1)).toString());
				funcAggGeo.add((new SAFTouche()).toString());
				funcAggGeo.add((new SAFIntersection()).toString());
				//funcAggGeo.add((new SAFNeighborhood()).toString());
				funcAggGeo.add((new SAFUnion()).toString());
				funcAggGeo.add((new SAFUnionMBR()).toString());
				funcAggGeo.add((new SAFUnionPolygon()).toString());


			}
			return funcAggGeo.toArray(new String[funcAggGeo.size()]);
		}
		else if (type.equals("MultiPolygon") || type.equals("Polygon"))
		{
			if (funcAggGeo==null)
			{
				funcAggGeo = new ArrayList<String>();
				//TODO: O que ã esse 1 fixo aqui
				funcAggGeo.add((new SAFDistance(1)).toString());
				funcAggGeo.add((new SAFDiference()).toString());
				funcAggGeo.add((new SAFBuffer(1)).toString());
				funcAggGeo.add((new SAFTouche()).toString());
				funcAggGeo.add((new SAFIntersection()).toString());
				//funcAggGeo.add((new SAFNeighborhood()).toString());
				funcAggGeo.add((new SAFUnion()).toString());
				funcAggGeo.add((new SAFUnionMBR()).toString());


			}
			return funcAggGeo.toArray(new String[funcAggGeo.size()]);
		}

		else if (type.equals("String"))
		{
			if (funcAggStr==null)
			{
				funcAggStr = new ArrayList<String>();
				//funcAggStr.add((new AFCount()).toString());
				funcAggStr.add((new AFAppend()).toString());

			}
			return funcAggStr.toArray(new String[funcAggStr.size()]);
		}
		else
		{
			if (funcAgg==null)
			{
				funcAgg = new ArrayList<String>();
				funcAgg.add((new AFAvg()).toString());
				funcAgg.add((new AFMax()).toString());
				funcAgg.add((new AFMin()).toString());
				funcAgg.add((new AFSum()).toString());
				funcAgg.add((new MAFEuclideDistance()).toString());
				//funcAgg.add((new AFCount()).toString());
			}
			return funcAgg.toArray(new String[funcAgg.size()]);
		}
	}

	private synchronized boolean doExit() {
		sShell.getDisplay().getDefault().syncExec(new Runnable() {
			public void run() {
				isClosing = true;
				sShell.close();
				sShell.dispose();
			}
		});
		return true;
	}
}
