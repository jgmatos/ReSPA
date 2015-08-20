package respa.leak.numeric.deprecatedStuff;

public class IntegerSolutionSingle extends IntegerSolution{

	
	
	private int solution;
	
	
	public IntegerSolutionSingle(int solution) {

		this.solution = solution;
		
	}
	
	
	
	
	@Override
	public void intersect(IntegerSolution solution) {

		//do nothing
		
	}

	@Override
	public int cardinality() {
		
		return 1;
	
	}

	
	public int getSolution() {
		
		return solution;
		
	}
	
	
}
