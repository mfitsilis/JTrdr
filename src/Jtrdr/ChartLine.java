package Jtrdr;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import kx.c.KException;

public class ChartLine {
	final static Logger logger=Logger.getLogger(ChartLine.class);
	private TimeSeries series;
	private String symbol="";
	private LT lt;
	private ChartPanel chartPanel;
	private FrameMain jfr;
	private ArrayList<TimeSeries> seriesList=new ArrayList<>();
	private XYDataset dataset;
	private JFreeChart chart;
	private ArrayList<Tick> trades=new ArrayList<>();
    public ChartLine(String chartTitle,FrameMain jfr) throws KException, IOException{ //ArrayList<Double> daty,ArrayList<Long> datx){
		//super(title);
		lt=LT.getInstance();
		this.jfr=jfr;
	    this.series = new TimeSeries(chartTitle);
	    seriesList.add(this.series);
		dataset = new TimeSeriesCollection(this.series);  
	   /*final*/ chart = createChart(dataset);
	}
	private JFreeChart createChart(final XYDataset dataset) {	
		JFreeChart lChart = ChartFactory.createTimeSeriesChart( //.createBarChart(
	       "",    //title       
	       "Time", // x           
	       "Price",    // y        
	       dataset ,         
	       true, true, false);
	       
	    chartPanel = new ChartPanel( lChart );
	    lChart.removeLegend();
	    lChart.setBackgroundPaint(Color.gray);
	    lChart.getPlot().setBackgroundPaint(Color.BLACK);
		return lChart;

	}
	
	public void clearDataset (String sym){
		int idx=lt.listWlSelected();
		if(idx>-1) {
			if(!sym.equals(this.symbol)) {
				lt.lastWlSelected(lt.listWlSelected());
				this.series.clear();
				if(idx<lt.wl().instruments().size()) {
					trades=lt.wl().instruments().get(idx).trades();
					String s1;
					lt.dlmTns().clear();
	    			int size=trades.size();
	    			int max=lt.maxSizeOfTrades(); //fill list from bottom to top - todo test
					for(int i=((size>max)?size-max:0);i<size;i++) {
						s1=String.format("%s ",lt.wl().instruments().get(lt.listWlSelected()).getTimeAndSalesOfInstrument(i));
						lt.dlmTns().add(0,String.format("%03d ",i+1)+s1);
		    		}
					for (int i=0;i<lt.wl().instruments().get(idx).trades().size();i++) { //todo check!
						this.series.addOrUpdate(U.tmst2Millisecond(trades.get(i).time(),lt.pbflag()?!lt.pbflag():lt.pbflag()),
								trades.get(i).price());
					}
				}
			}
		}
	}

	public ChartPanel chart(){
		return chartPanel;
	}
	
	private Millisecond mi;
	public void updateChartEvent(String symbol) {
		int idx=lt.listWlSelected();
		if(idx>-1) {
			String sym=lt.wl().symbols().get(idx);
			trades=lt.wl().instruments().get(idx).trades();
			int size=trades.size();
			if(size>0) {
		    	Tick t1=lt.wl().instruments().get(idx).lastTrade(); //can give out of bounds exception! 
		    	mi=U.tmst2Millisecond(t1.time(),lt.pbflag()?!lt.pbflag():lt.pbflag()); //todo check!
		    	this.series.addOrUpdate(mi, t1.price());
		    		
		    	this.symbol=sym;
			}
		}
    }
		
	
}
