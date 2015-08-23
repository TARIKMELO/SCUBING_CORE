/*
 * $Id: ColorBlend.java 35 2008-10-22 14:36:37Z iovergard $
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

import java.awt.Color;

/**
 * Description of the Class
 *
 * @author    en
 */
public class ColorBlend {

    /**
     * Description of the Field
     */
    protected float hueB;
    /**
     * Description of the Field
     */
    protected float saturationB;
    /**
     * Description of the Field
     */
    protected float brillianceB;
    /**
     * Description of the Field
     */
    protected float alphaB;
    /**
     * Description of the Field
     */
    protected double hueF;
    /**
     * Description of the Field
     */
    protected double saturationF;
    /**
     * Description of the Field
     */
    protected double brillianceF;
    /**
     * Description of the Field
     */
    protected double alphaF;
    /**
     * Description of the Field
     */
    protected double minimum;
    
    /**
     * 
     * @param src The original color
     * @param dest The color to mix with
     * @param percentage 0.0 = pure source color, 1.0 = pure destination color
     * @return The mixed color
     */
    public static Color mixColors(Color src, Color dest, float percentage) {
        float r = src.getRed() + (dest.getRed() - src.getRed()) * percentage ;
        float g = src.getGreen() + (dest.getGreen() - src.getGreen()) * percentage ;
        float b = src.getBlue() + (dest.getBlue() - src.getBlue()) * percentage ;
        float a = src.getAlpha() + (dest.getAlpha() - src.getAlpha()) * percentage ;
        return new Color(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
    }

    /**
     * Constructor for the ColorBlend object
     *
     * @param hsbB      Description of Parameter
     * @param hsbE      Description of Parameter
     * @param increase  Description of Parameter
     * @param alphaB    Description of Parameter
     * @param alphaE    Description of Parameter
     * @param minimum   Description of Parameter
     * @param maximum   Description of Parameter
     */
    public ColorBlend(float[] hsbB, float[] hsbE, boolean increase, float alphaB, float alphaE,
            double minimum, double maximum) {
        hueB = hsbB[0];
        float hueE = hsbE[0];
        if ((hueE <= hueB) && increase) {
            hueE += 1;
        } else if ((hueB <= hueE) && (!increase)) {
            hueE -= 1;
        }

        saturationB = hsbB[1];
        brillianceB = hsbB[2];
        this.alphaB = alphaB;

        this.minimum = minimum;
        double range = maximum - minimum;

        hueF = (range == 0.0) ? 0.0 : (hueE - hueB) / range;
        saturationF = (range == 0.0) ? 0.0 : (hsbE[1] - saturationB) /
                range;
        brillianceF = (range == 0.0) ? 0.0 : (hsbE[2] - brillianceB) /
                range;
        alphaF = (range == 0.0) ? 0.0 : (alphaE - alphaB) / range;
    }

    /**
     * Constructor for the ColorBlend object
     *
     * @param hueB         Description of Parameter
     * @param hueE         Description of Parameter
     * @param increase     Description of Parameter
     * @param saturationB  Description of Parameter
     * @param saturationE  Description of Parameter
     * @param brillianceB  Description of Parameter
     * @param brillianceE  Description of Parameter
     * @param alphaB       Description of Parameter
     * @param alphaE       Description of Parameter
     * @param minimum      Description of Parameter
     * @param maximum      Description of Parameter
     */
    public ColorBlend(float hueB, float hueE, boolean increase,
            float saturationB, float saturationE,
            float brillianceB, float brillianceE,
            float alphaB, float alphaE,
            double minimum, double maximum) {
        this(new float[]{hueB, saturationB, brillianceB},
                new float[]{hueE, saturationE, brillianceE},
                increase, alphaB, alphaE,
                minimum, maximum);
    }

    /**
     * Constructor for the ColorBlend object
     *
     * @param colorB    Description of Parameter
     * @param colorE    Description of Parameter
     * @param increase  Description of Parameter
     * @param alphaB    Description of Parameter
     * @param alphaE    Description of Parameter
     * @param minimum   Description of Parameter
     * @param maximum   Description of Parameter
     */
    public ColorBlend(Color colorB, Color colorE, boolean increase, float alphaB, float alphaE,
            double minimum, double maximum) {
        this(getHSB(colorB), getHSB(colorE), increase, alphaB, alphaE,
                minimum, maximum);
    }

    /**
     * Constructor for the ColorBlend object
     *
     * @param colorB    Description of Parameter
     * @param colorE    Description of Parameter
     * @param increase  Description of Parameter
     * @param minimum   Description of Parameter
     * @param maximum   Description of Parameter
     */
    public ColorBlend(Color colorB, Color colorE, boolean increase,
            double minimum, double maximum) {
        this(colorB, colorE, increase, 1.0f, 1.0f, minimum, maximum);
    }


    /*
     *
    ------------------------------------------------------------------------
    -
     * Access methods (public)
     *
     */
    /**
     * Gets the ARGB attribute of the ColorBlend object
     *
     * @param value  Description of Parameter
     * @return       The ARGB value
     */
    public final int getARGB(double value) {
        value -= minimum;
        return (Math.round(getProportionalValue(value, alphaB, alphaF) * 255) << 24) | Color.HSBtoRGB(getProportionalValue(value, hueB, hueF),
                getProportionalValue(value, saturationB, saturationF),
                getProportionalValue(value, brillianceB, brillianceF));
    }

    /**
     * Gets the HSB attribute of the ColorBlend class
     *
     * @param color  Description of Parameter
     * @return       The HSB value
     */
    private static float[] getHSB(Color color) {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }


    /*
     *
    ------------------------------------------------------------------------
    -
     * Access methods (private)
     *
     */
    /**
     * Gets the ProportinalValue attribute of the ColorBlend class
     *
     * @param offset  Description of Parameter
     * @param start   Description of Parameter
     * @param factor  Description of Parameter
     * @return        The ProportinalValue value
     */
    private final static float getProportionalValue(double offset, double start, double factor) {
        return (float) ((start + offset * factor) /*
                 * % 1.0
                 */);
    }

    /*
     *
    ------------------------------------------------------------------------
    -
     * End of class definition
     *
     */
}
