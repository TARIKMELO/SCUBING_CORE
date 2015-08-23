package presentation.layout;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwindx.examples.util.OpenStreetMapShapefileLoader;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;

import java.awt.Cursor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import presentation.layers.LayerFactory;
import presentation.layers.SimpleFeatureLayer;
import presentation.layout.ApplicationTemplate.AppFrame;

public class BuildLayerWorkerThread extends Thread
{
	protected Object source;
	protected AppFrame appFrame;

	public BuildLayerWorkerThread(Object source, AppFrame appFrame)
	{
		this.source = source;
		this.appFrame = appFrame;
	}

	public void run()
	{
		try
		{
			final List<Layer> layers = this.makeShapefileLayers();
			for (int i = 0; i < layers.size(); i++)
			{
				String name = this.makeDisplayName(this.source);
				layers.get(i).setName(i == 0 ? name : name + "-" + Integer.toString(i));
				layers.get(i).setPickEnabled(true);
			}

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					for (Layer layer : layers)
					{
						insertBeforePlacenames(appFrame.getWwd(), layer);
						appFrame.layers.add(layer);
					}

					appFrame.layerPanel.update(appFrame);


				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					appFrame.getWwd().setCursor(Cursor.getDefaultCursor());
				}
			});
		}
	}

	protected List<Layer> makeShapefileLayers() throws MalformedURLException, IOException, FactoryException, TransformException
	{



		if (source instanceof  SimpleFeatureSource)
		{
			SimpleFeatureLayer layer = LayerFactory.fromFeatureSource((FeatureSource<SimpleFeatureType, SimpleFeature>) source,appFrame);
			List<Layer> layers = new ArrayList<Layer>();
			layers.add(layer);
			return layers;
		}

		else if (OpenStreetMapShapefileLoader.isOSMPlacesSource(this.source))
		{
			Layer layer = OpenStreetMapShapefileLoader.makeLayerFromOSMPlacesSource(source);
			List<Layer> layers = new ArrayList<Layer>();
			layers.add(layer);
			return layers;
		}
		else
		{
			ShapefileLoader loader = new ShapefileLoader();
			return loader.createLayersFromSource(this.source);
		}
	}

	protected String makeDisplayName(Object source)
	{
		String name = WWIO.getSourcePath(source);
		if (name != null)
			name = WWIO.getFilename(name);
		else if (source instanceof SimpleFeatureSource)
			name = "Shapefile: "+((SimpleFeatureSource)source).getName().getLocalPart();
		else
			name = "Shapefile";

		return name;
	}


	public static void insertBeforePlacenames(WorldWindow wwd, Layer layer)
	{
		// Insert the layer into the layer list just before the placenames.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l instanceof PlaceNameLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition, layer);
	}

}