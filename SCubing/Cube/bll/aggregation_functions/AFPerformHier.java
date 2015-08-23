package bll.aggregation_functions;

public class AFPerformHier {

	public Object updateMeasure(Object x, Object xData) {
		return ( Integer.parseInt(xData.toString())%Integer.parseInt(x.toString()));
	}


	public String toString() {
		return " ";
	}
	
}
