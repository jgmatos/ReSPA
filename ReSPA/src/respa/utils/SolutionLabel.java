package respa.utils;

import respa.stateLabeling.Milestone;
import respa.utils.PathConditionLabel;
import respa.utils.StringSolutionLabel;

/**
 *	Solution label
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public abstract class SolutionLabel {

	private Milestone mile;

	private boolean success;

	private boolean processed;
	
	
	
	public SolutionLabel(Milestone mile) {
		
		this.mile = mile;
		success = true;
		processed = false;
		
	}
	
	public void setSuccessful() {
		this.success = true;
	}

	public void setUnsuccessful() {
		this.success = false;
	}

	public boolean success() {

		return success;

	}

	public boolean processed() {
		return processed;
	}
	public void setProcessed() {
		processed=true;
	}


	public Milestone getMile() {
		return mile;
	}
	
	
	
	public boolean sameMile(Milestone m) {

		return mile.equals(m);

	}

	

	@Override
	public boolean equals(Object other) {

		if(other instanceof StringSolutionLabel)
			if(((StringSolutionLabel)other).getMile().equals(mile))
				return true;

		return false;

	}

	@Override
	public int hashCode() {

		return mile.hashCode();

	}
	
	
	
	
	
	
	
	
	
	
	public abstract PathConditionLabel getConstraint();
	
	
	
	public abstract void invert();
	
	
	
	
}
