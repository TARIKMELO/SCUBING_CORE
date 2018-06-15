package bll.data_structures;

import java.io.IOException;
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import bll.aggregation_functions.IMAggFunction;
import bll.aggregation_functions.ISpatialAggFunction;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.parallel.Resource;
import bll.util.Util;
import dal.drivers.CubeColumn;
import dal.drivers.IResultSetText;
import dal.drivers.ShapeFileUtilities;
import dal.drivers.ShapeFileWriter;
import dal.drivers.ShapeFileWriterGrid;
import presentation.layout.MapFrame;


public class CubeGrid {

	String nomeColX;
	String nomeColY;
	String nomeColId;

	public CubeGrid(String nomeColX, String nomeColY,String nomeColId)
	{
		this.nomeColX = nomeColX;
		this.nomeColY = nomeColY;
		this.nomeColId = nomeColId;
	}
	public void performHierarchies(int x, int y, IResultSetText<DimensionTypeValue> rs, FeatureSource source, HashMap<String, CubeColumn> cubeColumns, int hierarquia) throws Exception
	{

		long tempoInicial = System.currentTimeMillis(); 


		while (source!=null && source.getFeatures().size()>4)
		{
			Util.getLogger().info("Tamanho: "+source.getFeatures().size());
			HashMap<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> result  = new HashMap<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>();
			//Não uso o tipo abaixo pois nao tem a funão contains
			//ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> result =new  ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>>();
			Util.getLogger().info("Regra de vizinhaça: "+x +" x "+y );
			ShapeFileWriterGrid shapeFileWriterGrid = new ShapeFileWriterGrid(cubeColumns);
			Object[] tuple;
			try
			{
				
				while((tuple=rs.next())!=null){


					ArrayList<DimensionTypeValue> dimensions =  new ArrayList<DimensionTypeValue>();
					ArrayList<MeasureTypeValue> measures =  new ArrayList<MeasureTypeValue>();
					ArrayList<MeasureTypeValue> measuresAux =  new ArrayList<MeasureTypeValue>();	
					double xData = Double.parseDouble(tuple[cubeColumns.get(nomeColX).getIndex() ].toString());

					double yData = Double.parseDouble(tuple[cubeColumns.get(nomeColY).getIndex()].toString());

					

					//Aqui está implementado a regra de vizinhaça. 
					//Regra de vizinhaça de Moore
					int newXData = (int) Math.ceil(xData/x);
					int newYData = (int) Math.ceil(yData/y);
					newXData =newXData+1;
					newYData = newYData+1;
					String key = "C"+newXData+"L"+newYData;


					//Regra de vizinhaça de Von Neumann - distância de Mahatan





					dimensions.add(new DimensionTypeValue(key,nomeColId));
					dimensions.add(new DimensionTypeValue(newXData+"",nomeColX));
					dimensions.add(new DimensionTypeValue(newYData+"",nomeColY));
					//System.out.println("Key: "+key+" Col: "+newXData+" Lin: "+newYData);
					if (result.containsKey(dimensions))
					{

						measuresAux = result.get(dimensions);
						measures = new ArrayList<MeasureTypeValue>();
						for (Object objectTuple : tuple) {
							DimensionTypeValue tupleItem = (DimensionTypeValue)objectTuple;
							CubeColumn cubeColumn = cubeColumns.get(tupleItem.getType());
							if (cubeColumn.isMeasure())
							{
								int index = cubeColumn.getIndex();
								//Só para distancia

								if (cubeColumn.getAggFunction() instanceof ISpatialAggFunction){

									ArrayList<Geometry> geometrias = (ArrayList<Geometry>) measuresAux.get(index).getValue();
									//String value = cubeColumn.getAggFunction().updateMeasure(measuresAux.get(index),tupleItem.getValue()).toString();
									//Geometry value = (Geometry) cubeColumn.getAggFunction().updateMeasure(measuresAux.get(index).getValue(),tupleItem.getValue());

									geometrias.add((Geometry) tupleItem.getValue());
									//System.out.println(value);
									measures.add(new MeasureTypeValue(geometrias,cubeColumn.getColumnName()));
								}
							}
						}
						result.put(dimensions, measures);
						//resource.putRegister(new AbstractMap.SimpleEntry (dimensions, measures));
					}
					else
					{

						measures = new ArrayList<MeasureTypeValue>();

						for (Object objectTuple : tuple) {
							DimensionTypeValue tupleItem = (DimensionTypeValue)objectTuple;
							CubeColumn cubeColumn = cubeColumns.get(tupleItem.getType());
							if (cubeColumn.isMeasure())
							{

								if (cubeColumn.getAggFunction() instanceof ISpatialAggFunction){

									ArrayList<Geometry> geometrias = new ArrayList<Geometry>();
									geometrias.add((Geometry) tupleItem.getValue());
									measures.add(new MeasureTypeValue(geometrias, tupleItem.getType()));	
								}

								else
								{

									//aquiiiiiiiiiiii
									measures.add(new MeasureTypeValue(tupleItem.getValue(), tupleItem.getType()));	
								}
							}

						}			
						result.put(dimensions, measures);
						//resource.putRegister(new AbstractMap.SimpleEntry (dimensions, measures));
					}
				}

			}
			finally
			{
				rs.close();
			}


			//			Aqui faz a uniao
			//			FeatureCollection collection = source.getFeatures();
			//			FeatureIterator<Feature> iterator =  collection.features();
			//			Feature feature;
			//			while( iterator.hasNext() ){
			//				feature = iterator.next();
			//				//System.out.println(feature.getIdentifier().getID().toString());
			//				System.out.println(feature.getDefaultGeometryProperty().getValue());
			//				System.out.println(feature.getValue());
			//			}




			//if (source.getFeatures().size()>8)
			//{


			//Calcular a distância até brasilia	


			//				long tempoInicialBrasilia = System.currentTimeMillis();
			//				Point brasiliaPoint =  gf.createPoint(new Coordinate(-47.7972188300436,-15.7811008579831));
			//				double distancia = currentGeometry.distance(brasiliaPoint);
			//				featureBuilder.set("distbrasilia", distancia);	
			//				long tempoFinalBrasilia = System.currentTimeMillis();  
			//				Util.getLogger().info("Tempo em milisegundos para INSERIR NO POSTIGIS: "+featureSourceCube.getSchema() +" "+(tempoFinalBrasilia - tempoInicialBrasilia) ); 
			//				
			//				

			//TODO: Nao precisa dessa linha
			//Passar o nome como parametro
			hierarquia = hierarquia+1;

			//FeatureSource sourceDesti = shapeFileWriter.insertCubeToShapefile(hashToResourceII(result), source);
			FeatureSource sourceDesti = shapeFileWriterGrid.insertCubeToSource(hashToResourceII(result), source, hierarquia);
			//shapeFileWriter.insertCubeToShapefile(result, source,"D:\\data\\Amazonia\\Amazonia"+sourceDesti.getFeatures().size()+".shp");
			//IResultSetText<DimensionTypeValue> rsDesti = ShapeFileUtilities.getData(sourceDesti, cubeColumns);


			IResultSetText<DimensionTypeValue> rsDesti = null;

			//Não quero visualizar
			if (sourceDesti!=null && sourceDesti.getSchema()!=null){
				//Cálculo do tempo de computação do cubo
				long tempoFinal = System.currentTimeMillis();  
				Util.getLogger().info("Tempo total em milisegundos para calcular a hierarquia: "+hierarquia +" "+(tempoFinal - tempoInicial) );

				insertToPostGis(sourceDesti);

			}
			//MapFrame.getInstance().createLayer (sourceDesti);
			//inserir no postgi

			//Para salvar em arquivos
			source = null;
			//System.gc();
			//rs = rsDesti;
			//Não quero visualizar


			//source = sourceDesti;



			performHierarchies(x, y, rsDesti, sourceDesti, cubeColumns, hierarquia);

			//}

		}
		/*else
		{*/
		//map.showLayers();
		/*}*/
	}

	public Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> hashToResourceII (HashMap<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> entrada)
	{
		Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> result =new  Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>>();
		for (Entry<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> entry : entrada.entrySet()) {
			result.putRegister(entry);
		}
		return result;
	}



	public void insertToPostGis(FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceCube ) throws IOException{
		long tempoInicial = System.currentTimeMillis();


		Map<String, Object> connectionParameters = new HashMap<String, Object>();

		connectionParameters.put("dbtype", "postgis");
		connectionParameters.put("host", "localhost");
		connectionParameters.put("port", 5432);
		connectionParameters.put("schema", "public");
		connectionParameters.put("user", "postgres");
		connectionParameters.put("passwd", "postgres");
		connectionParameters.put("database", "scubing");
		//Map<String, Object> connectionParameters = wizard.getConnectionParameters();
		DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);


		//Aqui que define o nome também
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
			dataStore.dispose();

			long tempoFinal = System.currentTimeMillis();  
			Util.getLogger().info("Tempo em milisegundos para INSERIR NO POSTIGIS: "+featureSourceCube.getSchema().getName() +" "+(tempoFinal - tempoInicial) );
			Util.getLogger().info("Tempo em SEGUNDOS para INSERIR NO POSTGIS: "+featureSourceCube.getSchema().getName() +" "+ (tempoFinal - tempoInicial) / 1000d);

		} else {
			System.out.println("Table does not support read/write access");
			System.exit(1);
		}

	}
}
