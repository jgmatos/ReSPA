package respa.input;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 *	This class implements an anonymized char
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class AnonymizedChar {




	private Character revealedValue;//worst case scenario this is not null


	private HashSet<Character> restrictions;//all the values that this variable cannot assume


	private HashSet<Character> domain;//the oposite of the previous hashset field


	public AnonymizedChar() {

		revealedValue= null;
		restrictions = new HashSet<Character>();
		domain = new HashSet<Character>();

		for(int i=0;i<256;i++)
			this.domain.add((char)i);

	}


	public boolean fullyRevealed() {

		return revealedValue!=null;
		
	}

	public boolean fullyAnonymized() {

		return restrictions.size()==0;

	}

	public void specifyValue(char c) {

		this.revealedValue = new Character(c);

		for(int j=0;j<256;j++)
			this.restrictions.add((char)j);

		this.restrictions.remove(c);
		this.domain.clear();
		this.domain.add(c);
	}

	public void specifyValue(int i) {

		specifyValue((char)i);
		
	}


	public void addRestriction(char c) {

		this.restrictions.add(c);
		this.domain.remove(c);
		
	}

	public void addRestriction(int i) {

		addRestriction((char)i);
		
	}

	public Iterator<Character> getRestrictions() {

		return restrictions.iterator();

	}
	
	public HashSet<Character> getRestrictionsSet() {
		
		return restrictions;
		
	}






	public Character getRandomAnonymization() {

		if(!fullyRevealed()) {

			Random r = new Random();
			char c = (char)r.nextInt(256);

			while(restrictions.contains(c)) 
				c = (char)r.nextInt(256);

			return c;
		}

		return this.revealedValue;

	}



	public Character getAnonymization(char replace) {

		if(!fullyRevealed()) {

			Random r = new Random();
			char c = replace;

			while(restrictions.contains(c)) 
				c = (char)r.nextInt(256);

			return c;
		}

		return this.revealedValue;

	}
	

	
	
	public Iterator<Character> getDomain(){
		
		return domain.iterator();
	
	}
	
	public HashSet<Character> getDomainSet() {
		
		return domain;
		
	}
	
	
	
	

	public int domainSize() {
		
		return domain.size();
		
	}
	
	
	public boolean insideDomain(char c) {
		
		return domain.contains(c);
		
	}
	
	/**
	 * Calculate the bit leakage for this particular Byte
	 * 
	 * @param originalCharacter The Byte in the original input
	 * @return	The fraction of bits leaked 1 to 0
	 */
	public double fractionBitsLeaked(char originalCharacter) {
		
		if(!domain.contains(originalCharacter))
			return 0.0;//this is very debatable
	
		return 1.0/((double)domainSize());
		
		
	}


	/**
	 * Calculate the bit anonymization for this particular Byte
	 * 
	 * @param originalCharacter The Byte in the original input
	 * @return	The fraction of bits anonymized 1 to 0
	 */
	public double fractionBitsAnonymized(char originalCharacter) {
		
		return 1.0-((double)fractionBitsLeaked(originalCharacter));
		
	}
	
	
	/**
	 * Calculate the bit leakage for this particular Byte
	 * 
	 * @param originalCharacter The Byte in the original input
	 * @return	The number of bits leaked 1 to 0
	 */
	public double bitsLeaked(char originalCharacter) {
		
		if(!domain.contains(originalCharacter))
			return 0.0;
		
		return 8.0*fractionBitsLeaked(originalCharacter);
		
	}
	
	
	/**
	 * Calculate the bit anonymization for this particular Byte
	 * 
	 * @param originalCharacter The Byte in the original input
	 * @return	The number of bits anonymized 1 to 0
	 */
	public double bitsAnonymized(char originalCharacter) {
		
		if(!domain.contains(originalCharacter))
			return 0.0;
		
		return 8.0 - bitsLeaked(originalCharacter);
		
	}
	
	
	
	
	
	
	public double getAlpha(char originalCharacter) {
	
		if(!domain.contains(originalCharacter))
			return 0.0;
	
		return (double)domain.size()/256;

	}
	
	public double getEntropyLeak(char originalCharacter) {
		
		if(!domain.contains(originalCharacter))
			return 0.0;
		
		return (double) -log2(getAlpha(originalCharacter));
		
	}
	
	
	public static double log2(double num)
	{
	return (Math.log(num)/Math.log(2));
	}

}
