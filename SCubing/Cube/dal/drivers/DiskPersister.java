package dal.drivers;

import java.util.ArrayList;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.vividsolutions.jts.geom.Geometry;



class DiskPersister {

	ArrayList<Geometry> geometries = new ArrayList<Geometry>();
	
	
	private void init()  
	{ 

	
		DB db =  DBMaker.fileDB("file.db").fileMmapEnable().make();
		
	}
	
	
	

} 
