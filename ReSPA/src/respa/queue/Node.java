
package respa.queue;


import java.util.ArrayList;
import java.util.HashSet;

import respa.leak.CollectedLeak;
import respa.stateLabeling.Milestone;
import respa.stateLabeling.StateLabel;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.RestorableVMState;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.string.StringSymbolic;


/**
 * 
 * @author Joao Gouveia Matos /GSD INESC-ID
 * 
 * A restorable vm state with some more information
 *
 */
public class Node implements Comparable<Node>, Cloneable{

	protected RestorableVMState vmState;
	protected int     stateId;
	protected StateLabel label;
	private boolean inOriginalPath;
	
	private int distance;
	private boolean isFinducing;

	private int manualInputCounter = 0;
	
	
	public ArrayList<StringSymbolic> stringsymbolicvars = new ArrayList<StringSymbolic>();
	
	public HashSet<Milestone> zeroedge = new HashSet<Milestone>();
	
	public CollectedLeak collectedleak;
	private double nodeLeak = 0.0;

	private PathCondition PC;
	
	
	private boolean sat = true;
	
	
	
	private Node(){
		
	}
	
	public Node (JVM vm) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = null;
		inOriginalPath = true;
		collectedleak=new CollectedLeak();
	}
	
	public Node (JVM vm,StateLabel label) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		inOriginalPath = false;
		collectedleak=new CollectedLeak();
	}

	public Node (JVM vm,StateLabel label,int manualInputCounter) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		inOriginalPath = false;
		this.manualInputCounter=manualInputCounter;
		collectedleak=new CollectedLeak();
	}

	public Node (JVM vm,StateLabel label,CollectedLeak cost,int manualInputCounter) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		inOriginalPath = false;
		this.manualInputCounter=manualInputCounter;
		collectedleak=cost;
	}
	
	public Node (JVM vm,StateLabel label,boolean inOriginalPath) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		this.inOriginalPath = inOriginalPath;
		collectedleak=new CollectedLeak();
	}
	
	public Node (JVM vm,StateLabel label,boolean inOriginalPath,int cost) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		this.inOriginalPath = inOriginalPath;
		collectedleak=new CollectedLeak();
	}

	public Node (JVM vm,StateLabel label,boolean inOriginalPath,int cost,int manualInputCounter) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		this.inOriginalPath = inOriginalPath;
		this.manualInputCounter = manualInputCounter;
		collectedleak=new CollectedLeak();
	}

	public Node (JVM vm,StateLabel label,boolean inOriginalPath,CollectedLeak cost,int manualInputCounter) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		this.inOriginalPath = inOriginalPath;
		this.manualInputCounter = manualInputCounter;
		collectedleak=cost;
	}
	
	public Node (JVM vm,StateLabel label,boolean inOriginalPath,CollectedLeak cost,int manualInputCounter,PathCondition pc) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		this.inOriginalPath = inOriginalPath;
		this.manualInputCounter = manualInputCounter;
		collectedleak=cost;
		this.PC = pc;
	}

	
	public Node(JVM vm,StateLabel label,int distance,int cost) {
		stateId = vm.getStateId();
		vmState = vm.getRestorableState();
		this.label = label;
		this.distance = distance;
		collectedleak=new CollectedLeak();
	}

	
	public RestorableVMState getVMState () {
		return vmState;
	}

	public int getStateId() {
		return stateId;
	}

	public StateLabel getLabel() {
		return label;
	}

	public void setLabel(StateLabel label) {
		this.label = label;
	}

	public boolean isInOriginalPath() {
		return inOriginalPath;
	}

	public void setInOriginalPath(boolean inOriginalPath) {
		this.inOriginalPath = inOriginalPath;
	}

	public double getLeak() {
		return collectedleak.leak();
	}
	public double getFinalLeak() {
		return collectedleak.finalCost();
	}



	public int distance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
	public boolean isFinducing() {
		return isFinducing;
	}

	public void setFinducing(boolean isFinducing) {
		this.isFinducing = isFinducing;
	}

	@Override
	public int compareTo(Node arg0) {

/*		if(cost>arg0.getCost())
			return 1;
		else if(cost<arg0.getCost())
			return -1;*/
		
		if(collectedleak.leak()>arg0.getLeak())
			return 1;
		else if(collectedleak.leak()<arg0.getLeak())
			return -1;
		
		return 0;
	}

	@Override
	public boolean equals(Object other) {
		
		if(other instanceof Node){
			if(((Node)other).getLabel().equals(this.label))
				return true;
		}
		return false;
	}

	
	
	
	public int getManualInputCounter() {
		
		return this.manualInputCounter;
		
	}
	
	public void setManualInputCounter(int manualInputCounter) {
		
		this.manualInputCounter = manualInputCounter;
		
	}

	public PathCondition getPC() {
		return PC;
	}

	public void setPC(PathCondition pC) {
		PC = pC;
	}

	public double getNodeLeak() {
		return nodeLeak;
	}

	public void setNodeLeak(double nodeLeak) {
		this.nodeLeak = nodeLeak;
	}

	public boolean isSat() {
		return sat;
	}

	public void setSat(boolean sat) {
		this.sat = sat;
	}

	
	
	

	private void setVmState(RestorableVMState vmState) {
		this.vmState = vmState;
	}

	
	
	private void setStateId(int stateId) {
		this.stateId = stateId;
	}

	@Override
	public Object clone() {

		Node clone = new Node();
		clone.setVmState(vmState);
		clone.setStateId(stateId);
		clone.setInOriginalPath(inOriginalPath);
		clone.setDistance(distance);
		clone.setFinducing(isFinducing);
		clone.setManualInputCounter(manualInputCounter);
		clone.stringsymbolicvars = stringsymbolicvars;
		clone.zeroedge = zeroedge;
		clone.collectedleak = collectedleak;
		clone.setNodeLeak(nodeLeak);
		clone.setPC(PC);
		clone.setSat(sat);
		clone.setLabel(label);

		
		return clone;
	}
	
	
	
	

}
