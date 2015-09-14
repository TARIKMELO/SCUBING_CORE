/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gw.layers;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Renderable;
import gw.events.FeatureSelectionListener;
import gw.renderables.ElevatedSurfaceImage;
import gw.util.GeoToolsUtils;
import gw.util.WorldWindUtils;
import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * Handles selections and tooltips for SimpleFeatureLayers
 * (note that the SimpleFeatureLayer creates this helper class automatically,
 * so you shouldn't need to instantiate this directly)
 *
 * @author Ian Overgard
 * @author Olaf David
 */
public class SimpleFeatureLayerSelectionMask implements SelectListener {

    SimpleFeatureLayer baseShapeLayer;
    GlobeAnnotation tooltip;
    SimpleFeature tooltipFeature;
    ArrayList<FeatureSelection> selections = new ArrayList<FeatureSelection>();
    List<FeatureSelectionListener> featureSelectionListeners = new ArrayList<FeatureSelectionListener>();

    /**
     * Represents a selected feature. There's one feature selection
     * object for every selection made.
     */
     public class FeatureSelection {

        private SimpleFeature feature;
        private Renderable renderable;

        /**
         *
         * @return Returns the SimpleFeature this selection wraps.
         */
        public SimpleFeature getFeature() {
            return feature;
        }

        public Renderable getRenderable() {
            return renderable;
        }

        public void setRenderable(Renderable renderable) {
            this.renderable = renderable;
        }

        public void makeRenderable() {
            Class<?> geomType = baseShapeLayer.getSchema().getGeometryDescriptor().getType().getBinding();
            if (geomType.isAssignableFrom(Polygon.class) ||
                    geomType.isAssignableFrom(MultiPolygon.class) ||
                    geomType.isAssignableFrom(LineString.class) ||
                    geomType.isAssignableFrom(MultiLineString.class)) {

                if (baseShapeLayer.isFloating()) {
                    renderable = (Renderable) WorldWindUtils.linePathFromFeature(feature, baseShapeLayer.getCRS(), new Color(1.0f, 0.0f, 0.0f, 0.75f), 2, baseShapeLayer.getElevation(), baseShapeLayer.isFloating());
                } else {
                    renderable = WorldWindUtils.polylineFromFeature(feature, baseShapeLayer.getCRS(),new Color(1.0f, 0.0f, 0.0f, 0.75f), baseShapeLayer.getElevation());
                }
            } else {
                Position p = GeoToolsUtils.getFeatureCenter(feature, baseShapeLayer.getCRS());
                renderable = new Sphere(new Vec4(p.getLongitude().getDegrees(), p.getLatitude().getDegrees(), p.getElevation()), 20);
            }
            ((RenderableLayer) baseShapeLayer).addRenderable(renderable);
        }

        public void remakeRenderable() {
            baseShapeLayer.removeRenderable(renderable);
            makeRenderable();
        }

        public FeatureSelection(SimpleFeature feature) {
            this.feature = feature;
            makeRenderable();
            
        }
    }


    /**
     *
     * @param sl
     */
    public SimpleFeatureLayerSelectionMask(SimpleFeatureLayer sl) {
        baseShapeLayer = sl;
    }

    /**
     * Called when the elevation is changed.
     */
    public void elevationChanged() {
        for(FeatureSelection selection : selections) {
            selection.remakeRenderable();
        }
    }

    /**
     * Called whenever the base shapefile layer is clicked.
     *
     * @param event
     */
    @Override
    public void selected(SelectEvent event) {

        if (event.getMouseEvent() == null) {
            return;
        }
        if (!baseShapeLayer.isPickEnabled()) {
            return;
        }
        if (event.getEventAction().equals(SelectEvent.LEFT_CLICK) || event.getEventAction().equals(SelectEvent.RIGHT_CLICK)) {
            PickedObject obj = event.getTopPickedObject();

            if (obj != null && obj.getObject() instanceof ElevatedSurfaceImage) {
                try {
                    SimpleFeature featureAtMousePoint = baseShapeLayer.getFeatureAt(baseShapeLayer.getMousePosition().getLatLon());
                    if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)) {
                        setSelectedFeature(featureAtMousePoint);
                    } else if (event.getEventAction().equals(SelectEvent.RIGHT_CLICK)) {
                        showTooltip(featureAtMousePoint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Removes any currently displayed tooltips.
     */
    public void clearTooltip() {
        if (tooltip != null) {
            this.baseShapeLayer.removeRenderable(tooltip);
        }
    }

    /**
     * Creates a renderable tooltip for a given feature.
     *
     * @param feature
     */
    public void showTooltip(SimpleFeature feature) {
        clearTooltip();
        if (feature.equals(tooltipFeature)) {
            tooltipFeature = null;
            return;
        }
        tooltipFeature = feature;
        String txt = "";
        for (int i = 1; i < feature.getAttributeCount(); i++) {
            txt = txt + feature.getFeatureType().getType(i).getName().getLocalPart() +
                    ": " + feature.getAttribute(i).toString() + "\n";
        }

        tooltip = new GlobeAnnotation(txt, new Position(baseShapeLayer.getMousePosition().getLatLon(), 0));
        tooltip.getAttributes().setInsets(new Insets(1, 1, 1, 1));
        tooltip.getAttributes().setBorderColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));
        tooltip.getAttributes().setBackgroundColor(new Color(1.0f, 1.0f, 0.85f, 1.0f));
        tooltip.getAttributes().setTextColor(Color.BLACK);
        tooltip.getAttributes().setScale(0.8);
        tooltip.setAlwaysOnTop(true);
        baseShapeLayer.addRenderable(tooltip);
    }

    /** 
     * Get the selected SimpleFeature
     * 
     * @return the feature that is selected, or null if nothing is selected.
     */
    public SimpleFeature getSelectedFeature() {
        if (selections.isEmpty()) {
            return null;
        }
        return selections.get(0).getFeature();
    }

    /** 
     * Add a feature selection Listener.
     * @param l the listener to add
     */
    public void addFeatureSelectionListener(FeatureSelectionListener l) {
        featureSelectionListeners.add(l);
    }

    /** 
     * Remove a feature selection Listener
     * @param l the listener to remove
     */
    public void removeFeatureSelectionListener(FeatureSelectionListener l) {
        featureSelectionListeners.remove(l);
    }

    /**
     * Calls featureSelected on any listeners to determine if they wish
     * to cancel or confirm the selection. This is so that a selection can
     * be stopped if it's not what the user app wants.
     *
     * @param feature
     * @return true is the selection is confirmed, otherwise false
     */
    private boolean confirmSelection(SimpleFeature feature) {
        boolean selectionConfirmed = true;
        for (FeatureSelectionListener listener : featureSelectionListeners) {
            if (!listener.featureSelected(feature)) {
                selectionConfirmed = false;
            }
        }
        return selectionConfirmed;
    }

    /**
     * Checks if a given feature is currently selected.
     *
     * @param feature
     * @return true if selected, false otherwise.
     */
    public boolean isFeatureSelected(SimpleFeature feature) {
        for (FeatureSelection s : selections) {
            if (s.getFeature() == feature) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets a feature as being selected, without clearing any other selections.
     * Note that this can be canceled by any listeners if they do not confirm
     * the selection.
     *
     * @param feature The feature to set as selected.
     * @throws java.lang.Exception
     */
    public void addSelectedFeature(SimpleFeature feature) {
        if (isFeatureSelected(feature)) {
            return;
        }
        if (confirmSelection(feature)) {
            selections.add(new FeatureSelection(feature));
        }
    }

    /** 
     * Select a feature, clear any other present selections.
     *
     * @param feature the feature to select.
     */
    public void setSelectedFeature(SimpleFeature feature) {
        if (selections.size() == 1 && isFeatureSelected(feature)) {
            return;
        }
        if (confirmSelection(feature)) {
            clearSelection();
            if (feature != null) {
                selections.add(new FeatureSelection(feature));
            }
        }
    }

    /**
     * Clears all the current selections.
     */
    public void clearSelection() {
        try {
            for (FeatureSelection f : this.selections) {
                ((RenderableLayer) baseShapeLayer).removeRenderable(f.getRenderable());
            }
            this.selections.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
