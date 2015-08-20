package respa.utils;

import gov.nasa.jpf.jvm.RestorableVMState;

import java.util.LinkedList;


/**
 * 
 * @author jmatos
 *
 * A fixed size stack
 * If size limit is reached the oldest element 
 * is removed when a new element is pushed
 *
 */
public class BoundedStack {

	
	private LinkedList<RestorableVMState> stack = new LinkedList<RestorableVMState>();
	
	private int max;
	
	
	
	public BoundedStack(int max) {
		
		this.max = max;
		
	}
	
	
	
	public RestorableVMState pop() {
		
		RestorableVMState state = null;
		
		if(!stack.isEmpty()){
			
			state =  stack.getLast();
			this.stack.removeLast();
		}
		
		return state;
		
	}
	
	//this is the main difference between this 
	//implementation and the common stack implementation
	public void push(RestorableVMState state) {
		
		this.stack.add(state);
		
		if(this.stack.size()>max)
			this.stack.removeFirst();
		
	}
	
	public boolean isEmpty() {
		
		return this.stack.isEmpty();
		
	}
	
	public int size() {
		
		return this.stack.size();
		
	}
	
	public RestorableVMState peek() {
		
		if(!stack.isEmpty())
			return stack.getLast();
		
		return null;
		
	}
	
	
	public void setNewMax(int newMax) {
		
		this.max = newMax;
		
	}
	
	
	public void clear() {
		
		this.stack.clear();
		
	}
	
}
