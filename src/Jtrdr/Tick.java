package Jtrdr;

import java.io.IOException;

import kx.c.KException;

/**
 * The Tick class stores information inside a Tick : time : integer and price,volume : double
 */
public class Tick {
    private long time;
    private double price;
    private double volume;
    /**
     * Constructor of a Tick
     * @param time milliseconds since epoch (1970.01.01 00:00:00)
     * @param price in dollars : double
     * @param volume number of lots (100xshares) : double
     */
    private LT lt;
    public Tick(long time, double price,double volume) throws KException, IOException {
    	lt=LT.getInstance();
    	this.time=time;
        this.price=price;
        this.volume=volume;
    }
    public long time(){
    	return time;
    }
    public double price(){
    	return price;
    }
    public double volume(){
    	return volume;
    }
    public String tick2String(){
    	return String.format("%s %.2f %.0f", U.timestamp2timestr(time,lt.pbflag()),price,volume);
    }
    
}
