package respa.input;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import respa.input.InvokedInsn;
import respa.stateLabeling.Location;

/**
 *	This class represents an invoked instruction
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class InvokedInsn {

	
	private InvokeInstruction insn;
	
	private Location location;
	
	private char[] buf;
	
	private int offset;
	
	private int length;

	
	public InvokedInsn() {
		
	}
	
	
	
	public Location getLocation() {
		return location;
	}



	public void setLocation(Location location) {
		this.location = location;
	}



	public InvokeInstruction getInsn() {
		return insn;
	}

	public void setInsn(InvokeInstruction insn) {
		this.insn = insn;
	}

	public char[] getBuf() {
		return buf;
	}

	public void setBuf(char[] buf) {
		this.buf = buf;
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
	
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof InvokeInstruction)
			if(((InvokedInsn)other).getLocation().equals(this.location))
				return true;
		
		return false;
		
	}
	
	
	
	
	
	
	
	
}
