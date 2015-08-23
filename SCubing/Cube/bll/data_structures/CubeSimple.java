package bll.data_structures;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import bll.data_structures.nodes.DimensionTypeValue;
import bll.data_structures.nodes.MeasureTypeValue;
import bll.data_structures.nodes.NodeSimple;
import bll.parallel.ResourceII;
import dal.drivers.CubeColumn;

public class CubeSimple <T> implements ICubeSimple<T>, Serializable{

	private static final long serialVersionUID = -6979370715315961825L;
	protected NodeSimple<T> root;
	protected NodeSimple<T> currentNode;
	long numOfNodes;
	int numColumns;
	HashMap<String, CubeColumn> cubeColumns;

	public CubeSimple(HashMap<String, CubeColumn> cubeColumns, int numColumns){

		this.cubeColumns = cubeColumns;
		this.numColumns = cubeColumns.size();
		//Criando o nodo root
		root = new NodeSimple<T>(this.cubeColumns, new ArrayList<MeasureTypeValue>());
		currentNode = root;
		numOfNodes = 0;
	}
	
	

	public NodeSimple<T> findNode(T id) {
		NodeSimple<T> aux = currentNode.findDescendant(id);
		if(aux!=null){
			currentNode = aux;
		}
		return aux;
	}



	public void insertNode(T id, NodeSimple<T> oneNode) {
		currentNode.insertDescendant(id, oneNode);
		currentNode = oneNode;
	}

	public void refresh() {
		currentNode = root;		
	}

	public void generateAggregations(){
		this.generateAggregations(null, root);
	}

	private void generateAggregations(NodeSimple<T> ancestral, NodeSimple<T> oneNode){
		List<NodeSimple<T>> d = new CopyOnWriteArrayList<NodeSimple<T>>(oneNode.getDescendants().values());
		for(NodeSimple<T> oneDesc: d)
			generateAggregations(oneNode, oneDesc);
		if(ancestral!=null){
			currentNode = ancestral;
			copyNodes(oneNode);
		}		
	}


	private void copyNodes(NodeSimple<T> oneNode){
		for(T auxId: oneNode.getDescendants().keySet()){
			NodeSimple<T> aux = oneNode.findDescendant(auxId);
			NodeSimple<T> auxAncestral = currentNode;
			if (findNode(auxId)==null)
			{
				NodeSimple<T> newNode =  new NodeSimple<T>(cubeColumns, aux.getMeasures());
				insertNode(auxId, newNode);			
			}
			else
			{
				currentNode.updateMeasure(aux.getMeasures());
			}

			copyNodes(aux);
			currentNode = auxAncestral;
		}			
	}

	public long countNumofNodes(){
		numOfNodes = 0;
		countNodes(root);
		return numOfNodes;

	}

	private void countNodes(NodeSimple<T> oneNode){

		for(NodeSimple<T> oneDescendant: oneNode.getDescendants().values()){
			numOfNodes++;
			countNodes(oneDescendant);
		}
	}


	public NodeSimple<T> getRoot() {
		return this.root;
	}

	public NodeSimple<T> getCurrentNode()
	{
		return currentNode;
	}


	ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> resource;

	//HashMap<ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>> hashResult;
	//Percorre o cubo que está em uma árvore e insere os registro em uma table(HashMap)
	public ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>> cubeToTable()
	{
		DimensionTypeValue value;
		DimensionTypeValue valueAux;
		resource =new  ResourceII<Entry <ArrayList<DimensionTypeValue>, ArrayList<MeasureTypeValue>>>();
		ArrayList<DimensionTypeValue> line = new ArrayList<DimensionTypeValue>(numColumns);
		NodeSimple<T> oneNode = root;
		Set<Entry<T, NodeSimple<T>>> a = oneNode.getDescendants().entrySet();

		for(Entry<T,NodeSimple<T>> oneDescendant: a ){
			line.clear();
			for (int i =0; i<numColumns;i++ )
			{
				line.add(new DimensionTypeValue("ALL",""));	
			}
			//Retirando o ultima caractere que indica a coluna
			valueAux = (DimensionTypeValue) oneDescendant.getKey();
			value = new DimensionTypeValue(valueAux.getValue(),valueAux.getType());
			line.set(cubeColumns.get(valueAux.getType()).getIndex(), value);
			
			resource.putRegister(new AbstractMap.SimpleEntry (deepCopy(line), oneDescendant.getValue().getMeasures()))	;
			cubeToTable(oneDescendant.getValue(), line);
		}
		return resource;
	}

	private void cubeToTable(NodeSimple<T> oneNode, ArrayList<DimensionTypeValue> line)
	{
		DimensionTypeValue valueAux;
		DimensionTypeValue value;
		ArrayList<DimensionTypeValue> lineAux =  new ArrayList<DimensionTypeValue>(numColumns);


		Set<Entry<T, NodeSimple<T>>> a = oneNode.getDescendants().entrySet();
		for(Entry<T,NodeSimple<T>> oneDescendant: a ){
			//Retirando o ultima caractere que indica a coluna
			lineAux =(ArrayList<DimensionTypeValue>) line.clone();
			valueAux = (DimensionTypeValue) oneDescendant.getKey();
			value = new DimensionTypeValue(valueAux.getValue(),valueAux.getType());
			//lineAux =  new ArrayList<DimensionTypeValue>(numColumns);
			//			/System.out.println("dddfsdfsdf "+lineAux.size());

			lineAux.set(cubeColumns.get(valueAux.getType()).getIndex(), value);

			//Estrutura que gera a visualização depois
			resource.putRegister(new AbstractMap.SimpleEntry (deepCopy(lineAux), new ArrayList<MeasureTypeValue>(oneDescendant.getValue().getMeasures())))	;
			//hashResult.put(deepCopy(lineAux), new ArrayList<MeasureTypeValue>(oneDescendant.getValue().getMeasures()));
			cubeToTable(oneDescendant.getValue(), lineAux);
		}
	}




	//TODO: Colocar isso tipado
	public ArrayList<DimensionTypeValue> deepCopy(ArrayList<DimensionTypeValue> src)
	{
		ArrayList<DimensionTypeValue> clone = new ArrayList<DimensionTypeValue>(src.size());
		for(DimensionTypeValue item: src)
		{
			clone.add(item);
		}
		return clone;
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


