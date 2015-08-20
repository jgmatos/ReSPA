package respa.leak.numeric.deprecatedStuff;



public class IntegerSolutionInterval extends IntegerSolution{

	
	private int lowerBound;
	
	
	private int upperBound;
	
	
	
	
	
	public IntegerSolutionInterval(int lowerBound, int upperBound) {
		
		this.lowerBound = lowerBound;
		
		this.upperBound = upperBound;
		
		
	}




	public int getLowerBound() {
		return lowerBound;
	}




	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}




	public int getUpperBound() {
		return upperBound;
	}




	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	
	
	
	
	
	
	
	public void intersect(IntegerSolutionInterval other) {
		
		if(this.lowerBound<other.getLowerBound())
			this.lowerBound = other.getLowerBound();
		
		if(this.upperBound>other.upperBound)
			this.upperBound=other.upperBound;
	
	}
	
	public void intersect(int other) {
		
		this.lowerBound=this.upperBound=other;
		
	}
	
	
	

	
	@Override
	public int cardinality() {
		
		int cardinality = (upperBound-lowerBound+1);

		if(cardinality>=0)
			return cardinality;
		else
			return 0;
		
	}




	@Override
	public void intersect(IntegerSolution solution) {

		if(solution instanceof IntegerSolutionInterval)
			intersect(solution);
		
		if(solution instanceof IntegerSolutionSingle){
			
			lowerBound = upperBound = ((IntegerSolutionSingle)solution).getSolution();
			
		}
			
		
	}
	
	
	
	
	
	
	
	
}
