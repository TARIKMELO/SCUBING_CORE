package bll.aggregation_functions;

public class MAFEuclideDistance extends IMAggFunction implements IAggFunction{

	@Override
	public Object updateMeasure(Object aggMeasure, Object newMeasure) {
		// TODO Auto-generated method stub
		return aggMeasure;
	}
	
	@Override
	public String toString() {
		return "Distância Euclidiana";
	}
}
