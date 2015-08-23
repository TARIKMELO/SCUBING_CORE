/*
 * $Id: ComplexPolygonTesselationCallback.java 9 2008-07-21 19:46:43Z iovergard $
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
package gw.renderables;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellatorCallback;

/**
 *
 * @author Ian Overgard
 */
public class ComplexPolygonTesselationCallback implements GLUtessellatorCallback {

    private GL gl;
    private GLU glu;

    public ComplexPolygonTesselationCallback(GL gl, GLU glu) {
        this.gl = gl;
        this.glu = glu;
    }

    public void begin(int type) {
        gl.glBegin(type);
    }

    public void end() {
        gl.glEnd();
    }

    public void vertex(Object vertexData) {
        double[] pointer;
        if (vertexData instanceof double[]) {
            pointer = (double[]) vertexData;
            if (pointer.length == 7) {
                gl.glColor4dv(pointer, 3);
            }
            gl.glVertex3dv(pointer, 0);
        }

    }

    public void vertexData(Object vertexData, Object polygonData) {
    }

    /*
     * combineCallback is used to create a new vertex when edges intersect.
     * coordinate location is trivial to calculate, but weight[4] may be used to
     * average color, normal, or texture coordinate data. In this program, color
     * is weighted.
     */
    public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
        double[] vertex = new double[7];
        int i;

        vertex[0] = coords[0];
        vertex[1] = coords[1];
        vertex[2] = coords[2];
        for (i = 3; i < 6; i++) {
            vertex[i] = weight[0] //
                    * ((double[]) data[0])[i] + weight[1] * ((double[]) data[1])[i] + weight[2] * ((double[]) data[2])[i] + weight[3] * ((double[]) data[3])[i];
        }
        outData[0] = vertex;
    }

    public void combineData(double[] coords, Object[] data, float[] weight, Object[] outData, Object polygonData) {
    }

    public void error(int errnum) {
        String estring;

        estring = glu.gluErrorString(errnum);
        System.err.println("Tessellation Error: " + estring);
        System.exit(0);
    }

    public void beginData(int type, Object polygonData) {
    }

    public void endData(Object polygonData) {
    }

    public void edgeFlag(boolean boundaryEdge) {
    }

    public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
    }

    public void errorData(int errnum, Object polygonData) {
    }
}
