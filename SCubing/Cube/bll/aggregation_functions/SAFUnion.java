package bll.aggregation_functions;



import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;

import dal.drivers.ShapeFileUtilities;

public class SAFUnion  extends ISpatialAggFunction implements IAggFunction{


	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return ((Geometry)oldMeasure).union(((Geometry)oldMeasureTwo));
	}

	

	@Override
	public String toString() {
		return "União";

	}
}



/*

package aggregation_functions;



import com.esri.arcgis.geodatabase.FeatureClass;
import com.esri.arcgis.geometry.IEnumGeometry;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.ITopologicalOperator;
import com.esri.arcgis.geometry.Polygon;

public class SAFUnion  extends ISpatialAggFunction implements IAggFunction{

	@Override
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return oldMeasure.toString() +"-"+  oldMeasureTwo.toString();
	}

	public IPolygon applyAggFunction(String fid, FeatureClass featureClass) 
	{
		try
		{
			IEnumGeometry geometryBag = SpatialFunctions.selectRegions(fid, featureClass);

			ITopologicalOperator unionedPolygon = new Polygon();
			unionedPolygon.constructUnion(geometryBag);

			return (IPolygon)unionedPolygon;
		}
		catch(Exception ex)
		{
			System.err.print("Erro ao aplicar a funãão de agregaãão");
			return null;
		}
	}

	@Override
	public String toString() {
		return "União";

	}
}
 */