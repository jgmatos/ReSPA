package respa.input;

import respa.stateLabeling.Location;
import gov.nasa.jpf.jvm.bytecode.Instruction;

/**
 *	This class represents a souce of input
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class InputSource {
	
	
	public Location location;
	
	public String variableId;
	
	//char [] or byte []
	public String type;
	
	
	public int index;//optional
	
	
	public Instruction insn;
	
	
	public InputSource (Location l, String id, String type) {
		this.location = l;
		this.variableId = id;
		this.type = type;
	}
	
	
	public InputSource (Location l, String id, String type,Instruction insn) {
		this.location = l;
		this.variableId = id;
		this.type = type;
		this.insn = insn;
	}
	
}
