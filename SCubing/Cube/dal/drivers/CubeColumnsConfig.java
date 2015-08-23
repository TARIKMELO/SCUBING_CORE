package dal.drivers;

import java.util.HashMap;

public class CubeColumnsConfig {
	
	public HashMap<String, CubeColumn> getCubeColumns() {
		return cubeColumns;
	}

	public void setCubeColumns(HashMap<String, CubeColumn> cubeColumns) {
		this.cubeColumns = cubeColumns;
	}

	HashMap<String, CubeColumn> cubeColumns;

}
