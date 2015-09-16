package bll.aggregation_functions;

import java.util.ArrayList;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import dal.drivers.ShapeFileUtilities;


public class SAFDistance extends ISpatialAggFunction implements IAggFunction{

	double distance;

	public SAFDistance(double distance)
	{
		this.distance = distance;	
	}

	
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return oldMeasure.toString() +"&&"+  oldMeasureTwo.toString();
	}

	@Override
	public Geometry applyAggFunction(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature>  featureSource) 
	{
		try
		{

			GeometryCollection geometrieCol = ShapeFileUtilities.selectRegions(fid, featureSource);
			Geometry unionedGeometry = geometrieCol.union();
			ArrayList<Geometry>   result = new ArrayList<Geometry>();
			
			Feature feature;
			for(FeatureIterator<SimpleFeature> iterator =  featureSource.getFeatures().features(); iterator.hasNext(); ){
				feature = iterator.next();
				if(unionedGeometry.isWithinDistance((Geometry) feature.getDefaultGeometryProperty().getValue(),distance))
				{
					result.add((Geometry) feature.getDefaultGeometryProperty().getValue());
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
		return "Distância";
	}

}
