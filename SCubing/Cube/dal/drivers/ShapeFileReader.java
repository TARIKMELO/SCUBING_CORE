package dal.drivers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import gw.layers.SimpleFeatureLayer;
import presentation.layout.MapFrame;

public class ShapeFileReader<T>  {

	FeatureSource<SimpleFeatureType, SimpleFeature> source;
	HashMap<String, CubeColumn> cubeColumns;
	String outputFileName;

	public ShapeFileReader(FeatureSource<SimpleFeatureType, SimpleFeature> source, HashMap<String, CubeColumn> cubeColumns)
	{
		
			this.cubeColumns = cubeColumns;
			this.source = source;
		
	}

	public ArrayList< Hashtable<String, String>> getColumnsInfo() 
	{
		
	
		ArrayList< Hashtable<String, String>> columnsInfo =  new ArrayList<Hashtable<String,String>>();
		Hashtable<String, String > columnInfo ;
		for (org.opengis.feature.type.PropertyDescriptor field : source.getSchema().getDescriptors()) {
			columnInfo = new Hashtable<String, String>();
			if (field == null) {
				System.out.println("Field is null ");
				continue;
			}
			columnInfo.put("NOME", field.getName().toString());
			//TODO:
			columnInfo.put("TIPO", field.getType().getBinding().getSimpleName());//getFieldTypeDescription(field.getType()));
			columnsInfo.add(columnInfo);
		}
		return columnsInfo;
	}

	public IResultSetText<T> getData() throws Exception {
		return ShapeFileUtilities.getData(source, cubeColumns);
	}

	public 	FeatureSource<SimpleFeatureType, SimpleFeature> getSource()
	{
		
		return source;
	}
}
