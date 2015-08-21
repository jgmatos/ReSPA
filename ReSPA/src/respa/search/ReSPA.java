package respa.search;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListener;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.string.StringSymbolic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import respa.leak.CollectedLeak;
import respa.log.Log;
import respa.main.Core;
import respa.main.Labeling;
import respa.main.ReSPAListener;
import respa.main.OutputManager;
import respa.main.Symbolic;
import respa.main.SystemOut;
import respa.path.Path;
import respa.search.ExploreUtils;
import respa.search.state.*;
import respa.search.throwable.SearchFailedException;
import respa.stateLabeling.StateLabel;


/**
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 * 
 *
 */
public class ReSPA extends Search {





	protected ExploreUtils exploreUtils;


	public Labeling labeling;


	protected boolean success = false;


	public static Node currentParent=null; 
	public static Node currentNode=null;


	public Path phi;

	private PQ_Remembering PQ;
	private int R;//radius requires R>=0
	private HashMap<StateLabel, Node> GB;

	public static boolean Finduced;




	boolean verbose = true;













	/**
	 * Obtains the cost of the current state
	 * @return the cost
	 */
	protected  int getCurrentCost(){
		return 0;//TODO
	}









	public  int inputCounter=0;



	public ReSPA (Config config, JVM vm) {

		super(config, vm);
		this.labeling = new Labeling(vm);
		labeling.loadProperties();
		exploreUtils = new ExploreUtils(vm,labeling);
		phi = new Path(false);
		PQ = new PQ_Remembering();
		GB = new HashMap<StateLabel, Node>();
		Core.load();
		setListeners();



	}




























	private int it=0;


	@Override
	public void search () {

		verbose = SystemOut.debug;
		R = Core.radius;


		Node first = getFirstState();
		currentParent = first;

		notifySearchStarted();

		if (!hasPropertyTermination()) {
			success=false;

			currentNode=first;
			restoreState(first);


			try {
				notifyPhigetStarted();

				Node fnode = getPHI(first);
				notifyPhigetEnded(fnode.getPC());

				ArrayList<PathCondition> pcs = new ArrayList<PathCondition>();
				pcs.add(fnode.getPC());

				it = 0;
				double cost;
				do{
					notifyStartIteration(it);
					it++;
					currentParent=first;
					currentNode=first;
					cost = fnode.getLeak();


					//			System.out.println("--------------------------");
					StateLabel nodeid = fnode.getLabel();
					LinkedList<StateLabel> dummy = new LinkedList<StateLabel>();
					do{
						dummy.addFirst(nodeid);
						nodeid = nodeid.prev;
					}
					while(nodeid!=null);
					phi.clear();
					for(StateLabel sl:dummy){
						phi.appendState(sl);
						//System.out.println(sl+" - "+sl.getConstraint());
					}
					//					System.out.println("-------------------------- phi has size "+phi.size());


					gettingPhi=false;
					PQ = new PQ_Remembering();//clear
					GB.clear();//clear

					restoreState(first);
					Core.clearSymb();
					notifySearchStarted();

					if((fnode = SPA(first, phi.get(phi.size()/2) , phi.getPath().getLast() ))==null){

						notifyReSPAerror("[ReSPA][SPA][ERROR] --> null path condition \n\n\n");
						done=true;
						Log.save(Core.target_project+"/retainerlog.txt");
						throw new SearchFailedException();

					}
					else {

						notifyStartIteration(it);
						pcs.add(fnode.getPC());


					}




				}
				while(cost > fnode.getLeak() && Core.maxAttempts <= pcs.size());


				PathCondition finalPC;
				if(Core.maxAttempts <= pcs.size()){

					finalPC = pcs.get((new Random()).nextInt(pcs.size()));
					notifyMaxIterations(it, finalPC);

				}
				else{

					finalPC =pcs.get(pcs.size()-1);
					//TODO: a notify for this scenario

				}
				notifyFinished(finalPC);


			}
			catch(SearchFailedException sfe){
				notifyReSPAerror("[ReSPA][SPA][ERROR] "+sfe.getMessage());

			}







			done=true;
			super.terminate();

		}

		notifySearchFinished();
	}

































































	/*PHASE 1: find phi*/
























	private Node getPHI(Node node)throws SearchFailedException {


		Log.log(verbose, "[ReSPA][SPA][GetPhi] Step forward: "+node.getLabel()+" - "+node.getLabel().getConstraint());




		if(node.isFinducing())
			return node;



		List<Node> childStates = generateChildren();

		if(childStates.isEmpty())
			throw new SearchFailedException();

		Node next = PickPhiChild(childStates);//the child that belongs to the original path

		if(!(vm.getChoiceGenerator() instanceof PCChoiceGenerator))
			node.getLabel().setNext(next.getLabel());//TODO: dont remember why we need this



		currentParent=next;
		restoreState(next);
		notifyStateAdvanced();



		return getPHI(next);

	}

	/**
	 * 
	 * @param childStates	the child nodes of the current node
	 * @return	the child node that belongs to the original execution path
	 */
	private Node PickPhiChild(List<Node> childStates) {

		Node next =null;

		if(childStates.isEmpty())
			return next;
		else if(childStates.size()==1){
			next = childStates.get(0);
			childStates.remove(0);
			return next;
		}


		next = childStates.get(0);
		if(!next.isInOriginalPath()){
			next = childStates.get(1);
			childStates.remove(1);
		}
		else
			childStates.remove(0);



		return next;

	}















	private Node SPA(Node src, StateLabel complyPT,StateLabel defeatPT) throws SearchFailedException {







		Node srcClone = (Node)src.clone();

		Node newsrc = Hybrid(srcClone, complyPT);




		if(src.getLabel().equals(phi.getPath().getLast()))
			return src;// finished





		if(newsrc.getLabel().equals(src.getLabel())){ // F was not reproduced
			notifyNotReproduced(srcClone, complyPT);
			defeatPT = complyPT;
		}
		else {// F was reproduced
			notifyReproduced(srcClone, complyPT);
			src = newsrc;
		}


		if(newsrc.getLabel().equals(phi.get(phi.indexOf(defeatPT)-1))){


			notifySuspect(src);

			defeatPT = phi.getPath().getLast();//reset



			restoreState(src);notifyStateAdvanced();//notifyStateRestored();
			currentParent = src;
			List<Node> childs = generateChildren();
			src = PickPhiChild(childs);

			PQ = new PQ_Remembering();//clear
			GB.clear();//clear





		}


		try{
			complyPT = phi.get( ( phi.indexOf(src.getLabel()) + phi.indexOf(defeatPT) ) / 2  );
		}
		catch(Exception e){

			Log.log(true, "[ReSPA][SPA] bug ");

		}
		Log.log(verbose, "[ReSPA][SPA] attempting a new destination node: "+complyPT);


		return SPA(src, complyPT,defeatPT);// one more round

	}





	private Node Hybrid(Node src, StateLabel dstID) throws SearchFailedException {

		Log.log(verbose, "[ReSPA][SPA][HybridDijkstra] Performing hybrid starting from: "+src.getLabel());


		Node backupSrc = (Node)src.clone();
		Node Ns;
		if(GB.containsKey(dstID)){


			Ns = GB.get(dstID);
			restoreState(Ns);notifyStateAdvanced();
			notifyGBrecovered(Ns);


		}
		else{

			notifyDijkstra(src, dstID);

			restoreState(src);notifyStateAdvanced();

			try{
				Ns = BoundedDijkstra(src, dstID);
			}
			catch(SearchFailedException sfe) {
				//if the search failed it can only mean that the 
				//destination node is unsat.
				return backupSrc;
			}
		}

		//		Log.log(verbose, "[ReSPA][SPA][HybridDijkstra] Performing phiComply starting from: "+Ns.getLabel());
		//TODO: notify for comply


		Node backupNs = (Node)Ns.clone();
		Node Nd = phiComply(Ns);


		if(!Nd.isSat())
			return backupSrc;

		if(Nd.isFinducing())
			return backupNs;
		else if(ReSPAListener.f) {
			ReSPAListener.f = false;
			backupNs.setFinducing(true);
			return backupNs;
		}

		return backupSrc;

	}





	private Node BoundedDijkstra(Node next,StateLabel destID) throws SearchFailedException {


		notifyStepForward(next);
		

		if(next.getLabel().equals(destID))
			return next;

		if(phi.contains(next.getLabel()) &&
				!GB.containsKey(next.getLabel())){
			GB.put(next.getLabel(),next);

			notifyGBadded(next);

		}
		currentParent=next;
		List<Node> childs = generateChildren();

		for(Node n: childs)
			queue(n);

		do{
			next = PQ.pop();

		}
		while( next==null && (!PQ.isEmpty()) );
		if(next==null)
			throw new SearchFailedException();

		restoreState(next);
		notifyStateRestored();
		notifyStateAdvanced();

		notifyPoppedNode(next);
		
		return BoundedDijkstra(next, destID);

	}



	private void queue(Node N) {

		if(N==null)
			return;



		if(!(vm.getChoiceGenerator() instanceof PCChoiceGenerator)){
			try{PQ.forcePush(N);}catch(Exception e){}
		}
		else if( N.distance() <= R ) {

			if( PQ.contains(N) ){

				if( PQ.getLeak(N) > N.getLeak() ) {

					PQ.push(N);/*the push method will automatically
						check the cost of both nodes before replacing.
						This means that the previous if test is not
						actually necessary. However it keeps the code
						coerent with the pseudocode.*/


					notifyUpdatedNode(N);
				}

			}
			else{
				PQ.push(N);


				notifyPushedNode(N);
			}
		}
		else{

			notifyOutOfR(R, N);
		}

	}

	/**
	 * Executes subphi such that: 
	 * 1) subphi \subset phi
	 * 2) subphi.last = phi.last 
	 * 
	 * @requires next.ID \in phi 
	 * @param next subphi.first
	 * @return subphi.last if satisfiable; some other node otherwise.
	 */
	private Node phiComply(Node next) throws SearchFailedException{

		System.out.println("[ReSPA][Comply] "+next.getLabel()+" - "+next.getLabel().getConstraint());
		if(next.getLabel().equals(phi.getPath().getLast()))
			return next;

		List<Node> childs = generateChildren();

		if(childs.isEmpty()){
			next.setSat(false);
			return next;
		}

		next = PickPhiChild(childs);
		restoreState(next);
		notifyStateAdvanced();

		return phiComply(next);

	}



































	/*
	 * generate the set of all child states for the current parent state
	 * 
	 * overriding methods can use the return value to determine if they
	 * have to process the childStates, e.g. to compute priorities
	 * that require the whole set
	 * 
	 * @returns false if this is cut short by a property termination or
	 * explicit termination request
	 */
	protected List<Node> generateChildren () {

		List<Node> childStates = new ArrayList<Node>();

		try{

			while (!done) {

				if (!forward()) {
					notifyStateProcessed();
					return childStates;
				}

				depth++;

				notifyStateAdvanced();

				{

					if (!isEndState() && !isIgnoredState()) {

						if (isNewState()) {


							Node newHState = getCurrentState();            
							if (newHState != null) { 

								childStates.add(newHState);
								notifyStateStored();

							}
						}

					}
					else if(isEndState()&&!ReSPAListener.fInducing.isEmpty()) {
						//TODO: merge elseif with if
						if (isNewState()) {


							Node newHState = getCurrentState();            
							if (newHState != null) { 

								childStates.add(newHState);
								notifyStateStored();

							}
						}


					}
					else{
						System.out.println("\n\n Unsat Node: "+isEndState()+" , "+isIgnoredState()+";; "+/*n.getLabel()+*/"\n\n");

					}


				}

				if(success)
					return childStates;

				backtrackToParent();
			}

		}
		catch(ClassCastException cce) {//this is a JPF bug
			childStates.clear();
			System.out.println("Caught a JPF crash");
			cce.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return childStates;

	}


	void backtrackToParent () {
		backtrack();

		depth--;
		notifyStateBacktracked();    
	}


	boolean gettingPhi=true;

	// get the current state
	protected Node getCurrentState (){

		CollectedLeak cc = (CollectedLeak)currentParent.collectedleak.clone();
		StateLabel statelabel = this.labeling.getCurrentState();
		statelabel.prev = currentParent.getLabel();



		if(!(vm.getChoiceGenerator() instanceof PCChoiceGenerator)){
			currentNode = new Node(vm,statelabel,true,cc,currentParent.getManualInputCounter(),currentParent.getPC());
			checkAndSetFinducing(statelabel);
			return currentNode;
		}


		cc = exploreUtils.evalLeak(cc);
		statelabel.setConstraint(exploreUtils.getCurrentConstraintAsString());




		int inputPointer = currentParent.getManualInputCounter()+inputCounter;//the pointer of the parent plus the symbvars created at this current state

		currentNode = new Node(vm, statelabel,false,cc,inputPointer);

		if(gettingPhi)
			currentNode.setInOriginalPath(exploreUtils.evalNode());
		else
			currentNode.setInOriginalPath(phi.contains(statelabel));

		currentNode.setPC(((PCChoiceGenerator)vm.getChoiceGenerator()).getCurrentPC());



		checkAndSetFinducing(statelabel);


		if(cc.getLast()!=null)
			currentNode.setNodeLeak(cc.getLast().lastCalculatedLeak());
		addSymbvars();

		inputCounter=0;


		if(!currentNode.isInOriginalPath())
			currentNode.setDistance(currentParent.distance()+1);
		else
			currentNode.setDistance(0);



		return currentNode;

	}

	private Node getFirstState (){

		StateLabel statelabel = this.labeling.getCurrentState();

		CollectedLeak cc = new CollectedLeak();
		if(!(vm.getChoiceGenerator() instanceof PCChoiceGenerator))
			return new Node(vm,statelabel,true,cc,0);



		int inputPointer = inputCounter;//the symbvars created at this current state

		currentNode = new Node(vm, statelabel,false,cc,inputPointer);
		currentNode.setInOriginalPath(true);


		currentNode.setFinducing(false);
		if(ReSPAListener.fInducing.contains(statelabel.getConstraint())){
			currentNode.setFinducing(true);
		}

		addSymbvars();

		inputCounter=0;


		currentNode.setDistance(0);


		return currentNode;

	}

	private void checkAndSetFinducing(StateLabel statelabel) {

		if(ReSPAListener.f){
			System.out.println("f inducing node: "+statelabel+" - "+statelabel.getConstraint());
			currentNode.setFinducing(true);
			ReSPAListener.fInducing.clear();
			ReSPAListener.f=false;
		}


	}



























	/* OTHER STUFF */








	private void restoreState (Node hState) {    

		if(vm.getChoiceGenerator()!=null&&(vm.getChoiceGenerator() instanceof PCChoiceGenerator)){
			PathCondition currentPC = ((PCChoiceGenerator)vm.getChoiceGenerator()).getCurrentPC();
			if(currentPC!=null){
				Core.currentPathCondition = currentPC;

			}
		}

		vm.restoreState(hState.getVMState());
		depth = vm.getPathLength();
		notifyStateRestored();

	}





	public boolean supportsBacktrack () {
		return false;
	}



	public void success() {
		this.success=true;
	}





	/**
	 * We need this to check for isSubset at GlobalMin
	 */
	private void addSymbvars() {

		for(StringSymbolic ss: currentParent.stringsymbolicvars)
			currentNode.stringsymbolicvars.add(ss);

		for(StringSymbolic ss: Symbolic.stringsymbolicvars)
			currentNode.stringsymbolicvars.add(ss);

		Symbolic.stringsymbolicvars.clear();

	}






	/**
	 * Use this only if strictly necessary
	 */
	public void forceUpdateCost() {
		CollectedLeak cc = (CollectedLeak)currentParent.collectedleak.clone();

		cc = exploreUtils.evalLeak(cc);
		StateLabel statelabel = exploreUtils.getLabel();
		currentNode = new Node(vm, statelabel,false,cc,currentParent.getManualInputCounter()+inputCounter);
	}






























	/**
	 * ReSPA Listening
	 */




	private ArrayList<ReSPAListener> respalisteners;


	private void setListeners() {

		respalisteners = new ArrayList<ReSPAListener>();
		for(SearchListener sl: super.listeners)
			if(sl instanceof ReSPAListener)
				respalisteners.add((ReSPAListener)sl);

	}



	private void notifyReSPAerror(String errorMessage) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_error(errorMessage);

	}

	private void notifyPhigetStarted() {

		for(ReSPAListener rl: respalisteners)
			rl.respa_phiget();

	}

	private void notifyPhigetEnded(PathCondition PC) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_phigetEnd(PC);

	}

	private void notifyStartIteration(int n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_startIteration(n);

	}


	private void notifyEndIteration(int n, PathCondition pc) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_endIteration(n,pc);

	}

	private void notifyMaxIterations(int n, PathCondition pc) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_maxIterations(n,pc);

	}


	private void notifyStepForward(Node n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_stepForward(n);


	}


	private void notifyReproduced(Node nx, StateLabel nm) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_reproduced(nx,nm);

	}

	private void notifyNotReproduced(Node nx, StateLabel nm) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_notreproduced(nx,nm);


	}

	private void notifySuspect(Node suspect) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_suspect(suspect);


	}

	private void notifyNewNm(Node Nm) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_newNm(Nm);

	}

	private void notifyGBrecovered(Node n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_gbRecovered(n,GB);

	}

	private void notifyGBadded(Node n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_gbAdded(n,GB);


	}

	private void notifyDijkstra(Node nx, StateLabel nm) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_notreproduced(nx,nm);

	}

	private void notifyPushedNode(Node n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_pushNode(n,PQ);

	}

	private void notifyPoppedNode(Node n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_popNode(n,PQ);

	}

	private void notifyUpdatedNode(Node n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_updateNode(n,PQ);

	}

	private void notifyOutOfR(int r,Node n) {

		for(ReSPAListener rl: respalisteners)
			rl.respa_outOfR(r,n);

	}

	private void notifyFinished(PathCondition pc) {
		for(ReSPAListener rl: respalisteners)
			rl.respa_finished(pc);
	}













}


