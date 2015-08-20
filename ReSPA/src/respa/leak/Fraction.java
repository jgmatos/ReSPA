package respa.leak;

public class Fraction {

	
	private int numerator;
	
	
	private int denominator;
	
	
	
	private double value;
	
	
	
	
	public Fraction(int numerator,int denominator) {
		
		this.numerator = numerator;
		this.denominator = denominator;
		
		value = ((double)(numerator/denominator));
		
	}




	public int getNumerator() {
		return numerator;
	}




	



	public int getDenominator() {
		return denominator;
	}




	
	public double getValue() {
		
		return value;
		
	}
	
	
	
	
	
	
	@Override
	public String toString() {
		
		return ""+numerator+"/"+denominator;
		
	}
	
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof Fraction) {
			Fraction f = (Fraction)other;
			
			if(((Fraction) other).getDenominator()==denominator &&
					((Fraction) other).getNumerator()==numerator)
					return true;
			
			if(value==f.getValue())
				return true;
			
		}
		else if(other instanceof Integer){
			
			if(value == (Double.valueOf(other.toString())))
				return true;
			
		}
		else if(other instanceof Double) {

			if(value == ((Double)other))
				return true;
			
		}
		
		return false;
		
	}
	
	
	
}
