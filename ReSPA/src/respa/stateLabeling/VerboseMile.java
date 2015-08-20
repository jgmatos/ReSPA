package respa.stateLabeling;

/**
 *	This class labels a specific point of the execution
 *
 *  class signature + line number + stack trace
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class VerboseMile extends Milestone{

	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Location location;


	private Trace trace;


	//private int loopIterations;



	public VerboseMile(Location location) {

		this.location = location;

		//this.loopIterations=0;

		this.trace = null;

	}

	public VerboseMile(Location location, Trace trace) {

		this.location = location;

//		this.loopIterations=0;

		this.trace = trace;

	}





	public Location getLocation() {
		return location;
	}



	public void setLocation(Location location) {
		this.location = location;
	}



/*	public int getLoopIterations() {
		return loopIterations;
	}



	public void setLoopIterations(int loopIterations) {
		this.loopIterations = loopIterations;
	}

	public void addIteration() {

		this.loopIterations++;

	}*/




	public Trace getTrace() {

		return this.trace;

	}


	public void setTrace(Trace trace) {

		this.trace = trace;

	}






/*	public boolean isInsideLoop() {

		return this.loopIterations>0;

	}
*/
	public boolean isInsideMethodCall() {

		return this.trace != null;

	}

	public boolean sameLocation(Location other) {

		return this.location.equals(other);

	}

	public boolean sameLocation(VerboseMile other) {

		return sameLocation(other.getLocation());

	}


	public boolean sameBackground(Milestone other) {
		
		if(other instanceof VerboseMile)
			return sameBackground(this,(VerboseMile)other);

		return false;
		
	}

	public static boolean sameBackground(VerboseMile mile1, VerboseMile mile2) {
		
		if(mile1.getTrace()==null && mile1.getTrace()==null)
			return true;
		
		if(mile1.getTrace()!=null && mile1.getTrace()!=null)
			if(mile1.getTrace().equals(mile2.getTrace()))
				return true;
		
		return false;

	}





	










	public VerboseMile copy() {

		return new VerboseMile(this.location.copy(), trace.copy());
		
	}


	

	public Milestone copyLocation() {

		return new VerboseMile(location.copy());

	}

	
	
	
	@Override
	protected boolean match(Milestone other) {

		if(other instanceof VerboseMile){
			if(sameLocation(((VerboseMile)other)))
				if(this.sameBackground(((VerboseMile)other)))
						return true;
			
		}
		else if(other instanceof HashMile) {
			if(((HashMile)other).hashMile()==this.hashCode())
				return true;
		}
			
		return false;
	
	}

	
	
	
	
	@Override
	protected String representation() {
		return this.location.toString()+" "+this.trace;//+" - "+this.loopIterations;
	}

	
	
	
	
	
	
	/**
	 * Should review this: maybe put everything into a string and 
	 * call hashcode to the whole thing
	 * 
	 * If this is called often it may be better to store the result
	 * globaly and return it
	 * 
	 * @return
	 */
	@Override
	protected int hashRepresentation() {

		String dummy = this.location.toString();
		
		if(this.trace!=null)
			dummy = dummy+this.trace.toString();
		
//		dummy = dummy+"-"+this.loopIterations;
		
		return dummy.hashCode();

	}



	
}
