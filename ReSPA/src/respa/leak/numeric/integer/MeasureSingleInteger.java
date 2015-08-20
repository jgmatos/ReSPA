package respa.leak.numeric.integer;

import respa.leak.numeric.interval.DisjointException;
import respa.leak.numeric.interval.IsInfiniteException;
import respa.leak.numeric.interval.integer.Interval;
import respa.leak.numeric.interval.integer.SubInterval;

public class MeasureSingleInteger {

	
	
	private Interval solutions;
	
	private double domain;
	
	private boolean flag=false;
	
	public MeasureSingleInteger(int value) {
		
		solutions = new Interval();
		domain = 4294967296.0;
		applyEquals(value);
		flag=false;
	}
	
	
	
	
	public void applyEquals(int solution) {
		
		SubInterval si = new SubInterval(solution,solution);
		solutions.union(si);
		flag=true;
	}

	
	public void applyNotEquals(int solution) {

		if(solutions.contains(solution))
			try {
				
				solutions.getSubInterval(solution).subtract(new SubInterval(solution, solution));
			
			} catch (DisjointException e) {}
	
	}


	public void totalRelaxation() {
		
		
		solutions.clearInterval();
		SubInterval si = new SubInterval(Integer.MIN_VALUE, Integer.MAX_VALUE);
		solutions.union(si);
		
	}
	
	
	
	public void applyGreaterThan(int lowerBound) {
		
		SubInterval si = new SubInterval(lowerBound,Integer.MAX_VALUE);
		solutions.union(si);
		
	}
	
	public void applyLowerThan(int upperBound) {
		
		SubInterval si = new SubInterval(Integer.MIN_VALUE,upperBound);
		solutions.union(si);
		
	}
	
	
	
	
	
	
	
	public double bitsLeaked() {

		try {
			
			if(!flag)
				return 0.0;
			else
				return log2(((double)solutions.size())/((double)domain))*(-1.0);
		} catch (IsInfiniteException e) {
			return 0.0;
		}
	
	}

	
	
	
	public static double log2(double num){
		return (double)(Math.log(num)/Math.log(2));
	}
	
	
	
	
}
