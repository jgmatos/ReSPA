package respa.stateLabeling;

import java.util.List;

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import respa.main.ReSPAConfig;




/**
 * 
 * @author Joao Matos / GSD INESC-ID
 * 
 * This class provides methods to label JPF states
 *
 */
public class Labeling {





	private JVM vm;

	private static int stepForCrucial;




	


	


	public Labeling(JVM vm) {

		this.vm = vm;
		stepForCrucial = 1;//default

	}





	public Milestone getCurrentBegin() {

		gov.nasa.jpf.jvm.bytecode.Instruction insn;
		if(vm.getChoiceGenerator()!=null)
			insn =vm.getChoiceGenerator().getInsn();
		else
			insn = vm.getLastInstruction();
		
		String fileLocation = insn.getFileLocation().replace(".java","");
		Location l = new Location(fileLocation);

		VerboseMile ifStmtMile = new VerboseMile(l);


		List<StackFrame> stack = vm.getCurrentThread().getInvokedStackFrames();
		String stringTrace = "(";
		int i=0; 
		for(i = stack.size()-2; i>=0; i--)
			stringTrace = stringTrace+(stack.get(i).getClassName()+":"+stack.get(i).getLine()+" <<< ");

		if(i<stack.size()-2){
			stringTrace = stringTrace.substring(0, stringTrace.length()-5);
			stringTrace.trim();
			stringTrace = stringTrace+")";
		}
		else
			stringTrace=null;


		Trace trace;
		if(stringTrace!=null)
			trace= new Trace(stringTrace);
		else
			trace =null;

		//		Trace trace = new Trace(this.stackTraceMap.get(l));
		//	if(this.stackTraceMap.get(l)!=null)trace = new Trace(this.stackTraceMap.get(l));
		//		System.out.println("Compare: "+trace+" :::::::::::: "+(new Trace(this.stackTraceMap.get(l))));

		
		ifStmtMile.setTrace(trace);

		
		if(ReSPAConfig.verbose)
			return ifStmtMile;
		else
			return new HashMile(ifStmtMile);

	}

	
	public Milestone getCurrentCrucial() {

		gov.nasa.jpf.jvm.bytecode.Instruction insn;
		if(vm.getChoiceGenerator()!=null)
			insn =vm.getChoiceGenerator().getInsn();
		else
			insn = vm.getLastInstruction();
		
		VerboseMile crucialMile;
		
		List<StackFrame> stack = vm.getCurrentThread().getInvokedStackFrames();
		String stringTrace = "(";
		int i=0; 
		for(i = stack.size()-2; i>=0; i--)
			stringTrace = stringTrace+(stack.get(i).getClassName()+":"+stack.get(i).getLine()+" <<< ");

		if(i<stack.size()-2){
			stringTrace = stringTrace.substring(0, stringTrace.length()-5);
			stringTrace.trim();
			stringTrace = stringTrace+")";
		}
		else
			stringTrace=null;


		Trace trace;
		if(stringTrace!=null)
			trace= new Trace(stringTrace);
		else
			trace =null;

		
		if(vm.getCurrentTransition().getStepCount()<=1) {
			Instruction crucialInsn = insn.getNext();
			String crucialLocation = crucialInsn.getFileLocation().replace(".java","");
			crucialMile = new VerboseMile(new Location(crucialLocation),trace);
		}
		else {
			crucialMile = new VerboseMile(new Location(vm.getCurrentTransition().getStep(stepForCrucial).toString()),trace);
		}
		
	//	if(vm.getChoiceGenerator()!=null)
		//	System.out.println("aaaaa: "+crucialMile+" ;; "+(new HashMile(crucialMile))+
			//		" ;; "+stack.size()+" == "+vm.getChoiceGenerator().getThreadInfo().getInvokedStackFrames().size()+" == "+vm.getCurrentThread().getStack().size()+" == "+vm.getCurrentThread().getChangedStackFrames().size());

		if(ReSPAConfig.verbose)
			return crucialMile;
		else
			return new HashMile(crucialMile);
		
	}


	
	public Milestone getCurrentCrucial(Trace trace) {
		
		gov.nasa.jpf.jvm.bytecode.Instruction insn;
		if(vm.getChoiceGenerator()!=null)
			insn =vm.getChoiceGenerator().getInsn();
		else
			insn = vm.getLastInstruction();
		
		VerboseMile crucialMile;
		
		if(vm.getCurrentTransition().getStepCount()<=1) {
			Instruction crucialInsn = insn.getNext();
			String crucialLocation = crucialInsn.getFileLocation().replace(".java","");
			crucialMile = new VerboseMile(new Location(crucialLocation),trace);
		}
		else {
			crucialMile = new VerboseMile(new Location(vm.getCurrentTransition().getStep(stepForCrucial).toString()),trace);
		}

		if(ReSPAConfig.verbose)
			return crucialMile;
		else
			return new HashMile(crucialMile);
		
	}
	
	
	
	public Milestone getCurrentBegin(Trace trace) {

		gov.nasa.jpf.jvm.bytecode.Instruction insn;
		if(vm.getChoiceGenerator()!=null)
			insn =vm.getChoiceGenerator().getInsn();
		else
			insn = vm.getLastInstruction();
		
		String fileLocation = insn.getFileLocation().replace(".java","");
		Location l = new Location(fileLocation);

		VerboseMile ifStmtMile = new VerboseMile(l);

		ifStmtMile.setTrace(trace);

		if(ReSPAConfig.verbose)
			return ifStmtMile;
		else
			return new HashMile(ifStmtMile);
	
	}
	
	
	
	
	
	
	public void loadProperties() {
		
		
		try{
		
			stepForCrucial = Integer.valueOf(ReSPAConfig.properties.getProperty("step_for_crucial"));

		}
		catch(Exception e) {
			stepForCrucial=1;
			System.out.println(":::::::::::WARNING: Setting stepForCrucial unsuccessful. Check properties file");
		}
	}
	
	
	

	public StateLabel getCurrentState() {
		
		Milestone ifStmtMile = getCurrentBegin();
		Milestone crucialMile = getCurrentCrucial();

		StateLabel currentState = new StateLabel(crucialMile, ifStmtMile);;
		
		return currentState;
		
	}
	
	
	
	
	
	
	
	
	
	
	public static Milestone getCurrentMile(JVM vm) {
		
		
		gov.nasa.jpf.jvm.bytecode.Instruction insn =vm.getLastInstruction();
		String fileLocation = insn.getFileLocation().replace(".java","");
		Location l = new Location(fileLocation);

		VerboseMile currentMile = new VerboseMile(l);


		List<StackFrame> stack = vm.getCurrentThread().getInvokedStackFrames();
		String stringTrace = "(";
		int i=0; 
		for(i = stack.size()-2; i>=0; i--)
			stringTrace = stringTrace+(stack.get(i).getClassName()+":"+stack.get(i).getLine()+" <<< ");

		if(i<stack.size()-2){
			stringTrace = stringTrace.substring(0, stringTrace.length()-5);
			stringTrace.trim();
			stringTrace = stringTrace+")";
		}
		else
			stringTrace=null;


		Trace trace;
		if(stringTrace!=null)
			trace= new Trace(stringTrace);
		else
			trace =null;

		
		currentMile.setTrace(trace);

		
		if(ReSPAConfig.verbose)
			return currentMile;
		else
			return new HashMile(currentMile);

		
	}
	
	public static Milestone getCurrentCrucial(JVM vm) {
		
		
		gov.nasa.jpf.jvm.bytecode.Instruction insn;
		if(vm.getChoiceGenerator()!=null)
			insn =vm.getChoiceGenerator().getInsn();
		else
			insn = vm.getLastInstruction();
		
		VerboseMile crucialMile;
		
		List<StackFrame> stack = vm.getCurrentThread().getInvokedStackFrames();
		String stringTrace = "(";
		int i=0; 
		for(i = stack.size()-2; i>=0; i--)
			stringTrace = stringTrace+(stack.get(i).getClassName()+":"+stack.get(i).getLine()+" <<< ");

		if(i<stack.size()-2){
			stringTrace = stringTrace.substring(0, stringTrace.length()-5);
			stringTrace.trim();
			stringTrace = stringTrace+")";
		}
		else
			stringTrace=null;


		Trace trace;
		if(stringTrace!=null)
			trace= new Trace(stringTrace);
		else
			trace =null;

		
		if(vm.getCurrentTransition().getStepCount()<=1) {
			Instruction crucialInsn = insn.getNext();
			String crucialLocation = crucialInsn.getFileLocation().replace(".java","");
			crucialMile = new VerboseMile(new Location(crucialLocation),trace);
		}
		else {
			crucialMile = new VerboseMile(new Location(vm.getCurrentTransition().getStep(stepForCrucial).toString()),trace);
		}

		if(ReSPAConfig.verbose)
			return crucialMile;
		else
			return new HashMile(crucialMile);
		
	}
	
	
	
	

}
