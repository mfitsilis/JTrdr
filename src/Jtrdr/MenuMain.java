package Jtrdr;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import kx.c.KException;

public class MenuMain extends JMenuBar{
	final static Logger logger=Logger.getLogger(MenuMain.class);

	private JMenu file = new JMenu("File");
	private JMenuItem help;
	private JMenuItem exit;
	private JMenu ac = new JMenu("Account");
	private JMenuItem getAccount;
	private JMenuItem showTrades;
	private JMenuItem saveSettings;
    private ActionListener fileHelpAction = new FileHelpAction(); 
    private ActionListener fileExitAction = new FileExitAction();
    private FrameMain jfr;

	public MenuMain(FrameMain jfr) {
		this.jfr=jfr;
		this.add(file);
	    file.add(help = new JMenuItem("Help"));		    
	    help.addActionListener(fileHelpAction);
	    file.add(saveSettings = new JMenuItem("Save settings"));		    
	    saveSettings.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		Properties prop = new Properties();
	    		OutputStream output = null;
	    		try {

	    			output = new FileOutputStream("jtrdr.cfg");
	    			prop.setProperty("kdbhost", "localhost");
	    			prop.setProperty("kdbport", "5300");
	    			prop.setProperty("symsfile", "c:\\q\\hist\\syms.txt");
	    			// save properties to project root folder
	    			prop.store(output, null);

	    		} catch (IOException e) { logger.fatal(e); }
	    		  finally {
	    			if (output != null) {
	    				try {
	    					output.close();
	    				} catch (IOException e) { logger.fatal(e); }
	    			}
	    		} 
	    	}
	    });
	    file.add(exit = new JMenuItem("Exit"));
	    exit.addActionListener(fileExitAction);
	    ///////Account
	    this.add(ac);
	    ac.add(getAccount = new JMenuItem("Get Account Update"));
	    ac.add(showTrades = new JMenuItem("Show trades"));
	    getAccount.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
				new FrameTable(jfr,"Account Data","0!h\"lacct\"",300,200,340,110);
	    	}
	    });
	    showTrades.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new FrameTable(jfr,"Orders","0!h\"reverse update id:i from 0!orders\"",100,200,1150,400);
	    	}
	    });
	}
	
	class FileHelpAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		    MessageBox msg=new MessageBox("Not yet implemented.","Help");
		}
	}

	class FileExitAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//KeyStroke keyStrokeToOpen= KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);
	    	//mi_exit.setAccelerator(keyStrokeToOpen);
	        System.exit(0);
		}
	}

}
