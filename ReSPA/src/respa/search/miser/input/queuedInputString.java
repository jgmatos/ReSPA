package respa.search.miser.input;

import respa.utils.InputLocation;

public class queuedInputString extends queuedInput {
	
	private String value;
	
	
	
	public queuedInputString(InputLocation il, String value, int priority) {

		super(il,priority);
		this.value= value;
		
	}
	
	



	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}
	
}
