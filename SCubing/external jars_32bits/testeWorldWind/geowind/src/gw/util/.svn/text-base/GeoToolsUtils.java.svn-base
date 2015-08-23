/*
 * $Id: GeoToolsUtils.java 66 2008-12-08 17:51:15Z iovergard $
 * 
 * This file is a part of the GeoWind package, a library for visualizing
 * data from GeoTools in Nasa WorldWind.
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *     1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *     2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *     3. This notice may not be removed or altered from any source
 *     distribution.
 */

package gw.util;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.*;
import gov.nasa.worldwind.globes.Globe;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author Ian Overgard
 */
public class GeoToolsUtils {


    public static double[] getMinMaxHeight(SimpleFeature[] features, Globe globe, CoordinateReferenceSystem crs) {
        double[] minmax = new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for(SimpleFeature feature : features) {
            Geometry geo = (Geometry)feature.getDefaultGeometry();
            Coordinate[] coords = geo.getCoordinates();
            for(Coordinate c : coords) {
                LatLon ll = ProjectionUtils.toLatLon(new double[] {c.x, c.y}, crs);
                double elevation = globe.getElevation(ll.getLatitude(), ll.getLongitude());
                if(elevation < minmax[0])
                    minmax[0] = elevation;
                if(elevation > minmax[1])
                    minmax[1] = elevation;
            }
        }
        return minmax;
    }

    public static Position getFeatureCenter(SimpleFeature feature, CoordinateReferenceSystem crs) {
        Position rval = null;
        Geometry geometry = (Geometry)feature.getDefaultGeometry();
        Coordinate[] coords = geometry.getCoordinates();
        Position[] vertices = new Position[coords.length];
        LatLon center = LatLon.ZERO;
        double cx=0, cy=0;
        
        for (int i = 0; i < coords.length; i++) {
            cx += coords[i].x;
            cy += coords[i].y;
        }
        cx /= (double)coords.length;
        cy /= (double)coords.length;
        try {
            rval = new Position( ProjectionUtils.toLatLon(new double[] { cx, cy }, crs), 10);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return rval;
    }
    
    public static ReferencedEnvelope[] subdivideEnvelope(ReferencedEnvelope e) {
        double cx = e.getCenter(0);
        double cy = e.getCenter(1);
        ReferencedEnvelope topLeft = new ReferencedEnvelope(
                                            e.getMinX(), cx, cy, e.getMaxY(),
                                            e.getCoordinateReferenceSystem());
        ReferencedEnvelope topRight = new ReferencedEnvelope(
                                            cx, e.getMaxX(), cy, e.getMaxY(),
                                            e.getCoordinateReferenceSystem());
        ReferencedEnvelope bottomLeft = new ReferencedEnvelope(
                                            e.getMinX(), cx, e.getMinY(), cy,
                                            e.getCoordinateReferenceSystem());
        ReferencedEnvelope bottomRight = new ReferencedEnvelope(
                                            cx, e.getMaxX(), e.getMinY(), cy,
                                            e.getCoordinateReferenceSystem());

        return new ReferencedEnvelope[] { topLeft, topRight, bottomLeft, bottomRight };
    }
    
    public static SimpleFeature getFeatureAt(LatLon latlon, FeatureSource featureSource, CoordinateReferenceSystem originalCRS) {
        try {
            if (latlon == null) {
                return null;
            }
            double[] pcoord = null;
            try {
                pcoord = ProjectionUtils.latLonToProjection(latlon, originalCRS);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
            GeometryFactory gf = new GeometryFactory();
            //Geometry point = gf.createPoint(new Coordinate(pcoord[0], pcoord[1]));

            double epsilon = 0.001;
            Coordinate[] coords = new Coordinate[5];
            double[] ll = ProjectionUtils.latLonToProjection(latlon.add(new LatLon(Angle.fromDegrees(-epsilon), Angle.fromDegrees(-epsilon))), originalCRS);
            double[] lr = ProjectionUtils.latLonToProjection(latlon.add(new LatLon(Angle.fromDegrees( epsilon), Angle.fromDegrees(-epsilon))), originalCRS);
            double[] ur = ProjectionUtils.latLonToProjection(latlon.add(new LatLon(Angle.fromDegrees( epsilon), Angle.fromDegrees( epsilon))), originalCRS);
            double[] ul = ProjectionUtils.latLonToProjection(latlon.add(new LatLon(Angle.fromDegrees(-epsilon), Angle.fromDegrees( epsilon))), originalCRS);
            coords[0] = new Coordinate(ll[0], ll[1]);
            coords[1] = new Coordinate(lr[0], lr[1]);
            coords[2] = new Coordinate(ur[0], ur[1]);
            coords[3] = new Coordinate(ul[0], ul[1]);
            coords[4] = new Coordinate(ll[0], ll[1]);
            LinearRing mbox = gf.createLinearRing(coords);
            Polygon mouseBox = gf.createPolygon(mbox, null);

            String fieldname = featureSource.getSchema().getGeometryDescriptor().getName().getLocalPart();
            if (fieldname.equals("")) {
                fieldname = "the_geom";
            }
            BinarySpatialOperator filter = ff.intersects(ff.property(fieldname), ff.literal(mouseBox));
            //BinarySpatialOperator filter = ff.contains(ff.property(fieldname), ff.literal(point));
            FeatureCollection fc = featureSource.getFeatures(filter);
            FeatureIterator i = fc.features();
            while (i.hasNext()) {
                SimpleFeature feature = (SimpleFeature) i.next();
                return feature;
            }
        }
        catch (Exception e) {
        }
        return null;
    }
}
