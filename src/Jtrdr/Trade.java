package Jtrdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Trade {
	private String sym;
	private String action; // BUY/SELL
    private int dir; // 1/-1
    private int barSize;
    private int barOpen;
    private int barClose;
    private long tmstOpen;
    private long tmstClose;
    private int qty; // >0 long, <0 short  // same as position
    private double prcOpen;
    private double prcTarget;
    private double prcClose;
    private double pnl;
    private double pnlFactor;
    
    public Trade(int barOpen,String action,int qty,String sym,double prcOpen){ //openPosition
    	this.sym=sym;
    	this.action=action;
    	assert(qty!=0);
    	dir = (int) Math.signum(qty);
    	this.barOpen=barOpen;
        this.prcOpen=prcOpen;
        this.qty=qty;    
    }
    public void closePosition(int barClose,double prcClose){ //closePosition
        this.barClose=barClose;
        this.prcClose=prcClose;
        this.pnl=(this.prcClose-this.prcOpen)*this.qty;
        this.pnlFactor=100*this.pnl/this.prcOpen;
    }
    public double pnl(){  // calculated when closing position
        return pnl;
    }
    public double unrealizedPnl(double currentPrc){
    	return (currentPrc-this.prcOpen)*this.qty;
    }
    public int positionDuration(){
        return (barClose-barOpen)*barSize;
    }
    public int dir(){
        return dir;
    }
    public int qty(){
    	return qty;
    }
    public int position(){ //alias for qty
    	return qty;
    }
    public String action(){
    	return action;
    }
    public double prcOpen(){
    	return prcOpen;
    }
    public double prcClose(){
    	return prcClose;
    }
}

//when reversing -> close open position first!
class Pnl {
  private String sym;
  private int barSize;
  private String algoName;
  private int Algonum;
  private HistData hist;
  private ArrayList<Trade> trades=new ArrayList<>();
  private ArrayList<Double> dayPnl;
  private ArrayList<Double> dayPnlFactor;
  private ArrayList<Integer> dayTrades; //trades per day
  private ArrayList<Integer> dayPosTrades;
  private ArrayList<Integer> dayNegTrades;
  private int posDays;
  private int negDays;
  private int totalDays;
  private int posTrades;
  private int negTrades;
  private double sumPnl;
  private double sumAbsPnl;
  private double pnlFactor=1.0;
  
  public Pnl(HistData hist){
	  int totalDays=hist.totalDays();
	  this.sym=hist.symbol();
	  dayPnl=new ArrayList<>(Collections.nCopies(totalDays, 0.0));
	  dayPnlFactor=new ArrayList<>(Collections.nCopies(totalDays, 1.0));
	  dayTrades=new ArrayList<>(Collections.nCopies(totalDays, 0));
	  dayPosTrades=new ArrayList<>(Collections.nCopies(totalDays, 0));
	  dayNegTrades=new ArrayList<>(Collections.nCopies(totalDays, 0));
	  this.hist=hist;
  }
  public void openPosition(int barNum,long tmst,String action,int qty,String sym,double prcOpen){
	  trades.add(new Trade(barNum,action,qty,sym,prcOpen));
	  Trade trd=U.last(trades);
	  //U.printlnx(U.timestamp2datetimestr(tmst,false),action,trd.dir(),sym,U.strf(trd.prcOpen()));
  }
  public void closePosition(int numOfTrade,int barNum,long tmst,double prc){
	  Trade trd=trades.get(numOfTrade);
      trd.closePosition(barNum,prc);
	  //U.printlnx(U.timestamp2datetimestr(tmst,false),trd.action(),trd.dir(),sym,U.strf(trd.prcClose()));
      calcDayPnl(barNum,numOfTrade);
  }
  public void reversePosition(int numOfTrade,int barNum,long tmst,double prc){
	  Trade trd=trades.get(numOfTrade);
	  trd.closePosition(barNum,prc);
      calcDayPnl(barNum,numOfTrade);
	  String action=trd.action().equals("BUY")?"SELL":"BUY"; //reverse action
	  int qty=-1*trd.qty();
	  trades.add(new Trade(barNum,action,qty,sym,prc));
	  trd=trades.get(numOfTrade+1);
	  //U.printlnx(U.timestamp2datetimestr(tmst,false),trd.action(),trd.dir(),sym,U.strf(trd.prcOpen()));
  }
  public int numOfDay(int barNum){ //assumption no gaps, same barSize, also array of num
	  return hist.barNum().get(barNum);
  }
  public void incDayTrades(int numOfDay) {
	  int oldNum=dayTrades.get(numOfDay);
	  oldNum++;
	  dayTrades.set(numOfDay,oldNum);
  }
  public void incDayNegTrades(int numOfDay) {
	  int oldNum=dayNegTrades.get(numOfDay);
	  oldNum++;
	  dayNegTrades.set(numOfDay,oldNum);
  }
  public void incDayPosTrades(int numOfDay) {
	  int oldNum=dayPosTrades.get(numOfDay);
	  oldNum++;
	  dayPosTrades.set(numOfDay,oldNum);
  }
  public int lastTradeNum() {
	  return trades.size()-1;
  }
  public Trade lastTrade() {
	  return U.last(trades);
  }
  public void calcDayPnl(int barNum,int numOfTrade){ //calc statistics trades/day statistics
	  int numOfDay=numOfDay(barNum)-1; //because daynums are 1,2,3,...
	  double oldPnl=dayPnl.get(numOfDay);
	  double oldPnlFactor=dayPnlFactor.get(numOfDay);
	  double pnl=trades.get(numOfTrade).pnl();
	  double prcOpen=trades.get(numOfTrade).prcOpen();
	  double pnlPrc=pnl/prcOpen;
	  this.sumPnl+=pnl;
	  this.sumAbsPnl+=Math.abs(pnl);
	  if(dayTrades.get(numOfDay)==0) { //1st trade of the day
		  if(pnl>=0) {
			  this.posDays++;
		  } else {
			  this.negDays++;
		  }
	  } else {
		  if((oldPnl<0)&&(oldPnl+pnl>=0)) {
			  this.posDays++;
			  this.negDays--;
		  } else if((oldPnl>=0)&&(oldPnl+pnl<0)) {
			  this.posDays--;
			  this.negDays++;
		  }
	  }
	  this.totalDays=posDays+negDays;
	  if(pnl>=0) {
		  this.posTrades++;
		  this.incDayPosTrades(numOfDay);
	  } else {
		  this.negTrades++;
		  this.incDayNegTrades(numOfDay);
	  }
	  dayPnl.set(numOfDay(barNum),oldPnl+pnl);
	  this.pnlFactor*=1+pnlPrc;
	  dayPnlFactor.set(numOfDay(barNum),(oldPnlFactor*(1+pnlPrc)));
	  incDayTrades(numOfDay);
  }
  public double dayPnlPrc(){
	  return posDays/totalDays;
  }
  public int posTrades(){
	  return posTrades;
  }
  public int negTrades(){
	  return negTrades;
  }
  public int posDays(){
	  return posDays;
  }
  public int negDays(){
	  return negDays;
  }
  public double sumPnl(){
	  return sumPnl;
  }
  public double sumAbsPnl(){
	  return sumAbsPnl;
  }
  public double pnlPrc(){
	  return sumPnl/sumAbsPnl;
  }
  public double pnlFactor(){	  
	  return pnlFactor;
  }
  public void algoName(String s1){	  
	  this.algoName=s1;
  }
  public void barSize(int b1){	  
	  this.barSize=b1;
  }
  public int lastDir(){	  
	  return U.last(trades).dir();
  }
  public void algoResults() { //create kdb table algores
	  //getClass().getName().substring(8)+" sym="+sym+" step="+step+" pos="+pos+" neg="+neg+" prc="+strf((double)pos/(pos+neg))+" sumpnl="+strf(tpnl)+" sumabspnl="+strf(sumabspnl)+" pnlprc="+strf(tpnl/sumabspnl)+" pnlx="+strf(tpnlprc));
	  U.println("algo="+algoName+" sym="+sym+" barSize="+barSize+" pos="+posTrades+" neg="+negTrades+" prc="+U.strf((double)posTrades/(posTrades+negTrades))+" sumpnl="+U.strf(sumPnl)+" sumabspnl="+U.strf(sumAbsPnl)+" pnlprc="+U.strf(sumPnl/sumAbsPnl)+" pnlx="+U.strf(pnlFactor())); 
  }
  
}