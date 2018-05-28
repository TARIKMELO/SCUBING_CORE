package dal.drivers;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;

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
	int hierarquia;
	public ShapeFileWriter(HashMap<String, CubeColumn>  cubeColumns)
	{
		this.cubeColumns = cubeColumns;
	}

	public 	ArrayList<SimpleFeature> insertCubeToCollection(SimpleFeatureType TYPE,Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource ) throws Exception
	{
		ArrayList<SimpleFeature> list = new ArrayList<SimpleFeature>();
		final int numConsumidores =Integer.parseInt(Util.getConfig().getNumThreads());
		System.out.println("Número de consumidores: "+ numConsumidores);
		//System.out.println("Começou os CONSUMIDORES");
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

			//System.out.println("Terminou os CONSUMIDORES");
			for(int i=0; i<consumidores.length; i++)
			{

				list.addAll(consumidores[i].getDefaultFeatureCollection());

			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return list;



	}




	public FeatureSource<SimpleFeatureType, SimpleFeature> insertCubeToSource(Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource , FeatureSource<SimpleFeatureType, SimpleFeature> source, int hierarquia) throws  Exception
	{
		this.hierarquia =  hierarquia;
		final SimpleFeatureType TYPE = createCubeSchema(source);
		//System.out.println("(Parcial 2.1) Entrando insertCubeToCollection ");
		ArrayList<SimpleFeature> collection = insertCubeToCollection(TYPE, resource);
		//System.out.println("(Parcial 2.2) Entrando DataUtilities.source(DataUtilities.collection(collection))");
		SimpleFeatureSource sourceResult = DataUtilities.source(DataUtilities.collection(collection));
		return sourceResult;
	}





	private SimpleFeatureType createCubeSchema(FeatureSource<SimpleFeatureType, SimpleFeature> source) throws SchemaException {
		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
		//Definindo o nome do layer criado
		String nameLayer = Util.getConfig().getNomeLayer();

		if(  (nameLayer != null && !nameLayer.isEmpty())){
			b.setName(nameLayer);
		}
		else
		{
			nameLayer = new SimpleDateFormat("ddMMyyyy_HH:mm:ss:SSS").format(new Date());
			b.setName( "GridCustomLayer"+nameLayer+"_"+hierarquia);

		}

		Util.getLogger().info("Nome da tabela: "+b.getName());



		if (source.getSchema().getCoordinateReferenceSystem()==null)
		{
			try {
				final String EPSG4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
				b.setCRS( CRS.parseWKT(EPSG4326));
			} catch (FactoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{

			b.setCRS( source.getSchema().getCoordinateReferenceSystem() ); // set crs first

		}
		//Adicionando as colunas espaciais. Medidas ou não.
		for (CubeColumn cubeColumn : cubeColumns.values()) {
			if (cubeColumn.getColumnName().equals("geom"))
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
			if (!cubeColumn.getColumnName().equals("geom"))
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
