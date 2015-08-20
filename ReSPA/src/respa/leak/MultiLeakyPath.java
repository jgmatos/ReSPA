package respa.leak;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import respa.leak.string.FactorizedAlpha;
import respa.leak.string.FactorizedAlpha1_256;
import respa.leak.string.LeakyChar;
import respa.leak.string.LeakyString;

public class MultiLeakyPath {

	
	private ArrayList<LeakyPath> paths;
	
	
	public MultiLeakyPath() {
		
		paths = new ArrayList<LeakyPath>();
		
	}

	
	
	
	public void add(LeakyPath path) {
		
		paths.add(path);
		
	}
	
	
	
	public double getAlphaValue() {
		
		double alphaValue = 0.0;

		for(LeakyPath lp: this.paths)
			alphaValue += lp.getAlphaValue();//UNION of the paths
		
		return alphaValue;
		
	}
	
	public BigDecimal getPreciseAlphaValue() {
		
		BigDecimal alpha = new BigDecimal("0.0");
		
		for(LeakyPath lp: this.paths)
			alpha = alpha.add(lp.getPreciseAlphaValue());

		
		return alpha;
		
	}
	
	
	public double getPreciseLeakage() {
		
		return LeakUtils.bitsLeaked(getPreciseAlphaValue().doubleValue());
		
	}
	
	
	
	public double getLeakage() {
		
		return LeakUtils.bitsLeaked(getAlphaValue());
		
		
	}
	
	
	

	
	public double getFactorizedLeak() {

		int minexponent = Integer.MAX_VALUE;
		
		ArrayList<FactorizedAlpha1_256> factorized = new ArrayList<FactorizedAlpha1_256>();
		
		for(LeakyPath lp: this.paths)
			factorized.add(lp.getFactorizedAlpha());
		
		for(FactorizedAlpha1_256 fa: factorized)
			if(fa.getExponent()<minexponent)
				minexponent = fa.getExponent();

		
		
		
		
		
		double rest = 1.0;
		//double nonfactorized =1.0;
		for(FactorizedAlpha1_256 fa: factorized){
			fa.factorize(minexponent);
			
			rest +=(fa.getRest()*fa.getExponent()*FactorizedAlpha1_256.getBeta());
			//nonfactorized*=fa.getExponent();
		}
		
		double leak = minexponent*LeakUtils.log2(FactorizedAlpha1_256.getBeta());
		
		leak += LeakUtils.log2(rest);
		
//		leak += nonfactorized*LeakUtils.log2(FactorizedAlpha.getBeta());
		
		return LeakUtils.invertSignal(leak);
	
		//3 causas possiveis para o problema:
		// leak+=
		// nonfactorized, ficou esquecido o resto do expoente depois de factorizado
		// somar n�meros negativos?
		
	}
	
	
	public double getFactorizedLeak_(){
		
	
		Double commonFactor = commonFactor(this.paths);
		if(commonFactor>0.0){
			
			ArrayList<FactorizedAlpha> factorized = new ArrayList<FactorizedAlpha>();
			for(LeakyPath lp: this.paths)
				factorized.add(lp.getFactorizedAlpha_(commonFactor));

			
			
			int minexponent = Integer.MAX_VALUE;
			for(FactorizedAlpha fa: factorized)
				if(fa.getExponent()<minexponent)
					minexponent = fa.getExponent();
			
			double rest = 1.0;
			for(FactorizedAlpha fa: factorized){
				
				fa.factorize(minexponent);
				
				rest +=(fa.getRest()*fa.getExponent()*commonFactor);//TODO: it should be commonfactor^exp ?
			}
			double leak = minexponent*LeakUtils.log2(commonFactor);
			leak += LeakUtils.log2(rest);
			return LeakUtils.invertSignal(leak);
		}
		else
			System.out.println("no common factor");
		return 0.0;
		
	}
	
	
	
	/**
	 * Get the most common factor amongst all LeakyStrings
	 * This procedure is expensive.
	 * 
	 * 
	 * @param factorized
	 * @return
	 */
	private Double commonFactor(ArrayList<LeakyPath> factorized) {
		
		HashMap<Double,Integer> pairs = CommonFactors();
		
		Double dmax = getMostCommon(pairs);//get the most common factor
		System.out.println("most common : "+dmax);
		while(!commonToAll(dmax) && dmax!=0.0){
			pairs.remove(dmax);//if it is not common to all LeakyStrings, exclude it
			dmax = getMostCommon(pairs); // get the next most common factor
		}
		
		return dmax;
		
	}
	
	/**
	 * Get common factors within all leakyStrings and how many times they occurred.
	 * 
	 * @return A map linking each common factor to the amount of times it occurs.
	 */
	private HashMap<Double,Integer> CommonFactors() {
		
		HashMap<Double,Integer> pairs = new HashMap<Double,Integer>();
		for(LeakyPath lp: this.paths){
			Collection<LeakyVariable> leakyvars = lp.getLeakyVars();
			for(LeakyVariable lv: leakyvars){
				if(lv instanceof LeakyString){
					
					for(LeakyChar lc: ((LeakyString) lv).getLeakyString()){
						Double doublealphaval = Double.valueOf(lc.getAlphaValue());
						
						if(pairs.containsKey(doublealphaval)){
							pairs.put(doublealphaval, pairs.get(doublealphaval)+1);
						}
						else
							pairs.put(doublealphaval, 1);
					}
					System.out.println("---");
					
				}
			}
		
		}
		return pairs;
		
	}
	
	
	/**
	 * Get the most common factor
	 * 
	 * @param pairs
	 * @return
	 */
	private Double getMostCommon(HashMap<Double,Integer> pairs) {
		
		int imax = 0, idummy=0;
		Double dmax = 0.0;
		for(Double d: pairs.keySet()){
			idummy = pairs.get(d);
			if(idummy>imax){
				imax = idummy; //the amount of times it occurred
				dmax = d; // the common factor
			}
		}
		
		return dmax; // the most common factor
		
	}
	
	/**
	 * Check whether a common factor is common to all LeakyStrings
	 * @param dmax The common factor
	 * @return
	 */
	private boolean commonToAll(Double dmax){
	
		for(LeakyPath lp: this.paths){
			
			Collection<LeakyVariable> leakyvars = lp.getLeakyVars();
			for(LeakyVariable lv: leakyvars){
				if(lv instanceof LeakyString){
					boolean found = false;
					for(LeakyChar lc: ((LeakyString) lv).getLeakyString()){
						
						Double doublealphaval = Double.valueOf(lc.getAlphaValue());
						
						if(doublealphaval.equals(dmax)){
							found=true;
							break;
						}
						
					}
					if(!found)
						return false;
					
				}
			}
			
		}
		
		return true;
		
	}
	
	
	
	
	
	
	
}
