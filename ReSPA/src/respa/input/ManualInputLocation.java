package respa.input;

import java.util.PriorityQueue;

import respa.utils.InputLocation;

@Deprecated
public class ManualInputLocation {
	private InputLocation il;
	private PriorityQueue<queuedInput> queue = new PriorityQueue<queuedInput>();

	public void add(queuedInput qi) {
		queue.add(qi);
	}
	
	public void add(InputLocation il,String value) {
		add(new queuedInputString(il,value,queue.size()));
	}

	
	public queuedInput pop() {
		
		return queue.poll();
		
	}
	
	public queuedInput peek() {
		
		return queue.peek();
		
	}

	public InputLocation getIl() {
		return il;
	}

	public void setIl(InputLocation il) {
		this.il = il;
	}
	
	public int sizeQueue() {
		return queue.size();
	}
	
	
	public boolean isEmptyQueue() {
		return queue.isEmpty();
	}
	
}
