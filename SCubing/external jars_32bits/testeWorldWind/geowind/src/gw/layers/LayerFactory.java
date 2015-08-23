/*
 * $Id: BillboardList.java 20 2008-07-22 17:57:47Z od $
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
package gw.layers;

import gov.nasa.worldwind.WorldWindow;
import gw.util.AnimationFile;
import gw.layers.PointLayer.Spot;
import gw.util.StyleUtils;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/** Factory for SimpleFeature and GridCoverage layer creation from different sources.
 *
 * @author Olaf David
 * @author Ian Overgard
 */
public class LayerFactory {

    private LayerFactory() {
    }

    /**
     * Creates a SimpleFeatureLayer from a shape file set.
     * @param shpFile The .shp file.
     * @param window The WorldWindow canvas.
     */
    public static SimpleFeatureLayer fromShapefile(File shpFile, WorldWindow window) throws IOException, MalformedURLException {
        if (!shpFile.exists()) {
            throw new IllegalArgumentException(shpFile.toString());
        }

        ShapefileDataStore shapeStore = new ShapefileDataStore(shpFile.toURI().toURL());
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = shapeStore.getFeatureSource();
        Style s = StyleUtils.createStyle(shpFile, featureSource.getSchema());
        return new SimpleFeatureLayer(shpFile.getName(), featureSource, s, window);
    }

    /**
     * Create an animated simple feature layer
     *
     * @param shpFile the shape file
     * @param animationFile the file with the animation data
     * @param window
     * @return An AnimatedSimpleFeatureLayer
     * @throws java.lang.Exception
     */
    public static AnimatedSimpleFeatureLayer fromFile(File shpFile, AnimationFile animationFile, WorldWindow window) throws IOException, MalformedURLException {
        if (!shpFile.exists()) {
            throw new IllegalArgumentException(shpFile.toString());
        }
        ShapefileDataStore shapeStore = new ShapefileDataStore(shpFile.toURI().toURL());
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = shapeStore.getFeatureSource();
        Style s = StyleUtils.createStyle(shpFile, featureSource.getSchema());
        return new AnimatedSimpleFeatureLayer(shpFile.getName(), featureSource, s, window, animationFile);
    }

    /**
     * Create a GridCoverageLayer from a Geotiff
     *
     * @param file The .tiff file
     * @return A GridCoverageLayer
     * @throws java.lang.Exception
     */
    public static GridCoverageLayer fromGeotiff(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException(file.toString());
        }

        GeoTiffReader reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
        GridCoverage2D coverage = (GridCoverage2D) reader.read(null);

        return new GridCoverageLayer(file.getName(), coverage);
    }

    //     public static GridCoverage2D readCoverageFromArcGridFile(String filename) {
//        URL url = ArcGridReader.class.getClassLoader().getResource(filename);
//        ArcGridFormat f = new org.geotools.gce.arcgrid.ArcGridFormat();
//        GridCoverageReader reader = f.getReader(url);
//        //reader.read();
//        return null;
//    }
    
    public static PointLayer spotLayer(Spot[] points, WorldWindow window, String iconFilename) {
        return new PointLayer(points, window, iconFilename);
    }
}
