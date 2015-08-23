package bll.aggregation_functions;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

import dal.drivers.ShapeFileUtilities;

public class SAFUnionPolygon  extends ISpatialAggFunction implements IAggFunction{

	
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return oldMeasure.toString() +"&&"+  oldMeasureTwo.toString();
	}

	public Geometry applyAggFunction(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) 
	{
		try
		{
			GeometryCollection geometrieCol = ShapeFileUtilities.selectRegions(fid, featureSource);
			return geometrieCol.union();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "Uni�o Pol�gono";
	}
}