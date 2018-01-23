package presentation.layout;

import java.io.File;

import org.geotools.data.DataStore;
import org.geotools.map.MapContent;

public class MapFrame {

	private volatile static MapFrame INSTANCE ;
	
	File fileSource;


	


	private DataStore dataStore;
	
	






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


	public DataStore getDataStore() {
		return dataStore;
	}


	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}



}
