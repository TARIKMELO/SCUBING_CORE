package presentation.layout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GoogleMapsPanel extends JPanel {



	public GoogleMapsPanel(Dimension canvasSize, boolean includeStatusBar)
	{
		
		
		super(new BorderLayout());
		JFrame frame = new JFrame("EditorPane Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			JEditorPane editorPane = new JEditorPane("http://maps.google.com.br/");
			editorPane.setEditable(false);
		
			JScrollPane scrollPane = new JScrollPane(editorPane);
			//frame.add(scrollPane);
			super.add(scrollPane);
		} catch (IOException e) {
			System.err.println("Unable to load: " + e);
		}
		//frame.setSize(640, 480);
		//frame.setVisible(true);
		
	}
}





/*class ActivatedHyperlinkListener implements HyperlinkListener {

	  JEditorPane editorPane;

	  public ActivatedHyperlinkListener(JEditorPane editorPane) {
	    this.editorPane = editorPane;
	  }

	  public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
	    HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
	    final URL url = hyperlinkEvent.getURL();
	    if (type == HyperlinkEvent.EventType.ENTERED) {
	      System.out.println("URL: " + url);
	    } else if (type == HyperlinkEvent.EventType.ACTIVATED) {
	      System.out.println("Activated");
	      Document doc = editorPane.getDocument();
	      try {
	        editorPane.setPage(url);
	      } catch (IOException ioException) {
	        System.out.println("Error following link");
	        editorPane.setDocument(doc);
	      }
	    }
	  }
	}*/


