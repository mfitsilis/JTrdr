package Jtrdr;

import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import kx.c;
import kx.c.KException;

public class HistData {
	final static Logger logger=Logger.getLogger(HistData.class);

	private ArrayList<Ohlc> ohlcdata=new ArrayList<>();
	private int nCols;
	private int nRows;
	private ArrayList<String> columnNames=new ArrayList<>();
	private LT lt;
	//private Flip flip;
	private String qry;
	private KxTableModel model = new KxTableModel();
	private Object result;
	private int barSize;
	private String symbol;
	private ArrayList<Long> time=new ArrayList<>();
	private ArrayList<Double> open=new ArrayList<>();
	private ArrayList<Double> high=new ArrayList<>();
	private ArrayList<Double> low=new ArrayList<>();
	private ArrayList<Double> close=new ArrayList<>();
	private ArrayList<Double> volume=new ArrayList<>();
	private ArrayList<Integer> barNum=new ArrayList<>();
	private ArrayList<Integer> dayNum=new ArrayList<>();
	private ArrayList<Integer> numOfBars=new ArrayList<>();
	private int totalDays;
	
	public void ohlc(Ohlc c1){
		ohlcdata.add(new Ohlc(c1.time(),c1.open(),c1.high(),c1.low(),c1.close(),c1.volume()));		
	}
	/** get ohlc at position i */
    public Ohlc ohlc(int i){
    	return ohlcdata.get(i);
    }
    /** size of ohlcdata */
    public int size(){
    	return ohlcdata.size();
    	//return model.getRowCount();
    }
	
HistData(String s1,int barSize) throws KException, IOException {
		lt = LT.getInstance();
		this.barSize=barSize;
		this.symbol=s1;
		loadcompdata(s1);
		calcNumOfBar();
    };
    
    void loadcompdata(String s1) {
    	//qry= "get `:../hist/"+s1+"_eod";
    	qry=(barSize==0)? "get `:../hist/"+s1+"_eod" : "data:(get `:../hist/"+s1+");ohlc3 "+barSize;
        try { //connect to kdb
        	result=lt.c3().k(qry);
//            model.setFlip((c.Flip) lt.c1().k(qry));
        	model.setFlip((c.Flip) result);
        } 
        catch (Exception e) { logger.fatal(e); U.exit(); }
        
        nCols = model.getColumnCount();
        nRows = model.getRowCount();
        
        for(int i=0;i<nCols;i++){
        	columnNames.add(model.getColumnName(i));
        }        
       // U.println(nRows);
        long t;
        double o,h,l,c,v;
        for (int i = 0; i < nRows; i++) {
          t=((java.util.Date)model.getValueAt(i, 0)).getTime();
          o=(Double)model.getValueAt(i, 1);
          h=(Double)model.getValueAt(i, 2);
          l=(Double)model.getValueAt(i, 3);
          c=(Double)model.getValueAt(i, 4);
          v=(Double)model.getValueAt(i, 5);
     
      	time.add(t);
        open.add((Double)model.getValueAt(i, 1));
        high.add((Double)model.getValueAt(i, 2));
        low.add((Double)model.getValueAt(i, 3));
        close.add((Double)model.getValueAt(i, 4));
        volume.add((Double)model.getValueAt(i, 5));
        ohlcdata.add(new Ohlc(t,o,h,l,c,v));
             
        }
    }
    public int nRows(){
    	return nRows;
    }
    public Object result(){
    	return result;
    }
    public ArrayList<Long> time(){
    	return time;
    }
    public ArrayList<Double> open(){
    	return open;
    }
    public ArrayList<Double> high(){
    	return high;
    }
    public ArrayList<Double> low(){
    	return low;
    }
    public ArrayList<Double> close(){
    	return close;
    }
    public ArrayList<Double> volume(){
    	return volume;
    }
    public ArrayList<Integer> barNum(){
    	return barNum;
    }
    public ArrayList<Integer> dayNum(){
    	return dayNum;
    }
    public ArrayList<Integer> numOfBars() {
    	return numOfBars;
    }
    public int barSize() {
    	return barSize;
    }
    
    public String symbol() {
    	return symbol;
    }
    public int type(int i) {
    	if(i>0)
    		return (high.get(i)>high.get(i-1)&&low.get(i)<low.get(i-1))?3:
    				(high.get(i)>high.get(i-1)&&low.get(i)>=low.get(i-1))?1:
    				(high.get(i)<high.get(i-1)&&low.get(i)<=low.get(i-1))?2:4;
    	else return 0;
    }
    /** calculates dayNum (>=1) for every bar
     * 	numOfBar is number of bars per day (eg. for 1min either 390 or 210), one entry per day
     * 	Period.between is used to check if it's a different date
     *  barNum is number of intraday bars (eg. 1..390 for 1min bars), for every bar
     *  totalDays is total number of days
     * */
    public void calcNumOfBar(){
    ////update barNum:1+(time.time-09:30)%60000*barSize from ohlc3 barSize
      long bnum0=time.get(0);
      long bnum=1;
      int dnum=1;
      long lasttdif=0;
      Period period;
      barNum.add((int)bnum);
	  for (int i=1;i<nRows;i++){
		  long tdif=Period.between(U.timestamp2date(time.get(i),false),U.timestamp2date(time.get(i-1),false)).getDays();
		  bnum++;
		  dayNum.add((int)dnum);
		  if(tdif!=0) { //new day
  			  bnum=1;
			  dnum++; 
			  numOfBars.add(barNum.get(i-1));
		//	  U.println(tdif+" "+i+" "+bnum0+" "+bnum+" "+time.get(i)+" "+barNum.get(i-1));
	  	  }
		  barNum.add((int)bnum);		  
		  lasttdif=tdif;
	  }
	  dayNum.add((int)dnum);
	  numOfBars.add(barNum.get(nRows-1));
  	  totalDays=dnum;
    }
    public int totalDays() {
    	return totalDays;
    }
}

