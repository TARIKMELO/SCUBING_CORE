package bll.core;

import java.io.File;
import java.util.HashMap;

import org.geotools.data.FeatureSource;

import bll.data_structures.PerformCube;
import bll.util.Util;
import dal.drivers.CubeColumn;

public class Core {

	public void gerarCubo() 
	{
		try
		{
			File file = Util.getFile("xml");
			final HashMap<String, CubeColumn> cubeColumns = Util.loadCubeColumnsFromXml(file);
			System.out.println("Colunas carregadas...");
			//Deepy Copy
			//HashMap<String, CubeColumn> cubeColumnsAux = new HashMap<String, CubeColumn>();
			//TODO: Modularizar isso melhor
			FeatureSource featureSource = Util.connectToSourcePostGis(Util.getConfig().getPostgisTable());
			System.out.println("Conectado ao Postgis...");
			System.out.println("Feature Source carregado: "+featureSource.getName());
			PerformCube performCube = new PerformCube();
			performCube.gerarCubo(cubeColumns, featureSource);

		}
		catch(Exception ioEx){
			//textAreaStatus.setText("Cubo gerado com sucesso!");
			ioEx.printStackTrace();
		}
	}
}
