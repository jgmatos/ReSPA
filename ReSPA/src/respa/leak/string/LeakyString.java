package respa.leak.string;

import java.math.BigDecimal;
import java.util.ArrayList;

import respa.leak.LeakyVariable;
import respa.leak.string.exclude.Exclude;
import respa.leak.string.exclude.ExcludingChar;
import respa.leak.string.exclude.ExcludingString;


public class LeakyString extends LeakyVariable implements Cloneable{

	
	private ArrayList<LeakyChar> leakystring;
	
	private ArrayList<Exclude> excluding=null;
	
	private String concrete=null;
	
	private int size;
	
	
	private int domainSize;
	
	
	private double lastCalculatedLeak=0.0;
	
	
	private String id=""; //helps to calculate leak dinamically
	
	
	
	
	public LeakyString(int size) {
		
		this.size = size;
		leakystring = new ArrayList<LeakyChar>();
		for(int i=0; i<size; i++)
			leakystring.add(new LeakyChar());
		
		domainSize = 256*size;
		
	}
	
	public LeakyString(int size,String id) {
		
		this.id = id;
		this.size = size;
		leakystring = new ArrayList<LeakyChar>();
		for(int i=0; i<size; i++)
			leakystring.add(new LeakyChar(id+"_["+i+"]"));
		
		domainSize = 256*size;
		
	}
	
	
	private LeakyString(ArrayList<LeakyChar> leakystring,ArrayList<Exclude> excluding,String concrete,int size,int domainSize,String id) {
		
		this.leakystring=leakystring;
		this.excluding = excluding;
		this.concrete = concrete;
		this.size = size;
		this.domainSize = domainSize;
		this.id  = id;
		
		
	}
	
	
	public int domainSize() {
		
		return domainSize;
		
	}

	
	
	public void applyEquals(String solution) {
		
		for(int i=0; i<solution.length() && i<size; i++)
			leakystring.get(i).applyEquals(solution.charAt(i));
		
		concrete = solution;
	}
	
	public void applyEqualsIgnoreCase(String solution) {
		
		for(int i=0; i<solution.length() && i<size; i++)
			leakystring.get(i).applyEqualsIgnoreCase(solution.charAt(i));
		
	}
	
	
	public void applyNotEquals(String nonSolution) {
		
		if(excluding==null)
			excluding = new ArrayList<Exclude>();
		
		ExcludingString sequence = new ExcludingString(nonSolution);
		excluding.add(sequence);
		
	}
	
	
	
	public void applyStartsWith(String prefix) {
		
		for(int i=0; i<prefix.length() && i<size; i++)
			leakystring.get(i).applyEquals(prefix.charAt(i));
		
		concrete=prefix;
		
		
	}
	
	public void applyNotStartsWith(String prefix) {
		
		if(excluding==null)
			excluding = new ArrayList<Exclude>();
		
		ExcludingString sequence = new ExcludingString(prefix);
		excluding.add(sequence);
		
	}

	
	public void applyEndsWith(String suffix) {
		
		applyStartsWith(suffix);//for us is the same thing
		
	}
	
	public void applyNotEndsWith(String suffix) {
		
		applyNotStartsWith(suffix);//for us is the same thing
		
	}
	
	public void applyContains(String what) {
		
		applyStartsWith(what);//for us is the same thing
		
	}
	
	
	public void applyEqualsCharAt(char c, int index) {
		
		if(index>=0 && leakystring.size()>index)
			leakystring.get(index).applyEquals(c);
		
	}
	

	public void applyNotEqualsCharAt(char c, int index) {
		
		if(excluding==null)
			excluding = new ArrayList<Exclude>();
		
		ExcludingChar ec = new ExcludingChar(c, index);
		excluding.add(ec);
		
	}
	
	
	public void applyGreaterEqualsCharAt(char c, int index) {
		
		if(index>=0 && leakystring.size()>index)
			leakystring.get(index).applyGreaterEquals((int)c);
		
	}

	public void applyGreaterCharAt(char c, int index) {
		if(index>=0 && leakystring.size()>index)
			leakystring.get(index).applyGreaterThan((int)c);		
		
	}
	
	public void applyLowerEqualsCharAt(char c, int index) {
	
		if(index>=0 && leakystring.size()>index)
			leakystring.get(index).applyLowerEquals((int)c);
		
	}
	
	public void applyLowerCharAt(char c, int index) {
		
		if(index>=0 && leakystring.size()>index)
			leakystring.get(index).applyLowerThan((int)c);
		
	}
	
	

	
	
	
	
	
	
	
	
	/**
	 * Get the alpha value as a double. This should not be done this way because the result
	 * can be too small for a double to handle, and the loss of precision would be too big
	 */
	@Deprecated
	public double getAlphaValue() {
		
		double 	alphavalue=1.0;
		for(LeakyChar c: leakystring)
			alphavalue*=c.getAlphaValue();

		if(excluding!=null){

			for(Exclude e: excluding)
				if(e.valid(concrete))
					alphavalue*=(1.0-e.getAlphaValue());
			
		}
		return alphavalue;
		
	}
	
	
	/**
	 * Get the alpha value as a BigDecimal. This method solves the problem of getAlphaValue(). However
	 * the use of BigDecimal can be too slow 
	 */
	@Deprecated
	public BigDecimal getPreciseAlphaValue() {
		
		BigDecimal alpha = new BigDecimal("1.0");
		for(LeakyChar c: leakystring)
			alpha = alpha.multiply(c.getPreciseAlphaValue());

		if(excluding!=null){

			for(Exclude e: excluding)
				if(e.valid(concrete))
					alpha = alpha.multiply((new BigDecimal("1.0")).subtract(e.getPreciseAlphaValue()));
			
		}
		
		return alpha;
		
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public double getLogAlpha() {
		
		
		double logalpha = 0.0;
		
		//log(a*b) = log(a) + log(b)
		for(LeakyChar c: leakystring)
			logalpha += c.getLogAlpha();
	
		if(excluding!=null){

			for(Exclude e: excluding)
				if(e.valid(concrete))
					logalpha+=e.getComplementLogAlpha();
			
		}
		
		
		return logalpha;
	}




	@Override
	public FactorizedAlpha1_256 getFactorizedAlpha() {

		FactorizedAlpha1_256 fa = new FactorizedAlpha1_256(0, 1.0);
		
		for(LeakyChar c: leakystring){
			fa.multiply(c.getFactorizedAlpha());
		}		
		if(excluding!=null){
			double rest = 1.0;
			for(Exclude e: excluding)
				if(e.valid(concrete))
					rest*=(1.0-e.getAlphaValue());
			
			fa.setRest(fa.getRest()*rest);
			
		}
		
		
		return fa;
	}
	
	
	public FactorizedAlpha getFactorizedAlpha_(Double CommonFactor) {
		
		FactorizedAlpha fa = new FactorizedAlpha(0, 1.0,CommonFactor);
		FactorizedAlpha dummy;
		for(LeakyChar c: leakystring){
			if(c.getAlphaValue()==CommonFactor){//this char is a common factor
				dummy = new FactorizedAlpha(1, 1.0, CommonFactor);//commonFactor^1 *1.0 = commonFactor = alphavalue
			}
			else {
				dummy = new FactorizedAlpha(0, c.getAlphaValue(), CommonFactor);//commonFactor^0 *alphaValue = alphaValue
				
			}
			fa.multiply(dummy);
		}
		
		return fa;
		
	}
	
	
	public int getResidue() {
		
		int residue =0;
		for(LeakyChar lc: leakystring)
			if(lc.totalLeak())
				residue++;
		return residue;
		
	}
	
	
	public ArrayList<LeakyChar> getLeakyString() {
		
		return this.leakystring;
		
	}




	@Override
	public double getFastLeak() {
		
		lastCalculatedLeak = 0.0;
		
		for(LeakyChar lc: this.leakystring){
			lastCalculatedLeak+=lc.getFastLeak();
		}
		
		return lastCalculatedLeak;
	}



	
	
	
	
	
	
	
	
	





	@Override
	public String id() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	

	@Override
	public Object getRandomSolution() {
		
		
		String randomsolution = "";
		for(LeakyChar lc: this.leakystring){
			randomsolution = randomsolution+((Character)lc.getRandomSolution());
		}		
		return (Object)randomsolution;
		
	}

	@Override
	public double lastCalculatedLeak() {
		return lastCalculatedLeak;
	}

	@Override
	public Object makeCopy() {
		ArrayList<LeakyChar> clonedlist = new ArrayList<LeakyChar>();
		for(LeakyChar lc: this.leakystring)
			clonedlist.add((LeakyChar)lc.clone());
		
		if(excluding ==null)
			return new LeakyString(clonedlist, null, concrete, size, domainSize, id);
		else{

			ArrayList<Exclude> clonedExclude = new ArrayList<Exclude>();
			for(Exclude e: excluding)
				clonedExclude.add((Exclude)e.clone());
			return new LeakyString(clonedlist, clonedExclude, concrete, size, domainSize, id);
		}

	}

	
	
	
	
	
	

	
	
	
	
}
