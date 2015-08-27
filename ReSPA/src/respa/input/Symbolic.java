package respa.input;


import java.util.ArrayList;

import respa.main.ReSPAConfig;
import respa.output.SystemOut;
import respa.utils.InputLocation;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils.VarType;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringSymbolic;

public class Symbolic {

	
	public static int totalCreated = 0;
	public static int justCreated = 0;


	
	public static StringSymbolic currentStringSymbolic=null;
	
	public static ArrayList<StringSymbolic> stringsymbolicvars = new ArrayList<StringSymbolic>();
	
	public static void newSymbolicInt(JVM vm, InputLocation il) {
		
		String name = "buf"+"["+il.location.getLine()+"]";
		String symbname = BytecodeUtils.varName(name, VarType.INT);
		IntegerExpression sym_v = new SymbolicInteger(symbname);



		vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);

			System.out.println("[ReSPA][Symbolic] --> NEW SYMBC Int: "+name+" -> "+sym_v+" ; "+
					vm.getLastInstruction().getFileLocation()+" ; value to anonymize: "+il.value);

		SymbolicInputInt sii = new SymbolicInputInt(ReSPAConfig.symbvars.size(),0);
		InputVariable sii_ = ReSPAConfig.symbvars.get(sii.toString());

		if(sii_==null || sii.getLength()==-1) {

			sii.setSym((SymbolicInteger)sym_v);
			sii.setValue(Integer.valueOf(il.value));
			ReSPAConfig.symbvars.put(sii.toString(),sii);

		}


		ReSPAConfig.runningConcrete=false;
		ReSPAConfig.inputLocationsSet_.put(symbname, il);
		ReSPAConfig.alreadyCreated.add(il);
		ReSPAConfig.symbvars_.put(symbname, sii);//makes it easier
		Symbolic.totalCreated++;
		Symbolic.justCreated++;
		
	}
	
	
	public static void newSymbolicString(JVM vm, InputLocation il) {
	
		String name = "buf"+"["+il.location.getLine()+"]";
		String symbname = BytecodeUtils.varName(name, VarType.STRING);
		StringSymbolic sym_v = new StringSymbolic(symbname);


		
		


		vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);
		
		if(SystemOut.print_new_symb)
			System.out.println("[ReSPA][Symbolic] --> NEW SYMBC String: "+name+" -> "+sym_v+" ; "+
					vm.getLastInstruction().getFileLocation()+" ; value to anonymize: "+il.value);

		currentStringSymbolic = sym_v;
		
		SymbolicInputString sis = new SymbolicInputString(ReSPAConfig.symbvars.size(),il.value.length(),0);
		InputVariable sis_ = ReSPAConfig.symbvars.get(sis.toString());

		Symbolic.stringsymbolicvars.add(sym_v);
		
		if(sis_==null || sis.getLength()==-1) {

			sis.setSym(sym_v);
			sis.setValue(il.value);
			sis.setOffset(ReSPAConfig.symbvars.size());
			sis.setLength(il.value.length());
			ReSPAConfig.symbvars.put(sis.toString(),sis);
		}

		ReSPAConfig.runningConcrete=false;
		ReSPAConfig.inputLocationsSet_.put(symbname, il);
		ReSPAConfig.alreadyCreated.add(il);
		ReSPAConfig.symbvars_.put(symbname, sis);//makes it easier
		Symbolic.totalCreated++;
		Symbolic.justCreated++;
	}

	
	public static void newSymbolicBoolean(JVM vm, InputLocation il) {
		
		String name = "buf"+"["+il.location.getLine()+"]";
		String symbname = BytecodeUtils.varName(name, VarType.INT);
		IntegerExpression sym_v = new SymbolicInteger(symbname,0,1);

		vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);

		if(SystemOut.print_new_symb)
			System.out.println("[ReSPA][Symbolic] --> NEW SYMBC boolean: "+name+" -> "+sym_v+" ; "+
					vm.getLastInstruction().getFileLocation()+" ; value to anonymize: "+il.value);

		SymbolicInputInt sii = new SymbolicInputInt(ReSPAConfig.symbvars.size(),0);
		InputVariable sii_ = ReSPAConfig.symbvars.get(sii.toString());

		if(sii_==null || sii.getLength()==-1) {

			sii.setSym((SymbolicInteger)sym_v);
			sii.setValue(Integer.valueOf(il.value));
			ReSPAConfig.symbvars.put(sii.toString(),sii);

		}


		ReSPAConfig.runningConcrete=false;
		ReSPAConfig.inputLocationsSet_.put(symbname, il);
		ReSPAConfig.alreadyCreated.add(il);
		Symbolic.totalCreated++;
		Symbolic.justCreated++;
		
	}
	
}
