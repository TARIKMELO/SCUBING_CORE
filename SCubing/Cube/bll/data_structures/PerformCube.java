package bll.data_structures;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.data_structures.nodes.NodeSimple;
import bll.parallel.Resource;
import bll.util.Util;
import dal.drivers.CubeColumn;
import dal.drivers.IResultSetText;
import dal.drivers.ShapeFileReader;
import dal.drivers.ShapeFileWriter;
import presentation.layout.MapFrame;

public class PerformCube<N extends NodeSimple<DimensionTypeValue>> {

	private FeatureSource<SimpleFeatureType, SimpleFeature> CreateCube(HashMap<String, CubeColumn> cubeColumns, int x, int y, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) throws Exception{
		//conectar
		//Cálculo do tempo de computação do cubo
		long tempoInicial = System.currentTimeMillis(); 
		ShapeFileReader<DimensionTypeValue> shapeFileReader = new ShapeFileReader<DimensionTypeValue>(featureSource,cubeColumns);
		IResultSetText<DimensionTypeValue> rs = shapeFileReader.getData();
		long tempoIntermediario0 = System.currentTimeMillis();  
		Util.getLogger().info("(Parcial  - GetData do Shapefile) Tempo em milisegundos para ler os dados: "+ (tempoIntermediario0 - tempoInicial) );
				
		Util.getLogger().info("Número de linhas da entrada: "+rs.getFetchSize());
		
		long inicialBaseCuboide = System.currentTimeMillis(); 
		ICubeSimple<DimensionTypeValue> cube = createBaseCuboide(rs, cubeColumns);
		//cubeGrid.performHierarchies(x,y,rs, shapeFileReader.getSource(),cubeColumns);
		long finalBaseCuboide = System.currentTimeMillis();  
		Util.getLogger().info("(Parcial 0 - Criar o cubo base - createBaseCuboide) Tempo em milisegundos para calcular o base cuboide: "+ (finalBaseCuboide - inicialBaseCuboide) );


		long inicialGenerateAggregations = System.currentTimeMillis(); 
		cube.generateAggregations();

		long finalGenerateAggregations = System.currentTimeMillis();  
		Util.getLogger().info("(Parcial 1 - Gerou as agregações - cube.generateAggregations()) Tempo em milisegundos para calcular o cubo: "+ (finalGenerateAggregations - inicialGenerateAggregations) );


		Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource= cube.cubeToTable();

		long tempoIntermediario2 = System.currentTimeMillis();  
		Util.getLogger().info("(Parcial 2 - Tempo cubeToTable) Tempo em milisegundos para calcular o cubo: "+ (tempoIntermediario2 - tempoInicial) );

		ShapeFileWriter shapeFileWriter = new ShapeFileWriter(cubeColumns);

		long insertCubeToSourceInicial = System.currentTimeMillis(); 
		//FeatureSource sourceDesti = shapeFileWriter.insertCubeToSource(hashResult, shapeFileReader.getSource());
		FeatureSource<SimpleFeatureType, SimpleFeature> sourceDesti = shapeFileWriter.insertCubeToSource(resource, shapeFileReader.getSource(), 0);
		long insertCubeToSourceFinal = System.currentTimeMillis(); 
		Util.getLogger().info("(Parcial 3 - PARTE PARALELIZADA: Tempo insertCubeToSource) Tempo em milisegundos para calcular o cubo: "+ (insertCubeToSourceFinal - insertCubeToSourceInicial) );


		//dfasdf//JOptionPane.showMessageDialog(null, tempoTotal);

		//Exportar o resultado para o Postgis
		long tempoPostGisInicial = System.currentTimeMillis();  
		insertToPostGis(sourceDesti);
		long tempoPostGisFinal = System.currentTimeMillis(); 
		Util.getLogger().info("(Parcial 4 - Tempo para inserir no Postgis): "+ (tempoPostGisFinal - tempoPostGisInicial));
		

		//Cálculo do tempo de computação do cubo
		long tempoFinal = System.currentTimeMillis();  
		
		Util.getLogger().info("RESULTADO: Número de registros:  "+ sourceDesti.getFeatures().size());
		Util.getLogger().info("RESULTADO: Tempo total em milisegundos: "+ (tempoFinal - tempoInicial) );
		Util.getLogger().info("RESULTADO: Tempo total em segundos: "+ (tempoFinal - tempoInicial) / 1000d);

		
		Util.getLogger().info("------------------------------Termínou o log da execução------------------------------");
		return sourceDesti;
	}

	public ICubeSimple<DimensionTypeValue> createBaseCuboide(IResultSetText<DimensionTypeValue> rs,HashMap<String, CubeColumn> cubeColumns ) throws SQLException
	{

		//TODO: Olhar o padrao adapter para colocar o nome da coluna ao invés do indice
		//-1 por causa da coluna que é a medida
		ICubeSimple<DimensionTypeValue> cube = new CubeSimple<DimensionTypeValue>(cubeColumns,cubeColumns.size());


		Object[] tuple;
		ArrayList<MeasureTypeValue>  measures;
		Object measureValue;
		Object attributeO;
		DimensionTypeValue typeValu;

		NodeSimple<DimensionTypeValue> n;
		try{
			while((tuple=rs.next())!=null){

				measures = new ArrayList<MeasureTypeValue>();
				for (Entry<String, CubeColumn> cubeColumn: cubeColumns.entrySet())
				{
					if(cubeColumn.getValue().isMeasure())
					{
						//measures = new ArrayList<MeasureTypeValue>();
						measureValue = tuple[cubeColumn.getValue().getIndex()];
						measures.add(new MeasureTypeValue( ((DimensionTypeValue)measureValue).getValue(),cubeColumn.getValue().getColumnName()));
					}
				} 
				for (Entry<String, CubeColumn> cubeColumn: cubeColumns.entrySet())
				{
					if(!cubeColumn.getValue().isMeasure())
					{
						attributeO = tuple[cubeColumn.getValue().getIndex()];

						typeValu = new DimensionTypeValue(((DimensionTypeValue)attributeO).getValue(), cubeColumn.getKey());
						n = cube.findNode(typeValu);
						if(n == null){
							n = new NodeSimple<DimensionTypeValue>(cubeColumns, measures);
							cube.insertNode(typeValu, n);
						}
						else
						{
							n.updateMeasure(measures);
						}
					}
				}
				cube.refresh();
			}
		}
		finally{
			rs.close();
		}

		return cube;
	}

	public void gerarCubo(HashMap<String, CubeColumn> cubeColumns, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) 
	{
		HashMap<Integer, ArrayList<CubeColumn> > hierarquias = new HashMap<Integer, ArrayList<CubeColumn> >();
		try
		{
			final HashMap<String, CubeColumn> commonCubeColumns = new HashMap<String, CubeColumn>();
			for (CubeColumn cubeColumn : cubeColumns.values()) {
				if (cubeColumn.isMeasure()||cubeColumn.getHierarchy()==-1)
				{
					commonCubeColumns.put(cubeColumn.getColumnName(), cubeColumn);
				}

			}


			for (CubeColumn cubeColumn : cubeColumns.values()) {
				if (!cubeColumn.isMeasure())
				{
					if (cubeColumn.getHierarchy()!=-1){

						if (hierarquias.get(cubeColumn.getHierarchy())==null)
						{
							hierarquias.put(cubeColumn.getHierarchy(),new ArrayList<CubeColumn>()); 
							hierarquias.get(cubeColumn.getHierarchy()).addAll(commonCubeColumns.values());
						}
						hierarquias.get(cubeColumn.getHierarchy()).add(cubeColumn);
					}
				}
			}

			//Deepy Copy
			HashMap<String, CubeColumn> cubeColumnsAux = new HashMap<String, CubeColumn>();

			for (ArrayList<CubeColumn> cubeColumnList : hierarquias.values()) {
				int auxT = 0;
				for (CubeColumn cubeColumn : cubeColumnList) {
					cubeColumn.setIndex(auxT);
					auxT++;
					cubeColumnsAux.put(cubeColumn.getColumnName(), cubeColumn);
					Util.getLogger().info(cubeColumn);
				}
				try {

					FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceCube =  CreateCube(cubeColumnsAux,0 ,0, featureSource);



					//MapFrame.getInstance().createLayer(featureSourceCube);



					cubeColumnsAux = new HashMap<String, CubeColumn>();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		catch(Exception ioEx){
			ioEx.printStackTrace();
		}
	}



	public void insertToPostGis(FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceCube ) throws IOException{

		//Map<String, Object> connectionParameters = wizard.getConnectionParameters();
		DataStore dataStore = Util.connectPostGis();

		dataStore.createSchema(featureSourceCube.getSchema());

		
		Transaction transaction = new DefaultTransaction("create");
		
		
		SimpleFeatureSource source = dataStore.getFeatureSource(featureSourceCube.getSchema().getTypeName());

		if (source instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) source;

			featureStore.setTransaction(transaction);

			try { 
				
				
				featureStore.addFeatures(featureSourceCube.getFeatures());
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
			//System.exit(0); // success!
		} else {
			System.out.println("Table does not support read/write access");
			System.exit(1);
		}

	}

}
