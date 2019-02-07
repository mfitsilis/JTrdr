package Jtrdr;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JInternalFrame;

import org.apache.log4j.Logger;

import kx.c.KException;

public class FrameChart {
	final static Logger logger=Logger.getLogger(FrameChart.class);
	private int startBar=0;
	private int numOfBars=65;
	private int endBar=startBar+numOfBars-1;
	private int selPos=0;
	private int barsPerDay;
	public FrameChart(FrameMain jfr,ChartHist chart,HistData hist,int startbar,int numofbars,int endbar,int selpos,int barsperday) {
		this.startBar=startbar;
		this.numOfBars=numofbars;
		this.endBar=endbar;
		this.selPos=selpos;
		this.barsPerDay=barsperday;
	  	JInternalFrame chartFrame = new JInternalFrame("Chart", true,true,true,true);
    	chartFrame.setLocation(200, 200);
		chartFrame.setSize(800, 450);
		startBar=0;//hist.nRows()-66;
		barsPerDay=390/hist.barSize();
		numOfBars=5*barsPerDay;
		selpos=0;
		try {
			chartFrame.setContentPane(new CanvasCandleChart(chartFrame,hist,startBar,numOfBars,selPos));
		} catch (IOException|KException e1) { logger.fatal(e1); }
		chartFrame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown()&&(e.getKeyCode() == KeyEvent.VK_LEFT)) {
					startBar--;endBar--;
				}
				else if (e.isControlDown()&&e.getKeyCode() == (KeyEvent.VK_RIGHT)) {
					startBar++;endBar++;
				}
				else if (e.isShiftDown()&&(e.getKeyCode() == KeyEvent.VK_LEFT)) {
					selPos--;
				}
				else if (e.isShiftDown()&&e.getKeyCode() == (KeyEvent.VK_RIGHT)) {
					selPos++;
				}
				else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					startBar-=13;endBar-=13;
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					startBar+=13;endBar+=13;
				}
				else if (e.getKeyCode() == KeyEvent.VK_UP) {
					endBar+=13;//numOfBars+=13;
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if(endBar>startBar) endBar-=13;//numOfBars-=13;
				}
				else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
					if(startBar>=0) { startBar-=65;endBar-=65; } //startBar-=65;
				}
				else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
					if(endBar<=hist.nRows()) { startBar+=65;endBar+=65; }
				}
				else if (e.getKeyCode() == KeyEvent.VK_HOME) {
					startBar=0;
				}
				else if (e.getKeyCode() == KeyEvent.VK_END) {
					startBar=hist.nRows()-numOfBars;
				}
				numOfBars=endBar-startBar+1;
				try {
					chartFrame.setContentPane(new CanvasCandleChart(chartFrame,hist,startBar,endBar,selPos));
				} catch (IOException|KException e1) { logger.fatal(e1); }
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		chartFrame.setFocusable(true);
		jfr.dtp().add(chartFrame);
		chartFrame.toFront();
		chartFrame.setVisible(true);

	}
}
