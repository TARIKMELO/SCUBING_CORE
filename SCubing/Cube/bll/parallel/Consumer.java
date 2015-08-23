package bll.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeatureType;

import bll.aggregation_functions.ISpatialAggFunction;
import bll.aggregation_functions.SAFUnion;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import dal.drivers.CubeColumn;
import dal.drivers.ShapeFileUtilities;


public class Consumer extends Thread{

	private ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> re;
	SimpleFeatureType TYPE;
	FeatureSource source;
	HashMap<String, CubeColumn> cubeColumns;
	SimpleFeatureCollection collection;
	//private LinkedList<S> parteMatriz;

	public Consumer(SimpleFeatureType TYPE,ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource ,FeatureSource source, HashMap<String, CubeColumn> cubeColumns, SimpleFeatureCollection collection){
		this.TYPE = TYPE;
		this.source = source;
		this.cubeColumns = cubeColumns;
		this.re = resource;
		this.collection =  collection;
		//parteMatriz = new LinkedList<S>();
	}

	public void run(){
		try {
			DimensionTypeValue value ;

			SimpleFeatureBuilder featureBuilder =new SimpleFeatureBuilder(TYPE);
			Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> entry= null;;
		

			while((re.isFinished()==false)||(re.getNumOfRegisters()!=0)){
				if ((entry = re.getRegister())!=null){
				

					//Atualizando as medidas
					for (MeasureTypeValue measureTypeValue : entry.getValue()) 
					{
						String measureValue = measureTypeValue.getValue();
						//TODO: Já parte do pressuposto que ta tudo certo caso a função de agregação seja espacial
						if ((cubeColumns.get(measureTypeValue.getType()).getAggFunction() instanceof ISpatialAggFunction))
						{
							//TODO
							//Atualizando a medida espacial
							featureBuilder = ShapeFileUtilities.generateVisualization(measureValue, featureBuilder, (ISpatialAggFunction)cubeColumns.get(measureTypeValue.getType()).getAggFunction() , source);
						}
						else
						{
							//Atualizando a medida numérica
							featureBuilder.set(measureTypeValue.getType(), measureTypeValue.getValue());
						}

					}
					//Atualizando as dimensões
					for (DimensionTypeValue dimensionTypeValue : entry.getKey()) 
					{
						//inserindo o valor da dimensao na nova linha
						value =dimensionTypeValue;
						//TODO: Consertar la no cubeToTable
						if (value.getType()!="")
						{
							if (dimensionTypeValue.getType()== "the_geom")
							{
								/*if(value.getValue()=="ALL")
								{
									value.getValue().se = "";
								}*/
								featureBuilder = ShapeFileUtilities.generateVisualization(value.getValue(), featureBuilder, new SAFUnion(), source);
							}
							else 
							{
								featureBuilder.set(dimensionTypeValue.getType(), value);
							}
						}
					}
					//Gravando o valor na nova linha
					collection.add(featureBuilder.buildFeature(null));

				}
			}


		} catch (Exception e) {

			e.printStackTrace();
		}

		//System.err.println("parte da matriz com: " + parteMatriz.size()+ " colunas");
	}

}
