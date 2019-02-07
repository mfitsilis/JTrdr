package Jtrdr;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import kx.c.KException;

public class Watchlist {
	final static Logger logger=Logger.getLogger(Watchlist.class);
	private final ArrayList<String> forexList=new ArrayList<>(Arrays.asList("EUR","GBP"));
	private ArrayList<String> symbols=new ArrayList<>();
	private ArrayList<String> types=new ArrayList<>();
	private ArrayList<Integer> numOfTradesPerSym=new ArrayList<>();
	private ArrayList<Instrument> instruments=new ArrayList<>();
	
	private LT lt;
	public Watchlist(LT lt) throws KException, IOException{
		this.lt=lt; //LT.getInstance();
	}
	/** must be synchronized cause it can be modified during execution */
	public /*synchronized*/ void addTrade(String s1,long t1,double p1,double v1) throws KException, IOException {
		int idx=symbols.indexOf(s1);
		if((idx>=0)&&(idx<instruments.size()))
			try {
			instruments.get(idx).updateInstrument(s1,t1,p1,v1);  //update trade
			}catch(Exception e) { logger.fatal(e); }
		else {
			instruments.add(new Instrument(s1,t1,p1,v1));
		}
	}
	/** must be synchronized cause it can be modified during execution */
	public /*synchronized*/ void addQuote(String s1,long t1,double b1,double a1,int type) throws KException, IOException {
		int idx=symbols.indexOf(s1);
		if((idx>=0)&&(idx<instruments.size()))
			try {
			instruments.get(idx).updateInstrument(s1,t1,b1,a1,InstrumentType.CASH);  //update quote
			}catch(Exception e) { logger.fatal(e); }
		else {
			instruments().add(new Instrument(s1,t1,b1,a1,InstrumentType.CASH));	
		}
	}
	
	public void clearData(){
//		numOfTradesPerSym.clear();
//		zeroNumOfTradesPerSym();
//		instruments.clear();
//		lt.SD().clear();
	}

//	private void zeroNumOfTradesPerSym() {
//		for (int i=0;i<numOfTradesPerSym.size();i++) numOfTradesPerSym.set(i, 0); 
//	}
	
	public ArrayList<Instrument> instruments() {
		return instruments;
	}
	public ArrayList<String> symbols() {
		return symbols;
	}
	public void symbols(ArrayList<String> symbols) {
		this.symbols=symbols;
	}
	public ArrayList<String> types() {
		return types;
	}
	public void types(ArrayList<String> types) {
		this.types=types;
	}
	public ArrayList<Integer> numOfTradesPerSym() {
		return numOfTradesPerSym;
	}
}

// small bug: watchlist after stopped and restart sometimes displays same symbol in next line
// but is corrected in next tick!