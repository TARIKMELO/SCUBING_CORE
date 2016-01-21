package bll.data_structures;

import java.util.ArrayList;
import java.util.Map.Entry;

import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.data_structures.nodes.NodeSimple;
import bll.parallel.Resource;


public interface ICubeSimple <T>{
	public NodeSimple<T> findNode(T id);
	public void refresh();
	public void insertNode(T id, NodeSimple<T> oneNode);
	public void generateAggregations();
	public long countNumofNodes();
	public Resource<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> cubeToTable();
//	public N getRoot();
}
