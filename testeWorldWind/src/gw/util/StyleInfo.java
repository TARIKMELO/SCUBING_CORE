/*
 * $Id: StyleInfo.java 58 2008-11-05 22:45:54Z iovergard $
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

import org.geotools.styling.AnchorPoint;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.Displacement;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeConstraint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.Halo;
import org.geotools.styling.ImageOutline;
import org.geotools.styling.LinePlacement;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.OverlapBehavior;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.ShadedRelief;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleVisitor;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.styling.UserLayer;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Ian Overgard
 */
public class StyleInfo implements StyleVisitor {
    private Stroke polygonStroke;
    private Fill polygonFill;
    
    private Fill textFill;
    private Halo textHalo;
    private Font[] textFonts;
    private Expression label;
    
    private Graphic pointGraphic;
    
    public void visit(AnchorPoint ap) {
    }


    public void visit(ChannelSelection value) {
    }


    public void visit(ColorMap colorMap) {
    }

    public void visit(ColorMapEntry colorMapEntry) {
    }


    public void visit(ContrastEnhancement value) {
    }


    public void visit(Displacement dis) {
    }

    public void visit(ExternalGraphic exgr) {
    }

    public void visit(FeatureTypeConstraint ftc) {
    }

    public void visit(FeatureTypeStyle fts) {
        for(Rule r : fts.getRules()) {
            r.accept(this);
        }
    }

    public void visit(Fill fill) {
    }

    public void visit(Graphic gr) {
    }

    public void visit(Halo halo) {
    }


    public void visit(ImageOutline value) {
    }


    public void visit(LinePlacement lp) {
    }

    public void visit(LineSymbolizer line) { 
    }

    public void visit(Mark mark) {
    }

    public void visit(NamedLayer layer) {
    }


    public void visit(OverlapBehavior value) {
    }


    public void visit(PointPlacement pp) {
    }

    public void visit(PointSymbolizer ps) {
        if(ps.getGraphic() != null) ps.getGraphic().accept(this);
        pointGraphic = ps.getGraphic();
    }

    public void visit(PolygonSymbolizer poly) {
        this.polygonFill = poly.getFill();
        this.polygonStroke = poly.getStroke();
    }

    public void visit(RasterSymbolizer raster) {
    }

    public void visit(Rule rule) {
        for(Symbolizer s : rule.getSymbolizers()) {
            s.accept(this);
        }
        for(Graphic g : rule.getLegendGraphic()) {
            g.accept(this);
        }
    }

    public void visit(SelectedChannelType value) {
    }

    public void visit(ShadedRelief relief) {
    }
    
    public void visit(Stroke stroke) {
    }
    
    public void visit(Style style) {
        for( FeatureTypeStyle s : style.getFeatureTypeStyles()) {
            s.accept(this);
        }
    }
    
    public void visit(StyledLayerDescriptor sld) {
    }
    
    public void visit(Symbolizer sym) {
    }

    public void visit(TextSymbolizer text) {
        textFill = text.getFill();
        textHalo = text.getHalo();
        textFonts = text.getFonts();
        label = text.getLabel();
    }

    public void visit(UserLayer layer) {
    }

    public Stroke getPolygonStroke() {
        return polygonStroke;
    }

    public Fill getPolygonFill() {
        return polygonFill;
    }

    public Fill getTextFill() {
        return textFill;
    }

    public Halo getTextHalo() {
        return textHalo;
    }
    
    public Font[] getTextFonts() {
        return textFonts;
    }
    
    public Expression getLabel() {
        return label;
    }
    
    public Graphic getPointGraphic() {
        return pointGraphic;
    }


}
