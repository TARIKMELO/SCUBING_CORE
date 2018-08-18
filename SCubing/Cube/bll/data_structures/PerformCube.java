package bll.data_structures;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.sun.javafx.embed.swing.Disposer;

import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.data_structures.nodes.NodeSimple;
import bll.parallel.Resource;
import bll.util.Util;
import dal.drivers.CubeColumn;
import dal.drivers.ShapeFileReader;
import dal.drivers.ShapeFileUtilities;
import dal.drivers.ShapeFileWriter;

public class PerformCube<N extends NodeSimple<DimensionTypeValue>> {

	private void CreateCube(HashMap<String, CubeColumn> cubeColumns, int x, int y, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) throws Exception{
		//conectar
		//Cálculo do tempo de computação do cubo
		long tempoInicial = System.currentTimeMillis(); 
		//ShapeFileReader<DimensionTypeValue> shapeFileReader = new ShapeFileReader<DimensionTypeValue>(featureSource,cubeColumns);
		//IResultSetText<DimensionTypeValue> rs = shapeFileReader.getData();
		long tempoIntermediario0 = System.currentTimeMillis();  
		Util.getLogger().info("Tempo em milisegundos para ler os dados: "+ (tempoIntermediario0 - tempoInicial) );

		//Util.getLogger().info("Número de linhas da entrada: "+rs.getFetchSize());

		//JUNTAR O shapeFileReader.getData() E O createBaseCuboide(rs, cubeColumns)


		long inicialBaseCuboide = System.currentTimeMillis(); 
		ICubeSimple<DimensionTypeValue> cube = createBaseCuboide(cubeColumns, featureSource);
		//cubeGrid.performHierarchies(x,y,rs, shapeFileReader.getSource(),cubeColumns);
		long finalBaseCuboide = System.currentTimeMillis();  
		Util.getLogger().info("(Parcial 0 - Criar o cubo base - createBaseCuboide) Tempo em milisegundos para calcular o base cuboide: "+ (finalBaseCuboide - inicialBaseCuboide) );


		long inicialGenerateAggregations = System.currentTimeMillis(); 
		cube.generateAggregations();

		long finalGenerateAggregations = System.currentTimeMillis();  
		Util.getLogger().info("(Parcial 1 - Gerou as agregações - cube.generateAggregations()) Tempo em milisegundos para calcular o cubo: "+ (finalGenerateAggregations - inicialGenerateAggregations) );


		//Criação do Resource - No Restource já vem um vetor com as geometrias. O Consumidor que realiza a união.
		Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource= cube.cubeToTable();

		//resource.
		//Gravar/Serializar todo o Resource em um arquivo.

		//DiskDataUtil.SaveResourceToDisk(resource);  
		//TOOD: Lê em porções 


		long tempoIntermediario2 = System.currentTimeMillis();  
		Util.getLogger().info("(Parcial 2 - Tempo cubeToTable) Tempo em milisegundos para calcular o cubo: "+ (tempoIntermediario2 - tempoInicial) );
		ShapeFileWriter shapeFileWriter = new ShapeFileWriter(cubeColumns);
		long insertCubeToSourceInicial = System.currentTimeMillis(); 
		//FeatureSource sourceDesti = shapeFileWriter.insertCubeToSource(hashResult, shapeFileReader.getSource());


		final SimpleFeatureType TYPE = shapeFileWriter.createCubeSchema(featureSource);

		Util.insertBlanckToPostGis(TYPE);
		DataStore dataStore = Util.connectPostGis();
		Transaction transaction = new DefaultTransaction("add");
		SimpleFeatureSource source = dataStore.getFeatureSource(TYPE.getTypeName());
		SimpleFeatureStore featureStore = (SimpleFeatureStore) source;
		featureStore.setTransaction(transaction);



		shapeFileWriter.applyAggFunctionInStarTree(featureStore,transaction,resource, TYPE, 0);
		System.out.println("-------------Commit------------");
		transaction.commit();
		transaction.close();
		long insertCubeToSourceFinal = System.currentTimeMillis(); 
		Util.getLogger().info("(Parcial 3 - PARTE PARALELIZADA: Tempo applyAggFunctionInStarTree) Tempo em milisegundos para calcular o cubo e fazer as uniões: "+ (insertCubeToSourceFinal - insertCubeToSourceInicial) );

		//Cálculo do tempo de computação do cubo
		long tempoFinal = System.currentTimeMillis();  

		//Util.getLogger().info("RESULTADO: Número de registros:  "+ sourceDesti.getFeatures().size());
		Util.getLogger().info("RESULTADO: Tempo total em milisegundos: "+ (tempoFinal - tempoInicial) );
		Util.getLogger().info("RESULTADO: Tempo total em segundos: "+ (tempoFinal - tempoInicial) / 1000d);


		Util.getLogger().info("------------------------------Termínou o log da execução------------------------------");
		//return sourceDesti;

	}

	public ICubeSimple<DimensionTypeValue> createBaseCuboide(HashMap<String, CubeColumn> cubeColumns, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) throws SQLException, CQLException
	{



		//TODO: Olhar o padrao adapter para colocar o nome da coluna ao invés do indice
		//-1 por causa da coluna que é a medida
		ICubeSimple<DimensionTypeValue> cube = new CubeSimple<DimensionTypeValue>(cubeColumns,cubeColumns.size());
		//IResultSetText<DimensionTypeValue> rs;
		//IResultSetText<T> rs = new ResultSetText<T>();
		//rs.configure(cubeColumns);


		ArrayList<MeasureTypeValue>  measures;
		Object measureValue;
		Object attributeO;
		DimensionTypeValue typeValu;

		NodeSimple<DimensionTypeValue> n;



		int i = 0;
		try {
			//			String where="";
			//			for (CubeColumn cubeColumn : cubeColumns.values()) {
			//				if(cubeColumn.getWhere()!=null)
			//				{
			//					where+=" AND "+cubeColumn.getColumnName()+" "+cubeColumn.getWhere() ;
			//				}
			//			} 
			//			FeatureCollection<SimpleFeatureType, SimpleFeature> diskMemory;
			//			if (where == "")
			//			{
			//
			//				diskMemory = featureSource.getFeatures();
			//			}
			//			else
			//			{
			//				where = where.replaceFirst("AND", "");
			//				Filter filter = CQL.toFilter(where);
			//				diskMemory = featureSource.getFeatures(filter);
			//			}








			try (FeatureIterator<SimpleFeature> iterator = featureSource.getFeatures().features()){
				while( iterator.hasNext() ){
					i++;
					if ((i%10000)==00) {
						System.out.println(i);
				//	System.gc();
					}
					//System.out.println("iterator");
					SimpleFeature feature = iterator.next();
					Object[] tuple = ShapeFileUtilities.formatShapefileLine(feature,cubeColumns);
					//					for(int i=0; i<str.length; i++)
					//					{
					//						rs.updateData(i, (DimensionTypeValue)(str[i]));
					//					}
					//
					//					//REFRESH
					//					rs.insertRow();




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
					tuple = null;

					feature = null;


				}
			}




		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Erro no método getData()");
			System.out.println("Parooooou no "+i);
			System.exit(-1);
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

				CreateCube(cubeColumnsAux,0 ,0, featureSource);

			}
		}
		catch(Exception ioEx){
			ioEx.printStackTrace();
		}
	}



	//	public void insertToPostGis(FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceCube ) throws IOException{
	//		DataStore dataStore = Util.connectPostGis();
	//		dataStore.createSchema(featureSourceCube.getSchema());
	//		Transaction transaction = new DefaultTransaction("create");
	//		SimpleFeatureSource source = dataStore.getFeatureSource(featureSourceCube.getSchema().getTypeName());
	//		if (source instanceof SimpleFeatureStore) {
	//			SimpleFeatureStore featureStore = (SimpleFeatureStore) source;
	//			featureStore.setTransaction(transaction);
	//			try { 
	//				featureStore.addFeatures(featureSourceCube.getFeatures());
	//				transaction.commit();
	//			} catch (Exception problem) {
	//				problem.printStackTrace();
	//				transaction.rollback();
	//			} finally {
	//				transaction.close();
	//			}
	//			//System.exit(0); // success!
	//		} else {
	//			System.out.println("Table does not support read/write access");
	//			System.exit(1);
	//		}
	//
	//	}

}
