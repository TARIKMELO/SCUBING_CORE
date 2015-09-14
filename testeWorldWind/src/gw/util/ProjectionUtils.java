/*
 * $Id: ProjectionUtils.java 20 2008-07-22 17:57:47Z od $
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

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gw.util.ProjectionNotFoundException;
import gw.util.TransformFailedException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Ian Overgard
 */
public class ProjectionUtils {

    private static CoordinateReferenceSystem EPSG4326 = null;
    static final String wkt = "GEOGCS[\"WGS 84\", DATUM[\"WGS_1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Longitude\", EAST], AXIS[\"Latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]]";

    public static CoordinateReferenceSystem getDefaultCRS() {
        if (EPSG4326 == null) {
            try {
                EPSG4326 = CRS.parseWKT(wkt);
            } catch (FactoryException factory) {
                //realistically, this should never happen, but just in case. 
                factory.printStackTrace();
            }
        }
        return EPSG4326;
    }

    public static double[] latLonToProjection(LatLon latlon, CoordinateReferenceSystem targetCRS) {
        double[] a = {latlon.getLongitude().getDegrees(), latlon.getLatitude().getDegrees() };
        double[] b = {0, 0};
        try {
            CRS.findMathTransform(getDefaultCRS(), targetCRS, true).transform(a, 0, b, 0, 1);
        }
        catch (FactoryException factoryEx) {
            throw new ProjectionNotFoundException(factoryEx);
        }
        catch (TransformException trans) {
            throw new TransformFailedException(trans);
        }

        return b;
    }

    public static LatLon toLatLon(double[] point, CoordinateReferenceSystem originalCRS)  {
        double[] result = {0, 0};
        try {
            CRS.findMathTransform(originalCRS, getDefaultCRS(), true).transform(point, 0, result, 0, 1);
        }
        catch (FactoryException factoryEx) {
            throw new ProjectionNotFoundException(factoryEx);
        }
        catch (TransformException trans) {
            throw new TransformFailedException(trans);
        }
        return LatLon.fromDegrees(result[1], result[0]);
    }

    public static ReferencedEnvelope transformEnvelope(Envelope env, CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS)  {
        double[] minLL = {env.getMinimum(0), env.getMinimum(1)};
        double[] maxLL = {env.getMaximum(0), env.getMaximum(1)};
        double[] a = {0, 0};
        double[] b = {0, 0};
        try {
            MathTransform mt = CRS.findMathTransform(sourceCRS, targetCRS);
            mt.transform(minLL, 0, a, 0, 1);
            mt.transform(maxLL, 0, b, 0, 1);
        }
        catch (FactoryException factoryEx) {
            throw new ProjectionNotFoundException(factoryEx);
        }
        catch (TransformException trans) {
            throw new TransformFailedException(trans);
        }
        return new ReferencedEnvelope(a[0], b[0], a[1], b[1], targetCRS);
    }

    public static Sector transformSector(Sector s, CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
        double[] minLL = {s.getMinLongitude().getDegrees(), s.getMinLatitude().getDegrees()};
        double[] maxLL = {s.getMaxLongitude().getDegrees(), s.getMaxLatitude().getDegrees()};
        double[] a = {0, 0};
        double[] b = {0, 0};
        try {
            MathTransform mt = CRS.findMathTransform(sourceCRS, targetCRS);
            mt.transform(minLL, 0, a, 0, 1);
            mt.transform(maxLL, 0, b, 0, 1);
        }
        catch (FactoryException factoryEx) {
            throw new ProjectionNotFoundException(factoryEx);
        }
        catch (TransformException trans) {
            throw new TransformFailedException(trans);
        }
        return Sector.fromDegrees(a[1], b[1], a[0], b[0]);
    }
}
