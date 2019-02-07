package Jtrdr;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import ulabel.ULabel;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class PanelTest2 extends JFrame{
	final static Logger logger=Logger.getLogger(PanelTest2.class);
	private JFrame jf=new JFrame();
	public PanelTest2(FrameMain jfr) throws KException,IOException{
		setTitle("hello");
		getContentPane().setLayout(null);
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBounds(111, 107, 1, 1);
		getContentPane().add(desktopPane);
		
		JInternalFrame internalFrame_1 = new JInternalFrame("New JInternalFrame");
		internalFrame_1.setBounds(10, 25, 200, 139);
		getContentPane().add(internalFrame_1);
		internalFrame_1.getContentPane().setLayout(null);
		internalFrame_1.setVisible(true);
		
		
		
		
		
	}
}
