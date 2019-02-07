package Jtrdr;

import java.awt.Color;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class FrameMinuteDataTree {
	private FrameMain jfr;
	private DefaultTreeModel dtm;
	private JTree jtree;
	private JInternalFrame mdFrame = new JInternalFrame("Minute data", true,true,true,true);
	private JScrollPane scrollpane_md;
	private DefaultMutableTreeNode data;
	private boolean dataTreeOpened=false;
	public FrameMinuteDataTree(DefaultTreeModel dtm,FrameMain jfr) {
		this.jfr=jfr;
		this.dtm=dtm;
		jtree=new JTree(dtm);
		jtree.setRootVisible(false);
		jtree.setBackground(Color.gray);
		scrollpane_md=new JScrollPane(jtree);
		InternalFrameListener internalFrameListener = new InternalFrameIconifyListener();
		mdFrame.addInternalFrameListener(internalFrameListener);
		mdFrame.getContentPane().add(scrollpane_md);
		mdFrame.setLocation(400, 200);
		mdFrame.setSize(400, 400);
		mdFrame.setVisible(true);
		jfr.dtp().add(mdFrame);
		mdFrame.toFront();		
	}
	public JInternalFrame mdFrame() {
		return mdFrame;
	}
	public void updateDataTree(DefaultTreeModel dtm) {
		this.dtm=dtm;
		jtree.setModel(dtm);
		jtree.expandRow(0);
	}
	class InternalFrameIconifyListener extends InternalFrameAdapter {
		  public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
			  JInternalFrame source = (JInternalFrame) internalFrameEvent.getSource();
			  jfr.panelWatchlist().dataTreeOpened(false);    
		  }
	}
}
