package respa.input;

import gov.nasa.jpf.symbc.numeric.SymbolicInteger; 
import respa.input.InputVariable;
import respa.input.SymbolicInputVariable;
import respa.leak.numeric.integer.MeasureSingleInteger;

/**
 *	This class represents a symbolic input integer
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class SymbolicInputInt extends SymbolicInputVariable{

	
	private SymbolicInteger sym;
	
	private int value;
	
	private int solution = Integer.MAX_VALUE;
	
	
	public MeasureSingleInteger leakMeasure;
	
	
	public SymbolicInputInt(int startIndex, int buffer,SymbolicInteger sym) {
		super(startIndex, buffer);
		this.sym=sym;
	}

	public SymbolicInputInt(int startIndex, int buffer) {
		super(startIndex, buffer);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void setSolution(int solution) {
		
		this.solution = solution;
		
	}
	

	public int getSolution() {
		
		return this.solution;
		
	}
	
	
	
	public void setSym(SymbolicInteger sym) {
		
		this.sym = sym;
		
	}
	
	
	public int getLength() {
		
		return 1;
		
	}
	
	public int getIndex() {
		
		return super.getStartIndex();
		
	}
	
	public int getBuffer() {
		
		return super.getBuffer();
		
	}
	
	public SymbolicInteger getSym() {
		
		return this.sym;
		
	}
	
	public int getValueAsInt() {
		
		return value;
		
	}
	
	
	
	public void setValue(int value) {
		
		this.value = value;
		
	}
	
	
	
	@Override
	public String getValueAsString() {
		
		return String.valueOf(this.value);
		
	}
	
	
	@Override
	public void anonymize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAnonymized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public char[] getDefaultAnonymization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getRandomAnonymization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getAnonymization(char c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isContraintFree() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double bitsLeaked() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double bitsAnonymized() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double fractionBitsLeaked() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double fractionBitsAnonymized() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char[] getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public char getValueAtIndex(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean containsIndex(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sameVariable(InputVariable other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hash() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String representation() {
		return "SII-"+getBuffer()+"-"+getStartIndex()+"-"+getLength();
	}


	
	
	
}
