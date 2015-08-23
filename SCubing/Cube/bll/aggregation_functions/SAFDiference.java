package bll.aggregation_functions;


import java.util.ArrayList;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import dal.drivers.ShapeFileUtilities;

public class SAFDiference  extends ISpatialAggFunction implements IAggFunction{

	
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return oldMeasure.toString() +"&&"+  oldMeasureTwo.toString();
	}

	public Geometry applyAggFunction(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) 
	{
		try
		{
			GeometryCollection geometrieCol = ShapeFileUtilities.selectRegions(fid, featureSource);
			Geometry unionedGeometry = geometrieCol.union();
			ArrayList<Geometry>  result = new ArrayList<Geometry>();
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();
			SimpleFeature feature;
			Geometry auxGeometry;
			for(FeatureIterator<SimpleFeature> iterator =  collection.features(); iterator.hasNext(); ){
				feature = iterator.next();
				auxGeometry= (Geometry)feature.getDefaultGeometry();

				if(unionedGeometry.intersects(auxGeometry))
				{
					result.add(unionedGeometry.difference(auxGeometry).union());
				}
			}
			Geometry[] geometries = new Geometry[result.size()];
			GeometryFactory factory = new GeometryFactory();
			GeometryCollection geometrieColResult = new GeometryCollection(result.toArray(geometries), factory);
			return geometrieColResult.union();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//System.exit(-1);
			return null;

		}
	}

	@Override
	public String toString() {
		return "Diferen�a";

	}
}








