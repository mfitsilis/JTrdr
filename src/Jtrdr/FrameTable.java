package Jtrdr;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import kx.c.KException;
import net.miginfocom.swing.MigLayout;

public class FrameTable extends Component {
	final static Logger logger=Logger.getLogger(FrameTable.class);
  	private ReadTable tab=null;
  	private String query;
	public FrameTable(FrameMain jfr, String title, String query,int x,int y,int width,int height) {
		this.query=query;
		JInternalFrame acFrame = new JInternalFrame(title, true,true,true,true);
	  	  try {
				tab = new ReadTable(query);
	  	  } catch (KException|IOException e1) { logger.fatal("showTable - "+title+" - "+e1); }
		  PanelTable pt=new PanelTable(jfr,query,tab);
		  acFrame.getContentPane().add(pt);
	  	  acFrame.setLocation(x, y);
	  	  acFrame.setSize(width, height);
	  	  acFrame.setVisible(true);
	  	  jfr.dtp().add(acFrame);

	  	acFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {           
                pt.setFrameSize(acFrame.getWidth(),acFrame.getHeight());
            }
        });
	  	  
	  	acFrame.toFront();	
	}
}
