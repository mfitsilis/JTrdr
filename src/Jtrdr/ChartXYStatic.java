package Jtrdr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
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
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;

public class ChartXYStatic {
	private LT lt;
	private FrameMain jfrm;
	//final 
	private XYSeriesCollection dataset = new XYSeriesCollection();
	private XYSeries series1 = new XYSeries("Object 1");
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private JFreeChart lChart;
	public ChartXYStatic(String chartTitle,ArrayList<Double> dataX,ArrayList<Double> dataY,FrameMain jfr ) throws KException, IOException{
		lt=LT.getInstance();
		jfrm=jfr;
		createDataset(dataX,dataY);
		dataset.addSeries(series1);
		chart = createChart("chart","x","y",dataset);
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
		Shape circle = new Ellipse2D.Double(-2.5, -2.5, 5, 5);
		renderer.setSeriesShape(0, circle); //ShapeUtilities.createDiamond(5.0f));
		plot.setRenderer(renderer);
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

	private JFreeChart createChart(String title, String xAxisLabel, String yAxisLabel,XYDataset dataset) {	
		JFreeChart lChart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset);
			        
	    chartPanel = new ChartPanel( lChart );
	    lChart.removeLegend();
	    lChart.setBackgroundPaint(Color.gray);
	    lChart.getPlot().setBackgroundPaint(Color.BLACK);
		return lChart;
	}

	public void clearDataset(){
		series1.clear();
	}
	private void createDataset(ArrayList<Double> dataX,ArrayList<Double> dataY){
		  for(int i=0; i<dataX.size();i++){
		      series1.add( dataX.get(i),dataY.get(i) );        
	      }
	   }
	public ChartPanel chart(){
		return chartPanel;
	}

	public void updateChartEvent(String sym) {
	//	createDataset();
	}
	
	
}

