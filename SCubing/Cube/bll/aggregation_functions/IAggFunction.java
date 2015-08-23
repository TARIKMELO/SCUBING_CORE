package bll.aggregation_functions;

public interface IAggFunction { 
	
	public Object updateMeasure(Object aggMeasure, Object newMeasure);
	public String toString();
}
