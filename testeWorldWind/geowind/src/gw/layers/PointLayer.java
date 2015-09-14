/*
 * $Id: PointLayer.java 9 2008-07-21 19:46:43Z iovergard $
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

import com.vividsolutions.jts.geom.Geometry;
import gw.util.ProjectionUtils;
import com.vividsolutions.jts.geom.Point;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwind.render.WWIcon;
import gw.util.ShapeUtils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.MapContext;
import org.opengis.feature.simple.SimpleFeature;


/**
 *  
 *
 * @author Ian Overgard
 */
public class PointLayer extends IconLayer implements SelectListener {

    public static class Spot {

        private LatLon position;
        private String description;
        private Object tag;
        private WWIcon icon;

        public Spot(LatLon position, String description, Object tag) {
            this.position = position;
            this.description = description;
            this.tag = tag;
        }

        public LatLon getPosition() {
            return position;
        }

        public void setPosition(LatLon position) {
            this.position = position;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public WWIcon getIcon() {
            return icon;
        }

        public void setIcon(WWIcon icon) {
            this.icon = icon;
        }
    }

    Sector sector;
    private ArrayList<Spot> spots = new ArrayList<Spot>();
    String tooltipFont = "Tahoma-12";
    String iconFilename;
    
    public PointLayer(Spot[] points, WorldWindow canvas, String iconFilename) {
        super();
        this.iconFilename = iconFilename;
        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY;
        double minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
        for (Spot p : points) {
            spots.add(p);
            LatLon ll = p.getPosition();
            double lat = ll.getLatitude().getDegrees();
            double lon = ll.getLongitude().getDegrees();
            addIconForSpot(p);

            if (lat < minLat) {
                minLat = lat;
            }
            if (lon < minLon) {
                minLon = lon;
            }
            if (lat > maxLat) {
                maxLat = lat;
            }
            if (lon > maxLon) {
                maxLon = lon;
            }
        }
        sector = Sector.fromDegrees(minLat, maxLat, minLon, maxLon);
        canvas.addSelectListener(this);
        
        this.setMaxActiveAltitude(100000);
    }

    public static Spot[] spotsFromShapefile(File file) throws MalformedURLException, IOException {
        ShapefileDataStore shapefile = new ShapefileDataStore(file.toURL());

        //read features
        MapContext map = ShapeUtils.createMapContext(file, shapefile.getFeatureSource());
        if (map.getCoordinateReferenceSystem() == null) {
            throw new IOException("Shape file does not have a projection");
        }

        String[] typeNames = shapefile.getTypeNames();
        FeatureSource featureSource = shapefile.getFeatureSource(typeNames[0]);

        FeatureCollection collection = featureSource.getFeatures();
        FeatureIterator iterator = null;
        Spot[] spots = new Spot[collection.size()];
        int i = 0;
        try {
            //Load all the features
            iterator = collection.features();
            while (iterator.hasNext()) {
                SimpleFeature feature = (SimpleFeature)iterator.next();
                Point center = ((Geometry)feature.getDefaultGeometry()).getCentroid();
                LatLon ll = ProjectionUtils.toLatLon(new double[]{center.getX(), center.getY()}, map.getCoordinateReferenceSystem());
                spots[i++] = new Spot(ll, feature.toString(), feature);
            }
        } finally {
            iterator.close();
        }
        return spots;
    }

    private Font makeToolTipFont() {
        HashMap<TextAttribute, Object> fontAttributes = new HashMap<TextAttribute, Object>();

        fontAttributes.put(TextAttribute.BACKGROUND, new java.awt.Color(1.0f, 1.0f, 1.0f, 1f));
        return Font.decode(tooltipFont).deriveFont(fontAttributes);
    }

    private void addIconForSpot(Spot s) {
        LatLon ll = s.getPosition();
        WWIcon icon = new UserFacingIcon(this.iconFilename,
                new Position(ll.getLatitude(), ll.getLongitude(), 0));
        icon.setSize(new Dimension(32, 32));
        icon.setHighlightScale(1.25);
        icon.setToolTipFont(this.makeToolTipFont());
        icon.setToolTipText(s.getDescription());
        icon.setToolTipTextColor(java.awt.Color.BLACK);
        icon.setShowToolTip(false);
        s.setIcon(icon);
        this.addIcon(icon);
    }

    public void calculateSector() {
        double minLat = Double.POSITIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        double minLon = Double.POSITIVE_INFINITY;
        double maxLon = Double.NEGATIVE_INFINITY;
        for (Spot p : getSpots()) {
            LatLon ll = p.getPosition();
            double lat = ll.getLatitude().getDegrees();
            double lon = ll.getLongitude().getDegrees();

            if (lat < minLat) {
                minLat = lat;
            }
            if (lon < minLon) {
                minLon = lon;
            }
            if (lat > maxLat) {
                maxLat = lat;
            }
            if (lon > maxLon) {
                maxLon = lon;
            }
        }
        sector = Sector.fromDegrees(minLat, maxLat, minLon, maxLon);
    }

    public Spot getSpotByTag(Object tag) {
        for (Spot spot : getSpots()) {
            if (spot.getTag() == tag) {
                return spot;
            }
        }
        return null;
    }

    public void addSpot(Spot s) {
        getSpots().add(s);
        addIconForSpot(s);
        calculateSector();
    }

    public void removeSpot(Spot s) {
        getSpots().remove(s);
        this.removeIcon(s.getIcon());
        calculateSector();
    }

    @Override
    public void selected(SelectEvent event) {
        for (WWIcon ico : this.getIcons()) {
            ico.setHighlighted(false);
            ico.setShowToolTip(false);
        }
        if (event.getTopPickedObject() != null && event.getTopPickedObject().getObject() instanceof WWIcon) {
            WWIcon myIcon = (WWIcon) event.getTopPickedObject().getObject();
            if (myIcon != null) {
                myIcon.setHighlighted(true);
                myIcon.setShowToolTip(true);
            }
        }
    }

    public Sector getSector() {
        return sector;
    }

    public ArrayList<Spot> getSpots() {
        return spots;
    }

    public void setSpots(ArrayList<Spot> spots) {
        this.spots = spots;
    }
    
}
