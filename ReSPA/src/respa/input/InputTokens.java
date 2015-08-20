package respa.input;

import respa.input.InputBuffer;

import java.util.ArrayList;

/**
 *	This class represents input delimited by \n
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class InputTokens extends InputBuffer{

	public ArrayList<String> tokens;
	
	public int lineCount;
	
	public InputTokens() {
		
		this.tokens = new ArrayList<String>();
		this.lineCount = 0;
		
	}

	
	
	
}
