package bll.data_structures.nodes;

public class MeasureTypeValue {
	
	String value;
	String type;
	public MeasureTypeValue(String value, String type) {
		super();
		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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
		return value;
	}
}
