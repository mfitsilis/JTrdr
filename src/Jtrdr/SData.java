package Jtrdr;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import kx.c.KException;
/**
 * every property that relates to the symbol, including all data like bars, ticks and algos
 */
public class SData {
	  private SymStep St;
	  private String sym;
	  private int sid;
	  private boolean mocmerge = false;
	  private boolean  opgmerge = false;
	  private boolean symhasdata = false;
	  private double diviamount=0.0;
	  private int tilnextearn;
	  private int tilnextdivi;
	  private double prevdclose=0.0;
	  private double dopencrs=0.0;
	  private int tildivi;
	  private int tilearn;
	  private int setDhiDlo = 0;
	  private double dlow;
	  private double dhigh;
	  private double prc;
	  private double lastprc;
	  private double dvol;
	  private double oldvol;
	  private double newvol;
	  private int started;
	  private boolean typesset;
	  private int prvopg;
	  private int prvpn;
	  private int prvtyp; 
	  private int opg;
	  private ArrayList<BarSeries> Bs=new ArrayList<>(); //1 for every step, Bs[0] is default 1m
	  private ArrayList<Tick> T=new ArrayList<>();
	  private ArrayList<Integer> Aid=new ArrayList<>();
	  private LT lt;
	  ///////////methods
	  public SData(SymStep st) throws KException, IOException{ //constructor
		  this.St=st;
		  lt=LT.getInstance();
		  this.prc=0.0;
		  this.lastprc=0.0;
		  this.dvol=0.0;
		  this.oldvol=0.0;
		  this.started=0;
		  this.typesset=false;
		  this.sym = st.sym();
		    int idx = 0;
		    for (AlgoParams c : lt.Algos()) { //populate aid ArrayList
		        //if((c.sym==this->sym)||(this->aid.indexOf(idx)==-1)) this->aid.add(idx);  //add adata id if not in list
		        if (c.sym == sym) { 
		            Aid.add(idx);
		        } //add adata id if not in list
		        idx++;
		    }
	  }
	  public void createBs(String sym) throws KException, IOException{
		  if(!Bs.isEmpty()) Bs.get(0).B().clear(); //is empty if Bs[0] is not initialized
		    int s = 0; //always add 1m bar 1st as default
		    for (int i = 0; i < (St.Steps()).size()+1;i++){ // s: St.Steps) {  //initialize step,bid in barseries
		        s = (i == 0) ? 1 : St.Steps().get(i-1); //1st step is 1 others are as in St.Steps vector
		        Bs.add(new BarSeries(this,s));
		        U.last(Bs).bid(Bs.size()-1);
		        U.last(Bs).addBar();
		        LocalTime lt1=LocalTime.parse(U.timestamp2timestr(lt.tmst(),lt.pbflag()));
                LocalTime lt2=LocalTime.parse(LT.SODSTR);
                long t1 = U.secsSinceMidnight(lt1);
		        long t2 = U.secsSinceMidnight(lt2);
		        U.last(Bs).tilsod( (int)((t2 >= t1 ? 1 : 0) + (t2 - t1) / 60 / s) ); //tilsod: depends if t2>t1 !!! //calc tilsod for all Bs !!!
		    }
		    for (BarSeries bs : Bs) {
		        for (AData a : lt.A()) {
		            if ((a.step() == bs.step()) && (a.sym()==lt.SD().get(bs.sid()).sym)) { //sym,step combo is not necessarily unique for an algo!
		                a.bid( bs.bid());
		                a.sid( bs.sid());
		            }
		        }
		    }
	  }
	  public void sendopg(){
		  
	  }
	  public void sendmoc(){
		  
	  }
	  
	  // getters and setters
	  public String sym(){
		  return sym;
	  }
	public ArrayList<Tick> T() {
		return T;
	}
	public int sid() {
		return sid;
	}
	public void sid(int i) {
		this.sid=i;
	}
	public double dopencrs() {
		return dopencrs;
	}
	public double prc() {
		return prc;
	}
	public double lastprc() {
		return lastprc;
	}
	public double dlow() {
		return dlow;
	}
	public double dhigh() {
		return dhigh;
	}
	public int prvopg() {
		return prvopg;
	}
	public int prvtyp() {
		return prvtyp;
	}
	public int opg() {
		return opg;
	}
	public void prevdclose(double d) {
		this.prevdclose=d;
	}
	public int prvpn() {
		return prvpn;
	}
	public void diviamount(double d) {
		this.diviamount=d;
	}
	public double prevdclose() {
		return prevdclose;
	}
	public double diviamount() {
		return diviamount;
	}
	public void prc(Double p1) {
		this.prc=p1;
	}
	public void newvol(Double p1) {
		this.newvol=p1;
	}
	public int started() {
		return started;
	}
	public void started(int i) {
		this.started=i;
	}
	public void dvol(double d) {
		this.dvol=d;
	}
	public double dvol() {
		return dvol;
	}
	public double newvol() {
		return newvol;
	}
	public double oldvol() {
		return oldvol;
	}
	public void symhasdata(boolean b) {
		this.symhasdata=b;
	}
	public boolean symhasdata() {
		return symhasdata;
	}
	public void oldvol(double d) {
		this.oldvol=d;
	}
	public void dopencrs(double d) {
		this.dopencrs=d;
	}
	public boolean typesset() {
		return typesset;
	}
	public void dhigh(double d) {
		this.dhigh=d;
	}
	public void dlow(double d) {
		this.dlow=d;
	}
	public void lastprc(double p) {
		this.lastprc=p;
	}
	public int setDhiDlo() {
		return setDhiDlo;
	}
	public void setDhiDlo(int i) {
		this.setDhiDlo=i;
	}
	public boolean opgmerge() {
		return opgmerge;
	}
	public boolean mocmerge() {
		return mocmerge;
	}
	public void prvopg(int i) {
		this.prvopg=i;
	}
	public void prvpn(int i) {
		this.prvpn=i;
	}
	public void prvtyp(int i) {
		this.prvtyp=i;
	}
	public void typesset(boolean b) {
		this.typesset=b;
	}
	public ArrayList<BarSeries> Bs() {
		return Bs;
	}
	public ArrayList<Integer> Aid() {
		return Aid;
	}

	
}
