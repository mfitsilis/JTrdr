package Jtrdr;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kx.c.KException;

/**
 * NOT USED
 * contains array os ticks, syms and sizes
 */
public class Tns {
	//https://stackoverflow.com/questions/1005073/initialization-of-an-arraylist-in-one-line
	private final ArrayList<String> forexList=new ArrayList<>(Arrays.asList("EUR","GBP"));
	private ArrayList<Integer> size=new ArrayList<>();
	private ArrayList<String> sym=new ArrayList<>();
	private ArrayList<Tic> ticks=new ArrayList<>();
	private LT lt;
	public Tns(LT lt) throws KException, IOException{
		this.lt=lt; //LT.getInstance();
	}
	private class Tic {
		private String sym;
		private ArrayList<Tick> t =new ArrayList<>();
		public Tic(Tick t1,String s1){
			add(t1,s1);
		}
		public void add(Tick t1,String s1){
			t.add(t1);
			sym=s1;
		}
		public void set(Tick t1,int i,String s1){
			t.set(i,t1);	//
			sym=s1;
		}
		public Tick get(int i){
			return t.get(i); 	//
		}
		public int size(){
			return t.size();
		}
		public ArrayList<Tick> ticks(){
			return t;
		}
		public Tick getlast(){
			return U.last(t);
		}
		public String getsym(){
			return sym;
		}
	}
	public void addTick(Tick T,String s1){
		int idx=sym.indexOf(s1);
		if(idx==-1){
			sym.add(s1);
			ticks.add(new Tic(T,s1));
			size.add(1);
		} else {
			ticks.get(idx).add(T,s1);	//
			size.set(idx,size.get(idx)+1);  //
		}
	}
	public String get(int i,int idx){ //idx:sym, i:Tick id
		long Time=ticks.get(idx).get(i).time();    //
		String Sym=sym.get(idx);				   //
		double prc=ticks.get(idx).get(i).price();  //
		double vol=ticks.get(idx).get(i).volume(); //
		//double mln=prc*vol/1e4;
		if(forexList.indexOf(Sym)!=-1)
			return String.format("%-6s %8s %7.5f %7.0f", Sym,U.timestamp2timestr(Time,lt.pbflag()),prc,vol);
		else
			return String.format("%-6s %8s %7.2f %7.0f", Sym,U.timestamp2timestr(Time,lt.pbflag()),prc,vol);
	}
//	public String get(int i,int idx){
//		if(idx<ticks.size()){
//			if(i<=ticks.get(idx).size()){ // <= ?
//				double prc=ticks.get(idx).get(i).price();
//				double vol=ticks.get(idx).get(i).volume();
//				double mln=prc*vol/1e4;
//				return String.format("%-6s %8s %7.2f %7.0f %4.0f", 
//						sym.get(idx),U.timestamp2timestr(ticks.get(idx).get(i).time(),lt.pbflag()),
//						prc,vol,mln);
//			}
//			else return "";
//		}
//		else return "";
//	}
	public String getlast(int idx){
		if(idx<ticks.size()){
			long Time=ticks.get(idx).getlast().time();    //
			String Sym=sym.get(idx);				   //
			double prc=ticks.get(idx).getlast().price();
			double vol=ticks.get(idx).getlast().volume();
			//double mln=prc*vol/1e4;
			if(forexList.indexOf(Sym)!=-1)
				return String.format("%-6s %8s %7.5f %7.0f",Sym,U.timestamp2timestr(Time,lt.pbflag()),prc,vol);
			else
				return String.format("%-6s %8s %7.2f %7.0f",Sym,U.timestamp2timestr(Time,lt.pbflag()),prc,vol);
//					ticks.get(idx).getlast().price(),ticks.get(idx).getlast().volume());
		}
		else return "";
	}
	public int getsize(int i){
		if(i<size.size()) //if(i<ticks.size())
			
			return size.get(i); //
		else return -1;
	}
	public String getlastsym(){
		return U.last(ticks).getsym();
	}
	public ArrayList<Tick> getPricebyIdx(int idx){
		return ticks.get(idx).ticks();
	}

	public Tick getTickBySym(String s1){
		int idx=sym.indexOf(s1);
		return U.last(ticks.get(idx).t); //
	}
	public ArrayList<String> getSyms(){
		return sym;
	}
	public Tick getLastTickbyIdx(int i){
		return (ticks.get(i).getlast()); //
	}
	public ArrayList<Tick> getTicksbyIdx(int i){
		return (ticks.get(i).t); //
	}
	public void clearData(){
		size.clear();
		sym.clear();
		ticks.clear();
	}
	//public long lastTime() {
	//	return (ticks.size()==0)?0:U.last(ticks).getlast().time();
	//}
}
