package respa.path;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

import respa.output.SystemOut;
import respa.stateLabeling.HashMile;
import respa.stateLabeling.Location;
import respa.stateLabeling.Milestone;
import respa.stateLabeling.StateLabel;
import respa.stateLabeling.Trace;
import respa.stateLabeling.VerboseMile;
import respa.utils.LocationNode;

/**
 *	This class implements a path represented by respa.stateLabeling.*
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class Path {





	private boolean verbose;



	//The path
	//Ordered
	private LinkedList<StateLabel> path;


	//The path
	//unordered
	//We use contains() very often so it is better to also maintain a hashset 
	private HashSet<StateLabel> states;



	//All path miles
	private HashSet<Milestone> miles;//miles from original path



	//The indexof operation of LinkedList is O(n). By keeping 
	//this map, we can provide this operation in o(1).
	private HashMap<StateLabel, Integer> indexes;
	private HashMap<Integer, StateLabel> indexedstates;












	public Path(boolean verbose) {

		this.verbose=verbose;


			this.path = new LinkedList<StateLabel>();
			this.states = new HashSet<StateLabel>();
			this.miles = new HashSet<Milestone>();
			indexes = new HashMap<StateLabel, Integer>();
			indexedstates = new HashMap<Integer, StateLabel>();

	}













	public boolean contains(StateLabel state) {

		if(this.states.contains(state))
			return true;

		return false;

	}


	public boolean contains(Milestone mile) {

		if(this.miles.contains(mile))
			return true;

		return false;

	}













	public void print() {

		for(StateLabel sl:this.path)
			System.out.println(sl+" ;; "+sl.getConstraint());

	}








	public void load(File file) throws FileNotFoundException {

		if(verbose)
			loadVerbose(file);
		else
			loadHash(file);


	}

	public void load(String file) throws FileNotFoundException {

		if(verbose)
			loadVerbose(new File(file));
		else
			loadHash(new File(file));
	}



	public void load(LinkedList<StateLabel>  path) {



			this.path = (LinkedList<StateLabel>) path;
			this.states.addAll(this.path);

			for(StateLabel sl:this.path){
				miles.add(sl.crucial());
				miles.add(sl.getBegin());
				this.indexes.put(sl, indexes.size());
				this.indexedstates.put(indexes.size(), sl);
			}


		

	}
	
	/**
	 * @requires that the StateLabels of the path are connected
	 * 
	 * @param head the StateLabel of the first state
	 */
	public void load(StateLabel head) {
		
		StateLabel dummy = head;
		while(dummy!=null){
			this.path.add(dummy);
			dummy = dummy.getNext();
		}
		this.states.addAll(this.path);
		
		for(StateLabel sl:this.path){
			miles.add(sl.crucial());
			miles.add(sl.getBegin());
			this.indexes.put(sl, indexes.size());
			this.indexedstates.put(indexes.size(), sl);
		}
		
	}


















	private void loadVerbose(File file) throws FileNotFoundException {

		//new File(this.target_project+"/"+this.originalPathFile)
		Scanner scan = new Scanner(file);		
		LinkedList<String> lines = new LinkedList<String>();
		LinkedList<LocationNode> locations = new LinkedList<LocationNode>();


		//Extract original path from file
		String token = " ";
		while(scan.hasNextLine()) {

			token = scan.nextLine();
			if(token.startsWith("--if--") ||
					token.startsWith("--crucial--"))
				lines.add(token);

		}
		scan.close();


		//build path
		String [] splits;
		for(String s:lines) {
			splits = s.split("--");
			LocationNode ln = new LocationNode(splits[2],splits[1],splits[3].trim());
			locations.add(ln);
		}

		//tricky part
		Milestone ifLocation = null;
		Milestone crucial = null;
		StateLabel newState=null;
		boolean lastWasIfstmt = false;
		for(LocationNode node:locations) {

			if(node.getType().equals("if")) {

				if(lastWasIfstmt) {
					crucial = new VerboseMile(
							new Location(node.getClassName(),node.getLine()),
							new Trace(node.getTrace()));

					newState = new StateLabel(crucial,ifLocation);
					this.path.add(newState);//add to ordered path
					this.states.add(newState);//add to unordered path
					this.indexes.put(newState, path.size()-1);
					this.indexedstates.put(path.size()-1, newState);

					ifLocation = null;
					crucial = null;

				}

				ifLocation= new VerboseMile(
						new Location(node.getClassName(),node.getLine()),
						new Trace(node.getTrace()));

				this.miles.add(ifLocation);
				lastWasIfstmt=true;


			}
			else if(node.getType().equals("crucial")) {

				crucial = new VerboseMile(
						new Location(node.getClassName(),node.getLine()),
						new Trace(node.getTrace()));

				this.miles.add(crucial);

				newState = new StateLabel(crucial,ifLocation);
				this.path.add(newState);//add to ordered path
				this.states.add(newState);//add to unordered path
				this.indexes.put(newState, path.size()-1);
				this.indexedstates.put(path.size()-1, newState);

				ifLocation = null;
				crucial = null;

				lastWasIfstmt = false;

			}
			//else nothing

		}

		/*		this.originStates.addAll(this.originalPath);

		for(StateLabel sl:this.originalPath){ 
			this.originMiles.add(((BasicBlockStateLabel)sl).getBegin());
		}*/


	}






	private void loadHash(File file) throws FileNotFoundException {

		System.out.println("loading hash");

		Scanner scan = new Scanner(file);		

		String [] splits;
		String line = "";
		StateLabel newState = null;
		HashMile crucial=null,ifLocation=null;
		boolean lastWasIfstmt = false;
		while(scan.hasNextLine()) {

			line = scan.nextLine();
			splits = line.split("--");

			if(line.startsWith("--if")) {

				if(lastWasIfstmt) {
					crucial =  new HashMile(Integer.valueOf(splits[2]));

					newState = new StateLabel(crucial,ifLocation);
					this.path.add(newState);
					this.states.add(newState);
					this.indexes.put(newState, path.size()-1);
					this.indexedstates.put(path.size()-1, newState);
				}

				ifLocation= new HashMile(Integer.valueOf(splits[2]));
				this.miles.add(ifLocation);
				lastWasIfstmt=true;


			}
			else if(line.startsWith("--crucial")) {

				crucial = new HashMile(Integer.valueOf(splits[2]));
				this.miles.add(crucial);

				newState = new StateLabel(crucial,ifLocation); 
				this.path.add(newState);
				this.states.add(newState);
				this.indexes.put(newState, path.size()-1);
				this.indexedstates.put(path.size()-1, newState);
				
				lastWasIfstmt = false;

			}
			//else nothing

		}

		if(SystemOut.print_loading)
			System.out.println("[ReSPA][Path] --> loading complete: "+path.size()+" ;; "+miles.size()+" ;; "+states.size());


	}

















	public void appendState(StateLabel state) {

			this.path.add(state);
			this.states.add(state);
			this.miles.add(state.getBegin());
			this.miles.add(state.crucial());
			this.indexes.put(state, path.size()-1);
			this.indexedstates.put(path.size()-1, state);
	}









	public int size() {
		
		return this.path.size();
		
	}


	public boolean isEmpty() {
		
		return this.path.isEmpty();
		
	}


	
	
	public LinkedList<StateLabel> getPath() {
		
		return this.path;
		
	}
	
	

	public void clear() {
		
		this.path.clear();
		this.states.clear();
		this.miles.clear();
		this.indexes.clear();
		this.indexedstates.clear();
		
	}
	
	

	
	
	
	
	
	
	public Integer indexOf(StateLabel sl) {
		
		return indexes.get(sl);
		
	}
	
	
	public StateLabel get(int index) {
		
		return indexedstates.get(index);
		
	}
	
	
	

}
