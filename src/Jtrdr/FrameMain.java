package Jtrdr;

import static kiss.API.asDouble;
import static kiss.API.asLong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import kx.c.KException;
import net.miginfocom.swing.MigLayout;

class FrameMain extends JFrame  {
	  final static Logger logger=Logger.getLogger(FrameMain.class);

	  public static JedisPubSub jedisPubSub;
	  public static JedisPubSub jedisPubSub2;
	  public static Jedis jedis2 = new Jedis("localhost");
	  public static Jedis jedis3 = new Jedis("localhost");

	  private ChartBar bchart;
      private ChartLine linechart;

	  private JDesktopPane dtp = new JDesktopPane();
	  private Font font = new Font("Liberation Mono", Font.PLAIN, 12);

      final JFrame jf = new JFrame("Jtrdr Main Window"); //title is not shown
	    
      private MenuMain menu = new MenuMain(this);
	  
      private JInternalFrame wlFrame = new JInternalFrame("Watchlist", true,false,false,true);
      private JInternalFrame pltBarFrame = new JInternalFrame("Bar Plot", true,false,false,true);	    
      private JInternalFrame pltLineFrame = new JInternalFrame("Line Plot", true,false,false,true);	    
	  private JInternalFrame setFrame = new JInternalFrame("Settings", true,false,false,true);
	  private JInternalFrame histFrame = new JInternalFrame("Historical", true,false,false,true);
	  private JInternalFrame tnsFrame = new JInternalFrame("T&S", true,false,false,true);
	  private JInternalFrame cmpFrame = new JInternalFrame("Company Info", true,false,false,true);
	  private JInternalFrame reflectionFrame = new JInternalFrame("Class Vars", true,false,false,true);
	  private JInternalFrame debugFrame = new JInternalFrame("Debug", true,false,false,true);
	  
	  private PanelReflection panelReflection;
	  private PanelSettings panelSettings;
	  private PanelWatchlist panelWatchlist;
	  private PanelHistorical panelHistorical;
	  private PanelTimeAndSales panelTimeAndSales;
	  private PanelCompanyData panelCompanyData;
	  private PanelBarChart panelBarChart;
	  private PanelLineChart panelLineChart;
	  private PanelDebug panelDebug;
	  private NewWatchedTickEvent newWatchedTickEvent;
	  
	  private UpdateChartsEvent updateChartsEvent;
      private ClearLineChartEvent clearLineChartEvent;
      private ClearBarChartEvent clearBarChartEvent;
	  //private UpdateNumberOfSymbols updateNumberOfSymbols;
	  private UpdateNumberOfTrades updateNumberOfTrades;
	  private UpdateWatchlistTime updateWatchlistTime;

	  private LT lt;
      
      private CompInfo cinfo=new CompInfo();
      private CompDescr cdescr=new CompDescr();
      private CompData cdata=new CompData();
      
      private String title="JTrdr"; //main window title
            
      public FrameMain() throws KException, IOException{
    	  	lt=LT.getInstance();
    	  	
    	  	bchart=new ChartBar("Num of symbols",lt.wl().symbols(),lt.wl().numOfTradesPerSym(),this);
		    linechart=new ChartLine("Num of trades",this);
		    
		    clearBarChartEvent=new ClearBarChartEvent();
       		clearBarChartEvent.addListener(s1 -> bchart.clearDataset());
	   		
       		clearLineChartEvent=new ClearLineChartEvent();
	   		clearLineChartEvent.addListener(s1 -> linechart.clearDataset(s1));
	   		
	   		updateChartsEvent=new UpdateChartsEvent();
	   		updateChartsEvent.addListener(s1 -> bchart.updateChartEvent(s1));
	   		updateChartsEvent.addListener(s1 -> linechart.updateChartEvent(s1));
       		
	   		updateWatchlistTime=new UpdateWatchlistTime();	   		
	   		updateWatchlistTime.addListener(s1 -> panelWatchlist.updateWatchlistTime(s1));
       			
       		updateNumberOfTrades=new UpdateNumberOfTrades();	   		
           	updateNumberOfTrades.addListener(i1 -> panelLineChart.updateNumberOfTrades(i1));
       		
           	newWatchedTickEvent=new NewWatchedTickEvent();
           	newWatchedTickEvent.addListener(s1 -> panelWatchlist.updateMinuteDataTree1s(s1));
           	
		  	jf.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
		    jf.setTitle(title);
		    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		    screenSize.width -= 42;
		    screenSize.height -= 42;
		    jf.setSize(screenSize);
		    jf.setLocation(20, 20);
		    ////////// desktop
		    jf.setContentPane(dtp);
		    ///////menu
		    jf.setJMenuBar(menu);
		    //////Watchlist
		    panelWatchlist=new PanelWatchlist(this);
		    wlFrame.setContentPane(panelWatchlist);	    
		    wlFrame.setLocation(10, 10);
		    wlFrame.setSize(480, 640);
		    wlFrame.setVisible(true);
		    wlFrame.setBackground(Color.gray);
		    dtp.add(wlFrame);
		    //////plots
		    panelBarChart=new PanelBarChart(this,bchart);
			pltBarFrame.setContentPane(panelBarChart);	    
			pltBarFrame.setSize(300, 160);
		    pltBarFrame.setLocation(500, 10);
		    pltBarFrame.setVisible(true);
		    pltBarFrame.setBackground(Color.gray);
		    dtp.add(pltBarFrame);
		    panelLineChart=new PanelLineChart(this,linechart);
		    pltLineFrame.setContentPane(panelLineChart);	    
		    pltLineFrame.setSize(300, 160);
		    pltLineFrame.setLocation(500, 180);
		    pltLineFrame.setVisible(true);
		    pltLineFrame.setBackground(Color.gray);
		    dtp.add(pltLineFrame);
		    //////settings
		    panelSettings=new PanelSettings(this);
		    setFrame.setContentPane(panelSettings);
		    setFrame.setLocation(500, 345);
		    setFrame.setSize(330, 200);
		    setFrame.setVisible(true);
		    dtp.add(setFrame);
		    //////load historical / run algo
		    panelHistorical=new PanelHistorical(this);
		    histFrame.setContentPane(panelHistorical);	    
		    histFrame.setLocation(500, 550);
		    histFrame.setSize(370, 100);
		    histFrame.setVisible(true);
		    dtp.add(histFrame);
		    //////Test
		    panelDebug=new PanelDebug(this);
		    debugFrame.setContentPane(panelDebug);	    
			debugFrame.setLocation(900, 340);
			debugFrame.setSize(350, 310);
			debugFrame.setVisible(true);
			debugFrame.setBackground(Color.gray);
			dtp.add(debugFrame);
			try {
				debugFrame.setIcon(true);
			} catch (Exception e) { logger.fatal(e); }
			//////Tns 	    
		    panelTimeAndSales=new PanelTimeAndSales(this);
		    tnsFrame.setContentPane(panelTimeAndSales);	    
			tnsFrame.setLocation(900, 340);
			tnsFrame.setSize(350, 310);
			tnsFrame.setVisible(true);
			tnsFrame.setBackground(Color.gray);
			dtp.add(tnsFrame);
		    //////Company Info
			panelCompanyData=new PanelCompanyData(this);
		    cmpFrame.setContentPane(panelCompanyData);
			cmpFrame.setLocation(850, 10);
			cmpFrame.setSize(400, 250);
			cmpFrame.setVisible(true);
			cmpFrame.setBackground(Color.gray);
			dtp.add(cmpFrame);
			//////Class Vars
			panelReflection=new PanelReflection(lt,this,panelTimeAndSales,panelSettings,lt.wl());
		    reflectionFrame.setContentPane(panelReflection);	    
		    reflectionFrame.setLocation(900, 265);
		    reflectionFrame.setSize(350, 70);
		    reflectionFrame.setVisible(true);
		    reflectionFrame.setBackground(Color.gray);
		    dtp.add(reflectionFrame);				    		    
			
			jf.setVisible(true);		    
		    jf.addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
		        jf.setVisible(false);
		        jf.dispose();
		        System.exit(0);
		      }
		    });
		    
	  }      	
			    
		public JDesktopPane dtp() {
			return dtp;
		}
		public Font font() {
			return font;
		}
		public ClearLineChartEvent clearLineChartEvent() {
			return clearLineChartEvent;
		}
		public ClearBarChartEvent clearBarChartEvent() {
			return clearBarChartEvent;
		}

		public CompInfo cinfo() {
			return cinfo;
		}
		public CompDescr cdescr() {
			return cdescr;
		}
		public CompData cdata() {
			return cdata;
		}

		public void updateEveryTradeEvent(String sym) {
	    	try {
	    		updateNumberOfTrades.call(0); //integer 0 has no meaning - update before tnsUpdate
	    	} catch(Exception e) { logger.fatal(e); }
	    	try {
	    		lt.watchListUpdateOrAdd(0,sym);
	    	} catch(Exception e) { logger.fatal(e); }
	    	try {
	    		lt.timeAndSalesUpdate(sym);
	    	} catch(Exception e) { logger.fatal(e); }
	    	try {
	    		updateChartsEvent.call(sym); //todo fix out of bounds Exception
	    	} catch(Exception e) { logger.fatal(e); }
	    		
		}
		public void updateEveryQuoteEvent(String sym) {
			lt.watchListUpdateOrAdd(1,sym);
			//updateNumberOfSymbols.call("");
		}

		public synchronized void updateEverySecondEvent (String s1) { //throws KException, IOException {
			try {
				lt.timeUpdate(this);
			} catch (KException|IOException e) { logger.fatal(e);			} 
			updateWatchlistTime.call(""); //always display time - live or pb	
			panelDebug.updateWatchVars();
			panelSettings.phaseUpdate();
		}
		
		public void setSize(String frame,int x,int y) {
			switch(frame) {
			case "Reflection":
				reflectionFrame.setSize(x, y);
				break;
			case "Watchlist":
				wlFrame.setSize(x, y);
				break;
			default:
				break;
			}
			
		}
		public PanelHistorical panelHistorical() {
			return panelHistorical;
		}
		public PanelCompanyData panelCompanyData() {
			return panelCompanyData;
		}
		public PanelWatchlist panelWatchlist() {
			return panelWatchlist;
		}
		public PanelLineChart panelLinechart() {
			return panelLineChart;
		}
		public ChartLine linechart() {
			return linechart;
		}
		public ChartBar bchart() {
			return bchart;
		}
		
		public void updateCompanyInfoEvent(Integer id) {
			
	    	String lastSymbol=lt.dlmWl().getElementAt(id).split("[\\s]")[1]; //get 1st item
	    	clearLineChartEvent.call(lastSymbol);
	    	int idx=cinfo.idx(lastSymbol);
    		logger.info("symbol: "+lastSymbol+" cinfoIdx: "+idx+" listId:"+ id);
    		if(idx>=0) {
		    	panelCompanyData.labCompanyName(cinfo.name(idx));
	    		panelCompanyData.labSector(cinfo.sector(idx));
	    		panelCompanyData.labIndustry(cinfo.industry(idx));
	    		panelCompanyData.labCountry(cinfo.country(idx));
	    		
	    		panelHistorical.textSymbol(lastSymbol);
		    	panelCompanyData.textSymbol(lastSymbol);
    		}
		}
		public void title(String title) {
			this.title=title;
		}	
		
		void run() throws InterruptedException {
	   		JedisPubSub jedisPubSub = setupSubscriber();
	    }
		private JedisPubSub setupSubscriber() {
	        //final JedisPubSub 
			jedisPubSub = new JedisPubSub() {
	            @Override
	            public void onUnsubscribe(String channel, int subscribedChannels) {}
	            @Override
	            public void onSubscribe(String channel, int subscribedChannels) {}
	            @Override
	            public void onPUnsubscribe(String pattern, int subscribedChannels) {}
	            @Override
	            public void onPSubscribe(String pattern, int subscribedChannels) {}
	            @Override
	            public void onPMessage(String pattern, String channel, String message) {
					onReceiveTick(channel+" "+message);
	            }
	            @Override
	            public void onMessage(String channel, String message) {}
	        };
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                //String Query;
	                Jedis jedis = null;
	                try {
	                	ArrayList<String> symsSub=new ArrayList<>();
	        			if(lt.pbflag()){
	                		//String[] symsSub;//={"*:trades","*:quotes:*"};
	            			for(String s:lt.wl().symbols()) {
	            				symsSub.add(s+":trdpb");
	            				//symsSub.add(s+":qtspb:*");
	            			}
	                    	logger.info("subscribed to "+symsSub.toString());
	            			jedis=new Jedis("localhost"); jedis.psubscribe(jedisPubSub,symsSub.toArray(new String[symsSub.size()]) ) ;
	                    }
	            		else{
	            			//String[] symsSub;//={"*:trades","*:quotes:*"};
	            			for(String s:lt.wl().symbols()) {
	            				symsSub.add(s+":trades");
	            				symsSub.add(s+":quotes:*");
	            			}
	            			logger.info("subscribed to "+symsSub.toString());
	                    	jedis=new Jedis("localhost"); jedis.psubscribe(jedisPubSub,symsSub.toArray(new String[symsSub.size()]) ) ;
	            		}
	                } catch (Exception e) { 
	                	logger.fatal(e); 
	                	jedis.close();
	                }
	            }
	        }, "subscriberThread").start();
	        return jedisPubSub;
	    }

		void run2() throws InterruptedException {
	        JedisPubSub jedisPubSub2 = setupSubscriber2();
	    }
		JedisPubSub setupSubscriber2() {
	        //final JedisPubSub  //cannot be final if we want to unsubscribe later!
			jedisPubSub2 = new JedisPubSub() {
	            @Override
	            public void onUnsubscribe(String channel, int subscribedChannels) {}
	            @Override
	            public void onSubscribe(String channel, int subscribedChannels) {}
	            @Override
	            public void onPUnsubscribe(String pattern, int subscribedChannels) {}
	            @Override
	            public void onPSubscribe(String pattern, int subscribedChannels) {}
	            @Override
	            public void onPMessage(String pattern, String channel, String message) {
	               try {
	            	   lt.calcCandle(channel+" "+message);
	            	   //newWatchedTickEvent.call("");
					} catch (KException|IOException e) { logger.fatal(e); }
	            }
	            @Override
	            public void onMessage(String channel, String message) {}
	        };
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {  //todo close at the end???
	                	if(lt.captureRunning()&&lt.pbflag()){ // use separate instruments object?
	                        jedis2.psubscribe(jedisPubSub2, "AAPL:trdpb") ;
	                	}
	                	else {
	                		jedis2.psubscribe(jedisPubSub2, "AAPL:trades") ;
	                	}
	                } catch (Exception e) { 
	                	logger.fatal(e); 
	                	jedis2.close();
	                }
	            }
	        }, "subscriberThread2").start();
	        return jedisPubSub2;
	    }
	    
		public void onReceiveTick(String s1){
	    	String[] s2=s1.split("[:;\\s]");
	    	double p1=0.0;
	    	double v1=0.0;
	    	double b1=0.0;
	    	double a1=0.0;
	    	long t1=0L;
	    	int type=0; //0:trade, 1:quote
	    	try {
	    	String sym=s2[0],mod=s2[1];
	    	if(s2[1].equals("trades")||s2[1].equals("trdpb")) {
	    		type=0; //trade
	        	t1=lt.pbflag()? asLong(s2[2]) : U.timestampNow();
		    	p1=asDouble(s2[3]);
		    	v1=asDouble(s2[4]);
		    	if(p1!=0.0) { // do not push 1min 0.0 ticks
		    		try {
						lt.wl().addTrade(sym,t1,p1,v1);
					} catch (KException|IOException e) {
						e.printStackTrace();
					}	    	
		    	}
	    	} else if (s2[2].equals("bid")) { //quotesBid
	    		type=1; //quote
	        	t1=lt.pbflag()? asLong(s2[3]) : U.timestampNow();
	    		b1=asDouble(s2[4]);
	    		a1=0;
	    		try {
	    			lt.wl().addQuote(sym,t1,b1,a1,0);
	    		} catch (KException|IOException e) {
					e.printStackTrace();
				}
	    	} else { //quotesAsk
	    		type=1; //quote
	        	t1=lt.pbflag()? asLong(s2[3]) : U.timestampNow();
	    		b1=0;
	    		a1=asDouble(s2[4]);
	    		try {
	    			lt.wl().addQuote(sym,t1,b1,a1,0);
	    		} catch (KException|IOException e) {
					e.printStackTrace();
				}
	    	}
	    		
	     	lt.lastTime(t1);
	    		if(lt.captureRunning()) {
		    		if(type==0) {
		    			try{
		    			    updateEveryTradeEvent(sym); //new trade
		    	    	} catch(Exception e) { logger.fatal(e); }
		    			if(sym.equals("AAPL")){
		    				//	updateAlgoTickEvent.call("");
		    			}
		    		}
		    		else if(type==1) {
		    			try{
		    				updateEveryQuoteEvent(sym); //new quote	
		    			} catch(Exception e) { logger.fatal(e); }
		    		}
		    	}
	    	}catch(Exception e) { logger.fatal(e); }
	    }


		public PanelBarChart panelBarChart() {
			return panelBarChart;
		}

}
