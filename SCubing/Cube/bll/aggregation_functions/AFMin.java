package bll.aggregation_functions;


public class AFMin implements IAggFunction{
	
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return Math.min(Double.parseDouble(oldMeasure.toString()), Double.parseDouble(oldMeasureTwo.toString()));
	}

	@Override
	public String toString() {
		return "Min";
	}
}
