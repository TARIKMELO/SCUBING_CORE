package bll.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import bll.aggregation_functions.ISpatialAggFunction;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import dal.drivers.CubeColumn;


public class Consumer extends Thread{

	private ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> re;
	final SimpleFeatureType TYPE;
	//final FeatureSource source;
	final HashMap<String, CubeColumn> cubeColumns;
	private DefaultFeatureCollection collection;

	//SimpleFeatureCollection collection = new ListFeatureCollection(TYPE,list);
	//private LinkedList<S> parteMatriz;

	public Consumer(SimpleFeatureType TYPE,ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource , HashMap<String, CubeColumn> cubeColumns){
		this.TYPE = TYPE;
		//this.source = source;
		this.cubeColumns = cubeColumns;
		this.re = resource;

		//collection resultante
		//this.collection =  collection;
		//parteMatriz = new LinkedList<S>();
	}

	public void run(){
		try {
			DimensionTypeValue value ;

			SimpleFeatureBuilder featureBuilder =new SimpleFeatureBuilder(TYPE);
			Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> entry= null;;

			collection = new DefaultFeatureCollection();

			while((re.isFinished()==false)||(re.getNumOfRegisters()!=0)){
				if ((entry = re.getRegister())!=null){


					//Atualizando as medidas
					for (MeasureTypeValue measureTypeValue : entry.getValue()) 
					{
						//Object measureValue = measureTypeValue.getValue();
						//TODO: Jã parte do pressuposto que ta tudo certo caso a funãão de agregaãão seja espacial
						if ((cubeColumns.get(measureTypeValue.getType()).getAggFunction() instanceof ISpatialAggFunction))
						{

							//featureBuilder = ShapeFileUtilities.generateVisualization(measureValue, featureBuilder, (ISpatialAggFunction)cubeColumns.get(measureTypeValue.getType()).getAggFunction());
							featureBuilder.set("the_geom", (Geometry)measureTypeValue.getValue());
						}
						else
						{
							//Atualizando a medida numérica
							featureBuilder.set(measureTypeValue.getType(), measureTypeValue.getValue());
						}

					}
					//Atualizando as dimensães
					for (DimensionTypeValue dimensionTypeValue : entry.getKey()) 
					{
						//inserindo o valor da dimensao na nova linha
						value =dimensionTypeValue;
						//TODO: Consertar la no cubeToTable
						if (value.getType()!="")
						{
							if (dimensionTypeValue.getType()== "the_geom")
							{								//uma região só
								//featureBuilder = ShapeFileUtilities.generateVisualization(value.getValue(), featureBuilder, new SAFUnion(), source);
								featureBuilder.set("the_geom", (Geometry)value.getValue());

							}
							else 
							{
								featureBuilder.set(dimensionTypeValue.getType(), value);
							}
						}
					}

					try{
						//Gravando o valor na nova linha

						collection.add(featureBuilder.buildFeature(null));
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				}
			}


		} catch (Exception e) {

			e.printStackTrace();
		}

		//System.err.println("parte da matriz com: " + parteMatriz.size()+ " colunas");
	}

	public DefaultFeatureCollection getDefaultFeatureCollection()
	{
		return  collection;
	}

}
