package respa.leak.numeric.integer;

import java.util.HashSet;
import java.util.Random;

public class IntegerInterval implements Cloneable{

	
	private int begin;
	
	private int end;
	
	private HashSet<Integer> excluding;
	
	
	
	
	public IntegerInterval() {
		
		begin = Integer.MIN_VALUE;
		end = Integer.MAX_VALUE;
		excluding = new HashSet<Integer>();
		
	}
	
	
	
	public IntegerInterval(int begin, int end) {
		
		this.begin = begin;
		this.end = end;
		excluding = new HashSet<Integer>();
		
	}
	
	private IntegerInterval(int begin, int end, HashSet<Integer> excluding) {
		
		this.begin = begin;
		this.end = end;
		this.excluding = excluding;
		
	}



	public int getBegin() {
		return begin;
	}



	public void setBegin(int begin) {
		this.begin = begin;
	}



	public int getEnd() {
		return end;
	}



	public void setEnd(int end) {
		this.end = end;
	}
	
	
	
	
	
	
	public void remove(int i) {
		
		if(i>=begin && i<=end)
			excluding.add(i);
		
	}
	
	
	
	
	public double size() {
		
		double beginDouble = (double) begin;
		double endDouble = (double) end;
		double intervalsize = ((double) (endDouble - beginDouble +1.0));
		
		if(intervalsize - excluding.size() <=1.0)
			return 1.0;
		else
			return intervalsize;
		
	}
	
	
	
	public void greaterThan(int c) {
		
		if(c > begin)
			begin = c;
		
	}
	
	public void lowerThan(int c) {
		
		if(c < end)
			end = c;
		
	}
	
	
	
	
	public int getRandomSolution() {

		Random random = new Random();
		int solution=0;
		
		do{
			solution = begin+random.nextInt(end);
		}
		while(excluding.contains(solution));
		
		return solution;
		
	}
	
	
	
	@Override
	public Object clone() {
		
		HashSet<Integer> clone = new HashSet<Integer>();
		for(Integer i: this.excluding)
			clone.add(new Integer(i.intValue()));
		
		return new IntegerInterval(begin, end, clone);
		
	}
	
	
}
