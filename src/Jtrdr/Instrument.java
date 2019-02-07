package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import kx.c.KException;

enum InstrumentType {STK,CASH};

public class Instrument {
	final static Logger logger=Logger.getLogger(Instrument.class);
	//private final ArrayList<String> forexList=new ArrayList<>(Arrays.asList("EUR","GBP"));
	private ArrayList<Tick> trades =new ArrayList<>(); //quotes ArrayList not needed as they are not plotted
	private String symbol;
	private InstrumentType type;
	//private int type=0; //0:stock, 1:forex, 2: future
	private double price=0.0;
	private long lastTradeTime=0L;
	private long lastQuoteTime=0L;
	private double bidPrice=0.0;
	private double askPrice=0.0;
	private double volume=0.0;
	private double dopen=0.0;
	private double dclose=0.0; // last trade of the day (rth)
	private double prevdclose=0.0;
	private int position=0;	//current position
	private double sharesOutstanding=0.0;
	private String exDivDay="";
	private String earningsDay="";
	private double averagePrice=0.0;
	private double unrealizedPnl=0.0;
	private double totalPnl=0.0;
	
	private LT lt;
	/** new trade 
	 * @throws IOException 
	 * @throws KException */
	public Instrument(String symbol,InstrumentType type,long lastTradeTime,double price,double volume,
							double dopen,double prevdclose,String exDivDay,String earningsDay,double sharesOutstanding) throws KException, IOException{
		lt=LT.getInstance();
		this.symbol=symbol;
		this.type=type;
		this.lastTradeTime=lastTradeTime;
		this.price=price;
		this.volume=volume;		
		this.dopen=dopen;
		this.prevdclose=prevdclose;
		this.prevdclose=prevdclose;
		this.exDivDay=exDivDay;
		this.earningsDay=earningsDay;
		this.sharesOutstanding=sharesOutstanding;
		trades.add(new Tick(lastTradeTime,price,volume));
	}
	/** new trade 
	 * @throws IOException 
	 * @throws KException */
	public Instrument(String symbol,long lastTradeTime,double price,double volume) throws KException, IOException{
		lt=LT.getInstance();
		this.symbol=symbol;
		this.lastTradeTime=lastTradeTime;
		this.price=price;
		this.volume=volume;		
		trades.add(new Tick(lastTradeTime,price,volume));
	}
	/** new quote 
	 * @throws IOException 
	 * @throws KException */
	public Instrument(String symbol,long lastQuoteTime,double bidPrice,double askPrice,InstrumentType type) throws KException, IOException{
		lt=LT.getInstance();
		this.symbol=symbol;
		this.lastQuoteTime=lastQuoteTime;
		if(bidPrice==0)
			this.askPrice=askPrice;
		else
			this.bidPrice=bidPrice;
	}
	/** update trade 
	 * @throws IOException 
	 * @throws KException */
	public void updateInstrument(String symbol,long lastTradeTime,double price,double volume) throws KException, IOException{
		this.symbol=symbol;
		this.lastTradeTime=lastTradeTime;
		this.price=price;
		this.volume=volume;	
		trades.add(new Tick(lastTradeTime,price,volume));
	}
	/** update quote */
	public void updateInstrument(String symbol,long lastQuoteTime,double bidPrice,double askPrice,InstrumentType type){
		this.symbol=symbol;
		this.lastQuoteTime=lastQuoteTime;
		if(bidPrice==0)
			this.askPrice=askPrice;
		else
			this.bidPrice=bidPrice;
	}
	public String getWatchListInstrument(int isQuoteTime) {
		double mln=price*volume/1e4;
		double change=(prevdclose==0.0)||(price==0)?0.0:price-prevdclose;
		double changePrc=(prevdclose==0.0)||(price==0)?0.0:100*(price-prevdclose)/prevdclose;
		double shares=price*sharesOutstanding/1e6;
		String sharesMil=shares>1000?String.format("%3.0fB",shares/1000):String.format("%3.0fM",shares);
		double spread=(askPrice<=0)||(bidPrice<=0)?0.0:askPrice-bidPrice;
		String formatQuote="%-6s %8s %7.5f %9.0f %5.0f %5d %7.5f %7.5f %7.5f %7.5f %7.5f %8.5f %6.2f%% %10s %10s %4s";
		String formatTrade="%-6s %8s %7.2f %9.0f %5.0f %5d %7.2f %7.2f %7.2f %7.2f %7.2f %8.2f %6.2f%% %10s %10s %4s";		//String formatWl=(forexList.indexOf(symbol)==-1)?formatTrade:formatQuote;
		String formatWl=type==InstrumentType.STK?formatTrade:formatQuote;
		String exdivday=exDivDay.equals(lt.datestr())?"----------":exDivDay;
		String earningsday=earningsDay.equals(lt.datestr())?"----------":earningsDay;
		long lastTime=lastTradeTime; //(isQuoteTime==0)?lastTradeTime:lastQuoteTime;
		return String.format(formatWl,  //lt.captureRunning() todo check!
				symbol,U.timestamp2timestr(lastTime,lt.pbflag()?!lt.pbflag():lt.pbflag()),price,volume,mln,trades.size(),
				bidPrice,askPrice,spread,dopen,prevdclose,change,changePrc,exdivday,earningsday,sharesMil);	
	}
	public String symbol() {
		return symbol;
	}
	public int numOfTrades() {
		return trades.size();
	}
	public String getTimeAndSalesOfInstrument(int i) {
		if(i>=trades.size()) logger.fatal("out of bounds");
		if(i<trades.size()) {
		long Time=trades.get(i).time();
		double Price=trades.get(i).price();
		double Volume=trades.get(i).volume();
		String formatTrade="%-6s %8s %7.2f %7.0f";
		String formatQuote="%-6s %8s %7.5f %7.0f";		//String formatWl=(forexList.indexOf(symbol)==-1)?formatTrade:formatQuote;
		String formatWl=type==InstrumentType.STK?formatTrade:formatQuote;
		return String.format(formatWl,symbol,U.timestamp2timestr(Time,lt.pbflag()?!lt.pbflag():lt.pbflag()),Price,Volume);
		} else return "";
	}
	public String getLastTimeAndSalesOfInstrument() {
		String formatTrade="%-6s %8s %7.2f %7.0f";
		String formatQuote="%-6s %8s %7.5f %7.0f";		//String formatWl=(forexList.indexOf(symbol)==-1)?formatTrade:formatQuote;
		String formatWl=type==InstrumentType.STK?formatTrade:formatQuote;
		return String.format(formatWl,symbol,U.timestamp2timestr(lastTradeTime,lt.pbflag()?!lt.pbflag():lt.pbflag()),price,volume);
	}
	public Tick lastTrade() {
		return U.last(trades);
	}
	public InstrumentType type() {
		return type;
	}
	public void symbol(String symbol) {
		this.symbol=symbol;
	}
	public ArrayList<Tick> trades() {
		return trades;
	}

}