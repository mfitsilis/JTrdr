package Jtrdr;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import kx.c.KException;

public class ChartLineStatic {
	final static Logger logger=Logger.getLogger(ChartLineStatic.class);
	private TimeSeries series;
	private double lastValue = 100.0;
	private LT lt;
	private ChartPanel chartPanel;
	private FrameMain jfrm;
	private int idx;
	private ArrayList<TimeSeries> seriesList=new ArrayList();
	public ChartLineStatic(String chartTitle,FrameMain jfr,int idx) throws KException, IOException{ //ArrayList<Double> daty,ArrayList<Long> datx){
		lt=LT.getInstance();
		jfrm=jfr;
		this.idx=idx;
	    this.series = new TimeSeries(chartTitle);
	    seriesList.add(this.series);
	    createSeries(this.series);
		XYDataset dataset = new TimeSeriesCollection(this.series);  
		final JFreeChart chart = createChart(dataset);
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
	private void createSeries(TimeSeries data) {
		ArrayList<Tick> trades=lt.wl().instruments().get(idx).trades();
		int size=trades.size();
		for (int i=0;i<size;i++) {
			this.series.addOrUpdate(U.tmst2Millisecond(trades.get(i).time(),lt.pbflag()),
							trades.get(i).price());
		}
	}
	private JFreeChart createChart(final XYDataset dataset) {	
		JFreeChart lChart = ChartFactory.createTimeSeriesChart(
	       "",    //title       
	       "Time", // x           
	       "Price",   // y        
	       dataset,
	       true, true, false);
	       
		//final XYPlot plot = result.getXYPlot();
	    chartPanel = new ChartPanel( lChart );
	    lChart.removeLegend();
	    lChart.setBackgroundPaint(Color.gray);
	    lChart.getPlot().setBackgroundPaint(Color.BLACK);
		return lChart;

	}
	
	
	public void clearDataset (){
		series.clear();
	}

	public ChartPanel chart(){
		return chartPanel;
	}	
}
