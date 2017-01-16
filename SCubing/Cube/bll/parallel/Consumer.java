package bll.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;

import bll.aggregation_functions.ISpatialAggFunction;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import dal.drivers.CubeColumn;


public class Consumer extends Thread{

	private Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> re;
	final SimpleFeatureType TYPE;
	//final FeatureSource source;
	final HashMap<String, CubeColumn> cubeColumns;
	private DefaultFeatureCollection collection;

	//SimpleFeatureCollection collection = new ListFeatureCollection(TYPE,list);
	//private LinkedList<S> parteMatriz;

	public Consumer(SimpleFeatureType TYPE,Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource , HashMap<String, CubeColumn> cubeColumns){
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
			Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> entry= null;

			//select st_astext(st_centroid(geom)) from municipios_poligonos_10000 where nome_muni like 'Brasília'
			//"POINT(-47.7972188300436 -15.7811008579831)"


			GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();


			Point brasiliaPoint =  gf.createPoint(new Coordinate(-47.7972188300436,-15.7811008579831));

			collection = new DefaultFeatureCollection();
			Geometry currentGeometry = null;
			//while((re.isFinished()==false)||(re.getNumOfRegisters()!=0)){
			while ((entry = re.getRegister())!=null){


				//Atualizando as medidas
				for (MeasureTypeValue measureTypeValue : entry.getValue()) 
				{
					//Object measureValue = measureTypeValue.getValue();
					//TODO: Jã parte do pressuposto que ta tudo certo caso a funãão de agregaãão seja espacial
					if ((cubeColumns.get(measureTypeValue.getType()).getAggFunction() instanceof ISpatialAggFunction))
					{

						if (measureTypeValue.getValue() instanceof ArrayList)
						{

							UnaryUnionOp union = new UnaryUnionOp((ArrayList<Geometry>)measureTypeValue.getValue());
							//							
							//															
							currentGeometry = union.union();
							featureBuilder.set("geom", currentGeometry);

//							double distancia = currentGeometry.distance(brasiliaPoint);
//
//							featureBuilder.set("distbrasilia", distancia);		

						}

						else
						{
							currentGeometry = (Geometry)measureTypeValue.getValue();
							featureBuilder.set("geom", currentGeometry);

//							double distancia = currentGeometry.distance(brasiliaPoint);
//
//							featureBuilder.set("distbrasilia", distancia);		

						}

						//featureBuilder = ShapeFileUtilities.generateVisualization(measureValue, featureBuilder, (ISpatialAggFunction)cubeColumns.get(measureTypeValue.getType()).getAggFunction());

					}


					else
					{
						//Atualizando a medida numérica
						featureBuilder.set(measureTypeValue.getType(), measureTypeValue.getValue());
					}

				}



				//Essa parte do código é somente para calcular medidas do tipo IMAggFunction









				//Atualizando as dimensães
				for (DimensionTypeValue dimensionTypeValue : entry.getKey()) 
				{
					//inserindo o valor da dimensao na nova linha
					value =dimensionTypeValue;
					//TODO: Consertar la no cubeToTable
					if (value.getType()!="")
					{
						if (dimensionTypeValue.getType().equals("geom"))
						{								//uma região só



							//featureBuilder = ShapeFileUtilities.generateVisualization(value.getValue(), featureBuilder, new SAFUnion(), source);
							currentGeometry = (Geometry)value.getValue();
							featureBuilder.set("geom", currentGeometry);

						}
						else 
						{
							featureBuilder.set(dimensionTypeValue.getType(), value);
						}
					}
				}





				//Geomtry brasilia = new Point(coordinates, factory); 
				//Atualizando as medidas




				try{
					//Gravando o valor na nova linha

					collection.add(featureBuilder.buildFeature(null));
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				//}
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
