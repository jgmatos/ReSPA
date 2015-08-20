package respa.input;

import respa.input.ConcreteInputChar;
import respa.input.ConcreteInputVariable;
import respa.input.InputVariable;

/**
 *	This class represents a non anonymized character
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class ConcreteInputChar extends ConcreteInputVariable{
 
	
	private char value;
	
//	private int buffer;
	
	
	
	public ConcreteInputChar(int startIndex,int buffer, char value) {
		super(startIndex,buffer);

		this.value = value;
//		this.buffer = buffer;
	
	}

	
	
	
	
	
	
	
	
	
	
	


	public void setValue(char value) {
		this.value = value;
	}











	@Override
	public char[] getValue() {

		char [] c = new char[1];
		c[0] = value;
		return c;
	
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

		if(other instanceof ConcreteInputChar)
			return containsIndex(other.getStartIndex()) && 
					((ConcreteInputChar)other).getBuffer()==super.getBuffer();
		
		return false;
	}

	
	
	
	
	
	@Override
	public String representation() {
		return "CIC-"+getBuffer()+"-"+getStartIndex();
	}
	
	
	
	@Override
	public int hash() {
		
		return representation().hashCode();
		
	}












	
	
	
}
