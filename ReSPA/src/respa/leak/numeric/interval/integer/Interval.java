package respa.leak.numeric.interval.integer;
import java.util.ArrayList;

import respa.leak.numeric.interval.DisjointException;
import respa.leak.numeric.interval.IsInfiniteException;
import respa.leak.numeric.interval.IsVoidException;
import respa.leak.numeric.interval.NoLimitException;


/**
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 * This class represents an interval a heterogeneous numeric interval
 *
 * Deals only with integers. For real numbers please see:
 * respa.leak.numeric.interval.real package
 *
 */
public class Interval {

	
	/**
	 * The finite subIntervals of this interval
	 */
	private ArrayList <SubInterval> interval;
	
	/**
	 * Minus infinite if it is the case
	 */
	private InfiniteInterval leftEdge;
	private boolean goesToMinusInfinite;
	
	/**
	 * Plus infinite if it is the case
	 */
	private InfiniteInterval rightEdge;
	private boolean goesToPlusInfinite;
	
	
	/**
	 * Void interval?
	 */
	private boolean isEmpty;
	
	
	/**
	 * Number of subintervals
	 */
	private int numberOfSubIntervals;
	
	
	
	
	
	
	
	
	
	/**
	 * Constructor
	 * 
	 * Constructs a void interval
	 */
	public Interval() {
		
		this.interval = new ArrayList<SubInterval>();
		
		this.isEmpty = true;
		this.numberOfSubIntervals = 0;
		this.goesToMinusInfinite = false;
		this.goesToPlusInfinite = false;
		
	}
	
	
	
	
	
	/**
	 * @return	true if this interval is void; false otherwise
	 */
	public boolean isEmpty(){
		
		return isEmpty;
		
	}
	
	
	
	
	/**
	 * This method determines if this interval 
	 * is a positive infinite
	 * 
	 * @return true if so; false otherwise
	 */
	public boolean goesToPlusInfinite() {

		return goesToPlusInfinite;
	
	}
	
	
	/**
	 * This method determines if this interval 
	 * is a negative infinite
	 * 
	 * @return true if so; false otherwise
	 */
	public boolean goesToMinusInfinite() {
		
		return goesToMinusInfinite;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Checks whether this interval contains some subinterval
	 * 
	 * @param infiniteInterval	The subinterval
	 * @return true if so; false otherwise
	 */
	public boolean contains(InfiniteInterval infiniteInterval) {
		
		if(this.goesToMinusInfinite)
			if(this.leftEdge.contains(infiniteInterval))
				return true;
		
		if(this.goesToPlusInfinite)
			if(this.rightEdge.contains(infiniteInterval))
				return true;
		
		return false;
		
	}
	
	
	/**
	 * Overloaded contains
	 * Checks whether this interval contains some subinterval
	 * 
	 * @param subinterval	The subinterval
	 * @return true if so; false otherwise
	 */
	public boolean contains(SubInterval subinterval) {
		
		if(this.goesToMinusInfinite)
			if(this.leftEdge.contains(subinterval))
				return true;
		
		if(this.goesToPlusInfinite)
			if(this.rightEdge.contains(subinterval))
				return true;
		
		for(SubInterval si: this.interval)
			if(si.contains(subinterval))
				return true;
				
			
		return false;
		
	}
	
	
	/**
	 * Overloaded contains
	 * Checks whether this interval contains some number
	 * 
	 * @param value 	The number
	 * @return true if so; false otherwise
	 */
	public boolean contains(int value) {
		
		for(SubInterval si:this.interval)
			if(si.contains(value))
				return true;
		
		return false;
		
	}
	
	
	
	/**
	 * Get a subinterval of this interval
	 * 
	 * @param index 	The index of the subinterval
	 * @return 	The subinterval
	 */
	public SubInterval getSubInterval(int index) {
	
		if(index == 0) 
			if(this.goesToMinusInfinite)
				return this.leftEdge;
		
		
		if(index == this.numberOfSubIntervals-1)
			if(this.goesToPlusInfinite)
				return this.rightEdge;
		
		return this.interval.get(index);	
		
		
	}
	
	
	
	
	
	
	
	/**
	 * Remove a subinterval of this interval
	 * 
	 * @param index 	The index of the subinterval
	 */
	public void removeSubInterval(int index) {
		
		this.interval.remove(index);
		
	}
	
	
	
	public void removeMinusInfinite() {
		
		if(this.goesToMinusInfinite){
			this.leftEdge = null;
			this.goesToMinusInfinite=false;
			this.numberOfSubIntervals -=1;
		}
			
		
	}
	
	
	public void removePlusInfinite() {
		
		if(this.goesToPlusInfinite){
			this.rightEdge = null;
			this.goesToPlusInfinite=false;
			this.numberOfSubIntervals -=1;
		}
		
	}
	
	
	/**
	 * @return 	The number of subintervals of this interval
	 */
	public int numberOfSubIntervals() {
		
		return this.numberOfSubIntervals;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void becomeEverything() {
		
		this.leftEdge = new InfiniteInterval();
		this.leftEdge.setTopLimit(0);
		this.goesToMinusInfinite=true;
		
		this.rightEdge = new InfiniteInterval();
		this.rightEdge.setBottomLimit(0);
		this.goesToPlusInfinite = true;
		
		this.isEmpty = false;
		
		this.interval.clear();
		this.numberOfSubIntervals = 2;
		
	}
	
	
	public void clearInterval() {
		
		if(this.goesToMinusInfinite)
			this.removeMinusInfinite();
		
		if(this.goesToPlusInfinite)
			this.removePlusInfinite();
		
		this.interval.clear();
		
		this.isEmpty = true;
		
		this.numberOfSubIntervals = 0;
		
	}
	
	
	
	
	
	
	/**
	 * Is this interval measurable?
	 * 
	 * @return true if this interval is finite; false if infinite
	 */
	public boolean isMeasurable() {
		
		if(this.goesToMinusInfinite)
			return false;
		
		if(this.goesToPlusInfinite)
			return false;
		
		return true;
		
	}
	
	
	/**
	 * @return true if this interval contains all numbers; false otherwise
	 */
	public boolean isEverything() {
		
		if(this.goesToMinusInfinite && this.goesToPlusInfinite)		
			if(!this.leftEdge.isDisjoint(this.rightEdge))
				return true;
		
		return false;
		
	}
	
	
	
	
	
	
	/**
	 * This method calculates the size of this interval
	 * 
	 * @return 	The size of this interval
	 * @throws IsInfiniteException 	If this interval is infinite
	 */
	public double size() throws IsInfiniteException{
		
		double size = 0.0;
		
		if(!isMeasurable())
			return 1.0;//throw new IsInfiniteException();
		
		
		
		for(SubInterval si: this.interval)
			size += si.size();
		
		if(size==0.0)
			return 1.0;
		
		return size;
		
	}
	
	
	/**
	 * Calculate the size of the complementary interval of this.interval
	 * 
	 * @return 	The size of the complementary interval
	 * @throws IsInfiniteException 	If size calculated is infinite 	
	 */
	public double sizeOfExclusion() throws IsInfiniteException{
		
		if(!this.goesToMinusInfinite)
			throw new IsInfiniteException();
		
		if(!this.goesToPlusInfinite)
			throw new IsInfiniteException();
		
		if(isEverything())
			return 0.0;
					
		
		double size = 0.0;

		try {
			
			size = this.rightEdge.getLimit() -
						 this.leftEdge.getLimit();
			
			for(SubInterval si: this.interval)
				size -= si.size();
				
			
		} catch (NoLimitException e) {
			// Nunca chega aqui
			e.printStackTrace();
		}
		
		
		
		return size;
		
	}
	
	
	
	
	public boolean isDisjoint(SubInterval interval) {
		
		if(this.goesToMinusInfinite)
			if(!this.leftEdge.isDisjoint(interval))
				return false;
		
		if(this.goesToPlusInfinite)
			if(!this.rightEdge.isDisjoint(interval))
				return false;
		
		for(SubInterval si: this.interval)
			if(!si.isDisjoint(interval))
				return false;
		
		return true;
		
	}
	
	
	public boolean isDisjoint(InfiniteInterval interval) {
		
		if(this.goesToMinusInfinite)
			if(!this.leftEdge.isDisjoint(interval))
				return false;
		
		if(this.goesToPlusInfinite)
			if(!this.rightEdge.isDisjoint(interval))
				return false;
		
		
		return true;
		
	}
	
	
	
	
	
	/**
	 * This method unites two intervals
	 * These intervals must not be disjoint
	 * For disjoint intervals the user should create an Interval object
	 * 
	 * @param subInterval1 
	 * @param subInterval2
	 * @return 	The resulting interval
	 * @throws DisjointException 	If parameters are disjoint
	 */
	public SubInterval union(SubInterval subInterval1,SubInterval subInterval2)throws DisjointException { 
	
		
		
		
		if(subInterval1 instanceof InfiniteInterval) {
		
			InfiniteInterval united = (InfiniteInterval) subInterval1;
			
			united.add(subInterval2);
			
			return united;
			
		}
		
		if(subInterval2 instanceof InfiniteInterval) {

			InfiniteInterval united = (InfiniteInterval) subInterval2;

			united.add(subInterval2);
			
			return united;
		}
		
		SubInterval united = subInterval1;
		united.add(subInterval2);
		
		return united;

		
	}
	
	
	
	public void union(SubInterval subinterval) {
		
		this.addSubInterval(subinterval);
		
	}
	
	public void union(InfiniteInterval subinterval) {
		
		this.addSubInterval(subinterval);
		
	}
	
	public void union(Interval interval) {
		
		int i;
		
		for(i=0; i<interval.numberOfSubIntervals; i++)
			this.addSubInterval(interval.getSubInterval(i));
		
	}
	 
	
	
	
	
	
	
	/**
	 * This method intersects two subintervals
	 * These subintervals must not be disjoint
	 * 
	 * @param subInterval1
	 * @param subInterval2
	 * @return The resulting subinterval
	 * @throws DisjointException	If the parameters are disjoint
	 */
	public SubInterval intersection(SubInterval subInterval1, SubInterval subInterval2) throws DisjointException {
		
		if(subInterval1.isDisjoint(subInterval2)) 
			throw new DisjointException();
			
		
		if(subInterval1 instanceof InfiniteInterval) {
			
			if(subInterval2 instanceof InfiniteInterval)
				return ((InfiniteInterval)subInterval1).subtract((InfiniteInterval)subInterval2);
			else
				return ((InfiniteInterval)subInterval1).subtract(subInterval2);
			
			
		
		}
		else {
			
			if(subInterval2 instanceof InfiniteInterval)
				return ((InfiniteInterval) subInterval2).subtract(subInterval1);
			else
				return subInterval1.subtract(subInterval2);
			
		}
		
		
	}
	
	
	/**
	 * Overloaded intersection
	 * Intersects this interval with another subinterval
	 * 
	 * @param interval 	The other subinterval
	 */
	public void intersection(SubInterval interval) {
		
		this.sub(interval);
		
	}
	
	
	/**
	 * Overloaded intersection
	 * Intersects this interval with another subinterval
	 * 
	 * @param interval 	The other subinterval
	 */
	public void intersection(InfiniteInterval interval) {
		
		this.sub(interval);
		
	}
	
	
	
	
	/**
	 * Overloaded intersection
	 * Intersects this interval with another interval
	 * 
	 * @param interval 	The other interval
	 */
	public void intersection(Interval interval){
		
		int i, j, stop;
		
		try {
		
			if(interval.goesToMinusInfinite)
				j=1;
			
			if(interval.goesToPlusInfinite)
				stop = interval.numberOfSubIntervals() -1;
			else
				stop = interval.numberOfSubIntervals();
			
			for(i=0; i < this.interval.size(); i++)
				for(j=0; j < stop; j++)
					if(this.interval.get(i).isDisjoint(interval.getSubInterval(j)))						
						this.interval.get(i).sub(interval.getSubInterval(j));

			SubInterval aux;
			if(interval.goesToMinusInfinite) {

				if(this.goesToMinusInfinite) {
					this.addSubInterval(this.leftEdge.subtract(interval.getSubInterval(0)));
					this.removeMinusInfinite();
				}
				
				for(i=0; i < this.interval.size(); i++)
					if(!interval.getSubInterval(0).isDisjoint(this.interval.get(i))) {
						aux = ((InfiniteInterval) interval.getSubInterval(0)).subtract(this.interval.get(i));
						this.interval.remove(i);
						this.addSubInterval(aux);
					}
				//TODO
				
				
			}
			
		}
		catch(DisjointException de) {
			
		}
			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*********************Private Methods*****************************/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void addToMinusInfinite(InfiniteInterval subinterval){
		
		try {
		
			if(this.goesToMinusInfinite)
				this.leftEdge.add(subinterval);
				
			else
				this.leftEdge = subinterval;
		
			refresh();
			
		}
		catch (DisjointException de) {
			/**Never reached**/
		}
			
	}
	
	
	private void addToPlusInfinite(InfiniteInterval subinterval){
		
		try {
		
			if(this.goesToPlusInfinite)
				this.rightEdge.add(subinterval);
			else
				this.rightEdge = subinterval;
			
			refresh();
		
		}
		catch (DisjointException de) {
			/**Never reached**/
		}
			
	}
	
	
	
	
	private void subFromMinusInfinite(SubInterval subinterval) {
		
		try {
		
			if(this.goesToMinusInfinite) {
				this.addSubInterval(this.leftEdge.subtract(subinterval));
				this.removeMinusInfinite();
			}
			
		
		}
		catch(DisjointException de) {
			/**If disjoint do nothing**/
		}
		
	}
	
	private void subFromMinusInfinite(InfiniteInterval subinterval) {
		
		try {
			
			if(this.goesToMinusInfinite) {
				this.addSubInterval(this.leftEdge.subtract(subinterval));
				this.removeMinusInfinite();
			}
			
		
		}
		catch(DisjointException de) {
			/**If disjoint do nothing**/
		}
		
	}
	
	
	
	
	private void refresh() {
		
		try {
		
			int i;
			if(this.goesToMinusInfinite) {
				
				for(i=0; i<this.interval.size();i++){
					if(!(this.leftEdge.isDisjoint(this.interval.get(i)))){
						this.leftEdge.add(this.interval.get(i));
						this.interval.remove(i);
					}
					else
						break;
				}
			
			}
			
			if(this.goesToPlusInfinite) {
			
				for(i = this.interval.size()-1;i>=0;i--){
					if(!(this.rightEdge.isDisjoint(this.interval.get(i)))){
						this.rightEdge.add(this.interval.get(i));
						this.interval.remove(i);
					}
					else
						break;
				}
			
			}
			
			if(this.goesToMinusInfinite && this.goesToPlusInfinite)
				if(!this.leftEdge.isDisjoint(this.rightEdge))
					this.becomeEverything();
			
			
			this.numberOfSubIntervals = 0;
			if(this.goesToMinusInfinite)
				this.numberOfSubIntervals ++;
			
			if(this.goesToPlusInfinite)
				this.numberOfSubIntervals ++;
			
			this.numberOfSubIntervals += this.interval.size();
					
		
		}
		catch(DisjointException de) {
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Adds a subinterval to this interval
	 * 
	 * @param subInterval 	The subinterval to be added
	 */
	private void addSubInterval(SubInterval subInterval) {
		
		try {
		
			if(this.interval.isEmpty())
				this.interval.add(subInterval);
			else if(subInterval.isVoid()) {
				/**do nothing**/
			}
			else{
		
				SubInterval result = subInterval;
		
				int i;
				for(i=0;i<this.interval.size();i++){
			
					if(!(this.interval.get(i).isDisjoint(result))){
						result.add(this.interval.get(i));
						this.interval.remove(i);
					}
			
				}
		
				for(i=0;i<this.interval.size();i++)
					if(this.interval.get(i).top() > result.top())
						break;
				
				this.interval.add(i, result);
		
			}
	
			this.refresh();
			
		}
		catch(DisjointException de) {/**never reached**/} 
		catch (IsVoidException e) {/**never reached**/}
		
	}
		
		
	

	
	
	
	/**
	 * Overloaded addSubInterval
	 * Adds a subinterval to this interval 
	 * 
	 * @param subInterval 	The subinterval to be added
	 */
	private void addSubInterval(InfiniteInterval subInterval) {
		
		if(subInterval.isEverything()) 
			this.becomeEverything();
		
		else if(this.isEverything()) {
			/**Do nothing**/
		}
		else if(subInterval.isMinusInfinite()) 
			this.addToMinusInfinite(subInterval);
			
		else if(subInterval.isPlusInfinite()) 
			this.addToPlusInfinite(subInterval);
		
		this.refresh();
		
	}
	
	
	
	
	private void sub(SubInterval subinterval) {
		
		try {
		
			int i;
			for(i=0;i<this.interval.size(); i++) {
				if(this.interval.get(i).isDisjoint(subinterval))
					this.interval.remove(i);
				else
					this.interval.get(i).sub(subinterval);
			}

			
			if(this.goesToMinusInfinite) {
				if(!this.leftEdge.isDisjoint(subinterval)) 
					this.addSubInterval(this.leftEdge.subtract(subinterval));
				
				this.removeMinusInfinite();
			}
			
			if(this.goesToPlusInfinite) {
				if(!this.rightEdge.isDisjoint(subinterval)) 
					this.addSubInterval(this.rightEdge.subtract(subinterval));
				
				this.removePlusInfinite();
			}
			
		
		}
		catch(DisjointException de) {/**Never reached**/}
		
		
	}
	
	
	
	private void sub(InfiniteInterval subinterval) {
	
		try {
			
			if(subinterval.isEverything()) {
				/**Do nothing**/
			}
			else {

				this.interval.clear();
				
				if(subinterval.isMinusInfinite())
					if(this.goesToMinusInfinite)
						if(this.leftEdge.getLimit() > subinterval.getLimit())
							this.leftEdge = subinterval;
				
				if(subinterval.isPlusInfinite())
					if(this.goesToPlusInfinite)
						if(this.rightEdge.getLimit() < subinterval.getLimit())
							this.rightEdge = subinterval;
				
			}
				
			
			
				
				
			
		}
		catch (NoLimitException e) {/**Never reached**/	}
	
	}
	
	
	
	
	
	
	
	
	
	
/*	
	public void unite(InfiniteInterval interval) {
		
		if(this.goesToMinusInfinite)
		
	}
	
	
	public void unite(SubInterval interval) {
		
		
		
	}
	
	
	public void unite(Interval interval) {
		
		
		
	}
	
	
	*/
	
	
	
	
	
	
}