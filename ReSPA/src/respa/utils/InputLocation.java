package respa.utils;

import respa.stateLabeling.Location;
import respa.stateLabeling.Milestone;
import respa.utils.InputLocation;


/**
 *	Input location
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class InputLocation {


	public Location location=null;
	
	
	public Milestone mile=null;


	public String readerClass=null;


	public String method=null;


	public String variableType=null;


	public String variableName=null;


	public String representation="";

	public String value="unknown";


	@Override
	public String toString() {

		return this.representation;

	}


	@Override
	public int hashCode() {
		return mile.hashCode();//return location.hashCode();
	}

	@Override
	public boolean equals(Object other) {

		if(other instanceof InputLocation)
			if(((InputLocation)other).mile.equals(this.mile))//if(((InputLocation)other).location.equals(this.location))
				return true;

		return false;

	}




}
