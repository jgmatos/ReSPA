package respa.leak.numeric.integer;

import java.math.BigDecimal;

import respa.leak.LeakUtils;
import respa.leak.LeakyVariable;
import respa.leak.string.FactorizedAlpha1_256;


public class LeakyInteger extends LeakyVariable{

	
	
	
	private IntegerInterval interval;
	
	
	private static final double domainSize = 4294967296.0;;
	

	private double lastCalculatedLeak = 0.0;
	
	
	
	public LeakyInteger() {
		
		interval = new IntegerInterval();
		
	}
	
	
	public LeakyInteger(IntegerInterval interval) {
		
		this.interval = interval;
		
	}

	
	public LeakyInteger(int begin, int end) {
		
		this.interval = new IntegerInterval(begin, end);
		
	}

	
	
	
	
	
	
	public static double domainSize() {
		
		return domainSize;
		
	}
	
	
	
	public double getAlphaValue() {

		return (double)((double)interval.size()/(double)domainSize());
		
	}
	
	
	public double numSolutions() {
		
		return interval.size();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	public void applyNotEquals(int c) {
		
		interval.remove(c);
		
		
	}
	
	public void applyEquals(int c) {
		
		interval = new IntegerInterval(c,c);
		
	}
	
	public void applyGreaterThan(int i) {
		
		interval.greaterThan(i+1);		
	
	}
	
	public void applyGreaterEquals(int i) {
		
		interval.greaterThan(i);				
	}
	
	public void applyLowerThan(int i) {
		
		interval.lowerThan(i-1);
		
	}
	
	public void applyLowerEquals(int i) {
		
		interval.lowerThan(i);
		
	}


	@Override
	public BigDecimal getPreciseAlphaValue() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double getLogAlpha() {

		return LeakUtils.log2(getAlphaValue());
	
	}


	@Override
	public FactorizedAlpha1_256 getFactorizedAlpha() {
		return new FactorizedAlpha1_256(0, getAlphaValue());
	}


	@Override
	public double getFastLeak() {
		
		return (lastCalculatedLeak=LeakUtils.invertSignal(getLogAlpha()));
	
	}


	@Override
	public String id() {
		return null;
	}


	@Override
	public Object getRandomSolution() {
		
		return (Object)interval.getRandomSolution();
	
	}


	@Override
	public double lastCalculatedLeak() {
		return lastCalculatedLeak;
	}


	@Override
	public Object makeCopy() {
		return new LeakyInteger((IntegerInterval)interval.clone());
	}
	
	
	
	
	
	
	
	
	
	
}
