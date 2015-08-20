package respa.utils;

import respa.stateLabeling.Milestone;
import respa.utils.NumericPathConditionLabel;
import respa.utils.PathConditionLabel;
import respa.utils.SolutionLabel;

/**
 *	Numeric Solution Label
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class NumericSolutionLabel extends SolutionLabel{

	private NumericPathConditionLabel constraint;

	

	public NumericSolutionLabel(NumericPathConditionLabel sc,Milestone m) {

		super(m);
		constraint = sc;
		

	}

	
	
	
	
	public void setConstraint(NumericPathConditionLabel constraint) {
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
