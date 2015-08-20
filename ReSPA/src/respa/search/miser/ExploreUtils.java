package respa.search.miser;



import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.util.HashSet;

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.Operator;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import respa.input.InputVariable;
import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
import respa.leak.CollectedLeak;
import respa.leak.string.MutableLeakyString;
import respa.main.Core;
import respa.main.Labeling;
import respa.main.SystemOut;
import respa.stateLabeling.HashMile;
import respa.stateLabeling.StateLabel;
import respa.utils.ConstraintClean;
import respa.utils.InputLocation;
import gov.nasa.jpf.symbc.string.StringComparator;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicCharAtInteger;
import gov.nasa.jpf.symbc.string.SymbolicIndexOfCharInteger;
import gov.nasa.jpf.symbc.string.SymbolicLengthInteger;



/**
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 *
 */
public class ExploreUtils {


	private JVM vm;




	protected Expression stmt,constant;
	protected Comparator comparator;
	protected StringComparator stringcomparator;
	protected boolean numeric;
	protected boolean charnumeric=false;

	public long start=0;


	private int currentEdge_Cost=0;



	public ExploreUtils(JVM vm, Labeling labeling) {

		this.vm = vm;
		this.labeling = labeling;

	}


	private Labeling labeling;






	protected PCChoiceGenerator currentCG=null;
	protected PathCondition currentPC =null;
	protected Constraint currentContraint=null;
	protected StringConstraint currentStringConstraint=null;
	private StateLabel currentState=null;
	private Instruction insn=null;

	//which states were backtracked
	public static HashSet<StateLabel> backtracked = new HashSet<StateLabel>();



	public static int unhandled=0;
	private static File unhandledLog=null;

	private String currentConstraintAsString="";







	public String getCurrentConstraintAsString() {
		return currentConstraintAsString;
	}






	public void setCurrentConstraintAsString(String currentConstraintAsString) {
		this.currentConstraintAsString = currentConstraintAsString;
	}






	public StateLabel getCurrentState() {
		return currentState;
	}






	public StateLabel getLabel() {

		if(currentState==null)
			return new StateLabel(new HashMile(-1), new HashMile(-1));


		return currentState;




	}










	public CollectedLeak evalLeak(CollectedLeak collected) {


		if(start == 0)
			start = System.currentTimeMillis();

		currentState = labeling.getCurrentState();
		insn =vm.getChoiceGenerator().getInsn();

		currentCG =  (PCChoiceGenerator)vm.getChoiceGenerator();
		currentPC = currentCG.getCurrentPC();


		if(currentPC!=null&&currentPC.header!=null && 
				!Core.ignoreNumeric){

			boolean stmtCompare=dissectConstraint(false);

			
			if(!stmtCompare) {//most cases

				if(stmt instanceof SymbolicCharAtInteger)

					return leak_SymbolicCharAtInteger(collected);

				else if(stmt instanceof SymbolicLengthInteger)

					return leak_SymbolicLengthInteger(collected);

				else if(stmt instanceof SymbolicIndexOfCharInteger) 

					return leak_SymbolicIndexOfCharInteger(collected);

				else if(stmt instanceof BinaryLinearIntegerExpression) 

					return leak_BinaryLinearIntegerExpression(collected);

				else if(stmt instanceof gov.nasa.jpf.symbc.numeric.SymbolicInteger)

					return leak_SymbolicInteger(collected);

				else if(stmt instanceof gov.nasa.jpf.symbc.numeric.IntegerConstant)

					return leak_IntegerConstant(collected);

				else{ //not implemented yet

					unhandled(11,stmt,"Source File Location: "+insn.getFileLocation());
					return collected;
				}

			}
			else {

				unhandled(13,stmt,"Source File Location: "+insn.getFileLocation());
				return collected;

			}




		}
		else if(currentPC!=null&&currentPC.spc!=null&&currentPC.spc.header!=null && 
				/*	( (this.lastStrConstraint==null) || 
							!this.lastStrConstraint.equals(currentPC.spc.header)) &&*/
				!Core.ignoreString){


			boolean stmtCompare=dissectStringConstraint(false);

			if(!stmtCompare) {//most cases

				if(stmt instanceof StringSymbolic)
					return leak_StringSymbolic(collected);
				else
					unhandled(17,stmt,"Source File Location: "+insn.getFileLocation());



			}
			else{
				if(stmt instanceof StringSymbolic && constant instanceof StringSymbolic){
					return leak_Binary_StringSymbolic(collected);
				}
				else{
					unhandled(18,stmt,"Source File Location: "+insn.getFileLocation());
				}
			}


			return collected;


		}
		else
			unhandled(70,stmt,"Source File Location: "+vm.getLastInstruction().getFileLocation()+": "+(currentPC==null)+" ; "+vm.getChoiceGenerator().getClass());

		return collected;


	}






	public boolean evalNode() {

		try{
		
		if(start == 0)
			start = System.currentTimeMillis();

		currentState = labeling.getCurrentState();

		if(vm.getChoiceGenerator()==null || !( vm.getChoiceGenerator() instanceof PCChoiceGenerator))
			return true;

		insn =vm.getChoiceGenerator().getInsn();

		currentCG =  (PCChoiceGenerator)vm.getChoiceGenerator();
		currentPC = currentCG.getCurrentPC();


		if(currentPC!=null&&currentPC.header!=null && 
				!Core.ignoreNumeric){

			boolean stmtCompare = dissectConstraint(false);


			if(!stmtCompare) {//most cases

				if(stmt instanceof SymbolicCharAtInteger)

					return handle_SymbolicCharAtInteger();

				else if(stmt instanceof SymbolicLengthInteger)

					return handle_SymbolicLengthInteger();

				else if(stmt instanceof SymbolicIndexOfCharInteger) 

					return handle_SymbolicIndexOfCharInteger();

				else if(stmt instanceof BinaryLinearIntegerExpression) 

					return handle_BinaryLinearIntegerExpression();

				else if(stmt instanceof gov.nasa.jpf.symbc.numeric.SymbolicInteger)

					return handle_SymbolicInteger();

				else if(stmt instanceof gov.nasa.jpf.symbc.numeric.IntegerConstant)

					return handle_IntegerConstant();

				else //not implemented yet

					return unhandled(11,stmt,"Source File Location: "+insn.getFileLocation());


			}
			else {

				//eval input cost
				//	currentEdge_Cost=evalStringCost(stringcomparator, ((StringConstant)constant).value);


				if(stmt instanceof SymbolicCharAtInteger && constant instanceof SymbolicCharAtInteger)

					return handle_unusual_SymbolicCharAtInteger();

				else //not implemented yet

					return unhandled(1333333,stmt,"Source File Location: "+insn.getFileLocation());


			}



		}
		else if(currentPC!=null&&currentPC.spc!=null&&currentPC.spc.header!=null && 
				/*	( (this.lastStrConstraint==null) || 
						!this.lastStrConstraint.equals(currentPC.spc.header)) &&*/
				!Core.ignoreString){
			/***
			 * 
			 * 
			 * 
			 * 
			 * 
			 * STRING OPERATIONS! 
			 * 
			 * 
			 * 
			 * 
			 * **/


			boolean stmtCompare = dissectStringConstraint(false);


			if(!stmtCompare) {//most cases

				if(stmt instanceof StringSymbolic)
					return handle_StringSymbolic();
				else
					return unhandled(17,stmt,"Source File Location: "+insn.getFileLocation());

			}
			else{

				if(stmt instanceof StringSymbolic && constant instanceof StringSymbolic){
					return handle_BinaryStringSymbolic();
				}
				else{

					return unhandled(1888,stmt,"Source File Location: "+insn.getFileLocation());
				}
			}
		}
		else
			return true;
		
		}
		catch(Throwable t) {
			
			return unhandled(1353454, stmt, "");
		}

	}






	public boolean forward(String debugMessage, PathCondition currentPC) {


		if(currentPC!=null)
			Core.currentPathCondition = currentPC;

		if(SystemOut.print_decisions )
			System.out.println(debugMessage);

		return true;

	}







	private boolean backtrack(int backtrack_id, int left, String comparator, int right) {
		return backtrack(backtrack_id,String.valueOf(left),comparator,String.valueOf(right));
	}

	private boolean backtrack(int backtrack_id, String left, String comparator, String right) {

	//	if(SystemOut.print_decisions)
		//	System.out.println("[REAP][ExploreUtils] --> BACKTRACK("+backtrack_id+"): the expression \""+left+" "+comparator+" "+right+"\" is false.");

		backtracked.add(currentState);

		return false;

	}




	private boolean forward(int forward_id, int left, String comparator, int right) {
		return forward(forward_id,String.valueOf(left),comparator,String.valueOf(right));
	}

	private boolean forward(int forward_id, String left, String comparator, String right) {


	//	if(SystemOut.print_decisions)
		//	System.out.println("[REAP][ExploreUtils] --> FORWARD("+forward_id+"): the expression \""+left+" "+comparator+" "+right+"\" is true.");

		if(Core.boundMemory)
			memoryBound();

		return true;

	}



	private boolean unhandled(int unhandled_id,Expression stmt, InputVariable iv, String logEntry) {

		if(unhandledLog==null)
			unhandledLog = new File(Core.target_project+"/unhandledLog.txt");

		try{
			FileWriter fw = new FileWriter(unhandledLog,true);
			fw.append("\n");
			fw.append("[REAP][ExploreUtils][Unhandled] ::: id: "+unhandled_id+" ::: Expression type: "+stmt.getClass()+" ::: Expression:"+
					stmt+" ::: InputVariable: "+iv+" ::: More info:"+logEntry);
			fw.append("\n");
			fw.flush();
			fw.close();
			unhandled++;
		}
		catch(Exception e) {e.printStackTrace();System.exit(-1);
		//do nothing
		}

		if(SystemOut.debug )
			System.out.println("[REAP][ExploreUtils] --> Unhandled issue in eval. Check unhandled.log");

		return false;//any boolean

	}

	private boolean unhandled(int unhandled_id,Expression stmt, String logEntry) {

		if(unhandledLog==null)
			unhandledLog = new File(Core.target_project+"/unhandledLog.txt");

		try{
			FileWriter fw = new FileWriter(unhandledLog,true);
			fw.append("\n");
			fw.append("[REAP][ExploreUtils][Unhandled] ::: id: "+unhandled_id+" ::: Expression type: "+stmt+" ::: Expression:"+
					stmt+" ::: More info:"+logEntry);
			fw.append("\n");
			fw.flush();
			fw.close();
			unhandled++;
		}
		catch(Exception e) {
			//do nothing
		}

		if(SystemOut.debug )
			System.out.println("[REAP][ExploreUtils] --> Unhandled issue in eval. Check unhandled.log");

		return false;//some boolean has to be the default, doesn't matter which

	}







	private boolean dissectConstraint(boolean verbose) {
		/*The constraint pointer addresses the following problem: when a state advances, JPF sometimes
		 *  adds more than one constraint and we need to know which is the relevant one (the logical test 
		 *  performed by the application). To the extent of my knowledge, the first constraint from the 
		 *  bottom of the set of new constraints is the relevant one. Thus the constraint pointer always
		 *  points to the size of the previous PC which corresponds to the relevant constraint.  */ 
		int cPointer;
		PCChoiceGenerator previousCG = currentCG.getPreviousChoiceGeneratorOfType(currentCG.getClass());
		if(previousCG==null){//possibility 1: this is the first PC CG
			cPointer = 0;
		}
		else if(previousCG.getCurrentPC().count()==currentPC.count()){//possibility 2: this is the twin state of the previous one
			previousCG = previousCG.getPreviousChoiceGeneratorOfType(previousCG.getClass());
			if(previousCG==null){//possibility 2.1: this is the third PC CG
				cPointer = 0;
			}
			else//possibility 2.2: set the counter to the size of the previous PC
				cPointer=previousCG.getCurrentPC().count();
		}
		else {//possibility 3: same as possibility 2.2
			cPointer=previousCG.getCurrentPC().count();
		}

		currentContraint = currentPC.header;
		//System.out.println("chupaaaa "+currentContraint);
		for(int i=0;i<currentPC.count()-/*constraintPointer*/cPointer-1;i++)
			currentContraint=currentContraint.and;

		currentConstraintAsString = ConstraintClean.clean(currentContraint);
		currentState.setConstraint(currentConstraintAsString);

		numeric = true;











		comparator = currentContraint.getComparator();

		boolean stmtCompare = false;//in some rare cases constant may be a stmt
		//TODO checkout the method isConstant()
		if(currentContraint.getLeft() instanceof IntegerConstant &&
				currentContraint.getRight() instanceof IntegerConstant) {
			constant = currentContraint.getRight();
			stmt = currentContraint.getLeft();
		}
		else if(currentContraint.getLeft() instanceof IntegerConstant){
			constant = currentContraint.getLeft();
			stmt = currentContraint.getRight();

			if(!(comparator.toString().equals(" == ")) && !(comparator.toString().equals(" != "))){
				comparator = invert(comparator);
			}

		}
		else if(currentContraint.getRight() instanceof IntegerConstant) {
			constant = currentContraint.getRight();
			stmt = currentContraint.getLeft();
		}
		else {
			stmtCompare = true;
			constant = currentContraint.getLeft();//in this case constant is a stmt
			stmt = currentContraint.getRight();
		}

		//debug
		if(SystemOut.print_constraints&&verbose)
			System.out.println("[REAP][ExploreUtils] --> constraint: "+currentContraint.getLeft()+" "+
					currentContraint.getComparator()+" "+currentContraint.getRight()+" ;; "+
					insn.getFileLocation()+" ;; "+currentPC.count()+" ;; ");

		return stmtCompare;
	}




	private boolean dissectStringConstraint(boolean verbose) {


		numeric = false;


		currentStringConstraint = currentPC.spc.header;
		currentConstraintAsString = ConstraintClean.clean(currentStringConstraint);
		currentState.setConstraint(currentConstraintAsString);


		if(SystemOut.print_constraints &&verbose)
			System.out.println("[REAP][ExploreUtils] --> string constraint: "+currentStringConstraint.getLeft()+" "+
					currentStringConstraint.getComparator()+" "+currentStringConstraint.getRight()+" ;; "+
					insn.getFileLocation()+" ;; "+currentPC.spc.count());


		//Expression stmt,constant;
		stringcomparator = currentStringConstraint.getComparator();
		boolean stmtCompare = false;//in some rare cases constant may be a stmt

		if(stringcomparator.toString().equals(" contains ")) {
			constant = currentStringConstraint.getRight();
			stmt = currentStringConstraint.getLeft();
		}
		else if(currentStringConstraint.getLeft() instanceof StringConstant){
			constant = currentStringConstraint.getLeft();
			stmt = currentStringConstraint.getRight();
		}
		else if(currentStringConstraint.getRight() instanceof StringConstant) {
			constant = currentStringConstraint.getRight();
			stmt = currentStringConstraint.getLeft();
		}
		else {
			stmtCompare = true;
			constant = currentStringConstraint.getLeft();//in this case constant is a stmt
			stmt = currentStringConstraint.getRight();
		}




		return stmtCompare;

	}









	//Ideally, these methods should not be in this class, but ok.

	private int evalNumericInputCost(Comparator comparator,int constant) {

		if(Core.intToChar)
			return 0;

		String comparatorString = comparator.toString();

		if(comparatorString.equals(" == ")){
			return String.valueOf(constant).length(); //byte string size
		}
		else if(comparatorString.equals(" != ")) {
			return 0; //TODO: it must be at least one digit long?
		}
		else if(comparatorString.equals(" >= ")) {//the cost of the smallest possible solving value
			return String.valueOf(constant).length(); //byte string size
		}
		else if(comparatorString.equals(" <= ")) {
			return 0; //TODO: what if we already have a constraint x>10? then it should return at least 2
		}
		else if(comparatorString.equals(" > ")) {
			return String.valueOf(constant+1).length(); //byte string size
		}
		else if(comparatorString.equals(" < ")) {
			return 0;//String.valueOf(constant-1).length(); //TODO: what if we already have a constraint x>10? then it should return at least 2
		}
		else
			System.out.println("[REAP][BoundedYen][evalNumeric]: Unknown operation!");

		return 0;

	}

	private int evalStringCost(StringComparator comparator,String constant) {


		if(comparator.toString().equals(" equals ")) {
			return constant.length();
		}
		else if(comparator.toString().equals(" notequals ")) {
			return 0; //empty string
		}
		else if(comparator.toString().equals(" == ")) {
			return constant.length();
		}
		else if(comparator.toString().equals(" != ")) {
			return 0; //empty string
		}
		else if(comparator.toString().equals(" equalsignorecase ")) {
			return constant.length();
		}
		else if(comparator.toString().equals(" notequalsignorecase ")) {
			return 0; //empty string
		}
		else if(comparator.toString().equals(" startswith ")) {
			return constant.length();
		}
		else if(comparator.toString().equals(" notstartswith ")) {
			return 0; //empty string
		}
		else if(comparator.toString().equals(" endswith ")) {
			return constant.length();
		}
		else if(comparator.toString().equals(" notendswith ")) {
			return 0; //empty string
		}
		else if(comparator.toString().equals(" contains ")) {
			return constant.length();
		}
		else if(comparator.toString().equals(" notcontains ")) {
			return 0; //empty string
		}
		else
			System.out.println("[REAP][BoundedYen][evalString]: Unknown operation!");

		return 0;
	}




	public int getCurrentEdgeCost() {
		return currentEdge_Cost;
	}


	private void memoryBound() {

		System.out.println("[REAP][ExploreUtils]--> Memory Used: "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()+"; constraint count: "+currentPC.count()+" ; time from start: "+(System.currentTimeMillis()-start));
		if(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()>1190695928){
			System.out.println("memory usage: "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()+" ;;; "+ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
			System.out.println("solving");
			System.out.println("solved");
			System.exit(0);
		}

	}
















	/****		TYPES OF CONSTRAINT		***/




	private boolean handle_SymbolicCharAtInteger() {

		charnumeric=true;

		SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt; //dont want to cast all the time
		String tostring = charat.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
		respa.input.InputVariable iv = Core.symbvars_.get(substring);

		if(iv instanceof SymbolicInputString) {

			if(((respa.input.SymbolicInputString) iv).getLength()>0){

				char [] value = new char[((SymbolicInputString) iv).getLength()];
				for(int i=iv.getStartIndex(),j=0;i<iv.getStartIndex()+((SymbolicInputString) iv).getLength()&&j<value.length;i++,j++)
					value[j]=Core.input[i];

				value = iv.getValue();//TODO do this a different way

				int left = (int)value[((IntegerConstant)charat.index).value];
				int right = ((IntegerConstant)constant).value;

				//eval input cost
				currentEdge_Cost=evalNumericInputCost(comparator, right);

				if(((IntegerConstant)charat.index).value>=value.length||value.length<1)
					return unhandled(1,stmt,iv,"Source File Location: "+insn.getFileLocation());
				else if( !eval(comparator,left,right) )
					return backtrack(1, left, comparator.toString(), right);
				else
					return forward(1, left, comparator.toString(), right);


			}
			else
				return unhandled(2,stmt,iv,"Source File Location: "+insn.getFileLocation());


		}
		else
			return unhandled(3,stmt,iv,"Source File Location: "+insn.getFileLocation());


	}




	private boolean handle_SymbolicLengthInteger() {

		charnumeric=true;

		SymbolicLengthInteger length = (SymbolicLengthInteger)stmt; //dont want to cast all the time
		String tostring = length.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
		InputVariable iv = Core.symbvars_.get(substring);


		if(iv instanceof SymbolicInputString) {

			char [] value = new char[((SymbolicInputString) iv).getLength()];
			for(int i=iv.getStartIndex(),j=0;i<iv.getStartIndex()+((SymbolicInputString) iv).getLength()&&j<value.length;i++,j++)
				value[j]=Core.input[i];

			value = iv.getValue();//TODO do this a different way

			int left = value.length;
			int right = ((IntegerConstant)constant).value;

			//eval input cost
			currentEdge_Cost=evalNumericInputCost(comparator, right);

			if(!eval(comparator,left,right))
				return backtrack(2, left, comparator.toString(), right);
			else 
				return forward(2, left, comparator.toString(), right);

		}
		else
			return unhandled(4,stmt,iv,"iv is not SymbolicInputString "+substring+" --- Source File Location"+insn.getFileLocation());


	}


	private boolean handle_SymbolicIndexOfCharInteger() {

		charnumeric=true;

		SymbolicIndexOfCharInteger length = (SymbolicIndexOfCharInteger)stmt; //dont want to cast all the time
		String tostring = length.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);//substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);

		InputVariable iv = Core.symbvars_.get(substring);

		if(iv instanceof SymbolicInputString) {

			char [] value = new char[((SymbolicInputString) iv).getLength()];
			for(int i=iv.getStartIndex(),j=0;i<iv.getStartIndex()+((SymbolicInputString) iv).getLength()&&j<value.length;i++,j++)
				value[j]=Core.input[i];

			value = iv.getValue();//TODO do this a different way

			int left = (char)String.valueOf(value).indexOf(((IntegerConstant)((SymbolicIndexOfCharInteger)stmt).getExpression()).value);
			int right = ((IntegerConstant)constant).value;

			//eval input cost
			currentEdge_Cost=evalNumericInputCost(comparator, right);

			if(!eval(comparator,left,right))
				return backtrack(3, left, comparator.toString(), right);
			else 
				return forward(3, left, comparator.toString(), right);

		}
		else{
			return unhandled(5,stmt,iv,"Source File Location: "+insn.getFileLocation());
		}


	}


	private boolean handle_BinaryLinearIntegerExpression() {



		BinaryLinearIntegerExpression binary = (BinaryLinearIntegerExpression) stmt;

		IntegerExpression binexpression,binconstant;
		if(binary.getRight() instanceof IntegerConstant){
			binexpression = binary.getLeft();
			binconstant = binary.getRight();
		}
		else{
			binexpression = binary.getRight();
			binconstant = binary.getLeft();
			//TODO: invert the operator
		}


		String tostring = binexpression.toString();


		if(tostring.contains("SYMSTRING")) {
			charnumeric=true;

			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputVariable iv = Core.symbvars_.get(substring);
			//if(iv instanceof SymbolicInputString) {

			char [] value = iv.getValue();

			if(binexpression instanceof  SymbolicCharAtInteger){
				SymbolicCharAtInteger charat = (SymbolicCharAtInteger)binexpression;



				int left = solveBinaryLinearIntExpression((int)value[((IntegerConstant)charat.index).value],((IntegerConstant)binconstant).value,binary.getOp());
				int right = ((IntegerConstant)constant).value;

				//eval input cost
				currentEdge_Cost=evalNumericInputCost(comparator, right);

				if(!eval(comparator,left,right))
					return backtrack(4, left, comparator.toString(), right);
				else 
					return forward(4, left, comparator.toString(), right);


			}
			else if(binexpression instanceof SymbolicLengthInteger){



				int left = solveBinaryLinearIntExpression(value.length,((IntegerConstant)binconstant).value,binary.getOp());
				int right = ((IntegerConstant)constant).value;

				//eval input cost
				currentEdge_Cost=evalNumericInputCost(comparator, right);

				if(!eval(comparator,left,right))
					return backtrack(5, left, comparator.toString(), right);
				else 
					return forward(5, left, comparator.toString(), right);

			}
			else 
				return unhandled(6,binexpression,iv,"Source File Location: "+insn.getFileLocation());



		}
		else if(tostring.contains("SYMINT")) {
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMINT")+6);
			InputVariable iv = Core.symbvars_.get(substring);
			int value = ((SymbolicInputInt)iv).getValueAsInt();

			int left= solveBinaryLinearIntExpression(value,((IntegerConstant)binconstant).value,binary.getOp());
			int right = ((IntegerConstant)constant).value;

			//eval input cost
			currentEdge_Cost=evalNumericInputCost(comparator, right);

			if(!eval(comparator,left,right))
				return backtrack(6, left, comparator.toString(), right);
			else 
				return forward(6, left, comparator.toString(), right);

		}
		else 
			return unhandled(7,stmt,"Source File Location: "+insn.getFileLocation());


	}






	private boolean handle_SymbolicInteger() {

		if(Core.automaticInputDetection) {

			SymbolicInteger symbint = (SymbolicInteger)stmt; //dont want to cast all the time
			String tostring = symbint.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));

			if(substring.endsWith("]"))
				substring = substring.substring(0, substring.lastIndexOf("["));

			InputVariable iv = Core.symbvars_.get(substring);

			if(iv instanceof SymbolicInputInt){
				int value=((SymbolicInputInt)iv).getValueAsInt();

				int left = value;
				int right = ((IntegerConstant)constant).value;

				//eval input cost
				currentEdge_Cost=evalNumericInputCost(comparator, right);

				if(!eval(comparator,left,right))
					return backtrack(7, left, comparator.toString(), right);
				else 
					return forward(7, left, comparator.toString(), right);
			}
			else{
				return unhandled(8,stmt,iv,"Source File Location: "+insn.getFileLocation());
			}

		}
		else {



			SymbolicInteger strsymb = (SymbolicInteger)stmt; //dont want to cast all the time
			String tostring = strsymb.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			InputVariable iv = Core.symbvars_.get(substring);
			if(iv==null)
				return unhandled(20,stmt,iv,"Source File Location: "+insn.getFileLocation());

			int left = Integer.valueOf(iv.getValueAsString());
			int right = ((IntegerConstant)constant).value;

			//eval input cost
			currentEdge_Cost=evalNumericInputCost(comparator, right);

			if(substring.contains("SYMINT")){//THIS PART IS NOT TESTED YET
				substring = substring.substring(0, substring.lastIndexOf("SYMINT")+6);

				if(!eval(comparator,left,right))
					return backtrack(8, left, comparator.toString(), right);
				else 
					return forward(8, left, comparator.toString(), right);

			}
			else if(substring.contains("SYMSTRING")){
				substring = substring.substring(0, substring.lastIndexOf("SYMINT")+6);

				//TODO: What now?
				return unhandled(9,stmt,iv,"Source File Location: "+insn.getFileLocation());

			}
			else
				return unhandled(10,stmt,iv,"Source File Location: "+insn.getFileLocation());



		}

	}


	private boolean handle_IntegerConstant() {

		gov.nasa.jpf.symbc.numeric.IntegerConstant stmtconst = (gov.nasa.jpf.symbc.numeric.IntegerConstant)stmt;

		int left = stmtconst.value;
		int right = ((IntegerConstant)constant).value;

		//eval input cost
		currentEdge_Cost=evalNumericInputCost(comparator, right);

		if(!eval(comparator,left,right))
			return backtrack(9, left, comparator.toString(), right);
		else 
			return forward(9, left, comparator.toString(), right);

	}





	private boolean handle_unusual_SymbolicCharAtInteger() {

		charnumeric=true;

		SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt;
		String tostring = charat.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("["));
		InputVariable iv1 = Core.symbvars_.get(substring);

		SymbolicCharAtInteger charat2 = (SymbolicCharAtInteger)constant; 
		String tostring2 = charat2.toString();
		String substring2 = tostring2.substring(tostring2.indexOf("buf"));
		substring2 = substring2.substring(0, substring2.lastIndexOf("["));
		InputVariable iv2 = Core.symbvars_.get(substring2);

		if(iv1 instanceof SymbolicInputString && iv2 instanceof SymbolicInputString) {

			char [] value1 = new char[((SymbolicInputString) iv1).getLength()];
			for(int i=iv1.getStartIndex(),j=0;i<iv1.getStartIndex()+((SymbolicInputString) iv1).getLength()&&j<value1.length;i++,j++)
				value1[j]=Core.input[i];

			char [] value2 = new char[((SymbolicInputString) iv2).getLength()];
			for(int i=iv2.getStartIndex(),j=0;i<iv2.getStartIndex()+((SymbolicInputString) iv2).getLength()&&j<value2.length;i++,j++)
				value2[j]=Core.input[i];

			int left= (int)iv1.getValue()[((IntegerConstant)charat.index).value];//TODO do this a different way
			int right = (int)iv2.getValue()[((IntegerConstant)charat2.index).value];//TODO do this a different way

			//eval input cost
			currentEdge_Cost=evalNumericInputCost(comparator, right);

			if(!eval(comparator,left,right))
				return backtrack(10, left, comparator.toString(), right);
			else 
				return forward(10, left, comparator.toString(), right);

		}
		else
			return unhandled(12,stmt,"Source File Location: "+insn.getFileLocation());

	}















	/**
	 * Operation such as: strSymbolic1 operator string
	 */
	private boolean handle_StringSymbolic() {

		if(Core.automaticInputDetection) {

			StringSymbolic strsymb = (StringSymbolic)stmt; //dont want to cast all the time
			String tostring = strsymb.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputVariable iv = Core.symbvars_.get(substring);


			if(iv instanceof SymbolicInputString) {

				String left = iv.getValueAsString();
				String right = ((StringConstant)constant).value;

				//eval cost
				currentEdge_Cost=evalStringCost(stringcomparator, right);

				if(!evalStringOperation(stringcomparator, left, right))
					return backtrack(11, left, stringcomparator.toString(), right);
				else
					return forward(11, left, stringcomparator.toString(), right);

			}
			else{
				return unhandled(16,stmt,iv,"Source File Location: "+insn.getFileLocation());
			}

		}
		else {

			StringSymbolic strsymb = (StringSymbolic)stmt; 
			String tostring = strsymb.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputLocation il = Core.inputLocationsSet_.get(substring);

			if(il!=null){

				String left = il.value;
				String right = ((StringConstant)constant).value;

				//eval cost
				currentEdge_Cost=evalStringCost(stringcomparator, right);

				if(!evalStringOperation(stringcomparator, left, right))
					return backtrack(12, left, stringcomparator.toString(), right);
				else
					return forward(12, left, stringcomparator.toString(), right);
			}
			else{
				return unhandled(21,stmt,"Source File Location: "+insn.getFileLocation()+". InputLocation was null. The key is "+substring);
			}

		}

	}





	/**
	 * Operation such as: strSymbolic1 operator strSymbolic2
	 */
	private boolean handle_BinaryStringSymbolic() {

		if(Core.automaticInputDetection) {

			StringSymbolic strsymb = (StringSymbolic)stmt; //dont want to cast all the time
			String tostring = strsymb.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputVariable iv1 = Core.symbvars_.get(substring);

			strsymb = (StringSymbolic)constant;
			tostring = strsymb.toString();
			substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputVariable iv2 = Core.symbvars_.get(substring);

			if(iv1==null||iv2==null)
				return unhandled(1645,stmt,"Source File Location: "+insn.getFileLocation()+
						"BinaryString, Either iv1 or iv2 is null");

			if(iv1 instanceof SymbolicInputString && iv2 instanceof SymbolicInputString) {

				String left = iv1.getValueAsString();
				String right = iv2.getValueAsString();

				//eval cost
				currentEdge_Cost=evalStringCost(stringcomparator, right);

				if(!evalStringOperation(stringcomparator, left, right))
					return backtrack(11, left, stringcomparator.toString(), right);
				else
					return forward(11, left, stringcomparator.toString(), right);

			}
			else{
				return unhandled(1646,stmt,"Source File Location: "+insn.getFileLocation());
			}

		}
		else {

			StringSymbolic strsymb = (StringSymbolic)stmt; 
			String tostring = strsymb.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputLocation il1 = Core.inputLocationsSet_.get(substring);

			strsymb = (StringSymbolic)constant;
			tostring = strsymb.toString();
			substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputLocation il2 = Core.inputLocationsSet_.get(substring);

			if(il1==null||il2==null)
				return unhandled(1647,stmt,"Source File Location: "+insn.getFileLocation()+
						"BinaryString, Either iv1 or iv2 is null");



			String left = il1.value;
			String right =il2.value;

			//eval cost
			currentEdge_Cost=evalStringCost(stringcomparator, right);

			if(!evalStringOperation(stringcomparator, left, right))
				return backtrack(12, left, stringcomparator.toString(), right);
			else
				return forward(12, left, stringcomparator.toString(), right);


		}

	}
























	public boolean eval(Comparator comparator, int value, int constant) {

		String comparatorString = comparator.toString();

		if(comparatorString.equals(" == ")){

			if(value==constant)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" != ")) {

			if(value!=constant)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" >= ")) {

			if(value>=constant)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" <= ")) {

			if(value<=constant)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" > ")) {

			if(value>constant)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" < ")) {

			if(value<constant)
				return true;
			else 
				return false;

		}

		return false;
	}



	public int solveBinaryLinearIntExpression(int expr, int constant,Operator op) {

		switch(op){
		case PLUS:       return expr + constant;
		case MINUS:      return expr - constant;
		case MUL: return expr * constant;
		case DIV: return expr / constant;
		case AND: return expr & constant;
		case OR: return expr | constant;
		case XOR: return expr ^ constant;
		case SHIFTL: return expr << constant;
		case SHIFTR: return expr >> constant;
		case SHIFTUR: return expr >>> constant;
			default: return -1;
		}


	}



	public Comparator invert(Comparator c) {

		if(c.toString().equals(" >= "))
			return Comparator.LE;
		else if(c.toString().equals(" <= "))
			return Comparator.GE;
		else if(c.toString().equals(" > "))
			return Comparator.LT;
		else if(c.toString().equals(" < "))
			return Comparator.GT;
		else
			return c;



	}


	public boolean evalStringOperation(StringComparator comparator, String value, String constant) {

		if(value==null){

			if(!comparator.toString().startsWith("not")){

				if(constant==null)
					return true;
				else
					return false;

			}
			else {

				if(constant==null)
					return false;
				else
					return true;

			}

		}
		else if(comparator.toString().equals(" equals ")) {

			return value.equals(constant);

		}
		else if(comparator.toString().equals(" notequals ")) {

			return !value.equals(constant);

		}
		else if(comparator.toString().equals(" == ")) {

			return value==constant;

		}
		else if(comparator.toString().equals(" != ")) {

			return value!=constant;

		}
		else if(comparator.toString().equals(" equalsignorecase ")) {

			return value.equalsIgnoreCase(constant);

		}
		else if(comparator.toString().equals(" notequalsignorecase ")) {

			return !value.equalsIgnoreCase(constant);

		}
		else if(comparator.toString().equals(" startswith ")) {

			return value.startsWith(constant);

		}
		else if(comparator.toString().equals(" notstartswith ")) {

			return !value.startsWith(constant);

		}
		else if(comparator.toString().equals(" endswith ")) {

			return value.endsWith(constant);

		}
		else if(comparator.toString().equals(" notendswith ")) {

			return !value.endsWith(constant);

		}
		else if(comparator.toString().equals(" contains ")) {

			return value.contains(constant);

		}
		else if(comparator.toString().equals(" notcontains ")) {

			return !value.contains(constant);

		}



		return false;
	}


















































	/****		TYPES OF COST		***/




	private CollectedLeak leak_SymbolicCharAtInteger(CollectedLeak collected) {

		SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt; //dont want to cast all the time
		String tostring = charat.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
		respa.input.InputVariable iv = Core.symbvars_.get(substring);

		if(iv instanceof SymbolicInputString) {

			MutableLeakyString cs;
			if(collected.contains(substring))//update existent
				cs= (MutableLeakyString)((MutableLeakyString)collected.get(substring)).clone();//clone!
			else{//new symbvar
				cs= new MutableLeakyString(((SymbolicInputString) iv).getValueAsString().length(),substring);
			}
			if(comparator.equals(Comparator.EQ))
				cs.applyEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
			else if(comparator.equals(Comparator.NE))
				cs.applyNotEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
			else if(comparator.equals(Comparator.GT))
				cs.applyGreaterCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
			else if(comparator.equals(Comparator.LT))
				cs.applyLowerCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
			else if(comparator.equals(Comparator.GE))
				cs.applyGreaterEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
			else if(comparator.equals(Comparator.LE))
				cs.applyLowerEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
			
			if(collected.contains(substring))//update existent
				collected.update(cs);//replace the previous version with the clone
			else
				collected.add(cs);
			//System.out.println("wtf: "+comparator+" ;; "+((char)((IntegerConstant)constant).value)+" = "+cs.getFastLeak());
			
			
		}
		else
			unhandled(32,stmt,iv,"Source File Location: "+insn.getFileLocation()+" ;; "+substring);

		return collected;

	}




	private CollectedLeak leak_SymbolicLengthInteger(CollectedLeak collected) {
		/*

		SymbolicLengthInteger length = (SymbolicLengthInteger)stmt; //dont want to cast all the time
		String tostring = length.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
		InputVariable iv = Core.symbvars_.get(substring);


		if(iv instanceof SymbolicInputString) {

			int right = ((IntegerConstant)constant).value;

			LeakyString cs = (LeakyString)((LeakyString)collected.get(substring)).clone();//clone!

			//add cost to clone
			if(comparator.toString().equals(" == "))
				cs.applyLengthEquals(right);
			else if(comparator.toString().equals(" < "))
				cs.applyLengthLT(right);
			else if(comparator.toString().equals(" > "))
				cs.applyLengthGT(right);
			else if(comparator.toString().equals(" <= "))
				cs.applyLengthLE(right);
			else if(comparator.toString().equals(" >= "))
				cs.applyLengthGE(right);


			collected.update(cs);//replace the previous version with the clone



		}
		else
			unhandled(33,stmt,iv,"iv is not SymbolicInputString "+substring+" --- Source File Location"+insn.getFileLocation());

		 */

		return collected;

	}


	private CollectedLeak leak_SymbolicIndexOfCharInteger(CollectedLeak collected) {

		return leak_SymbolicCharAtInteger(collected);

	}













	private CollectedLeak leak_BinaryLinearIntegerExpression(CollectedLeak collected) {

		/*BinaryLinearIntegerExpression binary = (BinaryLinearIntegerExpression) stmt;

		IntegerExpression binexpression,binconstant;
		if(binary.getRight() instanceof IntegerConstant){
			binexpression = binary.getLeft();
			binconstant = binary.getRight();
		}
		else{
			binexpression = binary.getRight();
			binconstant = binary.getLeft();
			//TODO: invert the operator
		}


		String tostring = binexpression.toString();


		if(tostring.contains("SYMSTRING")) {
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			InputVariable iv = Core.symbvars_.get(substring);
			//if(iv instanceof SymbolicInputString) {

			char [] value = iv.getValue();

			if(binexpression instanceof  SymbolicCharAtInteger){
				SymbolicCharAtInteger charat = (SymbolicCharAtInteger)binexpression;

				LeakyString cs = (LeakyString)collected.get(substring);
				cs.applyCharAt(((IntegerConstant)charat.index).value);
				collected.update(cs);
			}
			else if(binexpression instanceof SymbolicLengthInteger){



				int left = solveBinaryLinearIntExpression(value.length,((IntegerConstant)binconstant).value,binary.getOp());
				int right = ((IntegerConstant)constant).value;


				LeakyString cs = (LeakyString)collected.get(substring);

				if(comparator.toString().equals(" == "))
					cs.applyLengthEquals(right);
				else if(comparator.toString().equals(" < "))
					cs.applyLengthLT(right);
				else if(comparator.toString().equals(" > "))
					cs.applyLengthGT(right);
				else if(comparator.toString().equals(" <= "))
					cs.applyLengthLE(right);
				else if(comparator.toString().equals(" >= "))
					cs.applyLengthGE(right);


				collected.update(cs);



			}
			else 
				unhandled(35,stmt,iv,"Source File Location: "+insn.getFileLocation());



		}
		else if(tostring.contains("SYMINT")) {
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMINT")+6);
			InputVariable iv = Core.symbvars_.get(substring);
			int value = ((SymbolicInputInt)iv).getValueAsInt();

			int left= solveBinaryLinearIntExpression(value,((IntegerConstant)binconstant).value,binary.getOp());
			int right = ((IntegerConstant)constant).value;

			//eval input cost
			currentEdge_Cost=evalNumericInputCost(comparator, right);



		}
		else 
			 unhandled(7,stmt,"Source File Location: "+insn.getFileLocation());
		 */

		unhandled(1000,stmt,"Source File Location: "+insn.getFileLocation());

		return collected;

	}







	private CollectedLeak leak_SymbolicInteger(CollectedLeak collected) {

		unhandled(1001,stmt,"Source File Location: "+insn.getFileLocation());
		
		/*		SymbolicInteger strsymb = (SymbolicInteger)stmt; //dont want to cast all the time
		String tostring = strsymb.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("SYMINT")+6);
		InputVariable iv = Core.symbvars_.get(substring);


		int right = ((IntegerConstant)constant).value;
		//CostlyVar left = collected.get(substring);

		if(substring.contains("SYMINT")){//THIS PART IS NOT TESTED YET

			CostlyNumber cn = (CostlyNumber)((CostlyNumber)collected.get(substring)).clone();//clone!

			//add cost to clone
			if(comparator.toString().equals(" == "))
				cn.applyEquals(right);
			else if(comparator.toString().equals(" < "))
				cn.applyLT(right);
			else if(comparator.toString().equals(" > "))
				cn.applyGT(right);
			else if(comparator.toString().equals(" <= "))
				cn.applyLE(right);
			else if(comparator.toString().equals(" >= "))
				cn.applyGE(right);
			else
				cn.applyNotEquals(right);

			collected.update(cn);//replace the previous version with the clone
		}
		else
			unhandled(37,stmt,iv,"Source File Location: "+insn.getFileLocation());*/


		return collected;

	}


	private CollectedLeak leak_IntegerConstant(CollectedLeak collected) {

		/*		gov.nasa.jpf.symbc.numeric.IntegerConstant stmtconst = (gov.nasa.jpf.symbc.numeric.IntegerConstant)stmt;

		int left = stmtconst.value;
		int right = ((IntegerConstant)constant).value;*/

		//TODO: what to do here?
		return collected;

	}


















	private CollectedLeak leak_StringSymbolic(CollectedLeak collected) {



		StringSymbolic strsymb = (StringSymbolic)stmt; 
		String tostring = strsymb.toString();
		String substring = tostring.substring(tostring.indexOf("buf"));
		substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
		//InputLocation il = Core.inputLocationsSet_.get(substring);
		//CostlyVar left = collected.get(substring);

		if(/*il!=null&&*/substring.contains("SYMSTRING")/*&&left instanceof LeakyString*/){

			String right = ((StringConstant)constant).value;

			MutableLeakyString ls;
			if(collected.contains(substring))//update existent
				ls= (MutableLeakyString)((MutableLeakyString)collected.get(substring)).clone();//clone!
			else{//new symbvar
				ls= new MutableLeakyString(/*il.value.length()*/1,substring);
			}

			//add cost to clone
			if(stringcomparator.equals(StringComparator.EQUALS))
				ls.applyEquals(right);
			else if(stringcomparator.equals(StringComparator.EQUALSIGNORECASE))
				ls.applyEqualsIgnoreCase(right);
			else if(stringcomparator.equals(StringComparator.STARTSWITH))
				ls.applyStartsWith(right);
			else if(stringcomparator.equals(StringComparator.NOTSTARTSWITH))
				ls.applyNotStartsWith(right);
			else if(stringcomparator.equals(StringComparator.ENDSWITH))
				ls.applyEndsWith(right);
			else if(stringcomparator.equals(StringComparator.NOTENDSWITH))
				ls.applyNotEndsWith(right);
			else if(stringcomparator.equals(StringComparator.CONTAINS))
				ls.applyContains(right);
			else if(stringcomparator.equals(StringComparator.NOTEQUALS))
				ls.applyNotEquals(right);
			else
				unhandled(41,stmt,"unknown stringcomparator: "+stringcomparator+" ;;; Source File Location: "+insn.getFileLocation());

			if(collected.contains(substring))//update existent
				collected.update(ls);//replace the previous version with the clone
			else
				collected.add(ls);
		}
		else
			unhandled(40,stmt,"Source File Location: "+insn.getFileLocation());

		return collected;

	}


	private CollectedLeak leak_Binary_StringSymbolic(CollectedLeak collected) {


		if(stmt instanceof StringSymbolic && constant instanceof StringSymbolic) {

			StringSymbolic strsymb = (StringSymbolic)stmt; 
			String tostring = strsymb.toString();
			String substring1 = tostring.substring(tostring.indexOf("buf"));
			substring1 = substring1.substring(0, substring1.lastIndexOf("SYMSTRING")+9);

			strsymb = (StringSymbolic)constant; 
			tostring = strsymb.toString();
			String substring2 = tostring.substring(tostring.indexOf("buf"));
			substring2 = substring2.substring(0, substring2.lastIndexOf("SYMSTRING")+9);
			unhandled(42341,stmt,"Source File Location: "+insn.getFileLocation());
			//TODO
		}
		else
			unhandled(42343,stmt,"Source File Location: "+insn.getFileLocation());

		return collected;

	}


















}
