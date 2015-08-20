package respa.leak.string.exclude;

import java.math.BigDecimal;
import java.util.ArrayList;

import respa.leak.string.LeakyChar;

public class ExcludingString extends Exclude implements Cloneable{

	private ArrayList<LeakyChar> sequence;
	private String sequencestring;
	
	public ExcludingString(String sequence) {
		
		this.sequencestring = sequence;
		this.sequence = new ArrayList<LeakyChar>();
	
		for(int i=0;i<sequence.length();i++){
			LeakyChar c = new LeakyChar();
			c.applyEquals(sequence.charAt(i));
			this.sequence.add(c);
		}
			
		
	}
	
	private ExcludingString(ArrayList<LeakyChar> sequence,String sequencestring) {
		
		this.sequence = sequence;
		this.sequencestring = sequencestring;
		
	}
	
	
	
	
	@Override
	public double getAlphaValue() {
		
		double alpha = 1.0;
		for(LeakyChar c: this.sequence)
			alpha *= c.getAlphaValue();
		
		return alpha;
	}
	
	@Override
	public boolean valid(String concrete) {
		
		if(concrete==null)
			return true;
		
		if(concrete.length()>=sequencestring.length())
			return false;
		
		for(int i=0;i<concrete.length(); i++)
			if(concrete.charAt(i)!=sequencestring.charAt(i))
				return false;
		
		return true;
		
	}

	@Override
	public BigDecimal getPreciseAlphaValue() {
		
		BigDecimal alpha = new BigDecimal("1.0");
		for(LeakyChar c: this.sequence)
			alpha = alpha.multiply(c.getPreciseAlphaValue());
		
		return alpha;
		
	}

	@Override
	public double getLogAlpha() {

		double logalpha = 0.0;
		for(LeakyChar c: this.sequence)
			logalpha += c.getLogAlpha();
		
		return logalpha;
		
	}
	
	
	public double getComplementLogAlpha() {
		
		return 0.0;//TODO
		
		
		
	}

	
	@Override
	public Exclude makeCopy() {
		return new ExcludingString(sequence, sequencestring);
	}
	
	
	
}
