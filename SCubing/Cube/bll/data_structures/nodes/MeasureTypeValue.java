package bll.data_structures.nodes;

public class MeasureTypeValue implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8276406366996617355L;
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
