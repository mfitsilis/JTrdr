package Jtrdr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import kx.c.KException;

public class CanvasCandleChart extends JComponent {
	private int mx=5; //margin x
	private int my=5;
	private int ycor=41,yvol=100;
	private int xcor=21;
	private int startBar,endBar,numofbars,selpos;
	private int top,bottom,left,right,rightborder=75;
	private JInternalFrame jfr;
	private Rectangle rect;
	private HistData hist;
	private double maxhigh,minlow;
	private Font font = new Font("Liberation Mono", Font.PLAIN, 12);
	private LT lt;
	public CanvasCandleChart(JInternalFrame jf,HistData h1,int startBar,int endBar,int selpos) throws KException, IOException {
		lt=LT.getInstance();
		jfr=jf;
		hist=h1;
		this.startBar=startBar;
		this.endBar=endBar;
		//this.numofbars=numofbars;
		this.selpos=selpos;
	}
	public void paint(Graphics g) {
	    //g.drawLine (10, 10, 387, 10); //0-397 (2 pixels on right end!)
	    //g.drawLine (15, 0, 15, 368); //0-368 (31 pixels missing at the bottom!)
		top=my;
		left=mx;
		rect=jfr.getBounds();
		bottom=rect.height-ycor;
		right=rect.width-xcor;
		drawFrame(g,rect,Color.black);
		drawFrameUnfilled(g,rect,Color.red);
		g.setFont(font);
	//	drawTextCol(g,"this is a test",70,20,Color.blue);
		g.setColor(Color.red);
		drawHorizSeparatorLine(g,rect);
				
		int nRows=hist.nRows();
		//int len=endBar-startBar+1; //numofbars;

		double maxvol=max(hist.volume(),startBar,endBar);
		maxhigh=max(hist.high(),startBar,endBar);
		minlow=min(hist.low(),startBar,endBar);
		drawYAxis(g,rect,(int)maxhigh,(int)minlow,100,1);

		long prevtime=hist.ohlc(startBar).time();
		for(int i=startBar;i<endBar;i++) {
			int siz=hist.size();
			double vol=hist.ohlc(i).volume();
			double op=hist.ohlc(i).open();
			double cl=hist.ohlc(i).close();
			drawCandle(g,i-startBar,endBar,hist.ohlc(i));
			drawVolume(g,vol,maxvol,i-startBar,endBar,(op>=cl?Color.red:Color.green));
			drawGrid(g,i-startBar,i,prevtime);
			drawSelector(g,selpos);
			showLegend(g);
			drawXAxis(g);
			prevtime=hist.ohlc(i).time();			

		}
	  }
	/////////end of candlechart
	  void showLegend(Graphics g) {
		  Ohlc o1=hist.ohlc(startBar+selpos);
		  Ohlc oStart=hist.ohlc(startBar);
		  Ohlc oEnd=hist.ohlc(endBar);
		  Ohlc olst=startBar+selpos-1<0?null:hist.ohlc(startBar+selpos-1); //prevdbar
		  int barsPerDay=(int)(390/hist.barSize());
		  Ohlc o0=startBar+selpos-1<0?null:hist.ohlc(startBar+selpos-1); //1stbar
		  drawTextCol(g,"O: "+o1.open(),		40,20,Color.yellow);
		  drawTextCol(g,"H: "+o1.high(),		40,40,Color.yellow);
		  drawTextCol(g,"L: "+o1.low(),		40,60,Color.yellow);
		  drawTextCol(g,"C: "+o1.close(),		40,80,Color.yellow);
		  drawTextCol(g,"V: "+o1.volume()*100, 40,100,Color.yellow);
		  drawTextCol(g,"T: "+ lt.WDAYS[U.timestamp2weekday(o1.time())]+" "+U.timestamp2datetimestr(o1.time(),false), 40,120,Color.yellow);
		  drawTextCol(g,"D: "+(1+startBar/barsPerDay)+"-"+((numofbars)/barsPerDay)+"/"+hist.nRows()/barsPerDay, 40,140,Color.yellow);
		  drawTextCol(g,"YD:"+U.timestamp2currentdayofyear(o1.time(), false)+" CW:"+U.timestamp2currentweekofyear(o1.time(), false), 40,160,Color.yellow);
		  drawTextCol(g,"lo-hi: "+minlow+"-"+maxhigh, 40,180,Color.yellow);
		  drawTextCol(g,"bar: "+(selpos+1), 40,200,Color.yellow);
		  drawTextCol(g,"bsize: "+String.format("%.2f",Math.abs(o1.close()-o1.open())), 40,220,Color.yellow);
		  drawTextCol(g,"wsize: "+String.format("%.2f",(o1.high()-o1.low())), 40,240,Color.yellow);
		  drawTextCol(g,"opg: "+((o0==null)?"":((o1.open()>=o0.close())?"pos":"neg")), 40,260,Color.yellow);
		  jfr.setTitle(startBar+" "+endBar+" "+hist.symbol()+" "+hist.barSize()+"min bars - "+
				  	lt.WDAYS[U.timestamp2weekday(oStart.time())]+" "+U.timestamp2datetimestr(oStart.time(),false)+" to "+
				  	lt.WDAYS[U.timestamp2weekday(oEnd.time())]+" "+U.timestamp2datetimestr(oEnd.time(),false));
	  }
	  void drawGrid(Graphics g,int pos,int i,long prevtime) {
		  	Graphics2D g2d = (Graphics2D) g;
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
			Stroke org=g2d.getStroke();
			g2d.setStroke(dashed); 
			if(hist.ohlc(i).time()-prevtime>2*86400*1000L) //longer than 2 days
		  		drawLineColDelta(g2d,pos*(1+15)+mx+4,my+1,0,bottom-2,Color.red);
		  	else if(hist.ohlc(i).time()-prevtime>hist.barSize()*60*1000L) // longer than 1 step (next day)
		  		drawLineColDelta(g2d,pos*(1+15)+mx+4,my+1,0,bottom-2,Color.blue);
		    g2d.setStroke(org);
	  }
	  void drawFrameUnfilled(Graphics g,Rectangle rect,Color c) {
		  drawRectColUnfilled(g,mx,my,rect.width-xcor,rect.height-ycor,c);  
	  }
	  void drawFrame(Graphics g,Rectangle rect,Color c) {
		  drawRectCol(g,mx,my,rect.width-xcor,rect.height-ycor,c);
	  }
	  void drawRectCol(Graphics g,double x,double y,double dx,double dy,Color c){
		  	g.setColor(c);
			g.fillRect ((int)x,(int) y,(int) dx,(int) dy);
	  }
	  void drawRectColUnfilled(Graphics g,double x,double y,double dx,double dy,Color c){
		  	g.setColor(c);				
			g.drawRect ((int)x,(int) y,(int) dx,(int) dy);
	  }
	  void drawText(Graphics g,String s1,int x,int y){
			g.drawString(s1, x, y);
		}
	  void drawTextCol(Graphics g,String s1,int x,int y,Color c){
		  	g.setColor(c);
			g.drawString(s1, x, y);
		}
	  void drawHorizSeparatorLine(Graphics g, Rectangle rect){
			g.drawLine(mx, rect.height-yvol-ycor, mx+rect.width-xcor, rect.height-yvol-ycor); 
	  }
	  void drawLineCol(Graphics g, int x1,int y1,int x2,int y2,Color c){
		  	g.setColor(c);
			g.drawLine(x1, y1, x2, y2); 
	  }
	  /** same as drawLineCol but uses dx,dy instead of x2,y2 */
	  void drawLineColDelta(Graphics g, int x,int y,int dx,int dy,Color c){
		  	g.setColor(c);
			g.drawLine(x, y, x+dx, y+dy); 
	  }
	  void drawYAxis(Graphics g, Rectangle rect,int maxhigh,int minlow,int maxvolume,int showVol){
			g.drawLine(rect.width-rightborder, my, rect.width-rightborder, my+rect.height-ycor); 
			int minstep=25; //creates many gridlines and only prints enough not to be too crowded!
			int numofsteps=(maxhigh-minlow)/minstep;
			
			int maxh=maxhigh;
			int minl=minlow;

			int minnumofsteps=5;
				
			int ySize=showVol==1?bottom-yvol:bottom-5;
			int yLow=showVol==1?yvol:5;

			double b=maxhigh-minlow>0?(double)ySize/(maxhigh-minlow):1; //go from maxmin to display coords.
			int a=maxhigh-(int)(bottom/b);

			maxhigh=maxhigh-maxhigh%minstep; //maxhigh closer to minstep multiple
			minlow=minlow-minlow%minstep; //maxhigh closer to minstep multiple

			int step=numofsteps<5?1:numofsteps/minnumofsteps; 

			for(int i=0;i<numofsteps;i++){ 
				
				if(((int)(maxhigh-i*minstep-a)*b>yLow)&&((int)(maxhigh-i*minstep-a)*b<bottom)&&(i%(step)==0)){  //horiz separator line check

					drawLineCol(g,right-rightborder/2+10-xcor,(int)((maxhigh+i*minstep-a)*b)+ycor-bottom,right-rightborder/2-xcor,ycor-bottom+(int)((maxhigh+i*minstep-a)*b),Color.red);

					String s1=String.format("%.2f",(double)(maxhigh-i*minstep));
					drawTextCol(g,s1,right-rightborder/2+10-xcor,ycor-bottom+(int)((maxhigh+i*minstep-a)*b+5),Color.yellow);
				}
			}
	}

	  void drawXAxis(Graphics g){
		  int barWidth=15;
		  int dx=1;						
		  int dt=52200;//+hist.step()*60;
		  for(int i=startBar;i<endBar;i++) {
			 if(i%5==0)//+i*hist.step()*60
				 drawTextCol(g,(""+(int)(1+((hist.time().get(i)/1000)%86400-dt)/(hist.barSize()*60))),left+(i-startBar)*(dx+barWidth)+mx,bottom-yvol+15,Color.yellow);
			 drawTextCol(g,(""+hist.type(i)),left+(i-startBar)*(dx+barWidth)+mx,bottom-yvol+30,Color.yellow);
		  }
	  }
			
	  void drawSelector(Graphics g, int pos){
		  	int barWidth=15;
		  	int dx=1;						
		  	Color c=Color.magenta;
			drawRectColUnfilled(g,left+pos*(dx+barWidth)-1+mx,2*my-2,barWidth+1,bottom-2*my+4,c);			
	  }
	  	  
	  void drawVolume(Graphics g,double volume,double maxvolume,int num,int totalnum,Color c){
		    // left+totnum*(dx+barWidth)= right-rightborder
		  	int barWidth=15;
		  	int dx=1;						
			int barHeight=(int)(95.0*volume/maxvolume); //
			drawRectCol(g,left+num*(dx+barWidth)+mx,bottom-barHeight,barWidth,barHeight,c);
	  }
	  void drawCandle(Graphics g,int num,int totalnum,Ohlc ohlc){
		  	// barHeight+(max-high)+(low-min)=bottom-yvol
		  	int barWidth=15;
		  	int dx=1;
		  			
		  	int showVol=1;
		  	int ySize=showVol==1?bottom-yvol:bottom-5;
		  	double b=maxhigh-minlow>0?(double)(ySize-3*my)/(maxhigh-minlow):1;
		  	int a=(int) maxhigh;


		  	int y0=3*my+(int)((a-Math.max(ohlc.open(),ohlc.close()))*b);
		  	int ysize=(int)(Math.abs(ohlc.close()-ohlc.open())*b); //bar
		  	int y1=3*my+(int)((a-ohlc.high())*b); // line
			int ysize1=(int)(Math.abs(ohlc.high()-ohlc.low())*b);
			Color c=ohlc.close()>=ohlc.open()? Color.green : Color.red;
		  	drawLineColDelta(g,left+num*(dx+barWidth)+mx+barWidth/2,y1,0,ysize1,Color.blue);
		  	if(ysize>1)
		  		drawRectCol(g,left+num*(dx+barWidth)+mx,y0,barWidth,ysize,c);
		  	else
		  		drawLineColDelta(g,left+num*(dx+barWidth)+mx,y0,barWidth,0,Color.yellow);
	  }
	  int y2screen(int y,int showVol){
			
			int screencoord;

			int ySize=showVol==1?bottom-yvol:bottom-5;
			double b=maxhigh-minlow>0?(double)ySize/(maxhigh-minlow):1; //go from maxmin to display coords.
			int a=(int)(maxhigh-bottom/b);

			screencoord=(int)((y-a)*b+14);

			return screencoord;
		}

	  public static double max(ArrayList<Double> dat,int from,int to) {
		  Double max=dat.get(from);
		  for (int i=from+1;i<=to;i++) {
			  if(max<dat.get(i)) max=dat.get(i);
		  }
		  return max; 
	  }	  
	  public static double min(ArrayList<Double> dat,int from,int to) {
		  Double min=dat.get(from);
		  for (int i=from+1;i<=to;i++) {
			  if(min>dat.get(i)) min=dat.get(i);
		  }
		  return min; 	  
	  }	  

	  //https://stackoverflow.com/questions/2683202/comparing-the-values-of-two-generic-numbers
//	  class NumberComparator<T extends Number & Comparable> implements Comparator<T> {
//
//		    public int compare( T a, T b ) throws ClassCastException {
//		        return a.compareTo( b );
//		    }
//		}
	  
	  //	  public static <T extends Comparable<T>> T max(ArrayList<T> dat,int from,int to) {
//		  T m0=dat.get(from);
//		  for (int i=from+1;i<to;i++) {
//			  if(max(m0,dat.get(i))!=m0) m0=dat.get(i);
//		  }
//		  return m0; 
//	  }
//	  public static <T extends Comparable<T>> T max(T a, T b) {
//		    if (a == null) {
//		        if (b == null) return a;
//		        else return b;
//		    }
//		    if (b == null)
//		        return a;
//		    return a.compareTo(b) > 0 ? a : b;
//	  }
}
