package bll.aggregation_functions;

import java.util.ArrayList;
import java.util.Collection;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;

import dal.drivers.ShapeFileUtilities;


public class SAFIntersection  extends ISpatialAggFunction implements IAggFunction{

	
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return oldMeasure.toString() +"&&"+  oldMeasureTwo.toString();
	}

	
	public Geometry applyAggFunction(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) 
	{
		try
		{

			Collection<Geometry> geometrieCol =  ShapeFileUtilities.selectRegions2(fid, featureSource);
			Geometry unionedGeometry =  CascadedPolygonUnion.union( geometrieCol);
			ArrayList<Geometry>  result = new ArrayList<Geometry>();
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();
			SimpleFeature feature;
			Geometry auxGeometry;

			for(FeatureIterator<SimpleFeature> iterator =  collection.features(); iterator.hasNext(); ){
				feature = iterator.next();
				auxGeometry= (Geometry)feature.getDefaultGeometry();
				if (!geometrieCol.contains(auxGeometry))
					if(unionedGeometry.intersects(auxGeometry))
					{

						result.add(unionedGeometry.intersection(auxGeometry));
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
			return null;
		}
	}

	@Override
	public String toString() {
		return "Interseção";
	}
}
