package respa.utils;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import respa.utils.PathConditionLabel;

/**
 *	Numeric path condition label
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class NumericPathConditionLabel extends PathConditionLabel{

	public Expression left;
	
	
	public Comparator comp;
	
	
	public Expression right;

	
	
	@Override
	public String toString() {
		return "(" +left.stringPC() + comp.toString() + right.stringPC() + ")";
	}
	
}
