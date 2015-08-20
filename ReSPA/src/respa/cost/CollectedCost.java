package respa.cost;

import java.util.HashMap;


/**
 * 
 * @author Joao Gouveia de Matos GSD/INESC-ID
 *
 * Keep track of the amount of input bytes used by the execution
 *
 */
public class CollectedCost {


	public HashMap<String, CostlyVar> costlyVars;
	private CostlyVar lastAdded;
	private CostlyVar lastUpdated;
	private CostlyVar last;

	private int cost; 

	public static int lastAddedCost = 0;


	public CollectedCost() {

		costlyVars = new HashMap<String, CostlyVar>();
		cost = 0;
	}

	
	protected CollectedCost(int cost,HashMap<String, CostlyVar> costlyVars) {

		this.costlyVars = costlyVars;
		this.cost = cost;
	}

	public void add(CostlyVar costlyVar) {

		if(!contains(costlyVar)){
			costlyVars.put(costlyVar.id(),costlyVar);
			cost+=costlyVar.cost(); //update cost
			lastAddedCost=costlyVar.cost();
			lastAdded=costlyVar;
			last = costlyVar;
		}

	}


	public void update(CostlyVar costlyVar){

		if(contains(costlyVar)){
			cost-=costlyVars.get(costlyVar.id()).cost();//remove previous cost
			costlyVars.put(costlyVar.id(),costlyVar);
			cost+=costlyVar.cost(); //update cost
			lastAddedCost=costlyVar.cost();
			lastUpdated=costlyVar;
			last = costlyVar;
		}

	}
	
	public CostlyVar get(String id) {

		return costlyVars.get(id);

	}

	public CostlyVar getLast() {
		
		return last;
		
	}

	public CostlyVar getLastAdded() {
		
		return lastAdded;
		
	}

	public CostlyVar getLastUpdated() {
	
		return lastUpdated;
		
		
	}
	
	public CostlyVar getLastAddedOrUpdated() {
		
		return lastUpdated;
		
		
	}



	public boolean contains(String id) {

		return costlyVars.containsKey(id);

	}


	public boolean contains(CostlyVar costlyVar) {

		return contains(costlyVar.id());

	}



	

	public int cost() {
		return cost;
	}
	
	public int finalCost() {
		int cost = 0;
		for(CostlyVar cv: costlyVars.values()){
			cost = cost+cv.cost();
		}
		
		return cost;
	}


	

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		
		return new CollectedCost(this.cost,(HashMap<String, CostlyVar>)this.costlyVars.clone()); 		
	}
	
	
	
	
	
	
	

}


