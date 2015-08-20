package respa.leak.string.exclude;

import java.math.BigDecimal;

import respa.leak.LeakUtils;

public class ExcludingChar extends Exclude implements  Cloneable{

	
	private char value;
	
	
	private int index;

	
	public ExcludingChar(char value, int index) {
		
		this.value = value;
		
		this.index = index;
		
	}
	
	

	@Override
	public double getAlphaValue() {
		//return ((double)1.0)/((double)256.0);
		return 0.00390625;
	}
	
	


	@Override
	public boolean valid(String concrete) {

		if(concrete==null)
			return true;
		
		if(concrete.charAt(index)==value)
			return true;
		
		return false;
	
	}



	@Override
	public BigDecimal getPreciseAlphaValue() {
		
		return new BigDecimal("0.00390625");
		
	}



	@Override
	public double getLogAlpha() {

		return LeakUtils.log2(getAlphaValue());
	
	}
	
	
	
	public double getComplementLogAlpha() {
		
		return LeakUtils.log2(1.0-getAlphaValue());
		
	}
	



	@Override
	public Exclude makeCopy() {
		return new ExcludingChar(value, index);
	}
	
	
	
	
	
}
