package respa.input;

import respa.input.InputBuffer;

import java.util.ArrayList;

/**
 *	This class represents an input buffer
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class InputInt extends InputBuffer{

	public ArrayList<Integer> buffer;


	public InputInt() {

		this.buffer = new ArrayList<Integer>();

	}

}
