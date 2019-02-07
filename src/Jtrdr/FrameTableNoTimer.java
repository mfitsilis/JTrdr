package Jtrdr;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class FrameTableNoTimer {
	final static Logger logger=Logger.getLogger(FrameTableNoTimer.class);
  	private ReadTable tab=null;
  	private String query;
	public FrameTableNoTimer(FrameMain jfr, String title, String query,int x,int y,int width,int height) {
		this.query=query;
		JInternalFrame acFrame = new JInternalFrame(title, true,true,true,true);
	  	  try {
				tab = new ReadTable(query);
	  	  } catch (KException|IOException e1) { logger.fatal("showTable - "+title+" - "+e1); }
	  	  JTable table = new JTable(tab.model());
	  	  table.setForeground(Color.white);
	  	  table.setBackground(Color.gray);
	  	  table.setFont(jfr.font());
	  	  table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
	        	    @Override
	        	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	        	    {
	        	        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        	        c.setBackground(row % 2 == 0 ? Color.darkGray : Color.black);
	        	        return c;
	        	    }
	        	});
	  	  table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	      TableColumnAdjuster tca = new TableColumnAdjuster(table);
	      tca.adjustColumns();
		  JScrollPane scrollPane = new JScrollPane();
		  table.setAutoCreateRowSorter(true);
		  scrollPane.setViewportView(table);
	
		  acFrame.getContentPane().add(scrollPane);
	  	  acFrame.setLocation(x, y);
	  	  acFrame.setSize(width, height);
	  	  acFrame.setVisible(true);
	  	  jfr.dtp().add(acFrame);
	  	  acFrame.toFront();	
	}
}
