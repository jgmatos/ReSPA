package respa.utils;

import respa.stateLabeling.Milestone;
import gov.nasa.jpf.jvm.ChoiceGenerator;

/**
 *	Choice generator wrapper
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class CGNode {

	
	public Milestone mile;

	public ChoiceGenerator<?> cg;

	public Milestone aliveMethods;

	
	
}
