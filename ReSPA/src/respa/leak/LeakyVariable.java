package respa.leak;

import java.math.BigDecimal;

import respa.leak.string.FactorizedAlpha1_256;

public abstract class LeakyVariable implements Cloneable{ 

	
	public abstract double getAlphaValue();
	
	public abstract BigDecimal getPreciseAlphaValue();

	public abstract double getLogAlpha();
	
	
	public abstract FactorizedAlpha1_256 getFactorizedAlpha();


	public abstract double getFastLeak();

	public abstract double lastCalculatedLeak();
	
	
	
	
	public abstract String id();
	
	public abstract Object getRandomSolution();
	
	public abstract Object makeCopy();
	
	@Override
	public Object clone() {
		return makeCopy();
	}
	
}
