package respa.utils.datastruct.garbagebin;

import java.util.List;

import respa.queue.Node;
import respa.stateLabeling.StateLabel;

/**
 * This class implements a Map data strucure that allows for a key to 
 * point to multiple objects
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 *
 */
public class GarbageBin {

	
	HashMapMultiple<StateLabel, Node> gb = new HashMapMultiple<StateLabel, Node>();
	
	
	
	
	public void sendToTrash(Node node) {
		
		gb.put(node.getLabel(), node);
		
	}
	

	public void delete(Node node) {
		
		gb.remove(node.getLabel());
		
	}
	
	public void emptyTrash() {
		
		gb.clear();
		
	}
	
	
	public List<Node> recover(StateLabel sl) {
		
		return gb.get(sl);
		
	}
	
	
	public List<Node> recover(Node node) {
		
		return recover(node.getLabel());
		
	}
	
	public boolean contains(StateLabel sl)  {
		
		return gb.containsKey(sl);
		
	}
	
	public boolean contains(Node node) {
		
		return contains(node.getLabel());
		
	}
	
	
	public int size() {
		
		return this.gb.size();
		
	}
	
	
	
}
