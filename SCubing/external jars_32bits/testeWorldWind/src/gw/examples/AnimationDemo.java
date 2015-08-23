/*
 * $Id: AnimationDemo.java 57 2008-11-05 22:11:50Z iovergard $
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

package gw.examples;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gw.layers.AnimatedSimpleFeatureLayer;
import gw.util.AnimationFile;
import gw.layers.LayerFactory;
import gw.util.WorldWindUtils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 *
 * @author  Ian Overgard
 */
public class AnimationDemo extends javax.swing.JFrame {

    AnimatedSimpleFeatureLayer animatedLayer;
    Timer timer;
    boolean isInitializing = true;
    
    /** Creates new form AnimationDemo */
    public AnimationDemo() throws Exception {
        initComponents();
        
        if (canvas.getModel() == null) {
            canvas.setModel((Model) WorldWind.createConfigurationComponent(
                    AVKey.MODEL_CLASS_NAME));
        }
        
        animatedLayer = LayerFactory.fromFile(new File("sampledata/animation/wb_hrus.shp"), 
                                               new AnimationFile("sampledata/animation/wb_gis.out.nhru"),
                                                   canvas);
        animatedLayer.setAttrMaxColor(Color.BLUE);
        animatedLayer.setPrimaryAttr(animatedLayer.getSchema().indexOf("HRU"));
        animatedLayer.setAnimationColumn(3);
        animatedLayer.sharpenLines();
        animatedLayer.redraw();
        canvas.getModel().getLayers().add(animatedLayer);
        WorldWindUtils.flyTo(animatedLayer.getSector(), canvas);
        animationSlider.setMinimum(0);
        animationSlider.setMaximum(animatedLayer.getFrameCount()-1);
        animationSlider.setValue(0);
        
        
        final AnimatedSimpleFeatureLayer layer = animatedLayer;
        timer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try { 
                   animationSlider.setValue( animationSlider.getValue() + 1 );
                }
                catch(Exception e) {
                }
            }
        });
        isInitializing = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        animationSlider = new javax.swing.JSlider();
        playButton = new javax.swing.JButton();
        flyToButton = new javax.swing.JButton();
        canvas = new gov.nasa.worldwind.awt.WorldWindowGLJPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        animationSlider.setMajorTickSpacing(200);
        animationSlider.setPaintTicks(true);
        animationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                animationSliderChanged(evt);
            }
        });

        playButton.setText(">");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        flyToButton.setText("Focus");
        flyToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flyToButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout canvasLayout = new org.jdesktop.layout.GroupLayout(canvas);
        canvas.setLayout(canvasLayout);
        canvasLayout.setHorizontalGroup(
            canvasLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 445, Short.MAX_VALUE)
        );
        canvasLayout.setVerticalGroup(
            canvasLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 284, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(playButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(animationSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(flyToButton)
                .add(6, 6, 6))
            .add(canvas, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(canvas, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(flyToButton)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(playButton)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, animationSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void animationSliderChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_animationSliderChanged
    if(isInitializing) return;
    try {
        animatedLayer.setFrame(animationSlider.getValue());
        animatedLayer.redraw();
    } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }
    canvas.redrawNow();
}//GEN-LAST:event_animationSliderChanged

private void flyToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flyToButtonActionPerformed
    try {
        WorldWindUtils.flyTo(animatedLayer.getSector(), canvas);
    }
    catch(Exception e){
        e.printStackTrace();
    }
    
}//GEN-LAST:event_flyToButtonActionPerformed

private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
    if(timer.isRunning()) {
        timer.stop();
        playButton.setText(">");
    }
    else {
        timer.start();
        playButton.setText("||");
    }
}//GEN-LAST:event_playButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new AnimationDemo().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider animationSlider;
    private gov.nasa.worldwind.awt.WorldWindowGLJPanel canvas;
    private javax.swing.JButton flyToButton;
    private javax.swing.JButton playButton;
    // End of variables declaration//GEN-END:variables

}
