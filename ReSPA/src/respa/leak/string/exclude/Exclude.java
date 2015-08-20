package respa.leak.string.exclude;

import java.math.BigDecimal;

public abstract class Exclude implements Cloneable{

	public abstract double getAlphaValue();
	
	public abstract BigDecimal getPreciseAlphaValue();
	
	public abstract boolean valid(String concrete);
	
	public abstract double getLogAlpha();
	
	public abstract double getComplementLogAlpha();
	
	public abstract Exclude makeCopy();
	
	@Override
	public Object clone() {

		return makeCopy();
		
	}
	
}
