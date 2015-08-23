/*
 * $Id: AnimatedShapefileLayer.java 50 2008-10-31 22:40:42Z iovergard $
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

import gw.util.StyleInfo;
import gov.nasa.worldwind.WorldWindow;
import gw.util.AnimationFile;
import gw.util.ColorBlend;
import java.awt.Color;
import java.io.IOException;
import org.geotools.data.FeatureSource;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/** 
 * A SimpleFeatureLayer which can be animated with animation data. Note
 * that this is more a prototype than a usable class, and will likely
 * be soon removed. 
 *
 * @author Ian Overgard
 * @author Olaf David
 * @deprecated
 */
public class AnimatedSimpleFeatureLayer extends SimpleFeatureLayer {
    
    AnimationFile animationFile;
    StyleInfo styleInfo;
    private int frame = 0;
    AnimationFile.Timestep currentTimestep = null;
    double colMin = 0;
    double colMax = 0;
    private int animationColumn;

    /**
     *
     * @param name
     * @param source
     * @param s
     * @param canvas
     * @param animationFile
     * @throws java.lang.Exception
     */
    public AnimatedSimpleFeatureLayer(String name, FeatureSource<SimpleFeatureType, SimpleFeature> source,
            Style s, WorldWindow canvas, AnimationFile animationFile) throws IOException {
        super(name, source, s, canvas);
        this.animationFile = animationFile;
        setFrame(0);
    }

    /**
     * 
     * @return The current animation frame
     */
    public int getFrame() {
        return frame;
    }

    /**
     * 
     * @param frame The current animation frame
     */
    public void setFrame(int frame) {
        this.frame = frame;
        currentTimestep = animationFile.getTimestep(frame);
    }
    
    /**
     * 
     * @return The total number of animation frames
     */
    public int getFrameCount() { 
        if(animationFile == null)
            return 0;
        return this.animationFile.getTimestepCount();
    }
    
    /**
     * Sets the primary attribute by its index, which is the attribute
     * that acts as a unique identifier for the feature.
     *
     * @param attr
     */
    @Override
    public void setPrimaryAttr(int attr) {
        super.setPrimaryAttr(attr);
    }
    

    /**
     * Redraws the feature layer
     */
    @Override
    public void redraw() {
        redrawWithColorRamp();
    }
    
    /**
     * 
     * @param feature
     * @return The color the feature should be drawn with.
     */
    @Override
    protected Color colorForFeature(SimpleFeature feature) {
        String[] attrs = currentTimestep.match(1, feature.getAttribute(attrNum).toString());
        double attr = Double.parseDouble(attrs[getAnimationColumn()]);
        double percentage = (attr-colMin) / (colMax-colMin);
        return ColorBlend.mixColors(attrMinColor, attrMaxColor, (float)percentage);
    }

    /**
     *  Returns the column that provides the animation data.
     *
     * @return the column that is providing the animation data.
     */
    public int getAnimationColumn() {
        return animationColumn;
    }

    /**
     *
     * Sets the column that provides the animation data.
     *
     * @param animationColumn the column to provide animation data.
     */
    public void setAnimationColumn(int animationColumn) {
        this.animationColumn = animationColumn;
        colMin = this.animationFile.getMinDouble(animationColumn);
        colMax = this.animationFile.getMaxDouble(animationColumn);
    }

}
