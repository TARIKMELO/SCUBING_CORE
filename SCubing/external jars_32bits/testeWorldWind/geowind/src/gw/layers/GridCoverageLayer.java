/*
 * $Id: GeoTiffLayer.java 69 2008-12-08 21:35:21Z iovergard $
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

import gw.util.ProjectionUtils;
import gw.util.WorldWindUtils;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gw.renderables.ElevatedSurfaceImage;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.media.jai.RenderedImageAdapter;
import org.geotools.coverage.grid.GridCoverage2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/** 
 *
 * @author Ian Overgard
 * @author Olaf David
 */
public class GridCoverageLayer extends RenderableLayer {

    GridCoverage2D coverage;
    CoordinateReferenceSystem crs;
    Sector projectedSector;
    ElevatedSurfaceImage surfaceImage;

    public GridCoverageLayer(String name, GridCoverage2D coverage) {
        this.coverage = coverage;

        // Using a GridCoverage2D
        crs = coverage.getCoordinateReferenceSystem2D();
        RenderedImage rimage = coverage.getRenderedImage();
        RenderedImageAdapter ria = new RenderedImageAdapter(rimage);
        BufferedImage image = ria.getAsBufferedImage();
        projectedSector = WorldWindUtils.sectorFromEnvelope(ProjectionUtils.transformEnvelope(coverage.getEnvelope(), crs, ProjectionUtils.getDefaultCRS()));


        surfaceImage = new ElevatedSurfaceImage(image, projectedSector);
        surfaceImage.setFloating(false);
        this.addRenderable(surfaceImage);
        surfaceImage.setOpacity(1.0);
        setName(name);
    }

    public double getElevation() {
        return surfaceImage.getElevation();
    }

    public void setElevation(double elev) {
        surfaceImage.setElevation(elev);
    }

    public boolean getFloating() {
        return surfaceImage.getFloating();
    }

    public void setFloating(boolean floating) {
        surfaceImage.setFloating(floating);
    }

    @Override
    public double getOpacity() {
        return surfaceImage.getOpacity();
    }

    @Override
    public void setOpacity(double value) {
        surfaceImage.setOpacity(value);
    }

    public Sector getSector() {
        return projectedSector;
    }

      @Override
    public String toString() {
        return this.getName();
    }
}
