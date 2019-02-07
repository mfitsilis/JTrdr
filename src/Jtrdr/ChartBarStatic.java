package Jtrdr;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import kx.c.KException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ChartBarStatic {
	private LT lt;
	private FrameMain jfrm;
	//final 
	private DefaultCategoryDataset dataset = new DefaultCategoryDataset( );  
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private JFreeChart lChart;
	public ChartBarStatic(String chartTitle,ArrayList<String> syms,ArrayList<Double> num,FrameMain jfr ) throws KException, IOException{
		lt=LT.getInstance();
		jfrm=jfr;
		chart = createChart(syms,num);
		createDataset(syms,num);
		JInternalFrame chartFrame = new JInternalFrame("Company Data", true,true,true,true);
		JPanel jp=new JPanel();
		chartPanel = new ChartPanel( chart );
		jp.add(chartPanel);
		chartFrame.setContentPane(jp);
		jfr.dtp().add(chartFrame);
		chartFrame.setLocation(450, 150);
		chartFrame.setSize(680, 450);
		chartFrame.setVisible(true);	    		 
		chartFrame.toFront();
	}

	private JFreeChart createChart(ArrayList<String>syms,ArrayList<Double>num) {	
		JFreeChart lChart = ChartFactory.createBarChart(
	       "",    //title       
	       "Symbol", // x           
	       "Trades",    // y        
	        dataset,
	       PlotOrientation.VERTICAL,           
	       true, true, false);
			        
	    chartPanel = new ChartPanel( lChart );
	    lChart.removeLegend();
	    lChart.setBackgroundPaint(Color.gray);
	    lChart.getPlot().setBackgroundPaint(Color.BLACK);
		return lChart;
	}

	public void clearDataset (){
		dataset.clear();
	}
	private CategoryDataset createDataset(ArrayList<String> syms,ArrayList<Double> values){
		  for(int i=0; i<syms.size();i++){
		      dataset.addValue( values.get(i) , "Trades" , syms.get(i) );        
	      }
	      return dataset; 
	   }
	public ChartPanel chart(){
		return chartPanel;
	}

	public void updateChartEvent(String sym) {
	//	createDataset();
	}
}
