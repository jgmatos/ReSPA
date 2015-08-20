package respa.leak.string;

import respa.leak.Fraction;

/**
 * This class represents an alpha factorized (if possible) with a provided common factor 
 * When it is not possible to factorize we can simple instantiate with an exponent of 0 and
 * a rest of getAlphaValue()
 * 
 * @author Joao Gouveia de Matos
 *
 */
public class FactorizedAlpha {

	
	
	private int exponent;
	
	
	private  Double commonFactor = 0.0;
	
	private  Fraction commonFactorFraction = new Fraction(0, 256);
	
	
	
	
	private double rest;//lets hope we do not need a big decimal for this
	
	
	
	
	public FactorizedAlpha(int exponent, double rest,double commonFactor) {
		
		this.exponent = exponent;
		this.rest = rest;
		this.commonFactor=commonFactor;
		
	}

	public FactorizedAlpha(int exponent, double rest,Fraction commonFactor) {
		
		this.exponent = exponent;
		this.rest = rest;
		this.commonFactorFraction = commonFactor;
		
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




	public  double getCommonFactor() {
		return commonFactor;
	}




	
	public  Fraction getCommonFactorFraction() {
		return commonFactorFraction;
	}

	
	
	
	
	
	public void multiply(FactorizedAlpha1_256 other) {
		
		exponent = exponent + other.getExponent();
		rest *= other.getRest();
		
	}
	
	public void multiply(FactorizedAlpha other) {
		
		exponent = exponent + other.getExponent();
		rest *= other.getRest();
		
	}
	
	public void factorize(int exponent) {
		
		this.exponent -= exponent;
		
	}
	
	
	
	
	
	
}
