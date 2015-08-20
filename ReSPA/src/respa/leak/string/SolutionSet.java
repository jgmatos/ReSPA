package respa.leak.string;

public class SolutionSet {

	
	private int [] solutions;
	
	
	public SolutionSet(int size) {
		
		solutions = new int[size];
		for(int i=0;i<solutions.length; i++)
			solutions[i]=Integer.MIN_VALUE;
	}


	
	public int cardinality(){
		
		int cardinality=0;
		for(int i=0;i<solutions.length;i++)
			if(solutions[i]>=0)
				cardinality++;

		return cardinality;
		
	}
	
	
	public void addSolution(int i) {
		
		solutions[i] = i;
		
	}
	
	
	public void addSolution(char c) {
		
		addSolution((int)c);
		
	}
	
	
	
	public void removeSolution(int i) {
		
		solutions[i] = Integer.MIN_VALUE;
		
	}
	
	public void removeSolution(char c) {
		
		removeSolution((int)c);
		
	}

	
	public void totalRelaxation() {
		
		for(int i=0;i<solutions.length;i++)
			solutions[i]=i;
		
	}
	
}





