package dal.drivers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map.Entry;

import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.parallel.Resource;

public class DiskDataUtil {

	public static void SaveResourceToDisk(Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource)
	{
		try{
			FileOutputStream fos= new FileOutputStream("D:\\teste\\myfile");
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			
			
			
			
			
			oos.writeObject(resource);
			oos.close();
			fos.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}


	public static void DiskToResource ()
	{
		Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource;
		try
		{
			FileInputStream fis = new FileInputStream("D:\\teste\\myfile");
			ObjectInputStream ois = new ObjectInputStream(fis);
			resource = (Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>>) ois.readObject();
			ois.close();
			fis.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
			return;
		}catch(ClassNotFoundException c){
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
//		for(String tmp: resource){
//			System.out.println(tmp);
//		}
	}
}
