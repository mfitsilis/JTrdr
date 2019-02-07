package Jtrdr;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.JInternalFrame;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JSeparator;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import kiss.API;
import kiss.util.As;
import kx.c.KException;

import javax.swing.ListSelectionModel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;

public class PanelWatchlist extends JPanel{
	final static Logger logger=Logger.getLogger(PanelWatchlist.class);
	private FrameMain jfr;
	private JLabel labTimeNow;
	private JLabel labMode;
	private JList<String> listWl;
	private SelectWatchlistEvent selectWatchlistEvent;
	//private StartCaptureEvent startCaptureEvent;
	private ArrayList<ChartLineStatic> linecharts=new ArrayList<>();
	private String formatHeaderShort=" %3s  %-5s %6s  %7s    %8s  %4s";
	private String formatHeaderLong= " %3s  %-5s %6s  %7s    %8s  %4s %5s %s %7s %7s %5s   %5s    %6s %6s %8s";
	private String headerShort=String.format(formatHeaderShort,
			"idx","sym","time","price","volume","$mln");
	private String headerLong=String.format(formatHeaderLong,
			"idx","sym","time","price","volume","$mln","trades","bidPrc","askPrc","spread",
			"dopen","prvdcl","change","change%","exDivDay");
	
	private UpdateNumberOfSymbols updateNumberOfSymbols;
	private FrameMinuteDataTree dataTree; 
	DefaultTreeModel dataTreeModel;
	private LT lt;
	//private Jtrdr jtrdr;
	private JTextArea textSymbols;
	private boolean dataTreeOpened=false;
	public PanelWatchlist(FrameMain jfr) throws KException, IOException {
		this.jfr=jfr;
		//jtrdr=Jtrdr.getInstance(jfr);
		lt=LT.getInstance();
		updateNumberOfSymbols=new UpdateNumberOfSymbols();	   		
   		updateNumberOfSymbols.addListener(s1 -> jfr.panelBarChart().updateNumberOfSymbols(s1));
		selectWatchlistEvent=new SelectWatchlistEvent();
		selectWatchlistEvent.addListener(i1 -> lt.listWlSelected(i1));
		selectWatchlistEvent.addListener(i1 -> jfr.updateCompanyInfoEvent(i1));
		selectWatchlistEvent.addListener(i1 -> jfr.panelLinechart().updateNumberOfTrades(i1));
		setBackground(Color.GRAY);
		setLayout(new MigLayout("", "[121px,grow][94px,grow][125px,grow][127px,fill]", "[1px][6px][16px][4px][2px][16px][2px][15px][14px][28px][12px][28px][12px][28px][12px][][16px][7px][359px]"));
		
		JSeparator separator = new JSeparator();
		add(separator, "cell 0 4 3 1,growx,aligny top");
		
		JButton btnStartCapture = new JButton("Start capture");
		btnStartCapture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
		    	if(lt.wl().symbols().size()==0) {} else {
		    		if(btnStartCapture.getText().equals("Start capture")){
				    	lt.captureRunning(true);
				    	logger.info("starting capture");
				    	//logger.info("clearing Data before capture");
				    	btnStartCapture.setText("Stop capture");
				    	btnStartCapture.setBackground(Color.red);
				    	  
				        try {
				          	Jtrdr jtr=Jtrdr.getInstance(jfr); //null;
				           	lt.captureRunning(true);
				    		jfr.run(); //sub for watchlist
				    		jfr.run2(); //sub in another thread to build candles
				    	} catch (Exception e) { logger.fatal(e); }  // playback
				        
		    		}
				    else{
				    	lt.captureRunning(false);
				     	logger.info("stopping capture - capture="+lt.captureRunning());
				      	btnStartCapture.setText("Start capture");
				      	btnStartCapture.setBackground(Color.green);
				      	ArrayList<String> symsSub=new ArrayList<>();
				      	for(String s:lt.wl().symbols()) {
					      	if(lt.pbflag()){
		            			symsSub.add(s+":trdpb");
					      	}
					      	else {
					      		symsSub.add(s+":trades");
					      		symsSub.add(s+":quotes:*");
						    }
				      	}
				      	logger.info("punsubscribing from "+symsSub.toString());
	
				      	if(lt.pbflag()){
				      		jfr.jedisPubSub.punsubscribe("*:trdpb");
				      		if(jfr.jedisPubSub2.isSubscribed()) {
				      			jfr.jedisPubSub2.punsubscribe("AAPL:trdpb");
				      		}
				      	}
				      	else{
				      		jfr.jedisPubSub.punsubscribe(symsSub.toArray(new String[symsSub.size()]) ) ;	            		  
				      		if(jfr.jedisPubSub2.isSubscribed()) {
				      			jfr.jedisPubSub2.punsubscribe("AAPL:trades");
				      		}
				      	}
				    }
		    	}
			}
		});
		btnStartCapture.setBackground(Color.GREEN);
		add(btnStartCapture, "cell 3 0 1 5,growx,aligny top");
		
		JButton btnShowMinuteData = new JButton("Show minute data");
		btnShowMinuteData.addActionListener(new ActionListener() {
			   
			public void actionPerformed(ActionEvent e) {
				dataTreeOpened=true;
				updateMinuteDataTree1m("");
			}
		});
		
		add(btnShowMinuteData, "cell 3 5 1 3,growx,aligny bottom");
		
		JButton btnClearAllData = new JButton("Clear all data");
		btnClearAllData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!lt.captureRunning()){
					updateNumberOfSymbols.call("");
					try {
						lt.clearData();
						//emptyInstruments();
					} catch (KException|IOException ex) { logger.fatal(ex); }
			       	
					lt.dlmWl().clear();
					lt.dlmTns().clear();

					//emptyInstruments();
					jfr.clearBarChartEvent();
					jfr.clearLineChartEvent();
					try {
				    	logger.info("request to clear Data");
						lt.clearData();
					} catch (KException|IOException ex) { logger.fatal(ex); }
					jfr.clearLineChartEvent().call("");
					jfr.clearBarChartEvent().call("");
				}				
			}
		});
		add(btnClearAllData, "cell 3 9,growx,aligny top");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 7 3 12,grow");
		
		listWl=new JList<>(lt.dlmWl());
		listWl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_ENTER ) {
			    	int selIdx=listWl.getSelectedIndex();
					if(selIdx>=0) {
				    	selectWatchlistEvent.call(selIdx);
			    	}
			    }
			}
		});
		LCellRenderer lcr=new LCellRenderer();
		listWl.setCellRenderer(new LCellRenderer());
		listWl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
			    if ((e.getClickCount() == 1)&&(e.getButton()==MouseEvent.BUTTON1) ) {
			    	int selIdx=listWl.getSelectedIndex();
					if(selIdx>=0) {//&&(selIdx!=lt.lastWlSelected())) {
				    	selectWatchlistEvent.call(selIdx);
			    	}
			    }
										
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2)&&(e.getButton()==MouseEvent.BUTTON1) ) {
					int selIdx=listWl.getSelectedIndex();
					if(selIdx>=0) {
						try {
							linecharts.add(new ChartLineStatic("test",jfr,selIdx));
							//new ChartBarStatic("test",name,value,jfr);
							//new ChartXYStatic("test",value2,value,jfr);
						} catch (KException|IOException e1) { logger.fatal(e); }
					}
				}
			}
		});
		listWl.setFont(jfr.font());
		scrollPane.setViewportView(listWl);
		
		listWl.setForeground(Color.WHITE);
		listWl.setBackground(Color.BLACK);
		
		//JLabel lblHeader = new JLabel(String.format("%3s %-5s %8s %8s %8s %4s","idx","sym","time","price","volume","$mln"));
		
		JLabel lblHeader = new JLabel(headerShort);
		
		add(lblHeader, "cell 0 5 3 1");
		lblHeader.setFont(jfr.font());
		
		
		
		labMode = new JLabel("mode:");
		setLabMode("");
		add(labMode, "cell 2 2,alignx right,aligny top");
		
		JButton btnReadInstuments = new JButton("Read Instruments");
		btnReadInstuments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lt.captureRunning()) { /*emptyInstruments();*/ } else {  //if capture not running
					updateNumberOfSymbols.call("");
					try {
						emptyTrades();
						lt.clearData();
						//emptyInstruments();
					} catch (KException|IOException e) { logger.fatal(e); }
			       	
					lt.dlmWl().clear();
					lt.dlmTns().clear();
					String syms="";
					ArrayList<String> dopenSymbols=new ArrayList<>();
					ArrayList<String> prevdcloseSymbols=new ArrayList<>();
					ArrayList<String> tradeSymbols=new ArrayList<>();
					ArrayList<String> numoftradesSymbols=new ArrayList<>();
					ArrayList<String> diviSymbols=new ArrayList<>();
					ArrayList<String> fdataSymbols=new ArrayList<>();
					ArrayList<Integer> dopenIdx=new ArrayList<>();
					ArrayList<Integer> prevdcloseIdx=new ArrayList<>();
					ArrayList<Integer> tradeIdx=new ArrayList<>();
					ArrayList<Integer> numoftradesIdx=new ArrayList<>();
					ArrayList<Integer> diviIdx=new ArrayList<>();
					ArrayList<Integer> fdataIdx=new ArrayList<>();
					KxTableModel table=null;
					KxTableModel table1=null;
					KxTableModel table2=null;
					KxTableModel table3=null;
					KxTableModel table4=null;
					KxTableModel table5=null;
					KxTableModel table6=null;
					String lastTradingDay=lt.datestr();
					
					for (String s:lt.wl().symbols()) syms+="`"+s;
					try {
						lastTradingDay=(String) lt.c3().k("`$($)h\"ltrday\"");
						if(lt.pbflag()) {
							table=(new ReadTable("0!select by sym from trades where sym in "+syms)).model();
							table1=(new ReadTable("0!select c lastprc by sym from trades where sym in "+syms)).model();
							table6=(new ReadTable("0!select from trades")).model();
						} 
						else {
							//1_delete bid,ask from update price:0.5*(bid+ask)from select time,sym,fills bid,fills ask from quotes where sym in `EUR`GBP
							table=(new ReadTable("0!h\"select by sym from trades where sym in "+syms+"\"")).model();
							table1=(new ReadTable("0!h\"select c lastprc by sym from trades where sym in "+syms+"\"")).model();
							table6=(new ReadTable("0!h\"select from trades"+"\"")).model();	
						}
						table2=(new ReadTable("0!h\"select from crsprc where crosstype=`open,date="+lastTradingDay+",sym in "+syms+"\"")).model();
						table3=(new ReadTable("0!h\"select from crsprc where crosstype=`close,date="+lastTradingDay+",sym in "+syms+"\"")).model();
						if(table3.getRowCount()==0) //use day before?
							table3=(new ReadTable("0!h\"select from crsprc where crosstype=`close,date="+lastTradingDay+",sym in "+syms+"\"")).model();
						table4=(new ReadTable("0!h\"select last `$($)Earnings,last ShsOutstand by sym from fdata where sym in "+syms+"\"")).model();
						table5=(new ReadTable("0!h\"select sym,`$($)date from select by sym from divi where sym in "+syms+"\"")).model();
					} catch (KException|IOException e) { logger.fatal(e); }
					
					for (int i=0;i<table2.getRowCount();i++) {
						dopenSymbols.add((String)table2.getValueAt(i,1));
					}
					for (int i=0;i<table3.getRowCount();i++) {
						prevdcloseSymbols.add((String)table3.getValueAt(i,1));
					}
					for (int i=0;i<table.getRowCount();i++) {
						tradeSymbols.add((String)table.getValueAt(i,0));
					}
					for (int i=0;i<table1.getRowCount();i++) {
						numoftradesSymbols.add((String)table1.getValueAt(i,0));
					}
					for (int i=0;i<table4.getRowCount();i++) {
						fdataSymbols.add((String)table4.getValueAt(i,0));
					}
					for (int i=0;i<table5.getRowCount();i++) {
						diviSymbols.add((String)table5.getValueAt(i,0));
					}
					dopenIdx=getListIdx(dopenSymbols,table2);
					prevdcloseIdx=getListIdx(prevdcloseSymbols,table3);
					tradeIdx=getListIdx(tradeSymbols,table);
					numoftradesIdx=getListIdx(numoftradesSymbols,table1);
					fdataIdx=getListIdx(fdataSymbols,table4);
					diviIdx=getListIdx(diviSymbols,table5);
					
					String sym;
					String type;
					InstrumentType itype;
					long time=0L;
					double price=0.0;
					double volume=0.0;
					double dopen=0.0;
					double prevdclose=0.0;
					long numOfTrades=0;
					String exDivDay="";
					String earningsDay="";
					double sharesOutstanding=0.0;
					for (int i=0;i<lt.wl().symbols().size();i++) {
						try {
							time=(tradeIdx.get(i)==-1)?0L:U.date2ts(table.getValueAt(tradeIdx.get(i),1));
							price=(tradeIdx.get(i)==-1)?0.0:(double)table.getValueAt(tradeIdx.get(i),2);
							volume=(tradeIdx.get(i)==-1)?0.0:(double)table.getValueAt(tradeIdx.get(i),3);
							numOfTrades=(numoftradesIdx.get(i)==-1)?0L:(long)table1.getValueAt(numoftradesIdx.get(i),1);
							dopen=(dopenIdx.get(i)==-1)?0.0:(double)table2.getValueAt(dopenIdx.get(i),3);
							prevdclose=(prevdcloseIdx.get(i)==-1)?0.0:(double)table3.getValueAt(prevdcloseIdx.get(i),3);
							exDivDay=(diviIdx.get(i)==-1)?"-":(String)table5.getValueAt(diviIdx.get(i),1);
							earningsDay=(fdataIdx.get(i)==-1)?"-":(String)table4.getValueAt(fdataIdx.get(i),1);
							sharesOutstanding=(fdataIdx.get(i)==-1)?0.0:(double)table4.getValueAt(fdataIdx.get(i),2);
							sym=lt.wl().symbols().get(i);
							type=lt.wl().types().get(i);
							itype=type.equals("STK")?InstrumentType.STK:InstrumentType.CASH;
							lt.wl().numOfTradesPerSym().add(API.asInt(numOfTrades));
							
							lt.wl().instruments().add(new Instrument(sym,itype,time,price,volume,dopen,prevdclose,exDivDay,earningsDay,sharesOutstanding));							
							U.last(lt.wl().instruments()).trades().clear();
							lt.dlmWl().addElement(String.format("%03d %s",i+1,U.last(lt.wl().instruments()).getWatchListInstrument(0)));
						} catch (KException|IOException e) { logger.fatal(e); }										
					}
					// fill the trades sofar (takes ~2 second)
					int idx=-1;
					for (int i=0;i<table6.getRowCount();i++) { //for all trades in trades table
						time=U.date2ts(table6.getValueAt(i, 0));
						sym=(String)table6.getValueAt(i, 1);
						price=(double)table6.getValueAt(i, 2);
						volume=(double)table6.getValueAt(i, 3);
						try {
							idx=lt.wl().symbols().indexOf(sym);
							if(idx!=-1)
								lt.wl().instruments().get(idx).updateInstrument(sym,time,price,volume);
						} catch (KException|IOException e) { logger.fatal(e); }
					}
	
					// update the watchlist since the numOfTrades have changed after adding the trades
					for (int i=0;i<lt.wl().symbols().size();i++) {
						lt.dlmWl().set(i,String.format("%03d %s",i+1,lt.wl().instruments().get(i).getWatchListInstrument(0)));
					}
//					ArrayList<Instrument> inst=lt.wl().instruments();
//					for(int i=0;i<lt.wl().symbols().size();i++) {
//						U.printlnx(i,inst.get(i).symbol(),inst.get(i).trades().size()); 
//					}
				}
				
			}
			
			private ArrayList<Integer> getListIdx(ArrayList<String> list,KxTableModel table) {
				String sym;
				ArrayList<Integer> prevdclose=new ArrayList<>();
				int idx=-1;
				for (int i=0;i<lt.wl().symbols().size();i++) {
					sym=lt.wl().symbols().get(i);
					idx=list.indexOf(sym);
					if(idx==-1) {
						prevdclose.add(-1);
						continue;
					}
					else prevdclose.add(idx);
				}
				return prevdclose;				
			}
		});
		add(btnReadInstuments, "cell 3 11,growx,aligny top");
		
		JButton btnResize = new JButton(">>");
		btnResize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(btnResize.getText().equals(">>")) {
					btnResize.setText("<<");
					lblHeader.setText(headerLong);
					jfr.setSize("Watchlist", 1000, 640);
				}
				else if(btnResize.getText().equals("<<")) {
					btnResize.setText(">>");
					lblHeader.setText(headerShort);
					jfr.setSize("Watchlist", 480, 640);
				}
			}
		});
		add(btnResize, "cell 3 13,growx,aligny top");
		
		JButton btnSymbolsUpdate = new JButton("Update Symbols");
		btnSymbolsUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!lt.captureRunning()){
					ArrayList<String> syms=new ArrayList<>();
					ArrayList<String> types=new ArrayList<>();
					String[] lineStr=textSymbols.getText().split("\n");
					if(!lineStr[0].equals("")) {
						for (String s:lineStr) {
							syms.add(s.split("\t")[0]);
							types.add(s.split("\t")[1]);
						}
						lt.wl().symbols(syms);
						lt.wl().types(types);
						updateNumberOfSymbols.call("");
					}
				}
			}		
		});
		add(btnSymbolsUpdate, "cell 3 15");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		add(scrollPane_1, "cell 3 18,grow");
		
		textSymbols = new JTextArea();
		scrollPane_1.setViewportView(textSymbols);
		textSymbols.setForeground(Color.WHITE);
		textSymbols.setBackground(Color.BLACK);
		textSymbols.setColumns(10);
		
		labTimeNow = new JLabel("time:");
		add(labTimeNow, "cell 0 2,growx,aligny top");
	}
	public void setLabMode(String s1) { //triggered by ModeUpdateEvent
    	if(lt.pbflag()) {
    		labMode.setForeground(Color.BLUE);
    		labMode.setText("Mode: playback");
    	}
    	else {
    		labMode.setForeground(Color.GREEN);
    		labMode.setText("Mode: live");
    	}
	}
	public void setTimeNow(String s1){ //every 1 second
  		labTimeNow.setText("Time: "+s1); //show time
	}
	public JList<String> listWl(){
		return listWl;
	}
	public void updateWatchlistTime(String string) {
		if(!lt.datetimestr().equals("1970.01.01 00:00:00"))
			labTimeNow.setText("Time: "+lt.datetimestr());
	}

	public void emptyTrades() {
		for (int i=0;i<lt.wl().symbols().size();i++) {
			if(lt.wl().instruments().size()>0)
				lt.wl().instruments().get(i).trades().clear();
		}
	}
	
	public void emptyInstruments() {
		lt.wl().instruments().clear();
		String sym;
		String type;
		InstrumentType itype;
		long time=0L;
		double price=0.0;
		double volume=0.0;
		double dopen=0.0;
		double prevdclose=0.0;
		long numOfTrades=0;
		String exDivDay="";
		String earningsDay="";
		double sharesOutstanding=0.0;
		for (int i=0;i<lt.wl().symbols().size();i++) {
			try {
				time=0;
				price=1.0;
				volume=1.0;
				numOfTrades=0;
				dopen=1.0;
				prevdclose=0.0;
				exDivDay="-";
				earningsDay="-";
				sharesOutstanding=0;
				sym=lt.wl().symbols().get(i);
				type=lt.wl().types().get(i);
				itype=type.equals("STK")?InstrumentType.STK:InstrumentType.CASH;
				lt.wl().numOfTradesPerSym().add(0);
				
				lt.wl().instruments().add(new Instrument(sym,itype,time,price,volume,dopen,prevdclose,exDivDay,earningsDay,sharesOutstanding));							
				U.last(lt.wl().instruments()).trades().clear();
				lt.dlmWl().addElement(String.format("%03d %s",i+1,U.last(lt.wl().instruments()).getWatchListInstrument(0)));
			} catch (KException|IOException e) { logger.fatal(e); }										
		}
		
	}

	public void updateMinuteDataTree1s(String s1) {
	}
	/** in playback mode it may update faster if ticks are published faster than real time */
	public void updateMinuteDataTree1m(String s1) {
		if(dataTreeOpened==true) {
			DefaultMutableTreeNode data;
			DefaultMutableTreeNode symNode;
			DefaultMutableTreeNode barNode;
			data=new DefaultMutableTreeNode("Minute Data");  
			dataTreeModel=new DefaultTreeModel(data);
			DefaultMutableTreeNode tickNode;
			String seriesBarSize="";
			String sym="";
			for(SData sd: lt.SD()){
				sym=sd.sym();
				symNode= new DefaultMutableTreeNode(sym);
				data.add(symNode);
				tickNode=new DefaultMutableTreeNode("Ticks: "+sd.T().size());
				symNode.add(tickNode);
			 //	for(Tick t: lt.SD().get(0).T()){ // java.util.ConcurrentModificationException
				for(int i=0;i<lt.SD().get(0).T().size();i++){
			//	 tickNode.add(new DefaultMutableTreeNode(t.tick2String()));
			 	 tickNode.add(new DefaultMutableTreeNode(lt.SD().get(0).T().get(i).tick2String()));
				}
			  //}
				for(BarSeries bs: sd.Bs()){
					barNode=new DefaultMutableTreeNode(bs.bs2String()+": "+bs.B().size());
					symNode.add(barNode);
					for(Bar b: bs.B()){ //exception...
						barNode.add(new DefaultMutableTreeNode(b.bar2String()));
					}
				}
			}
			
			if((dataTree==null)||!dataTree.mdFrame().isVisible()) {
				dataTree= new FrameMinuteDataTree(dataTreeModel, jfr);
			}
			else {
				dataTree.updateDataTree(dataTreeModel);
			}
		}
	}
	public void dataTreeOpened(boolean b) {
		dataTreeOpened=b;
	}
}

/** https://stackoverflow.com/questions/10251142/java-jlist-setting-color-of-items */
class LCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index,
	          boolean isSelected, boolean cellHasFocus) {
	     Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	     String[] val=((String)value).split("\\s+"); //split on whitespace
	     if (value instanceof String) {
              if (API.asDouble(val[12])>0.0) { //change>0
                  setForeground(Color.GREEN);
              } 
              else if (API.asDouble(val[12])<0.0) { //change<0
                  setForeground(Color.RED);
              }
              else {
                  setForeground(Color.WHITE);
              }
	          if (isSelected) {
	               setBackground(getBackground().darker());
	          }
	     } 
	     return c;
	}	
	
}
