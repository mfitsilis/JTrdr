package Jtrdr;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import kx.c;
import kx.c.Flip;
import kx.c.KException;

public class ReadTable extends AbstractTableModel  {
	final static Logger logger=Logger.getLogger(ReadTable.class);	

	  private Flip flip;
	  private KxTableModel model = new KxTableModel();
	  private int nCols,nRows;
	  private String[] header;
	  private List<String[]> allRows;
	  private boolean ShowData=true;
	  private LT lt;
	
        public ReadTable(String s1) throws KException, IOException { //only executed once at the beginning!
        		lt=LT.getInstance();
		    	try {
 		    		if(!ShowData) lt.c3().k(s1);
                  	else {
     		    		//use Object result only when result is needed
                  		Object result=lt.c3().k(s1);
                  		flip=kx.c.td(result);
                      	nCols=flip.y.length;
                      	nRows=Array.getLength(flip.y[0]);
                      	header=flip.x;
                        model.setFlip((c.Flip) lt.c3().k(s1));
                  	}
 		        } catch (Exception e) { logger.fatal(e); } //finally {
 		          //  if (LT.c1 != null) {try{LT.c1.close();} catch (IOException ex) {}
 		          //}
 		          //}
		    	//if(ShowData) model.update();
        }
    	
        @Override
        public int getRowCount() {
        	if(nRows!=0) {
        		return nRows;
        	}
        	else 
        		return 0;
        }
        @Override
        public int getColumnCount() {
        	if(nCols!=0) {
        		return nCols;
        	}
        	else 
        		return 0;
    	}
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            	String[] row = allRows.get(rowIndex);
            	return row[columnIndex];
          }
        @Override
        public String getColumnName(int columnIndex) {
        	if(header!=null){
        		return header[columnIndex];
        	}
        	else 
        		return "";	
        }
        public void update() {
            fireTableStructureChanged(); //necessary!!!
        }   
        public KxTableModel model(){
        	return model;
        }
}
