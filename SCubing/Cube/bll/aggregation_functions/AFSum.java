package bll.aggregation_functions;


public class AFSum implements IAggFunction{


	
	public Object updateMeasure(Object oldMeasure, Object oldMeasureTwo) {
		//return 0;
//		if (oldMeasure instanceof Double && oldMeasureTwo instanceof Double)
//		{
//			return (((double)(oldMeasure)) + ((double)(oldMeasureTwo)));
//		}
//		
		return (Double.parseDouble(oldMeasure.toString()) + Double.parseDouble(oldMeasureTwo.toString()));
	}

	@Override
	public String toString() {
		return "Soma";
	}

}
