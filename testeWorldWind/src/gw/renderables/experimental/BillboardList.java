/*
 * $Id: BillboardList.java 20 2008-07-22 17:57:47Z od $
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
package gw.renderables.experimental;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import javax.media.opengl.GL;

/**
 *
 * @author Ian Overgard
 */
public class BillboardList implements Renderable {

    LatLon[] points;
    float size;
    String imagename;

    public BillboardList(LatLon[] points, float size, String imagename) {
        this.points = points;
        this.size = size;
        this.imagename = imagename;
    }

    private void drawBillboard(DrawContext dc) {
        dc.drawUnitQuad();
    }

    @Override
    public void render(DrawContext dc) {
        Globe globe = dc.getGlobe();
        GL gl = dc.getGL();
        gl.glPushMatrix();

        Vec4 eyePoint = dc.getView().getEyePoint();

        // get the current modelview matrix
        float[] modelview = new float[16];
        gl.glGetFloatv(gl.GL_MODELVIEW_MATRIX, modelview, 0);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    modelview[i * 4 + j] = 1.0f;
                } else {
                    modelview[i * 4 + j] = 0.0f;
                }
            }
        }

        // set the modelview matrix
        gl.glLoadMatrixf(modelview, 0);

        //now that rotations are neutralized except the up vector, draw all the 
        //billboards
        for (LatLon ll : this.points) {
            double elevation = globe.getElevationModel().getElevation(ll.getLatitude(), ll.getLongitude());
            Vec4 objPos = globe.computePointFromPosition(ll.getLatitude(), ll.getLongitude(), elevation);
            gl.glPushMatrix();
            //gl.glTranslated(v.x, v.y, v.z);
            //gl.glScalef(size, size, size);
            //drawBillboard(dc);
            gl.glPointSize(5);
            gl.glBegin(gl.GL_POINTS);
            gl.glVertex3d(objPos.x, objPos.y, objPos.z);
            gl.glEnd();
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }
}
