package respa.input;

import respa.input.InputVariable;

/**
 *	This class represents a symbolic input variable
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public abstract class SymbolicInputVariable extends InputVariable{

	public SymbolicInputVariable(int startIndex,int buffer) { 
		super(startIndex,buffer);
	}

	

	
	public abstract void anonymize();
	
	public abstract boolean isAnonymized();

	public abstract char [] getDefaultAnonymization();
	
	public abstract char [] getRandomAnonymization();
	
	public abstract char [] getAnonymization(char c);
	
	public abstract boolean isContraintFree();
	
	public abstract double bitsLeaked();
	
	public abstract double bitsAnonymized();
	
	public abstract double fractionBitsLeaked();
	
	public abstract double fractionBitsAnonymized();
	
	
}
