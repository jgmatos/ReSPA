package respa.utils;

/**
 *	An exception
 *
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class APFException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Exception cause;
	
	
	
	public APFException(Exception e) {
		
		
	}
	
	public Exception getCause() {
		
		return this.cause;
		
	}
	
	public void setCause(Exception cause) {
		
		this.cause = cause;
		
	}
	
}
