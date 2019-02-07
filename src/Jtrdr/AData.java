package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;

import kx.c.KException;

public class AData {
	  private String sectyp;
	  private String sym;
	  private int aid;
	  private int bid;
	  private int sid;
	  private boolean frstbar; //true if algo supports it(set in constructor)
	  private int algo;
	  private int live; //0 1 2 (dont send,send inactive,send active)
	  private int dir=0;
	  private int pos=0;
	  private int qty=0;
	  private int step;
	  private int norders=0;
	  private int ntrades=0;
	  private int chng;
	  private double tgtprc=0.0;
	  private int cancelifnotfilled=-1; //bar it was triggered(to be cancelled in next)
	  private int oid;
	  private int lastoid=0; //use arraylist and check if oid is in list...
	  private int lastbarnum=0;
	  private int lastexecbar=0;
	  private int execAtBarend=0;
	  private String time1stbar;
	  private LT lt;
	  
	/////////////methods
	  public AData(AlgoParams c) throws KException, IOException{ //constructor
		  	lt=LT.getInstance();
		    algo=     c.num;
		    qty=      c.qty;
		    step=     c.step;
		    sym=      c.sym;
		    sectyp=   c.sectyp;
		    live=     c.live;
		    lt.aLastoid().add(0);

		    if(algo==1) frstbar=true; else frstbar=false; // call task4 if true
	  
	  }
	  public void sord(Order o){
		  
	  }
	  public int getnub(){
		   //if (SD[m_sid].Bs.size() == 0) return 0;
		    if (lt.SD().get(0).T().isEmpty()|| (sid<0)) return 0;
		    else return U.last(lt.SD().get(sid).Bs().get(bid).B()).nuc();
	  }
	  public int getbarsize(){ //num of bars in BarSeries B
		    return lt.SD().get(sid).Bs().get(bid).B().size();
	  }
	  public int gettilsod(){
		    return lt.SD().get(sid).Bs().get(bid).tilsod();
	  }
	  public int getprvopg(){
		    return lt.SD().get(sid).prvopg();		  
	  }
	  public int getprvpn(){
		    return lt.SD().get(sid).prvpn();
	  }
	  public int getprvtyp(){
		    return lt.SD().get(sid).prvtyp();
	  }
	  public int getopg(){
		    return lt.SD().get(sid).opg();
	  }
	  public double getprevdclose(){
		    return lt.SD().get(sid).prevdclose();
	  }
	  public double getdopencrs(){
		    return lt.SD().get(sid).dopencrs();		  
	  }
	  public double getprc(){
		    return lt.SD().get(sid).prc();		  
	  }
	  public double getlastprc(){
		    return lt.SD().get(sid).lastprc();		  
	  }
	  public double getprvprc(){
		    return U.last(lt.SD().get(sid).T(),1).price(); //get last last price (not prvhi/prvlo)		  
	  }
	  public double getdlow(){
	    if (lt.SD().get(0).T().isEmpty()||(sid<0)) return 0;
		    else return lt.SD().get(sid).dlow();
	  }
	  public double getdhigh(){	
		    if (lt.SD().get(0).T().isEmpty()||(sid<0)) return 0;
		    else return lt.SD().get(sid).dhigh();
	  }

	  // getters and setters
  
	public void sid(int i) {
		this.sid=i;
	}
	public void aid(int i) {
		this.aid=i;
	}
	public void ntradesInc() {
		this.ntrades++;
		
	}
	public void posChng(int i) {
		this.pos+=i;
	}
	public void dir(int i) {
		dir=i;
	}
	public void lastoid(int i) {
		this.lastoid=i;
	}
	public void live(int i) {
		this.live=i;
	}
	public void pos(int p) {
		this.pos=p;
	}
	public void qty(int q) {
		this.qty=q;
	}
	public String sectyp() {
		return sectyp;
	}
	public String sym() {
		return sym;
	}
	public int aid() {
		return aid;
	}
	public int bid() {
		return bid;
	}
	public void bid(int i) {
		this.bid=i;
	}
	public int sid() {
		return sid;
	}
	public boolean frstbar() {
		return frstbar;
	}
	public int algo() {
		return algo;
	}
	public int live() {
		return live;
	}
	public int dir() {
		return dir;
	}
	public int pos() {
		return pos;
	}
	public int qty() {
		return qty;
	}
	public int step() {
		return step;
	}
	public int norders() {
		return norders;
	}
	public int ntrades() {
		return ntrades;
	}
	public int chng() {
		return chng;
	}
	public double tgtprc() {
		return tgtprc;
	}
	public int cancelifnotfilled() {
		return cancelifnotfilled;
	}
	public int oid() {
		return oid;
	}
	public int lastoid() {
		return lastoid;
	}
	public int lastbarnum() {
		return lastbarnum;
	}
	public int lastexecbar() {
		return lastexecbar;
	}
	public int execAtBarend() {
		return execAtBarend;
	}
	public String time1stbar() {
		return time1stbar;
	}
	
//	  public bool symhasdata();
//	  public double roundx(double val);

}
