package Jtrdr;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JInternalFrame;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PanelCompanyData extends JPanel{
	final static Logger logger=Logger.getLogger(PanelCompanyData.class);

	private JTextField textSymbol;
	private FrameMain jfr;
	private JLabel labCompanyName = new JLabel("");
	private JLabel labSector = new JLabel("");
	private JLabel labIndustry = new JLabel("");
	private JLabel labCountry = new JLabel("");
	private LT lt;
	public PanelCompanyData(FrameMain jfr) throws KException, IOException {
		lt=LT.getInstance();
		this.jfr=jfr;
		setBackground(Color.GRAY);
		setLayout(null);
		
		JButton butShowCmpData = new JButton("Company Data");
		butShowCmpData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateLabels(textSymbol.getText());
				new FrameTableNoTimer(jfr,"Company Data","h\"d2t last select from fdata where sym=`"+
								textSymbol.getText()+"\"",300,100,350,350);
			}
		});
		butShowCmpData.setBounds(166, 164, 122, 28);
		add(butShowCmpData);
		
		JButton butShowCmpDescr = new JButton("Company Description");
		butShowCmpDescr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int id=jfr.cdescr().getidx(textSymbol.getText());
				if(!jfr.cdescr().getdescription(id).equals("enter an existing symbol")){
					updateLabels(textSymbol.getText());
					new FrameText(jfr,jfr.cdescr().getdescription(id));
				}
			}
		});
		butShowCmpDescr.setBounds(6, 164, 152, 28);
		add(butShowCmpDescr);
		
		JLabel label = new JLabel("Symbol:");
		label.setBounds(25, 27, 61, 14);
		add(label);
		
		JLabel label_1 = new JLabel("Name:");
		label_1.setBounds(25, 52, 61, 14);
		add(label_1);
		
		JLabel label_2 = new JLabel("Sector:");
		label_2.setBounds(25, 77, 61, 14);
		add(label_2);
		
		JLabel label_3 = new JLabel("Industry:");
		label_3.setBounds(25, 102, 61, 14);
		add(label_3);
		
		JLabel label_4 = new JLabel("Country:");
		label_4.setBounds(25, 127, 61, 14);
		add(label_4);
		
		textSymbol = new JTextField();
		textSymbol.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textSymbol.setText(textSymbol.getText().toUpperCase());
				if(!textSymbol.getText().equals("")) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER ) {
						updateLabels(textSymbol.getText());
						updateCompanyInfo();
					}
				}
			}
		});
		textSymbol.setForeground(Color.WHITE);
		textSymbol.setColumns(10);
		textSymbol.setBackground(Color.BLACK);
		textSymbol.setBounds(107, 20, 261, 28);
		add(textSymbol);
		
		labCompanyName.setBounds(107, 52, 261, 14);
		add(labCompanyName);
		
		labSector.setBackground(Color.GRAY);
		labSector.setBounds(107, 77, 261, 14);
		add(labSector);
		
		labIndustry.setBounds(107, 102, 261, 14);
		add(labIndustry);
		
		labCountry.setBounds(107, 127, 261, 14);
		add(labCountry);
		
		JButton btnHistData = new JButton("Hist. Data");
		btnHistData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateLabels(textSymbol.getText());
				new FrameTableNoTimer(jfr,"Company Data","h\"reverse 0! select from fdata where sym=`"+textSymbol.getText()+"\"",300,100,600,300);
			}
		});
		btnHistData.setBounds(292, 164, 90, 28);
		add(btnHistData);
	}
	public void labCompanyName(String s1) {
		labCompanyName.setText(s1);
	}
	public void labSector(String s1) {
		labSector.setText(s1);
	}
	public void labIndustry(String s1) {
		labIndustry.setText(s1);
	}
	public void labCountry(String s1) {
		labCountry.setText(s1);
	}
	public void textSymbol(String s1) {
		textSymbol.setText(s1);
	}

	public void updateLabels(String symbol) {
		int idx=jfr.cinfo().idx(symbol);
		if(idx>=0) {
			labCompanyName(jfr.cinfo().name(idx));
			labSector(jfr.cinfo().sector(idx));
			labIndustry(jfr.cinfo().industry(idx));
			labCountry(jfr.cinfo().country(idx));
		}
	}
	
	public void updateCompanyInfo() {
		String lastSymbol=textSymbol.getText();
		int idx=jfr.cinfo().idx(lastSymbol);
		logger.info("entered symbol: "+lastSymbol+" cinfoIdx: "+idx);
		if(idx>=0) {
			labCompanyName(jfr.cinfo().name(idx));
			labSector(jfr.cinfo().sector(idx));
			labIndustry(jfr.cinfo().industry(idx));
			labCountry(jfr.cinfo().country(idx));
		}
	}
}
