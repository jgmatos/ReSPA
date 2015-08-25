package respa.input;

import respa.utils.InputLocation;

public class queuedInputInt extends queuedInput{

	private int value;
	
	
	
	public queuedInputInt(InputLocation il, int value, int priority) {

		super(il,priority);
		this.value= value;
		
	}
	
	



	public int getValue() {
		return value;
	}


	public void setValue(int value) {
		this.value = value;
	}
	
}
