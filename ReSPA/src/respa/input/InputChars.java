package respa.input;

import respa.input.InputBuffer;

import java.util.ArrayList;

/**
 *	This class represents an input buffer
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class InputChars extends InputBuffer{

	//better to use arraylist because we do not know 
	//the size of the buffer
	public ArrayList<Character> buffer;
	
	
	public InputChars() {
		
		this.buffer = new ArrayList<Character>();
		
	}
	
	
}
