package dal.drivers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TextFileReader<T> {

	private BufferedReader in;
	private String file;
	HashMap<String, CubeColumn> cubeColumns;

	public void connect(String inputFileName,  HashMap<String, CubeColumn> cubeColumns) {
		try {
			file  = inputFileName;
			this.cubeColumns = cubeColumns;
			in = new BufferedReader(new FileReader(file));	        
		} catch (IOException e) {
			System.out.println("no connection");
		}
	}

	public IResultSetText<T> getData() {
		ResultSetText rs = new ResultSetText();


		rs.configure(cubeColumns);
		String str = null;	
		try {

			while ((str = in.readLine()) != null) {
				//divide a string em string[]
				String[] s = str.split(" ");
				//String x = s[0] + s[1];

				int length = s.length;
				for(int i=0; i<length; i++)
					rs.updateData(i, (T)(s[i]+i));
				//REFRESH
				rs.insertRow();
			}

			in.close();
		} catch (IOException e) {
			System.out.println(str);
		}

		return rs;
	}

}
