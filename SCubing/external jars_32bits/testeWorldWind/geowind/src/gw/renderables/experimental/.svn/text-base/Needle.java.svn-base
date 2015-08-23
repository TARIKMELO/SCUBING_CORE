/*
 * $Id: LinePath.java 26 2008-09-11 06:28:49Z iovergard $
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

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Ian Overgard
 */
public class Needle implements Renderable {

    private Position position;
    private Color color;

    int lineWidth = 2;
    private double length = 4000.0f;

    public Needle(Position position, Color color) {
        this.position = position;
        this.color = color;
    }

    public void setPosition(Position p) {
        this.position = p;
    }

    public Position getPosition() {
        return this.position;
    }

    private void drawPrimitives(DrawContext dc) {
        javax.media.opengl.GL gl = dc.getGL();
        GLU glu = dc.getGLU();
        Globe myglobe = dc.getGlobe();

        gl.glPushAttrib(gl.GL_CURRENT_BIT | gl.GL_ENABLE_BIT);
        //gl.glDisable(gl.GL_CULL_FACE);
        gl.glDisable(gl.GL_TEXTURE_2D);
        gl.glEnable(gl.GL_BLEND);
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);

        gl.glLineWidth(lineWidth);
        gl.glBegin(gl.GL_LINE_STRIP);
        gl.glColor4d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0 );
        double elevation = myglobe.getElevation(position.getLatitude(), position.getLongitude() ) ;
        Position upvec = Position.fromDegrees(0, 0, length);
        Vec4 pointA = myglobe.computePointFromPosition(position);
        Vec4 pointB = myglobe.computePointFromPosition(position.add(upvec));

        
        gl.glVertex3d(pointA.x, pointA.y, pointA.z);

        gl.glColor4d(0, 0, 0, 1);
        gl.glVertex3d(pointB.x, pointB.y, pointB.z);
        gl.glLineWidth(1);
        gl.glEnd();

        gl.glPopAttrib();
    }

    public void render(DrawContext dc) {

        drawPrimitives(dc);

    }

    /**
     * @return the length
     */
    public double getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

}