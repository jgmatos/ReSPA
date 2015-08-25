package respa.queue;

import gov.nasa.jpf.jvm.JVM;

/**
 * A class that represents a node respective to a unsatisfiable path condition
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 *
 */
public class UnsatNode extends Node{

	public UnsatNode(JVM vm) {
		super(vm);
	}

}
