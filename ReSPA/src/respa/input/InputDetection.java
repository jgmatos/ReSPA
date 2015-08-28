package respa.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.LocalVarInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.FieldInstruction;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils.VarType;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import respa.main.ReSPAConfig;
import respa.output.SystemOut;
import respa.stateLabeling.Location;
import gov.nasa.jpf.symbc.string.StringSymbolic;
 

/**
 * 
 * @author Joao Matos / GSD INESC-ID / jmatos@gsd.inesc-id.pt
 *  
 * Manage fields related to input creation
 * 
 * 1) Store every char[] and byte[]
 * 2) If java.io.Read operation is done over these fields, validate them
 * 3) Every String, char, long, boolean, double that touches the
 * 		validated fields should be marked as symbolic
 * 
 * <2do> symbolic int will remain a limitation 
 * <2do> consider other types such as StringBuffer
 *
 */
public class InputDetection {


	//assuming that there will be no multiple readers
	public HashMap<String, InputSource> inputSources;
	//	public ArrayList<SimpleField> symbolicFields;

	private HashSet<Location> ignoredLocations;

	private L currentL;

	private JVM vm; 

	private ArrayList<InvokedInsn> invoked;

	private boolean accepting = true;

	public SymbVarCreated lastCreated=null;


	public InputDetection(JVM vm) {

		inputSources = new HashMap<String,InputSource>();

		this.vm = vm;//pointer to vm

		this.invoked = new ArrayList<InvokedInsn>();

		this.ignoredLocations = new HashSet<Location>();

	}



	public InputDetection(JVM vm,HashSet<Location> ignoredLocations) {

		inputSources = new HashMap<String,InputSource>();

		this.vm = vm;//pointer to vm

		this.invoked = new ArrayList<InvokedInsn>();

		this.ignoredLocations = ignoredLocations;

	}




	


/*	public void setAcceptedSymbVars(boolean symbstring,boolean symbint,boolean symbarray) {

		this.symbInt = symbint;
		this.symbString = symbstring;
		this.symbArray = symbarray;

	}*/




	/**
	 * When a java.io.*read* is made taint the location
	 * 
	 * @param javaio
	 */
	public void addJavaIOInsn(gov.nasa.jpf.jvm.bytecode.InvokeInstruction javaio) {

		//String stringInsn = javaio.toString();


		MethodInfo mi = javaio.getInvokedMethod();
		String methodname = mi.getName();
		int numberOfArgs = mi.getNumberOfArguments();

		if(methodname.equals("readLine")) {//readline() is provided by many java.io classes and also java.util.Scanner

			taint(javaio);

			handleReadTokens(javaio);

		}
		else if(methodname.equals("read")&&numberOfArgs==3) {//java.io.read(char[] cbuf,int off,int len)

			taint(javaio);

			handleRead(javaio);

		}
		else if(methodname.equals("read")&&numberOfArgs==1) {//java.io.read(char[] cbuf)

			taint(javaio);	

			handleRead(javaio);


		}
		else if(methodname.equals("read")&&numberOfArgs==0) {//java.io.read()

			System.out.println("read: "+javaio.getFileLocation());

			taint(javaio);	

			handleReadSingleChar(javaio);

		}



	}


	public void addConsoleInsn(gov.nasa.jpf.jvm.bytecode.InvokeInstruction console) {

		String stringinsn = console.toString();
		//System.out.println("aqui: "+stringinsn);
		if(stringinsn.contains("readLine(")) {

			taint(console);
			handleReadTokens(console);

		}
		

		vm.getCurrentThread().skipInstruction(console.getNext());


	}



	public void addScannerInsn(gov.nasa.jpf.jvm.bytecode.InvokeInstruction scanner) {

		String stringinsn = scanner.toString();

		if(stringinsn.contains("init")){

			taint(scanner);
			vm.getCurrentThread().pop();

		}
		else if(stringinsn.contains("next(")) {

			taint(scanner);
			handleReadTokens(scanner);

		}
		else if(stringinsn.contains("nextInt(")) {

			taint(scanner);
			handleReadTokens(scanner);//handleReadInt(scanner);

		}
		else if(stringinsn.contains("nextLine(")) {

			taint(scanner);
			handleReadTokens(scanner);

		}
		else if(stringinsn.contains("close")){
			vm.getCurrentThread().pop();
		}
		//else
			//System.out.println("ups: scanner: "+scanner.getClass()+" ;; "+scanner.getFileLocation());
		
		
		vm.getCurrentThread().skipInstruction(scanner.getNext());

	}

	
	
	
	
	
	
	
	public void addSwingInsn(gov.nasa.jpf.jvm.bytecode.InvokeInstruction swing) {
		
		String stringinsn = swing.toString();
		
		if(stringinsn.contains("getText")){

			handleReadTokens(swing);
			taint(swing);
			
		}
		
		
	}
	
	
	
	
	
	
	
	




	public void taint(gov.nasa.jpf.jvm.bytecode.InvokeInstruction javaio) {

		Location l = new Location(javaio.getFileLocation());
		if(currentL==null||(currentL!=null && !currentL.l.equals(l)))
			currentL = new L(l);

		currentL.taint();

	}




	public void handleRead(gov.nasa.jpf.jvm.bytecode.InvokeInstruction invoke) {

		Object [] values = invoke.getArgumentValues(this.vm.getCurrentThread());
		//	System.out.println("addInputBuffer: "+invoke.getFileLocation());
		if(values.length>0){

			gov.nasa.jpf.jvm.DynamicElementInfo di = (gov.nasa.jpf.jvm.DynamicElementInfo)values[0];
			if(di.getType().equals("[C")){
				InputBuffer buf = new InputBuffer();
				buf.buffer = di.getArrayFields().asCharArray();
				ReSPAConfig.input = di.getArrayFields().asCharArray();
//				if(Core.singleInputSource){
					if(ReSPAConfig.inputBuffer==null)
						ReSPAConfig.inputBuffer=buf;
		//		}
	//			else
					ReSPAConfig.inputbuffers.add(buf);
			
			
			}
		}


	}

	public void handleReadTokens(gov.nasa.jpf.jvm.bytecode.InvokeInstruction invoke) {


		if(ReSPAConfig.inputbuffers.size()==0 || 
				!(ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1) instanceof InputTokens)){

			InputTokens il = new InputTokens();

			String inputString = String.valueOf(ReSPAConfig.input);
			Scanner scan = new Scanner(inputString);
			while(scan.hasNextLine())
				il.tokens.add(scan.nextLine());

			ReSPAConfig.inputbuffers.add(il);

			scan.close();

		}

	}


	public void handleReadSingleChar(gov.nasa.jpf.jvm.bytecode.InvokeInstruction invoke) {

		if(ReSPAConfig.inputbuffers.size()==0 || 
				!(ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1) instanceof InputChars)){

			InputChars il = new InputChars();
			ReSPAConfig.inputbuffers.add(il);

		}

	}

	public void handleReadInt(gov.nasa.jpf.jvm.bytecode.InvokeInstruction invoke) {

		if(ReSPAConfig.inputbuffers.size()==0 || 
				!(ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1) instanceof InputInt)){

			InputInt il = new InputInt();
			ReSPAConfig.inputbuffers.add(il);

		}

		if(ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1) instanceof InputInt){

			((InputInt)ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1)).buffer.add(1);//TODO: find a way to determine the input

		}

	}




	/**
	 * A field was loaded 
	 * 
	 * If the field is an input source: taint this location
	 * 
	 * If this location is tainted: mark String and char fields as symbolic; 
	 * 		mark char[], byte[] and StringBuffer as input locations;
	 * 
	 * This is not robust: Maybe some fields are not touched by the input locations. This
	 * happens frequently with int types so avoid doing this to integer fields. However this 
	 * should work well for String parameters 
	 * 
	 * @param field
	 */
	public void addFieldInsn(FieldInstruction field) {

		gov.nasa.jpf.jvm.FieldInfo fieldinfo = field.getFieldInfo();
		String type = fieldinfo.getType();

		Location l = new Location(field.getFileLocation());
		if(this.currentL==null || !this.currentL.l.equals(l))
			currentL = new L(l);




		if(this.inputSources.containsKey(field.getVariableId())) {

			//potential input sources are now input sources
			currentL.taint();

		}
		else if((type.contains("char[]") || 
				type.contains("byte[]")) ||
				type.contains("StringBuffer")){ ////////Potential input locations



			if(currentL.tainted){//input source
				addInputSource(field.getVariableId(), new InputSource(l,field.getVariableId(), type,field));

				if(!field.getVariableId().equals(fieldinfo.getFullName()))
					addInputSource(fieldinfo.getFullName(), new InputSource(l,fieldinfo.getFullName(), type,field));
			}
			else//potential input source
				this.currentL.is.add(new InputSource(l,field.getVariableId(), type,field));

			//TODO Other buffering types

		}
		else if(!type.contains("[]")) {

			//////////////////Potential symbolic fields
			
			if(type.contains("java.lang.String") ||type.contains("char")) { //TODO: other types /*||((!fieldinfo.isArrayField())&&type.contains("char"))*/

				if(currentL.tainted)//symbolic field
					addSymbVar(new SimpleField(type, field.getVariableId()));//this.symbolicFields.add(new SimpleField(type, field.getVariableId()));
				else//potential symbolic field
					this.currentL.sf.add(new SimpleField(type, field.getVariableId()));

			}
			else if(field.getSourceLine()!=null&&field.getSourceLine().trim().endsWith(".getText();")){//aparently fieldinfo.getType() does not work with awt
				
				if(currentL.tainted)//symbolic field
					addSymbVar(new SimpleField(type, field.getVariableId()));
				else//potential symbolic field
					this.currentL.sf.add(new SimpleField(type, field.getVariableId()));
				
			}

		}
		//else nothing


	}


	/**
	 * Local variable instruction
	 * 
	 * if an input source is loaded or stored: taint this location
	 * if this location is tainted: mark stored variables as symbolic
	 * 
	 * @param lvi
	 */
	public void addLviInsn(gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction lvi) {

		String type= lvi.getLocalVariableType();

		Location l = new Location(lvi.getFileLocation());
		if(this.currentL==null || !this.currentL.l.equals(l))
			currentL = new L(l);




		if(this.inputSources.containsKey(lvi.getVariableId())) {

			//potential input sources are now input sources
			currentL.taint();

		}
		else if((type.contains("char[]") || 
				type.contains("byte[]")) ||
				type.contains("StringBuffer")){ ////////Potential input locations


			if(currentL.tainted)//input source
				addInputSource(lvi.getVariableId(), new InputSource(l,lvi.getVariableId(), type,lvi));
			else//potential input source
				this.currentL.is.add(new InputSource(l,lvi.getVariableId(), type, lvi));

			//TODO Other buffering types

		}
		else if(!type.contains("[]")&&(lvi instanceof gov.nasa.jpf.jvm.bytecode.StoreInstruction)) {

			//////////////////Potential symbolic fields

			if(type.equals("?"))
				type = insufficientInfo(lvi);

			if(type.contains("java.lang.String") 
					/*||type.contains("char")
					||type.contains("int")*/) { //TODO: other types 

				if(currentL.tainted)//symbolic field
					addSymbVar(new SimpleField(type, lvi.getVariableId()));
				else//potential symbolic field
					this.currentL.sf.add(new SimpleField(type, lvi.getVariableId()));
			}
			else if(type.contains("int")) {

				if(currentL.tainted)//symbolic field
					addSymbVar(new SimpleField(type, lvi.getVariableId()));
				else//potential symbolic field
					this.currentL.sf.add(new SimpleField(type, lvi.getVariableId()));

			}

		}
		//else nothing


	}






	public void addCALoadInsn(gov.nasa.jpf.jvm.bytecode.CALOAD caload) {

		Instruction prev = caload;
		while(!(prev instanceof gov.nasa.jpf.jvm.bytecode.ALOAD)) {
			prev = prev.getPrev();
		}

		//TODO: IMPLEMENTAR A BLACKLIST
		//TODO: se ocorre um CALOAD deves fazer untaint. Significa que estamos a aceder a um campo do array e nao a todo ele
		//no entanto um CALOAD de uma fonte de input deve original a uma variavel simbolica nova

		Location l = new Location(prev.getFileLocation());
		if(l.equals(currentL.l)){
			//	System.out.println("CALOAD: "+ ((gov.nasa.jpf.jvm.bytecode.ALOAD)prev).getLocalVarInfo().toString()+" ;; "+prev.getFileLocation());		
			if(this.inputSources.containsKey(((gov.nasa.jpf.jvm.bytecode.ALOAD)prev).getVariableId())) {

				currentL.unTaint();
				SimpleField sf = new SimpleField("char",((gov.nasa.jpf.jvm.bytecode.ALOAD)prev).getVariableId());//review this
				addSymbVar(sf);

			}

		}

	}










	/**
	 * Return instruction
	 * 
	 * if this location is tainted: the return value should be symbolic
	 * 
	 * @param returninsn
	 */
	public void addReturnInsn(gov.nasa.jpf.jvm.bytecode.ReturnInstruction returninsn) {

		MethodInfo mi = returninsn.getMethodInfo();

		if(mi.getReturnTypeName().equals("java.lang.String")){

			Location l = new Location(returninsn.getFileLocation());
			if(this.currentL==null || !this.currentL.l.equals(l))
				currentL = new L(l);

			if(currentL.tainted){


				String id = returninsn.getMethodInfo().getFullName();
				id = id+"return("+this.currentL.l+")";
				addSymbVar(new SimpleField(mi.getReturnTypeName(), id));//this.symbolicFields.add(new SimpleField(mi.getReturnTypeName(),id));
				//	System.out.println("fuck: "+returninsn.getFileLocation());




			}


		}


	}



	/**
	 * Invoke instruction
	 * 
	 * if the invoked instruction contains input sources in its parameters: taint this location
	 * 
	 * if the location is tainted: mark the parameters as symbolic
	 * 
	 * This is not very robust: Maybe some parameters are not touched by the input locations. This
	 * happens frequently with int typeso avoid doing this to integer parameters. However this 
	 * should work well for String parameters
	 * 
	 * @param invoke
	 */
	public void addInvokeInsn(gov.nasa.jpf.jvm.bytecode.InvokeInstruction invoke) {

		try{

			Location l = new Location(invoke.getFileLocation());
			if(this.currentL==null || !this.currentL.l.equals(l))
				currentL = new L(l);

			MethodInfo method = invoke.getInvokedMethod();
			String [] types = method.getArgumentTypeNames();
			LocalVarInfo [] lvi = method.getArgumentLocalVars();
			String varId = "";
			storeInvokeInsn();

			int i=0;
			for(;i<method.getArgumentsSize();i++){

				if(types[i].equals("char[]")||types[i].equals("byte[]")||types[i].equals("java.lang.StringBuffer")){

					varId = varId+method.getFullName()+"."+lvi[i+1].getName();

					if(currentL.tainted){//input source
						addInputSource(varId, new InputSource(l,varId, types[i],invoke));
						handleRead(invoke);
					}

				}
				else if(!types[i].contains("[]")){
					if(types[i].equals("java.lang.String")||types[i].contains("char")) { //TODO: other types

						varId = varId+method.getFullName()+"."+lvi[i+1].getName();

						if(currentL.tainted)
							addSymbVar(new SimpleField(types[i],varId));//this.symbolicFields.add(new SimpleField(types[i],varId));

					}

				}
				varId="";
			}

		}
		catch(Exception e){
			//IT IS EASIER THIS WAY
		}

	}



	private class L {

		public Location l;

		public ArrayList<InputSource> is;

		public ArrayList<SimpleField> sf;

		public boolean tainted = false;


		public L(Location l) {
			this.l = l;
			is = new ArrayList<InputSource>();
			sf = new ArrayList<SimpleField>();
		}

		public void taint() {

			if(!ignoredLocations.contains(l)){

				tainted = true;
				for(InputSource i: is)
					addInputSource(i.variableId, i);
				
				for(SimpleField f: sf)
					addSymbVar(f);//symbolicFields.add(f);

				is.clear();
				sf.clear();

			}
		}

		public void unTaint() {

			tainted = false;
			is.clear();
			sf.clear();
		}

	}



	/**
	 * We need to know the type of the current insn 
	 * 
	 * Sometimes JPF does not provide enough information about 
	 *	 the current insn and gives a "?"
	 *
	 * The idea is to provide the type of the previous insn when
	 * 	 JPF gives us a "?"
	 * 
	 * <2do> This is likely not to work in some cases
	 * 
	 * @param insn
	 * @return
	 */
	private String insufficientInfo(gov.nasa.jpf.jvm.bytecode.Instruction insn) {

		gov.nasa.jpf.jvm.bytecode.Instruction previous = insn.getPrev();


		if(previous instanceof gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction){

			return ((gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction)previous).getLocalVariableType();

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.InvokeInstruction) {

			if(((gov.nasa.jpf.jvm.bytecode.InvokeInstruction)previous).getInvokedMethod()!=null)
				return ((gov.nasa.jpf.jvm.bytecode.InvokeInstruction)previous).getInvokedMethod().getReturnTypeName();

			if(previous.toString().contains("next("))
				return "java.lang.String";
			else if(previous.toString().contains("nextInt"))
				return "int";
			else
				return "java.lang.String";

			//TODO: improve this!

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.ICONST) {

			return "int";

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.LDC) {

			return ((gov.nasa.jpf.jvm.bytecode.LDC)previous).getType().name();

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.FieldInstruction) {

			return ((gov.nasa.jpf.jvm.bytecode.FieldInstruction)previous).getFieldInfo().getType();

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.ReturnInstruction) {

			return ((gov.nasa.jpf.jvm.bytecode.ReturnInstruction)previous).getMethodInfo().getReturnTypeName();

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.ARRAYLENGTH) {

			return "int";

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.CALOAD|| previous instanceof gov.nasa.jpf.jvm.bytecode.CASTORE) {

			return "char[]";

		}
		else if(previous instanceof gov.nasa.jpf.jvm.bytecode.IALOAD|| previous instanceof gov.nasa.jpf.jvm.bytecode.IASTORE) {

			return "int[]";

		}

		//	System.out.println("FAIL: Must add the following bytecode to the if clause: "+previous.getClass()+" ;; "+currentL.l);

		return "?";//TODO: whatever bytecodes are missing

	}









	private void addSymbVar(SimpleField sf) {

		//System.out.println("fdx: "+sf.getVariableId()+" ;; "+sf.getType());
		try{

			if(accepting) {

				if(sf.getType().equals("java.lang.String")&&ReSPAConfig.symbString) {



					SymbolicInputString sis = (SymbolicInputString)getSymbVarValue();
					InputVariable sis_ = ReSPAConfig.symbvars.get(sis.toString());
					
					if((sis_==null || sis.getLength()==-1)&&accepting) {

						String name;
						if(ReSPAConfig.singleInputSource)
							name = "buf["+sis.getStartIndex()+"-"+(sis.getLength()+sis.getStartIndex()-1)+"]";
						else
							name = "buf"+sis.getBuffer()+"["+sis.getStartIndex()+"-"+(sis.getLength()+sis.getStartIndex()-1)+"]";

						
						String symbname = BytecodeUtils.varName(name, VarType.STRING);
						StringSymbolic sym_v = new StringSymbolic(symbname);
						vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);

						sis.setSym(sym_v);
						ReSPAConfig.symbvars.put(sis.toString(),sis);
						if(SystemOut.print_new_symb)
							System.out.println("[ReSPA][InputDetection] --> New Symbolic Variable: "+sym_v+"; Value to anonymize: "+sis.getValueAsString()+" ; Location: "+currentL.l);


						ReSPAConfig.symbvars_.put(symbname, sis);//makes it easier

						InputBuffer ib = ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1);


						if(ib instanceof InputTokens)
							this.lastCreated = new SymbVarCreated(vm.getStateCount(), ((InputTokens)ib).lineCount,sis.toString(), symbname);
						else
							this.lastCreated = new SymbVarCreated(vm.getStateCount(), -1,sis.toString(), symbname);

						//	this.symbolicFields.add(sf);
					}
					else{
						vm.getCurrentThread().getTopFrame().setOperandAttr(((SymbolicInputString)sis_).getSym());
					}




				}
				else if(sf.getType().equals("char")) {

					ConcreteInputChar cic = (ConcreteInputChar)getSymbVarValue();
					InputVariable cic_ = ReSPAConfig.symbvars.get(cic.toString());
					if(cic_==null){


						ReSPAConfig.symbvars.put(cic.toString(),cic);
						//		this.symbolicFields.add(sf);
						//	System.out.println("Disclosed Char: buf"+cic.getBuffer()+"["+cic.getStartIndex()+"] = "+cic.getValueAsString()+" ;; "+Integer.valueOf(cic.getValue()[0]));

					}

				}
				else if(sf.getType().equals("int")&&ReSPAConfig.symbInt){
					//TODO: use a flag to determine if we want to use this functionality
					//for xerces this does not work well

					/*	ConcreteInputInteger	cii = (ConcreteInputInteger)getSymbVarValue();
					InputVariable cic_ = this.symbvars.get(cii.toString());
					if(cic_==null){
				this.symbvars.put(cii.toString(),cii);
				//		this.symbolicFields.add(sf);
			}*/

					SymbolicInputInt si = (SymbolicInputInt)getSymbVarValue();
					InputVariable si_ = ReSPAConfig.symbvars.get(si.toString());
					if(si_==null){

						String name;
						if(ReSPAConfig.singleInputSource)
							name = "buf["+si.getStartIndex()+"]";
						else
							name = "buf"+si.getBuffer()+"["+si.getStartIndex()+"]";
						
						String symbname = BytecodeUtils.varName(name, VarType.INT);
						SymbolicInteger sym_v = new SymbolicInteger(symbname);
						si.setSym(sym_v);
						vm.getCurrentThread().getTopFrame().pop();
						vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);
						
						if(SystemOut.print_new_symb)
							System.out.println("[ReSPA][InputDetection] --> New Symbolic Variable: "+sym_v+"; Value to anonymize: "+si.getValueAsInt()+"; Location: "+currentL.l);
						ReSPAConfig.symbvars_.put(symbname, si);
						ReSPAConfig.symbvars.put(si.toString(),si);

					}

				}

				ReSPAConfig.runningConcrete = false;

			}

		}
		catch(Exception e) {}

	}








	public void addInputSource(String id, InputSource in) {

		this.inputSources.put(id, in);

		//	this.mappingManager.newInputSource(id, in);

	}


	public InputVariable getSymbVarValue() {

		try{

			Instruction insn = vm.getLastInstruction();

			if(insn instanceof gov.nasa.jpf.jvm.bytecode.ARETURN) {

				//gov.nasa.jpf.jvm.DynamicElementInfo di = (gov.nasa.jpf.jvm.DynamicElementInfo)((gov.nasa.jpf.jvm.bytecode.ARETURN)insn).getReturnValue(this.vm.getCurrentThread());

				InvokedInsn invoked = null;
				for(int i=this.invoked.size()-1;i>=0;i--) {
					invoked = this.invoked.get(i);
					if(invoked.getLocation().equals(this.currentL.l))
						break;
				}


				InputBuffer buf = ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1);
				if(insn.toString().contains("readLine")) {
					//System.out.println("MAGIA: "+insn.getFileLocation());
					//					System.out.println("banhada: "+vm.getCurrentThread().getStackTrace());
					if(ReSPAConfig.inputbuffers.size()>0){

						InputBuffer ib = ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1);

						if(ib instanceof InputTokens) {

							InputTokens il = (InputTokens) ib;
							il.lineCount++;
							SymbolicInputString sis = new SymbolicInputString(il.lineCount,-1,ReSPAConfig.inputbuffers.size()-1);

							if(il.tokens.size()<=il.lineCount){
								sis.setValue("unknown for now");
								accepting = false; //Test this
							}
							else{
								sis.setValue(il.tokens.get(il.lineCount-1));
								sis.setLength(sis.getValue().length);
							}

							return sis;
						}

					}

				}
				else {//TODO: there is the method read(buf) that has only one arg
					SymbolicInputString sis = new SymbolicInputString(invoked.getOffset(), invoked.getLength(), 1/*Core.inputbuffers.size()-1*/);
					sis.setValue(new String(buf.buffer, sis.getOffset(), sis.getLength()));
					return sis;
				}




			}
			else if(insn instanceof gov.nasa.jpf.jvm.bytecode.InvokeInstruction) {
				System.out.println("ta dificil: "+insn.getFileLocation());
			}
			else if(insn instanceof gov.nasa.jpf.jvm.bytecode.CALOAD) {//for now we do not support this

				InputBuffer buf = ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1);
				int index = ((gov.nasa.jpf.jvm.bytecode.CALOAD)insn).getIndex(vm.getCurrentThread());
				ConcreteInputChar cic = new ConcreteInputChar(
						index,
						ReSPAConfig.inputbuffers.size()-1, 
						buf.buffer[index]);


				return cic;

			}
			else if(insn instanceof gov.nasa.jpf.jvm.bytecode.ASTORE) {
				//	System.out.println("BANHADA: "+insn.getClass()+" ;; "+insn.getFileLocation()+" ;; "+Core.inputbuffers.size());

				if(ReSPAConfig.inputbuffers.size()>0){

					InputBuffer ib = ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1);

					if(ib instanceof InputTokens) {

						InputTokens il = (InputTokens) ib;
						il.lineCount++;
						SymbolicInputString sis = new SymbolicInputString(il.lineCount,-1,ReSPAConfig.inputbuffers.size()-1);
						//sis.setValue("unknown for now");
						if(il.tokens.size()<il.lineCount){
							sis.setValue("unknown for now");
							accepting = false;
						}
						else{
							sis.setValue(il.tokens.get(il.lineCount-1));
							sis.setLength(sis.getValue().length);
						}

						return sis;
					}

				}
			}
			else if(insn instanceof gov.nasa.jpf.jvm.bytecode.ISTORE) {
				//	System.out.println("BANHADA: "+insn.getClass()+" ;; "+insn.getFileLocation()+" ;; "+Core.inputbuffers.size());

				if(ReSPAConfig.inputbuffers.size()>0){

					InputBuffer ic = ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1);

					if(ic instanceof InputChars) {

						InputChars il = (InputChars) ic;

						SymbolicInputInt sii = new SymbolicInputInt(il.buffer.size(),ReSPAConfig.inputbuffers.size()-1);
						sii.setValue(Integer.MIN_VALUE);

						return sii;
					}
					else if(ic instanceof InputInt) {

						InputInt ii = (InputInt)ic;
						SymbolicInputInt sii = new SymbolicInputInt(ii.buffer.size(),ReSPAConfig.inputbuffers.size()-1);
						sii.setValue(ii.buffer.get(ii.buffer.size()-1));

						return sii;

					}
					else if(ic instanceof InputTokens) {
						
						InputTokens il = (InputTokens) ic;
						il.lineCount++;
						SymbolicInputInt sii = new SymbolicInputInt(il.lineCount,0);
						//sis.setValue("unknown for now");
						if(il.tokens.size()<il.lineCount){
							sii.setValue(Integer.MIN_VALUE);
							accepting = false;
						}
						else{
							sii.setValue(Integer.valueOf(il.tokens.get(il.lineCount-1)));
							
						}

						return sii;
						
					}

				}
			}


		}
		catch(Exception e) {
		}
		return null;
	}






	public void storeInvokeInsn() {

		Instruction insn = vm.getLastInstruction();

		if(insn instanceof InvokeInstruction&&currentL.tainted) {

			InvokeInstruction invoke = (InvokeInstruction)insn;
			MethodInfo mi = invoke.getInvokedMethod();
			String name = mi.getFullName();
			if(name.equals("java.lang.String.<init>([CII)V")){

				int offset, length;
				Object [] values = invoke.getArgumentValues(this.vm.getCurrentThread());

				offset = (Integer)values[1];
				length = (Integer)values[2];

				InvokedInsn ii = new InvokedInsn();
				ii.setInsn(invoke);
				ii.setLength(length);
				ii.setOffset(offset);
				ii.setLocation(currentL.l);

				this.invoked.add(ii);

			}




		}

	}







	public char[] cleanBuffer(char [] buf) {

		HashSet <Integer> garbageChars = new HashSet<Integer>();
		for(int i=0;i<32;i++)
			garbageChars.add(new Integer(i));
		garbageChars.add(127);
		garbageChars.add(129);
		garbageChars.add(141);
		garbageChars.add(143);
		garbageChars.add(144);
		garbageChars.add(157);

		String clean="";
		for(int i=0;i<buf.length;i++)
			if(!garbageChars.contains(Integer.valueOf(buf[i])))
				clean = clean+buf[i];

		return clean.toCharArray();

	}




	public class SymbVarCreated {

		public int statecount;

		public int lineCount=-1;

		public String symbvar;

		public String symbvar_;

		public SymbVarCreated(int statecount,int lineCount, String symbvar, String symbvar_) {

			this.statecount = statecount;
			this.lineCount = lineCount;
			this.symbvar = symbvar;
			this.symbvar_ = symbvar_;

		}


		public void unCreate() {

			if(lineCount>=0){
				InputBuffer ib = ReSPAConfig.inputbuffers.get(ReSPAConfig.inputbuffers.size()-1);
				if(ib instanceof InputTokens)
					((InputTokens)ib).lineCount = lineCount-1;
			}

			ReSPAConfig.symbvars.remove(symbvar);
			ReSPAConfig.symbvars_.remove(symbvar_);

		}


	}







}
