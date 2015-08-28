package respa.input;

import respa.input.InputVariable;

/**
 *	This class represents an input variable
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public abstract class InputVariable implements Comparable<InputVariable>{

	
	private int startIndex;
	
	private int buffer;
	
	
	public InputVariable(int startIndex,int buffer) {
		
		this.startIndex = startIndex;
		this.buffer = buffer;
		
	}
	

	
	
	
	
	public int getStartIndex() {
		return startIndex;
	}


	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}



	public int getBuffer() {
		
		return buffer;
		
	}
	
	public void setBuffer(int buffer) {
		
		this.buffer = buffer;
		
	}




	public abstract char[] getValue();
	
	public abstract String getValueAsString();
	
	public abstract char getValueAtIndex(int index);
	
	public abstract boolean containsIndex(int index);
	
	public abstract boolean sameVariable(InputVariable other);
	
	public abstract int hash();
	
	public abstract String representation();
	
	
//	@Override
	public int compareTo(InputVariable o) {
		
		if(startIndex < o.getStartIndex())
			return -1;
		else if(startIndex > o.getStartIndex())
			return 1;
			
		return 0;
		
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof InputVariable)
			return sameVariable((InputVariable)other);
		
		return false;
		
	}
	
	
	@Override
	public int hashCode() {
		
		return hash();
		
	}
	
	
	@Override
	public String toString() {
		
		return this.representation();
		
	}
	
	
	

	
	
	
	///////////////////// ReSPA-BD Stuff oct/2014
	private int minimumCost=0;
	public int getMinimumCost() {
		return minimumCost;
	}
	public void setMinimumCost(int min) {
		minimumCost=min;
	}
	
	
}
