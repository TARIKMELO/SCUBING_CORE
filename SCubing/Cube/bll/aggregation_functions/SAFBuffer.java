package bll.aggregation_functions;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import presentation.layout.MapFrame;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

import dal.drivers.ShapeFileUtilities;


public class SAFBuffer extends ISpatialAggFunction implements IAggFunction{

	double distance;

	public SAFBuffer(double distance)
	{
		this.distance = distance;	
	}

	
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return oldMeasure.toString() +"&&"+  oldMeasureTwo.toString();
	}

	
	public Geometry applyAggFunction(String[] fid, FeatureSource<SimpleFeatureType, SimpleFeature>  featureSource) 
	{
		try
		{
			GeometryCollection geometrieCol = ShapeFileUtilities.selectRegions(fid, featureSource);
			//distance - mesma unidade do mapa
			//distance = bufferInKm(distance);
			//System.out.println(distance);
			return geometrieCol.buffer(distance);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//System.exit(-1);
			return null;
		}
	}


//	public double bufferInKm(Double radiusKm) 
//	{
//		double converted = radiusKm;
//		Unit<?> u = MapFrame.getInstance().getMap().getCoordinateReferenceSystem().getCoordinateSystem().getAxis(0).getUnit();
//		Unit<?> km = Unit.valueOf("km");
//		if (u.isCompatible(km)) {
//			UnitConverter converter =  km.getConverterTo(u);
//			converted = converter.convert(radiusKm);
//		}
//		return converted;
//		//throw new IllegalStateException("Unable to convert between " + u + " and " + km);    
//	}

	@Override
	public String toString() {
		return "Buffer";
	}

}
