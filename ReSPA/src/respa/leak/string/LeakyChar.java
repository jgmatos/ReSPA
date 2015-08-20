package respa.leak.string;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import respa.leak.Fraction;
import respa.leak.LeakUtils;
import respa.leak.LeakyVariable;

public class LeakyChar extends LeakyVariable{

	
	private HashSet<Character> solutionsManager;
	

	
	private static final int domainSize = 256;

	
	
	
	private String id="";
	
	
	private double lastCalculatedLeak = 0.0;
	
	
	public LeakyChar() {
		
		loadManager();
		
		
	}
	
	public LeakyChar(String id) {
		this.id = id;
		loadManager();
	}
	
	private LeakyChar(HashSet<Character> solutionsManager,String id,double lastCalculatedLeak) {
		
		this.solutionsManager = solutionsManager;
		this.id = id;
		this.lastCalculatedLeak = lastCalculatedLeak;
		
	}
	
	
	
	
	
	public static int domainSize() {
		return domainSize;
	}
	
	
	public double getAlphaValue() {
		return (double)((double)solutionsManager.size()/(double)domainSize());
	}
	
	public BigDecimal getPreciseAlphaValue() {
		
		BigDecimal solutions = new BigDecimal(solutionsManager.size());
		BigDecimal domain = new BigDecimal(domainSize);
		
		return  solutions.divide(domain);
		
	}
	
	
	public Fraction getAlpha() {
		
		return new Fraction(solutionsManager.size(), domainSize);
		
	}
	
	public int numSolutions() {
		
		return solutionsManager.size();
		
	}
	

	
	
	
	public double getLogAlpha() {
		
		return LeakUtils.log2(getAlphaValue());
		
	}
	
	
	
	
	
	
	public void applyNotEquals(int c) {
		
		solutionsManager.remove(c);
		
		
	}
	
	public void applyEquals(int c) {
		
		solutionsManager.clear();
		solutionsManager.add((char)c);
		
	}
	
	public void applyEqualsIgnoreCase(int c) {
		
		solutionsManager.clear();
		solutionsManager.add((char)c);
		if(c>=65&&c<=90)
			solutionsManager.add((char)(c+32));
		else if(c>=97&&c<=122)
			solutionsManager.add((char)(c-32));
		
	}
	
	public void applyGreaterThan(int i) {
		for(int j=0;j<=i;j++)
			solutionsManager.remove((char)j);
	}
	
	public void applyGreaterEquals(int i) {
		
		applyGreaterThan(i-1);
		
	}
	
	public void applyLowerThan(int i) {
		for(int j=i;j<256;j++)
			solutionsManager.remove((char)j);
	}
	
	public void applyLowerEquals(int i) {
		
		applyLowerThan(i+1);
		
	}

	
	
	
	

	
	
	private void loadManager() {
		
		solutionsManager = new HashSet<Character>();
		for(int i=0;i<256; i++)
			solutionsManager.add((char)i);
		
	}
	

	
	
	public boolean totalLeak() {
		
		return this.solutionsManager.size()==1;
		
	}


	@Override
	public FactorizedAlpha1_256 getFactorizedAlpha() {

		if(totalLeak())
			return new FactorizedAlpha1_256(1,1.0);
		else
			return new FactorizedAlpha1_256(0,getAlphaValue());
		
	}


	@Override
	public double getFastLeak() {
		return (lastCalculatedLeak=LeakUtils.invertSignal(getLogAlpha()));
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

		Random rand = new Random();
		int coin = rand.nextInt(this.solutionsManager.size());
		
		Character solution = new Character(' ');
		Iterator<Character> iterator = this.solutionsManager.iterator();
		int i=0;
		while(iterator.hasNext()){
			if(i==coin){
				solution = iterator.next();
				break;
			}
			else
				iterator.next();
			i++;
		}
		
		return (Object)solution.charValue();
	
	}

	@Override
	public double lastCalculatedLeak() {
		return lastCalculatedLeak;
	}
	

	@Override
	public Object makeCopy() {
		HashSet<Character> cloneset = new HashSet<Character>();
		for(Character c: this.solutionsManager)
			cloneset.add(new Character(c.charValue()));

		return (Object)new LeakyChar(cloneset, id, lastCalculatedLeak);
	}
	
	
	
	
	
}
