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

public class PanelLineChart extends JPanel{
	
    private JLabel labNumberOfTrades = new JLabel("");
    private LT lt;
	public PanelLineChart(FrameMain jfr,ChartLine linechart) throws KException, IOException {
		lt=LT.getInstance();
		setBackground(Color.GRAY);
	    //setLayout(new MigLayout("", "[112px][20px][123px]", "[16px][2px][99px]"));
		setLayout(new MigLayout("", "[][]", "[][][]"));
	    add(labNumberOfTrades,"cell 0 0 2 1,growx"); 
		JSeparator separator = new JSeparator();
		add(separator,"cell 0 1 2 1,growx"); 
		
		JPanel panelTradesChart = new JPanel();
		panelTradesChart.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panelTradesChart.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panelTradesChart.add(linechart.chart());
		panelTradesChart.setBackground(Color.GRAY);
		add(panelTradesChart, "cell 0 2 2 1,grow");
		panelTradesChart.setLayout(new BoxLayout(panelTradesChart, BoxLayout.X_AXIS));
	}
	/** use integer because event sends integer to listWlSelected (don't want to create another event) */
	public void updateNumberOfTrades(int i1) { 
		if(lt.listWlSelected()!=-1)
			labNumberOfTrades.setText("Symbol: "+lt.wl().symbols().get(lt.listWlSelected())+ " - "+"Number of Trades: "+API.asString(lt.wl().instruments().get(lt.listWlSelected()).numOfTrades() ));
	}

}

//class UpdateSliderPlotAction implements ChangeListener {
//	private FrameMain jfr;
//	public UpdateSliderPlotAction(FrameMain jfrm) {
//		jfr=jfrm;
//	}
//	@Override
//	public void stateChanged(ChangeEvent e) {
//		RangeSlider slider = (RangeSlider) e.getSource();
//		    int v1=slider.getMinimum();
//			int v2=slider.getValue();
//			int v3=slider.getUpperValue();
//			int v4=slider.getMaximum();
//			jfr.rangeSliderValue1a().setText(String.valueOf(slider.getValue()));
//		    jfr.rangeSliderValue1b().setText(String.valueOf(slider.getUpperValue()));
//		}        
//}