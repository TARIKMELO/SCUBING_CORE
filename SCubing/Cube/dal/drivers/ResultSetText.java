
package dal.drivers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public final class ResultSetText<T> implements IResultSetText<T>{

	private T[] tuple;
	private ArrayList<T[]> tuples;
	private int position;
	private int numOfColuns;
	public static final long serialVersionUID = 0;

	public ResultSetText(){
		super();
	}

	public void configure(HashMap<String, CubeColumn> cubeColumns){
		if(tuples != null)
			tuples.clear();
		else tuples = new ArrayList<T[]>();

		if(tuple !=null)
			tuple = null;
		this.numOfColuns = cubeColumns.size();
		tuple = (T[])new Object[numOfColuns];

		position = 0;





	}	

	public T[] next(){
		try{

			tuple = tuples.get(this.position);
			this.position ++;
			return tuple;
		}catch (Exception e)
		{
			System.out.println(e.getMessage());
			return null;}
	}		

	public boolean hasNext(){
		return tuples.iterator().hasNext();
	}

	public void deleteRow(){
		//E.......
		tuple = null; 
	}

	public void refreshRow()throws SQLException{
		this.position = 0;
		tuple = null;
		tuple = (T[])new Object[this.numOfColuns];

	}

	public void close()throws SQLException{
		this.tuples = null;		
	}			

	public void insertRow(){
		tuples.add(tuple);		
		tuple = (T[])new Object[this.numOfColuns];
	}

	public boolean rowDeleted(){
		if (tuple.length == 0) return true;
		else return false;
	}


	public boolean rowInserted(){
		if (tuple.length > 0)  return true;
		else return false;
	}

	public T getData(int i){
		return tuple[i];
	}	

	public void updateData(int index, T data) {
		tuple[index] = data;
	}	

	public boolean wasNull()throws SQLException{
		if (this.tuples == null) return true;
		else return false;
	}

	public int getFetchSize()throws SQLException{
		return tuples.size(); 
	}

	public static <T extends Object> IResultSetText<T> getInstance(){
		return new ResultSetText<T>();
	}

}
