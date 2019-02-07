package Jtrdr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JInternalFrame;

import kx.c;
import kx.c.Flip;
import kx.c.KException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;


public class ChartHist extends JInternalFrame {
	final static Logger logger=Logger.getLogger(ChartHist.class);

	  private Flip flip;
	  private KxTableModel model = new KxTableModel();
	  private int nCols,nRows;
	  private Object result;
	  
	 private JFreeChart chart;
	 private ChartPanel chartpanel;
	 private LT lt;
	 private CategoryDataset createDataset( ) {
	      final DefaultCategoryDataset dataset = 
	      new DefaultCategoryDataset( );  
	      dataset.addValue( 1.0 , "" , "1" );        
	      dataset.addValue( 3.0 , "" , "2" );        
	      dataset.addValue( 5.0 , "" , "3" ); 
	      dataset.addValue( 5.0 , "" , "4" );           
	      return dataset; 
	   }

	 private int from,to;
	 public ChartHist(String s1,int v1, int v2,Object result) throws KException, IOException {

		 lt=LT.getInstance();
		 from=v1;
		 to=v2;
		 this.result=result;
       //JFreeChart chart = ChartFactory.createXYLineChart(s1,"Category","Score",            
       //    createDataset1(),PlotOrientation.VERTICAL,false, false, false);
       JFreeChart chart = ChartFactory.createCandlestickChart("","","", getPriceDataSet(s1), false) ;
       				//(s1,"Category","Score",            
               //createDataset1(),PlotOrientation.VERTICAL,false, false, false);
       //chart.getPlot().
       //((DateAxis) domainAxis).setTimeline( SegmentedTimeline.newMondayThroughFridayTimeline() );
       XYPlot plot=chart.getXYPlot();
       plot.setDomainGridlinesVisible(false); 
       ((DateAxis) plot.getDomainAxis()).setTimeline(newWorkdayTimeline());
       //XYLineAndShapeRenderer renderer =
       //        (XYLineAndShapeRenderer) plot.getRenderer();
           
       //XYBarRenderer renderer = (XYBarRenderer) chart.getCategoryPlot().getRenderer();
       //renderer.setMargin(0);
       
       chart.setBackgroundPaint(Color.gray);
       chart.getPlot().setBackgroundPaint(Color.BLACK);
       

       /*       XYPlot plot = (XYPlot) chart.getPlot();
       plot.getDomainAxis().setStandardTickUnits(
           NumberAxis.createIntegerTickUnits());
       XYLineAndShapeRenderer renderer =
           (XYLineAndShapeRenderer) plot.getRenderer();
       renderer.setSeriesShapesVisible(0, true);
       renderer.setSeriesShapesVisible(1, true);
       renderer.setSeriesShapesVisible(2, true);
       //Rectangle rect = new Rectangle(2, 2);
       Shape circle = new Ellipse2D.Float(-3, -3, 6, 6); //must be half the diameter
       renderer.setSeriesShape(0,circle);
       renderer.setSeriesShape(1,circle);
       renderer.setSeriesShape(2,circle);
  */ 	   
        chartpanel = new ChartPanel( chart ); 

    }
    public ChartPanel getchart(){
    	return chartpanel;
    }
    
    private XYDataset createDataset1( ) {
        final XYSeries firefox = new XYSeries( "Firefox" );          
        firefox.add( 1.0 , 1.0 );          
        firefox.add( 2.0 , 4.0 );          
        firefox.add( 3.0 , 3.0 );          
        
        final XYSeries chrome = new XYSeries( "Chrome" );          
        chrome.add( 1.0 , 4.0 );          
        chrome.add( 2.0 , 5.0 );          
        chrome.add( 3.0 , 6.0 );          
        
        final XYSeries iexplorer = new XYSeries( "InternetExplorer" );          
        iexplorer.add( 3.0 , 4.0 );          
        iexplorer.add( 4.0 , 5.0 );          
        iexplorer.add( 5.0 , 4.0 );          
        
        final XYSeriesCollection dataset = new XYSeriesCollection( );          
        dataset.addSeries( firefox );          
        dataset.addSeries( chrome );          
        dataset.addSeries( iexplorer );
        return dataset;
     }
	 private CategoryDataset createDataset2( ) {
	      final String fiat = "FIAT";        
	      final String audi = "AUDI";        
	      final String ford = "FORD";        
	      final String speed = "Speed";        
	      final String millage = "Millage";        
	      final String userrating = "User Rating";        
	      final String safety = "safety";        
	      final DefaultCategoryDataset dataset = 
	      new DefaultCategoryDataset( );  

	      dataset.addValue( 1.0 , fiat , speed );        
	      dataset.addValue( 3.0 , fiat , userrating );        
	      dataset.addValue( 5.0 , fiat , millage ); 
	      dataset.addValue( 5.0 , fiat , safety );           

	      dataset.addValue( 5.0 , audi , speed );        
	      dataset.addValue( 6.0 , audi , userrating );       
	      dataset.addValue( 10.0 , audi , millage );        
	      dataset.addValue( 4.0 , audi , safety );

	      dataset.addValue( 4.0 , ford , speed );        
	      dataset.addValue( 2.0 , ford , userrating );        
	      dataset.addValue( 3.0 , ford , millage );        
	      dataset.addValue( 6.0 , ford , safety );               

	      return dataset; 
	   }

	    public OHLCDataset getPriceDataSet(String s1) {
	    	String symbol=s1;
	        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
	        
		    	try {
		    		//Object result=c.k("ohlc3 30");//get `:../hist/"+symbol);//+"_eod"); 
//		    		Object result=lt.c1().k("data:(get `:../hist/"+symbol+");ohlc3 30");
		    		flip=kx.c.td(result);
	              	nCols=flip.y.length;
	              	nRows=Array.getLength(flip.y[0]);
	              	//header=flip.x;
	          	} catch (Exception e) { logger.fatal(e); U.exit(); }
		    	//finally {
		          //  //if (LT.c1 != null) {try{LT.c1.close();} catch (IOException ex) {}
		          //}
	    		  //}
	        try { 
	        	
	        	//U.println(nRows);
	            
	        	//for(int i=0;i<nRows;i++)
	        	for(int i=from;i<to;i++)
	        	{ 
	                Date timestamp  = (Date)   kx.c.at(flip.y[0], i);
	            	double open     = (double) kx.c.at(flip.y[1], i);
	                double high     = (double) kx.c.at(flip.y[2], i);
	                double low      = (double) kx.c.at(flip.y[3], i);
	                double close    = (double) kx.c.at(flip.y[4], i);
	                double volume   = (double) kx.c.at(flip.y[5], i);
	                OHLCDataItem item = new OHLCDataItem(timestamp, open, high, low, close, volume);
	               // FixedMillisecond fm = new FixedMillisecond((long) c.at(flip.y[0], i));
	               // series.add(fm, open,high,low,close);
	                dataItems.add(item);
	            }	        
      	    } catch (Exception e) { logger.fatal(e); }

	        Collections.reverse(dataItems);
	        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
	        //axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
	        return new DefaultOHLCDataset(symbol, data);
	    }
	    public SegmentedTimeline newWorkdayTimeline() {
	        SegmentedTimeline timeline = new SegmentedTimeline(
	            SegmentedTimeline.HOUR_SEGMENT_SIZE, 8, 16);
	        timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900()
	            + 8 * timeline.getSegmentSize());
	        timeline.setBaseTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
	        return timeline;
	    }
	 public int nRows(){
		 return nRows;
	 }
	 
}


