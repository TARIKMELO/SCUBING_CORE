package Main;

import java.io.IOException;

import bll.util.Util;
import presentation.layout.ApplicationTemplate;
import presentation.layout.ApplicationTemplate.AppFrame;


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
		ApplicationTemplate.start("World Wind Application", AppFrame.class);
		
		
	}
}
