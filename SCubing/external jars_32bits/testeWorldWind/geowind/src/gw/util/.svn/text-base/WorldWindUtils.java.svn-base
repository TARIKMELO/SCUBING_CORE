/*
 * $Id: WorldWindUtils.java 67 2008-12-08 20:23:28Z iovergard $
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

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.view.BasicOrbitView;
import gov.nasa.worldwind.view.FlyToOrbitViewStateIterator;
import gw.renderables.ComplexPolygon;
import gw.renderables.LinePath;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Ian Overgard
 */
public class WorldWindUtils {

    static final double DEG_IN_METER = 111131.745d;

    /**
     * Fly to a certain latlong position
     * 
     * @param upperleft
     * @param lowerright
     */
    public static void flyTo(LatLon upperleft, LatLon lowerright, WorldWindow canvas) {
        flyTo(new Sector(lowerright.getLatitude(), upperleft.getLatitude(), upperleft.getLongitude(), lowerright.getLongitude()), canvas);
    }


    public static void flyTo(Sector sect, WorldWindow canvas) {


        //This block of code is to figure out the proper altitude to set the camera at.
        //It doesn't quite work yet, but it comes close enough. It will probably work
        //a lot better when I stop using a constant for lengthOfDegreeInMeters, which is
        //only accurate near the equator.
        //height of the map (depending on which is bigger)
        double majorDimension = (sect.getDeltaLatDegrees() > sect.getDeltaLonDegrees()) ? sect.getDeltaLatDegrees() : sect.getDeltaLonDegrees();
        BasicOrbitView vw = (BasicOrbitView) canvas.getView();
        double fov = vw.getFieldOfView().getRadians();
        double alt = (majorDimension * DEG_IN_METER) / Math.sin(fov);// + 2000;

        LatLon ll = sect.getCentroid();
        Position p = Position.fromDegrees(ll.getLatitude().getDegrees(), ll.getLongitude().getDegrees(), 0);
        FlyToOrbitViewStateIterator pan = FlyToOrbitViewStateIterator.createPanToIterator(
                vw, canvas.getModel().getGlobe(),
                p, Angle.ZERO, Angle.ZERO, alt);
        vw.applyStateIterator(pan);
    }

    public static double getCameraAltitude(LatLon upperleft, LatLon lowerright, double fovInRadians) {
        //This block of code is to figure out the proper altitude to set the camera at.
        //It doesn't quite work yet, but it comes close enough. It will probably work
        //a lot better when I stop using a constant for lengthOfDegreeInMeters, which is
        //only accurate near the equator.
        double majorDimension = 0; //majorDimension is the width or 
        //height of the map (depending on which is bigger)

        LatLon dim = lowerright.subtract(upperleft);
        if (dim.getLatitude().getDegrees() > dim.getLongitude().getDegrees()) {
            majorDimension = dim.getLatitude().getDegrees();
        } else {
            majorDimension = dim.getLongitude().getDegrees();
        }

        double lengthOfDegreeInMeters = 111131.745;
        double extentInDegrees = majorDimension;
        double extentInMeters = extentInDegrees * lengthOfDegreeInMeters;
        return extentInMeters / Math.sin(fovInRadians);// + 2000;
    }

    public static RenderableLayer buildTextureLayer(BufferedImage image, Sector sector) {
        SurfaceImage simg = new SurfaceImage(image, sector);
        RenderableLayer layer = new RenderableLayer();
        layer.addRenderable(simg);
        return layer;
    }

    public static SurfacePolygon polygonFromFeature(SimpleFeature feature, CoordinateReferenceSystem crs, Color insideColor, Color borderColor)  {
        Geometry geometry = (Geometry)feature.getDefaultGeometry();
        Coordinate[] coords = geometry.getCoordinates();

        ArrayList<LatLon> vertices = new ArrayList<LatLon>();

        for (int i = 0; i < coords.length; i++) {
            double[] c = {coords[i].x, coords[i].y};
            vertices.add(ProjectionUtils.toLatLon(c, crs));
        }
        return new SurfacePolygon(vertices, insideColor, borderColor);
    }

    public static ComplexPolygon complexPolygonFromFeature(SimpleFeature feature, CoordinateReferenceSystem crs, Color color)  {
        Geometry geometry = (Geometry)feature.getDefaultGeometry();
        Coordinate[] coords = geometry.getCoordinates();
        Position[] vertices = new Position[coords.length];

        for (int i = 0; i < coords.length; i++) {
            double[] c = {coords[i].x, coords[i].y};
            LatLon ll = ProjectionUtils.toLatLon(c, crs);
            vertices[i] = new Position(ll, 1000);
        }
        return new ComplexPolygon(vertices, color);
    }

    public static LinePath linePathFromFeature(SimpleFeature feature, CoordinateReferenceSystem crs, Color color, int width, double elevation, boolean absElevation)  {
        Geometry geometry = (Geometry)feature.getDefaultGeometry();
        Coordinate[] coords = geometry.getCoordinates();
        Position[] vertices = new Position[coords.length];

        for (int i = 0; i < coords.length; i++) {
            double[] c = {coords[i].x, coords[i].y};
            LatLon ll = ProjectionUtils.toLatLon(c, crs);
            vertices[i] = new Position(ll, elevation);
        }
        return new LinePath(vertices, color, width, absElevation);
    }

    public static Polyline polylineFromFeature(SimpleFeature feature, CoordinateReferenceSystem crs, Color color, double elevation) {
        Geometry geometry = (Geometry)feature.getDefaultGeometry();
        Coordinate[] coords = geometry.getCoordinates();

        ArrayList<Position> vertices = new ArrayList<Position>();

        for (int i = 0; i < coords.length; i++) {
            double[] c = {coords[i].x, coords[i].y};
            LatLon ll = ProjectionUtils.toLatLon(c, crs);
            vertices.add(new Position(ll, elevation));
        }
        Polyline polyline = new Polyline();
        polyline.setPositions(vertices);
        polyline.setColor(color);
        polyline.setFollowTerrain(true);
        polyline.setAntiAliasHint(Polyline.ANTIALIAS_NICEST);
        polyline.setPathType(Polyline.LINEAR);
        polyline.setLineWidth(2);
        polyline.setClosed(false);
        polyline.setFilled(false);
        return polyline;
    }

    public static SurfacePolygon polygonFromBoundingBox(double lat1, double long1, double lat2, double long2) {
        return null;
    }

    public static Sector sectorFromEnvelope(Envelope env) {
        return Sector.fromDegrees(env.getMinimum(1), env.getMaximum(1), env.getMinimum(0), env.getMaximum(0));
    }
}
