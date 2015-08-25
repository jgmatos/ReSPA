package respa.input;

import respa.utils.InputLocation;

public class queuedInput implements Comparable<queuedInput>{

	private InputLocation il;
	
	
	private int priority;
	
	
	public queuedInput(InputLocation il, int priority) {
		
		this.il=il;
		this.priority=priority;
		
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof queuedInput)
			if(((queuedInput)other).getIl().equals(il))
				if(((queuedInput)other).getPriority()==priority)
					return true;
		
		return false;
		
	}
	

	public InputLocation getIl() {
		return il;
	}



	public int getPriority() {
		return priority;
	}

	public void setIl(InputLocation il) {
		this.il = il;
	}



	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(queuedInput o) {
		if(o.getPriority()>priority)
			return -1;
		else if(o.getPriority()<priority)
			return 1;
		else
			return 0;
	}
	
}
