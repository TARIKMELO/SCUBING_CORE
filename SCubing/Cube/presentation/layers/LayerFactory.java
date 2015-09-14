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
package presentation.layers;

import gov.nasa.worldwind.WorldWindow;
import gw.layers.PointLayer;
import gw.layers.PointLayer.Spot;
import gw.util.StyleUtils;

import java.io.IOException;
import java.net.MalformedURLException;

import org.geotools.data.FeatureSource;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import presentation.layout.ApplicationTemplate;

/** Factory for SimpleFeature and GridCoverage layer creation from different sources.
 *
 * @author Olaf David
 * @author Ian Overgard
 */
public class LayerFactory {

	private LayerFactory() {
	}


	public static SimpleFeatureLayer fromFeatureSource(FeatureSource<SimpleFeatureType, SimpleFeature> featureSource, ApplicationTemplate.AppFrame window) throws IOException, MalformedURLException, FactoryException, TransformException {
		Style s;
//		try
//		{
//			s = StyleUtils.createStyle(featureSource.getSchema().getGeometryDescriptor().getType().getBinding(), featureSource.getSchema());
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			s = StyleUtils.createPolygonStyle();
//		}
		//TODO: tirar esse teste
		SimpleFeatureLayer simpleFeatureLayer = new SimpleFeatureLayer("Teste", featureSource, null, window);
		simpleFeatureLayer.setPickEnabled(false);
		return simpleFeatureLayer;
	}







	public static PointLayer spotLayer(Spot[] points, WorldWindow window, String iconFilename) {
		return new PointLayer(points, window, iconFilename);
	}
}
