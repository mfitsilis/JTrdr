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

public class ChartBar {
	final static Logger logger=Logger.getLogger(ChartBar.class);
	private LT lt;
	private FrameMain jfrm;
	//final 
	private DefaultCategoryDataset dataset = new DefaultCategoryDataset( );  
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private JFreeChart lChart;
	public ChartBar(String chartTitle,ArrayList<String> syms,ArrayList<Integer> num,FrameMain jfr ) throws KException, IOException{
		lt=LT.getInstance();
		jfrm=jfr;
		chart = createChart(syms,num);
	}//constructor

	private JFreeChart createChart(ArrayList<String>syms,ArrayList<Integer>num) {	
		JFreeChart lChart = ChartFactory.createBarChart(
	       "",    //title       
	       "Symbol", // x           
	       "Trades",    // y        
	        dataset,//(),//syms,num),          
	       PlotOrientation.VERTICAL,           
	       true, true, false);
		
		//XYPlot xyPlot = (XYPlot) lChart.getPlot();
        //XYItemRenderer renderer = xyPlot.getRenderer();
        //renderer.setSeriesPaint(0, Color.blue);
        //NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        //domain.setVerticalTickLabels(true);
            
	    chartPanel = new ChartPanel( lChart );
	    lChart.removeLegend();
	    lChart.setBackgroundPaint(Color.gray);
	    lChart.getPlot().setBackgroundPaint(Color.BLACK);
		return lChart;
	}

	public void clearDataset (){
		dataset.clear();
		//dataset = new DefaultCategoryDataset( );
		//chartPanel=null;
		//chart=null;
		//lChart=null;
	}
	private CategoryDataset createDataset(){
		  for(int i=0; i<lt.wl().symbols().size();i++){
			 try {
			  if(i<lt.wl().instruments().size())
				  dataset.addValue( lt.wl().instruments().get(i).numOfTrades() , "Trades" , lt.wl().symbols().get(i) );
			 } catch (Exception  e) { logger.fatal(e); }
	      }
	      return dataset; 
	   }
	public ChartPanel chart(){
		return chartPanel;
	}

	public void updateChartEvent(String sym) {
		createDataset();
	}
}
