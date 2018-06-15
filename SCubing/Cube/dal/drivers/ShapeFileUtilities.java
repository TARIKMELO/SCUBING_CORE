package dal.drivers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;	
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import bll.data_structures.nodes.DimensionTypeValue;
import sun.misc.Cleaner;


public class ShapeFileUtilities {

	public static GeometryCollection selectRegions(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) throws UnknownHostException, IOException
	{

		FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		Set<FeatureId> set = new  HashSet<FeatureId>();
		for (int i = 0; i< fid.length; i++)
		{
			set.add(ff.featureId(fid[i]));
		}
		Filter filter = ff.id(set);
		FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures = featureSource.getFeatures(filter);
		SimpleFeature[] selectedFeaturesArray =  (SimpleFeature[]) selectedFeatures.toArray();
		Collection<Geometry> geometryCollection = new ArrayList<Geometry>();
		for (SimpleFeature geometry : selectedFeaturesArray) {
			geometryCollection.add((Geometry)geometry.getDefaultGeometry());
		} 



		GeometryFactory factory = new GeometryFactory();


		return (GeometryCollection) factory.buildGeometry( geometryCollection );
	}


	public static Collection<Geometry> selectRegions2(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) throws UnknownHostException, IOException
	{
		FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		Set<FeatureId> set = new  HashSet<FeatureId>();
		for (int i = 0; i< fid.length; i++)
		{
			set.add(ff.featureId(fid[i]));
		}
		Filter filter = ff.id(set);
		FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures = featureSource.getFeatures(filter);
		FeatureIterator<SimpleFeature> iterator =  selectedFeatures.features();
		Collection<Geometry> geometries = new ArrayList<Geometry>();
		int aux = 0;

		try{
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				geometries.add((Geometry) feature.getDefaultGeometry()); 
				aux++;
			}
		}
		finally{
			iterator.close();
		}


		return geometries;
	}


//	public static <T> IResultSetText<T> getData(FeatureSource<SimpleFeatureType, SimpleFeature> featureSource,HashMap<String, CubeColumn> cubeColumns2 ) throws Exception {
//		IResultSetText<T> rs = new ResultSetText<T>();
//		rs.configure(cubeColumns2);
//
//		DimensionTypeValue[] str ;	
//		try {
//			String where="";
//			for (CubeColumn cubeColumn : cubeColumns2.values()) {
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
//
//
//
//
//
//			//System.out.println("Sizeeeeeeeeeeeeee "+memory.size());
//			//diskMemory = null;
//
//
//
//			//-------------------------------------------------//
//
//
//
//
//			//DESSA MANEIRA - TODO O DADO NÃO É CARREGADO NA MEMÓRIA, ELE LÊ SOB DEMANDA DO POSTGRE, PODE DAR ERRO DE GARBAGE COLLECTOR
//			//FeatureCollection diskMemory = featureSource.getFeatures();
//			//FeatureIterator<SimpleFeature> iterator =  memory.features();
//
//			//FeatureCollection<SimpleFeatureType, SimpleFeature> diskMemory  = featureSource.getFeatures();
//			//FeatureIterator<SimpleFeature> iterator =  diskMemory.features();
//			//System.out.println("Sizeeeeeeeeeeeeee "+diskMemory.size());
//
//			//DESSA MANEIRA TODO O DADO É CARREGADO NA MEMÓRIA
//			//SimpleFeatureCollection memory = DataUtilities.collection( collection );
//			//FeatureIterator<SimpleFeature> iterator =  memory.features();
//			//SimpleFeature feature;
//
//			//			FeatureReader<SimpleFeatureType, SimpleFeature> reader =  DataUtilities.reader(diskMemory.features());
//			//
//			//
//			//			try{
//			//				while( reader.hasNext() ){
//			//					SimpleFeature feature = reader.next();
//			//					str = formatShapefileLine(feature,cubeColumns2);
//			//					for(int i=0; i<str.length; i++)
//			//					{
//			//						rs.updateData(i, (T)(str[i]));
//			//					}
//			//					//REFRESH
//			//					rs.insertRow();
//			//
//			//				}
//			//			}
//			//			finally{
//			//				reader.close();
//			//
//			//			}
//			//
//			//SimpleFeature feature = null;
//			
//			
//			
//
//			try (FeatureIterator<SimpleFeature> iterator = diskMemory.features()){
//				while( iterator.hasNext() ){
//					//System.out.println("iterator");
//					SimpleFeature feature = iterator.next();
//					str = formatShapefileLine(feature,cubeColumns2);
//					for(int i=0; i<str.length; i++)
//					{
//						rs.updateData(i, (T)(str[i]));
//					}
//					
//					//REFRESH
//	//				rs.insertRow();
//					feature = null;
//					
//					
//				
//					
//				}
//			}
//
//			//			try{
//			//				while( iterator.hasNext() ){
//			//					SimpleFeature feature = iterator.next();
//			//					str = formatShapefileLine(feature,cubeColumns2);
//			//					for(int i=0; i<str.length; i++)
//			//					{
//			//						rs.updateData(i, (T)(str[i]));
//			//					}
//			//					//REFRESH
//			//					rs.insertRow();
//			//
//			//				}
//			//			}
//			//			finally{
//			//				iterator.close();
//			//				
//			//			}
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//			System.out.println("Erro no método getData()");
//			System.exit(-1);
//		}
//
//
//		return rs;
//	}


	//Formata a linha - Garanto que o arquivo fica na ordem que eu quero
	public static DimensionTypeValue[] formatShapefileLine(SimpleFeature feature,HashMap<String, CubeColumn> cubeColumns2 ) throws IOException {
		DimensionTypeValue[]  row = new DimensionTypeValue[cubeColumns2.size()];
		for (CubeColumn cubeColumn : cubeColumns2.values()) {
			if (cubeColumn.getColumnName().equals("geom"))
			{ 

				//aquiiiii
				//row[cubeColumn.getIndex()] = new DimensionTypeValue(feature.getIdentifier().getID().toString(),cubeColumn.columnName);
				//row[cubeColumn.getIndex()] = new DimensionTypeValue(feature.getIdentifier().getID().toString(),cubeColumn.columnName, (Geometry)feature.getDefaultGeometry());
				row[cubeColumn.getIndex()] = new DimensionTypeValue(feature.getDefaultGeometry(),cubeColumn.columnName);
			}
			else
			{

				if (feature.getProperty(cubeColumn.getColumnName())!=null)
				{
					Object sourceValue = feature.getProperty(cubeColumn.getColumnName()).getValue();
					String sourceValueString = sourceValue==null?"":feature.getProperty(cubeColumn.getColumnName()).getValue().toString();
					row[cubeColumn.getIndex()] =new DimensionTypeValue(sourceValueString,cubeColumn.columnName);
				}
			}
		}
		return row;
	}


}
