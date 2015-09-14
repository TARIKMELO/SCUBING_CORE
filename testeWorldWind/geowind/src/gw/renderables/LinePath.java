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

package gw.renderables;

import com.sun.opengl.util.BufferUtil;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import java.nio.FloatBuffer;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Ian Overgard
 */
public class LinePath implements Renderable {
    
    private Position[] vertices;
    private Color color;
    
    int displayList = -1;
    int refreshCounter = 0;
    int refreshTime = 120;
    int lineWidth = 1;
    boolean elevationAbsolute=true;

    public LinePath(Position[] vertices, Color color) {
        this.vertices = vertices;
        this.color = color;
    }
    
    public LinePath(Position[] vertices, Color color, int lineWidth, boolean absElevation) {
        this.vertices = vertices;
        this.color = color;
        this.lineWidth = lineWidth;
        this.elevationAbsolute = absElevation;
    }
    
    
    private void drawPrimitives(DrawContext dc) {
        javax.media.opengl.GL gl = dc.getGL();
        GLU glu = dc.getGLU();
        Globe myglobe = dc.getGlobe();
        
        gl.glPushAttrib(gl.GL_CURRENT_BIT | gl.GL_ENABLE_BIT);
        //gl.glDisable(gl.GL_CULL_FACE);
        gl.glDisable(gl.GL_TEXTURE_2D);
        gl.glDisable(gl.GL_DEPTH_TEST);
        gl.glEnable(gl.GL_BLEND);
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);	

        gl.glLineWidth(lineWidth);
        gl.glBegin(gl.GL_LINE_STRIP);
        gl.glColor4d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0 );
        for(int i=0; i<vertices.length; i++) {
            double elevation = vertices[i].getElevation();
            if(!elevationAbsolute)        
                elevation += myglobe.getElevation(vertices[i].getLatitude(), vertices[i].getLongitude() ) ;
            Position upvec = Position.fromDegrees(0,0, 0);
            Vec4 point = myglobe.computePointFromPosition(vertices[i].add(upvec));
            gl.glVertex3d(point.x, point.y, point.z);
        }
        gl.glLineWidth(1);
        gl.glEnd();

        gl.glPopAttrib();
    }

    
    @Override
    public void render(DrawContext dc) {
        boolean regenerate = false;
        javax.media.opengl.GL gl = dc.getGL();
        
        if(displayList == -1  ) {
            displayList = gl.glGenLists(1);
            regenerate = true;
        }
        if(refreshCounter >= refreshTime) {
            regenerate = true;
            refreshCounter = 0;
        }
        if (regenerate) {
            gl.glNewList(displayList, gl.GL_COMPILE);
            drawPrimitives(dc);            
            gl.glEndList();
        }
        
        gl.glCallList(displayList);
        refreshCounter++;
        
        //drawPrimitives(dc);
        //drawPrimitivesFast(dc);
    }
    
}
