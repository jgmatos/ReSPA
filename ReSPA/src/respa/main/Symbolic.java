package respa.main;


import java.util.ArrayList;

import respa.cost.CostlyNumber;
import respa.cost.CostlyString;
import respa.cost.CostlyVar;
import respa.input.InputVariable;
import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
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

	public static ArrayList<CostlyVar> cvBag = new ArrayList<CostlyVar>();

	
	public static StringSymbolic currentStringSymbolic=null;
	
	public static ArrayList<StringSymbolic> stringsymbolicvars = new ArrayList<StringSymbolic>();
	
	public static void newSymbolicInt(JVM vm, InputLocation il) {
		
		String name = "buf"+"["+il.location.getLine()+"]";
		String symbname = BytecodeUtils.varName(name, VarType.INT);
		IntegerExpression sym_v = new SymbolicInteger(symbname);



		vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);

		if(SystemOut.print_new_symb)
			System.out.println("[REAP][Symbolic] --> NEW SYMBC Int: "+name+" -> "+sym_v+" ; "+
					vm.getLastInstruction().getFileLocation()+" ; value to anonymize: "+il.value);

		SymbolicInputInt sii = new SymbolicInputInt(Core.symbvars.size(),0);
		InputVariable sii_ = Core.symbvars.get(sii.toString());

		if(sii_==null || sii.getLength()==-1) {

			sii.setSym((SymbolicInteger)sym_v);
			sii.setValue(Integer.valueOf(il.value));
			Core.symbvars.put(sii.toString(),sii);

		}


		Core.runningConcrete=false;
		Core.inputLocationsSet_.put(symbname, il);
		Core.alreadyCreated.add(il);
		Core.symbvars_.put(symbname, sii);//makes it easier
		Symbolic.totalCreated++;
		Symbolic.justCreated++;
		cvBag.add(new CostlyNumber(symbname, 1));
		
	}
	
	
	public static void newSymbolicString(JVM vm, InputLocation il) {
	
		String name = "buf"+"["+il.location.getLine()+"]";
		String symbname = BytecodeUtils.varName(name, VarType.STRING);
		StringSymbolic sym_v = new StringSymbolic(symbname);


		
		


		vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);
		
		if(SystemOut.print_new_symb)
			System.out.println("[REAP][Symbolic] --> NEW SYMBC String: "+name+" -> "+sym_v+" ; "+
					vm.getLastInstruction().getFileLocation()+" ; value to anonymize: "+il.value);

		currentStringSymbolic = sym_v;
		
		SymbolicInputString sis = new SymbolicInputString(Core.symbvars.size(),il.value.length(),0);
		InputVariable sis_ = Core.symbvars.get(sis.toString());

		Symbolic.stringsymbolicvars.add(sym_v);
		
		if(sis_==null || sis.getLength()==-1) {

			sis.setSym(sym_v);
			sis.setValue(il.value);
			sis.setOffset(Core.symbvars.size());
			sis.setLength(il.value.length());
			Core.symbvars.put(sis.toString(),sis);
		}

		Core.runningConcrete=false;
		Core.inputLocationsSet_.put(symbname, il);
		Core.alreadyCreated.add(il);
		Core.symbvars_.put(symbname, sis);//makes it easier
		Symbolic.totalCreated++;
		Symbolic.justCreated++;
		CostlyString cs = new CostlyString(symbname, 0);
		cs.setSymb(sym_v);
		cvBag.add(cs);
	}

	
	public static void newSymbolicBoolean(JVM vm, InputLocation il) {
		
		String name = "buf"+"["+il.location.getLine()+"]";
		String symbname = BytecodeUtils.varName(name, VarType.INT);
		IntegerExpression sym_v = new SymbolicInteger(symbname,0,1);

		vm.getCurrentThread().getTopFrame().setOperandAttr(sym_v);

		if(SystemOut.print_new_symb)
			System.out.println("[REAP][Symbolic] --> NEW SYMBC boolean: "+name+" -> "+sym_v+" ; "+
					vm.getLastInstruction().getFileLocation()+" ; value to anonymize: "+il.value);

		SymbolicInputInt sii = new SymbolicInputInt(Core.symbvars.size(),0);
		InputVariable sii_ = Core.symbvars.get(sii.toString());

		if(sii_==null || sii.getLength()==-1) {

			sii.setSym((SymbolicInteger)sym_v);
			sii.setValue(Integer.valueOf(il.value));
			Core.symbvars.put(sii.toString(),sii);

		}


		Core.runningConcrete=false;
		Core.inputLocationsSet_.put(symbname, il);
		Core.alreadyCreated.add(il);
		Symbolic.totalCreated++;
		Symbolic.justCreated++;
		
	}
	
}
