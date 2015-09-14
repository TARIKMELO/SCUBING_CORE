/*
 * $Id: AnimatedShapefileLayer.java 28 2008-10-05 16:29:18Z iovergard $
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

import gw.events.FeatureSelectionListener;
import gw.util.StyleInfo;
import gw.renderables.ElevatedSurfaceImage;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gw.util.ColorBlend;
import gw.util.GeoToolsUtils;
import gw.util.ProjectionUtils;
import gw.util.WorldWindUtils;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.SurfaceImage;
import gw.util.ProjectionNotFoundException;
import gw.util.TransformFailedException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.Map;
import org.geotools.data.FeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Ian Overgard
 * @author Olaf David
 */
public class SimpleFeatureLayer extends RenderableLayer implements PositionListener {

    int dim = 1024;
    CoordinateReferenceSystem originalCRS;
    FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;
    MapContext map;
    Envelope initialBounds;
    ReferencedEnvelope layerBounds;
    Style style;
    BufferedImage img;
    Graphics2D ig;
    ElevatedSurfaceImage surfaceImage;
    SurfaceImage shadowImage;
    double attrMin, attrMax;
    int attrNum = -1;
    Color borderColor = Color.WHITE;
    Color attrMinColor = Color.BLACK;
    Color attrMaxColor = Color.WHITE;
    int borderWidth = 1;
    SimpleFeatureLayerSelectionMask selectionMask;
    SimpleFeature[] features;
    int[][] imgX;
    int[][] imgY;
    boolean drawLines = true;
    long lastCursorMoveTime = 0;
    Position mousePosition = Position.ZERO;

    boolean drawStyled = true;
    Map<Integer, Double> colorRampValues = null;
    

    /**
     * Constructs a SimpleFeatureLayer
     *
     * @param name The name of the layer (usually the filename)
     * @param source The object providing SimpleFeatures
     * @param s The style to draw this with (if null, a default style is created)
     * @param canvas The canvas this will be drawn on
     * @throws java.io.IOException
     */
    public SimpleFeatureLayer(String name, FeatureSource<SimpleFeatureType, SimpleFeature> source,
            Style s, WorldWindow canvas) throws IOException {

        setPickEnabled(false);
        setName(name);

        featureSource = source;
        style = s;
        img = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);

        map = new DefaultMapContext(featureSource.getSchema().getGeometryDescriptor().getCoordinateReferenceSystem());
        map.addLayer(featureSource, style);

        initialBounds = map.getAreaOfInterest();
        originalCRS = map.getCoordinateReferenceSystem();
        try {
            map.setCoordinateReferenceSystem(ProjectionUtils.getDefaultCRS());
        } catch (FactoryException fe) {
            throw new ProjectionNotFoundException(fe);
        } catch (TransformException te) {
            throw new TransformFailedException(te);
        }

        //store the features locally
        features = (SimpleFeature[]) featureSource.getFeatures().toArray();

        //project the coordinates
        calculateProjectedCoordinates(getSector());
        layerBounds = map.getLayerBounds();

        img = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        redraw();

        //Create the main surface image
        surfaceImage = new ElevatedSurfaceImage(img, getSector());
        surfaceImage.setElevation(0.0f);
        setFloating(false);

        //Create a shadow for the surface image (but hide it initially)
        shadowImage = new SurfaceImage(this.createShadowImage(), getSector());
        shadowImage.setOpacity(0.0f);
        this.addRenderable(shadowImage);
        this.addRenderable(surfaceImage);

        selectionMask = new SimpleFeatureLayerSelectionMask(this);

        canvas.addPositionListener(this);
        canvas.addSelectListener(selectionMask);


    }

    /**
     * Creates geometry for the feature boundaries, providing a sharper
     * image at higher zoom levels (at the cost of more rendering time)
     */
    public void sharpenLines()  {
        StyleInfo info = new StyleInfo();
        this.getStyle().accept(info);
        Class<?> type = this.getSchema().getGeometryDescriptor().getType().getBinding();
        if (type.isAssignableFrom(Polygon.class) || type.isAssignableFrom(MultiPolygon.class)) {
            for(SimpleFeature feature : features) {
                Color lineColor = Color.decode((String) info.getPolygonStroke().getColor().evaluate(feature));
                int width = ((Number) info.getPolygonStroke().getWidth().evaluate(feature)).intValue();
                Renderable renderable = WorldWindUtils.linePathFromFeature(feature, this.getCRS(), lineColor, width, 0, false);
                this.addRenderable(renderable);
            }
        }
        drawLines = false;
    }

    /**
     * 
     * @param f
     */
    public void addFeatureSelection(SimpleFeature f) {
        selectionMask.addSelectedFeature(f);
    }

    /**
     * 
     * @param l the featureselectionListener to add.
     */
    public void addFeatureSelectionListener(FeatureSelectionListener l) {
        selectionMask.addFeatureSelectionListener(l);
    }

    /**
     * 
     * @param sector
     * @throws java.lang.Exception
     */
    private void calculateProjectedCoordinates(Sector sector) {
        imgX = new int[features.length][];
        imgY = new int[features.length][];
        int featureIndex = 0;
        for (SimpleFeature feature : features) {
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Coordinate[] coords = geometry.getCoordinates();
            //int[] xc = new int[coords.length];
            //int[] yc = new int[coords.length];
            imgX[featureIndex] = new int[coords.length];
            imgY[featureIndex] = new int[coords.length];

            for (int i = 0; i < coords.length; i++) {
                double[] c = {coords[i].x, coords[i].y};
                LatLon ll = ProjectionUtils.toLatLon(c, originalCRS);

                double px = (ll.getLongitude().getDegrees() - sector.getMinLongitude().getDegrees()) / sector.getDeltaLonDegrees();
                double py = 1.0 - (ll.getLatitude().getDegrees() - sector.getMinLatitude().getDegrees()) / sector.getDeltaLatDegrees();

                imgX[featureIndex][i] = (int) (px * img.getWidth());
                imgY[featureIndex][i] = (int) (py * img.getHeight());
            }
            featureIndex++;
        }
    }

    /**
     * Clear all selections.
     */
    public void clearSelections() {
        selectionMask.clearSelection();
    }

    /**
     * 
     * @param feature
     * @return The color the feature should be drawn with.
     */
    protected Color colorForFeature(SimpleFeature feature) {
        if (attrNum == -1) {
            return attrMaxColor;
        }
        double attrval = ((Number) (feature.getAttribute(attrNum))).doubleValue();
        double percentage = (attrval - attrMin) / (attrMax - attrMin);
        return ColorBlend.mixColors(attrMinColor, attrMaxColor, (float) percentage);
    }

    /**
     * Draws the feature from the features array at the given index
     *
     * @param index The index of the feature (in features array)
     * @param min The minimum possible value that the feature can have.
     * @param max The maximum possible value the feature can have.
     * @param values The values for the various features.
     */
    protected void drawFeature(int index, double min, double max, Map<Integer, Double> values) {

        //get stuff out of the style info
        SimpleFeature feature = features[index];

        Color featureColor = null;
        if (values != null) { // Use the user specified map of values
            double attrval = values.get(index);
            double percentage = (attrval - min) / (max - min);
            featureColor = ColorBlend.mixColors(attrMinColor, attrMaxColor, (float) percentage);
        } else {// Use the inherent feature values
            featureColor = colorForFeature(feature);
        }
        this.drawFeature(ig, index, featureColor, drawLines);
    }

    /**
     *
     * @param graphics
     * @param index
     * @param color
     * @param drawBorder
     */
    protected void drawFeature(Graphics2D graphics, int index, Color color, boolean drawBorder) {
        Class<?> geomType = getSchema().getGeometryDescriptor().getType().getBinding();
        if (geomType.isAssignableFrom(Polygon.class) || geomType.isAssignableFrom(MultiPolygon.class)) {
            graphics.setStroke(new BasicStroke(getBorderWidth()));
            graphics.setPaint(color);
            graphics.fillPolygon(imgX[index], imgY[index], imgX[index].length);
            if (drawBorder) {
                graphics.setPaint(getBorderColor());
                graphics.drawPolygon(imgX[index], imgY[index], imgX[index].length);
            }
        } else if (geomType.isAssignableFrom(LineString.class) || geomType.isAssignableFrom(MultiLineString.class)) {
            graphics.setStroke(new BasicStroke(4));
            graphics.setPaint(color);
            graphics.drawPolyline(imgX[index], imgY[index], imgX[index].length);
        } else {
            graphics.setPaint(color);
            graphics.fillOval(imgX[index][0], imgY[index][0], 10, 10);
        }
    }
    
    /**
     * Returns the time in milliseconds when the cursor was last moved.
     * @return time in milliseconds.
     */
    public long getLastCursorMoveTime() {
        return lastCursorMoveTime;
    }

    /**
     * 
     * @param event
     */
    @Override
    public void moved(PositionEvent event) {
        if (event == null || event.getPosition() == null) {
            return;
        }
        lastCursorMoveTime = System.currentTimeMillis();
        mousePosition = event.getPosition();

    //ArrayList<Position> vertices = new ArrayList<Position>();
    //vertices.add(new Position(mousePosition.getLatLon(), 0) );
    //vertices.add(new Position(mousePosition.getLatLon(), 8000) );
    //needle.setPositions(vertices);
    //needle.setPosition(mousePosition);
    }

    private double getShadowBrightness() {
        return 0.25 + (1.0 - getElevation() / 40000.0) * 0.75;
    }

    private BufferedImage createShadowImage() {
        BufferedImage shadowImg = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = shadowImg.createGraphics();
        graphics.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        graphics.clearRect(0, 0, dim, dim);
        for (int i = 0; i < features.length; i++) {
            drawFeature(graphics, i, new Color(0.0f, 0.0f, 0.0f, 0.4f), false);
        }
        return shadowImg;
    }

    /**
     * Redraw the features to the texture buffer
     */
    public void redraw()  {
        if(isDrawStyled())
            redrawStyled();
        else
            redrawWithColorRamp();
    }

    /**
     * Redraws the layer with color ramping instead of geotools styling.
     */
    protected void redrawWithColorRamp()  {
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        if (getColorRampValues() != null) {
            for (Object obj : getColorRampValues().values()) {
                double attr;
                attr = ((Number) obj).doubleValue();

                if (attr < min) {
                    min = attr;
                }
                if (attr > max) {
                    max = attr;
                }
            }
        }

        //Envelope e = initialBounds;
        //Sector sector = this.getSector();
        ig = img.createGraphics();
        ig.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        ig.clearRect(0, 0, dim, dim);
        for (int i = 0; i < features.length; i++) {
            drawFeature(i, min, max, getColorRampValues());
        }
        if (surfaceImage != null) {
            surfaceImage.refresh();
        }
    }

    /**
     * Redraws the layer using the GeoTools style
     */
    protected void redrawStyled()  {
        ig = img.createGraphics();
        ig.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        ig.clearRect(0, 0, dim, dim);
        StreamingRenderer render = new StreamingRenderer();
        render.setContext(map);
        render.paint(ig, new Rectangle(dim, dim), layerBounds);
        if (surfaceImage != null) {
            surfaceImage.refresh();
        }
    }

    /**
     * 
     * @param f the feature to select
     */
    public void selectFeature(SimpleFeature f) {
        try {
            selectionMask.setSelectedFeature(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines how transparent the shadow needs to be, based on the elevation
     * of the floating layer (if it's floating).
     */
    private void calcShadow() {
        if (shadowImage == null || surfaceImage == null) {
            return;
        }
        double elev = getElevation();
        if (elev > 0.0f && isFloating()) {
            shadowImage.setOpacity(surfaceImage.getOpacity() * getShadowBrightness());
        } else {
            shadowImage.setOpacity(0.0f);
        }
    }

    /**
     *
     * @return Returns true if the layer is currently floating off the ground.
     */
    public boolean isFloating() {
        return surfaceImage.getFloating();
    }

    /**
     * Controls if the layer should be floating off the ground, or
     * spread across it.
     *
     * @param floating
     */
    public void setFloating(boolean floating) {
        surfaceImage.setFloating(floating);
        if(selectionMask != null)
            selectionMask.elevationChanged();
        calcShadow();
    }

    /**
     *
     * @return the elevation of the surface image
     */
    public double getElevation() {
        return surfaceImage.getElevation();
    }

    /** set the elevation
     *
     * @param elev
     */
    public void setElevation(double elev) {
        surfaceImage.setElevation(elev);
        if(selectionMask != null)
            selectionMask.elevationChanged();
        calcShadow();
    }

    /**
     * @return The original coordinate reference system this shapefile was in.
     */
    public CoordinateReferenceSystem getCRS() {
        return this.originalCRS;
    }

    /** Get the feature at a certain location
     * 
     * @param latlon the feature at a position.
     * @return The feature object.
     */
    public SimpleFeature getFeatureAt(LatLon latlon) {
        return GeoToolsUtils.getFeatureAt(latlon, featureSource, originalCRS);
    }

    /**
     * 
     * @return all SimpleFeatures
     */
    public SimpleFeature[] getFeatures() {
        return features;
    }

    /**
     * 
     * @return The feature source for this shapefile.
     */
    public FeatureSource getFeatureSource() {
        return featureSource;
    }

    /** Get the current mouse position.
     *
     * @return the current mouse position
     */
    public Position getMousePosition() {
        return mousePosition;
    }

    /**
     * 
     * @return The schema for this shapefile.
     */
    public SimpleFeatureType getSchema() {
        return featureSource.getSchema();
    }

    /**
     * 
     * @return The boundaries of this shapefile.
     * @throws java.io.IOException
     */
    public Sector getSector() {
        Envelope env = map.getAreaOfInterest();
        double aspect = 1.0;//env.getWidth()/env.getHeight();
        double halfWidth = env.getWidth() * 0.5 * aspect;
        double halfHeight = env.getHeight() * 0.5;
        double cx = env.getMinX() + (env.getWidth() * 0.5);
        double cy = env.getMinY() + (env.getHeight() * 0.5);

        Sector unprojectedSector = Sector.fromDegrees(cy - halfHeight, cy + halfHeight, cx - halfWidth, cx + halfWidth);
        return unprojectedSector;
    }

    /**
     * 
     * @param attrnum
     */
    public void setPrimaryAttr(int attrnum) {
        this.attrNum = attrnum;

        //figure out min and max range of the attribute
        attrMin = Double.MAX_VALUE;
        attrMax = Double.MIN_VALUE;
        for (SimpleFeature feature : this.getFeatures()) {
            double attr = Double.parseDouble(feature.getAttribute(attrNum).toString());
            if (attr < attrMin) {
                attrMin = attr;
            }
            if (attr > attrMax) {
                attrMax = attr;
            }
        }
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style)  {
        this.style = style;
        this.map.clearLayerList();
        map.addLayer(featureSource, style);
        this.redrawStyled();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getAttrMinColor() {
        return attrMinColor;
    }

    public void setAttrMinColor(Color attrMinColor) {
        this.attrMinColor = attrMinColor;
    }

    public Color getAttrMaxColor() {
        return attrMaxColor;
    }

    public void setAttrMaxColor(Color attrMaxColor) {
        this.attrMaxColor = attrMaxColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    @Override
    public double getOpacity() {
        return this.surfaceImage.getOpacity();
    }

    @Override
    public void setOpacity(double opacity) {
        this.surfaceImage.setOpacity(opacity);

        if (getElevation() > 0) {
            shadowImage.setOpacity(opacity * getShadowBrightness());
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @return the drawStyled
     */
    public boolean isDrawStyled() {
        return drawStyled;
    }

    /**
     * @param drawStyled the drawStyled to set
     */
    public void setDrawStyled(boolean drawStyled) {
        this.drawStyled = drawStyled;
    }

    /**
     * @return the colorRampValues
     */
    public Map<Integer, Double> getColorRampValues() {
        return colorRampValues;
    }

    /**
     * @param colorRampValues the colorRampValues to set
     */
    public void setColorRampValues(Map<Integer, Double> colorRampValues) {
        this.colorRampValues = colorRampValues;
    }
}
