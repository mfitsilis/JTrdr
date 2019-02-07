package Jtrdr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import kiss.API;
import kx.c.KException;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextPane;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class PanelTable extends JPanel {
	final static Logger logger=Logger.getLogger(PanelTable.class);
  	private JTextField textField;
  	private TitledBorder tb;
  	private TimerPeriodic timerPeriodic;
  	private long period=1;
  	private LT lt;
  	private long secondsSinceStart=0;
  	private JLabel lblLastUpdated = new JLabel("last updated: ");
  	private ReadTable tab;
  	private String query;
  	private JTable table;
  	private JScrollPane scrollPane = new JScrollPane();
  	private int sizeX,sizeY;
	public PanelTable(FrameMain jfr, String query,ReadTable tab) {
		try {
			lt=LT.getInstance();
		} catch (Exception ex) { logger.fatal(ex); }
		setLayout(null);
		this.tab=tab;
		this.query=query;
		try {
			timerPeriodic=new TimerPeriodic(this,period);
		} catch (Exception e) { logger.fatal(e); }
		JPanel panel = new JPanel();
		panel.setName("");
		tb=new TitledBorder(null, "timer not set", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59));
		panel.setBorder(tb);
		panel.setBounds(10, 6, 457, 100);
		add(panel);
		panel.setLayout(null);
		JButton buttonExp = new JButton("+");
		JButton buttonRed = new JButton("-");
		buttonRed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(false);
				scrollPane.setBounds(10, 40, sizeX,sizeY);				
				buttonRed.setVisible(true);				
			}
		});
		buttonRed.setBounds(14, 22, 41, 29);
		panel.add(buttonRed);
		buttonExp.setBounds(14, 6, 41, 29);
		this.add(buttonExp);
		
		buttonExp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(true);
				scrollPane.setBounds(10, 110, 457, 211);
				buttonExp.setVisible(true);
			}
		});
		
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(1, 1, 86400, 1));
		spinner.setBounds(59, 22, 68, 28);
		panel.add(spinner);
		
		JButton btnSetTimer = new JButton("set Timer");
		btnSetTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tb.setTitle("Timer set to "+spinner.getValue().toString()+" seconds");
				period=API.asLong(spinner.getValue());
				setQuery(textField.getText());
				try {
					secondsSinceStart=0;
					updateEveryXSecondsEvent("");
					if(timerPeriodic.stopped()) {
						timerPeriodic.start(period);
					}
					else {
						timerPeriodic.stop();
						timerPeriodic.start(period);
					}
					
				} catch (Exception ex) { logger.fatal(ex); }
				panel.repaint();
				
			}
		});
		btnSetTimer.setBounds(130, 22, 95, 29);
		panel.add(btnSetTimer);
		
		
		lblLastUpdated.setBounds(284, 29, 167, 14);
		panel.add(lblLastUpdated);
		
		textField = new JTextField();
		textField.setBounds(14, 57, 356, 29);
		textField.setText(query);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnStop = new JButton("stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					timerPeriodic.stop();
					tb.setTitle("timer not set");
					panel.repaint();
				} catch (Exception e) { logger.fatal(e); }
			}
		});
		btnStop.setBounds(225, 22, 57, 29);
		panel.add(btnStop);
		
	  	  table= new JTable(tab.model());
	  	  table.setForeground(Color.white);
	  	  table.setBackground(Color.gray);
	  	  table.setFont(jfr.font());
	  	  table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
	        	    @Override
	        	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	        	    {
	        	        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        	        c.setBackground(row % 2 == 0 ? Color.darkGray : Color.black);
	        	        return c;
	        	    }
	        	});
	  	  table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	      TableColumnAdjuster tca = new TableColumnAdjuster(table);
	      tca.adjustColumns();
		  
		  table.setAutoCreateRowSorter(true);
		  scrollPane.setViewportView(table);

		scrollPane.setBounds(10, 110, 457, 211);
		scrollPane.setViewportView(table);
		add(scrollPane);
		
		panel.setVisible(false);
		scrollPane.setBounds(10, 40, 457, 211);				
		buttonRed.setVisible(true);				

		
	}
	public void updateEveryXSecondsEvent(String s1) { //needs another timer every second!
		secondsSinceStart++;
		lblLastUpdated.setText("last updated: "+ lt.timestr() );
		try {
			tab = new ReadTable(query);
		} catch (Exception ex) { logger.fatal(ex); }
		table.setModel(tab.model());
	 	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    TableColumnAdjuster tca = new TableColumnAdjuster(table);
	    tca.adjustColumns();
		
	}
	public void setQuery(String s1) {
		query=s1;
	}
	public void setFrameSize(int width, int height) {
		sizeX=width;
		sizeY=height;
	}
}

class TimerPeriodic {
	final static Logger logger=Logger.getLogger(Timers.class);
	private LT lt;
	private FrameMain jfr;
	private TimerTask1s task1s;
	private long period;
	private PanelTable ptab;
	private ScheduledExecutorService scheduler1s;
	public TimerPeriodic(PanelTable ptab,long period) throws KException, IOException{
		lt=LT.getInstance();
		this.ptab=ptab;
		this.period=period*1000;
		start(this.period);
	}
	public void start(long period) throws KException, IOException{
		task1s=new TimerTask1s(ptab);
		this.period=period*1000;
		scheduler1s = Executors.newSingleThreadScheduledExecutor(); //calc time every 1 sec
        scheduler1s.scheduleAtFixedRate(task1s, 0, this.period, TimeUnit.MILLISECONDS);
	}
	public void stop() throws KException, IOException{
		scheduler1s.shutdown();
	}
	public boolean stopped() {
		return scheduler1s.isShutdown();
	}
}

class TimerTask1s extends TimerTask { //calc time every x sec //separate thread that monitors time
	final static Logger logger=Logger.getLogger(MyTimerTask1s.class);
    private UpdateEveryXSecondsEvent updateEveryXSecondsEvent;
    private PanelTable ptab;
	private LT lt;
	public TimerTask1s(PanelTable ptab) throws KException, IOException {
		lt=LT.getInstance();
		this.ptab=ptab;
   		updateEveryXSecondsEvent=new UpdateEveryXSecondsEvent();
   		updateEveryXSecondsEvent.addListener(s1 -> ptab.updateEveryXSecondsEvent(""));
    }
	public void run(){
		updateEveryXSecondsEvent.call("");
    }
}
