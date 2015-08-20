package respa.input;

/**
 *	This class represents a field 
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class SimpleField {

	private String type;
	
	private String variableId;
	
	
	public SimpleField(String type, String variableId) {
		
		this.type = type;
		this.variableId = variableId;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}
	
	
	
}
