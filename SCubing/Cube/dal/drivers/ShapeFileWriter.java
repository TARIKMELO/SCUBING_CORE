package dal.drivers;

	import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import bll.aggregation_functions.SAFUnion;
import bll.aggregation_functions.SAFUnionMBR;
import bll.aggregation_functions.SAFUnionPolygon;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.parallel.Consumer;
import bll.parallel.ResourceII;
import bll.util.Util;

import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
public class ShapeFileWriter {
	HashMap<String, CubeColumn> cubeColumns;

	public ShapeFileWriter(HashMap<String, CubeColumn>  cubeColumns)
	{
		this.cubeColumns = cubeColumns;
	}

	public 	DefaultFeatureCollection	  insertCubeToCollection(SimpleFeatureType TYPE,ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource ,FeatureSource<SimpleFeatureType, SimpleFeature> source ) throws Exception
	{
		//collectin com o cubo resultante
		DefaultFeatureCollection collection =  new DefaultFeatureCollection();
		

		final int numConsumidores =Integer.parseInt(Util.getConfig().getNumThreads());
		//criamos os consumidores
		Consumer[] consumidores = new Consumer[numConsumidores];
		for(int i=0; i<consumidores.length; i++)
			//TODO: Voltar para parametrizado
			consumidores[i] = new Consumer(TYPE,resource,source,cubeColumns,collection);
		for(int i=0; i<consumidores.length; i++)
			consumidores[i].start();
		//finalizamos
		try{
			resource.setFinished();
			for(int i=0; i<consumidores.length; i++)
				consumidores[i].join();
		}catch (Exception e){
			e.printStackTrace();
		}
		return collection;
	}


	

	public FeatureSource<SimpleFeatureType, SimpleFeature> insertCubeToSource(ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource , FeatureSource<SimpleFeatureType, SimpleFeature> source) throws  Exception
	{
		final SimpleFeatureType TYPE = createCubeSchema(source);
		SimpleFeatureCollection collection = insertCubeToCollection(TYPE, resource, source);
		SimpleFeatureSource sourceResult = DataUtilities.source( collection );
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
