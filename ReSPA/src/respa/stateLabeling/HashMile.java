package respa.stateLabeling;

/**
 *	This class labels a specific point of the execution
 *	
 *  It saves only the hashCode of the point of the execution
 *	and therefore requires less memory
 *
 *  (class signature + line number + stack trace).hashCode()
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class HashMile extends Milestone{

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7118854050783091010L;

	
	
	private int hashMile;
	
	
	
	public HashMile(Location location, Trace trace) {

		hashMile = (new VerboseMile(location, trace)).hashCode();

	}
	
	public HashMile(VerboseMile verbose) {
		
		hashMile = verbose.hashCode();
		
	}
	
	
	public HashMile(int hash) {
		
		hashMile = hash;
		
	}

	
	
	
	
	public int hashMile() {
		
		return this.hashMile;
		
	}
	
	
	
	
	
	@Override
	protected boolean match(Milestone other) {
		
		if(other instanceof HashMile){
			if(((HashMile)other).hashMile()==this.hashMile)
				return true;
		}
		else if(other instanceof VerboseMile){
			if(((VerboseMile)other).hashCode()==this.hashMile)
				return true;
		}
		
		return false;

	}

	@Override
	protected String representation() {
		return String.valueOf(this.hashMile);
	}

	@Override
	protected int hashRepresentation() {
		
		return this.hashMile;
	
	}

	@Override
	public Milestone copy() {

		return new HashMile(hashMile);

	}

	
	
	
	
	
}
