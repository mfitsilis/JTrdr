package Jtrdr;  

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Painter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

import kx.c.KException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static kiss.API.*;

/**
 * java trading software 
 * 
 * @version 1.1
 */

/**
 * Main class that includes main function.
 */
public class Jtrdr{
	final static Logger logger=Logger.getLogger(Jtrdr.class);
    
	private UpdateAlgoTickEvent updateAlgoTickEvent;
    private LT lt;
    private FrameMain jfr;
    	
	private Timers timers;
	
    private static Jtrdr instance = null;
    
    public static Jtrdr getInstance(FrameMain jfr) throws KException, IOException {
       if(instance == null) {
          instance = new Jtrdr(jfr);
       }
       return instance; //does not reach?
    }

	private Jtrdr(FrameMain jfr) throws KException, IOException{
			lt=LT.getInstance();
    		this.jfr=jfr;
    		
    		timers=new Timers(jfr);
    }
	
	public static void main(String[] args) throws KException, IOException
	{
		logger.info("*** starting app ***");
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            UIManager.getLookAndFeelDefaults().put("TextArea[Disabled].backgroundPainter",
                            new FillPainter(Color.black));
		            break;
		        }
		    }
		} catch (Exception e) { //if not available
			logger.fatal(e);
		    try {
		        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		    } catch (Exception ex) { logger.fatal(ex); }
		}
				
		SwingUtilities.invokeLater(new Runnable() {
		
		FrameMain jfr;
		@Override
        public void run()
        {
            try {
				jfr= new FrameMain(); //build gui
	    	} catch (Exception e) { logger.fatal(e); }
        		
            
        	try {
        		Jtrdr jtrdr=Jtrdr.getInstance(jfr);
        		Holidays h1=new Holidays(2018);        
        	}catch (Exception e) { logger.fatal(e); }
        }
     });
	}

}

class Timers{
	final static Logger logger=Logger.getLogger(Timers.class);
	private LT lt;
	private FrameMain jfr;
	private MyTimerTask1s task1s;
	private MyTimerTask1m task1m;
	private ScheduledExecutorService scheduler1s;
	private ScheduledExecutorService scheduler1m;
	public Timers(FrameMain jfr) throws KException, IOException{
		lt=LT.getInstance();
		this.jfr=jfr;
		start();
	}
	public void start() throws KException, IOException{
		task1s=new MyTimerTask1s(jfr);
		task1m=new MyTimerTask1m(jfr);
		ScheduledExecutorService scheduler1s = Executors.newSingleThreadScheduledExecutor(); //calc time every 1 sec
        scheduler1s.scheduleAtFixedRate(task1s, 0, 1000, TimeUnit.MILLISECONDS);
        
        long tmst=U.timestampNow(); long tmstAtEnd1m=(60000-tmst%60000);
        ScheduledExecutorService scheduler1m = Executors.newScheduledThreadPool(1); //1min bars
        scheduler1m.schedule(task1m, tmstAtEnd1m<1000? 60000 : tmstAtEnd1m, TimeUnit.MILLISECONDS); // if too fast then wait 1min
        
	}
	public void stop() throws KException, IOException{
		//task1s.cancel();
		//task1m.cancel();
		scheduler1s.shutdown();
		scheduler1m.shutdown();
	}
}

class MyTimerTask1s extends TimerTask { //calc time every x sec //separate thread that monitors time
	final static Logger logger=Logger.getLogger(MyTimerTask1s.class);
    private UpdateEverySecondEvent updateEverySecondEvent;
    private FrameMain jfr;
	private LT lt;
	public MyTimerTask1s(FrameMain jfr) throws KException, IOException {
		lt=LT.getInstance();
		this.jfr=jfr;
   		updateEverySecondEvent=new UpdateEverySecondEvent();
   		updateEverySecondEvent.addListener(s1 -> jfr.updateEverySecondEvent(""));
    }
	public void run(){
		updateEverySecondEvent.call("");
    }
}

class MyTimerTask1m extends TimerTask { //calc time every 1min
	final static Logger logger=Logger.getLogger(MyTimerTask1m.class);
	private UpdateEveryMinuteEvent updateEveryMinuteEvent;
	private FrameMain jfr;
	private LT lt;
	public MyTimerTask1m(FrameMain jfr) throws KException, IOException{
		lt=LT.getInstance();
		this.jfr=jfr;
 		updateEveryMinuteEvent=new UpdateEveryMinuteEvent();
 		updateEveryMinuteEvent.addListener(s1 -> lt.updateEveryMinuteEvent(s1));
 		updateEveryMinuteEvent.addListener(s1 -> jfr.panelWatchlist().updateMinuteDataTree1m(s1));
   	}
	public void run(){
		updateEveryMinuteEvent.call("");
		if(!lt.pbflag()){
            for(SData s:lt.SD()){
            	if(s.symhasdata()){
                    try {
						lt.calcCandle(s.sym()+":trades"+" "+lt.tmstNextmin()+";0.0;0.0");
					} catch (KException|IOException e) { logger.fatal(e); }
                    //logger.info(s.sym()+":trades"+" "+U.timestamp2timestr(lt.tmstNextmin(),lt.pbflag())+";0.0;0.0");
                }
            }
		}
            
		long tmstAtEnd1m=(60000-lt.tmst()%60000);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        try {
			scheduler.schedule(new MyTimerTask1m(jfr), tmstAtEnd1m<1000? 60000 : tmstAtEnd1m, TimeUnit.MILLISECONDS); // if too fast then wait 1min
		} catch (KException|IOException e) { logger.fatal(e); }
    }	    
}



/** required to modify Nimbus defaults! */
class FillPainter implements Painter<JComponent> {
    private final Color color;
    FillPainter(Color c) {
        color = c;
    }
    @Override
    public void paint(Graphics2D g, JComponent object, int width, int height) {
        g.setColor(color);
        g.fillRect(0, 0, width - 1, height - 1);
    }
}
