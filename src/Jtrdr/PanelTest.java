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

@SuppressWarnings("serial")
public class PanelTest extends JPanel{
	final static Logger logger=Logger.getLogger(PanelTest.class);
	private JPanel jp=new JPanel();
	public PanelTest(FrameMain jfr) throws KException,IOException{
		setLayout(null);
		
		ULabel label = new ULabel("z:","0");
		label.setBounds(52, 21, 81, 29);
		add(label);
		
		ULabel label_1 = new ULabel("a:","1");
		label_1.setBounds(52, 61, 81, 29);
		add(label_1);
		
		ULabel label_2 = new ULabel("b:","2");
		label_2.setValue("fitsilis");
		label_2.setTitle("michalis");
		label_2.setBounds(52, 141, 81, 29);
		add(label_2);
		
		ULabel label_3 = new ULabel("c:","3");
		label_3.setBounds(52, 101, 81, 29);
		add(label_3);
		
		ULabel label_4 = new ULabel("d:","4");
		label_4.setValue("test");
		label_4.setTitle("name");
		label_4.setBounds(143, 141, 81, 29);
		add(label_4);
		
		ULabel label_5 = new ULabel("e:","5");
		label_5.setBounds(143, 101, 81, 29);
		add(label_5);
		
		ULabel label_6 = new ULabel("f:","6");
		label_6.setValue("world");
		label_6.setTitle("hello");
		label_6.setBounds(143, 61, 81, 29);
		add(label_6);
		
		ULabel label_7 = new ULabel("g:","7");
		label_7.setValue("cooperation");
		label_7.setTitle("international");
		label_7.setBounds(143, 21, 140, 29);
		add(label_7);
		
		
		
		
		
	}
}
