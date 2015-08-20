package respa.main;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import respa.search.miser.input.queuedInputInt;
import respa.search.miser.input.queuedInputString;
import respa.search.miser.ReSPA;
import respa.stateLabeling.Location;
import respa.stateLabeling.VerboseMile;
import respa.utils.ClassFinder;
import respa.utils.ConstraintClean;
import respa.utils.FileInInputLocation;
import respa.utils.InputLocation;
import respa.utils.Loader;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;



/**
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 *
 * Search listener
 *
 */
public class ReSPAListener extends PropertyListenerAdapter{










	///////////////////////////////////
	///////// SYMBOLIC INPUT
	//////////////////////////////////

	//a set with all the classes in the system under test
	//we need this to filter the instructions that matter
	private HashSet<String> classNames;

	//optional: some lines of code in the system under test that we want to skip
	private HashSet<Location> ignoredLocations;

	//if we want to identify input locations manually
	//provide locations in a file
	private HashMap <Location,InputLocation> inputLocationsSet = new HashMap<Location,InputLocation>();






	///////////////////////
	//SEARCH 
	//////////////////////	
	private Search thisSearch;

	
	
	
	
	//crash point
	private VerboseMile crashMile = null;
	
	
	
	
	



	



	//////////////////////////
	//////////LOGGING
	///////////////////////////
	private long newIterationTS = 0;

	//log if successful












 

























	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private InputDetection inputDetectionManager;

	public ReSPAListener(Config conf, JPF jpf) {

		try {




			this.newIterationTS = System.currentTimeMillis();

			jpf.addPublisherExtension(ConsolePublisher.class, this);






			load();





			if(this.ignoredLocations==null||this.ignoredLocations.isEmpty())
				this.inputDetectionManager = new InputDetection(jpf.getVM());
			else
				this.inputDetectionManager = new InputDetection(jpf.getVM(),this.ignoredLocations);







		}
		catch(Exception e) {

			System.out.println("Unknown error: ");
			e.printStackTrace();
			System.out.println("************************");
			System.exit(-1);

		}




	}


	private boolean load() {

		try{

			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> loading properties...");

			loadProperties();

			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> done");







			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> loading input locations if any from "+Core.target_project+"/"+Core.inputLocationsDir+"...");

			this.inputLocationsSet = Loader.getInputLocations(Core.target_project+"/"+Core.inputLocationsDir);

			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> done");


			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> loading manual input locations if any...");

			Core.manualInputLocationsSet = FileInInputLocation.extractFileIn();

			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> done");




			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> loading source info...");

			this.classNames = Loader.getSources(Core.target_project+"/"+Core.sourceNamesFile);

			if(this.classNames==null)//if none is provided
				this.classNames=ClassFinder.getClasses();

			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> done");





			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> loading crash info...");

			this.crashMile = Loader.getStackTrace(Core.target_project+"/"+Core.crashMileFile,this.classNames);

			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> done");







			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> loading ignored locations if any...");

			this.ignoredLocations = Loader.getIgnoredLocations(Core.target_project+"/"+Core.ignoredLocationsFile);

			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> done");






			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> loading AWT files...");

			
			if(SystemOut.print_loading)
				System.out.println("[REAP][ExploreListener] --> done");

			return true;

		}catch(Throwable t) {

			t.printStackTrace();
			return false;

		}

	}





	private boolean loadProperties() {

		

		return true;
	}



	public void searchStarted(Search search) {


		this.thisSearch = search;

		search.addProperty(this);


		search.getVM().recordSteps(true);



	}











	HashMap<String,String> tmp = new HashMap<String,String>();



	public void executeInstruction(JVM vm) {

		try{

			//instruction at hand
			gov.nasa.jpf.jvm.bytecode.Instruction lastInsn =  vm.getLastInstruction();



			if(lastInsn !=null){ 

				//get the location of lastInsn
				String target = lastInsn.getFileLocation().split(".java:")[0];
				target = target.replace('/', '.');



				if(this.classNames.contains(target)){//filter

					Location l = new Location(lastInsn.getFileLocation());


					if(target.contains("XMLDocumentScanner"))
						tmp.put(l.toString(), l.toString());
					//System.out.println("insn: "+lastInsn.getFileLocation());

					//we have the option to ignore lines of code
					if(this.ignoredLocations!=null && this.ignoredLocations.contains(l)){
						vm.getCurrentThread().skipInstruction(lastInsn.getNext());
					}
					else if(Core.automaticInputDetection) { //detect input automatically?
						automaticInputDetection(lastInsn);
					}
					else {//or manually?
						markSymbolicManually(lastInsn,vm);
					}




				}



			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
		catch(Error ee){ee.printStackTrace();}
	}








	/**
	 * Automatically detect input and override it as symbolic variables
	 *
	 * This is a non trivial approach, needs revision TODO
	 * 
	 * @param lastInsn	The instruction at hand
	 */
	private void automaticInputDetection(gov.nasa.jpf.jvm.bytecode.Instruction lastInsn) {



		if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.EXECUTENATIVE) {


		}
		else if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.InvokeInstruction) {


			String lastInsnString = lastInsn.toString();



			if(lastInsnString.contains("java.io")&&lastInsnString.contains("Read")) { 
				this.inputDetectionManager.addJavaIOInsn((gov.nasa.jpf.jvm.bytecode.InvokeInstruction)lastInsn);

			}
			else if(lastInsnString.contains("java.util.Scanner")) {

				this.inputDetectionManager.addScannerInsn((gov.nasa.jpf.jvm.bytecode.InvokeInstruction)lastInsn);
			}
			else if(lastInsnString.toLowerCase().contains("console")&&(lastInsnString.contains("java.io")||lastInsnString.contains("java.lang"))) {

				this.inputDetectionManager.addConsoleInsn((gov.nasa.jpf.jvm.bytecode.InvokeInstruction)lastInsn);

			}
			else if(lastInsnString.contains("javax.swing.JTextField")||lastInsnString.contains("javax.swing.text")||
					lastInsnString.contains("javax.swing.text.JTextComponent")) {

				this.inputDetectionManager.addSwingInsn((gov.nasa.jpf.jvm.bytecode.InvokeInstruction)lastInsn);

			}
			else {

				this.inputDetectionManager.addInvokeInsn((gov.nasa.jpf.jvm.bytecode.InvokeInstruction)lastInsn);

			}

		}
		else if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.FieldInstruction) {

			this.inputDetectionManager.addFieldInsn((gov.nasa.jpf.jvm.bytecode.FieldInstruction)lastInsn);

		}
		else if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction) {

			this.inputDetectionManager.addLviInsn((gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction)lastInsn);

		}
		else if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.ARETURN) {

			this.inputDetectionManager.addReturnInsn((gov.nasa.jpf.jvm.bytecode.ARETURN)lastInsn);

		}
		else if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.ArrayInstruction) {

			if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.CALOAD) {
				this.inputDetectionManager.addCALoadInsn((gov.nasa.jpf.jvm.bytecode.CALOAD)lastInsn);
			}

		}


	}




	/**
	 * If we prefer to do this manually
	 * 
	 * @param lastInsn
	 * @param vm
	 */
	private void markSymbolicManually(gov.nasa.jpf.jvm.bytecode.Instruction lastInsn, JVM vm) {



		if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction) {

			if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.ASTORE) { //reference to other objects



				handleSymbolic(lastInsn, vm);

			}
			else if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.ISTORE) { //INT

				handleSymbolic(lastInsn, vm);


			}

		}
		else if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.FieldInstruction) {

			if(lastInsn instanceof gov.nasa.jpf.jvm.bytecode.PUTFIELD) {

				handleSymbolic(lastInsn, vm);

			}


		}












	}



	private void handleSymbolic(Instruction lastInsn, JVM vm) {

		Location key = new Location(lastInsn.getFileLocation());



		if(Core.manualInputLocationsSet!=null&&Core.manualInputLocationsSet.containsKey(key)) {//under test

			FileInInputLocation fil = Core.manualInputLocationsSet.get(key);
			fil.mile=Labeling.getCurrentMile(vm);



			if(fil.variableType.equals("java.lang.String")) {

				if(!fil.isEmptyQueue()) {

					InputLocation il = new InputLocation();
					il.location = fil.location;
					il.method = fil.method;
					il.mile = fil.mile;
					il.readerClass= fil.readerClass;
					il.representation= fil.representation;

					il.variableName= fil.variableName;
					il.variableType= fil.variableType;


					System.out.print("counter: "+(ReSPA.currentParent.getManualInputCounter()+((ReSPA)thisSearch).inputCounter)+" ;; ");
					queuedInputString qis =(queuedInputString) fil.get(ReSPA.currentParent.getManualInputCounter()+((ReSPA)thisSearch).inputCounter);
					if(qis!=null) 
						il.value=qis.getValue();
					else
						il.value="";
					((ReSPA)thisSearch).inputCounter++;


					Symbolic.newSymbolicString(vm, il);

				}

			}
			else if(fil.variableType.equals("int")) {

				if(!fil.isEmptyQueue()) {

					InputLocation il = new InputLocation();
					il.location = fil.location;
					il.method = fil.method;
					il.mile = fil.mile;
					il.readerClass= fil.readerClass;
					il.representation= fil.representation;

					il.variableName= fil.variableName;
					il.variableType= fil.variableType;

					queuedInputInt qii = (queuedInputInt) fil.get(ReSPA.currentParent.getManualInputCounter()+((ReSPA)thisSearch).inputCounter);
					if(qii!=null) {
						il.value=String.valueOf(qii.getValue());
						Symbolic.newSymbolicInt(vm, il);
						((ReSPA)thisSearch).inputCounter++;
					}


				}

			}

		}
		else if(this.inputLocationsSet!=null&&this.inputLocationsSet.containsKey(key)) {



			InputLocation il = this.inputLocationsSet.get(key);
			il.mile=Labeling.getCurrentMile(vm);

			if(il.variableType.equals("java.lang.String")) {

				if(!Core.alreadyCreated.contains(il)){

					Symbolic.newSymbolicString(vm, il);						

				}

			}
			else if(il.variableType.equals("int")) {

				if(!Core.alreadyCreated.contains(il)){

					Symbolic.newSymbolicInt(vm, il);

				}
			}
			else if(il.variableType.equals("boolean")) {

				if(!Core.alreadyCreated.contains(il)){

					Symbolic.newSymbolicBoolean(vm, il);

				}
			}


		}


	}











	/**
	 * When an exception is thrown we need to know if it is
	 * the one we want.
	 * 
	 * @param vm
	 */
	public void exceptionThrown(JVM vm) { 


		Instruction lastInsn = vm.getLastInstruction();
		String target = lastInsn.getFileLocation().split(".java:")[0];
		target = target.replace('/', '.');
		//	System.out.println("\n\n\n\n F induced : "+this.crashMile+" ;; "+lastInsn.getFileLocation()+"\n\n\n\n");

		if(this.crashMile.getLocation().getLine()==lastInsn.getLineNumber()) {

			if(this.crashMile.getLocation().getClassName().equals(target)) {
				PCChoiceGenerator pcg =null;
				ChoiceGenerator<?> cg = vm.getChoiceGenerator();
				/*			do{*/
				if(cg instanceof PCChoiceGenerator)
					pcg = (PCChoiceGenerator)cg;
				/*					cg = cg.getPreviousChoiceGenerator();
				}
				while(pcg==null&&cg!=null);*/
				//String constraint = ConstraintClean.clean(((PCChoiceGenerator)vm.getChoiceGenerator()).getCurrentPC().spc.header);


				String constraint="";
				if(pcg!=null){

					if(Core.ignoreNumericPC)
						constraint = ConstraintClean.clean(pcg.getCurrentPC().spc.header);
					else
						constraint = ConstraintClean.clean(pcg.getCurrentPC().header);

				}
				//System.out.println("\n\n\n\n F induced by the node holding this constraint: "+constraint+"\n\n\n\n currentnode: ");
				fInducing.add(constraint);
				f=true;
			}				

		}
		else if(Core.stop_any_crash) {
			String constraint = ConstraintClean.clean(((PCChoiceGenerator)vm.getChoiceGenerator()).getCurrentPC().spc.header);
			System.out.println("\n\n\n\n F induced by the node holding this constraint: "+constraint+"\n\n\n\n");
			fInducing.add(constraint);
		}


	} 

	public static HashSet<String> fInducing = new HashSet<String>();
	public static boolean f=false;


	public static ArrayList<Location> processStackTrace(InputStream stream) {

		ArrayList<Location> processed = new ArrayList<Location>();
		Scanner scan = new Scanner(stream);

		if(scan.hasNextLine())//skip first line
			scan.nextLine();

		while(scan.hasNextLine())
			processed.add(getLocation(scan.nextLine()));

		return processed;

	}


	public static ArrayList<Location> processStackTrace(String stacktrace) {

		ArrayList<Location> processed = new ArrayList<Location>();
		Scanner scan = new Scanner(stacktrace);

		if(scan.hasNextLine())//skip first line
			scan.nextLine();

		while(scan.hasNextLine())
			processed.add(getLocation(scan.nextLine()));

		return processed;

	}

	public static Location getLocation(String line) {

		return new Location(getClass(line), getLineNumber(line));

	}

	public static String getClass(String line) {

		String newline="";
		newline = line.substring(line.indexOf("at ")+3);
		newline = newline.substring(0,newline.lastIndexOf("."));
		newline = newline.substring(0,newline.lastIndexOf("."));

		return newline; 

	}

	public static int getLineNumber(String line) {

		return Integer.valueOf(line.substring(line.indexOf(":")+1,line.length()-1));

	}

















	public void stateAdvanced(Search search) {

		if(System.currentTimeMillis()-newIterationTS>Core.timeout){
			search.terminate();
			//searchFinished(thisSearch);
			System.exit(0);
		}

	}









}
