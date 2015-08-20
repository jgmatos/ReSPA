package respa.input;

import respa.input.ConcreteInputInteger;
import respa.input.InputVariable;

/**
 *	This class represents a non anonymized integer
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class ConcreteInputInteger extends InputVariable{

 
	private int value;
	
	private int buffer;
	
	
	
	public ConcreteInputInteger(int startIndex,int buffer, int value) {
		super(startIndex,buffer);

		this.value = value;
		this.buffer = buffer;
	
	}

	
	
	
	
	
	
	
	
	
	
	public int getBuffer() {
		return buffer;
	}



	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}



	public void setValue(char value) {
		this.value = value;
	}











	@Override
	public char[] getValue() {

		return (""+value).toCharArray();
		
	
	}

	@Override
	public String getValueAsString() {
		return ""+value;
	}

	@Override
	public char getValueAtIndex(int index) {
		return '?';//TODO
	}

	@Override
	public boolean containsIndex(int index) {
		return index==getStartIndex();
	}

	
	
	
	
	@Override
	public boolean sameVariable(InputVariable other) {

		if(other instanceof ConcreteInputInteger)
			return containsIndex(other.getStartIndex()) && 
					((ConcreteInputInteger)other).getBuffer()==buffer;
		
		return false;
	}

	
	
	
	
	
	public int getValueAsInteger() {
		
		return this.value;
		
	}
	
	
	
	@Override
	public String representation() {
		return "CII-"+getBuffer()+"-"+getStartIndex();
	}
	
	
	@Override
	public int hash() {
		
		return representation().hashCode();
		
	}







	
	
	
}
