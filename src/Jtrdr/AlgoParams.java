package Jtrdr;


public class AlgoParams {

	public AlgoParams(int num, String sym, String sectyp, int step,
			int qty, int live) {
		if (qty <= 0) { new MessageBox ("quantity cannot be negative","Warning"); }
		this.num = num;
		this.sym = sym;
		this.sectyp = sectyp;
		this.step = step;
		this.qty = qty;
		this.live = live;
	}
	int num;
	String sym;
	String sectyp;
	int step; 
	int qty;
	int live;
}
