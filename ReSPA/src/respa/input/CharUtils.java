package respa.input;

import java.util.HashSet;

/**
 * Some utils to assist the anonymization process
 * 
 * At this time it only assists xml
 * Other types will be supported very soon
 * 
 * @author jmatos
 *
 */
public class CharUtils {

	//private static HashSet<Integer> garbageChars;
	private static HashSet<Integer> xmlSpecialChars;
	private static boolean xmlLoaded = false;
	
	
	
	
	public static void loadXmlChars() {
		
		xmlSpecialChars = new HashSet<Integer>();
		xmlSpecialChars = new HashSet<Integer>();
		
		//garbage chars
		for(int i=0;i<32;i++)
			xmlSpecialChars.add(new Integer(i));
		xmlSpecialChars.add(127);
		xmlSpecialChars.add(129);
		xmlSpecialChars.add(141);
		xmlSpecialChars.add(143);
		xmlSpecialChars.add(144);
		xmlSpecialChars.add(157);

		//special chars
		xmlSpecialChars.add(139);
		xmlSpecialChars.add(155);
		xmlSpecialChars.add(147);
		xmlSpecialChars.add(148);
		xmlSpecialChars.add(34);
		xmlSpecialChars.add(39);
		xmlSpecialChars.add(91);
		xmlSpecialChars.add(92);
		xmlSpecialChars.add(93);
		xmlSpecialChars.add(60);
		xmlSpecialChars.add(62);
		xmlSpecialChars.add(33);
		xmlSpecialChars.add(47);
		xmlSpecialChars.add(32);
		xmlSpecialChars.add(63);
		xmlSpecialChars.add(61);

		
		xmlLoaded = true;
		
	}
	
	
	public static boolean isXmlLoaded() {
		
		return xmlLoaded;
		
	}
	
	
	
	
	
	
	public static boolean isXmlSpecialChar(int i) {

		if(!isXmlLoaded())
			loadXmlChars();
		
		return xmlSpecialChars.contains(i);
		
	}
	
	public static boolean isXmlSpecialChar(char c) {

		if(!isXmlLoaded())
			loadXmlChars();
		
		return xmlSpecialChars.contains(new Integer(c));
		
	}
	

	
	public static boolean isZeroChar(char c){
		if(((int)c)==0)
			return true;
		
		return false;
	}
	

	public static boolean unsupportedXmlChar(char c) {
		
		if(c==13||c==10)//new line
			return false;
		
		if(c>126)
			return true;
		
		if(isZeroChar(c))
			return true;
			
		//TODO: maybe more?
		if(c==35||c==36||c==37||c==42||c==94)
			return true;
		
		if(c<32)
			return true;
		
		return false;
		
	}
	
	
	public static boolean nonVisibleChar(char c) {
		
		if(c<=32)
			return true;
		
		return false;
		
	}

	public static boolean emptyChar(char c) {
		
		if(c<=31)
			return true;
		
		return false;
		
	}
	
	///////////////////
	//////other stuff
	/////////////////

	public static boolean isNumber(int i) {
		
		return (i>=48 && i<=57);
		
	}
	
	public static boolean isLetter(int i) {
		
		return (( i>=65 && i<=90 )||( i>=97 && i<=122));
		
	}
	

	public static char valueof(int i) {
		
		return (char)i;
		
	}
	
	
	//TODO other stuff
	
}

