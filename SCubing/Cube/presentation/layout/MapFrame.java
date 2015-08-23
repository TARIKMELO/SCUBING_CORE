package presentation.layout;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.MapContent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import presentation.layers.SimpleFeatureLayer;

public class MapFrame {

	private volatile static MapFrame INSTANCE ;
	private SimpleFeatureLayer layer; 
	File fileSource;


	private AppPanel wwpanel;
	private ApplicationTemplate.AppFrame appFrame;

	public ApplicationTemplate.AppFrame getAppFrame() {
		return appFrame;
	}


	public void setAppFrame(ApplicationTemplate.AppFrame appFrame) {
		this.appFrame = appFrame;
	}


	public SimpleFeatureSource getSelectedLayerFeatureSource() {
		if (layer!=null)
		{
			return (SimpleFeatureSource) layer.getFeatureSource();
		}
		else
		{
			return null;
		}
	}


	public SimpleFeatureLayer getSelectedLayer() {
		return layer;
	}

	public void setSelectedLayer(SimpleFeatureLayer layer) {
		this.layer = layer;
	}



	public File getFileSource() {
		return fileSource;
	}

	public void setFileSource(File fileSource) {
		this.fileSource = fileSource;
	}

	private MapFrame()
	{
		//map = new MapContent();
	}

	public static  MapFrame getInstance()
	{
		if (INSTANCE==null)
		{
			synchronized (MapFrame.class)
			{
				if (INSTANCE==null)
				{
					INSTANCE = new MapFrame();
				}
			}
		}
		return INSTANCE;
	}



	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}


	public MapContent getMap() {
		return null;
	}


	/*public void setMap(MapContent map) {
		//this.map = map;
	}*/


	public void createLayer(FeatureSource<SimpleFeatureType, SimpleFeature> source) throws IOException
	{
		if (source.getFeatures()!=null & source.getFeatures().size()>0)
		{
			Thread t = new BuildLayerWorkerThread(source, getAppFrame());
			t.start();
			getAppFrame().getWwd().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		}
	}



}