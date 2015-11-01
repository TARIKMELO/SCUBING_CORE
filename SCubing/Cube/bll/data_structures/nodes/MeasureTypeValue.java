package bll.data_structures.nodes;

public class MeasureTypeValue {
	
	Object value;
	String type;
	public MeasureTypeValue(Object value, String type) {
		super();
		this.value = value;
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString()
	{
		return value+"";
	}
}
