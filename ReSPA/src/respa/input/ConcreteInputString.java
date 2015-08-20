package respa.input;

import respa.input.ConcreteInputString;
import respa.input.ConcreteInputVariable;
import respa.input.InputVariable;

/**
 *	This class represents a non anonymized string
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class ConcreteInputString extends ConcreteInputVariable {
 
	 
	
	private String value;
	
	private int length;
	
	private int buffer;
	
	
	
	
	
	public ConcreteInputString(int startIndex, int length, int buffer, String value) {
		
		super(startIndex,buffer);
		
	}
	
	
	
	
	
	public int getOffset() {
		return super.getStartIndex();
	}

	public void setOffset(int offset) {
		super.setStartIndex(offset);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public char[] getValue() {

		return value.toCharArray();
	
	}

	@Override
	public String getValueAsString() {
	
		return value;
	
	}

	
	
	
	@Override
	public char getValueAtIndex(int index) {
		return '?';//TODO
	}

	@Override
	public boolean containsIndex(int index) {

		if(index >= getStartIndex() && index < length+getStartIndex())
			return true;
		
		return false;
	
	}

	@Override
	public boolean sameVariable(InputVariable other) {

		if(other instanceof ConcreteInputString)
			if(other.getStartIndex()==this.getStartIndex() &&
				((ConcreteInputString)other).getLength()==this.length &&
				((ConcreteInputString)other).getBuffer()==this.buffer )
				return true;
		
		return false;
	}

	
	@Override
	public String representation() {
		return "CIS-"+getBuffer()+"-"+getStartIndex()+"-"+getLength();
	}
	
	
	@Override
	public int hash() {
		
		return representation().hashCode();
		
	}










	
	
}
