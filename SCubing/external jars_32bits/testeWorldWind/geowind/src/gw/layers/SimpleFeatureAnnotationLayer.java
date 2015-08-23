/*
 * $Id: ShapefileAnnotationLayer.java 67 2008-12-08 20:23:28Z iovergard $
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
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gw.util.GeoToolsUtils;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import org.geotools.feature.FeatureIterator;
import org.geotools.styling.Font;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Provides detailed information for a SimpleFeatureLayer, such as
 * labels showing attribute values.
 *
 * @author Ian Overgard
 * @author Olaf David
 *
 */
public class SimpleFeatureAnnotationLayer extends RenderableLayer {
    
    public SimpleFeatureAnnotationLayer(SimpleFeatureLayer base) throws IOException {
        constructUsingStyle(base);
    }

    public SimpleFeatureAnnotationLayer(SimpleFeatureLayer base, String[] text) throws IOException {
        Renderable[] renderables = buildLabels(base, text);
        for(Renderable r : renderables) { 
            if(r != null)
                this.addRenderable(r);
        }
    }

    public static Renderable[] buildLabels(SimpleFeatureLayer base, String[] text) {
        Renderable[] rval = new Renderable[text.length];
        Class<?> type = base.getSchema().getGeometryDescriptor().getType().getBinding();
        
        for(int i=0; i<base.getFeatures().length; i++) {
            Position center = GeoToolsUtils.getFeatureCenter(base.getFeatures()[i], base.getCRS());

            GlobeAnnotation ga = new GlobeAnnotation(text[i],
                                                     Position.fromDegrees(center.getLatitude().getDegrees(),
                                                        center.getLongitude().getDegrees(),
                                                        0));
            ga.getAttributes().setInsets(new Insets(1,1,1,1));
            ga.getAttributes().setBorderColor(new Color(1.0f,1.0f,1.0f,0.0f));
            ga.getAttributes().setBackgroundColor(new Color(0.0f,0.0f,0.0f,0.0f));
            ga.getAttributes().setTextColor(Color.WHITE);
            ga.getAttributes().setDrawOffset(new Point(0,0));
            rval[i] = ga;
        }
        return rval;
    }

    public void constructUsingStyle(SimpleFeatureLayer base) throws IOException {
        StyleInfo info = new StyleInfo();
        base.getStyle().accept(info);
        Class<?> type = base.getSchema().getGeometryDescriptor().getType().getBinding();
        if (type.isAssignableFrom(Polygon.class) || type.isAssignableFrom(MultiPolygon.class)) {
            FeatureIterator i = base.getFeatureSource().getFeatures().features();
            while(i.hasNext()) {
                SimpleFeature feature = (SimpleFeature)i.next();
                Color backgroundColor = Color.BLACK;
                Color textColor = Color.WHITE;
                Font[] fonts = info.getTextFonts();
                Double op;
                if(info.getTextFill().getBackgroundColor() != null)
                    backgroundColor = Color.decode((String)info.getTextFill().getBackgroundColor().evaluate(feature));
                if(info.getTextFill().getColor() != null)
                    textColor = Color.decode((String)info.getTextFill().getColor().evaluate(feature));
                if(info.getTextFill().getOpacity() != null)
                    op = (Double)info.getTextFill().getOpacity().evaluate(feature);

                if(info.getLabel() != null) {
                    String text = info.getLabel().evaluate(feature).toString();
                    Position center = GeoToolsUtils.getFeatureCenter(feature, base.getCRS()) ;
                    GlobeAnnotation ga = new GlobeAnnotation(text, center);
                    ga.getAttributes().setBorderColor(Color.BLACK);
                    ga.getAttributes().setBackgroundColor(backgroundColor);
                    ga.getAttributes().setTextColor(textColor);
                    if(fonts != null && fonts.length > 0) {
                        String fontName = fonts[0].getFontFamily().evaluate(feature).toString();
                        double fontSize = Double.parseDouble(fonts[0].getFontSize().evaluate(feature).toString());
                        String fontStyle = fonts[0].getFontStyle().evaluate(feature).toString();
                        ga.getAttributes().setFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, (int)fontSize));
                    }
                    this.addRenderable(ga);
                }

                //this.addRenderable(WorldWindUtils.linePathFromFeature(feature, base.getCRS(), Color.decode((String)info.getPolygonStroke().getColor().evaluate(feature))));
            }
        }
    }

}
