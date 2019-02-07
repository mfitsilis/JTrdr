package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import kx.c;
import kx.c.Flip;
import kx.c.KException;

public class CompInfo {
final static Logger logger=Logger.getLogger(CompInfo.class);

private ArrayList<String> symbol=new ArrayList<>();
private ArrayList<String> name=new ArrayList<>();
private ArrayList<String> sector=new ArrayList<>();
private ArrayList<String> industry=new ArrayList<>();
private ArrayList<String> country=new ArrayList<>();
private ArrayList<String> description=new ArrayList<>();

private int nCols;
private int nRows;
private ArrayList<String> columnNames=new ArrayList<>();
private LT lt;
private Flip flip;
private String qry = "get `:company";
private KxTableModel model = new KxTableModel();

CompInfo() throws KException, IOException {
		lt=LT.getInstance();
        loadcompdata();
    };
    
    void loadcompdata() {
        try { //connect to kdb
           // c1 = new c("localhost", 5500);
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
            name.add((String)model.getValueAt(i, 1));
            sector.add((String)model.getValueAt(i, 2));
            industry.add((String)model.getValueAt(i, 3));
            country.add((String)model.getValueAt(i, 4));
        }
    }
    public String name(int i){
    	try {name.get(i);} catch(Exception e) { logger.fatal(e); }
    	return name.get(i);
    }
    public String sector(int i){
    	return sector.get(i);
    }
    public String industry(int i){
    	return industry.get(i);
    }
    public String country(int i){
    	return country.get(i);
    }
    public int idx(String s1){
    	return symbol.indexOf(s1);
    }
}

