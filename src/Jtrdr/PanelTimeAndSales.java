package Jtrdr;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.border.TitledBorder;

import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JSeparator;
import javax.swing.JList;
import net.miginfocom.swing.MigLayout;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;

import kiss.API;

import javax.swing.event.ChangeEvent;
import javax.swing.ListModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanelTimeAndSales extends JPanel{	
	private JList<String> listTns;
	private FrameMain jfr;
	private JSpinner sboxMaxSize;
	final static int MAXSIZE=1000;
	private int maxSize;
	private LT lt;
	
	public PanelTimeAndSales(FrameMain jfr) throws KException, IOException {
		this.jfr=jfr;
		this.lt=LT.getInstance();
		
		setBackground(Color.GRAY);
		setLayout(null);
		
		JLabel lblTimeAndSales = new JLabel("Time and Sales");
		lblTimeAndSales.setBounds(7, 7, 87, 16);
		add(lblTimeAndSales);
		
		JLabel lblHeader = new JLabel(String.format("%3s %-5s %8s %8s %8s","idx","sym","time","price","volume"));
		lblHeader.setFont(new Font("Liberation Mono", Font.PLAIN, 12));
		lblHeader.setBounds(17, 33, 308, 16);
		add(lblHeader);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(7, 27, 247, 2);
		add(separator);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 55, 319, 213);
		add(scrollPane);
		listTns=new JList<>(lt.dlmTns());
		listTns.setSelectionForeground(Color.WHITE);
		scrollPane.setViewportView(listTns);
		listTns.setFont(jfr.font());
		
		listTns.setForeground(Color.WHITE);
		listTns.setBackground(Color.BLACK);
		
		sboxMaxSize = new JSpinner();
		sboxMaxSize.setValue(MAXSIZE); //initial value
		sboxMaxSize.addChangeListener(new ChangeListener() { //after typing number must press enter!
			public void stateChanged(ChangeEvent e) {
				if(lt.wl().instruments().size()!=0) {
				maxSize=(int)sboxMaxSize.getValue();
				if(maxSize>MAXSIZE) sboxMaxSize.setValue(MAXSIZE);
				else if(maxSize<1) sboxMaxSize.setValue(1);
				maxSize=(int)sboxMaxSize.getValue();
				lt.maxSizeOfTrades(maxSize);
				int tnsSize=lt.dlmTns().size();
				if(maxSize>tnsSize) { // cannot just add missing entries because nums would be wrong
					lt.dlmTns().clear();
					Instrument instrument=lt.wl().instruments().get(lt.listWlSelected());
	    			int size=instrument.trades().size();
	    			String s1;
	    			for(int i=((size>maxSize)?size-maxSize:0);i<size;i++) { //add all entries or up to maxSize
						s1=String.format("%s ",instrument.getTimeAndSalesOfInstrument(i));
						lt.dlmTns().add(0,String.format("%03d ",i+1)+s1);
		    		}
				}
				else {
					while(lt.dlmTns().size()>maxSize) { //remove excess entries
						lt.dlmTns().removeElementAt(lt.dlmTns().size()-1);	
					}
				}
			}
			}
		});
		maxSize=(int)sboxMaxSize.getValue();
		lt.maxSizeOfTrades(maxSize);

		sboxMaxSize.setForeground(Color.WHITE);
		sboxMaxSize.setBackground(Color.BLACK);
		sboxMaxSize.setBounds(167, 1, 87, 28);
		add(sboxMaxSize);
		
		JLabel lblMaxRows = new JLabel("max rows:");
		lblMaxRows.setBounds(106, 7, 87, 16);
		add(lblMaxRows);
	}
}
