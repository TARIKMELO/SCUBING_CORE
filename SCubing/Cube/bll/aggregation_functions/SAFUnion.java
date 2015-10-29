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
		return oldMeasure.toString() +"&&"+  oldMeasureTwo.toString();
	}

	public Geometry applyAggFunction(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature> featureSource) 
	{
		try
		{


			//Necessário para gerar a uniao de grandes regioes
//			if (fid.length>3000)
//			{
//
//				String[] fid1 = new String[fid.length/2];
//				String[] fid2 = new String[fid.length/2];
//
//				System.arraycopy(fid, 0, fid1, 0, fid1.length);
//
//				GeometryCollection geometrieCol1 = ShapeFileUtilities.selectRegions(fid1, featureSource);
//
//				Geometry geoUnionOne = UnaryUnionOp.union(geometrieCol1);
//				geometrieCol1 = null;
//
//				System.arraycopy(fid, fid1.length, fid2, 0, fid2.length);
//				GeometryCollection geometrieCol2 = ShapeFileUtilities.selectRegions(fid2, featureSource);
//				Geometry geoUnionTwo =   UnaryUnionOp.union(geometrieCol2);
//				geometrieCol2 = null;
//				return geoUnionOne.union(geoUnionTwo);
//
//
//			}
//			else
//			{
				GeometryCollection geometrieCol = ShapeFileUtilities.selectRegions(fid, featureSource);
				return geometrieCol.union();
				//return	UnaryUnionOp.union(geometrieCol);

//			}


		}
		catch(Exception ex)
		{
			System.out.println("Número total de regiãos à ser resgatada: "+ fid.length+ "  FID: "+fid); 
			System.out.println();
			ex.printStackTrace();
			return null;
		}
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