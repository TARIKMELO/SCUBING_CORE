/*
 * $Id: RasterLayer.java 20 2008-07-22 17:57:47Z od $
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

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 *
 * @author Ian Overgard
 * @author Olaf David
 */
public class RasterLayer extends RenderableLayer {

    Sector sector;

    class RasterFile {

        private int ncols;
        private int nrows;
        private double nodatavalue;
        private double cellsize;
        private double xllcorner;
        private double yllcorner;
        private double[][] data;
        private double min;
        private double max;

        private double readDouble(String name, String line) throws Exception {
            StringTokenizer t = new StringTokenizer(line);
            if (t.countTokens() != 2) {
                throw new Exception("Illegal line: " + line);
            }
            if (!t.nextToken().equalsIgnoreCase(name)) {
                throw new Exception(name);
            }
            return Double.parseDouble(t.nextToken());
        }

        private Color interpolateColors(Color ca, Color cb, double percent) {
            int r = (int) ((cb.getRed() - ca.getRed()) * percent + ca.getRed());
            int g = (int) ((cb.getGreen() - ca.getGreen()) * percent + ca.getGreen());
            int b = (int) ((cb.getBlue() - ca.getBlue()) * percent + ca.getBlue());
            int a = (int) ((cb.getAlpha() - ca.getAlpha()) * percent + ca.getAlpha());
            return new Color(r, g, b, a);
        }

        public RasterFile(String filename) throws Exception {
            BufferedReader r = new BufferedReader(new FileReader(filename));
            ncols = (int) readDouble("ncols", r.readLine());
            nrows = (int) readDouble("nrows", r.readLine());
            xllcorner = readDouble("xllcorner", r.readLine());
            yllcorner = readDouble("yllcorner", r.readLine());
            cellsize = readDouble("cellsize", r.readLine());
            nodatavalue = (int) readDouble("nodata_value", r.readLine());
            data = new double[nrows][ncols];
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
            for (int y = 0; y < nrows; y++) {
                StringTokenizer t = new StringTokenizer(r.readLine());
                for (int x = 0; x < ncols; x++) {
                    data[y][x] = Double.parseDouble(t.nextToken());
                    if (data[y][x] < min && (int) data[y][x] != this.nodatavalue) {
                        min = data[y][x];
                    }
                    if (data[y][x] > max && (int) data[y][x] != this.nodatavalue) {
                        max = data[y][x];
                    }
                }
            }
            r.close();
        }

        public BufferedImage getImage(Color minColor, Color maxColor) {
            BufferedImage image = new BufferedImage(getNrows(), getNcols(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < getNrows(); y++) {
                for (int x = 0; x < getNcols(); x++) {
                    Color c;
                    if (data[y][x] == this.nodatavalue) {
                        c = new Color(0, 0, 0, 0);
                    } else {
                        c = interpolateColors(minColor, maxColor, (data[y][x] - min) / (max - min));
                    }
                    image.setRGB(y, x, c.getRGB());
                }
            }
            return image;
        }

        public int getNcols() {
            return ncols;
        }

        public int getNrows() {
            return nrows;
        }

        public double getNodatavalue() {
            return nodatavalue;
        }

        public double getCellsize() {
            return cellsize;
        }

        public double getXllcorner() {
            return xllcorner;
        }

        public double getYllcorner() {
            return yllcorner;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double[][] getData() {
            return data;
        }
    }
    
    public RasterLayer(String rasterFile) throws Exception {
        //Generator.Grid g = Generator.getRasterData(rasterFile);
        this.setName(rasterFile);
        RasterFile f = new RasterFile(rasterFile);
        sector = Sector.fromDegrees(f.getYllcorner(),
                f.getYllcorner() + f.getCellsize(),
                f.getXllcorner(),
                f.getXllcorner() + f.getCellsize());
        addRenderable(new SurfaceImage(f.getImage(new Color(0, 0, 0, 255), Color.RED), getSector()));
    }

    public Sector getSector() {
        return sector;
    }
}
