package bll.aggregation_functions;

public class AFAppend implements IAggFunction{

	
	public Object updateMeasure(Object aggMeasure, Object newMeasure) {
		if(aggMeasure.toString().length()>256) return aggMeasure.toString();
		if((" - "+aggMeasure.toString()+" - ").contains(" - "+newMeasure.toString()+" - ")) return aggMeasure.toString();
		return aggMeasure.toString().concat(" - "+newMeasure.toString());
	}

	@Override
	public String toString() {
		return "Append";
	}
}
