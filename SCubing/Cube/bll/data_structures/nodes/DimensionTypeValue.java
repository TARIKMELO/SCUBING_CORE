package bll.data_structures.nodes;

public class DimensionTypeValue<T> implements Comparable<T>, java.io.Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 8979689502535173809L;
	Object value;
	String type;
	//Geometry geometry;
	//	public Geometry getGeometry() {
	//		return geometry;
	//	}
	//
	//	public void setGeometry(Geometry geometry) {
	//		this.geometry = geometry;
	//	}

	//	public DimensionTypeValue(String value, String type) {
	//		super();
	//		this.value = value;
	//		this.type = type;
	//	}

	public DimensionTypeValue(Object value, String type) {
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
		return value.toString();
	}



	public int compareTo(T o) {
		DimensionTypeValue a = (DimensionTypeValue) o;
		String aux = a.value.hashCode()+""+a.type;
		int result = aux.compareTo(this.value.hashCode()+""+this.type);
		return result;


	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DimensionTypeValue other = (DimensionTypeValue) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}





}
