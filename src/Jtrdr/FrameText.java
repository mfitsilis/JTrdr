package Jtrdr;

import java.awt.Color;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class FrameText {
	public FrameText(FrameMain jfr,String text)  {
		 JInternalFrame descrFrame = new JInternalFrame("Company Data", true,true,true,true);
		 JScrollPane scrpaneCmpDescr=new JScrollPane();
		 JTextArea txtCmpDescr=new JTextArea(); 
		 txtCmpDescr.setLineWrap(true);
		 txtCmpDescr.setDisabledTextColor(Color.white);
		 txtCmpDescr.setWrapStyleWord(true);
		 txtCmpDescr.setEnabled(false);
		 scrpaneCmpDescr.setViewportView(txtCmpDescr);
		 descrFrame.setContentPane(scrpaneCmpDescr);
		 jfr.dtp().add(descrFrame);
		 descrFrame.setLocation(450, 150);
		 descrFrame.setSize(400, 400);
		 descrFrame.setVisible(true);	    		 
		 descrFrame.toFront();
	     txtCmpDescr.setText(text);
	}
}
