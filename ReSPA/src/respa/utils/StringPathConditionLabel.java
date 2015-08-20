package respa.utils;

import respa.utils.PathConditionLabel;
import gov.nasa.jpf.symbc.string.StringComparator;
import gov.nasa.jpf.symbc.string.StringExpression;

/**
 *	String path condition label
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class StringPathConditionLabel extends PathConditionLabel{


	public StringExpression left;

	public StringComparator comp;

	public StringExpression right;


	public StringPathConditionLabel() {

	}

	@Override
	public String toString() {
		return "(" +left.stringPC() + comp.toString() + right.stringPC() + ")";
	}

}
