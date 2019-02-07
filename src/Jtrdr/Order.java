package Jtrdr;

public class Order {
	    private int live;
	    private String action;
	    private String typ;
	    private String sym;
	    private int qty;
		private double auxprc;
	    private double lmtprc;
	    private String gat;
	    private String exch;
	    private String tif;
	    private String orderref;
	    private String oca;
	    private String oref;

	    private String status;
	    private int barOpened;
	    private int barCancelled;
	    private int barFilled;
	    private double prcSubmitted;
	    private double prcFilled;
	    
	    public Order(int live, String action, String typ, String sym, int qty,
				double auxprc, double lmtprc, String gat, String exch,
				String tif, String orderref, String oca, String oref) {
			this.live = live;
			this.action = action;
			this.typ = typ;
			this.sym = sym;
			this.qty = qty;
			this.auxprc = auxprc;
			this.lmtprc = lmtprc;
			this.gat = gat;
			this.exch = exch;
			this.tif = tif;
			this.orderref = orderref;
			this.oca = oca;
			this.oref = oref;
		}
	    public Order(String action, String typ, String sym, int qty,double prc,int barNum) {
	        this.action = action;
		 	this.typ = typ;
		 	this.sym = sym;
		 	this.qty = qty;
		 	this.prcSubmitted = prc;
		 	this.barOpened = barNum;
		 	this.status="active";
	    }
	    public void cancelOrder(int barNum) {
	 		this.status="cancelled";
	 		this.barCancelled = barNum;
	    }
	    public void orderFilled(int barNum,double prc) {
	    	this.status="filled";
	    	this.prcFilled=prc;
	    	this.barFilled=barNum;
	    }
}
