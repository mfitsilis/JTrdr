package Jtrdr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import kiss.API;
import kx.c;
import kx.c.KException;
/**
 * only one instance of this exists in the program - singleton with lazy eval
 * the containers of the actual data are defined here 
 */
public class LT {
	final static Logger logger=Logger.getLogger(LT.class);

	//private ArrayList<String> symbols=new ArrayList<>();
    private boolean eodflag;
    private boolean marketopen;
    private boolean xth;
	private enum ph { pre,rth,post,closed }
	private ph phase;
	private long tmst;
	private long timeold;
	//private long tmstAtEnd1m;
	private int  tday;
	private int  tmonth;
	private int  tmday;
	private String todayHoliday;
	private String datestr;
	private String olddatestr;
	private String timestr;
	private String datestrStart;
	private String datetimestr;
	static final int WKND = 7;
	private int totMins  = 390;
	private int eodMins  = 960;
    private int daysrunning;
    	
	static final String POSTSTR  = "20:00:00";
	private static String eodstr   = "16:00:00";
	static final String EODNSTR  = "16:00:00";
	private static String mocstr   = "15:49:00";
	static final String MOCNSTR  = "15:49:00";
	static final String EODESTR  = "13:00:00"; //eod early
	static final String MOCESTR  = "12:49:00"; //moc early
	static final String REVSTR   = "09:37:59";
	static final String CRSOPSTR = "09:30:10";
	static final String SODSTR   = "09:30:00";
	static final String OPGSTR   = "09:27:00";
	static final String PRESTR   = "04:00:00";
	    
	public static final String[] MONTHS = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	public static final String[] WDAYS = {"Sat","Sun","Mon","Tue","Wed","Thu","Fri"};
	public static final String[] WEEKDAYS = { "Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday" };
	public static final String[] PHASES = { "pre","rth","post","closed" };
	public static final String[] HOLIDAYS = { "new_year_day","mlk_day","president_day","good_friday","memorial_day","independence_day","labor_day","thanksgiving_day","christmas_day"};
	
	private ArrayList<AlgoParams> Algos = new ArrayList<>();
	private ArrayList<String>Symlist = new ArrayList<>(); //all unique syms
	private ArrayList<String>Pubsubstr = new ArrayList<>();
	private ArrayList<AData>      A = new ArrayList<>();
	private ArrayList<Integer>    aLastoid = new ArrayList<>();
	private ArrayList<SData>      SD = new ArrayList<>();
	private ArrayList<SymStep>    AlgoStep = new ArrayList<>();
	private ArrayList<Order> Torder = new ArrayList<>();
	private String Salgoparams="";
	private String Sorders="";
	private ArrayList<String> pubsubstr =new ArrayList<>();
	
	private boolean captureRunning = false;
	private Watchlist wl;

	private long lastTime=0;
	private boolean pbflag=false;
	private boolean contflag=false;
	private  String subscrStr = "";
	
	private c c3;

    private int nuc=0;
    private boolean restarted=false;
    private boolean algosbusy=false;
    private boolean testflag=false;
    private Long tmstNextmin=0L;
    private boolean todayhol=false;
    
	private int lastTnsIdx=-1;
    private boolean dataCapture=false;
    private boolean finishedThread=false;
	private int lastWlSelected=-1; //not selected
	private int listWlSelected=-1;
	private DefaultListModel<String> dlmWl = new DefaultListModel<String>();
	private DefaultListModel<String> dlmTns = new DefaultListModel<String>();
	
	private String currentSelectedSymbol="";
	private int maxSizeOfTrades=0; //max trades to show in tns window (updated with spinbox)

	private Properties properties;
	
    private static LT instance = null;
    private static boolean dataCreated = false;
    
    public static LT getInstance() throws KException, IOException {
       if(instance == null) {
          instance = new LT();
       }
       return instance; //does not reach?
    }
    
    private LT() throws KException, IOException{
    	try {
    		if(!dataCreated) { 
    			dataCreated=true;
		    	readConfig();
				c3 = new c(properties.getProperty("kdbhost"), API.asInt(properties.getProperty("kdbport")));
    			logger.info("connect to kdb");
				initData();
				wl=new Watchlist(this);
		    	logger.info("initialize LT: "+LTtoString());
		    	for(String s:readSymbols()) wl.symbols().add(s.split("\t")[0]);
		    	for(String s:readSymbols()) wl.types().add(s.split("\t")[1]);
			}
		} catch (KException|IOException e) {
			logger.fatal("LT() - "+e); U.exit();
		}
    	
    }
    
    private ArrayList<String> readSymbols() {
		ArrayList<String> symsTypes=new ArrayList<>();
		//ArrayList<String> types=new ArrayList<>();
		String lineStr;
		API.inOpen(properties.getProperty("symsfile"));
		lineStr=API.readLine();
		lineStr=API.readLine();				
		while(lineStr!=null) {
			//syms.add(lineStr.split("\t")[0]);
			symsTypes.add(lineStr);
			lineStr=API.readLine();				
		}
		API.inClose();
		return symsTypes;
	}

	/** https://stackoverflow.com/questions/1526826/printing-all-variables-value-from-a-class */
    public String LTtoString() {
    	return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public void initData() throws KException, IOException{
    	logger.info("initializing Data");
	    String str;
	    int algo = 1;
	    int live = -1;//initial value is -1 -> is compared for override later!
	            
	    Algos.add(new AlgoParams(1, "AAPL", "STK", 5, 50, 1)); //step,qty,live
	    Algos.add(new AlgoParams(5, "AAPL", "STK", 2, 60, 1));
	    Algos.add(new AlgoParams(4, "AAPL", "STK", 30, 150, 1));
	    Long tmstsec;
	    
	    //pbflag = jf.option1.isSelected()? true : false;
	    
		if (!pbflag) { // set in panelSettings
	        tmstsec = tmst / 1000;
	        datestr = datestrStart = U.timestamp2datestr(tmst,false);
	        datetimestr = U.timestamp2datetimestr(tmst,false);
	    }
	    else{ 
	    	datestr = datestrStart = U.timestamp2datestr(tmst,true);
	    	datetimestr = U.timestamp2datetimestr(tmst,true);
	    }
		
		try {
			if (pbflag&&!contflag) {
		        	c3.ks("orat:delete from get `:orat;");
			}
		    else if ((U.timediff(tmst , datestr, OPGSTR,pbflag) < 0) && (!contflag)) { //time<OPGSTR
					c3.ks("delete from `orat where time.date = .z.D");
		    }
		} catch (Exception e) { logger.fatal(e); }

	    for (AlgoParams c : Algos) {  //create symlist(unique) and algostep with unique steps per symbol starting with empty vectors
	                                  //condition is that double entries e.g. 2x(AAPL,5) do not exist ... todo filter as string?
	        int idx = Symlist.indexOf(c.sym);
	        //if (indexOf(Steplist, c.step) == -1) Steplist.push_back(c.step);
	        if (idx == -1) { // if different symbol add new Algostep
	            Symlist.add(c.sym);
	            AlgoStep.add(new SymStep(c.sym, c.step));
	        }
	        else { // if same symbol add to Steps array in that algostep
	            if (AlgoStep.get(idx).Steps().indexOf(c.step) == -1) AlgoStep.get(idx).Steps().add(c.step); //else do not insert
	        }
	    }

	    subscrStr = pbflag ? "trdpb" : "trades"; //pb or rt
	                                              //pubsub strings
	    for (String s : Symlist) {
	        pubsubstr.add(s + ":crs:*");
	        pubsubstr.add(s + ":" + subscrStr);
	        if (contflag) pubsubstr.add(s + ":trdpb");
	    }
	    if (contflag) { //only if continuing an interrupted session
	        pubsubstr.add("*:pb_end");
	        subscrStr = "trdpb"; //change back at the end
	    }
	    pubsubstr.add("*:oid");
	    pubsubstr.add("*:mod");   //modify algo parameters
	    pubsubstr.add("openord:*");   //cancel live orders

	    int liveflag = 0;
	    for (AlgoParams al : Algos) {
	        if (live != -1) al.live = live;
	        logger.info("algo:" + al.num + "|sym:" + al.sym + "|step:" + al.step + "|qty:" + al.qty + "|live:" + al.live);
	        if (al.live == 2) liveflag++;
	    }

	    int idx = 0;
	    for (SymStep s : AlgoStep) { //create sdata
	        SD.add(new SData(s));
	        SD.get(idx).sid (idx);
	        idx++;
	    }
	    idx = 0;
	    for (AlgoParams c : Algos) { //sid, bsid
	        A.add(new AData(c)); //for all algo entries...
	        A.get(idx).aid( idx); //last(A).aid = idx; //should not be necessary
	        idx++;
	    }
	    //int amaxlive = 0;
	    int maxlive = 0;
	    //idx = 0;
	    for (AData a : A) {
	        //    println(a.live);
	        if (a.live() > maxlive) { maxlive = a.live(); /*amaxlive = idx;*/ } //find a with max live
	        //idx++;
	    }
	    
	    if ((!pbflag) && (!contflag)) { //only for RT opg use timers
	        String datestr1;
	        for (SData s : SD) {
	            
	            datestr1 = U.timestamp2datestr((tmst - 86400000L),pbflag); //must be prev day!
	            str = "$[null last exec amount from divi where sym=`" + s.sym() + ",date=" + datestr1 + ";0f;last exec amount from divi where sym=`" + s.sym() + ",date=" + datestr1 + "]";
	            try {
					s.diviamount ( (double)c3.k("h\""+str+"\"") );  //h"command"
				    str = "exec last prc from crsprc where sym=`" + s.sym() + ",crosstype=`close,date<=" + datestr1;
			        s.prevdclose ( (double)c3.k("h\""+str+"\"") );
			        s.prevdclose ( s.prevdclose() - s.diviamount() );
			       
	            } catch (KException|IOException e) { logger.fatal(e); }
	            //println("sym:" + s.sym + "|prevdclose:" + s.prevdclose + "|divi:" + s.diviamount);
	        }

	       // tmst = TMtimestampNow();

	    } //end RT
	    
	    for (SData s : SD) { //opgbuy,opgsell,mocbuy
	        str = "moc" + s.sym() + ":0;opgb" + s.sym() + ":0;obgs" + s.sym() + ":0";
	     //   write2kdb(str); //initialize mocstr,opgstr
	    }
	    if (!contflag) {
	       // timer_1sec();  //start 1 sec timer
	    }
	}

	public void calcCandle(String line) throws KException, IOException {
	    String Query;
	    String str;
	    String[] subs;
	    String[] subs0;
	    String[] msub;
	   
	    subscrStr = pbflag ? "trdpb" : "trades";
	    subs = line.split(" "); //subs[0] is channel, subs[1] is message
	    subs0 = subs[0].split(":"); //sub of topic  pb:sym:trdpb
	    msub = subs[1].split(";"); //just trades
	                                      //println(subs0[0]+" "+subs0[1]+" "+ts2str(Long.parseLong(msub[0]))+" "+msub[1]);
	                                       //println(subs0[0]+" "+subs0[1]+" "+msub[0]+" "+msub[1]);
	    if (subs0[1].equals(subscrStr)) { 	 //sym+":trades"  or  sym+":trdpb"
	        int id=Symlist.indexOf(subs0[0]); //use in S.get(id)
	        SData s = SD.get(id);
	        s.prc (Double.valueOf(msub[1]));
	        s.newvol (Double.valueOf(msub[2]));
	        tmst = !pbflag&&!contflag ? U.timestampNow() : API.asLong(msub[0]);
	        //tmst = !pbflag&&!contflag ? p.tmst() : Long.valueOf(msub[0]);
	        //tmst = 1500000000L;//!pbflag ? U.timestampNow() : API.asLong(msub[0]);
	      //  calcphase();
	        // RT:volume incorrect after 1st tick???
	        if (s.prc() != 0) { //else dvol will be 0
	                          //s.dvol=s.T.size()==0 ? 0.0 : s.newvol-s.oldvol;
	            s.dvol ( s.newvol() - s.oldvol());
	            s.oldvol (s.newvol());
	            s.T().add(new Tick(tmst, s.prc(), s.dvol()));
	        }
	        else s.dvol (0.0);
	        //started/////////////
	        if ((s.started() == 0)) { //only for 1st tick //calc when 09:30 starts
	            s.started ( 1);
	            //for (AData a : A) a.time1stbar = TMtimestrPlusMinsStr(sodstr, a.step,'l');
	            s.symhasdata (true);
	          
	            datestr= U.timestamp2datestr(tmst,pbflag);
	            timestr= U.timestamp2timestr(tmst,pbflag);

	            //if (contflag&&!datestr.equals(datestr_start)) exit("pbdata date is not of today!");
	            //if (datestr!=datestr_start) exit(1);// "pbdata date is not of today!"); //means in case of a pause???
	            //tmstNextmin = 60000 + 60000 * (tmst / 60000); //used in task1

	            if (!pbflag) {
	   //             eodflag = false;
	                //tseod1 = 60000 - tmst % 60000;
	                //MyTimerTask1(), tseod1    //1min bars
	            }

	 //           timer_moc(); //
	            getDayInfo();
	            if (!restarted) {
	                s.dhigh ( s.prc());
	                s.dlow  ( s.prc()); 
	            }
	            if (pbflag) {
	                for (SData s1 : SD) {
	                    tday = U.timestamp2weekday(tmst);
	                    if (!s1.typesset()) {
	                        getdaytypes(s1);
	                        //playback multiple syms todo........
	                        //s1.prevdclose = null == jedis2.get("algo:" + s1.sym + ":crs:prevdclose") ? 0 : Double.parseDouble(jedis2.get("algo:" + s1.sym + ":crs:prevdclose"));
	                        String str1 = "algo:" + s1.sym() + ":crs:prevdclose";
	                        //get 
	                        Object ret=FrameMain.jedis3.get("algo:"+s1.sym()+":crs:prevdclose");
	                        s1.prevdclose (null==ret ? 0 : API.asDouble(ret));
	                        str = "$[null last exec amount from divi where sym=`" + s1.sym() + ",date=" + datestr +
	                            ";0f;last exec amount from divi where sym=`" + s1.sym() + ",date=" + datestr + "]";
	                        s1.diviamount ( (double) c3.k("h\""+str+"\""));
	                        s1.prevdclose(s1.prevdclose() - s1.diviamount());
	                        str1 = "algo:" + s.sym() + ":crs:dopen";
	                        ret=FrameMain.jedis3.get("algo:"+s1.sym()+":crs:dopen");
	                        s1.dopencrs ((null==ret) || ret.equals("-nan(ind)")? 0 : API.asDouble(ret));
	                        //todo: if no prevdclose and/or dopencrs exist???
	                        System.out.printf("prevdclose:%.2f|dopen:%.2f|divi:%.2f|date:%s\n", s1.prevdclose(), s1.dopencrs(), s1.diviamount(), datestr); //only in pb mode
	                    }
	                }
	            }
	            if (!contflag) {
	                // try{
	                // //    c2.ks("algos"+ ( pbflag ? "pb:" : ":" ) +"([]sym:();algo:();step:();tmst:();nub:();live:();qty:();dir:();pos:();ntrades:();norders:();tgtprc:();dlow:();dhigh:();typ:())");
	                // //    c2.ks("bar1:([]algo:();ts:();ticklen:();ticksperbar:();open:();high:();low:();close:();volume:();siz:();medb:();medsum:();avgmed:())");
	                // //    c2.ks("barx:([]algo:();ts:();ticklen:();ticksperbar:();open:();high:();low:();close:();volume:();siz:();num:();medb:();medsum:();avgmed:())");
	                // } catch (Exception ex) {  System.err.println ("Ex14:"+ex);  } 
	                //nuc = (U.secsSinceMidnight(Jtrdr.tmst) - U.secsSinceMidnight((Long)U.time2tsToday(sodstr) * 1000L)) / 60; //minutes til start of day
	                nuc=(int) (U.secsSinceMidnight(LocalTime.parse(U.timestamp2timestr(tmst,pbflag)))-U.secsSinceMidnight(LocalTime.parse(SODSTR)))/60;
	                s.createBs(s.sym());
	            }

	        } //end of started ////////
	        //       println(ts2str(round2sec(tmst))+"|"+ts2str(blist1.get(blist1.size()-1).timeend)+"|"+prc);        
	          //if((round2sec(tmst)==blist1.get(blist1.size()-1).timeend )&&(tlist.size()>1)){ //1min bar end //empty quote every 1min (>=)
	          //       println(ts2str(tmst));

	        //2nd bar - 1st bar in createBs!
	        if (s.prc() == 0) { //new bar - 1min 0 tick - live&playback  (is pushed at end of every minute only!)
	                          //println("minutepassed "+minute_passed++); //playback
	                          //println(s.B1.size()+" "+ts2str(last(s.B1).timestart)+" "+ts2str(last(s.B1).timeend));
	            nuc++; //global var - 1min end of bar is added once for all symbols
	            while (algosbusy) { logger.info("algos busy...\n"); } //avoid race conditions
	            s.Bs().get(0).addBar(); // Bs[0] is 1min bar series
	            U.last(s.Bs().get(0).B()).setBar1m();
	            //s.prc = s.lastprc; 
	            //for (BarSeries &bs : s.Bs) { //check if new bar for other bar sizes
	            for (int i = 1; i<s.Bs().size();i++) { //check if new bar for other bar sizes
	                if (U.last(s.Bs().get(0).B()).timeend()>U.last(s.Bs().get(i).B()).timeend()) { //new stepbar
	                    s.Bs().get(i).addBar();
	             //       bs.nub = bs.B.size() - bs.tilsod;
	                    //for (AData a : A) {
	                    //    cancelifnot(a);
	                    //}
	                }
	                U.last(s.Bs().get(i).B()).setBar1m(); //update all bars at 1m eob
	            }

	        }
	        else if(s.T().size()>1){ //update bar
	            for (BarSeries bs : s.Bs()) {
	                //std::cout << bs.B.back().bid << "\n";
	               //if(bs.B.back().T.size()!=0)
	                   U.last(bs.B()).setBar();       //update all bars for sym

	              //  SD[0].Bs[0].p = &SD[0];
	              //  SD[0].Bs[0].B[0].p = &SD[0].Bs[0];
	              //  Bar * b = &SD[0].Bs[0].B[0];
	              //  printf("barseries pointer:%p %p\n", (void *)b->p, (void *)b->p->p);
	              //  if (b->p->p->prc != 0) {
	              //      b->T.push_back(b->p->p->T.back());
	              //      if (b->T.back().price > b->high) b->high = b->T.back().price;
	              //      if (b->T.back().price < b->low) b->low = b->T.back().price;
	              //      b->close = b->T.back().price;
	              //      b->volume += b->T.back().volume;
	              //  }
	            }
	        }
	            //for (int i = 0; i < SD[0].Bs.size();i++) {
	            //    SD[0].Bs[i].B.back().setBar();       //update all bars for sym
	            //}
	        //}
	          //  s.Bs[0].B.back().setBarT(s.T.back()); //update 1min bar

	        //Bar b = last(s.B1.B);
	        //jedis2.publish("bar:sym:" + s.sym, nuc + ";" + 999 + ";" + b.open + ";" + b.high + ";" + b.low + ";" + b.close + ";" + b.volume);

	        //for (BarSeries &bs : s.Bs) {
	        //    bs.B.back().setBarB(s.Bs[0].B.back());       //update xmin bar
	        //                                             //println("idx:"+bs.B.size());
	        //                                             //bs.B.get(bs.nux).setBarxm(last(s.B1.B)); //to avoid race condition
	        //    //bs.B.back().setBarxm(s.Bs[0].B.back()); //use semaphore
	        //}
	        //////////end of bar building
	        if (s.prc() != 0) s.lastprc ( s.prc());  //last prc only used in pb mode for gap fills
	                            //println(ts2str(tmst)+" "+s.prc+" "+s.dvol);

	                            //println(datestr+" "+timecross(tmst,sodstr)+" "+ts2str(timeold)+" "+ts2str(tmst)+" "+sodstr);
	        if (pbflag) {
	            if ((U.timecross(timeold,tmst/1000, datestr,SODSTR,pbflag)>0)) { //set low/high at sod >=09:30 1st bar rth //use timer???
	                if (s.prc() != 0){ 
	                    s.dhigh ( s.prc());
	                    s.dlow ( s.prc());
	                }
	                else {
	                	s.dhigh ( s.lastprc());
	                	s.dlow ( s.lastprc());
	                }
	            }
	        }
	        else {
	            if ((s.setDhiDlo() == 0)&&(s.prc() != 0)) { //set dhi/dlo (works even if started after sod) ??? //on 1st tick and at sod
	                                      //if(s.Bs.size()==0) { s.createBs(last(s.T)); }
	                s.setDhiDlo ( 1);
	                s.dlow (s.prc());
	                s.dhigh(s.prc());
	            }
	        }

	        //calltest(id);
	        //if(timecross(tmst,"13:00:00")>0) { exit(); }
	        //if(s.B1.B.size()<=0) {//
	        //println("nuc:"+nuc+" totmins:"+tot_mins);
	        if ((nuc < totMins) || testflag) {
	            algosbusy = true;
	            for (int i : s.Aid()) {
	                //async not possible because pubsub single threaded!  //only for algos of that sym
	                AData a = A.get(i);
	                //print(nuc+" ");showMinData(A.get(0),last(s.B1.B));
	                //print(a.getnub()+" ");showMinData(A.get(0),last(s.Bs.get(a.bid).B));
	         
	                //Bar b=last(s.Bs[a.bid].B);
	                //jedis2.publish("bar:sym:"+a.sym, nuc+";"+a.getnub()+";"+b.open+";"+b.high+";"+b.low+";"+b.close+";"+b.volume);
	                if (!contflag) callalgo(a); //executing algos
	           //     a.lastbarnum = a.getnub();
	            }
	            algosbusy = false;
	        }
	        else if (pbflag) {  //at the end of eod
	            finishedThread = true;
	            //println(nuc+" "+tot_mins+ts2str(tmst));
	            //for(SData s1:S) {
	            //    for(SData.BarSeries.Bar b:s1.B1.B){
	            //        showMinData(A.get(0),b);
	            //    }
	            //    for(SData.BarSeries bs:s1.Bs){
	            //        for(SData.BarSeries.Bar b:bs.B){
	            //            showData(A.get(0),b);
	            //        }
	            //    }
	            //}
	            //time2int gtime 2017.03.29T08:30:00  //ltime int2time 1490831612308
	        //    kclose(handle); //c5.close(); 
	            //catch (Exception ex) { System.err.println("Ex14:" + ex); } //c2 can be 5200 or 5300!  //exit at eod
	                                                                       //println("end of data");
	        //    exit(1);
	        }
	        if (pbflag&&s.opgmerge()) s.sendopg();
	        if (pbflag&&s.mocmerge()) s.sendmoc();
	        //new dhigh/dlow
	        if (s.prc() != 0) {
	            if (s.prc()<s.dlow()) { s.dlow ( s.prc()); }
	            else if (s.prc()>s.dhigh()) { s.dhigh ( s.prc()); }
	        }
	        timeold = tmst; //for timecross in algos in pb mode (not only)
	    } //end trades/trdpb
	    else if (subs0[1].equals("crs")) { //reset at midnight??? // print only once???
	        int id = Symlist.indexOf(subs0[0]); //todo......
	        SData s = SD.get(id);
	        if (subs0[2].equals("open")) {
	            s.dopencrs ( Double.valueOf(msub[1]));
	            //println("sym:" + subs0[0] + "|sub/dopencrs:" + s.dopencrs);
	        }
	        else if (subs0[2].equals("close")) {
	            s.prevdclose ( Double.valueOf(msub[1]));
	            s.prevdclose (s.prevdclose()- s.diviamount());
	            //println("sym:" + subs0[0] + "|sub/prevdclose:" + s.prevdclose + "|divi:" + s.diviamount);
	        }
	    } //end crs
	      //must be an oid! //must be AData num NOT algo num!  
	    else if ((subs0[1].equals("oid")) && (!subs0[0].equals("man"))) { //to ignore manual order because oref is "man"
	        int id = subs0[0].equals("") ? 0 : Integer.valueOf(subs0[0]); //goes into A.get(id)
	                                                                      //try{
	        if (id >= 0) { //not necessary because man is filtered before but just in case
	            AData a = A.get(id);
	            a.lastoid ( Integer.valueOf(msub[0])); 
	           // A_lastoid[a.aid]= std::stoi(msub[0]); //??? todo  replace with something???
	        }
	        //}catch(Exception ex) { exit("Ex34:"+ex); }
	        //println(subs0[0]+" "+subs0[1]+" "+msub[0]);//a.lastoid);
	    } //end oid
	    else if (subs0[1].equals("mod")) { //id: ;live: ;(qty): ;dir: ;pos: ;(tgtprc): ;(dlow): ;(dhigh): 
	            //id starts at 0!!!    //  publish :mod id:1;live:0;pos:50;qty:100;dir:-1
	    		ArrayList<String> tmp = new ArrayList<String>(Arrays.asList(subs[1].split(":|;")));
	            int tid = tmp.indexOf("id") + 1;
	            int tlive = tmp.indexOf("live") + 1;
	            int tpos = tmp.indexOf( "pos") + 1;
	            int tqty = tmp.indexOf( "qty") + 1;
	            int tdir = tmp.indexOf( "dir") + 1;
	            if (tid>0) {
	                //println("\n"+tid+" "+tlive+" "+tpos+" "+tdir);
	                int id =  Integer.valueOf(tmp.get(tid));
	                int live= Integer.valueOf(tmp.get(tlive)); ;
	                int pos = Integer.valueOf(tmp.get(tpos));;
	                int qty = Integer.valueOf(tmp.get(tqty));;
	                int dir = Integer.valueOf(tmp.get(tdir));;
	                //double tgtprc=Double.parseDouble(tmp.get(tmp.indexOf("tgtprc")+1));
	                //double dlow=Double.parseDouble(tmp.get(tmp.indexOf("dlow")+1));
	                //double dhigh=Double.parseDouble(tmp.get(tmp.indexOf("dhigh")+1));
	                AData a = A.get(id);
	                //check valid numbers...
	                //if((algo>algos.size())||(algo<0)) exit("bad algo num");
	                if (tlive>0) a.live ( live);
	                if (tpos>0) a.pos (pos);
	                if (tqty>0) a.qty ( qty);
	                if (tdir>0) a.dir (dir);
	            }
	        }
	        
	    //} //end mod
	    //else if (subs0[1].equals("pb_end")) { //called at end of cont playback
	    //                                       //if cont flag
	    //} //end pb_end
	} //end of calccand
	private void callalgo(AData a) throws IOException, KException {
		if (((!todayhol) && (tday<WKND)) || testflag) {
		    switch (a.algo()) {
		    case 1: {
		        AL_algo1 a1 = new AL_algo1(a);  break;
		        //algo1 *a1 = &algo1(a); delete[] a1;  break;
		    }
		    	default:
		        break;
		    }
		}
	}

//	static void calcphase() { /////////////////////
//	    Long ts = Jtrdr.tmst;// / 1000;
//	    datestr=U.timestamp2datestr(ts);
//	    int res = (int) U.timediff(ts, prestr);
//	    res = (int) U.timediff(ts, poststr);
//	    if ((U.timediff(ts, prestr)<0) || (U.timediff(ts, poststr) >= 0) || (tday<2)) { phase = ph.closed; }
//	    else if (U.timediff(ts, eodstr) >= 0) { phase = ph.post; }
//	    else if ((U.timediff(ts, sodstr)<0) && (U.timediff(ts, prestr) >= 0)) { phase = ph.pre; }
//	    else { phase = ph.rth; }
//	    marketopen = phase == ph.rth;
//	    xth = phase == ph.pre || phase == ph.post;
//	    eodflag = phase == ph.closed;
//	}

	public void getdaytypes(SData s){ //executed on start up and after midnight
        String Query;
        //String todaystr=LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateformatdot));
        String todaystr=U.formattedDateTimeNow(U.dateformat);
        try{
            //Query="data:get `:../../Users/mfitsilis/Documents/stock_data/kdbdata/"+s.sym;
            Query="data:get `:../hist/"+s.sym();
            if(pbflag){
                c3.k(Query);
                c3.k("data0:data");
                if(todaystr.equals(datestr)){ //check if current day is being played back!
                    //Query="";//todaytrd[]"; 
                    //c2.k(Query);                    
                    Query="$[0<=first exec Close[i+1] from reverse ohlcd 1;`pos;`neg]";
                    if(((String)c3.k(Query)).equals("pos")) s.prvopg(1); else s.prvopg(-1);
                    Query="$[0<=last exec Close-Open from ohlcd 1;`pos;`neg]";
                    if(((String)c3.k(Query)).equals("pos")) s.prvpn(1); else s.prvpn(-1);
                    Query="first exec ?[(High>High[i+1])&(Low<Low[i+1]);3;?[(High>High[i+1]);1;?[(Low<Low[i+1]);2;4]]] from reverse ohlcd 1";
                    s.prvtyp((int)(long)c3.k(Query));                
                } else {
                    Query="last exec prvopg from prvopgdates where date="+datestr;
                    if(((String)c3.k(Query)).equals("pos")) s.prvopg(1); else s.prvopg(-1);
                    Query="last exec prvpn from prvpndates where date="+datestr; 
                    if(((String)c3.k(Query)).equals("pos")) s.prvpn(1); else s.prvpn(-1);
                    Query="last exec prvtyp from prvtypdates where date="+datestr;
                    s.prvtyp((int)(long)c3.k(Query));                
                }
            }
            else {
                c3.k(Query);
                c3.k("data0:data");
                Query="last (select opg:?[Open<Close[i-1];`neg;`pos] from -2#(ohlcd 1))[`opg]";
                if(((String)c3.k(Query)).equals("pos")) s.prvopg(1); else s.prvopg(-1);
                Query="last (select time.date,pn:?[Open[i]>Close[i];`neg;`pos] from -2#(ohlcd 1))[`pn]";
                if(((String)c3.k(Query)).equals("pos")) s.prvpn(1); else s.prvpn(-1);
                Query="last (select prvtyp:?[(High[i]>High[i-1])&(Low[i]<Low[i-1]);3;?[High[i]>High[i-1];1;?[Low[i]<Low[i-1];2;4]]] from -2#(ohlcd 1))[`prvtyp]";
                s.prvtyp((int)(long)c3.k(Query));
            }
        } catch (Exception e) { logger.fatal(e); }
        logger.info("sym:"+s.sym()+"|prvopg:"+s.prvopg()+"|prvpn:"+s.prvpn()+"|prvtyp:"+s.prvtyp()+"|tday:"+tday);
        s.typesset (true);
    }
	
	public boolean pbflag(){
		return pbflag;
	}
	public void pbflag(boolean t1){
		pbflag=t1;
	}
	public boolean contflag(){
		return contflag;
	}
	public void contflag(boolean t1){
		contflag=t1;
	}

	public long tmst(){
		return tmst;
	}
	public boolean dataCapture(){
		return dataCapture;
	}
	public void dataCapture(boolean d1){
		dataCapture=d1;
	}
	public void clearData() throws KException, IOException{
		if(!SD.isEmpty()){
			logger.info("clear data");
			//SD.clear();
			//Symlist.clear();
			//Algos.clear();
			//A.clear();
			//A_lastoid.clear();
			//AlgoStep.clear();
			//Torder.clear();
			//Pubsubstr.clear();

			//clear data
			
			dlmWl.clear();
			dlmTns.clear();
			wl.clearData();
			lastWlSelected=-1; //not selected
			listWlSelected=-1;
			
		}
	}
	
	public c c3(){
		return c3;
	}
	
	// getters and setters
	public ArrayList<SData> SD() {
		return SD;
	}

	public ArrayList<AData> A() {
		return A;
	}
	public ArrayList<AlgoParams> Algos() {
		return Algos;
	}

	public ArrayList<Integer> aLastoid() {
		return aLastoid;
	}

	/** calc new time,date,phaseupdate every 1s */
	public void timeUpdate(FrameMain jfr) throws KException, IOException {		
		if(pbflag) { //tmst also updated in calcCandle - for subscribed symbols
			tmst = lastTime;
		}
		else
		{
			tmst = U.timestampNow();
		}
		tmstNextmin = 60000 + 60000 * (tmst / 60000); //used in task1
		if(tmst!=0){
			//getDayInfo();
			tday=U.timestamp2weekday(tmst);
	        datestr=U.timestamp2datestr(tmst,pbflag);
		    timestr=U.timestamp2timestr(tmst,pbflag);
		    datetimestr=datestr+" "+timestr;
		 		
			if((!datestr.equals(olddatestr))&&(!pbflag)){
	        	//if((restarted==1)&&(daysrunning==2)) restarted=0; //after restarted program can continue running indefinitely (reset restarted on 2nd day change)
	            getDayInfo();        	            
	        }
			
		    if ((U.timediff(tmst, datestr,PRESTR,pbflag)<0) || (U.timediff(tmst, datestr,POSTSTR,pbflag) >= 0) || (tday<2) || (!todayHoliday.equals("no"))) { phase = ph.closed; }
		    else if (U.timediff(tmst, datestr,eodstr,pbflag) >= 0) { phase = ph.post; }
		    else if ((U.timediff(tmst, datestr,SODSTR,pbflag)<0) && (U.timediff(tmst, datestr,PRESTR,pbflag) >= 0)) { phase = ph.pre; }
		    else { phase = ph.rth; }
		    marketopen = phase == ph.rth;
		    xth = phase == ph.pre || phase == ph.post;
		    eodflag = phase == ph.closed;
		    
	//	    if(((tday<wknd)&&(!todayhol))||testflag){
	//            for(SData s:S) {
	//                getdaytypes(s)
	//            }
	//	    }
		    
	        olddatestr=datestr;
		}
	}

	public boolean eodflag() {
		return eodflag;
	}

	public void eodflag(boolean eodflag) {
		this.eodflag = eodflag;
	}

	public boolean marketopen() {
		return marketopen;
	}
	public void marketopen(boolean marketopen) {
		this.marketopen = marketopen;
	}

	public boolean xth() {
		return xth;
	}

	public void xth(boolean xth) {
		this.xth = xth;
	}

	public ph phase() {
		return phase;
	}
	
	public String getPhase() {
		switch (phase){
			case pre:
				return "pre";
			case rth:
				return "rth";
			case post:
				return "post";
			case closed:
				return "closed";
			default:
				return "closed";
		}
	}

	public void phase(ph phase) {
		this.phase = phase;
	}
	public void tmst(long t1) {
		tmst=t1;
	}

	public int tday() {
		return tday;
	}
	public int tmonth() {
		return tmonth;
	}
	public int tmday() {
		return tmday;
	}
	public String eodstr(){
		return eodstr;
	}
	public String mocstr(){
		return mocstr;
	}
	public int daysrunning(){
		return daysrunning;
	}
	public String datestrStart(){
		return datestrStart;
	}
	public String datestr(){
		return datestr;
	}
	public void datestr(String s1){
		datestr=s1;
	}
	public void timestr(String s1){
		timestr=s1;
	}
	public String timestr(){
		return timestr;
	}
	public String datetimestr(){
		return datetimestr;
	}
	public int totMins(){
		return totMins;
	}
	public int eodMins(){
		return eodMins;
	}
//	public void tmstAtEnd1m(long ts){
//		tmstAtEnd1m=ts;
//	}
//	public long tmstAtEnd1m(){
//		return tmstAtEnd1m;
//	}
	public long tmstNextmin(){
		return tmstNextmin;
	}	
	
	public void getDayInfo() throws KException, IOException{ //at new day(RT), 1st tick(pb)
        tmonth=U.timestamp2month(tmst);
        tmday=U.timestamp2monthday(tmst);
           
        if(datestr.substring(5,10).equals("12.24")||datestr.substring(5,10).equals("07.03")||((tday==6)&&(tmonth==11)&&(tmday>22)))
             { eodstr=EODESTR; mocstr=MOCESTR; eodMins=780; totMins=210; } 
        else { eodstr=EODNSTR; mocstr=MOCNSTR; eodMins=960; totMins=390; }
    
        String[] holidays= (String[]) c3().k("h\""+"`$($) hol year"+"\"");
        int idx=java.util.Arrays.asList(holidays).indexOf(datestr);
        if(idx==-1) todayHoliday="no";
        else todayHoliday=HOLIDAYS[idx];
	}	
	
	public String todayHoliday(){
		return todayHoliday;
	}

	public String marketHours(){
		if (tday<2) return "weekend";
		else if (totMins==210) return "09:30 - 13:00";
		else return "09:30 - 16:00";
	}

	public void updateEveryMinuteEvent(String string) {
	}
	public boolean captureRunning(){
		return captureRunning;
	}
	public void captureRunning(boolean b) {
		captureRunning=b;			
	}
	public Watchlist wl() {
		return wl;
	}
	/** sym must be added to LT first! */
	public void watchListUpdateOrAdd(int isQuote,String sym) { //0:stock, 1:fx
		int idx=wl.symbols().indexOf(sym);
		if((idx>=0)&&(idx<wl().instruments().size())) {	
			try {
				String s1=String.format("%03d %s",idx+1,wl.instruments().get(idx).getWatchListInstrument(isQuote));
				if(idx<dlmWl.size())
					dlmWl.set(idx,s1);
			} catch (Exception e) { logger.fatal(e); }
		}
		else {
			try {
				dlmWl.addElement(String.format("%03d %s",wl.instruments().size(),U.last(wl.instruments()).getWatchListInstrument(0)));
			} catch (Exception e) { logger.fatal(e); }
		}
	}
	public void timeAndSalesUpdate(String sym) {
		if(sym.equals(currentSelectedSymbol)) {
			Instrument instrument=wl.instruments().get(listWlSelected);
			String s1=String.format("%s",instrument.getLastTimeAndSalesOfInstrument());
			dlmTns.add(0,String.format("%03d ",instrument.trades().size())+s1); //item on top of list
			while(dlmTns.size()>maxSizeOfTrades) { //remove excess entries
				dlmTns.removeElementAt(dlmTns.size()-1);	
			}
		}			
	}

	public DefaultListModel<String> dlmWl() {
		return dlmWl;
	}
	public DefaultListModel<String> dlmTns() {
		return dlmTns;
	}
	public int lastWlSelected() {
		return lastWlSelected;
	}
	public int listWlSelected() {
		return listWlSelected;
	}
	public void listWlSelected(int l1) {
		listWlSelected=l1;
		String sym=wl().symbols().get(l1);
		currentSelectedSymbol=sym;
	}
	public void lastTime(long t1) {
		this.lastTime=t1;
	}

	public void maxSizeOfTrades(Integer i1) {
		maxSizeOfTrades=i1;
	}
	
	//https://www.mkyong.com/java/java-properties-file-examples/
	public void readConfig() {
		properties = new Properties();
		InputStream input = null;

		try 
		{
			input = new FileInputStream("jtrdr.cfg");
			properties.load(input);
		} 
		catch (IOException ex) { logger.fatal(ex); }
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) { logger.fatal(e); }
			}
		}

	}

	public int maxSizeOfTrades() {
		return maxSizeOfTrades;
	}

	public void lastWlSelected(int i1) {
		this.lastWlSelected=i1;
	}

	public void currentSelectedSymbol(String sym) {
		this.currentSelectedSymbol=sym;		
	}
}
