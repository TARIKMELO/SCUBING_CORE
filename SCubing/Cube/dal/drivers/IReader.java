package dal.drivers;

import java.util.HashMap;

public interface IReader <T>{
	
	public enum SelColumnType {
	    INDEX, NAME 
	}
	
	
	public void connect(String inputFileName,  HashMap<String, CubeColumn> cubeColumns) ;
	public IResultSetText<T> getData();
	
}
