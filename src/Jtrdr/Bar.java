package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;

import kx.c.KException;

/**
 * for 1min and xmin bars
 */
public class Bar {
	  private int 	 bid;
	  private int 	 sid;
	  private int    cnt=0;          //count of 1min bars per xm bar
	  private int    cntinc;       //count of 1min bars total per xm bar - resets at sod
	  private int    totcnt;       //xm bars til sod - resets at sod
	  private int    nuc;
	  private Long   timestart;
	  private Long   timeend;
	  private double open, high=0, low=1000000, close, volume=0, phigh, plow;
	  private double med=0;
	  private double medsLsob=0;//last added to meds_sob
	  private double medsSob=0;//meds_sob sum avg 1m til sob -> divide by num
	  private double medsSod=0;//meds_sod sum avg 1m til sod -> divide by num
	  private double avgmedSob=0;
	  private double avgmedSod=0;
	  private ArrayList<Tick> T=new ArrayList<>();
	  private LT lt;
	  
	  //////////////methods
	  public int gettickscount(){
		    return T.size();
	  }
	  public double posneg() {//cl-op
		  return close - open > 0 ? 1 : (close - open < 0 ? -1 : 0);
	  }
	  public int    type(){
		    return (high>phigh) && (low<plow) ? 3 : (high>phigh) ? 1 : (low<plow) ? 2 : 4;
	  }
	  public double ameddistup(){ //hi-rmed
		    return high - avgmedSod;
	  }
	  public double ameddistdn(){ //rmed-lo
		  return avgmedSod - low;
	  }
	  public double meddistup(){ //hi-med
		  return high - avgmedSob;
	  }
	  public double meddistdn(){ //med-lo
		  return avgmedSob - low;
	  }
	  public void setBar(){ //bar update
		    SData pS = lt.SD().get(sid);
		    BarSeries pB = lt.SD().get(sid).Bs().get(bid);
		    T.add(U.last(pS.T()));
		    if (T.size() == 1) { //if 1st tick of bar
		        open = high = low = close = U.last(T).price();
		    }
		    if (U.last(T).price() > high) high = U.last(T).price();
		    if (U.last(T).price() < low) low = U.last(T).price();
		    close = U.last(T).price();
		    volume += U.last(T).volume();
		    //mov.avg. calc
		    if (bid == 0) { //if 1st 1min bar set all meds_sob,meds_sod
		        med = (open + close) / 2;
		        if (pB.B().size() >1) { //needed because it can be called in the 1st 1min bar!
		            Bar lbar = U.last(pS.Bs().get(bid).B(), 1); //prevprev - don't use same bar because it gets overwritten!
		            medsSod = (nuc == 0) ? med : lbar.medsSod + med;   //crashes here???
		        }
		    }
		    else {
		        Bar bar1 = U.last(pS.Bs().get(0).B()); //last 1m bar
		        medsSob += -medsLsob + bar1.med;
		        medsSod += -medsLsob + bar1.med;
		        medsLsob = bar1.med;
		    }
		    avgmedSob = medsSob / cnt;
		    avgmedSod = medsSod / cntinc;

	  }
	  public void setBar1m(){ //bar update for every 1min bar
		    SData pS = lt.SD().get(sid);
		    BarSeries pB = lt.SD().get(sid).Bs().get(bid);

		    cnt++;
		    cntinc++;
		    //mov.avg. calc
		    if (bid == 0) { //if 1st 1min bar set all meds_sob,meds_sod
		        med = medsSob = (open + close) / 2;
		        Bar lbar = U.last(pS.Bs().get(bid).B(), 1); //prevprev
		        medsSod = (nuc == 0) ? med : lbar.medsSod + med;
		    }
		    else {
		        Bar bar1 = U.last(pS.Bs().get(0).B()); //last 1m bar
		        medsSob += bar1.med;
		        medsSod += bar1.med;
		        medsLsob = bar1.med;
		    }
		    avgmedSob = medsSob / cnt;
		    avgmedSod = medsSod / cntinc;

	  }
	  public Bar() throws KException, IOException{
		  lt=LT.getInstance();
	  }
	  public Bar(BarSeries p) throws KException, IOException{ //constructor
		  						//new bar constructor
		  		lt=LT.getInstance();
		  		this.sid=p.sid();
		  		this.bid=p.bid();
		  		SData pS = lt.SD().get(sid);
		        BarSeries pB = lt.SD().get(sid).Bs().get(bid);
		        int t = pB.step() * 60000;
		        if (pB.B().size()>0) {
		            Bar lbar = U.last(pB.B());
		            cntinc = lbar.cntinc;
		            totcnt = lbar.totcnt;
		        }
	  	        if ((pS.prc() != 0)) { //only for 1st tick during createBs - only 1st bar
		            T.add(U.last(pS.T())); //add very 1st tick
		            open = high = low = close = U.last(T).price();
		            volume = U.last(T).volume();
		            timestart = U.last(T).time() - U.last(T).time()%t;
		            timeend = timestart + t;

		            cnt = 1;
		            cntinc = 1;
		            totcnt = 1;
		            nuc = (int)nuc / pB.step() + ((nuc < 1) ? -1 : 0); //only for > 1min before 09:30 add -1!
		            med = medsSob = medsSod = (open + close) / 2;
		            medsLsob = med;
		            avgmedSob = medsSob / cnt;
		            avgmedSod = medsSod / cntinc;
		        }
		        else { //(pS->Bs[bid].B.size()>0) { //pS->prc == 0  // if not needed - possible crash? - 2nd bar and later
		             //last xm bar - the current bar hasn't been pushed yet!       
		            Bar lbar = U.last(pB.B());
		            open = high = low = close = lbar.close;
		            volume = 0;
		            
		            timestart = lbar.timeend;
		            timeend = timestart + t;

		            nuc = lbar.nuc + 1;  //gaps in live trading are handled by 1min zero bar!
		            //nuc = (int)nuc / pB->step + (((nuc%pB->step == 0)&&(nuc < 0)) ? 0 : -1); //just as good as prev. one (gaps in playback do not work with either!)
		            totcnt = (nuc == 0) ? 1 : totcnt + 1;
		            cntinc = (nuc == 0) ? 0 : cntinc;

		            if (pB.B().size() > 0) {
		                medsSod = (nuc == 0) ? 0 : lbar.medsSod;
		            }
		        }

	  }
	  public String bar2String(){
		  return String.format("%s %.2f %.2f %.2f %.2f %.0f",U.timestamp2timestr(timestart,lt.pbflag()),open,high,low,close,volume);
	  }
	  
	  // getters and setters
	public int nuc() {
		return nuc;
	}
	public int getBid() {
		return bid;
	}
	public int sid() {
		return sid;
	}
	public int cnt() {
		return cnt;
	}
	public int cntinc() {
		return cntinc;
	}
	public int totcnt() {
		return totcnt;
	}
	public Long timestart() {
		return timestart;
	}
	public Long timeend() {
		return timeend;
	}
	public double open() {
		return open;
	}
	public double high() {
		return high;
	}
	public double low() {
		return low;
	}
	public double close() {
		return close;
	}
	public double volume() {
		return volume;
	}
	public double phigh() {
		return phigh;
	}
	public double plow() {
		return plow;
	}
	public double med() {
		return med;
	}
	public double medsLsob() {
		return medsLsob;
	}
	public double medsSob() {
		return medsSob;
	}
	public double medsSod() {
		return medsSod;
	}
	public double avgmedSob() {
		return avgmedSob;
	}
	public double avgmedSod() {
		return avgmedSod;
	}
	public ArrayList<Tick> T() {
		return T;
	}
	
}
