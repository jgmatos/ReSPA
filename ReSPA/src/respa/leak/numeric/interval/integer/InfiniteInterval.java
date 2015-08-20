package respa.leak.numeric.interval.integer;

import respa.leak.numeric.interval.DisjointException;
import respa.leak.numeric.interval.IsVoidException;
import respa.leak.numeric.interval.NoLimitException;

/**
 * 
 * This class represents an interval with no bounds. 
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class InfiniteInterval extends SubInterval{

	private boolean plusInfinite;
	
	private boolean minusInfinite;
	
	private boolean hasLimit;
	
	
	
	private int limit;
	
	
	public InfiniteInterval(){
		
		super();
		
		this.plusInfinite = true;
		
		this.minusInfinite = true;
		
		hasLimit = false;
		
	}
	
	
	private void setAsPositive(){
		
		this.minusInfinite = false;
		this.plusInfinite = true;
		
	}
	
	
	private void setAsNegative(){
		
		this.minusInfinite = true;
		this.plusInfinite = false;
		
		
	}
	
	public void setTopLimit(int limit) {
		
		this.limit = limit;
		
		setAsNegative();
		
		hasLimit = true;
		
	}
	
	
		
	
	public void setBottomLimit(int limit) {

		this.limit = limit;
		
		setAsPositive();
		
		hasLimit = true;
		
	}
	
	
	
	
	public int getLimit() throws NoLimitException{
		
		if(hasLimit)
			return limit;
		else
			throw new NoLimitException();
		
	}
	
	
	public boolean isMinusInfinite(){
		
		return this.minusInfinite;
		
	}
	
	public boolean isPlusInfinite() {
		
		return this.plusInfinite;
		
	}
	
	
	public boolean hasLimit(){
		
		return this.hasLimit;
		
	}
	
	public boolean isEverything() {
		
		return !this.hasLimit;
		
	}
	
	
	
	/*superclass methods*/
	
	
	
	
	
	
	public void setEdges(int edge1, int edge2) {
		
		
		
	}
	
	
	public int [] edges() throws IsVoidException{
		
		return null;
		
	}


	public boolean contains(int value) {
		
		if(hasLimit){
			if(this.limit >= value)
				if(isMinusInfinite())
					return true;
			
			if(this.limit <= value)
				if(isPlusInfinite())
					return true;
			
		}
		else
			return true;
		
		return false;
		
	}
	

	public boolean contains(SubInterval subInterval) {
		
		try {
			if(this.contains(subInterval.bottom()))
				if(this.contains(subInterval.top()))
					return true;
		} catch (IsVoidException e) {
			return true;
		}
		
		return false;
		
	}
	
	public boolean contains(InfiniteInterval subinterval) {
		
		if(this.isEverything())
			return true;
		
		if(subinterval.isEverything())
			return false;
		
		try {
		
		if(this.isMinusInfinite()&&subinterval.isMinusInfinite())
			if(this.limit > subinterval.getLimit())
				return true;
			
		
		if(this.isPlusInfinite()&&subinterval.isPlusInfinite()) 
			if(this.limit < subinterval.getLimit())
				return true;
			
		}
		catch(NoLimitException nle) {
			/**Never reached**/
		}
		
		return false;
		
	}
	
	
	
	public boolean isDisjoint(SubInterval interval) {
			
			if(!hasLimit)
				return false;
		
			if(interval.contains(limit))
				return true;
		
		return false;
		
	}
	
	
	public boolean isDisjoint(InfiniteInterval interval) {
		
		if(!hasLimit)
			return false;
		
		if(interval.contains(limit))
			return true;
		
		
		return false;
		
	}
	
	
	public void add(SubInterval interval) throws DisjointException{
		
		if(this.isDisjoint(interval))
			throw new DisjointException();
		
		try {
		
			if(hasLimit) {
		
				if(this.minusInfinite)
					this.limit = interval.top();
		
				if(this.plusInfinite)
					this.limit = interval.bottom();
		
			}
		
		}
		catch(IsVoidException ive) {
			/**Do nothing**/
		}
		
	}
	
	
	public void add(InfiniteInterval interval) throws DisjointException{
		
		if(this.isDisjoint(interval))
			throw new DisjointException();
			
		try {
		
		if(hasLimit||interval.hasLimit()) {
			
			if(this.minusInfinite){
				if(interval.isPlusInfinite())	
					this.hasLimit = false;
				else
					if(interval.getLimit()>this.limit)
						this.limit = interval.getLimit(); 
			}
		
			if(this.plusInfinite)
				if(interval.isMinusInfinite())	
					this.hasLimit = false;
				else
					if(interval.getLimit()<this.limit)
						this.limit = interval.getLimit();
		
		}
		}
		catch(NoLimitException nle) {
			//this part of the code is never reached
		}
		
	}
	
	
	
	
	public SubInterval subtract(SubInterval interval) throws DisjointException{
		
		if(this.isDisjoint(interval))
			throw new DisjointException();
		
		if(this.contains(interval))
			return interval;
		
		try{
		
		if(this.minusInfinite)
			return new SubInterval(interval.bottom(), this.limit);
		
		return new SubInterval(this.limit, interval.top());
		
		}
		catch(IsVoidException ive){
			return null;
		}
	}	
	
	
	public SubInterval subtract(InfiniteInterval interval) throws DisjointException {
		
		if(this.isDisjoint(interval))
			throw new DisjointException();
		
		if(this.contains(interval))
			return interval;
		
		if(interval.contains(this))
			return this;
		
		if(this.minusInfinite && interval.isMinusInfinite())
			return auxiliarSub1(interval);
		
		if(this.plusInfinite && interval.isPlusInfinite())
			return auxiliarSub1(interval);
		
		return auxiliarSub2(interval);
		
	}
	
	
	
	
	/*subtraction methods**/
	
	
	
	
	
	private SubInterval auxiliarSub1(InfiniteInterval aux) {
	
		if(this.minusInfinite)
			return new SubInterval(aux.limit, this.limit);
		else
			return new SubInterval(this.limit,aux.limit);
		
	}
	
	private InfiniteInterval auxiliarSub2(InfiniteInterval aux) {
		
	    InfiniteInterval i = new InfiniteInterval();

		
		try {
			

		if(this.minusInfinite) {
			 if(this.limit < aux.getLimit())
				 i.setTopLimit(this.limit);
			 else
				 i.setTopLimit(aux.limit);
		}
		else {
			
			 if(this.limit > aux.getLimit())
				 i.setTopLimit(this.limit);
			 else
				 i.setTopLimit(aux.limit);
			
		}
	
		
		}
		catch(NoLimitException nle) {
			
		}
		
		return i;

	}
	
	
	
	
	
	
	
}
