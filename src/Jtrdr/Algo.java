package Jtrdr;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import kx.c;
import kx.c.Flip;
import kx.c.KException;


class Algo {
	private Flip flip;
	private static KxTableModel model = new KxTableModel(); //must be static to be the same for all subclasses!
	private int nCols;
	private int nRows;
	private String[] header;	
    private int dir=0;
    private double pnl[];
    private double pnlp[];
    private double dpnl=0,tpnl=0;
    private int pos=0;int neg=0;
    private int crossmm = 0;
    private double sumabspnl=0;double pnlprc=0;double tpnlprc=1;
    private double posstart=0;
    private double offset=0.00;
    private double dpnlprc=0;
    private double count=0;
    private int trgup=0,trgdn=0;
    private double trgtarget=0.0;
    private double price=0; //price of trade
    private int numofday=0;
    private int chngbar=0;
    private double dlow=1e6;
    private double dhigh=0;
    private int finishbar=8;  //3m finish=8 //1m finish=12 //5m finish=10 //10m finish=7
    //calc in k: k) %': 3.72 6.6 11 16.17 21.49 29.97 45.52 69.20 109.67 147.54 210.78 315.35 635.94
    private int brkhilo=0; //0,1:brkhi,2:brklo
    private int brkfree=0; //1st bar tan brks free from avgmed
    private double llow=0;
    private double lhigh=1000000.0;
    private double prvmed=0;
    private int setnewbrkhi=0;
    private int setnewbrklo=0;
    private double bigbar=0.0;
    private double avgmedstp=0.0;
    private double medhi=0.0;
    private double medlo=0.0;
    private double firstavgmed=0.0;
    private int chng=0;
    private double pvol=0.0;
    private double nvol=0.0;
    private double psiz=0.0;
    private double nsiz=0.0;
    private double voli=0.0;
    private double brkhi=1000.0;
    private double brklo=0.0;
    private int barstart=0;
    private double maxameddist=0;
    private int ameddistdir=0;
    private double cdlow=1e6,cdhigh=0;
    private double prevmed=0.0;
    private double prevlmed=0.0; //prevdlastmed
    private double prevdcl=0.0;
    private double prevdhi=0.0;
    private double prevdlo=0.0;
    private int Numofbars=0;
    private int barSize=30;
    private String sym;
    private int qty=1;
    private LT lt;
    protected HistData hist;
    protected HistData hist1; //1min data for intrabar - useful for some algos
    protected Pnl pnlList;
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss");
    SimpleDateFormat sdfd = new SimpleDateFormat("yyyy.MM.dd");

    public Algo() throws KException, IOException{
    	lt=LT.getInstance();
    }
    public void chdir(int i,int setdir,double posbrk) throws IOException {
            pnl[i]=dir*(posbrk-posstart);
            price=posbrk;
            String qry;
            qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`"+((setdir==1?"MS":"ML"))+";"+1+";"+barSize+";"+count+")";
            //cout << qry + CR;
            lt.c3().ks(qry); 
            count++;
            dpnl+=pnl[i];
            sumabspnl+=Math.abs(pnl[i]);
            tpnlprc*=(1+pnl[i]/posstart);
            if(pnl[i]>0) pos++; else if(pnl[i]<0) neg++;
            posstart=posbrk;
            dir=-1*setdir;  //must use this if param has same name as class var!   
    }
    public void clpos(int i,double posbrk) throws IOException {
            pnl[i]= dir*(posbrk-posstart);
            price=posbrk;
            String qry;
            qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`"+((dir==1?"CL":"CS"))+";"+1+";"+barSize+";"+count+")";
            lt.c3().ks(qry); 
            count++;
            dpnl+=pnl[i];
            sumabspnl+=Math.abs(pnl[i]);
            tpnlprc*=(1+pnl[i]/posstart);
            if(pnl[i]>0) pos++; else if(pnl[i]<0) neg++;
            dir=0;    
    }
    public void oppos(int i,int setdir,double posbrk) throws IOException {
            pnl[i]=0.0;
            price=posbrk;
            posstart=posbrk;
            String qry;
            qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`"+(setdir==1?"L":"S")+";"+1+";"+barSize+";"+count+")";
            lt.c3().ks(qry); 
            count++;
            dpnl+=pnl[i];
            sumabspnl+=Math.abs(pnl[i]);
            tpnlprc*=(1+pnl[i]/posstart);
            if(pnl[i]>0) pos++; else if(pnl[i]<0) neg++;
            dir=setdir;    
    } 
    public void sodfunc(int i) throws IOException {
            String qry;
            if(num(i)==1){ //sod
                numofday++;
                crossmm = 0;
                prevmed= i==0?0.0:rmed(i-1);
                prevdcl= i==0?0.0:Cl(i-1);
                prevdhi= i==0?0.0:Hi(i-1);
                prevdlo= i==0?0.0:Lo(i-1);
                Numofbars=numofbars(i);
                finishbar=Numofbars;
                if(numofday>1){
                    prevdhi=Hi(i-numofbars(i-1));
                    prevdlo=Lo(i-numofbars(i-1));
                    for(int j=1;j<numofbars(i-1);j++){
                        if(Hi(i-numofbars(i-1)+j) > prevdhi) prevdhi=Hi(i-numofbars(i-1)+j);
                        if(Lo(i-numofbars(i-1)+j) < prevdlo) prevdlo=Lo(i-numofbars(i-1)+j);
                    }
                }
                barstart=0;
                posstart=Op(i);
                if(i==0) { //1st bar
                   dir=1;
                   price=Op(i);
                   qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`L;"+1+";"+barSize+";"+count+")";
                   lt.c3().ks(qry);
                   count++;
                }
                else{
                    if(Op(i)>=Cl(i-1)) {
                        dir=-1;
                        price=Op(i);
                        qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`S;"+1+";"+barSize+";"+count+")";
                        lt.c3().ks(qry);
                        count++;
                    }
                    else {
                        dir=1;
                        price=Op(i);
                        qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`L;"+1+";"+barSize+";"+count+")";
                        lt.c3().ks(qry);
                        count++;
                    }
                }
                dlow=Lo(i);
                dhigh=Hi(i);
                brkhilo=0;
                brkfree=0;
                llow=0;
                lhigh=1000000.0;
                prvmed=i==0?0.0:rmed(i-1);
                setnewbrkhi=0; //start new brk
                setnewbrklo=0; //start new brk
                bigbar=bsize(i);
                avgmedstp=0.0;
                medhi=med(i);medlo=med(i);
                firstavgmed=med(i);
                chng=0;
                pvol=0;nvol=0;
                psiz=0;nsiz=0;   
                brkhi=1000.0;
                brklo=0.0;
                maxameddist=max(ameddistup(i),ameddistdn(i));
                ameddistdir=ameddistup(i)>=ameddistdn(i)?1:-1;
            }
    }//end of sodfunc
    public void sodfunc1(int i) throws IOException { //no numofday++
                String qry;
                if(num(i)==1){ //sod
                        prevmed= i==0?0.0:rmed(i-1);
                        prevdcl= i==0?0.0:Cl(i-1);
                        prevdhi= i==0?0.0:Hi(i-1);
                        prevdlo= i==0?0.0:Lo(i-1);
                        Numofbars=numofbars(i);
                        finishbar=Numofbars;
                        if(numofday>1){
                            prevdhi=Hi(i-numofbars(i-1));
                            prevdlo=Lo(i-numofbars(i-1));
                            for(int j=1;j<numofbars(i-1);j++){
                                if(Hi(i-numofbars(i-1)+j) > prevdhi) prevdhi=Hi(i-numofbars(i-1)+j);
                                if(Lo(i-numofbars(i-1)+j) < prevdlo) prevdlo=Lo(i-numofbars(i-1)+j);
                            }
                        }
                        
                        barstart=0;
                        posstart=Op(i);
                        if(i==0) { // 1st bar
                           dir=1;
                           price=Op(i);
                           qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`L;"+1+";"+barSize+";"+count+")";
                           //cout << qry + CR; 
                           lt.c3().ks(qry);
                           count++;
                        }
                        else{
                            if(Op(i)>=Cl(i-1)) {
                                dir=-1;
                                price=Op(i);
                                qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`S;"+1+";"+barSize+";"+count+")";
                                //cout << qry + CR; 
                                lt.c3().ks(qry);
                                count++;
                            }
                            else {
                                dir=1;
                                price=Op(i);
                                qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`L;"+1+";"+barSize+";"+count+")";
                                lt.c3().ks(qry);
                                count++;
                            }
                        }
                        
                        dlow=Lo(i);
                        dhigh=Hi(i);
                        brkhilo=0;
                        brkfree=0;
                        llow=0;
                        lhigh=1000000.0;
                        prvmed=i==0?0.0:rmed(i-1);
                        setnewbrkhi=0; //start new brk
                        setnewbrklo=0; //start new brk
                        bigbar=bsize(i);
                        avgmedstp=0.0;
                        medhi=med(i);medlo=med(i);
                        firstavgmed=med(i);
                        chng=0;
                        pvol=0;nvol=0;
                        psiz=0;nsiz=0;   
                        brkhi=1000.0;
                        brklo=0.0;
                    //    maxameddist=fmax(ameddistup(i),ameddistdn(i));
                    //    ameddistdir=ameddistup(i)>=ameddistdn(i)?1:-1;
                }
    }//end of sodfunc1
    public void eodfunc(int i) throws IOException {   //eod
                    String qry;
                    if((i==nRows-1)||(num(i)>num(i+1))||(num(i)==finishbar)){
                       price=Cl(i);
                       if(dir==1) {
                           pnl[i]=Cl(i)-posstart;
                           qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`CL;"+1+";"+barSize+";"+count+")";
                           lt.c3().ks(qry);
                           count++;
                        }
                        else if(dir==-1) {
                           pnl[i]=posstart-Cl(i);
                           qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`CS;"+1+";"+barSize+";"+count+")";
                           lt.c3().ks(qry);
                           count++;
                        }
                        dpnl+=pnl[i]; tpnl+=dpnl;
                        sumabspnl+=Math.abs(pnl[i]);    
                        tpnlprc*=(1+pnl[i]/posstart);
                        if(pnl[i]>0) pos++; else if(pnl[i]<0) neg++;
                        dir=0;
                        dpnl=0;
                        count=0;
                    }
    } //end of eodfunc
    public void eodfunc2(int i) throws IOException {   //eod
                  String qry;
                  if((i==nRows-1)||(num(i)>num(i+1))){
                       price=Cl(i);
                       if(dir==1) {
                           pnl[i]=Cl(i)-posstart;
                           qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`CL;"+";"+1+";"+barSize+";"+count+";";
                           lt.c3().ks(qry);
                           count++;
                       }
                       else if(dir==-1) {
                           pnl[i]=posstart-Cl(i);
                           qry="algores,:("+sdf.format(Tm(i))+";"+numofday+";"+i+";"+num(i)+";`"+sym+";"+strf(price)+";"+strf(posstart)+";"+strf(pnl[i])+";`CS;"+";"+1+";"+barSize+";"+count+";";
                           lt.c3().ks(qry);
                           count++;
                       }
                       dpnl+=pnl[i]; tpnl+=dpnl;
                       sumabspnl+=Math.abs(pnl[i]);    
                       tpnlprc*=(1+pnl[i]/posstart);
                       if(pnl[i]>0) pos++; else if(pnl[i]<0) neg++;
                       dir=0;
                       dpnl=0;
                       count=0;
                  }
    } //end of eodfunc2
    public void eodfunc0(int i) throws IOException {   //eod for cl2op at bar1
     String qry;
     if ((num(i)==1)&&(dir!=0)) {
         price = Op(i);
         if (dir == 1) {
             pnl[i]= price - posstart;
             qry = "algores,:(" + sdf.format(Tm(i)) + ";" + numofday + ";" + i + ";" + num(i) + ";`"+sym + ";" + strf(price) + ";" + strf(posstart) + ";" + strf(pnl[i]) + ";`CL;" + 1 + ";" + barSize + ";" + count + ")";
             lt.c3().ks(qry);
             count++;
         }
         else if (dir == -1) {
             pnl[i]=posstart - price;
             qry = "algores,:(" + sdf.format(Tm(i)) + ";" + numofday + ";" + i + ";" + num(i) + ";`"+sym + ";" + strf(price) + ";" + strf(posstart) + ";" + strf(pnl[i]) + ";`CS;" + 1 + ";" + barSize + ";" + count + ")";
             lt.c3().ks(qry);
             count++;
         }
         dpnl += pnl[i]; tpnl += dpnl;
         sumabspnl += Math.abs(pnl[i]);
         tpnlprc *= (1 + pnl[i] / posstart);
         if (pnl[i]>0) pos++; else if (pnl[i]<0) neg++;
         dir = 0;
         dpnl = 0;
         count = 0;
     }
    } //end of eodfunc0
    public void inittab() throws KException, IOException {
        		pnlList=new Pnl(hist);
        		pnl=new double[nRows];
        		pnlp=new double[nRows];
                String qry="algores:([]time:();numofday:();barnum:();num:();stock:();price:();posstart:();pnl:();action:();algo:();step:();i:())";
                lt.c3().k(qry);
    }
    public void callQOutput() throws KException, IOException {
                lt.c3().k("system \"l algores.q\"");
                System.out.println("algo="+getClass().getName().substring(8)+" sym="+sym+" step="+barSize+" pos="+pos+" neg="+neg+" prc="+strf((double)pos/(pos+neg))+" sumpnl="+strf(tpnl)+" sumabspnl="+strf(sumabspnl)+" pnlprc="+strf(tpnl/sumabspnl)+" pnlx="+strf(tpnlprc));
    }
    static double round(double val) { //default 100
    	return (double)Math.round(val * 100d) / 100d;
    }
    static double roundn(double val, int num) {
        return (double)Math.round(val * Math.pow(10d,num)) / (Math.pow(10d,num));
    }
    
    public static int rowCount,totdays;
    
    public String strf(double x){
        return String.format( "%.2f",x);
    }
    public String strfx(double x,int y){
        return String.format( "%."+y+"f",x);
    }
    static void println(Object line) {
        System.out.println(line);
    }
    static void print(Object line) {
        System.out.print(line);
    }
    
    static void printlnx(Object... obj) { //print multiple objects in one line
       for(Object s:obj)
            System.out.print(s+" ");
       System.out.print("\n");
    }
    static void exit(Object line) {
        System.out.print(line);
        System.exit(1);
    }
    static void exit() {
        System.exit(1);
    }
    /**
     * max value of two
     * @return max of two values
     */
    public static double max(double m1,double m2) {
        return Math.max(m1,m2);
    }
    public static double min(double m1,double m2) {
        return Math.min(m1,m2);
    }
    public Date Tm(int rowIndex) {
        return (Date)c.at(flip.y[0], rowIndex);
    }
    public long Tmst(int rowIndex) {
        return U.date2timestamp((Date)c.at(flip.y[0], rowIndex));
    }
    public double Op(int rowIndex) {
        return (double)c.at(flip.y[1], rowIndex);
    }
    public double Hi(int rowIndex) {
        return (double)c.at(flip.y[2], rowIndex);
    }
    public double Lo(int rowIndex) {
        return (double)c.at(flip.y[3], rowIndex);
    }
    public double Cl(int rowIndex) {
        return (double)c.at(flip.y[4], rowIndex);
    }
    public double Vo(int rowIndex) {
        return (double)c.at(flip.y[5], rowIndex);
    }
    public int numofbars(int i) {
        int numofbars=0;
        for (int j=i;j<nRows;j++){
            if((j+1==nRows)||(num(j+1)<num(j))) { numofbars=num(j); break; }
        }
        return numofbars;
    }
    /**
     * mid price of a bar. If it is a 1m bar then it is (op+cl)/2 else it is the average of 1m bar mid prices for the bar 
     * @param rowIndex
     * @return the mid price
     */
    public double med(int rowIndex) {
        return (double)c.at(flip.y[6], rowIndex);
    }
    /**
     * average of 1min mid prices since the start of the trading day 
     * @param rowIndex
     * @return the average of 1min mid prices since the start of the trading day
     */
    public double rmed(int rowIndex) {
        return (double)c.at(flip.y[7], rowIndex);
    }
    public double mid(int rowIndex) {
        return minbar(rowIndex)+bsize(rowIndex)/2;
    }
    public double wsize(int rowIndex) {
        return ((double)c.at(flip.y[2], rowIndex)-(double)c.at(flip.y[2], rowIndex));
    }
    /**
     * size of bar in $0.0
     * @param rowIndex
     * @return the size of the bar in $
     */
    public double bsize(int rowIndex) {
        return Math.abs((double)c.at(flip.y[1], rowIndex)-(double)c.at(flip.y[4], rowIndex));
    }
    public double ameddistup(int i) {
        return (double)c.at(flip.y[2], i)-rmed(i);
    }
    public double ameddistdn(int i) {
        return rmed(i)-(double)c.at(flip.y[3], i);
    }
    public double meddistup(int i) {
        return (double)c.at(flip.y[2], i)-med(i);
    }
    public double meddistdn(int i) {
        return med(i)-(double)c.at(flip.y[3], i);
    }
    public double posneg(int rowIndex) {
        return Math.signum((double)c.at(flip.y[4], rowIndex)-(double)c.at(flip.y[1], rowIndex));
    }
    public int cover(int i) {
        return (Cl(i)>Hi(i-1)) ? 1 : (Cl(i)<Lo(i-1)) ? -1 : 0;
    }
    public int type(int rowIndex) {
        return ((double)c.at(flip.y[2], rowIndex)>(double)c.at(flip.y[2], rowIndex-1))&&((double)c.at(flip.y[3], rowIndex)<(double)c.at(flip.y[3], rowIndex-1))?3:((double)c.at(flip.y[2], rowIndex)>(double)c.at(flip.y[2], rowIndex-1))?1:((double)c.at(flip.y[3], rowIndex)<(double)c.at(flip.y[3], rowIndex-1))?2:4 ;
    }
    public boolean istyp33(int rowIndex) {
        //y[1],2,3,4 : ohlc 
        return ((((double)c.at(flip.y[2], rowIndex)>(double)c.at(flip.y[2], rowIndex-1))&&((double)c.at(flip.y[3], rowIndex)<(double)c.at(flip.y[3], rowIndex-1)))||
        (((double)c.at(flip.y[2], rowIndex)>(double)c.at(flip.y[2], rowIndex-1))&&((double)c.at(flip.y[4], rowIndex)<med(rowIndex-1)))||
        (((double)c.at(flip.y[3], rowIndex)<(double)c.at(flip.y[3], rowIndex-1))&&((double)c.at(flip.y[4], rowIndex)>med(rowIndex-1))));

    }
    public double maxbar(int rowIndex) { //mac op,cl
        return Math.max((double)c.at(flip.y[1], rowIndex),(double)c.at(flip.y[4], rowIndex));
    }
    public double minbar(int rowIndex) { //min op,cl
        return Math.min((double)c.at(flip.y[1], rowIndex),(double)c.at(flip.y[4], rowIndex));
    }
    public int tbar(int rowIndex) { //1 if > avg, -1 if <avg else 0
        return minbar(rowIndex)>med(rowIndex)? 1 :maxbar(rowIndex)<med(rowIndex) ? -1 : 0;
    }
    public int trbar(int rowIndex) { //1 if > avg, -1 if <avg else 0
        return minbar(rowIndex)>rmed(rowIndex)? 1 :maxbar(rowIndex)<rmed(rowIndex) ? -1 : 0;
    }
    public int twhisk(int rowIndex) { //1 if > avg, -1 if <avg else 0
        return Lo(rowIndex)>med(rowIndex)? 1 :Hi(rowIndex)<med(rowIndex) ? -1 : 0;
    }
    public int trwhisk(int rowIndex) { //1 if > avg, -1 if <avg else 0
        return Lo(rowIndex)>rmed(rowIndex)? 1 :Hi(rowIndex)<rmed(rowIndex) ? -1 : 0;
    }
    public int wday(int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(Tm(i));
        return c.get(Calendar.DAY_OF_WEEK);
    }
    public double prvmax(int rowIndex) {
        return 0.0;
    }
    public double prvmax2(int rowIndex) {
        return 0.0;
    }
    public double prvmin(int rowIndex) {
        return 0.0;
    }
    public double prvmin2(int rowIndex) {
        return 0.0;
    }
    /**
     * This function gives the current bar number e.g. for 30min bars between 1-13
     * @param rowIndex
     * @return the number of bar in the current day starting from bar 1
     */
    public int num(int rowIndex) { //convert to eg. 1-13 for 30min bars
        int ret=((int)Math.round((double)c.at(flip.y[8], rowIndex))+1);
    	return ret;            
    }
    
    public void getdata(String s1) throws KException, IOException{
   	Object result;
   	String[] s2= s1.split(" ");
   	sym=s2[0];
   	ArrayList<String> barSizes=new ArrayList<>(Arrays.asList("1","2","3","5","10","15","30"));
   	if((s2.length>1)&&(barSizes.contains(s2[1]))) barSize=Integer.parseInt(s2[1]);
       String qry="data:get `:../hist/"+sym;
       lt.c3().k(qry);
       qry="dat1:update rMedian:avgs Median by time.date from select time:\"z\"$date+minute,Open,High,Low,Close,Volume,Median from select"+
           " Open:first Open,High:max High,Low:min Low,Close:last Close,Volume:sum Volume,"+
           "Median:0.5*(sum (Open+Close))%"+barSize+" by 1 xbar time.date,"+barSize+" xbar time.minute from data";
       lt.c3().k(qry);
       qry="select time,Open,High,Low,Close,Volume,Median,rMedian,"+
           "num:?[time.date[i-1]<time.date[i];0;(time.time-09:30:00)%"+barSize+"*60000] from dat1";
           result=lt.c3().k(qry);

		flip=kx.c.td(result);
          	nCols=flip.y.length;
          	nRows=Array.getLength(flip.y[0]);
          //	double[]o1= (double[]) flip.y[1];
          	String[] header= flip.x;
            model.setFlip((c.Flip) lt.c3().k(qry));

    }
    
    public int barSize(){
    	return barSize;
    }
    public int nRows(){
    	return nRows;
    }
    public int nCols(){
    	return nCols;
    }
    public double posstart(){
    	return posstart;
    }
    public void posstart(double p1){
    	this.posstart=p1;
    }
    public String sym(){
    	return sym;
    }
    public void sym(String s1){
    	this.sym=s1;
    }
    public int qty(){
    	return qty;
    }
    public void qty(int quantity){
    	this.qty=quantity;
    }
    public KxTableModel model(){
    	return model;
    }
    public int dir(){
    	return dir;
    }
    public void dir(int d){
    	this.dir=d;
    }
    public void dirReverse(){
    	this.dir*=-1;
    }
    public double dlow(){
    	return dlow;
    }
    public double dhigh(){
    	return dhigh;
    }
    public void dlow(double d1){
    	dlow=d1;
    }
    public void dhigh(double d1){
    	dhigh=d1;
    }
    public int finishbar(){
    	return finishbar;
    }
    public int Numofbars(){
    	return Numofbars;
    }
    public int barstart(){
    	return barstart;
    }
    public void finishbar(int i){
    	finishbar=i;
    }
    public void barstart(int i){
    	barstart=i;
    }
    public int numOfBars(int numOfDay) {
    	if(numOfDay>=hist.totalDays()) {
    		U.println("numOfDay>=totalDays");
    		return hist.numOfBars().get(hist.totalDays()-1);
    	}
    	else
    		return hist.numOfBars().get(numOfDay);
    }
    public int dayNum(int numOfBar) {
    	return hist.dayNum().get(numOfBar);
    }
    public int barNum(int numOfBar) {
    	return hist.barNum().get(numOfBar);
    }
}

