package respa.utils;

import respa.stateLabeling.Milestone;
import respa.stateLabeling.StateLabel;
import respa.utils.InputMile;



/**
 * Dont think we need this
 * 
 * This class allows us to distinguish when 
 * a read() inside a loop from a read() 
 * that follows a backtrack
 * 
 * The idea is as follows:
 * if a read is done in a different state, then it is a backtrack
 * if a read is done in the same state as before, then is within a loop
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class InputMile {

	
	
	private Milestone mile;
	
	
	//a crucial basta?
	private StateLabel state;

	
	
	public InputMile(Milestone mile, StateLabel state) {
		
		this.mile = mile;
		
		this.state = state;
		
	}
	
	
	
	public Milestone getMile() {
		
		return this.mile;
		
	}
	
	
	public StateLabel getState() {
		
		return this.state;
		
	}
	
	
	
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof InputMile)
			if(((InputMile)other).getMile().equals(mile))
				if(((InputMile)other).getState().equals(state))
					return true;//loop
		
		return false;
		
	}
	
	public boolean loop(InputMile other) {
		
		if(other.getMile().equals(mile))
			if(other.getState().equals(state))
				return true;
		
		return false;
		
	}
	
	
	
	
}
