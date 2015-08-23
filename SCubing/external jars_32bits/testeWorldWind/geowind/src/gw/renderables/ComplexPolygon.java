/*
 * $Id: ComplexPolygon.java 20 2008-07-22 17:57:47Z od $
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

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

/**
 * A tesselated polygon renderable.
 *
 *
 * @author Ian Overgard
 */
public class ComplexPolygon implements Renderable {

    private Position[] vertices;
    private Color color;
    int displayList = -1;

    public ComplexPolygon(Position[] vertices, Color color) {
        this.vertices = vertices;
        this.color = color;
    }

    private double[] makeVertex(Vec4 vec, Color c) {
        return new double[]{vec.x, vec.y, vec.z,
                    c.getRed() / 255.0,
                    c.getGreen() / 255.0,
                    c.getBlue() / 255.0,
                    c.getAlpha() / 255.0
                };
    }

    @Override
    public void render(DrawContext dc) {

        javax.media.opengl.GL gl = dc.getGL();
        GLU glu = dc.getGLU();
        Globe myglobe = dc.getGlobe();


        GLUtessellator tess = glu.gluNewTess();
        ComplexPolygonTesselationCallback cb = new ComplexPolygonTesselationCallback(gl, glu);
        glu.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, cb);// glVertex3dv);
        glu.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, cb);// beginCallback);
        glu.gluTessCallback(tess, GLU.GLU_TESS_END, cb);// endCallback);
        glu.gluTessCallback(tess, GLU.GLU_TESS_ERROR, cb);// errorCallback);

        //gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_TRANSFORM_BIT);
        double polygon[][] = new double[vertices.length][7];
        for (int i = 0; i < vertices.length; i++) {
            double elevation = myglobe.getElevation(vertices[i].getLatitude(), vertices[i].getLongitude());
            Position upvec = Position.fromDegrees(0, 0, elevation);
            polygon[i] = makeVertex(myglobe.computePointFromPosition(vertices[i].add(upvec)), color);
        }



        if (displayList == -1) {
            displayList = gl.glGenLists(1);
            gl.glNewList(displayList, gl.GL_COMPILE);

            gl.glPushAttrib(gl.GL_CURRENT_BIT | gl.GL_ENABLE_BIT);
            gl.glDisable(gl.GL_CULL_FACE);
            gl.glDisable(gl.GL_TEXTURE_2D);
            gl.glEnable(gl.GL_BLEND);
            gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
            //

            glu.gluTessBeginPolygon(tess, null);
            glu.gluTessBeginContour(tess);
            for (int i = 0; i < polygon.length; i++) {
                glu.gluTessVertex(tess, polygon[i], 0, polygon[i]);
            }
            glu.gluTessEndContour(tess);
            glu.gluTessEndPolygon(tess);
            gl.glPopAttrib();
            gl.glEndList();
        }
        //gl.glPolygonMode( gl.GL_FRONT_AND_BACK, gl.GL_LINE);
        gl.glCallList(displayList);
    }
}
