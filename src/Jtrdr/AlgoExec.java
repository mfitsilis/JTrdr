package Jtrdr;

import java.io.IOException;

import kx.c.KException;

/**
 * calls the correct algo and passes the name of the symbol
 */
class AlgoExec extends Algo {
	/**
	 * @param s1 symbol name
	 * @param s2 name of algo
	 * @throws IOException
	 * @throws KException	
	 */
	AlgoExec(String s1,String s2) throws IOException, KException{
		String[] s0= s2.split(" ");
		if(s0.length==2) s1+=" "+s0[1];
		else if(s0.length==3) s1+=" "+s0[1]+" "+s0[2];
		
		switch (s0[0]){
			case "test":
				break;
			default:
				break;
		}
	}

	public AlgoExec() throws IOException, KException {
	}
	
}
//Add more class algos here
