package respa.cost;


import gov.nasa.jpf.symbc.string.StringSymbolic;

import java.util.ArrayList;

/**
 * 
 * @author Joao Gouveia de Matos GSD/INESC-ID
 *
 * Keep track of the amount of bytes used by a string
 *
 */
public class CostlyString implements CostlyVar{

	
	private String id;
	
	
	private ArrayList<CostlyChar> costlyString;
	private boolean isFinal = false;
	
	private StringSymbolic symb;
	
	
	public CostlyString(String id,int initialSize) {
		
		this.id=id;
		costlyString= new ArrayList<CostlyChar>();
		for(int i=0;i<initialSize;i++)
			costlyString.add(new CostlyChar(id+"_["+i+"]"));
	}
	
	private CostlyString(String id, ArrayList<CostlyChar> costlyString,boolean isFinal) {
		
		this.id = id;
		this.costlyString = costlyString;
		this.isFinal = isFinal;
		
	}
	

	@Override	
	public int cost() {
		
		return costlyString.size();
		
	}
	
	@Override
	public String id() {
		
		return id;
		
	}
	
	
	public void applyEquals(String s) {
		
		costlyString.clear();
		for(int i=0;i<s.length();i++)
			costlyString.add(new CostlyChar(id+"_["+i+"]"));
		isFinal=true;//don't care about other operations for now on

	}
	
	
	public void applyEqualsIgnoreCase(String s) {
		

		applyEquals(s);
		
	}
	
	

	
	/**
	 * 
	 * @param at The index of the string
	 * 
	 * Note that if at=n implies that the string has minimum size of n+1
	 * @requires at > costyString.size()
	 */
	public void applyCharAt(int at) {

		if(!isFinal&&costlyString.size()<at)
			for(int i=costlyString.size();i<at;i++)
				costlyString.add(new CostlyChar(id+"_["+i+"]"));
		
	}


	
	/**
	 * @param prefix
	 * 
	 * @requires prefix.length > costyString.size()
	 */
	public void applyStartsWith(String prefix) {
		
		applyCharAt(prefix.length());
		
	}
	
	/**
	 * @param suffix
	 * 
	 * @requires suffix.length > costyString.size()
	 */
	public void applyEndsWith(String suffix) {
		
		applyStartsWith(suffix);
		
	}
	
	
	/**
	 * @param substring
	 * 
	 * @requires substring.length > costyString.size()
	 */
	public void applyContains(String substring) {
		
		applyStartsWith(substring);
		
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param length
	 * 
	 * @requires length > costyString.size()
	 */
	public void applyLengthEquals(int length) {
		
		applyCharAt(length);
		
	}
	
	/**
	 * @param length
	 * 
	 * @requires length > costyString.size()
	 */
	public void applyLengthGE(int length) {
		
		applyLengthEquals(length);
		
	}
	
	/**
	 * @param length
	 * 
	 * @requires length > costyString.size()
	 */
	public void applyLengthGT(int length) {
		
		applyLengthGE(length+1);
		
	}
	
	/**
	 * @param length
	 */
	public void applyLengthLT(int length) {

		//Do nothing
		
	}
	
	
	/**
	 * @param length
	 */
	public void applyLengthLE(int length) {

		//Do nothing
		
	}
	
	
	
	
	
	
	
	
	
	
	//TODO: not operations
	
	public void applyNotEquals(String s) {
		
		if(s.equals("")){
			applyCharAt(1);
		}
		
	}



	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		return new CostlyString(new String(this.id), (ArrayList<CostlyChar>)costlyString.clone(), this.isFinal);		
	}

	
	
	
	
	public StringSymbolic getSymb() {
		return symb;
	}

	public void setSymb(StringSymbolic symb) {
		this.symb = symb;
	}

	

	
	
	
	
	
}
