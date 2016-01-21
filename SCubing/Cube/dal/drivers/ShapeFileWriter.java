package dal.drivers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import bll.aggregation_functions.SAFUnion;
import bll.aggregation_functions.SAFUnionMBR;
import bll.aggregation_functions.SAFUnionPolygon;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.parallel.Consumer;
import bll.parallel.Resource;
import bll.util.Util;
public class ShapeFileWriter {
	HashMap<String, CubeColumn> cubeColumns;

	public ShapeFileWriter(HashMap<String, CubeColumn>  cubeColumns)
	{
		this.cubeColumns = cubeColumns;
	}

	public 	ArrayList<SimpleFeature>	  insertCubeToCollection(SimpleFeatureType TYPE,Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource ) throws Exception
	{
		ArrayList<SimpleFeature> list = new ArrayList<SimpleFeature>();
		final int numConsumidores =Integer.parseInt(Util.getConfig().getNumThreads());
		System.out.println("Número de consumidores: "+ numConsumidores);
		System.out.println("Começou os CONSUMIDORES");
		//resource.setFinished();
		//criamos os consumidores
		Consumer[] consumidores = new Consumer[numConsumidores];
		for(int i=0; i<consumidores.length; i++)
			consumidores[i] = new Consumer(TYPE,resource,cubeColumns);
		for(int i=0; i<consumidores.length; i++)
			consumidores[i].start();
		//finalizamos
		try{
			resource.setFinished();


			for(int i=0; i<consumidores.length; i++)
			{
				consumidores[i].join();

			}
			System.out.println("Terminou os CONSUMIDORES");
			for(int i=0; i<consumidores.length; i++)
			{

				list.addAll(consumidores[i].getDefaultFeatureCollection());
				
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return list;



	}




	public FeatureSource<SimpleFeatureType, SimpleFeature> insertCubeToSource(Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource , FeatureSource<SimpleFeatureType, SimpleFeature> source, long tempoInicial) throws  Exception
	{



		final SimpleFeatureType TYPE = createCubeSchema(source);

		long tempoIntermediario3 = System.currentTimeMillis();  
		System.out.println("(Parcial 3 - Tempo createCubeSchema) Tempo em milisegundos para calcular o cubo: "+ (tempoIntermediario3 - tempoInicial) );

		System.out.println("Número de registros:  "+ resource.getNumOfRegisters()) ;

		ArrayList<SimpleFeature> collection = insertCubeToCollection(TYPE, resource);


		long tempoIntermediario4 = System.currentTimeMillis(); 
		System.out.println("(Parcial 4 - Tempo insertCubeToCollection) Tempo em milisegundos para calcular o cubo: "+ (tempoIntermediario4 - tempoInicial) );


		SimpleFeatureSource sourceResult = DataUtilities.source(DataUtilities.collection(collection));


		long tempoIntermediario5 = System.currentTimeMillis(); 
		System.out.println("(Parcial 5 - Tempo source) Tempo em milisegundos para calcular o cubo: "+ (tempoIntermediario5 - tempoInicial) );


		return sourceResult;
	}




	private SimpleFeatureType createCubeSchema(FeatureSource<SimpleFeatureType, SimpleFeature> source) throws SchemaException {

		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();

		//TODO:set the name
		b.setName( "CustomLayer" );
		b.setCRS( source.getSchema().getCoordinateReferenceSystem() ); // set crs first

		//Adicionando as colunas espaciais. Medidas ou não.
		for (CubeColumn cubeColumn : cubeColumns.values()) {
			if (cubeColumn.getColumnName()=="the_geom")
			{
				if (cubeColumn.getAggFunction()!=null && cubeColumn.getAggFunction() instanceof SAFUnionMBR){
					b.add(source.getSchema().getGeometryDescriptor().getName().getLocalPart(), Polygon.class); // then add geometry
				}
				else if (cubeColumn.getAggFunction()!=null && cubeColumn.getAggFunction() instanceof SAFUnionPolygon)
				{
					b.add(source.getSchema().getGeometryDescriptor().getName().getLocalPart(), Polygon.class); // then add geometry
				}
				else if (cubeColumn.getAggFunction()!=null && cubeColumn.getAggFunction() instanceof SAFUnion && source.getSchema().getGeometryDescriptor().getType().getBinding() == Point.class)
				{
					b.add( source.getSchema().getGeometryDescriptor().getName().getLocalPart(), MultiPoint.class);
				}
				else
				{
					b.add( source.getSchema().getGeometryDescriptor().getName().getLocalPart(), source.getSchema().getGeometryDescriptor().getType().getBinding()); // then add geometry	
				}
			}
		}


		for (CubeColumn cubeColumn : cubeColumns.values()) {
			//A coluna espacial jã foi adicionada no for anterior 
			if (cubeColumn.getColumnName()!="the_geom")
			{
				//add some properties
				b.add( source.getSchema().getDescriptor(cubeColumn.getColumnName()).getName().getLocalPart(), String.class );

				//TODO:
				//attributes+= source.getSchema().getDescriptor(columnName).getType().getBinding().getSimpleName();
			}

		}
		//build the type
		//attributes = attributes.replaceFirst(",", "");
		return b.buildFeatureType();
	}
}
