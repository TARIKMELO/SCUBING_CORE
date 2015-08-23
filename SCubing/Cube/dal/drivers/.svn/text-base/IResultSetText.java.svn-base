package dal.drivers;

import java.sql.SQLException;
import java.util.HashMap;

/*
 * Joubert Lima
 * @version 0.5
 * 
 * This interface enables the implementation of GENERIC Result set
 */

public interface IResultSetText<T>{

	public abstract T[] next();

	public abstract boolean hasNext();

	public abstract void deleteRow();

	public abstract void refreshRow() throws SQLException;

	public abstract void close() throws SQLException;

	public abstract void insertRow();

	public abstract boolean rowDeleted();

	public abstract boolean rowInserted();

	public abstract T getData(int i);

	public abstract void updateData(int index, T data);

	public abstract boolean wasNull() throws SQLException;

	public abstract int getFetchSize()throws SQLException;

	public abstract void configure(HashMap<String, CubeColumn> cubeColumns);

}