package bll.aggregation_functions;

public class aggFuncFactory {
		
	 public static IAggFunction getAggFunction( String aggFunctionType, Object parameter) {  
	        if( aggFunctionType == null ) return null;  
	        else if( aggFunctionType.equals("Avg") ) return new AFAvg();  
	        else if( aggFunctionType.equals("Count") ) return new AFCount();  
	        else if( aggFunctionType.equals("Append") ) return new AFAppend(); 
	        else if( aggFunctionType.equals("Max") ) return new AFMax();  
	        else if( aggFunctionType.equals("Min") ) return new AFMin();
	        else if( aggFunctionType.equals("Soma") ) return new AFSum();
	        else if( aggFunctionType.equals("Distância") ) return new SAFDistance(Double.parseDouble(parameter.toString()));
	        else if( aggFunctionType.equals("Buffer") ) return new SAFBuffer(Double.parseDouble(parameter.toString()));
	        else if( aggFunctionType.equals("Diferenãa") ) return new SAFDiference();
	        else if( aggFunctionType.equals("Count") ) return new AFCount();  
	        else if( aggFunctionType.equals("Interseção") ) return new SAFIntersection();  
	        else if( aggFunctionType.equals("Toca") ) return new SAFTouche();  
	        //else if( aggFunctionType.equals("Vizinhaãa") ) return new SAFNeighborhood();
	        else if( aggFunctionType.equals("União") ) return new SAFUnion();
	        else if( aggFunctionType.equals("União MBR") ) return new SAFUnionMBR();
	        else if( aggFunctionType.equals("União Polígono") ) return new SAFUnionPolygon();
	        else return null;  
	    }  

}
