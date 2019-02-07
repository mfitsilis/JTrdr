package Jtrdr;

import java.io.IOException;
import java.util.ArrayList;

import kx.c.KException;

/**
 * for running live algos with LT
 */
public class AlgoLive {
	double op;
	double hi;
	double lo;
	double cl;
	double vo;
	LT lt;
	
	public AlgoLive() throws KException, IOException {
		lt=LT.getInstance();
	}
/*    public void opgcrs(AData a) { //final merged opg orders
        if (TMtimecross(tmst/1000, opgstr)>0) { //todo:use timer in RT
            callopg(a);
        }
    }
    public void dopencrs(AData a) { //not for all algos
        if (TMtimecross(tmst/1000, crsopstr)>0) {
            calldopen(a);
        }
    }
    public void moccrs(AData a) { //final merged moc orders
        if (TMtimecross(tmst/1000, mocstr)>0) {
            callmoc(a);
        }
    }
    public void frstbar(AData a) {
        if ((TMtimecross(tmst/1000, a.time1stbar)>0)) {
            callfrstbar(a);
        }
    }
    */
}
//Add more class algos here
