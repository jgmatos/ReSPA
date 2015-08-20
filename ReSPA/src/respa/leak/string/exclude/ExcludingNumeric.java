package respa.leak.string.exclude;

import java.math.BigDecimal;

import respa.leak.LeakUtils;

public class ExcludingNumeric extends Exclude implements Cloneable{

	
	private double start;
	
	private double end;
	
	private double digits;
	
	
	public ExcludingNumeric(int start, int end) {
		
		this.start = (double) start;
		
		this.end = (double) end;
		
		digits = (double) String.valueOf(end).length();
		
	}
	
	public ExcludingNumeric(double start, double end) {

		this.start= start;
		this.end = end;
		digits = (double) String.valueOf(end).length();
		
	}
	
	
	
	@Override
	public double getAlphaValue() {
		return (double)((double)(end-start))/((double)256.0*digits);
	}

	@Override
	public boolean valid(String concrete) {

		try {
			double i = Double.valueOf(concrete);
			
			if(i>start && i<end)
				return true;
			
		}
		catch(Exception e) {
			
		}
		
		return false;
		
	}



	@Override
	public BigDecimal getPreciseAlphaValue() {
		
		return new BigDecimal((double)((double)(end-start))/((double)256.0*digits));
		
	}



	@Override
	public double getLogAlpha() {
		return LeakUtils.log2(getAlphaValue()); //double can handle a single numeric value
	}

	
	
	@Override
	public double getComplementLogAlpha() {
		
		return LeakUtils.log2(1.0-getAlphaValue());
		
	}
	

	

	@Override
	public Exclude makeCopy() {
		return new ExcludingNumeric(start, end);
	}
	
}
