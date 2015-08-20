package respa.search.miser.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import respa.stateLabeling.StateLabel;

/**
 * 
 * @author Joao Gouveia de Matos - GSD/INESC-ID
 * 
 * Shuffle queue. Pop random elements from the queue. 
 * 
 * We keep a hashmap to keep track of the elements that need to be updated, because
 * FibonacciHeap does not have a 'contains' method. Plus, we want to replace the
 * previous instance of the node by the new instance in O(1).
 * 
 * This version remember nodes that were already visited. If we push node n for the
 * second time:
 * 1) if n is still in the queue and n' is cheaper than n, then the cost of n is updated to the cost of n'
 * 2) if n is still in the queue and n' is more expensive than n, n' is discarded
 * 3) if n is no longer in the queue, n' is discarded
 * 
 */
public class SQ_Remembering implements PQ {


	private ArrayList<StateLabel> list;

	private HashMap<StateLabel,Node> contentmap;

	private HashSet<StateLabel> visited;

	public SQ_Remembering() {

		list = new ArrayList<StateLabel>();
		this.contentmap = new HashMap<StateLabel, Node>();
		this.visited = new HashSet<StateLabel>();

	}


	public Node pop() {

		if(list.size()==0)
			return null;


		StateLabel sl = list.get( (new Random()).nextInt(list.size()));
		Node pop = contentmap.get(sl);
		contentmap.remove(sl);
		return pop;

	}

	public void push(Node hs) {


		if(contentmap.containsKey(hs.getLabel())){//not new but possibly cheaper 
			System.out.println("[REAP][PriorityQueue]-> Replace!");
			contentmap.put(hs.getLabel(),hs);
			visited.add(hs.getLabel());
		}
		else if(!visited.contains(hs.getLabel())) {//new node
			list.add(hs.getLabel());	
			contentmap.put(hs.getLabel(),hs);
			visited.add(hs.getLabel());
		}
		//else: if a node was already popped, discard this new version

	}

	/**
	 * Careful when using this. 
	 * 
	 * @param hs
	 */
	public void forcePush(Node hs) {
		list.add(hs.getLabel());	
		contentmap.put(hs.getLabel(),hs);
		visited.add(hs.getLabel());
	}

	public Node peek() {
			return null;//doesnt make sense in this context
	}

	public int size() {

		return contentmap.size();

	}

	public boolean isEmpty() {
		return list.size()==0;
	}

	public boolean contains(Node N) {

		return contentmap.containsKey(N.getLabel());

	}

	public boolean contains(StateLabel sl) {

		return contentmap.containsKey(sl);

	}







}
