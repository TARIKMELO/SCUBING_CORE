package bll.data_structures.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.vividsolutions.jts.geom.Geometry;

import bll.aggregation_functions.IAggFunction;
import bll.aggregation_functions.SAFUnion;
import dal.drivers.CubeColumn;



public class NodeSimple <T> implements INodeSimple<T>, Serializable{

	private static final long serialVersionUID = 1L;
	protected Map<T, NodeSimple<T>> descendants;
	protected ArrayList<MeasureTypeValue> measureValues;
	protected HashMap<String, CubeColumn> cubeColumns;
	ArrayList<Geometry> geometries = new ArrayList<Geometry>();

	public NodeSimple(	HashMap<String, CubeColumn> cubeColumns, ArrayList<MeasureTypeValue> measureValues){
		descendants = new TreeMap<T, NodeSimple<T>>();		
		this.cubeColumns = cubeColumns;
		this.measureValues = measureValues;
		
		if (measureValues.size()>0)
		{
	
			for (int i=0;i<measureValues.size(); i++)
			{
				
				IAggFunction aggFunction = cubeColumns.get(measureValues.get(i).getType()).getAggFunction();
				//TODO: Ainda tem indice aqui
				if (aggFunction instanceof SAFUnion)
				{
					geometries.add((Geometry) measureValues.get(i).getValue());
					this.measureValues.set(i, new MeasureTypeValue( geometries,measureValues.get(i).getType()));
				}
			}
		}

	}

	public NodeSimple<T> findDescendant(T id) {
		return descendants.get(id);
	}

	public void insertDescendant(T id, NodeSimple<T> descendant) {
		descendants.put(id, descendant);
	}

	public Map<T, NodeSimple<T>> getDescendants() {
		return descendants;
	}

	public ArrayList<MeasureTypeValue> getMeasures() {

		return deepCopy2(measureValues);
	}

	public void updateMeasure(ArrayList<MeasureTypeValue> newValues) {
		Object atualizedValue;
		//for (MeasureTypeValue measureTypeValue : newValues) {
		if (measureValues.size()>0)
		{
			ArrayList<MeasureTypeValue> auxArray = deepCopy(measureValues);
			measureValues = (ArrayList<MeasureTypeValue>) auxArray.clone();
			for (int i=0;i<newValues.size(); i++)
			{
				atualizedValue = new Object();
				IAggFunction aggFunction = cubeColumns.get(newValues.get(i).getType()).getAggFunction();
				//TODO: Ainda tem indice aqui
				if (aggFunction instanceof SAFUnion)
				{
					geometries.add((Geometry) newValues.get(i).getValue());
					this.measureValues.set(i, new MeasureTypeValue( geometries,newValues.get(i).getType()));
				}

				else
				{
					atualizedValue = aggFunction.updateMeasure(this.measureValues.get(i).getValue(), newValues.get(i).getValue());
					this.measureValues.set(i, new MeasureTypeValue( atualizedValue,newValues.get(i).getType()));
				}




			}
		}
	}	


	public ArrayList<MeasureTypeValue> deepCopy(ArrayList<MeasureTypeValue> src)
	{
		ArrayList<MeasureTypeValue> clone = new ArrayList<MeasureTypeValue>(src.size());
		for(MeasureTypeValue item: src)
		{
			clone.add(item);
		}
		return clone;
	}

	public NodeSimple<T> removeNode(T id) {
		return descendants.remove(id);
	}


	public void setMeasureValue(ArrayList<MeasureTypeValue> values) {
		this.measureValues = values;
	}


	public ArrayList<MeasureTypeValue> deepCopy2(ArrayList<MeasureTypeValue> src)
	{
		ArrayList<MeasureTypeValue> clone = new ArrayList<MeasureTypeValue>(src.size());
		for(MeasureTypeValue item: src)
		{
			clone.add(item);
		}
		return clone;
	}
}
