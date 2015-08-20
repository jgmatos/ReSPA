package respa.cost;

import java.util.ArrayList;

/**
 * 
 * @author jmatos
 *
 * This class treats the byte-cost of an input integer as the length of
 * its string representation
 *
 */
public class CostlyNumber implements CostlyVar{


	private String id;

	private ArrayList<CostlyChar> costlyNumber;
	private boolean isFinal = false;

	
	
	public CostlyNumber(String id,int initialSize) {

		this.id=id;
		costlyNumber= new ArrayList<CostlyChar>();
		for(int i=0;i<initialSize;i++)
			costlyNumber.add(new CostlyChar(id+"_["+i+"]"));
		
	}

	private CostlyNumber(String id, ArrayList<CostlyChar> costlyNumber,boolean isFinal) {
		
		this.id = id;
		this.costlyNumber = costlyNumber;
		this.isFinal = isFinal;
		
	}
	


	public String id() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	protected ArrayList<CostlyChar> getCostyNumber() {
		return costlyNumber;
	}


	
	public int cost() {
		
		return costlyNumber.size();
		
	}
	
	
	
	
	public void applyEquals(int number) {
		
		costlyNumber.clear();
		int length = String.valueOf(number).length();
		for(int i=0;i<length;i++)
			costlyNumber.add(new CostlyChar(id+"_["+i+"]"));
		isFinal=true;//don't care about other operations for now on
		
	}
	
	
	
	public void applyGE(int number) {
		
		if(!isFinal&&costlyNumber.size()<number)
			for(int i=costlyNumber.size();i<number;i++)
				costlyNumber.add(new CostlyChar(id+"_["+i+"]"));
		
	}
	

	public void applyGT(int number) {
		applyGE(number+1);
	}

	/**
	 * It must be at least one digit long
	 * 
	 * @param number
	 */
	public void applyLE(int number) {
		if(!isFinal&&costlyNumber.size()==0)
			costlyNumber.add(new CostlyChar(id+"_["+0+"]"));
	}

	public void applyLT(int number) {
		applyLE(number);
	}

	public void applyNotEquals(int number) {
		applyLE(number);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		return new CostlyNumber(new String(this.id), (ArrayList<CostlyChar>)costlyNumber.clone(), this.isFinal);		
	}




}
