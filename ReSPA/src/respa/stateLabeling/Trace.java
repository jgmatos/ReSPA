package respa.stateLabeling;

import respa.stateLabeling.Location;
import respa.stateLabeling.Milestone;
import respa.stateLabeling.Trace;

import java.util.ArrayList;

/**
 *	Stack trace of a Milestone
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 *
 */
public class Trace {

	/**
	 * FOR NOW WE ARE DEALING WITH THE TRACE REPRESENTATION ONLY
	 */
	
	
	private ArrayList<Milestone> trace;
	
	private String representation;
	
	private int hashcode;
	
	private int size;
	
	public Trace(String representation) {
		
		this.representation = representation.trim();
		this.trace = new ArrayList<Milestone>();
		//representation = representation.substring(1, representation.length()-1);
		this.hashcode = representation.hashCode();
		
		
		
		//For now we are not using this
		String [] splits = representation.substring(1, representation.length()-1).split(" <<< ");
		Milestone m = null;
		for(int i=splits.length-1;i>0;i--) {//first element does not count
			m = new VerboseMile(new Location(splits[i]));
			//TODO: add trace
			this.trace.add(m);

		}
		
		size = trace.size();
			
	}
	
	
	public Milestone get(int index) {
		
		return this.trace.get(index);
		
	}
	
	
	
	
	
	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	@Override
	public int hashCode() {

		return this.hashcode;
		
	}
	
	@Override
	public String toString() {
		
		return this.representation;
		
	}
	
	@Override
	public boolean equals(Object other) {

		if(other instanceof Trace){
			Trace othertrace = (Trace)other;
			if(othertrace.hashcode==this.hashcode){
				return true;
			}
			else if(size!=othertrace.getSize()){
				int minsize = Math.min(size, othertrace.getSize());
				if(this.representation(minsize).hashCode()==othertrace.representation(minsize).hashCode()){
					return true;
				}
			}
			
		}
		
		return false;
		
	}
	
	public Trace copy() {

		return new Trace(new String(this.representation));
		
	}
	
	public String representation(int size){

		String [] splits = representation.substring(1, representation.length()-1).split(" <<< ");
		String represent="";
		for(int i=0;i<size-1; i++)
			represent=represent+splits[i]+" <<< ";
		represent = represent+splits[size-1];
		
		return represent;
	}
	
	
	
	
	
	
}
