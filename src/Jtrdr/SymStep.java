package Jtrdr;

import java.util.ArrayList;

public class SymStep {
	private String sym;
	private ArrayList<Integer> Steps=new ArrayList<>();
	public SymStep(String sym, int step) {
		this.sym = sym;
		Steps.add(step);
	}
	public SymStep(SymStep s1){
		this.sym=s1.sym;
		this.Steps=s1.Steps;
	}
	public String sym(){
		return this.sym;
	}
	public ArrayList<Integer> Steps(){
		return Steps;
	}
}
