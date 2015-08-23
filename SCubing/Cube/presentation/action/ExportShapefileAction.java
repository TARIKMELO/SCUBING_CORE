package presentation.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.MapPane;

import presentation.layout.MapFrame;
import bll.util.Util;

public class ExportShapefileAction extends AbstractAction{


	public ExportShapefileAction(MapPane mapPane) {

	}

	
	public void actionPerformed(ActionEvent ev) {

		try {
			exportToShapefile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void exportToShapefile() throws Exception {

		/*
		 * Get an output file name and create the new shapefile
		 */
		File newFile =Util.getNewFile("shp");

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		Map<String, Serializable> params = new HashMap<String, Serializable>();

		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.createSchema(MapFrame.getInstance().getSelectedLayerFeatureSource().getSchema());

		/*
		 * You can comment out this line if you are using the createFeatureType method (at end of
		 * class file) rather than DataUtilities.createType
		 */
		newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);

		/*
		 * Write the features to the shapefile
		 */
		Transaction transaction = new DefaultTransaction("create");

		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(MapFrame.getInstance().getSelectedLayerFeatureSource().getFeatures());
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
			//System.exit(0); // success!
		} else {
			System.out.println(typeName + " does not support read/write access");
			System.exit(1);
		}
	}







}