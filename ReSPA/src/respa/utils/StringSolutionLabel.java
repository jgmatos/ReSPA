package respa.utils;

import respa.stateLabeling.Milestone;
import respa.utils.PathConditionLabel;
import respa.utils.SolutionLabel;
import respa.utils.StringPathConditionLabel;

/**
 *	String solution label
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class StringSolutionLabel extends SolutionLabel{

	private StringPathConditionLabel constraint;

	

	public StringSolutionLabel(StringPathConditionLabel sc,Milestone m) {

		super(m);
		constraint = sc;
		

	}
	
	
	
	
	
	public void setConstraint(StringPathConditionLabel constraint) {
		this.constraint = constraint;
	}

	@Override
	public PathConditionLabel getConstraint() {

		return constraint;

	}
	
	@Override
	public void invert() {
		
		constraint.comp = constraint.comp.not();
		
	}



}
