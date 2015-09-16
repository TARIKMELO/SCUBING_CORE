// docs start source
/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This file is hereby placed into the Public Domain. This means anyone is
 *    free to do whatever they wish with this file. Use it well and enjoy!
 */

package presentation.layout;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import presentation.layers.SimpleFeatureLayer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * The Query Lab is an excuse to try out Filters and Expressions on your own data with a table to
 * show the results.
 * <p>
 * Remember when programming that you have other options then the CQL parser, you can directly make
 * a Filter using CommonFactoryFinder.getFilterFactory2().
 */
public class AttributeTable extends JPanel {
	//private DataStore dataStore;
	//private FileDataStore dataStore;
	SimpleFeatureSource source; 
	//private JComboBox featureTypeCBox;
	private JTable table;
	private JTextField text;


	ListSelectionListener rowListener;
	// docs end main

	// docs start constructor
	public AttributeTable() throws Exception {
		//TODO:
		//this.dataStore = FileDataStoreFinder.getDataStore(MapFrame.getInstance().getFileSource());
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//GridBagLayout gridLayout = new GridBagLayout();

		//JPanel panelMenu = new JPanel(new BorderLayout());

		//super.setLayout(new GridLayout(2,1));;
		super.setLayout(new GridBagLayout());
		text = new JTextField(80);
		text.setText("include"); // include selects everything!
		//super.add(text, BorderLayout.NORTH);

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new DefaultTableModel(0, 5));
		table.setPreferredScrollableViewportSize(new Dimension(500, 200));

		rowListener = new RowListener();
		table.getSelectionModel().addListSelectionListener(rowListener);




		JMenuBar menubar = new JMenuBar();
		//setJMenuBar(menubar);

		/*featureTypeCBox = new JComboBox();
		menubar.add(featureTypeCBox);*/
		menubar.add(text);

		JMenu dataMenu = new JMenu("Data");
		menubar.add(dataMenu);
		//menubar.setMaximumSize(new Dimension(20,40));
		//pack();
		GridBagConstraints c = new GridBagConstraints();
		JScrollPane scrollPane = new JScrollPane(table);
		c.insets = new Insets(5,5,0,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		super.add(menubar,c);
		//c =  new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10,0,0,0);
		c.weighty = 1.0;
		super.add(scrollPane,c);
		//super.add(scrollPane, BorderLayout.CENTER);


		// docs start data menu
		dataMenu.add(new SafeAction("Get features") {
			public void action(ActionEvent e) throws Throwable {
				filterFeatures();

			}
		});
		dataMenu.add(new SafeAction("Count") {
			public void action(ActionEvent e) throws Throwable {
				countFeatures();
			}
		});
		dataMenu.add(new SafeAction("Geometry") {
			public void action(ActionEvent e) throws Throwable {
				//TODO:
				//queryFeatures();
			}
		});
		// docs end data menu
		dataMenu.add(new SafeAction("Center") {
			public void action(ActionEvent e) throws Throwable {
				centerFeatures();
			}
		});


		//Preenchendo a tabela de atributos
		//super.add(menubar);
		updateUI();


	}

	public void setSource(SimpleFeatureSource source)
	{
		this.source = source;

	}
	public void filterFeatures() throws Exception {
		//String typeName = (String) featureTypeCBox.getSelectedItem();
		//source = dataStore.getFeatureSource(typeName);
		//source12 = MapFrame.getInstance().getSelectedLayerFeatureSource();

		Filter filter = CQL.toFilter(text.getText());
		SimpleFeatureCollection features = source.getFeatures(filter);
		FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
		table.setModel(model);
	}
	// docs end filterFeatures

	// docs start countFeatures

	private void countFeatures() throws Exception {
		//String typeName = (String) featureTypeCBox.getSelectedItem();
		//SimpleFeatureSource sourceFeature  = dataStore.getFeatureSource(typeName);

		Filter filter = CQL.toFilter(text.getText());
		SimpleFeatureCollection features = source.getFeatures(filter);

		int count = features.size();
		JOptionPane.showMessageDialog(text, "Number of selected features:" + count);
	}
	// docs end countFeatures

	// docs start queryFeatures
	public void queryFeatures(SimpleFeature feature) throws Exception {


		Set<FeatureId> set = new  HashSet<FeatureId>();

		/*for (Integer i : table.getSelectedRows()) {
			//Tirar o index 0
		 */			set.add(ff.featureId( feature.getID() ));
		 //	}
		 Filter filter = ff.id(set);

		 //String typeName = (String) featureTypeCBox.getSelectedItem();
		 //SimpleFeatureSource source = dataStore.getFeatureSource(typeName);

		 FeatureType schema = source.getSchema();
		 String name = schema.getGeometryDescriptor().getLocalName();

		 // Filter filter = CQL.toFilter(text.getText());

		 Query query = new Query(source.getSchema().getTypeName(), filter, new String[] { name });

		 SimpleFeatureCollection features = source.getFeatures(query);

		 FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
		 table.setModel(model);
	}
	// docs end queryFeatures

	// docs start centerFeatures
	private void centerFeatures() throws Exception {
		//String typeName = (String) featureTypeCBox.getSelectedItem();
		//SimpleFeatureSource source = dataStore.getFeatureSource(typeName);

		Filter filter = CQL.toFilter(text.getText());

		//FeatureType schema = source.getSchema();
		//String name = schema.getGeometryDescriptor().getLocalName();
		//Query query = new Query(typeName, filter, new String[] { name });

		SimpleFeatureCollection features = source.getFeatures(filter);

		double totalX = 0.0;
		double totalY = 0.0;
		long count = 0;
		SimpleFeatureIterator iterator = features.features();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geom = (Geometry) feature.getDefaultGeometry();
				Point centroid = geom.getCentroid();
				totalX += centroid.getX();
				totalY += centroid.getY();
				count++;
			}
		} finally {
			iterator.close(); // IMPORTANT
		}
		double averageX = totalX / (double) count;
		double averageY = totalY / (double) count;
		Coordinate center = new Coordinate(averageX, averageY);

		JOptionPane.showMessageDialog(text, "Center of selected features:" + center);
	}
	// docs end centerFeatures



	/*
	 * Factories that we will use to create style and filter objects
	 */

	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);



	// docs start select features
	/**
	 * This method is called by our feature selection tool when
	 * the user has clicked on the map.
	 *
	 * @param pos map (world) coordinates of the mouse cursor
	 */
	void selectFeatures() {
		
		Set<String> set = new  HashSet<String>();

		for (Integer i : table.getSelectedRows()) {
			//Tirar o index 0
			set.add(table.getValueAt(i, 0).toString());
		}
		
		
		SimpleFeatureLayer layer = (SimpleFeatureLayer) MapFrame.getInstance().getSelectedLayer();
		layer.displaySelectedFeatures(set);
		MapFrame.getInstance().getAppFrame().getWwd().redraw();
		

	}
	// docs end select features

	// docs start display selected
	/**
	 * Sets the display to paint selected features yellow and
	 * unselected features in the default style.
	 *
	 * @param IDs identifiers of currently selected features
	 */
	//TODO: Refazer este mãtodo


	public void selectTableRow(String id, boolean sel) {
		if(table.getRowCount()>0)
		{
			ListSelectionModel selectionModel =  table.getSelectionModel();
			selectionModel.removeListSelectionListener(rowListener);
			for (int i=0; i<table.getRowCount(); i++) {
				//TODO: Fazer pelo nome //Tirar o index 0

				if (table.getValueAt(i, 0).toString().equals(id))
				{
					if(sel)
					{
						selectionModel.addSelectionInterval(i, i);
					}
					else
					{
						selectionModel.removeSelectionInterval(i, i);
					}
					selectionModel.addListSelectionListener(rowListener);
					return;
				}
			}
			
		}
	}

	


	private class RowListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {

			if (event.getValueIsAdjusting()) {
				return;
			}			
			selectFeatures();
		}
	}
}
// docs end source

