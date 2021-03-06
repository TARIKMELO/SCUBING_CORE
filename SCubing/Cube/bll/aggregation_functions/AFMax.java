package bll.aggregation_functions;


public class AFMax implements IAggFunction{

	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		return Math.max(Double.parseDouble(oldMeasure.toString()), Double.parseDouble(oldMeasureTwo.toString()));
	}

	@Override
	public String toString() {
		return "Max";
	}
}
