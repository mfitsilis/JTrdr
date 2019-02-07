package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;

import kx.c.KException;

/**
 * contains bars few vars 
 */
public class BarSeries {
	  private int bid;
	  private int sid;
	  private int step;
	  private int tilsod; //bars til sod //needed???
	  private ArrayList<Bar> B=new ArrayList<>(); //accessible to package for iterations!
	  ////////////////methods
	  public BarSeries(SData p,int step) {//constructor
		  this.sid=p.sid();
		  this.step=step; 
	  }
	  public void addBar() throws KException, IOException{
		  B.add(new Bar(this));
	  }
	  public int step(){
		  return step;
	  }
	  public String bs2String(){
		  return String.format("%d minute bars", step);
	  }
	public void bid(int i) {
		this.bid=i;	
	}
	public void tilsod(int i) {
		this.tilsod=i;
	}
	public int bid() {
		return bid;	
	}
	public int sid() {
		return sid;	
	}
	public int tilsod() {
		return tilsod;
	}
	public ArrayList<Bar> B() {
		return B;
	}
}
