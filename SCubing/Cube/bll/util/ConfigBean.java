package bll.util;

public class ConfigBean {

	public ConfigBean(){}
	//Todas as propriedades devem ser strings
	public String numThreads;
	public String nomeLayer;
	public String circleRadius;
		
	//Propriedades de conexão ao banco de dados
	public String postgisDbtype;
	public String postgisHost;
	public String postgisPort;
	public String postgisSchema;
	public String postgisUser;
	public String postgisPasswd;
	public String postgisDatabase;
	public String postgisTable;

	
	
	public String getPostgisTable() {
		return postgisTable;
	}

	public void setPostgisTable(String postgisTable) {
		this.postgisTable = postgisTable;
	}

	public String getPostgisDbtype() {
		return postgisDbtype;
	}

	public void setPostgisDbtype(String postgisDbtype) {
		this.postgisDbtype = postgisDbtype;
	}

	public String getPostgisHost() {
		return postgisHost;
	}

	public void setPostgisHost(String postgisHost) {
		this.postgisHost = postgisHost;
	}

	public String getPostgisPort() {
		return postgisPort;
	}

	public void setPostgisPort(String postgisPort) {
		this.postgisPort = postgisPort;
	}

	public String getPostgisSchema() {
		return postgisSchema;
	}

	public void setPostgisSchema(String postgisSchema) {
		this.postgisSchema = postgisSchema;
	}

	public String getPostgisUser() {
		return postgisUser;
	}

	public void setPostgisUser(String postgisUser) {
		this.postgisUser = postgisUser;
	}

	public String getPostgisPasswd() {
		return postgisPasswd;
	}

	public void setPostgisPasswd(String postgisPasswd) {
		this.postgisPasswd = postgisPasswd;
	}

	public String getPostgisDatabase() {
		return postgisDatabase;
	}

	public void setPostgisDatabase(String postgisDatabase) {
		this.postgisDatabase = postgisDatabase;
	}

	public String getNomeLayer() {
		return nomeLayer;
	}

	public void setNomeLayer(String nomeLayer) {
		this.nomeLayer = nomeLayer;
	}

	public String getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(String circleRadius) {
		this.circleRadius = circleRadius;
	}
	public String geometryColumnName;

	public String getNumThreads() {
		return numThreads;
	}

	public String getGeometryColumnName() {
		return geometryColumnName;
	}

	public void setGeometryColumnName(String geometryColumnName) {
		this.geometryColumnName = geometryColumnName;
	}

	public void setNumThreads(String numThreads) {
		this.numThreads = numThreads;
	}
}
