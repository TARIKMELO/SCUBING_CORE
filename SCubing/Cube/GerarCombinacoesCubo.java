import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;


public class GerarCombinacoesCubo {
	static HashSet<List<String>>  powerSet = new HashSet<List<String>>();
	static List<String> colunas = new ArrayList<String>();
	static String select;
	static String from = "FROM municipios_poligonos_10000";
	static String groupBy;
	public static void main(String[] args){
		select = 

				"SELECT "
						+ "ST_Union(geom) as the_geom,\n"
						+ "sum(AREA) as AREA,"
						;
		colunas.add("NOME_UF,");
		colunas.add("COD_UFMESO,");
		colunas.add("COD_IBGE,");
		colunas.add("COD_UFMICR,");
		colunas.add("ANO");
		buildPowerSet(colunas,colunas.size());

		//System.out.println(powerSet);

		printSql();
	}

	private static void printSql()
	{

		for (List<String> list : powerSet) {
			groupBy = "";
			System.out.println(select);
			for (String string : colunas) {
				if (list.contains(string))
				{ 

					groupBy+=string;
					System.out.println(string);
				}
				else

				{
					System.out.println("'ALL' as "+string);
				}

			}
			System.out.println(from);

			if (!groupBy.isEmpty())
			{

				if (groupBy.substring(groupBy.length()-1,groupBy.length()).equals(","))
				{
					System.out.println("GROUP BY "+groupBy.substring(0,groupBy.length()-1));
				}
				else
				{

					System.out.println("GROUP BY "+groupBy);
				}
			}
			System.out.println("  ");
			System.out.println("UNION ");
			System.out.println("  ");
		}
	}



	private static void buildPowerSet(List<String> list, int count)
	{
		powerSet.add(list);

		for(int i=0; i<list.size(); i++)
		{
			List<String> temp = new ArrayList<String>(list);
			temp.remove(i);
			buildPowerSet(temp, temp.size());
		}
	}





	//	public static void main(String[] args) {
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//		
	//
	//		ArrayList<String> columns = new ArrayList<String>();
	//
	//		columns.add("uf");
	//		columns.add("nome_micro");
	//		columns.add("regiao");
	//		columns.add("nome_meso");
	//
	//
	//
	//		for (int i=0; i<16; i++) {
	//			System.out.println(Integer.toBinaryString(i).toCharArray());
	//
	////			if (Integer.toBinaryString(i).toCharArray().length>=columns.size())
	////			{
	////				for (int j=0; j<columns.size(); j++) {
	////
	////					if (Integer.toBinaryString(i).toCharArray()[j]=='1')
	////					{
	////
	////						System.out.println("ALL as "+ columns.get(j));
	////					}
	////					else
	////
	////					{
	////						System.out.println(columns.get(j));
	////					}
	////
	////				}
	////
	////
	////
	////				System.out.println("UNION");
	////				System.out.println(" ");
	////			}
	//
	//		}
	//	}

}
