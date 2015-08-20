package respa.leak.string;

import respa.leak.Fraction;

/**
 * This class represents an alpha factorized (if possible) with a common factor of (1/256)^x
 * When it is not possible to factorize we can simple instantiate with an exponent of 0 and
 * a rest of getAlphaValue()
 * 
 * @author Joao Gouveia de Matos
 *
 */
public class FactorizedAlpha1_256 {

	
	
	private int exponent;
	
	
	private static final double beta = ((double)(1.0)/(256.0));
	
	private static final Fraction betaFraction = new Fraction(1, 256);
	
	
	
	
	private double rest;//lets hope we do not need a big decimal for this
	
	
	
	
	public FactorizedAlpha1_256(int exponent, double rest) {
		
		this.exponent = exponent;
		this.rest = rest;
		
	}




	public int getExponent() {
		return exponent;
	}




	public void setExponent(int exponent) {
		this.exponent = exponent;
	}




	public double getRest() {
		return rest;
	}




	public void setRest(double rest) {
		this.rest = rest;
	}




	public static double getBeta() {
		return beta;
	}




	public static Fraction getBetafraction() {
		return betaFraction;
	}

	
	
	
	
	
	public void multiply(FactorizedAlpha1_256 other) {
		
		exponent = exponent + other.getExponent();
		rest *= other.getRest();
		
	}
	
	public void factorize(int exponent) {
		
		this.exponent -= exponent;
		
	}
	
	
	
	
	
	
}
