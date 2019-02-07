package Jtrdr;

public class Ohlc {
		private long time;
		private double open;
		private double high;
		private double low;
		private double close;
		private double volume;
		Ohlc(long t,double o,double h,double l,double c,double v){
			setohlc(t,o,h,l,c,v);
		}
		public void setohlc(long t,double o,double h,double l,double c,double v){
			this.time=t;
			this.open=o;
			this.high=h;
			this.low=l;
			this.close=c;
			this.volume=v;		
		}
		public long time(){
			return time;
		}
		public double open(){
			return open;
		}
		public double high(){
			return high;
		}
		public double low(){
			return low;
		}
		public double close(){
			return close;
		}
		public double volume(){
			return volume;
		}
		public void time(long t){
			this.time=t;
		}
		public void open(double o){
			this.open=o;
		}
		public void high(double h){
			this.high=h;
		}
		public void low(double l){
			this.low=l;
		}
		public void close(double c){
			this.close=c;
		}
		public void volume(double v){
			this.volume=v;
		}
	}