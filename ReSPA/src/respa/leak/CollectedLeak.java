package respa.leak;

import java.util.HashMap;



/**
 * 
 * @author Joao Gouveia de Matos GSD/INESC-ID
 *
 * Keep track of the amount of bit revealed by the execution
 *
 */
public class CollectedLeak {


	public HashMap<String, LeakyVariable> leakyVars;
	private LeakyVariable lastAdded;
	private LeakyVariable lastUpdated;
	private LeakyVariable last;

	private double leak; 

	public static double lastAddedCost = 0;


	public CollectedLeak() {

		leakyVars = new HashMap<String, LeakyVariable>();
		leak = 0;
	}

	
	protected CollectedLeak(double leak,HashMap<String, LeakyVariable> leakyVars) {

		this.leakyVars = leakyVars;
		this.leak = leak;
	}
	
	
	public void add(LeakyVariable leakyVar) {

		if(!contains(leakyVar)){
			leakyVars.put(leakyVar.id(),leakyVar);
			leak+=leakyVar.getFastLeak(); //update cost
			lastAddedCost=leakyVar.lastCalculatedLeak();
			lastAdded=leakyVar;
			last = leakyVar;
		}

	}


	public void update(LeakyVariable leakyVar){

		if(contains(leakyVar)){
			leak-=leakyVars.get(leakyVar.id()).getFastLeak();//.lastCalculatedLeak();//remove previous leak
			leakyVars.put(leakyVar.id(),leakyVar);
			leak+=leakyVar.getFastLeak(); //calculate leak and add
			lastAddedCost=leakyVar.lastCalculatedLeak();
			lastUpdated=leakyVar;
			last = leakyVar;
		}

	}
	
	public LeakyVariable get(String id) {

		return leakyVars.get(id);

	}

	public LeakyVariable getLast() {
		
		return last;
		
	}

	public LeakyVariable getLastAdded() {
		
		return lastAdded;
		
	}

	public LeakyVariable getLastUpdated() {
	
		return lastUpdated;
		
		
	}
	
	public LeakyVariable getLastAddedOrUpdated() {
		
		return lastUpdated;
		
		
	}



	public boolean contains(String id) {

		return leakyVars.containsKey(id);

	}


	public boolean contains(LeakyVariable costlyVar) {

		return contains(costlyVar.id());

	}

	

	public double leak() {
		return leak;
	}
	
	public double finalCost() {
		double cost = 0;
		for(LeakyVariable cv: leakyVars.values()){
			cost = cost+cv.getFastLeak();
		}
		
		return cost;
	}


	

	@Override
	public Object clone() {
		
		CollectedLeak cloned = new CollectedLeak(this.leak,new HashMap<String,
				LeakyVariable>()); 
		
		if(lastAdded!=null)
			cloned.setLastAdded(lastAdded);
		if(lastUpdated!=null)
			cloned.setLastUpdated(lastUpdated);
		if(last!=null)
			cloned.setLast(last);
		
		
		for(String s: leakyVars.keySet())
			cloned.leakyVars.put(s, (LeakyVariable)leakyVars.get(s).clone());
		
		return cloned;
		
	}
	
	private void setLastAdded(LeakyVariable lastAdded) {
		this.lastAdded = lastAdded;
	}
	
	private void setLastUpdated(LeakyVariable lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	private void setLast(LeakyVariable last) {
		this.last = last;
	}
	
	
	
	

}


