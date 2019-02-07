package Jtrdr;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JInternalFrame;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanelSettings extends JPanel{
	final static Logger logger=Logger.getLogger(PanelSettings.class);
	private JTextField textQuery;
	private FrameMain jfr;
    private ActionListener changeModeEvent;
    private JRadioButton radioPlayback;
    private JRadioButton radioLive;
    private JLabel labWeekday;
    private JLabel labHoliday;
    private JLabel labMarketHours;
	private JLabel labPhase;
	private LT lt;
	
	public PanelSettings(FrameMain jfr) throws KException, IOException {
		lt=LT.getInstance();
		this.jfr=jfr;
	    setBackground(Color.GRAY);
		setLayout(new MigLayout("", "[144px][12px][20px][12px][46px][10px][69px]", "[14px][9px][14px][11px][14px][9px][22px][14px][28px]"));
		
		JButton btnRunAlgo = new JButton("run query");
		btnRunAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textQuery.getText().equals("")){
					new FrameTable(jfr,"Query",textQuery.getText(),300,100,600,300);
				}
			}
		});
		add(btnRunAlgo, "cell 4 8 3 1,growx,aligny top");
		
		JLabel lblWeekday = new JLabel("weekday");
		add(lblWeekday, "cell 2 0 3 1,alignx left,growy");
		
		JLabel lblHoliday = new JLabel("holiday:");
		add(lblHoliday, "cell 2 2 3 1,grow");
		
		textQuery = new JTextField();
		textQuery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_ENTER ) {
					if(!textQuery.getText().equals("")){
						new FrameTable(jfr,"Query",textQuery.getText(),300,100,600,300);
					}
			    }
			}
		});
		textQuery.setForeground(Color.WHITE);
		textQuery.setBackground(Color.BLACK);
		add(textQuery, "cell 0 8 3 1,growx,aligny top");
		textQuery.setColumns(10);
		
		JLabel lblEnterExpressionTo = new JLabel("Enter expression to show table:");
		add(lblEnterExpressionTo, "cell 0 7 4 1,alignx center,growy");
		
		JLabel lblMarketHours = new JLabel("market hours:");
		add(lblMarketHours, "cell 2 4 3 1,alignx left,growy");
		
		JLabel lblPhase = new JLabel("phase:");
		add(lblPhase, "cell 2 6 3 1,growx,aligny top");
		
		labWeekday = new JLabel("");
		add(labWeekday, "cell 6 0,grow");
		
		labHoliday = new JLabel("");
		add(labHoliday, "cell 6 2,grow");
		
		labMarketHours = new JLabel("");
		add(labMarketHours, "cell 6 4,grow");
		
		labPhase = new JLabel("");
		add(labPhase, "cell 6 6,grow");
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		panel.setBorder(new TitledBorder(null, "Mode", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, "cell 0 0 1 7,grow");
		panel.setLayout(null);
		
		
		radioPlayback = new JRadioButton("Playback");
		radioPlayback.setBounds(23, 23, 90, 18);
		
		radioLive = new JRadioButton("Live");
		radioLive.setBounds(23, 53, 90, 18);
		if(lt.pbflag())
			radioPlayback.setSelected(true);
		else
			radioLive.setSelected(true);
		ButtonGroup group = new ButtonGroup();
		group.add(radioPlayback);
        group.add(radioLive);
		radioPlayback.setActionCommand("playback");
        radioLive.setActionCommand("live");
		changeModeEvent = new ChangeModeEvent(jfr,this);

		radioPlayback.addActionListener(changeModeEvent);
        radioLive.addActionListener(changeModeEvent);
        
        panel.add(radioPlayback);
		panel.add(radioLive);
				
	}
	public JRadioButton radPlayback() {
		return radioPlayback;
	}
	public JRadioButton radLive() {
		return radioLive;
	}
	public void phaseUpdate() {
		labWeekday.setText(LT.WEEKDAYS[lt.tday()]);
		labHoliday.setText(lt.todayHoliday());
		labMarketHours.setText(lt.marketHours());
		labPhase.setText(lt.getPhase());
	}
}

class ChangeModeEvent implements ActionListener {
	final static Logger logger=Logger.getLogger(PanelSettings.class);
	private PanelSettings pS;
	private LT lt;
	private boolean playback; //must save states!
	private boolean live;
	private ModeUpdateEvent modeUpdateEvent; 
	public ChangeModeEvent(FrameMain jfr,PanelSettings panelSettings) throws KException, IOException {
		lt=LT.getInstance();
		modeUpdateEvent=new ModeUpdateEvent();
		modeUpdateEvent.addListener(s1 -> jfr.panelWatchlist().setLabMode(s1));
		this.pS=panelSettings;
		playback=pS.radPlayback().isSelected();
		live=pS.radLive().isSelected();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(lt.captureRunning()){ //if capture running no switch at all
			if(!e.getActionCommand().equals("playback")&&(playback)){ 
				pS.radPlayback().setSelected(true);
				//new MessageBox("Capture is running. Stop capture first.","Info");
			}
			else if(!e.getActionCommand().equals("live")&&(live)){
				pS.radLive().setSelected(true); //only provide true state!
				//new MessageBox("Capture is running. Stop capture first.","Info");
			}
		}
		else{ //capture not running
			if(playback){ // pb switch to live
				if(e.getActionCommand().equals("playback")){ //do not change
					pS.radPlayback().setSelected(true); playback=true;live=false; 	lt.pbflag(true);
				}
				else { // change
					pS.radLive().setSelected(true); live=true;playback=false;	lt.pbflag(false);
					logger.info("change mode to live");
				}
			} 
	        else { // live switch to pb - if more than 2 buttons, add as many else if with a pair of if/else and e.g. playback=true;live=opt3=!playback;
				if(!e.getActionCommand().equals("playback")){
					pS.radLive().setSelected(true); live=true;playback=false; 	lt.pbflag(false);
				}
				else{ //do not change
					pS.radPlayback().setSelected(true); playback=true;live=false;	lt.pbflag(true);
					logger.info("change mode to playback");
				}
			}	
		}
		modeUpdateEvent.call("");
	}
}
