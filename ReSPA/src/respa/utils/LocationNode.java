package respa.utils;

import respa.stateLabeling.Location;


/**
 *	Location wrapper
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class LocationNode extends Location{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private String trace;

	public LocationNode(String locationString,String type,String trace) {
		super(locationString);
		this.type = type;
		this.trace = trace;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}




}