package respa.search;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.string.StringSymbolic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
@Deprecated
public class HRecursiveRetainer extends Search {





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





	//////log stuff
	private long startts;
	private long startItTs;
	//////








	/**
	 * Obtains the cost of the current state
	 * @return the cost
	 */
	protected  int getCurrentCost(){
		return 0;//TODO
	}









	public  int inputCounter=0;



	public HRecursiveRetainer (Config config, JVM vm) {

		super(config, vm);
		this.labeling = new Labeling(vm);
		labeling.loadProperties();
		exploreUtils = new ExploreUtils(vm,labeling);
		phi = new Path(false);
		PQ = new PQ_Remembering();
		GB = new HashMap<StateLabel, Node>();
		Core.load();

		Log.verboseLog("[REAP][HRRetainer] --> Initiating...");


	}




























	private int it=0;


	@Override
	public void search () {

		verbose = SystemOut.debug;
		R = Core.radius;
		startts =startItTs= System.currentTimeMillis();

		firstnode = getFirstState();
		currentParent = firstnode;

		notifySearchStarted();

		if (!hasPropertyTermination()) {
			success=false;

			currentNode=firstnode;
			restoreState(firstnode);


			Log.verboseLog("[REAP][HRRetainer] --> Attempting to obtain phi");

			try {
				Node fnode = getPHI(firstnode);
				logPhiget(fnode);

				it = 0;
				double cost;
				do{
					startItTs=System.currentTimeMillis();
					it++;
					currentParent=firstnode;
					currentNode=firstnode;
					cost = fnode.getLeak();


					System.out.println("--------------------------");
					StateLabel nodeid = fnode.getLabel();
					LinkedList<StateLabel> dummy = new LinkedList<StateLabel>();
					do{
						dummy.addFirst(nodeid);
						nodeid = nodeid.prev;
					}
					while(nodeid!=null);
					phi.clear();
					boolean started=false;
					for(StateLabel sl:dummy){
						if(sl.getConstraint()!=null)
							started=true;
						if(started){
							phi.appendState(sl);
							System.out.println(sl+" - "+sl.getConstraint());
						}
					}
					System.out.println("-------------------------- phi has size "+phi.size());


					gettingPhi=false;
					PQ = new PQ_Remembering();//clear
					GB.clear();//clear
					clearLogvars();

					restoreState(firstnode);
					System.out.println("do not clear symb vars");
		//			Core.clearSymb();
					notifySearchStarted();

					if((fnode = Retain(firstnode, phi.get(phi.size()/2) , phi.getPath().getLast() ))==null){

						Log.verboseLog("[REAP][HRRetainer][ERROR] --> null path condition \n\n\n");
						done=true;
						Log.save(Core.target_project+"/retainerlog.txt");
						throw new SearchFailedException();

					}
					else {

						Log.verboseLog("\n\n\n\n\n\n\n Iteration: "+it);
						logRetainer(fnode);
						Log.verboseLog("\n\n\n\n\n\n\n ");

					}


				}
				while(cost > fnode.getLeak());

			}
			catch(SearchFailedException sfe){
				Log.verboseLog("[REAP][HRRetainer] failed. Exiting...");
				Log.save(Core.target_project+"/retainerlog.txt");
				System.exit(0);
			}








			done=true;
			super.terminate();

		}

		notifySearchFinished();
	}








	private void logRetainer(Node fnode) {

		Log.verboseLog("[REAP][HRRetainer] --> Success!");
		OutputManager outputManager = new OutputManager();
		outputManager.outputleaky(fnode.getPC());

		Log.verboseLog("LEAK: "+(OutputManager.rleak)+" = "+OutputManager.rleakPercent);
		Log.verboseLog("Residue: "+(OutputManager.residue)+" = "+OutputManager.residuePercent);
		Log.verboseLog("Elapsed time: "+(System.currentTimeMillis()-this.startts));
		Log.verboseLog("Elapsed time of this IT: "+(System.currentTimeMillis()-this.startItTs));
		Log.verboseLog("Memory: "+(Runtime.getRuntime().totalMemory()));

		Log.verboseLog("Amount of HybridDijkstra performed: "+(num_hybrid));
		Log.verboseLog("Amount of HybridDijkstra successful: "+(num_success_hybrid));
		Log.verboseLog("Amount of HybridDijkstra unsuccessful: "+(num_notsuccess_hybrid));
		Log.verboseLog("Amount of HybridDijkstra failed: "+(num_failed));

		Log.verboseLog("Amount of nodes recovered from GB: "+(num_gb));
		Log.verboseLog("Amount of new BD invocations (not recovered from GB): "+(num_bd));
		Log.verboseLog("Amount of nodes sent to GB: "+(sent_gb));

		Log.verboseLog("Amount of nodes popped from fheap: "+(popped));
		Log.verboseLog("Amount of nodes updated in fheap: "+(updated));
		Log.verboseLog("Amount of nodes pushed into fheap: "+(pushed));
		Log.verboseLog("Amount of dropped nodes (out of R): "+(outofR));

		Log.verboseLog("Size of phi: "+(phi.size()));
		Log.log(false,"new PC: "+(fnode.getPC())+"\n"+fnode.getPC().spc);
		Log.log(false,"\n\n ####################################################################################\n\n");


		Log.save(Core.target_project+"/retainerlog.txt");

		System.out.println(fnode.getPC());
		System.out.println(fnode.getPC().spc);

	}

	private void clearLogvars() {
		num_hybrid=0;
		num_success_hybrid=0;
		num_notsuccess_hybrid=0;
		num_failed=0;

		num_gb=0;
		num_bd=0;
		sent_gb=0;

		popped=0;
		updated=0;
		pushed=0;
		outofR=0;
	}

	private void logPhiget(Node fnode) {

		Log.verboseLog("[REAP][HRRetainer] --> Success!");
		OutputManager outputManager = new OutputManager();
		outputManager.outputleaky(fnode.getPC());

		Log.verboseLog("LEAK: "+(OutputManager.rleak)+" = "+OutputManager.rleakPercent);
		Log.verboseLog("Residue: "+(OutputManager.residue)+" = "+OutputManager.residuePercent);
		Log.verboseLog("Elapsed time: "+(System.currentTimeMillis()-this.startts));
		Log.verboseLog("Memory: "+(Runtime.getRuntime().totalMemory()));
		Log.log(false,"new PC: "+(fnode.getPC())+"\n"+fnode.getPC().spc);
		Log.log(false,"\n\n ####################################################################################\n\n");

	}






















































	/*PHASE 1: find phi*/




















	boolean first =false;
	Node firstnode=null;


	private Node getPHI(Node node)throws SearchFailedException {


		Log.log(verbose, "[REAP][HRRetainer][GetPhi] Step forward: "+node.getLabel()+" - "+node.getLabel().getConstraint());




		if(node.isFinducing())
			return node;


		////hack
		if(!first&&node.getLabel().getConstraint()!=null){
			firstnode=(Node)currentParent.clone();
			first = true;
		}
		////


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













	private int num_hybrid=0;
	private int num_success_hybrid=0;
	private int num_notsuccess_hybrid=0;
	private int num_failed=0;

	private Node Retain(Node src, StateLabel complyPT,StateLabel defeatPT) throws SearchFailedException {

		//log stuff
		num_hybrid++;
		//





		Node srcClone = (Node)src.clone();

		Node newsrc = HybridDijkstra(srcClone, complyPT);//TODO: VERIFICAR QUE SRCCLONE:ID != complyPT




		//TODO: rever o porque de testar o src e nao o newsrc
		if(src.getLabel().equals(phi.getPath().getLast()))
			return src;// finished





		if(newsrc.getLabel().equals(src.getLabel())){ // F was not reproduced

			//////////////////////log stuff
			num_notsuccess_hybrid++;
			Log.log(verbose, "[REAP][HRRetainer][Retain] F was *not* reproduced: BoundedDijkstra("+
					src.getLabel()+"-"+src.getLabel().getConstraint()+"->"+complyPT+"-"+complyPT.getConstraint()+") + phiComply. Unsuccessful: "+num_notsuccess_hybrid+
					" out of "+num_hybrid);
			////////////////////////////////

			defeatPT = complyPT;

		}
		else {
			//////////////////////log stuff
			num_success_hybrid++;
			Log.log(verbose, "[REAP][HRRetainer][Retain] F was reproduced: BoundedDijkstra("+
					src.getLabel()+"->"+complyPT+") + phiComply. Successful: "+num_success_hybrid+
					" out of "+num_hybrid);
			////////////////////////////////
			src = newsrc;
		}


		if(newsrc.getLabel().equals(phi.get(phi.indexOf(defeatPT)-1))){


			//////////////////////log stuff
			num_failed++;
			Log.log(verbose, "[REAP][HRRetainer][Retain] we failed to bypass this node "+src.getLabel()+
					"; Failed: "+num_failed);
			////////////////////////////////


			defeatPT = phi.getPath().getLast();//reset

			
			/// bypass that fucking bug
			martelada = src.getLabel();
			//
			
			restoreState(src);notifyStateAdvanced();//notifyStateRestored();
			currentParent = src;
			List<Node> childs = generateChildren();
			src = PickPhiChild(childs);

			PQ = new PQ_Remembering();//clear
			GB.clear();//clear


			//			System.out.println("\n\n\n !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//			StateLabel dummy=(StateLabel)src.getLabel().clone();
			//			while(dummy!=null){
			//				System.out.println("banhada: "+dummy+"-"+dummy.getConstraint());
			//				dummy = dummy.prev;
			//			}
			//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n");



		}

		try{
			complyPT = phi.get( ( phi.indexOf(src.getLabel()) + phi.indexOf(defeatPT) ) / 2  );
		}
		catch(Exception e){

			if(martelada!=null){

				Log.log(true, "[REAP][Retainer][Retain] martelada !! "+martelada);
				int index = phi.indexOf(martelada);
				index = index+1;
				complyPT = phi.get( ( index + phi.indexOf(defeatPT) ) / 2  );

			}
			else
				Log.log(true, "[REAP][Retainer][Retain] banhada !! "+martelada);
			
		}
		Log.log(verbose, "[REAP][HRRetainer][Retain] attempting a new destination node: "+complyPT);


		return Retain(src, complyPT,defeatPT);// one more round

	}

	StateLabel martelada =null;

	private int num_gb=0;
	private int num_bd=0;

	private Node HybridDijkstra(Node src, StateLabel dstID) throws SearchFailedException {

		Log.log(verbose, "[REAP][HRRetainer][HybridDijkstra] Performing hybrid starting from: "+src.getLabel());


		Node backupSrc = (Node)src.clone();
		Node Ns;
		if(GB.containsKey(dstID)){

			//////////////////////log stuff
			num_gb++;
			Log.log(verbose, "[REAP][HRRetainer][HybridDijkstra] Recovering node from garbage bin: "+dstID+
					". Amount of nodes recoverd from gb: "+num_gb);
			///////////////////////////////

			Ns = GB.get(dstID);
			restoreState(Ns);notifyStateAdvanced();



		}
		else{

			//////////////////////log stuff
			num_bd++;
			Log.log(verbose, "[REAP][HRRetainer][HybridDijkstra] Performing BoundedDijkstra starting from: "+
					src.getLabel()+" until "+dstID+". Amount of BD procedures: "+num_bd);
			///////////////////////////////

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

		Log.log(verbose, "[REAP][HRRetainer][HybridDijkstra] Performing phiComply starting from: "+Ns.getLabel());

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




	private int sent_gb=0;
	private int popped=0;
	private Node BoundedDijkstra(Node next,StateLabel destID) throws SearchFailedException {


		System.out.println("[REAP][HRRetainer][BoundedDijkstra] step forward "+next.getLabel()+" - "+next.getLabel().getConstraint());


		if(next.getLabel().equals(destID))
			return next;

		if(phi.contains(next.getLabel()) &&
				!GB.containsKey(next.getLabel())){
			GB.put(next.getLabel(),next);

			//////////////////////log stuff
			sent_gb++;
			Log.log(verbose, "[REAP][HRRetainer][BoundedDijkstra] Sent node to garbage bin: "+next.getLabel()+
					". Amount of nodes sent: "+sent_gb+" ;; the queue has size of: "+PQ.size());
			//////////////////////////////

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
		//System.out.println("pop node: "+next.getLabel()+" ; "+next.getLabel().getConstraint()+" ;; "+next.getLeak());
		restoreState(next);
		notifyStateRestored();
		notifyStateAdvanced();

		//////////////////////log stuff
		popped++;
		Log.log(false, "[REAP][HRRetainer][BoundedDijkstra] popped node: "+next.getLabel()+" queue size: "+PQ.size()+
				". Popped: "+popped);
		///////////////////////////////

		return BoundedDijkstra(next, destID);

	}

	private int updated = 0;
	private int pushed = 0;
	private int outofR=0;

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

					//////////////////////log stuff
					updated++;
					Log.log(false, "[REAP][HRRetainer][BoundedDijkstra] updated node: "+N.getLabel()+" queue size: "+PQ.size()+
							"Amount of updated nods: "+updated);
					///////////////////////////////

				}

			}
			else{
				PQ.push(N);

				//////////////////////log stuff
				pushed++;
				Log.log(false, "[REAP][HRRetainer][BoundedDijkstra] pushed node: "+N.getLabel()+" queue size: "+PQ.size()+
						". Amount of pushed: "+pushed);
				///////////////////////////////
			}
		}
		else{
			//////////////////////log stuff
			outofR++;
			Log.log(false, "[REAP][HRRetainer][BoundedDijkstra] out of range R: "+N.getLabel()+". Amount: "+outofR);
			///////////////////////////////
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

		System.out.println("[HRRetainer][Comply] "+next.getLabel()+" - "+next.getLabel().getConstraint());
		if(next.getLabel().equals(phi.getPath().getLast()))
			return next;

		List<Node> childs = generateChildren();

		if(childs.isEmpty()){
			next.setSat(false);
			return next;//throw new SearchFailedException();
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

				/*if (currentError != null){
				notifyPropertyViolated();
				if (hasPropertyTermination()) {
					//return false;
				}
			} else */{

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
					//	Node n = getCurrentState();
					System.out.println("\n\n Skipped Node: "+isEndState()+" , "+isIgnoredState()+";; "+/*n.getLabel()+*/"\n\n");

					//System.out.println(n.getPC());
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
		//statelabel.setConstraint("");
		CollectedLeak cc = new CollectedLeak();
		if(!(vm.getChoiceGenerator() instanceof PCChoiceGenerator))
			return new Node(vm,statelabel,true,cc,0);


		//statelabel.setConstraint(exploreUtils.getCurrentConstraintAsString());

		int inputPointer = inputCounter;//the symbvars created at this current state

		currentNode = new Node(vm, statelabel,false,cc,inputPointer);
		currentNode.setInOriginalPath(true);

		//currentNode.setPC(((PCChoiceGenerator)vm.getChoiceGenerator()).getCurrentPC());

		currentNode.setFinducing(false);
		if(ReSPAListener.fInducing.contains(statelabel.getConstraint())){
			currentNode.setFinducing(true);
		}

		//	if(cc.getLast()!=null)
		//	currentNode.setNodeLeak(cc.getLast().lastCalculatedLeak());
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
		/*		currentNode.setFinducing(false);
		if(MiserListener.fInducing.contains(statelabel.getConstraint())){
			currentNode.setFinducing(true);
		}*/

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


	//	private void updateSolver() {
	//		PathCondition currentPC =PathCondition.getPC(vm);
	//		if(currentPC!=null){
	//			currentPC.simplify();
	//			if(currentPC.spc!=null){
	//				currentPC.spc.simplify();
	//			}
	//		}
	//	}




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








































	/*

	private void checkInfiniteLoops(CollectedLeak cc) {

		if(currentNode.getNodeLeak()==0){

			if(currentParent.zeroedge.contains(currentNode.getLabel().crucial())){

				if(jitterlist.contains(currentNode.getLabel().crucial())){

					cc=exploreUtils.prohibitEmptyString(cc, cc.getLast());
					currentNode.ecost=cc.getLast().getFastLeak();


				}
				else{

					jitterlist.add(currentParent.getLabel().crucial()); //add parent to jitterlist
					currentNode.zeroedge.add(currentParent.getLabel().crucial());//and to zero edge list of the current node

				}

			}
			else
				currentNode.zeroedge.addAll(currentParent.zeroedge);//inherit previous zero edge nodes

			currentNode.zeroedge.add(currentNode.getLabel().crucial()); //add itself and pass along
		}
	}*/









}


