package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import kx.c;
import kx.c.Flip;
import kx.c.KException;

public class CompData {
	final static Logger logger=Logger.getLogger(CompData.class);

	private ArrayList<Integer> types=new ArrayList<>();
	private ArrayList<String> TNames=new ArrayList<>();
	private ArrayList<tdata> TData=new ArrayList<>();
	private ArrayList<String> symbol=new ArrayList<>();

	private int nCols;
	private int nRows;
	private LT lt;
    private Flip flip;
    private KxTableModel model = new KxTableModel();

    private class tdata {
		private ArrayList<String> data=new ArrayList<>();
		tdata(String s1){
			add(s1);
		}
		public void add(String s1){
			data.add(s1);
		}
		public String get(int i){
			return data.get(i);
		}
		public void set(int i, String s1){
			data.set(i, s1);
		}
	}

    CompData() throws KException, IOException {
		lt=LT.getInstance();
        loadTdata("0!fdata");
    };

    public void loadTdata(String qry) {
        
        try { //connect to kdb
            model.setFlip((c.Flip) lt.c3().k("h\""+qry+"\""));
        } 
        catch (Exception e) { logger.fatal(e); U.exit(); }

        nCols = model.getColumnCount();
        nRows = model.getRowCount();
        
        for (int j = 0; j < nCols; j++) {
          	TNames.add(model.getColumnName(j));
            if(model.getValueAt(1, j) instanceof Long)
        		types.add(7);
        	else if(model.getValueAt(1, j) instanceof Double)
        		types.add(9);
        	else if(model.getValueAt(1, j) instanceof String)
        		types.add(11);
        	else if(model.getValueAt(1, j) instanceof java.util.Date)
        		types.add(15);
        }
        
        for (int j = 0; j < nCols; j++) {
        	for (int i = 0; i < nRows; i++) {
        		if (j==0) symbol.add(model.getValueAt(i, j).toString()); //get symbols
                if(types.get(j)==7){
                	Long tmp=(Long) model.getValueAt(i, j);
                	if(model.getValueAt(i, j)==null)
                		TData.get(j).add("");
                	else if(i==0)
                		TData.add(new tdata(tmp.toString()));
                	else 
                		TData.get(j).add(tmp.toString());
                }
                else if(types.get(j)==9){
                   	Double tmp=(Double) model.getValueAt(i, j);
                   	if(model.getValueAt(i, j)==null)
                		TData.get(j).add("");
                   	else if(i==0)
                		TData.add(new tdata(tmp.toString()));
                	else 
                		TData.get(j).add(tmp.toString());
                }
                else if(types.get(j)==11){
                	if(model.getValueAt(i, j)==null)
                		TData.get(j).add("");
                	else if(i==0)
                		TData.add(new tdata(model.getValueAt(i, j).toString()));
                	else{ 
                		TData.get(j).add(model.getValueAt(i, j).toString());
                	}
                }
                else if(types.get(j)==15){
                	if(model.getValueAt(i, j)==null)
                		TData.get(j).add("");
                	else if(i==0)
                		TData.add(new tdata(model.getValueAt(i, j).toString()));
                	else 
                		TData.get(j).add(model.getValueAt(i, j).toString());
                }
            }
        }

    }
    public String getdata(int i){
    	//return description.get(i);
    	String s1="";
    	if(i==-1) s1="enter an existing symbol";
    	else {
	    	for(int j=0;j<TData.size();j++){
	    		s1+=TNames.get(j)+": "+TData.get(j).get(i)+"\n";
	    	}
    	}
    	return s1;
    }
    public int getidx(String s1){
    	int t1= symbol.indexOf(s1); //also TData.get(0).get(i)
    	return t1;
    }
}
