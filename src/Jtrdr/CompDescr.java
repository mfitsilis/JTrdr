package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import kx.c;
import kx.c.Flip;
import kx.c.KException;

public class CompDescr {
	final static Logger logger=Logger.getLogger(CompDescr.class);

private ArrayList<String> symbol=new ArrayList<>();
private ArrayList<String> description=new ArrayList<>();

private int nCols;
private int nRows;
private ArrayList<String> columnNames=new ArrayList<>();
private LT lt;
//private Flip flip;
private String qry = "get `:companyinfo";
KxTableModel model = new KxTableModel();

CompDescr() throws KException, IOException {
		lt=LT.getInstance();
        loadcompdata();
    };
    
    void loadcompdata() {
        try { //connect to kdb
            model.setFlip((c.Flip) lt.c3().k("h\""+qry+"\""));
        } 
        catch (Exception e) { logger.fatal(e); U.exit(); }

        nCols = model.getColumnCount();
        nRows = model.getRowCount();
        
        for(int i=0;i<nCols;i++){
        	columnNames.add(model.getColumnName(i));
        }        
        
        for (int i = 0; i < nRows; i++) {
            symbol.add((String)model.getValueAt(i, 0));
            description.add((String)model.getValueAt(i, 1));
        }
    }
    public String getdescription(int i){
    	if(i==-1) 
    		return "enter an existing symbol";
    	else
    		return description.get(i);
    }
    public int getidx(String s1){
    	return symbol.indexOf(s1);
    }
}

