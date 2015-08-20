package respa.leak.numeric.deprecatedStuff;

import java.util.ArrayList;

public class Interval {

	
	
	private ArrayList<IntegerSolution> interval;
	
	
	
	public Interval() {
		
		interval = new ArrayList<IntegerSolution>();
		
	}
	
	
	
	public void addSolution(int i) {
		
		IntegerSolutionSingle singleint = new IntegerSolutionSingle(i);
		
	}
	
	
	public void addSolution(char c) {
		
		addSolution((int)c);
		
	}
	
	
	
	public void removeSolution(int i) {
		
		
	}
	
	public void removeSolution(char c) {
		
		removeSolution((int)c);
		
	}
	
	
	
	
	
	//o resto eh tudo igual.
	
	
	
	
}
