package respa.output.solve;

import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringComparator;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicCharAtInteger;
import gov.nasa.jpf.symbc.string.SymbolicIndexOfCharInteger;
import gov.nasa.jpf.symbc.string.SymbolicLengthInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import respa.input.InputVariable;
import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
import respa.input.SymbolicInputVariable;
import respa.leak.LeakyPath;
import respa.leak.numeric.integer.MeasureSingleInteger;
import respa.leak.string.MeasureSingleString;
import respa.main.ReSPAConfig;
import respa.output.Output;


/**
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 * 
 * Solve the path condition obtained by our search, extract
 * new input and build an output
 *
 */
@SuppressWarnings({"deprecation","unused"})
public abstract class Solver {





	private LeakyPath leakypath;


	@Deprecated
	public HashMap<String, InputVariable> solve() {


		if(ReSPAConfig.currentPathCondition!=null)//if ReSPA outputs an alternative path
			return solve(ReSPAConfig.currentPathCondition);
		else if(ReSPAConfig.previousPathCondition!=null)//if not do it with the original path
			return solve(ReSPAConfig.previousPathCondition);

		return null;//nothing will be anonymized

	}





	/**
	 * This is how we solve our path condition and get new input.
	 * The general idea is to iterate over the constraints of the
	 * path condition and extract the value of the solution field.
	 * The values of the solution field were obtained by the 
	 * solver specified in ReSPA.jpf.
	 * 
	 * @param pc The path condition
	 * @return	a HashMap containing the new input values
	 */
	public HashMap<String, InputVariable> solve(PathCondition pc) {

		HashMap<String, InputVariable> symbvarsByName = new HashMap<String, InputVariable>();
		HashSet<String> varsInPC = new HashSet<String>();

		leakypath = new LeakyPath();

		if(pc!=null){

			if(!PathCondition.flagSolved){
				pc.simplify();
				pc.solve();
				if(pc.spc!=null){
					pc.spc.simplify();
					pc.spc.solve();
				}
			}

			ArrayList <String> stringsymbvar = new ArrayList<String>();
			ArrayList <String> intsymbvar = new ArrayList<String>();

			fillWithSymbvars(stringsymbvar, intsymbvar, symbvarsByName);

			Constraint dummy;
			
			for(String sisname: stringsymbvar){


				//Numeric operations on a symbolic string

				if(pc.header!=null && 
						!ReSPAConfig.ignoreNumeric) {
					dummy = pc.header;

					while(dummy!=null){

						if(dummy.getLeft().toString().contains(sisname)||
								dummy.getRight().toString().contains(sisname)) {
							
							SymbolicInputString sis = (SymbolicInputString)symbvarsByName.get(sisname);
							getNumericSolution(dummy,sis);
							symbvarsByName.put(sisname, sis);

							if(ReSPAConfig.measure_leak){
								leakypath.add(dummy,sis);
							//	handleLeakMeasurement(sis, dummy);
							}

							varsInPC.add(sisname);

						}

						dummy = dummy.and;

					}
					dummy = pc.header;
				}


				//string operatations on a symbolic string
				if(pc.spc.header!=null && 
						!ReSPAConfig.ignoreString && !ReSPAConfig.ignoreStringPC){
					StringConstraint dummyS = pc.spc.header;


					while(dummyS!=null){

						if(dummyS.getLeft().toString().contains(sisname)||
								dummyS.getRight().toString().contains(sisname)) {

							SymbolicInputString sis = (SymbolicInputString)symbvarsByName.get(sisname);
							sis.solution = getStringSolution(dummyS).toCharArray();
							symbvarsByName.put(sisname, sis);

							if(ReSPAConfig.measure_leak){
							//	handleLeakMeasurement(sis, dummyS);
								leakypath.add(dummyS, sis);
							}
							varsInPC.add(sisname);

						}

						dummyS = dummyS.and();

					}
					dummyS = pc.spc.header;
				}





			}//end for



			for(String sisname: intsymbvar){

				//numeric operations on an integer
				if(pc.header!=null &&
						!ReSPAConfig.ignoreNumeric) {

					dummy = pc.header;

					while(dummy!=null){

						if((dummy.getLeft()!=null&&dummy.getLeft().toString().contains(sisname))||
								(dummy.getRight()!=null&&dummy.getRight().toString().contains(sisname))) {

							SymbolicInputInt sii = (SymbolicInputInt)symbvarsByName.get(sisname);
							sii.setSolution(getNumericSolution(dummy));
							symbvarsByName.put(sisname, sii);

							if(ReSPAConfig.measure_leak){
								//handleLeakMeasurement(sii, dummy);
								leakypath.add(dummy, sii);
							}
							varsInPC.add(sisname);

						}

						dummy = dummy.and;

					}
					dummy = pc.header;
				}




			}//end for




			//return null;
		}
		else if(ReSPAConfig.symbvars.values().size()>0){//No path condition

			symbvarsByName = ReSPAConfig.symbvars;

		}

		ReSPAConfig.delimiters=varsInPC.size();

		return symbvarsByName;


	}








	private void fillWithSymbvars(ArrayList <String> stringsymbvar,ArrayList <String> intsymbvar,HashMap<String, InputVariable> symbvarsByName) {



		ArrayList<InputVariable> symbvars = new ArrayList<InputVariable>(ReSPAConfig.symbvars.values());

		String dummyName;
		for(InputVariable iv: symbvars) {
			if(iv instanceof SymbolicInputString) {
				dummyName = ((SymbolicInputString)iv).getSym().toString();
				dummyName = dummyName.substring(0,dummyName.indexOf("SYMSTRING"));
				dummyName = dummyName.substring(dummyName.indexOf("buf"));//dummyName = dummyName.split("_")[0];
				symbvarsByName.put(dummyName,(SymbolicInputString)iv);
				stringsymbvar.add(dummyName);
			}
			else if(iv instanceof SymbolicInputInt) {
				dummyName = ((SymbolicInputInt)iv).getSym().toString();
				dummyName = dummyName.substring(0,dummyName.indexOf("SYMINT"));
				dummyName = dummyName.split("_")[0];
				symbvarsByName.put(dummyName,(SymbolicInputInt)iv);
				intsymbvar.add(dummyName);
			}
		}


	}













	public LeakyPath getLeakyPath() {

		return leakypath;

	}
















	public abstract Output construct(HashMap<String, InputVariable> input);



























	//For numeric constraints only
	protected int getNumericSolution(Constraint constraint) {




		if(constraint.getLeft() instanceof SymbolicInteger)
			return ((SymbolicInteger) constraint.getLeft()).solution;
		else if(constraint.getRight() instanceof SymbolicInteger)
			return ((SymbolicInteger) constraint.getRight()).solution;


		return Integer.MIN_VALUE;

	}

















	//for string constraints only
	private void getNumericSolution(Constraint constraint,SymbolicInputString sis) {



		if(sis.solution==null){
			sis.solution = new char[sis.getValue().length];
			//sis.solution[0]='x';
		}

		Expression stmt,constant;

		boolean stmtCompare = false;//in some rare cases constant may be a stmt

		//TODO checkout the method isConstant()
		if(constraint.getLeft() instanceof IntegerConstant &&
				constraint.getRight() instanceof IntegerConstant) {
			constant = constraint.getRight();
			stmt = constraint.getLeft();
		}
		else if(constraint.getLeft() instanceof IntegerConstant){
			constant = constraint.getLeft();
			stmt = constraint.getRight();
		}
		else if(constraint.getRight() instanceof IntegerConstant) {
			constant = constraint.getRight();
			stmt = constraint.getLeft();
		}
		else {
			stmtCompare = true;
			constant = constraint.getLeft();//in this case constant is a stmt
			stmt = constraint.getRight();
		}

		if(!stmtCompare) {//most cases

			if(stmt instanceof SymbolicCharAtInteger){
				try{
					SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt; //dont want to cast all the time
					sis.solution[charat.index.solution()]=(char)charat.solution;
					//System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII "+String.valueOf(sis.solution));
				}
				catch(Exception e) {}
			}
			else if(stmt instanceof SymbolicLengthInteger){


				SymbolicLengthInteger length = (SymbolicLengthInteger)stmt; //dont want to cast all the time
				char [] dummy = sis.solution;
				if(length.solution>0){
					sis.solution = new char[length.solution];
					for(int i=0;i<dummy.length&&i<length.solution;i++)
						sis.solution[i]=dummy[i];
				}
			}
			else if(stmt instanceof SymbolicIndexOfCharInteger) {

				SymbolicIndexOfCharInteger indexof = (SymbolicIndexOfCharInteger)stmt; //dont want to cast all the time
				sis.solution[indexof.solution] = (char)((IntegerConstant)constant).value;			


			}
			else if(stmt instanceof BinaryLinearIntegerExpression) {

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
					String substring = tostring.substring(tostring.indexOf("buf"));
					substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
					InputVariable iv = Core.symbvars_.get(substring);
					char [] value = iv.getValue();

					if(binexpression instanceof  SymbolicCharAtInteger){
						SymbolicCharAtInteger charat = (SymbolicCharAtInteger)binexpression;




					}
					else if(binexpression instanceof SymbolicLengthInteger){



					}*/



			}
			else if(stmt instanceof gov.nasa.jpf.symbc.numeric.SymbolicInteger) {

			//	SymbolicInteger symbint = (SymbolicInteger)stmt; //dont want to cast all the time

			}
			else if(stmt instanceof gov.nasa.jpf.symbc.numeric.IntegerConstant) {

			//	gov.nasa.jpf.symbc.numeric.IntegerConstant stmtconst = (gov.nasa.jpf.symbc.numeric.IntegerConstant)stmt;

			}


		}
		else {

			if(stmt instanceof SymbolicCharAtInteger && constant instanceof SymbolicCharAtInteger){

				/*				SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt;
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

						value1 = iv1.getValue();//TODO do this a different way
						value2 = iv2.getValue();//TODO do this a different way


					}
				 */
			}



		}

		//TODO: find a solution for this. Ask the solver for a new solution
		if(sis.solution.length==1&&sis.solution[0]==' ')
			sis.solution[0]='x';






	}


















	
	protected String getStringSolution(StringConstraint constraint) {







		Expression stmt,constant;
		StringComparator comparator = constraint.getComparator();
		boolean stmtCompare = false;//in some rare cases constant may be a stmt
		boolean unSwitchedPos = false;

		if(comparator.toString().equals(" contains ")) {
			constant = constraint.getRight();
			stmt = constraint.getLeft();
			unSwitchedPos = true;
		}
		else if(constraint.getLeft() instanceof StringConstant){
			constant = constraint.getLeft();
			stmt = constraint.getRight();
		}
		else if(constraint.getRight() instanceof StringConstant) {
			constant = constraint.getRight();
			stmt = constraint.getLeft();
		}
		else {
			stmtCompare = true;
			constant = constraint.getLeft();//in this case constant is a stmt
			stmt = constraint.getRight();
		}



		if(!stmtCompare) {//most cases

			if(stmt instanceof StringSymbolic){


				StringSymbolic strsymb = (StringSymbolic)stmt; //dont want to cast all the time
				return strsymb.solution;


			}

		}








		return "";
	}
















	private ArrayList<SymbolicInputVariable> symbolicVariables = new ArrayList<SymbolicInputVariable>();

	public ArrayList<SymbolicInputVariable> getSymbolicVariables() {

		return this.symbolicVariables;

	}

	private void handleLeakMeasurement(SymbolicInputString sis, StringConstraint sc) {


		if(sis.leakMeasure==null)
			sis.leakMeasure = new MeasureSingleString(String.valueOf(sis.getValue()));

		if(sc.getComparator().toString().equals(" equals ")){
			sis.leakMeasure.applyEquals(String.valueOf(sis.solution));
		}
		else if(sc.getComparator().toString().equals(" notequals ")){

			if(sc.getLeft() instanceof StringConstant)
				sis.leakMeasure.applyNotEquals(String.valueOf(((StringConstant)sc.getLeft()).value));
			else if(sc.getRight() instanceof StringConstant)
				sis.leakMeasure.applyNotEquals(String.valueOf(((StringConstant)sc.getRight()).value));

		}
		else if(sc.getComparator().toString().equals(" startswith ")) {

			if(sc.getLeft() instanceof StringConstant)
				sis.leakMeasure.applyStartswith(String.valueOf(((StringConstant)sc.getLeft()).value));
			else if(sc.getRight() instanceof StringConstant)
				sis.leakMeasure.applyStartswith(String.valueOf(((StringConstant)sc.getRight()).value));

		}
		else if(sc.getComparator().toString().equals(" notstartswith ")) {

			if(sc.getLeft() instanceof StringConstant)
				sis.leakMeasure.applyNotStartswith(String.valueOf(((StringConstant)sc.getLeft()).value));
			else if(sc.getRight() instanceof StringConstant)
				sis.leakMeasure.applyNotStartswith(String.valueOf(((StringConstant)sc.getRight()).value));

		}

		//TODO: other operations

	}



	private void handleLeakMeasurement(SymbolicInputInt sii, Constraint c) {


		if(sii.leakMeasure==null)
			sii.leakMeasure = new MeasureSingleInteger(sii.getValueAsInt());

		if(c.getComparator().toString().equals(" == ")){
			sii.leakMeasure.applyEquals(sii.getSolution());
		}
		else if(c.getComparator().toString().equals(" != ")){
			//sii.leakMeasure.applyNotEquals(sii.getSolution());
		}
		else if(c.getComparator().toString().equals(" >= ")){
			sii.leakMeasure.applyGreaterThan(sii.getSolution());
		}
		else if(c.getComparator().toString().equals(" > ")){
			sii.leakMeasure.applyGreaterThan(sii.getSolution()+1);
		}
		else if(c.getComparator().toString().equals(" <= ")){
			sii.leakMeasure.applyLowerThan(sii.getSolution());
		}
		else if(c.getComparator().toString().equals(" < ")){
			sii.leakMeasure.applyLowerThan(sii.getSolution()-1);
		}





		//TODO: other operations

	}



	private void handleLeakMeasurement(SymbolicInputString sis, Constraint constraint) {


		if(sis.leakMeasure==null)
			sis.leakMeasure = new MeasureSingleString(String.valueOf(sis.getValue()));







		if(sis.solution==null){
			sis.solution = new char[sis.getValue().length];
		}

		Expression stmt,constant;

		boolean stmtCompare = false;//in some rare cases constant may be a stmt

		//TODO checkout the method isConstant()
		if(constraint.getLeft() instanceof IntegerConstant &&
				constraint.getRight() instanceof IntegerConstant) {
			constant = constraint.getRight();
			stmt = constraint.getLeft();
		}
		else if(constraint.getLeft() instanceof IntegerConstant){
			constant = constraint.getLeft();
			stmt = constraint.getRight();
		}
		else if(constraint.getRight() instanceof IntegerConstant) {
			constant = constraint.getRight();
			stmt = constraint.getLeft();
		}
		else {
			stmtCompare = true;
			constant = constraint.getLeft();//in this case constant is a stmt
			stmt = constraint.getRight();
		}

		if(!stmtCompare) {//most cases

			if(stmt instanceof SymbolicCharAtInteger){

				SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt; //dont want to cast all the time
				sis.leakMeasure.applyEquals((char)charat.solution, charat.index.solution());

			}
			else if(stmt instanceof SymbolicLengthInteger){


				//SymbolicLengthInteger length = (SymbolicLengthInteger)stmt; //dont want to cast all the time
				//who cares
			}
			else if(stmt instanceof SymbolicIndexOfCharInteger) {

				//		SymbolicIndexOfCharInteger indexof = (SymbolicIndexOfCharInteger)stmt; //dont want to cast all the time
				//			sis.leakMeasure.applyEquals(indexof.solution,indexof.)

			}
			else if(stmt instanceof BinaryLinearIntegerExpression) {




			}
			else if(stmt instanceof gov.nasa.jpf.symbc.numeric.SymbolicInteger) {

				//				SymbolicInteger symbint = (SymbolicInteger)stmt; //dont want to cast all the time

			}
			else if(stmt instanceof gov.nasa.jpf.symbc.numeric.IntegerConstant) {

				//			gov.nasa.jpf.symbc.numeric.IntegerConstant stmtconst = (gov.nasa.jpf.symbc.numeric.IntegerConstant)stmt;

			}


		}







	}








}
