package dal.drivers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

import bll.aggregation_functions.ISpatialAggFunction;
import bll.data_structures.nodes.DimensionTypeValue;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;


public class ShapeFileUtilities {

//	public static SimpleFeatureBuilder generateVisualization(String ids, SimpleFeatureBuilder  newFeature, ISpatialAggFunction spatialAggFunc) throws IOException
//	{	
//		//TODO: Mudar isso urgente
//		String[] idArray = ids.split("&&");
//		Geometry resultantPolygon = spatialAggFunc.applyAggFunction(idArray);
//		newFeature.set("geom", resultantPolygon);
//		return newFeature;
//	}

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
		//		FeatureIterator<SimpleFeature> iterator =  selectedFeatures.features();
		//		Collection<Geometry> geometryCollection = new ArrayList<Geometry>();
		//		try
		//		{
		//			while (iterator.hasNext()) {
		//				SimpleFeature feature = iterator.next();
		//				geometryCollection.add((Geometry)feature.getDefaultGeometry());
		//			}
		//		}
		//		finally
		//		{
		//			iterator.close();
		//		}

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


	public static <T> IResultSetText<T> getData(FeatureSource<SimpleFeatureType, SimpleFeature> featureSource,HashMap<String, CubeColumn> cubeColumns2 ) throws Exception {
		IResultSetText<T> rs = new ResultSetText<T>();
		rs.configure(cubeColumns2);
	
		DimensionTypeValue[] str ;	
		try {
			String where="";
			for (CubeColumn cubeColumn : cubeColumns2.values()) {
				if(cubeColumn.getWhere()!=null)
				{
					where+=" AND "+cubeColumn.getColumnName()+" "+cubeColumn.getWhere() ;
				}
			} 
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
			if (where == "")
			{

				collection = featureSource.getFeatures();
			}
			else
			{
				where = where.replaceFirst("AND", "");
				Filter filter = CQL.toFilter(where);
				collection = featureSource.getFeatures(filter);
			}

			//FeatureCollection collection = featureSource.getFeatures();
			FeatureIterator<SimpleFeature> iterator =  collection.features();
			SimpleFeature feature;

			try{
				while( iterator.hasNext() ){
					feature = iterator.next();
					str = formatShapefileLine(feature,cubeColumns2);
					for(int i=0; i<str.length; i++)
					{
						rs.updateData(i, (T)(str[i]));
					}
					//REFRESH
					rs.insertRow();

				}
			}
			finally{
				iterator.close();
				
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Erro no método getData()");
			System.exit(-1);
		}
		return rs;
	}


	//Formata a linha - Garanto que o arquivo fica na ordem que eu quero
	private static DimensionTypeValue[] formatShapefileLine(SimpleFeature feature,HashMap<String, CubeColumn> cubeColumns2 ) throws IOException {
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
