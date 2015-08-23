package bll.data_structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;

import presentation.layout.MapFrame;
import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.parallel.ResourceII;
import dal.drivers.CubeColumn;
import dal.drivers.IResultSetText;
import dal.drivers.ShapeFileUtilities;
import dal.drivers.ShapeFileWriter;


public class CubeGrid {

	String nomeColX;
	String nomeColY;
	String nomeColId;
	public CubeGrid(String nomeColX, String nomeColY,String nomeColId)
	{
		this.nomeColX = nomeColX;
		this.nomeColY = nomeColY;
		this.nomeColId = nomeColId;
	}
	public void performHierarchies(int x, int y, IResultSetText<DimensionTypeValue> rs, FeatureSource source, HashMap<String, CubeColumn> cubeColumns) throws Exception
	{
		while (source.getFeatures().size()>1)
		{
			HashMap<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> result  = new HashMap<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>();
			//N�o uso o tipo abaixo pois nao tem a fun�o contains
			//ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> result =new  ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>>();

			ShapeFileWriter shapeFileWriter = new ShapeFileWriter(cubeColumns);
			Object[] tuple;
			while((tuple=rs.next())!=null){

				ArrayList<DimensionTypeValue> dimensions =  new ArrayList<DimensionTypeValue>();
				ArrayList<MeasureTypeValue> measures =  new ArrayList<MeasureTypeValue>();
				ArrayList<MeasureTypeValue> measuresAux =  new ArrayList<MeasureTypeValue>();	
				double xData = Double.parseDouble(tuple[cubeColumns.get(nomeColX).getIndex() ].toString());

				double yData = Double.parseDouble(tuple[cubeColumns.get(nomeColY).getIndex()].toString());
				int newXData = (int) Math.ceil(xData/x);
				int newYData = (int) Math.ceil(yData/y);
				String key = "C"+newXData+"L"+newYData;

				dimensions.add(new DimensionTypeValue(key,nomeColId));
				dimensions.add(new DimensionTypeValue(newXData+"",nomeColX));
				dimensions.add(new DimensionTypeValue(newYData+"",nomeColY));
				//System.out.println("Key: "+key+" Col: "+newXData+" Lin: "+newYData);
				if (result.containsKey(dimensions))
				{

					measuresAux = result.get(dimensions);
					measures = new ArrayList<MeasureTypeValue>();
					for (Object objectTuple : tuple) {
						DimensionTypeValue tupleItem = (DimensionTypeValue)objectTuple;
						CubeColumn cubeColumn = cubeColumns.get(tupleItem.getType());
						if (cubeColumn.isMeasure())
						{
							int index = cubeColumn.getIndex();
							String value = cubeColumn.getAggFunction().updateMeasure(measuresAux.get(index),tupleItem.getValue()).toString();
							//System.out.println(value);
							measures.add(new MeasureTypeValue(value,cubeColumn.getColumnName()));
						}
					}
					result.put(dimensions, measures);
					//resource.putRegister(new AbstractMap.SimpleEntry (dimensions, measures));
				}
				else
				{

					measures = new ArrayList<MeasureTypeValue>();

					for (Object objectTuple : tuple) {
						DimensionTypeValue tupleItem = (DimensionTypeValue)objectTuple;
						CubeColumn cubeColumn = cubeColumns.get(tupleItem.getType());
						if (cubeColumn.isMeasure())
						{
							measures.add(new MeasureTypeValue(tupleItem.getValue(), tupleItem.getType()));	
						}

					}			
					result.put(dimensions, measures);
					//resource.putRegister(new AbstractMap.SimpleEntry (dimensions, measures));
				}
			}

			FeatureCollection collection = source.getFeatures();
			FeatureIterator<Feature> iterator =  collection.features();
			Feature feature;
			/*while( iterator.hasNext() ){
				feature = iterator.next();
				//System.out.println(feature.getIdentifier().getID().toString());
				System.out.println(feature.getDefaultGeometryProperty().getValue());
				System.out.println(feature.getValue());
			}*/
			if (source.getFeatures().size()>1)
			{

				//TODO: Nao precisa dessa linha
				//Passar o nome como parametro
				//FeatureSource sourceDesti = shapeFileWriter.insertCubeToShapefile(hashToResourceII(result), source);
				FeatureSource sourceDesti = shapeFileWriter.insertCubeToSource(hashToResourceII(result), source);
				//shapeFileWriter.insertCubeToShapefile(result, source,"D:\\data\\Amazonia\\Amazonia"+sourceDesti.getFeatures().size()+".shp");
				IResultSetText<DimensionTypeValue> rsDesti = ShapeFileUtilities.getData(sourceDesti, cubeColumns);
				
			
				MapFrame.getInstance().createLayer (sourceDesti);
				//Para salvar em arquivos
				source = null;
				System.gc();
				rs = rsDesti;
				source = sourceDesti;


				performHierarchies(x, y, rsDesti, sourceDesti, cubeColumns);
			}

		}
		/*else
		{*/
		//map.showLayers();
		/*}*/
	}

	public ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> hashToResourceII (HashMap<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> entrada)
	{
		ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> result =new  ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>>();
		for (Entry<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> entry : entrada.entrySet()) {
			result.putRegister(entry);
		}
		return result;
	}
}
