package Jtrdr;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import kiss.API;
import kiss.util.As;
import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import ulabel.ULabel;
import utextbox.UTextBox;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.CompoundBorder;
import java.awt.SystemColor;
import javax.swing.JList;

@SuppressWarnings("serial")
public class PanelDebug extends JPanel{
	final static Logger logger=Logger.getLogger(PanelDebug.class);
	private JPanel jp=new JPanel();
	private LT lt;
	private ArrayList<String> watchNames=new ArrayList<>();
	private JList listVars;
	private DefaultListModel<String> dlmListVars = new DefaultListModel<String>();
	JTextArea jta=new JTextArea();
	public PanelDebug(FrameMain jfr) throws KException,IOException{
		lt=LT.getInstance();
		setBackground(Color.GRAY);
		setLayout(null);
		
		JScrollPane scrollPaneText = new JScrollPane();
		scrollPaneText.setBounds(7, 7, 282, 63);
		add(scrollPaneText);
		scrollPaneText.setViewportView(jta);
		jta.setWrapStyleWord(true);
		jta.setLineWrap(true);
		jta.setForeground(Color.WHITE);
		jta.setBackground(Color.BLACK);
		
		JButton btnRefresh = new JButton("");
		btnRefresh.setBounds(297, 7, 28, 63);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String textStr=jta.getText();
				String[] wNames=textStr.split(",");
				if(wNames.length>0) {
					watchNames.clear();
					dlmListVars.clear();
					for(String n:wNames) {
						watchNames.add(n);
					}
					setWatchVars();
				}
			}
		});
		btnRefresh.setBackground(SystemColor.desktop);
		btnRefresh.setForeground(Color.BLACK);
		add(btnRefresh);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(7, 74, 318, 192);
		add(scrollPane);
		
		listVars=new JList<>(dlmListVars);
		scrollPane.setViewportView(listVars);
		listVars.setBackground(Color.BLACK);
		listVars.setForeground(Color.WHITE);
		String watchStr="tmst,datetimestr";
		String[] wNames=watchStr.split(",");	
		if(wNames.length>0) {
			watchNames.clear();
			dlmListVars.removeAllElements();
			for(String n:wNames) {
				watchNames.add(n);
			}
		}
		setWatchVars();
	}
	
	public void setWatchVars() {
		for(String n: watchNames){
			Object value="";
			try {
				Field field = LT.class.getDeclaredField(n);
			    field.setAccessible(true);
				value = field.get(lt);
			}
			catch (NoSuchFieldException|SecurityException|IllegalAccessException|IllegalArgumentException e) {
			 	logger.fatal(e);
			} 
			if(value==null) {
				dlmListVars.addElement(n+":"+"null"); //if null timer stops,new threads start(??? ->all fields should be initialized
				logger.fatal("setWatchVars() - var is null - timer stopped -> must restart, initialize var!");
			}
			else {
				dlmListVars.addElement(n+":"+As.asString(value));			
			}
		}
	}
	
	public void updateWatchVars() {
		Object value="";
		Field field;
		int i=0;
		for(String n: watchNames){
			try {
				field = LT.class.getDeclaredField(n);
			    field.setAccessible(true);
				value = field.get(lt);

				dlmListVars.set(i,n+":"+As.asString(value));
				i++;
			}
			catch (NoSuchFieldException|SecurityException|IllegalAccessException|IllegalArgumentException e) {
			 	e.printStackTrace();
			} 
			
		}
		
	}
	public void updateWatchVar(String varName,Object val) {
		int idx=watchNames.indexOf(varName);
		if (idx>-1) {
		//	uLabel.get(idx).setValue((String) val);
		}
	}
}
