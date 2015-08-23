/*
Copyright (C) 2001, 2011 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
 */
package presentation.layout;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.RenderingExceptionListener;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwind.util.StatisticsPanel;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.util.HighlightController;
import gov.nasa.worldwindx.examples.util.ToolTipController;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.wizard.JWizard;

import presentation.action.CubeConfigurationAction;
import presentation.action.ExportKmlAction;

/**
 * Provides a base application framework for simple WorldWind examples. Examine other examples in this package to see
 * how it's used.
 *
 * @version $Id: ApplicationTemplate.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class ApplicationTemplate
{
	public static class AppFrame extends JFrame
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8541109069460872126L;

		private Dimension canvasSize = new Dimension(800, 500);

		protected AppPanel wwjPanel;
		protected LayerPanel layerPanel;
		protected StatisticsPanel statsPanel;
		protected AttributeTable bottomPanel;
		public AppFrame()
		{
			try {
				this.initialize(true, true, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public AppFrame(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel)
		{
			try {
				this.initialize(includeStatusBar, includeLayerPanel, includeStatsPanel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public AttributeTable getBottomPanel()
		{
			return bottomPanel;
		}


		protected void ShowWW(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) throws Exception
		{

			// Create the WorldWindow.
			this.wwjPanel = this.createAppPanel(this.canvasSize, includeStatusBar);
			
			this.wwjPanel.setSize(this.canvasSize);
			//this.wwjPanel.setPreferredSize(canvasSize);
			if (includeLayerPanel)
			{
				this.layerPanel = new LayerPanel(this, null);
				//this.getContentPane().add(this.layerPanel, BorderLayout.WEST);
			}

			if (includeStatsPanel || System.getProperty("gov.nasa.worldwind.showStatistics") != null)
			{
				this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, canvasSize.height));
				//this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
			}


			// Create and install the view controls layer and register a controller for it with the World Window.
			ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
			insertBeforeCompass(getWwd(), viewControlsLayer);
			this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

			// Register a rendering exception listener that's notified when exceptions occur during rendering.
			this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener()
			{
				public void exceptionThrown(Throwable t)
				{
					if (t instanceof WWAbsentRequirementException)
					{
						String message = "Computer does not meet minimum graphics requirements.\n";
						message += "Please install up-to-date graphics driver and try again.\n";
						message += "Reason: " + t.getMessage() + "\n";
						message += "This program will end when you press OK.";

						JOptionPane.showMessageDialog(AppFrame.this, message, "Unable to Start Program",
								JOptionPane.ERROR_MESSAGE);
						System.exit(-1);
					}
				}
			});

			// Search the layer list for layers that are also select listeners and register them with the World
			// Window. This enables interactive layers to be included without specific knowledge of them here.
			for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers())
			{
				if (layer instanceof SelectListener)
				{

					this.getWwd().addSelectListener((SelectListener) layer);
				}
			}

			this.pack();

			// Center the application on the screen.
			WWUtil.alignComponent(this, this, AVKey.CENTER);
			this.setResizable(true);
			this.setSize(canvasSize);

			//*****************************

			MapFrame.getInstance().setAppFrame(this);
			//ShowWW(includeStatusBar, includeLayerPanel, includeStatsPanel);
			// Create a panel for the bottom component of a vertical split-pane.
			//JPanel bottomPanel = new JPanel(new BorderLayout());
			this.bottomPanel = new AttributeTable();
			// JLabel label = new JLabel("Bottom Panel");
			// label.setBorder(new EmptyBorder(10, 10, 10, 10));
			//label.setHorizontalAlignment(SwingConstants.CENTER);
			//bottomPanel.add(label, BorderLayout.CENTER);
			// Create a vertical split-pane containing the horizontal split plane and the button panel.
			JSplitPane verticalSplitPane = new JSplitPane();
			verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			verticalSplitPane.setTopComponent(wwjPanel);
			verticalSplitPane.setBottomComponent(this.bottomPanel);
			verticalSplitPane.setOneTouchExpandable(true);
			verticalSplitPane.setContinuousLayout(true);
			verticalSplitPane.setResizeWeight(1);


			JSplitPane horizontalSplitPane = new JSplitPane();
			horizontalSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			horizontalSplitPane.setLeftComponent(layerPanel);
			horizontalSplitPane.setRightComponent(verticalSplitPane);
			horizontalSplitPane.setOneTouchExpandable(true);
			horizontalSplitPane.setContinuousLayout(true); // prevents the pane's being obscured when expanding right



			// Put the pieces together.
			this.getContentPane().add(horizontalSplitPane, BorderLayout.CENTER);






			// Add our Menu control.
			this.makeMenu();

			// Create a select listener for shape dragging but do not add it yet. Dragging can be enabled via the user
			// interface.
			//this.dragger = new BasicDragger(this.getWwd());

			// Setup file chooser
			this.fc = new JFileChooser();
			this.fc.addChoosableFileFilter(new FileNameExtensionFilter("ESRI Shapefile", "shp"));
		}


		protected void showGoogleMaps(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel)throws Exception
		{
			GoogleMapsPanel panel = new GoogleMapsPanel(canvasSize, includeStatusBar);




			// Create a panel for the bottom component of a vertical split-pane.
			//JPanel bottomPanel = new JPanel(new BorderLayout());
			this.bottomPanel = new AttributeTable();
			// JLabel label = new JLabel("Bottom Panel");
			// label.setBorder(new EmptyBorder(10, 10, 10, 10));
			//label.setHorizontalAlignment(SwingConstants.CENTER);
			//bottomPanel.add(label, BorderLayout.CENTER);
			// Create a vertical split-pane containing the horizontal split plane and the button panel.
			JSplitPane verticalSplitPane = new JSplitPane();
			verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			verticalSplitPane.setTopComponent(panel);
			verticalSplitPane.setBottomComponent(this.bottomPanel);
			verticalSplitPane.setOneTouchExpandable(true);
			verticalSplitPane.setContinuousLayout(true);
			verticalSplitPane.setResizeWeight(1);


			JSplitPane horizontalSplitPane = new JSplitPane();
			horizontalSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			horizontalSplitPane.setLeftComponent(layerPanel);
			horizontalSplitPane.setRightComponent(verticalSplitPane);
			horizontalSplitPane.setOneTouchExpandable(true);
			horizontalSplitPane.setContinuousLayout(true); // prevents the pane's being obscured when expanding right



			// Put the pieces together.
			this.getContentPane().add(horizontalSplitPane, BorderLayout.CENTER);






			// Add our Menu control.
			this.makeMenu();

			// Create a select listener for shape dragging but do not add it yet. Dragging can be enabled via the user
			// interface.
			//this.dragger = new BasicDragger(this.getWwd());

			// Setup file chooser
			this.fc = new JFileChooser();
			this.fc.addChoosableFileFilter(new FileNameExtensionFilter("ESRI Shapefile", "shp"));
		}



		protected void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) throws Exception
		{

			//showGoogleMaps(includeStatusBar, includeLayerPanel, includeStatsPanel);
			ShowWW(includeStatusBar, includeLayerPanel, includeStatsPanel);

		}

		protected AppPanel createAppPanel(Dimension canvasSize, boolean includeStatusBar)
		{
			return new AppPanel(canvasSize, includeStatusBar);
		}

		public Dimension getCanvasSize()
		{
			return canvasSize;
		}

		public AppPanel getWwjPanel()
		{
			return wwjPanel;
		}

		public WorldWindowGLCanvas getWwd()
		{
			return this.wwjPanel.getWwd();
		}

		public StatusBar getStatusBar()
		{
			return this.wwjPanel.getStatusBar();
		}

		public LayerPanel getLayerPanel()
		{
			return layerPanel;
		}

		public StatisticsPanel getStatsPanel()
		{
			return statsPanel;
		}

		public void setToolTipController(ToolTipController controller)
		{
			if (this.wwjPanel.toolTipController != null)
				this.wwjPanel.toolTipController.dispose();

			this.wwjPanel.toolTipController = controller;
		}

		public void setHighlightController(HighlightController controller)
		{
			if (this.wwjPanel.highlightController != null)
				this.wwjPanel.highlightController.dispose();

			this.wwjPanel.highlightController = controller;
		}





		protected List<Layer> layers = new ArrayList<Layer>();
		protected BasicDragger dragger;
		protected JFileChooser fc = new JFileChooser(Configuration.getUserHomeDirectory());
		protected JCheckBox pickCheck;




		protected void makeMenu()
		{
			JMenuBar jMenuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			jMenuBar.add(fileMenu);

			//JMenu openFileMenu = new JMenu("Open Shapefile");

			JMenuItem connectPostGis = new JMenuItem("Connect to PostGis");
			connectPostGis.addActionListener(new SafeAction("Connect to PostGIS database...") {
				public void action(ActionEvent e) throws Throwable {
					connectToSourcePostGis(new PostgisNGDataStoreFactory(),false);

				}
			});


			JMenuItem openFileButtonPolygon = new JMenuItem("Open Shapefile...");
			openFileButtonPolygon.addActionListener(new SafeAction("Connect to PostGIS database...") {
				public void action(ActionEvent e) throws Throwable {
					connectToSource(new ShapefileDataStoreFactory(),true);
				}
			});




			// Open shapefile from URL button.
			/*JMenuItem openURLButton = new JMenuItem("Open URL...");
			openURLButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					showOpenURLDialog();
				}
			});*/




			fileMenu.add(openFileButtonPolygon);
			fileMenu.add(connectPostGis);
			//fileMenu.add(openURLButton);

			JMenu geoCubeMenu = new JMenu("GeoCube");
			jMenuBar.add(geoCubeMenu);
			JMenuItem configurationsMenu = new JMenuItem("Configurations...");
			configurationsMenu.addActionListener(new CubeConfigurationAction());

			geoCubeMenu.add(configurationsMenu);


			JMenu visuCubeMenu = new JMenu("Google");
			jMenuBar.add(visuCubeMenu);

			JMenuItem exportKmlMenuItem = new JMenuItem("Export KML");
			exportKmlMenuItem.addActionListener(new ExportKmlAction());
			visuCubeMenu.add(exportKmlMenuItem);

			JMenuItem googleMapsMenuItem = new JMenuItem("Show Google Maps");
			googleMapsMenuItem.setEnabled(false);
			//googleMapsMenuItem.addActionListener(new CubeConfigurationAction());
			visuCubeMenu.add(googleMapsMenuItem);




			this.add(jMenuBar, BorderLayout.NORTH);
			//buttonPanel.add(openFileButton);
		}



		//Todo: Colocar isso em um action
		private void connectToSource(DataStoreFactorySpi format, boolean isPolygon) throws Exception {

			File sourceFile = JFileDataStoreChooser.showOpenFile("shp", null);

			if (sourceFile == null) {

				return;
			}

			else{
				FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);

				//Se quiser mudar par ao modo mais simples
				//int retVal = AppFrame.this.fc.showOpenDialog(this);
				//if (retVal != JFileChooser.APPROVE_OPTION)
				//	return;

				//TODO: Adicionar por aquSi
				//FileDataStore store = FileDataStoreFinder.getDataStore(this.fc.getSelectedFile());
				SimpleFeatureSource featureSource = store.getFeatureSource(store.getNames().get(0));
				Thread t = new BuildLayerWorkerThread(featureSource, this);
				t.start();
				getWwd().setCursor(new Cursor(Cursor.WAIT_CURSOR));

			}
			//updateUI();            

		}



		private void connectToSourcePostGis(DataStoreFactorySpi format, boolean isPolygon) throws Exception {
			JDataStoreWizard wizard = new JDataStoreWizard(format);
			int result = wizard.showModalDialog();
			if (result == JWizard.FINISH) {
				Map<String, Object> connectionParameters = wizard.getConnectionParameters();
				DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);
				if (dataStore == null) {
					JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
				}
				else{

					//Se quiser mudar par ao modo mais simples
					//int retVal = AppFrame.this.fc.showOpenDialog(this);
					//if (retVal != JFileChooser.APPROVE_OPTION)
					//	return;

					//TODO: Adicionar por aquSi
					//FileDataStore store = FileDataStoreFinder.getDataStore(this.fc.getSelectedFile());
					SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getNames().get(0));
					Thread t = new BuildLayerWorkerThread(featureSource, this);
					t.start();
					getWwd().setCursor(new Cursor(Cursor.WAIT_CURSOR));

				}
				//updateUI();            
			}
		}



		/*	private void connectToSourcePostGis(DataStoreFactorySpi format, boolean isPolygon) throws Exception {
				JDataStoreWizard wizard = new JDataStoreWizard(format);
				int result = wizard.showModalDialog();
				if (result == JWizard.FINISH) {
					Map<String, Object> connectionParameters = wizard.getConnectionParameters();
					DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);
					if (dataStore == null) {
						JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
					}
					else{
						MapFrame.getInstance().isPolygonMap = isPolygon;
						//Se quiser mudar par ao modo mais simples
						//int retVal = AppFrame.this.fc.showOpenDialog(this);
						//if (retVal != JFileChooser.APPROVE_OPTION)
						//	return;

						//TODO: Adicionar por aquSi
						//FileDataStore store = FileDataStoreFinder.getDataStore(this.fc.getSelectedFile());
						SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getNames().get(0));
						Thread t = new BuildLayerWorkerThread(featureSource, this);
						t.start();
						getWwd().setCursor(new Cursor(Cursor.WAIT_CURSOR));

					}
					//updateUI();            
				}
			}*/

		/*
		public void showOpenFileDialog(boolean isPolygon) throws IOException
		{
			MapFrame.getInstance().isPolygonMap = isPolygon;
			int retVal = AppFrame.this.fc.showOpenDialog(this);
			if (retVal != JFileChooser.APPROVE_OPTION)
				return;

			//TODO: Adicionar por aquSi
			FileDataStore store = FileDataStoreFinder.getDataStore(this.fc.getSelectedFile());
			SimpleFeatureSource featureSource = store.getFeatureSource();
			Thread t = new WorkerThread(featureSource, this);
			t.start();
			getWwd().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		}*/

	}

	public static void insertBeforeCompass(WorldWindow wwd, Layer layer)
	{
		// Insert the layer into the layer list just before the compass.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers)
		{
			if (l instanceof CompassLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition, layer);
	}

	static
	{
		System.setProperty("java.net.useSystemProxies", "true");
		if (Configuration.isMacOS())
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
			System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
			System.setProperty("apple.awt.brushMetalLook", "true");
		}
		else if (Configuration.isWindowsOS())
		{
			System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
		}
	}

	public static AppFrame start(String appName, Class appFrameClass)
	{
		if (Configuration.isMacOS() && appName != null)
		{
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
		}

		try
		{
			final AppFrame frame = (AppFrame) appFrameClass.newInstance();
			frame.setTitle(appName);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			java.awt.EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					frame.setVisible(true);
				}
			});

			return frame;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

}







