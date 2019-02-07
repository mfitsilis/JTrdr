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
import javax.swing.border.TitledBorder;

import kiss.API;
import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JSeparator;
import javax.swing.JList;
import net.miginfocom.swing.MigLayout;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import javax.swing.Box;

public class PanelBarChart extends JPanel{
	
    private JLabel labNumberOfSymbols = new JLabel("");
    private LT lt;
	public PanelBarChart(FrameMain jfr,ChartBar bchart) throws KException, IOException {
		lt=LT.getInstance();
		setBackground(Color.GRAY);
	    //setLayout(new MigLayout("", "[112px][20px][123px]", "[16px][2px][99px]"));
		setLayout(new MigLayout("", "[][]", "[][][]"));
		
	    JLabel lblNumberOfSymbols = new JLabel("Number of Symbols:");
	    add(lblNumberOfSymbols,"cell 0 0");
	    add(labNumberOfSymbols,"cell 1 0,growx"); 
		JSeparator separator = new JSeparator();
		add(separator,"cell 0 1 2 1,growx"); 
		
		JPanel panelSymbolsChart = new JPanel();
		panelSymbolsChart.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panelSymbolsChart.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panelSymbolsChart.add(bchart.chart());
		panelSymbolsChart.setBackground(Color.GRAY);
		add(panelSymbolsChart, "cell 0 2 2 1,grow");
		panelSymbolsChart.setLayout(new BoxLayout(panelSymbolsChart, BoxLayout.X_AXIS));
	}
	public void updateNumberOfSymbols(String s1) {
		labNumberOfSymbols.setText(API.asString(lt.wl().symbols().size()));
	}

}
