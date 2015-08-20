package respa.cost;


public class CostlyChar implements CostlyVar{

	
	private final int cost = 1;
	
	private String id;
	
	public CostlyChar(String id) {
		
		this.id = id;
		
	}
	
	
	
	
	
	public String id() {
		return id;
	}



	public int cost() {
		
		return cost;
		
	}
	
	
	@Override
	public Object clone() {
		return new CostlyChar(this.id);
	}
	
	
}
