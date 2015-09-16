package bll.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.geotools.swing.data.JFileDataStoreChooser;

import com.thoughtworks.xstream.XStream;

import dal.drivers.CubeColumn;

public class Util {
	public static File getNewFile(String fileExtension) {
		JFileDataStoreChooser chooser = new JFileDataStoreChooser(fileExtension);
		chooser.setDialogTitle("Save...");
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
			System.exit(0);
		}

		File newFile = chooser.getSelectedFile();
		//Colocando a extensão no arquivo caso o usuãrio não coloque
		if (!newFile.getName().toString().toUpperCase().endsWith("."+fileExtension.toUpperCase()))
		{
			return new File( newFile.getPath().concat("."+fileExtension)) ;
		}
		return newFile;
	}


	public static File getFile(String fileExtension) {
		JFileDataStoreChooser chooser = new JFileDataStoreChooser(fileExtension);
		chooser.setDialogTitle("Save...");
		int returnVal = chooser.showOpenDialog(null);
		chooser.setFocusable(true);
		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {

			System.exit(0);
		}
		File newFile = chooser.getSelectedFile();
		//Colocando a extensão no arquivo caso o usuãrio não coloque
		if (!newFile.getName().toString().toUpperCase().endsWith("."+fileExtension.toUpperCase()))
		{
			return new File( newFile.getPath().concat("."+fileExtension)) ;
		}
		return newFile;
	}

	public static HashMap<String, CubeColumn> deepCopy(HashMap<String, CubeColumn> src)
	{
		HashMap<String, CubeColumn> clone = new HashMap<String, CubeColumn>();;
		for (Entry<String, CubeColumn> entry : src.entrySet()) {
			clone.put(entry.getKey(), new CubeColumn(entry.getValue().getAggFunction(),entry.getValue().isMeasure(),entry.getValue().getIndex(),entry.getValue().getColumnName()));
		}
		return clone;
	} 


	static ConfigBean config;

	public static ConfigBean getConfig() {

		try {
			XStream xstream = new XStream();
			File xmlFile = new File("CubeConfig.xml");
			config = (ConfigBean)xstream.fromXML(xmlFile);
		} catch (Exception e) {
			ConfigBean defaultConfig = new ConfigBean();
			defaultConfig.setNumThreads("4");
			defaultConfig.setCircleRadius("5");
			return defaultConfig;
		}
		return config;
	}


	public static void saveConfig()
	{
		try {
			XStream xstream = new XStream();
			FileOutputStream fs = new FileOutputStream("CubeConfig.xml");
			xstream.toXML(config, fs);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}


	public static void saveCubeColumnsInXml(File file,HashMap<String, CubeColumn> cubeColumns)
	{
		try {
			XStream xstream = new XStream();
			FileOutputStream fs = new FileOutputStream(file);
			xstream.toXML(cubeColumns, fs);
			fs.close();
			JOptionPane.showMessageDialog(null, "Arquivo Exportado!");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}



	public static HashMap<String, CubeColumn> getCubeColumnsFromXml(File file)
	{
		HashMap<String, CubeColumn> cubeColumns = null;
		try {
			XStream xstream = new XStream();
			File xmlFile = new File("CubeConfig.xml");
			cubeColumns = (HashMap<String, CubeColumn>)xstream.fromXML(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cubeColumns;
	}


	public static HashMap<String, CubeColumn> loadCubeColumnsInXml(File file)
	{
		HashMap<String, CubeColumn> cubeColumns = null;
		try {
			XStream xstream = new XStream();

			cubeColumns = (HashMap<String, CubeColumn>)xstream.fromXML(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cubeColumns;
	}
}
