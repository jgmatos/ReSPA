package respa.listening;

import java.util.HashMap;

import respa.queue.Node;
import respa.queue.PQ;
import respa.stateLabeling.StateLabel;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.symbc.numeric.PathCondition;

public abstract class RespaPropertyListenerAdapter extends PropertyListenerAdapter{

	
	
	
	
	
	public void respa_error(String message) {}
	
	public void respa_phiget() {}
	public void respa_phigetEnd(PathCondition PC) {}
	
	public void respa_startIteration(int n) {}
	public void respa_endIteration(int n, PathCondition PC) {}
	public void respa_maxIterations(int n, PathCondition PC) {}
	
	public void respa_stepForward(Node n) {}
	public void respa_reproduced(Node nx, StateLabel nm) {}
	public void respa_notreproduced(Node nx, StateLabel nm) {}
	public void respa_suspect(Node suspect) {}
	public void respa_newNm(Node nm) {}
	
	public void respa_gbRecovered(Node nx, HashMap<StateLabel, Node> GB) {}
	public void respa_gbAdded(Node nx, HashMap<StateLabel, Node> GB) {}
	
	public void respa_dijkstra(Node nx, StateLabel nm) {}
	public void respa_pushNode(Node n,PQ pq) {}
	public void respa_popNode(Node n,PQ pq) {}
	public void respa_updateNode(Node n,PQ pq) {}
	
	public void respa_outOfR(int r, Node n) {}
	
	public void respa_finished(PathCondition PC) {}
	
	
	
	
	
	
}
