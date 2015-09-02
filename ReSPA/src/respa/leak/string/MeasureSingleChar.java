package respa.leak.string;

public class MeasureSingleChar {

	
	
	private SolutionSet solutions;
	
	private int domain;
	
	
	
	public MeasureSingleChar(char token) {
		
		domain = 256;
		solutions = new SolutionSet(256);
		//solutions.addSolution(token);
	
	}
	
	
	
	
	

	public void applyEquals(int solution) {
		
		solutions.addSolution(solution);
		
	}

	
	public void applyNotEquals(int solution) {
		//TODO
		//solutions.addNonSolution(solution);
		
	}


	public void totalRelaxation() {
		
		solutions.totalRelaxation();
		
	}
	
	
	
	public double bitsLeaked() {

		return log2(((double)solutions.cardinality())/((double)domain))*(-1.0);
	
	}

	
	
	
	public static double log2(double num){
		return (double)(Math.log(num)/Math.log(2));
	}
	
	
}
