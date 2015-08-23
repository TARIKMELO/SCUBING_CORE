package bll.aggregation_functions;


public class AFCount implements IAggFunction{

	int count = 0;
	
	public Object updateMeasure(Object oldMeasure, Object newMeasure)
	{	
		return count++ ;
	}

	@Override
	public String toString() {
		return "Count";
	}
}