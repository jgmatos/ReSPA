package respa.input;

import respa.input.InputVariable;

/**
 *	This class represents a non anonymized variable
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public abstract class ConcreteInputVariable extends InputVariable {

	public ConcreteInputVariable(int startIndex,int buffer) {
		super(startIndex,buffer);
	}

}
