package Main;

import java.io.IOException;

import bll.core.Core;
import bll.util.Util;


public class main {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args)
	{
		// Call the static start method like this from the main method of your derived class.
		// Substitute your application's name for the first argument.
		
	
		Util.beginLog();
		Core core = new Core();
		core.gerarCubo();
		//ApplicationTemplate.start("World Wind Application", AppFrame.class);
		
		
	}
}
