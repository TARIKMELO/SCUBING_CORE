package bll.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.thoughtworks.xstream.XStream;

import dal.drivers.CubeColumn;

public class Util {

	static Logger logger = Logger.getLogger("scubing");


	public static void beginLog()
	{

		logger.info("------------------------------Iniciou o log da execução------------------------------");
		final int numConsumidores =Integer.parseInt(Util.getConfig().getNumThreads());
		logger.info("Número de consumidores: "+ numConsumidores);
		logger.info("Iniciou o log da execução");
	}


	public static Logger getLogger() {
		return logger;
	}


	public static void setLogger(Logger logger) {
		Util.logger = logger;
	}


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
			defaultConfig.setNumThreads("1");
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


	public static HashMap<String, CubeColumn> loadCubeColumnsFromXml()
	{
		HashMap<String, CubeColumn> cubeColumns = null;
		try {
			//Pergunta onde está o arquivo
			//File file = Util.getFile("xml");
			
			File xmlFile = new File("CubeMDXConfig.xml");
			XStream xstream = new XStream();
			cubeColumns = (HashMap<String, CubeColumn>)xstream.fromXML(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cubeColumns;
	}



	public static DataStore connectPostGis() throws IOException
	{
		Map<String, Object> connectionParameters = new HashMap<String, Object>();
		ConfigBean configBean = getConfig();
		connectionParameters.put("dbtype", configBean.getPostgisDbtype());
		connectionParameters.put("host", configBean.getPostgisHost());
		connectionParameters.put("port", configBean.getPostgisPort());
		connectionParameters.put("schema", configBean.getPostgisSchema());
		connectionParameters.put("user", configBean.getPostgisUser());
		connectionParameters.put("passwd", configBean.getPostgisPasswd());
		connectionParameters.put("database", configBean.getPostgisDatabase());

		DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);
		if (dataStore == null) {
			System.out.println("Could not connect - check parameters");
		}

		return 	dataStore;
	}
	
	
	


	public static SimpleFeatureSource connectToSourcePostGis(String featureSourceName) throws Exception {

		SimpleFeatureSource featureSource = null;


		DataStore dataStore = connectPostGis();


		if (dataStore != null) {
			
			//Setando a conexão ativa do Postgre para ser usada na exportação dos dados
			//MapFrame.getInstance().setDataStore(dataStore);

			featureSource = dataStore.getFeatureSource(featureSourceName);




		}
		return featureSource;
	}
	
	
	
	public void insertToPostGis(Transaction transaction, SimpleFeatureStore featureStore, FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceCube ) throws IOException{
		
			featureStore.setTransaction(transaction);
			try { 
				featureStore.addFeatures(featureSourceCube.getFeatures());
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
			//System.exit(0); // success!

	}
	
	
	
	
	//Insere todo o FeatureSource
	public void insertToPostGis(FeatureSource<SimpleFeatureType, SimpleFeature> featureSourceCube ) throws IOException{
		DataStore dataStore = Util.connectPostGis();
		dataStore.createSchema(featureSourceCube.getSchema());
		Transaction transaction = new DefaultTransaction("create");
		SimpleFeatureSource source = dataStore.getFeatureSource(featureSourceCube.getSchema().getTypeName());
		if (source instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) source;
			featureStore.setTransaction(transaction);
			try { 
				featureStore.addFeatures(featureSourceCube.getFeatures());
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
			//System.exit(0); // success!
		} else {
			System.out.println("Table does not support read/write access");
			System.exit(1);
		}

	}

	
	
	public static void insertBlanckToPostGis(SimpleFeatureType featureSourceCubeType ) throws IOException{
		DataStore dataStore = Util.connectPostGis();
		dataStore.createSchema(featureSourceCubeType);
		Transaction transaction = new DefaultTransaction("create");
		SimpleFeatureSource source = dataStore.getFeatureSource(featureSourceCubeType.getTypeName());
		if (source instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) source;
			featureStore.setTransaction(transaction);
			try { 
				//featureStore.addFeatures(featureSourceCube.getFeatures());
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
			//System.exit(0); // success!
		} else {
			System.out.println("Table does not support read/write access");
			System.exit(1);
		}

		
		dataStore.dispose();
	}

}
