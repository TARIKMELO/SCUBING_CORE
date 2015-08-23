package presentation.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import presentation.layout.ConfigurationWindow;

public class CubeConfigurationAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -486497449553709422L;

	public void actionPerformed(ActionEvent arg0) {
		try {
			// TODO Auto-generated method stub
			ConfigurationWindow cubeWindow = new ConfigurationWindow();
			cubeWindow.createSShell();
			cubeWindow.sShell.open();
			cubeWindow.sShell.forceActive();
			org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();
			while (!cubeWindow.sShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();   

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
