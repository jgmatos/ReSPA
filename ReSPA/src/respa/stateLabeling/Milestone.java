package respa.stateLabeling;

import respa.stateLabeling.Milestone;

import java.io.Serializable;


/**
 *	This class labels a specific point of the execution
 *
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public abstract class Milestone implements Serializable {




	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;








	
	protected abstract boolean match(Milestone other);

	protected abstract String representation();
	
	protected abstract int hashRepresentation();
	

	@Override
	public boolean equals(Object other) {

		if(other instanceof Milestone)
			if(match((Milestone)other))						
				return true;

		return false;

	}


	@Override
	public String toString() {

		return representation();

	}
	
	



	/**
	 * Should review this: maybe put everything into a string and 
	 * call hashcode to the whole thing
	 * 
	 * If this is called often it may be better to store the result
	 * globaly and return it
	 * 
	 * @return
	 */
	@Override
	public int hashCode(){

		return hashRepresentation();

	}









	public abstract Milestone copy();







}
