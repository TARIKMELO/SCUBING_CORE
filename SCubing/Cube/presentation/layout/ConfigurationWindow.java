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
import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import bll.util.ConfigBean;
import bll.util.Util;

public class ConfigurationWindow {

	public org.eclipse.swt.widgets.Shell sShell = null; //  @jve:decl-index=0:visual-constraint="19,7"

	private Table tableConfiguration = null;

	private Button button1 = null;

	private Button button2 = null;

	private boolean hasChanged = false;
	private boolean isClosing = false;

	private static final String title = "Simple Text Editor";

	private static final String NEW_LINE = System.getProperty("line.separator");

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		/* Before this is run, be sure to set up the following in the launch configuration 
		 * (Arguments->VM Arguments) for the correct SWT library path. 
		 * The following is a windows example:
		 * -Djava.library.path="installation_directory\plugins\org.eclipse.swt.win32_3.0.0\os\win32\x86"
		 */
		org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display
				.getDefault();
		ConfigurationWindow thisClass = new ConfigurationWindow();
		thisClass.createSShell();
		thisClass.sShell.open();

		while (!thisClass.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * This method initializes sShell
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void createSShell() throws IllegalArgumentException, IllegalAccessException {
		sShell = new org.eclipse.swt.widgets.Shell();
		org.eclipse.swt.layout.GridLayout gridLayout2 = new GridLayout();
		org.eclipse.swt.layout.GridData gridData3 = new org.eclipse.swt.layout.GridData();
		org.eclipse.swt.layout.GridData gridData5 = new org.eclipse.swt.layout.GridData();
		org.eclipse.swt.layout.GridData gridData6 = new org.eclipse.swt.layout.GridData();
		tableConfiguration = new Table(sShell, SWT.NONE);
		tableConfiguration.setHeaderVisible(true);
		tableConfiguration.setLinesVisible(true);
		button1 = new Button(sShell, SWT.NONE);
		button2 = new Button(sShell, SWT.NONE);
		sShell.setText(title);
		sShell.setLayout(gridLayout2);
		gridLayout2.numColumns = 3;
		gridLayout2.makeColumnsEqualWidth = false;
		gridData3.horizontalSpan = 3;
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		tableConfiguration.setLayoutData(gridData3);
		button1.setText("Save File");
		button1.setLayoutData(gridData5);
		button2.setText("Exit");
		button2.setLayoutData(gridData6);
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData6.grabExcessHorizontalSpace = true;
		sShell.setSize(new org.eclipse.swt.graphics.Point(393, 279));
		createTableDimensions();
		button2.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				doExit();
			}
		});
		button1
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				saveFile();
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

	private void loadFile() {

	}
	//CubeConfig cubeConfig;  //  @jve:decl-index=0:


	private void saveFile() {
		ConfigBean configBean = Util.getConfig();
		try {
			for (TableItem measureItem : tableConfiguration.getItems()) {

				Field field;

				field = configBean.getClass().getField(measureItem.getText(0));


				field.set(configBean,measureItem.getText(1));
				Util.saveConfig();
			}

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean doExit() {
		if (hasChanged) {
			MessageBox mb = new MessageBox(sShell, SWT.ICON_QUESTION | SWT.YES
					| SWT.NO | SWT.CANCEL);
			mb.setText("Save Changes?");
			mb.setMessage("File has been changed. Save before exit?");
			int state = mb.open();
			if (state == SWT.YES) {
				saveFile();
			} else if (state == SWT.CANCEL) {
				return false;
			}
		}
		isClosing = true;
		sShell.close();
		sShell.dispose();
		return true;
	}



	public void createTableDimensions() throws IllegalArgumentException, IllegalAccessException
	{


		//cubeConfig = new CubeConfig();

		TableColumn tblColSelDim = new TableColumn(tableConfiguration, SWT.NONE,0);
		tblColSelDim.setWidth(200);
		tblColSelDim.setText("Propriedades");

		TableColumn tblColDim = new TableColumn(tableConfiguration, SWT.NONE,1);
		tblColDim.setWidth(150);
		//tblColDim.setWidth(tblDimensoes.getGridLineWidth()/tblDimensoes.getColumnCount());
		tblColDim.setText("Valor");

		ConfigBean configBean = Util.getConfig();
		if (configBean!=null && configBean.getClass()!=null)
			for (Field field : configBean.getClass().getDeclaredFields()) {

				final TableItem  tableItem=new TableItem(tableConfiguration,SWT.NONE);
				tableItem.setText(0,field.getName());
				String value = field.get(configBean)+"";
				tableItem.setText(1,value);

				Text text = new Text(tableConfiguration, SWT.LEFT);
				text.setText(value);
				final TableEditor editor = new TableEditor(tableConfiguration);

				text.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(1, text.getText());
					}
				});
				//editor.getItem().setText(1,(int)field.getInt(configBean)+"");
				editor.minimumWidth = 140;


				editor.setEditor(text, tableItem, 1);


			}


	}








}
