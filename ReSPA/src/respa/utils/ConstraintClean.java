package respa.utils;

import respa.main.ReSPAConfig;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicCharAtInteger;
import gov.nasa.jpf.symbc.string.SymbolicIndexOfCharInteger;
import gov.nasa.jpf.symbc.string.SymbolicLengthInteger;

public class ConstraintClean {



	public static String clean(Constraint constraint) {

		//return "(" + trim(constraint.getLeft()) + constraint.getComparator().toString() + trim(constraint.getRight()) + ")";
		
		return "(" + cleanLength(cleanCharAt(cleanSolutionValue(constraint.getLeft()))) + constraint.getComparator().toString() + cleanLength(cleanCharAt(cleanSolutionValue(constraint.getRight()))) + ")";
		
		
		
	}

	public static String cleanSolutionValue(Expression constraint) {
		
		String c = constraint.toString();
		if(c.contains("SYMSTRING[")){
			return c.substring(0,c.lastIndexOf("SYMSTRING")+9);
		}
		else if(c.contains("SYMINT[")){
			return c.substring(0,c.lastIndexOf("SYMINT")+6);
		}

		return c;
	}
	

	/**
	 * Parses the constraint and removes the increment of the charAt. 
	 * 
	 * @param constraint
	 * @return
	 */
	public static String cleanCharAt(String constraint) {
		
		if(!constraint.contains("CharAt"))
			return constraint;
		
		
		String clean = constraint.substring(0, constraint.indexOf(")_")+2);
		clean = clean + constraint.substring(constraint.indexOf("buf"));
		return clean;
		
	}
	
	public static String cleanLength(String constraint) {
		
		if(!constraint.contains("Length"))
			return constraint;
		
		String clean = constraint.substring(0, 7);
		clean = clean + constraint.substring(constraint.indexOf("buf"));
		return clean;
		
	}
	
	
	
	
	public static String minus(String s,int index) {
		
		String news = s.substring(0,index);
		if(s.length()>(index+1))
			news = news+s.substring(index+1);
		return news;
	}

	public static String minus(String s,int startIndex,int endIndex) {
		
		String news = s.substring(0,startIndex);
		if(s.length()>(endIndex+1))
			news = news+s.substring(endIndex+1);
		return news;
	}


	public static String clean(StringConstraint constraint) {
		
		//return "(" + trim(constraint.getLeft()) + constraint.getComparator().toString() + trim(constraint.getRight()) + ")";
		return "(" + cleanLength(
				cleanCharAt(cleanSolutionValue(constraint.getLeft()))) + constraint.getComparator().toString() + cleanLength(cleanCharAt(cleanSolutionValue(constraint.getRight()))) + ")";
	
	}


	
	public static String trim(Expression stmt) {
		
		String trim = stmt.toString();
		
		if(!(stmt instanceof StringConstant || 
				stmt instanceof gov.nasa.jpf.symbc.numeric.IntegerConstant)) {
			return trim.split("_")[0];
		}
		
		
		
		
		return trim;
		
	}



	public static String clean(Expression stmt) {

		String clean = null;

		if(stmt instanceof StringConstant){
			clean = stmt.toString();
		}
		else if(stmt instanceof gov.nasa.jpf.symbc.numeric.IntegerConstant) {
			clean = stmt.toString();
		}
		else if(stmt instanceof StringSymbolic){

			StringSymbolic strsymb = (StringSymbolic)stmt; //dont want to cast all the time
			String tostring = strsymb.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);

			clean = substring;

		}
		else if(stmt instanceof SymbolicCharAtInteger) {

			SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt;
			String tostring = charat.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("["));

			clean = substring;

		}
		else if(stmt instanceof SymbolicCharAtInteger) {

			SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt;
			String tostring = charat.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("["));

			clean = substring;

		}
		else if(stmt instanceof SymbolicIndexOfCharInteger) {

			SymbolicIndexOfCharInteger length = (SymbolicIndexOfCharInteger)stmt; //dont want to cast all the time
			String tostring = length.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);//substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			clean = substring;

		}
		else if(stmt instanceof SymbolicLengthInteger){

			SymbolicLengthInteger length = (SymbolicLengthInteger)stmt; //dont want to cast all the time
			String tostring = length.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			clean = substring;

		}
		else if(stmt instanceof SymbolicCharAtInteger){

			SymbolicCharAtInteger charat = (SymbolicCharAtInteger)stmt; //dont want to cast all the time
			String tostring = charat.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			clean = substring;

		}
		else if(stmt instanceof gov.nasa.jpf.symbc.numeric.SymbolicInteger) {

			if(ReSPAConfig.automaticInputDetection) {

				SymbolicInteger symbint = (SymbolicInteger)stmt; //dont want to cast all the time
				String tostring = symbint.toString();
				String substring = tostring.substring(tostring.indexOf("buf"));
				if(substring.endsWith("]"))
					substring = substring.substring(0, substring.lastIndexOf("["));
				clean = substring;

			}
			else {

				SymbolicInteger strsymb = (SymbolicInteger)stmt; //dont want to cast all the time
				String tostring = strsymb.toString();
				String substring = tostring.substring(tostring.indexOf("buf"));
				substring = substring.substring(0, substring.lastIndexOf("SYMINT")+6);
				clean = substring;

			}


		}
		else if(stmt instanceof gov.nasa.jpf.symbc.numeric.IntegerExpression) {

			String tostring = stmt.toString();
			String substring = tostring.substring(tostring.indexOf("buf"));
			substring = substring.substring(0, substring.lastIndexOf("SYMSTRING")+9);
			clean = substring;

		}


		return clean;

	}











}
