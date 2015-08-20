package respa.utils;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.string.StringComparator;

public class Cost {

	
	
	public static boolean binaryCostString(StringComparator comparator, String constant) {
		
		if(comparator.toString().equals(" equals ")) {

			return false;

		}
		else if(comparator.toString().equals(" notequals ")) {

			return true;

		}
		else if(comparator.toString().equals(" == ")) {

			return false;

		}
		else if(comparator.toString().equals(" != ")) {

			return true;

		}
		else if(comparator.toString().equals(" equalsignorecase ")) {

			return false;

		}
		else if(comparator.toString().equals(" notequalsignorecase ")) {

			return true;

		}
		else if(comparator.toString().equals(" startswith ")) {

			return false;

		}
		else if(comparator.toString().equals(" notstartswith ")) {

			return true;

		}
		else if(comparator.toString().equals(" endswith ")) {

			return false;

		}
		else if(comparator.toString().equals(" notendswith ")) {

			return true;

		}
		else if(comparator.toString().equals(" contains ")) {

			return false;

		}
		else if(comparator.toString().equals(" notcontains ")) {

			return true;

		}
		return false;
		
	}




	public static boolean binaryCostInt(Comparator comparator, int constant) {
		
		
		String comparatorString = comparator.toString();

		if(comparatorString.equals(" == ")){

				return false;

		}
		else if(comparatorString.equals(" != ")) {

				return true;

		}
		else if(comparatorString.equals(" >= ")) {

			if(constant<1)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" <= ")) {

			if(constant>=1)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" > ")) {

			if(constant<=1)
				return true;
			else 
				return false;

		}
		else if(comparatorString.equals(" < ")) {

			if(constant>1)
				return true;
			else 
				return false;

		}

		return false;
		
		
	}

	
	/**
	 * 
	 * @param comparator
	 * @param constant
	 * @return Integer.MAX_VALUE if broad edge; Integer.MIN_VALUE if narrow edge; 0 if even split
	 */
	public static int broadOrNarrow(Comparator comparator, int constant) {
		
		
		String comparatorString = comparator.toString();

		if(comparatorString.equals(" == ")){

				return Integer.MIN_VALUE;

		}
		else if(comparatorString.equals(" != ")) {

				return Integer.MAX_VALUE;

		}
		else if(comparatorString.equals(" >= ")) {

			if(constant>0)
				return Integer.MIN_VALUE;
			else if(constant <0)
				 return Integer.MAX_VALUE;
			else
				return 0;

		}
		else if(comparatorString.equals(" <= ")) {

			if(constant>0)
				return Integer.MAX_VALUE;
			else if(constant <0)
				 return Integer.MIN_VALUE;
			else
				return 0;


		}
		else if(comparatorString.equals(" > ")) {

			if(constant>-1)
				return Integer.MIN_VALUE;
			else if(constant <-1)
				 return Integer.MAX_VALUE;
			else
				return 0;
		}
		else if(comparatorString.equals(" < ")) {

			if(constant > -1)
				return Integer.MAX_VALUE;
			else if(constant < -1)
				 return Integer.MIN_VALUE;
			else
				return 0;

		}

		return 0;
		
		
	}

	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param comparator
	 * @param constant
	 * @return Integer.MAX_VALUE if broad edge; Integer.MIN_VALUE if narrow edge; 0 if even split
	 */
	public static int broadOrNarrow(Comparator comparator, char constant) {
		
		
		String comparatorString = comparator.toString();

		if(comparatorString.equals(" == ")){

				return Integer.MIN_VALUE;

		}
		else if(comparatorString.equals(" != ")) {

				return Integer.MAX_VALUE;

		}
		else if(comparatorString.equals(" >= ")) {

			if(constant>127)
				return Integer.MIN_VALUE;
			else if(constant <127)
				 return Integer.MAX_VALUE;
			else
				return 0;

		}
		else if(comparatorString.equals(" <= ")) {
			
			if(constant>127)
				return Integer.MAX_VALUE;
			else if(constant <127)
				 return Integer.MIN_VALUE;
			else
				return 0;


		}
		else if(comparatorString.equals(" > ")) {

			if(constant>126)
				return Integer.MIN_VALUE;
			else if(constant <126)
				 return Integer.MAX_VALUE;
			else
				return 0;
		}
		else if(comparatorString.equals(" < ")) {
			
			if(constant > 126)
				return Integer.MAX_VALUE;
			else if(constant < 126)
				 return Integer.MIN_VALUE;
			else
				return 0;

		}

		return 0;
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
