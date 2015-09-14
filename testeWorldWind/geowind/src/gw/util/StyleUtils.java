/*
 * $Id: StyleUtils.java 26 2008-09-11 06:28:49Z iovergard $
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

import java.io.*;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.*;
import org.opengis.filter.FilterFactory;
import com.vividsolutions.jts.geom.*;
import java.awt.Color;
import java.net.MalformedURLException;
import java.util.Random;
import org.geotools.filter.ConstantExpression;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Ian Overgard
 */
public class StyleUtils {

    static final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    static final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

    public static Style createStyle(File file, FeatureType schema) throws IOException, NullPointerException {
        File sld = toSLDFile(file);
        if (sld.exists()) {
            return createFromSLD(sld);
        }
        Class<?> type = schema.getGeometryDescriptor().getType().getBinding();
        if (type.isAssignableFrom(Polygon.class) || type.isAssignableFrom(MultiPolygon.class)) {
            return createPolygonStyle();
        } else if (type.isAssignableFrom(LineString.class) || type.isAssignableFrom(MultiLineString.class)) {
            return createLineStyle();
        } else {
            return createPointStyle();
        }
    }

    private static Style createFromSLD(File sld) throws IOException, MalformedURLException {
        SLDParser stylereader;
        stylereader = new SLDParser(styleFactory, sld.toURL());
        Style[] style = stylereader.readXML();
        return style[0];
    }
    
    public static Style createFromSLDString(String sldtext) {
        SLDParser parser = new SLDParser(styleFactory, new StringReader(sldtext));
        return parser.readXML()[0];
    }
    
    public static Style createFromFile(File sldFile) throws FileNotFoundException {
        SLDParser parser = new SLDParser(styleFactory, sldFile);
        return parser.readXML()[0];
    }

    private static String getRandomColorHex() {
        Random r = new Random();
        Color randomColor = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), 0);
        return "#" + Integer.toHexString(randomColor.getRGB());
    }
    
    public static void hidePolygonStrokes(Style style) {
        for( FeatureTypeStyle s : style.getFeatureTypeStyles() ) {
            for( Object ruleObject : s.getRules()) {
                Rule r = (Rule)ruleObject;
                for(Object symbolizer : r.getSymbolizers()) {
                    try {
                        
                        PolygonSymbolizer sym = (PolygonSymbolizer)symbolizer;
                        Stroke stroke = sym.getStroke();
                        stroke.setOpacity(ConstantExpression.ZERO);
                        sym.setStroke(stroke);
                    }
                    catch(ClassCastException e)
                    {
                        
                    } 
                }
            }
        }
    }
 

    private static Style createPointStyle() {
        Style style;
        PointSymbolizer symbolizer = styleFactory.createPointSymbolizer();
        symbolizer.getGraphic().setSize(filterFactory.literal(4));
        Rule rule = styleFactory.createRule();
        rule.setSymbolizers(new Symbolizer[]{symbolizer});
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
        fts.setRules(new Rule[]{rule});
        style = styleFactory.createStyle();
        style.addFeatureTypeStyle(fts);
        return style;
    }

    private static Style createLineStyle() {
        Style style;
        LineSymbolizer symbolizer = styleFactory.createLineSymbolizer();
        SLD.setLineColour(symbolizer, Color.BLUE);
        symbolizer.getStroke().setWidth(filterFactory.literal(1));
        symbolizer.getStroke().setColor(filterFactory.literal(getRandomColorHex()));

        Rule rule = styleFactory.createRule();
        rule.setSymbolizers(new Symbolizer[]{symbolizer});
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
        fts.setRules(new Rule[]{rule});
        style = styleFactory.createStyle();
        style.addFeatureTypeStyle(fts);
        return style;
    }

    private static Style createPolygonStyle() {
        Style style;
        PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer();

        Fill fill = styleFactory.createFill(
                filterFactory.literal(getRandomColorHex()),
                filterFactory.literal(1.0));

        symbolizer.setFill(fill);
        Rule rule = styleFactory.createRule();
        rule.setSymbolizers(new Symbolizer[]{symbolizer});
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle();
        fts.setRules(new Rule[]{rule});
        style = styleFactory.createStyle();
        style.addFeatureTypeStyle(fts);
        return style;
    }

    /** Figure out the URL for the "sld" file */
    public static File toSLDFile(File file) {
        String filename = file.getAbsolutePath();
        if (filename.endsWith(".shp") || filename.endsWith(".dbf") || filename.endsWith(".shx")) {
            filename = filename.substring(0, filename.length() - 4);
            filename += ".sld";
        } else if (filename.endsWith(".SLD") || filename.endsWith(".SLD") || filename.endsWith(".SLD")) {
            filename = filename.substring(0, filename.length() - 4);
            filename += ".SLD";
        }
        return new File(filename);
    }
}
