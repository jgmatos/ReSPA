package respa.leak;

//import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.SymbolicCharAtInteger;
import gov.nasa.jpf.symbc.string.SymbolicIndexOfCharInteger;
import gov.nasa.jpf.symbc.string.SymbolicLengthInteger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
import respa.input.SymbolicInputVariable;
import respa.leak.numeric.integer.LeakyInteger;
import respa.leak.string.FactorizedAlpha;
import respa.leak.string.FactorizedAlpha1_256;
import respa.leak.string.LeakyString;

public class LeakyPath extends LeakyVariable{



	private HashMap<SymbolicInputVariable, LeakyVariable> map;

	private double lastCalculatedLeak = 0.0;


	public LeakyPath() {

		map = new HashMap<SymbolicInputVariable, LeakyVariable>();

	}







	public void add(SymbolicInputVariable siv) {

		if(siv instanceof SymbolicInputString){

			SymbolicInputString sis = (SymbolicInputString) siv;
			LeakyString ls = new LeakyString(sis.getValue().length);
			map.put(siv, ls);

		}
		else if(siv instanceof SymbolicInputVariable) {
			LeakyInteger li = new LeakyInteger();
			map.put(siv, li);
		}


	}


	public void add(StringConstraint sc, SymbolicInputString sis) {

		handleLeakMeasurement(sis, sc);

	}


	public void add(Constraint c,SymbolicInputString sis) {

		handleLeakMeasurement(sis, c);

	}



	public void add(Constraint c,SymbolicInputInt sii) {

		handleLeakMeasurement(sii, c);

	}





	public double getAlphaValue() {

		double alphavalue = 1.0;

		for(LeakyVariable lv: map.values())
			alphavalue *= lv.getAlphaValue();

		return alphavalue;

	}


	public BigDecimal getPreciseAlphaValue() {

		BigDecimal alpha = new BigDecimal("1.0");

		for(LeakyVariable lv: map.values())
			alpha = alpha.multiply(lv.getPreciseAlphaValue());

		return alpha;

	}


	public double getLeakage() {

		return LeakUtils.bitsLeaked(getAlphaValue());

	}



	public double getPreciseLeakage() {

		return LeakUtils.bitsLeaked(getPreciseAlphaValue().doubleValue());

	}










	private void handleLeakMeasurement(SymbolicInputString sis, StringConstraint sc) {

		LeakyString ls = (LeakyString)map.get(sis);

		if(ls==null)
			ls = new LeakyString(sis.getValue().length);

		if(sc.getComparator().toString().equals(" equals ")){

			if(sc.getLeft() instanceof StringConstant)
				ls.applyEquals(String.valueOf(((StringConstant)sc.getLeft()).value));
			else if(sc.getRight() instanceof StringConstant)
				ls.applyEquals(String.valueOf(((StringConstant)sc.getRight()).value));

		}
		else if(sc.getComparator().toString().equals(" notequals ")){

			if(sc.getLeft() instanceof StringConstant)
				ls.applyNotEquals(String.valueOf(((StringConstant)sc.getLeft()).value));
			else if(sc.getRight() instanceof StringConstant)
				ls.applyNotEquals(String.valueOf(((StringConstant)sc.getRight()).value));

		}
		else if(sc.getComparator().toString().equals(" startswith ")) {

			if(sc.getLeft() instanceof StringConstant)
				ls.applyStartsWith(String.valueOf(((StringConstant)sc.getLeft()).value));
			else if(sc.getRight() instanceof StringConstant)
				ls.applyStartsWith(String.valueOf(((StringConstant)sc.getRight()).value));


		}
		else if(sc.getComparator().toString().equals(" notstartswith ")) {

			if(sc.getLeft() instanceof StringConstant)
				ls.applyNotStartsWith(String.valueOf(((StringConstant)sc.getLeft()).value));
			else if(sc.getRight() instanceof StringConstant)
				ls.applyNotStartsWith(String.valueOf(((StringConstant)sc.getRight()).value));


		}
		else
			System.out.println("banhada");

		//TODO: other operations


		map.put(sis, ls);

	}



	private void handleLeakMeasurement(SymbolicInputString sis, Constraint constraint) {


		LeakyString ls = (LeakyString)map.get(sis);

		if(ls==null)
			ls = new LeakyString(sis.getValue().length);


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
				if(constraint.getComparator().toString().equals(" == "))
					ls.applyEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
				else if(constraint.getComparator().toString().equals(" != "))
					ls.applyNotEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
				else if(constraint.getComparator().toString().equals(" > "))
					ls.applyGreaterCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
				else if(constraint.getComparator().toString().equals(" >= "))
					ls.applyGreaterEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
				else if(constraint.getComparator().toString().equals(" < "))
					ls.applyLowerCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);
				else if(constraint.getComparator().toString().equals(" <= "))
					ls.applyLowerEqualsCharAt((char)((IntegerConstant)constant).value, ((IntegerConstant)charat.index).value);

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



		map.put(sis, ls);



	}








	private void handleLeakMeasurement(SymbolicInputInt sii, Constraint c) {

		LeakyInteger li = (LeakyInteger)map.get(sii);

		if(li==null)
			li = new LeakyInteger();




		if(c.getComparator().toString().equals(" == ")){
			if(c.getLeft() instanceof IntegerConstant)
				li.applyEquals(((IntegerConstant)c.getLeft()).value);
			else if(c.getRight() instanceof IntegerConstant)
				li.applyEquals(((IntegerConstant)c.getRight()).value);
		}
		else if(c.getComparator().toString().equals(" != ")){
			if(c.getLeft() instanceof IntegerConstant)
				li.applyNotEquals(((IntegerConstant)c.getLeft()).value);
			else if(c.getRight() instanceof IntegerConstant)
				li.applyNotEquals(((IntegerConstant)c.getRight()).value);
		}
		else if(c.getComparator().toString().equals(" >= ")){
			if(c.getLeft() instanceof IntegerConstant)
				li.applyGreaterEquals(((IntegerConstant)c.getLeft()).value);
			else if(c.getRight() instanceof IntegerConstant)
				li.applyGreaterEquals(((IntegerConstant)c.getRight()).value);
		}
		else if(c.getComparator().toString().equals(" > ")){
			if(c.getLeft() instanceof IntegerConstant)
				li.applyGreaterThan(((IntegerConstant)c.getLeft()).value);
			else if(c.getRight() instanceof IntegerConstant)
				li.applyGreaterThan(((IntegerConstant)c.getRight()).value);
		}
		else if(c.getComparator().toString().equals(" <= ")){
			if(c.getLeft() instanceof IntegerConstant)
				li.applyLowerEquals(((IntegerConstant)c.getLeft()).value);
			else if(c.getRight() instanceof IntegerConstant)
				li.applyLowerEquals(((IntegerConstant)c.getRight()).value);
		}
		else if(c.getComparator().toString().equals(" < ")){
			if(c.getLeft() instanceof IntegerConstant)
				li.applyLowerThan(((IntegerConstant)c.getLeft()).value);
			else if(c.getRight() instanceof IntegerConstant)
				li.applyLowerThan(((IntegerConstant)c.getRight()).value);
		}


		map.put(sii, li);


		//TODO: other operations

	}







	@Override
	public FactorizedAlpha1_256 getFactorizedAlpha() {

		FactorizedAlpha1_256 fa = new FactorizedAlpha1_256(0, 1.0);

		for(LeakyVariable lv: map.values())
			fa.multiply(lv.getFactorizedAlpha());

		return fa;

	}

	public FactorizedAlpha getFactorizedAlpha_(Double commonFactor) {

		FactorizedAlpha fa = new FactorizedAlpha(0, 1.0, commonFactor);

		for(LeakyVariable lv: map.values())
			if(lv instanceof LeakyString){
				fa.multiply(((LeakyString)lv).getFactorizedAlpha_(commonFactor));
			}	

		return fa;

	}


	public double getFactorizedLeakage() {

		FactorizedAlpha1_256 fa = getFactorizedAlpha();

		double leak = fa.getExponent()*LeakUtils.log2(FactorizedAlpha1_256.getBeta());

		leak += LeakUtils.log2(fa.getRest());

		return LeakUtils.invertSignal(leak);

	}
	


	
	
	public double getLogAlpha() {

		double logalpha = 0.0;

		for(LeakyVariable lv: map.values())
			logalpha += lv.getLogAlpha();


		return logalpha;

	}

	public Collection<LeakyVariable> getLeakyVars() {

		return map.values();

	}


	
	
	
	
	
	
	
	
	public double getFastLeak() {
		
		lastCalculatedLeak = 0.0;
		
		for(LeakyVariable lv: getLeakyVars())
			lastCalculatedLeak += lv.getFastLeak();
		
		return lastCalculatedLeak;
		
		
	}







	@Override
	public String id() {
		return null;
	}







	@Override
	public Object getRandomSolution() {
		// TODO Auto-generated method stub
		return null;
	}







	@Override
	public double lastCalculatedLeak() {
		return lastCalculatedLeak;
	}







	@Override
	public Object makeCopy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	



}
