package respa.search.state;

import java.util.HashMap;
import java.util.HashSet;

import respa.stateLabeling.StateLabel;

/**
 * 
 * @author Joao Gouveia de Matos - GSD/INESC-ID
 * 
 * Use the FibonacciHeap implementation of Apache to implement
 * our priority queue. 
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
public class PQ_Remembering implements PQ {

	private FibonacciHeap heap;

	private HashMap<StateLabel,Node> contentmap;

	private HashSet<StateLabel> visited;

	public PQ_Remembering() {

		this.heap = new FibonacciHeap();
		this.contentmap = new HashMap<StateLabel, Node>();
		this.visited = new HashSet<StateLabel>();

	}


	public Node pop() {

		if(heap.size()==0)
			return null;

		StateLabel sl =(StateLabel) heap.popMin();
		Node pop = contentmap.get(sl);
		contentmap.remove(sl);
		return pop;

	}

	public void push(Node hs) {


		if(contentmap.containsKey(hs.getLabel())){//not new but possibly cheaper 
			if(contentmap.get(hs.getLabel()).getLeak()>hs.getLeak()){//update node priority
				//System.out.println("[REAP][PriorityQueue]-> Decrease Key!");
				heap.decreaseKey(hs.getLabel(),hs.getLeak());
				contentmap.put(hs.getLabel(),hs);
				visited.add(hs.getLabel());
			}
		}
		else if(!visited.contains(hs.getLabel())) {//new node
			heap.add(hs.getLabel(), hs.getLeak());	
			contentmap.put(hs.getLabel(),hs);
			visited.add(hs.getLabel());
		}
		//else: if a node was already popped, there is no way that the new version is cheaper

	}
	
	/**
	 * Careful when using this. 
	 * 
	 * @param hs
	 */
	public void forcePush(Node hs) {
		heap.add(hs.getLabel(), hs.getLeak());	
		contentmap.put(hs.getLabel(),hs);
		visited.add(hs.getLabel());
	}

	public Node peek() {
		if(heap.size()==0)
			return null;

		StateLabel sl = (StateLabel)heap.peekMin();
		Node pop = contentmap.get(sl);
		return pop;
	}

	public int size() {

		return contentmap.size();

	}

	public boolean isEmpty() {
		return heap.size()==0;
	}

	public boolean contains(Node N) {

		return contentmap.containsKey(N.getLabel());

	}

	public boolean contains(StateLabel sl) {

		return contentmap.containsKey(sl);

	}
	
	public double getLeak(Node N) {
		
		return getLeak(N.getLabel());
		
	}
	
	public double getLeak(StateLabel sl) {
		
		return contentmap.get(sl).getLeak();
		
	}





}
