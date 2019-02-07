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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import java.awt.Font;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class PanelReflection extends JPanel{
	final static Logger logger=Logger.getLogger(PanelReflection.class);
	private FrameMain jfr;
	private PanelTimeAndSales ptns;
	private PanelSettings ps;
	private PanelReflection pr;
	private LT lt;
	private String classVars="";
	private JTextArea textClassVars = new JTextArea();
	private JComboBox cmbClasses = new JComboBox();
	private Watchlist wl;
	public PanelReflection(LT lt,FrameMain jfr,PanelTimeAndSales ptns,PanelSettings ps,Watchlist wl) throws KException,IOException{
		this.lt=lt;
		this.jfr=jfr;
		this.ptns=ptns;
		this.ps=ps;
		this.wl=wl;
		pr=this;
		
		
		setBackground(Color.GRAY);
		setLayout(null);
		
		JButton btnGetData = new JButton("refresh");
		btnGetData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectClass(cmbClasses);
			}
		});
		btnGetData.setBounds(218, 7, 67, 28);
		add(btnGetData);
		
		JLabel lblSymbol = new JLabel("Class:");
		lblSymbol.setBounds(7, 13, 44, 16);
		add(lblSymbol);
		
		
		cmbClasses.setModel(new DefaultComboBoxModel(new String[] {"PanelReflection","LT", "FrameMain","PanelTimeAndSales","PanelSettings","Watchlist"}));
		
		cmbClasses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectClass(cmbClasses);
			}
		});
		
		cmbClasses.setBounds(49, 8, 164, 26);
		add(cmbClasses);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(7, 41, 323, 164);
		add(scrollPane);
		classVars=ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
		String s1=classVars.replace(",", "\n");
		textClassVars.setFont(new Font("Liberation Mono", Font.PLAIN, 12));
		textClassVars.setText(s1);
		scrollPane.setViewportView(textClassVars);
		textClassVars.setEditable(false);
		textClassVars.setLineWrap(true);
		textClassVars.setWrapStyleWord(true);
		textClassVars.setBackground(Color.BLACK);
		textClassVars.setForeground(Color.WHITE);
		JButton butResize = new JButton(">>");
		butResize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(butResize.getText().equals(">>")) {
					butResize.setText("<<");
					jfr.setSize("Reflection", 350, 250);
				}
				else if(butResize.getText().equals("<<")) {
					butResize.setText(">>");
					jfr.setSize("Reflection", 350, 70);
				}
			}
		});
		butResize.setBounds(286, 7, 44, 28);
		add(butResize);
		
	}
	public void selectClass(JComboBox cmbClasses) {
		switch (cmbClasses.getSelectedIndex()) {
		case 0:
			classVars=ReflectionToStringBuilder.toString(pr, ToStringStyle.MULTI_LINE_STYLE);
			break;
		case 1:
			classVars=ReflectionToStringBuilder.toString(lt, ToStringStyle.MULTI_LINE_STYLE);
			break;
		case 2:
			classVars=ReflectionToStringBuilder.toString(jfr, ToStringStyle.MULTI_LINE_STYLE);
			break;
		case 3:
			classVars=ReflectionToStringBuilder.toString(ptns, ToStringStyle.MULTI_LINE_STYLE);
			break;
		case 4:
			classVars=ReflectionToStringBuilder.toString(pr, ToStringStyle.MULTI_LINE_STYLE);
			break;
		case 5:
			classVars=ReflectionToStringBuilder.toString(wl, ToStringStyle.MULTI_LINE_STYLE);
			break;
		}			
		//U.println(classVars.length());
		String s1=classVars.replace(",", "\n");
		textClassVars.setText(s1);			

	}
}
