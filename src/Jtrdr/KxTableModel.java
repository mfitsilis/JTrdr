package Jtrdr;

import java.lang.reflect.Array;

import javax.swing.table.AbstractTableModel;

import kx.c;

public class KxTableModel extends AbstractTableModel {

	    //private static final long serialVersionUID = 1L; //to remove serial warning
	    private c.Flip flip;
	    public void setFlip(c.Flip data) {
	        this.flip = data;
	    }
	    public int getRowCount() {
	        return Array.getLength(flip.y[0]);
	    }
	    public int getColumnCount() {
	        return flip.y.length;
	    }
	    public Object getValueAt(int rowIndex, int columnIndex) {
	        return c.at(flip.y[columnIndex], rowIndex);
	    }
	    public String getColumnName(int columnIndex) {
	        return flip.x[columnIndex];
	    }
	    //for adata table
	  /*  public String getsym(int rowIndex) {
	        return (String)c.at(flip.y[0], rowIndex);
	    }
	    public int getalgo(int rowIndex) {
	        return (int)(long)c.at(flip.y[1], rowIndex);
	    }
	    public int getstep(int rowIndex) {
	        return (int)(long)c.at(flip.y[2], rowIndex);
	    }
	    public int getlive(int rowIndex) {
	        return (int)(long)c.at(flip.y[5], rowIndex);
	    }
	    public int getqty(int rowIndex) {
	        return (int)(long)c.at(flip.y[6], rowIndex);
	    }
	    public int getdir(int rowIndex) {
	        return (int)(long)c.at(flip.y[7], rowIndex);
	    }
	    public int getpos(int rowIndex) {
	        return (int)(long)c.at(flip.y[8], rowIndex);
	    }
	    public int getntrades(int rowIndex) {
	        return (int)(long)c.at(flip.y[9], rowIndex);
	    }
	    public int getnorders(int rowIndex) {
	        return (int)(long)c.at(flip.y[10], rowIndex);
	    }
*/	    
}
