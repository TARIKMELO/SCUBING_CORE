package dal.drivers;

import bll.aggregation_functions.IAggFunction;

public class CubeColumn {

	public IAggFunction aggFunction;
	public boolean isMeasure;
	public int index;
	public String columnName;
	public String where;
	public int hierarchy;
	
	public CubeColumn(){}
	public CubeColumn(IAggFunction aggFunction, boolean isMeasure, int index, String columnName) {
		super();
		this.columnName = columnName;
		this.index = index;
		this.aggFunction = aggFunction;
		this.isMeasure = isMeasure;
		
	}
	
	
	public int getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(int hierarchy) {
		this.hierarchy = hierarchy;
	}

	public void setAggFunction(IAggFunction aggFunction) {
		this.aggFunction = aggFunction;
	}
	
	public IAggFunction getAggFunction() {
		return aggFunction;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isMeasure() {
		return isMeasure;
	}

	public void setMeasure(boolean isMeasure) {
		this.isMeasure = isMeasure;
	}
	
	
	public String toString()
	{
			
		return "\n CUbeColumn:  "+ 	"\n IAggFunction " + aggFunction +
				"- isMeasure " + isMeasure +
				"- columnName " + columnName +
				"- where " + where +
				"- hierarchy " + hierarchy;
	}
}
