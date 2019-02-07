package Jtrdr;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import kx.c.KException;

import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PanelHistorical extends JPanel{
	final static Logger logger=Logger.getLogger(PanelHistorical.class);
	private JTextField textAlgo;
	private JTextField textSymbol;
	private FrameMain jfr;
	private ChartHist chart;
    private ChartPanel cp;
	private HistData hist;
	private JPanel jp=new JPanel();
	private String[] steps = { "30", "15", "10", "5", "3","2","1","d" };
	private int startBar=0;
	private int numOfBars=65;
	private int endBar=startBar+numOfBars-1;
	private int selPos=0;
	private int barsPerDay;
	private LT lt;
	int[] isteps={30,15,10,5,3,2,1,0};
	JComboBox cbSteps = new JComboBox(steps);
	public PanelHistorical(FrameMain jfr) throws KException,IOException{
		lt=LT.getInstance();
		this.jfr=jfr;
		setBackground(Color.GRAY);
		setLayout(new MigLayout("", "[77px][7px][122px][154px]", "[28px][28px]"));
		
		JButton btnGetData = new JButton("Get Data");
		btnGetData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textSymbol.getText().equals("")){
			   		 try {
			   			int step=isteps[cbSteps.getSelectedIndex()];
			   			hist=new HistData(textSymbol.getText(),step);
			   			chart=new ChartHist(textSymbol.getText(),0,hist.nRows(),hist.result());
			   		 } catch (KException|IOException e1) { logger.fatal(e1); }
			   		 new FrameChart(jfr,chart,hist, startBar,numOfBars,endBar,selPos,barsPerDay);
				}
			}
		});
		add(btnGetData, "cell 3 0,growx,aligny top");
		
		JLabel lblSymbol = new JLabel("Enter Symbol:");
		add(lblSymbol, "cell 0 0,alignx left,aligny center");
		
		JLabel lblAlgo = new JLabel("Enter Algo:");
		add(lblAlgo, "cell 0 1,growx,aligny center");
		
		textAlgo = new JTextField();
		textAlgo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		textAlgo.setForeground(Color.WHITE);
		textAlgo.setBackground(Color.BLACK);
		add(textAlgo, "cell 2 1,alignx left,aligny top");
		textAlgo.setColumns(10);
		
		textSymbol = new JTextField();
		textSymbol.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textSymbol.setText(textSymbol.getText().toUpperCase());
				if (e.getKeyCode()==KeyEvent.VK_ENTER ) {
					if((!textSymbol.getText().equals(""))&&(lt.wl().symbols().indexOf(textSymbol.getText())!=-1)){
				   		 try {
				   			int step=isteps[cbSteps.getSelectedIndex()];
				   			hist=new HistData(textSymbol.getText(),step);
				   			chart=new ChartHist(textSymbol.getText(),0,hist.nRows(),hist.result());
				   		 } catch (KException|IOException e1) { logger.fatal(e1); }
				   		 new FrameChart(jfr,chart,hist, startBar,numOfBars,endBar,selPos,barsPerDay);
					}
				}
			}
		});
		textSymbol.setForeground(Color.WHITE);
		textSymbol.setBackground(Color.BLACK);
		textSymbol.setColumns(10);
		add(textSymbol, "cell 2 0,alignx left,aligny top");
		
		JButton btnRunAlgo = new JButton("Run Algo");
		btnRunAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if((!textSymbol.getText().toUpperCase().equals(""))&&
						(lt.wl().symbols().indexOf(textSymbol.getText().toUpperCase())!=-1)&&
						!textAlgo.getText().equals("")) {
						
						 AlgoExec algo=null;
						 try {
							//hist=new HistData(jfr.edit_hist_sym().getText(),step);
						 	algo = new AlgoExec(textSymbol.getText(), textAlgo.getText());
						 } catch (IOException|KException e1) { logger.fatal(e1); } 
						 JPanel panelAlgo = new JPanel();
						 JInternalFrame algoFrame = new JInternalFrame("Algo backtest results", true,true,true,true);
						 JTable tab = new JTable(algo.model());
						 //JTable tab = new JTable(algo.getmodel());
						 tab.setForeground(Color.white);
						 tab.setFont(jfr.font());
						 tab.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
						 {
							    @Override
							    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
							    {
							        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
							        c.setBackground(row % 2 == 0 ? Color.darkGray : Color.black);
							        return c;
							    }
							});
						 tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						 TableColumnAdjuster tca = new TableColumnAdjuster(tab);
						 tca.adjustColumns();
						 JScrollPane scrollpaneAlgo = new JScrollPane(tab, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						 algoFrame.getContentPane().add(scrollpaneAlgo);
						 algoFrame.setLocation(400, 200);
						 algoFrame.setSize(400, 400);
						 algoFrame.setVisible(true);
						 jfr.dtp().add(algoFrame);
						 algoFrame.toFront();
					  }
			}
		});
		add(btnRunAlgo, "cell 3 1,growx,aligny top");
	}
	public void textSymbol(String s1) {
		this.textSymbol.setText(s1);
	}
	public void textAlgo(String s1) {
		this.textAlgo.setText(s1);
	}
}
