package respa.input;

import gov.nasa.jpf.symbc.numeric.Expression;
import respa.input.SymbVar;

/**
 *	This class represents a symbolic variable
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class SymbVar {


	private Expression symbvar;
	
	private String type;
	
	private String value;
	
	private int offset;
	
	private int length;
	
	private int buffer;
	
	private boolean supported;
	
	
	public SymbVar(Expression symbvar,int offset, int length,int buffer) {
		
		this.offset = offset;
		this.length = length;
		this.symbvar = symbvar;
		this.buffer = buffer;
		
	}

	public SymbVar(int offset, int length,int buffer) {
		this.offset = offset;
		this.length = length;
		this.buffer = buffer;
	}


	public Expression getSymbvar() {
		return symbvar;
	}



	public void setSymbvar(Expression symbvar) {
		this.symbvar = symbvar;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public int getOffset() {
		return offset;
	}



	public void setOffset(int offset) {
		this.offset = offset;
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


	
	
	public void setSupported(boolean supported) {
		
		this.supported=supported;
		
	}
	
	public boolean isSupported() {
		
		return this.supported;
		
	}
	
	
	
	

	@Override
	public boolean equals(Object other) {
		
		if(other instanceof SymbVar)
			if(((SymbVar)other).getLength()==length &&
					((SymbVar)other).getOffset()==offset&&
					((SymbVar)other).getBuffer()==buffer)
				return true;
		
		return false;
		
	}
	
	@Override
	public int hashCode() {
		
		return (""+this.buffer+"-"+this.offset+"-"+this.length).hashCode();
		
	}

	@Override
	public String toString() {
		
		return ""+this.buffer+"-"+this.offset+"-"+this.length;
		
	}

	
	
	
	
	
}
