package respa.input;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import respa.input.AnonymizedChar;
import respa.input.InputVariable;
import respa.input.SymbolicInputString;
import respa.input.SymbolicInputVariable;
import respa.leak.string.MeasureSingleString;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicCharAtInteger;
import gov.nasa.jpf.symbc.string.SymbolicLengthInteger;

/**
 *	This class represents a symbolic input string
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
@SuppressWarnings("deprecation")
public class SymbolicInputString extends SymbolicInputVariable{


	private StringSymbolic sym;

	private String value;

	private int length;

	public char[] solution;


	public MeasureSingleString leakMeasure;


	public SymbolicInputString(int startIndex, int length, int buffer, StringSymbolic sym) {

		super(startIndex,buffer);
		this.sym = sym;
		this.length = length;

	}

	public SymbolicInputString(int startIndex, int length, int buffer) {

		super(startIndex,buffer);
		this.length = length;

	}



	
	





	public StringSymbolic getSym() {
		return sym;
	}

	public void setSym(StringSymbolic sym) {
		this.sym = sym;
	}

	public int getOffset() {
		return super.getStartIndex();
	}

	public void setOffset(int offset) {
		super.setStartIndex(offset);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}







	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public char[] getValue() {

		return value.toCharArray();

	}

	@Override
	public String getValueAsString() {

		return value;

	}






	@Override
	public boolean containsIndex(int index) {

		if(index >= getStartIndex() && index < length+getStartIndex())
			return true;

		return false;

	}

	@Override
	public char getValueAtIndex(int index) {

		//TODO

		//		if(!containsIndex(index))
		return '?';



	}









	@Override
	public boolean sameVariable(InputVariable other) {

		if(other instanceof SymbolicInputString)
			if(other.getStartIndex()==this.getStartIndex() &&
			((SymbolicInputString)other).getLength()==this.length &&
			((SymbolicInputString)other).getBuffer()==super.getBuffer() )
				return true;

		return false;
	}






	@Override
	public String representation() {
		return "SIS-"+getBuffer()+"-"+getStartIndex()+"-"+getLength();
	}



	@Override
	public int hash() {

		return representation().hashCode();

	}



















	/************************************/
	/** 		 SOLVER PART	       **/
	/************************************/




	private AnonymizedChar[] anonymization = null;


	private ArrayList<Constraint> numericConstraints = new ArrayList<Constraint>();
	private ArrayList<StringConstraint> stringConstraints = new ArrayList<StringConstraint>();

	public void addContraint(Constraint constraint) {

		this.numericConstraints.add(constraint);

	}
	public void addContraint(StringConstraint constraint) {

		this.stringConstraints.add(constraint);

	}
	public Iterator<Constraint> getNumericConstraints() {

		return numericConstraints.iterator();

	}
	public Iterator<StringConstraint> getStringConstraints() {

		return stringConstraints.iterator();

	}

 



	@Override
	public boolean isContraintFree() {

		return (numericConstraints.size()==0)&&(stringConstraints.size()==0);

	}



	
	
	private void initAnonymization() {
		for(int i=0;i<this.anonymization.length;i++)
			this.anonymization[i] = new AnonymizedChar();
	}
	
	
	@Override
	public void anonymize() {

		//be advised that a constraint may increase or decrease the
		//original size of the solution
		//	char [] sol = new char [this.length];
		this.anonymization = new AnonymizedChar[this.length];
		initAnonymization();
		
		
		
		LinkedList<Constraint> lengthConstraints = new LinkedList<Constraint>();
		LinkedList<Constraint> charatConstraints = new LinkedList<Constraint>();


		Iterator<Constraint> it = getNumericConstraints();
		Constraint dummy;
		while(it.hasNext()){
			dummy=it.next();
			if(dummy.getLeft() instanceof SymbolicLengthInteger||
					dummy.getRight() instanceof SymbolicLengthInteger){

				if(dummy.getComparator().equals(" == "))//this facilitates a lot the next step 
					lengthConstraints.addFirst(dummy);
				else
					lengthConstraints.add(dummy);

			}
			else if(dummy.getLeft() instanceof SymbolicCharAtInteger||
					dummy.getRight() instanceof SymbolicCharAtInteger) {

				if(dummy.getComparator().equals(" == "))
					charatConstraints.addFirst(dummy);
				else
					charatConstraints.add(dummy);

			}
			//TODO: other operations
		}




		////////////////////
		////// LENGTH
		////////////////////
		if(!lengthConstraints.isEmpty()){
			if(lengthConstraints.getFirst().getComparator().toString().equals(" == ")) {

				if(lengthConstraints.getFirst().getLeft() instanceof IntegerConstant) {
					anonymization = new AnonymizedChar[((IntegerConstant)lengthConstraints.getFirst().getLeft()).value];
				}
				else if(lengthConstraints.getFirst().getRight() instanceof IntegerConstant) {
					anonymization = new AnonymizedChar[((IntegerConstant)lengthConstraints.getFirst().getRight()).value];
				}
				//otherwise leave it as it was
			}
			else {//bad luck 

				HashSet<Integer> ignoreNumbers = new HashSet<Integer>();
				int biggerThan=Integer.MAX_VALUE;
				int smallerThan=Integer.MIN_VALUE;
				int auxiliar;
				for(Constraint c: lengthConstraints){//

					if(c.getComparator().toString().equals(" != ")){

						if(c.getLeft() instanceof IntegerConstant)
							ignoreNumbers.add(((IntegerConstant)c.getLeft()).value);
						else if(c.getRight() instanceof IntegerConstant)
							ignoreNumbers.add(((IntegerConstant)c.getRight()).value);

					}
					else if(c.getComparator().toString().equals(" > ")) {
						if(c.getLeft() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getLeft()).value;
							if(auxiliar < biggerThan)
								biggerThan = auxiliar;
						}
						else if(c.getRight() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getRight()).value;
							if(auxiliar < biggerThan)
								biggerThan = auxiliar;
						}
					}
					else if(c.getComparator().toString().equals(" < ")) {
						if(c.getLeft() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getLeft()).value;
							if(auxiliar > smallerThan)
								smallerThan = auxiliar;
						}
						else if(c.getRight() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getRight()).value;
							if(auxiliar > smallerThan)
								smallerThan = auxiliar;
						}
					}
					else if(c.getComparator().equals(" >= ")) {
						if(c.getLeft() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getLeft()).value;
							if(auxiliar < biggerThan)
								biggerThan = auxiliar-1;
						}
						else if(c.getRight() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getRight()).value;
							if(auxiliar < biggerThan)
								biggerThan = auxiliar-1;
						}
					}
					else if(c.getComparator().toString().equals(" <= ")) {
						if(c.getLeft() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getLeft()).value;
							if(auxiliar > smallerThan)
								smallerThan = auxiliar+1;
						}
						else if(c.getRight() instanceof IntegerConstant){
							auxiliar=((IntegerConstant)c.getRight()).value;
							if(auxiliar > smallerThan)
								smallerThan = auxiliar+1;
						}
					}
					//we have already ruled out ==
				}
				Random solutionGen = new Random();
				int bottom = 0;

				if(smallerThan>0)
					bottom = smallerThan;

				if(biggerThan==Integer.MAX_VALUE)
					biggerThan=ignoreNumbers.size()+length;

				int newLength = this.length;
				while(ignoreNumbers.contains(newLength)){
					newLength = bottom+solutionGen.nextInt(biggerThan);
				}

				anonymization = new AnonymizedChar[newLength];		
			
			}
			initAnonymization();

		}


		/////////////////////
		///// CHARAT
		/////////////////////

		if(!charatConstraints.isEmpty()){

			int index=Integer.MIN_VALUE;
			int value=Integer.MIN_VALUE;
			for(Constraint c: charatConstraints) {

				
				
				
				if(c.getComparator().toString().equals(" == ")) {

					if(c.getLeft() instanceof SymbolicCharAtInteger) {

						if(((SymbolicCharAtInteger)c.getLeft()).index instanceof IntegerConstant &&
								c.getRight() instanceof IntegerConstant){

							index =((IntegerConstant)((SymbolicCharAtInteger)c.getLeft()).index).value; 
							value = ((IntegerConstant)c.getRight()).value;

						}

					}
					else if(c.getRight() instanceof SymbolicCharAtInteger) {

						if(((SymbolicCharAtInteger)c.getRight()).index instanceof IntegerConstant &&
								c.getLeft() instanceof IntegerConstant){

							index =((IntegerConstant)((SymbolicCharAtInteger)c.getRight()).index).value; 
							value = ((IntegerConstant)c.getLeft()).value;

						}

					}

					if(index>=0 && value>Integer.MIN_VALUE)
						anonymization[index].specifyValue((char)value);

				}
				else if(c.getComparator().toString().equals(" != ")) {

					if(c.getLeft() instanceof SymbolicCharAtInteger) {

						if(((SymbolicCharAtInteger)c.getLeft()).index instanceof IntegerConstant &&
								c.getRight() instanceof IntegerConstant){

							index =((IntegerConstant)((SymbolicCharAtInteger)c.getLeft()).index).value; 
							value = ((IntegerConstant)c.getRight()).value;

						}

					}
					else if(c.getRight() instanceof SymbolicCharAtInteger) {

						if(((SymbolicCharAtInteger)c.getRight()).index instanceof IntegerConstant &&
								c.getLeft() instanceof IntegerConstant){

							index =((IntegerConstant)((SymbolicCharAtInteger)c.getRight()).index).value; 
							value = ((IntegerConstant)c.getLeft()).value;

						}

					}

					if(index>=0 && value>Integer.MIN_VALUE)
						anonymization[index].addRestriction((char)value);

				}

			}
		}

		

			

		/////////////////////////////////
		////TODO: other operations
		/////////////////////////////////


	}
	
	
	
	
	
	
	
	
	public double leak() {

	//	 double [] leak = null;

		//leak = new double[this.length];
	
		LinkedList<Constraint> charatConstraints = new LinkedList<Constraint>();


		Iterator<Constraint> it = getNumericConstraints();
		Constraint dummy;
		while(it.hasNext()){
			dummy=it.next();
			if(dummy.getLeft() instanceof SymbolicCharAtInteger||
					dummy.getRight() instanceof SymbolicCharAtInteger) {

				if(dummy.getComparator().toString().equals(" == "))
					charatConstraints.addFirst(dummy);
				else
					charatConstraints.add(dummy);

			}
			//TODO: other operations
		}
		
		SymbolicCharAtInteger charat;
		int domain = 1;
		double result = 0.0;
		System.out.println("banhada: "+charatConstraints.size());
		for(Constraint c: charatConstraints){
			
			if(c.getLeft() instanceof SymbolicCharAtInteger)
				charat = (SymbolicCharAtInteger)c.getLeft();
			else
				charat = (SymbolicCharAtInteger)c.getRight();
			
			domain = charat._max-charat._min;
			result +=(double)(domain/256);
			System.out.println("banhada: "+domain+" ;; "+result);
			
		}

		
		return result;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	@Override
	public char[] getAnonymization(char replace) {

		if(!isAnonymized())
			anonymize();

		char [] sol = new char [this.anonymization.length];

		for(int i=0;i<sol.length;i++)
			sol[i] = anonymization[i].getAnonymization(replace);
		
			
		return sol;

	}




	@Override
	public char[] getDefaultAnonymization() {

		return getAnonymization('x');

	}

	@Override
	public char[] getRandomAnonymization() {

		if(!isAnonymized())
			anonymize();

		char [] sol = new char [this.anonymization.length];

		for(int i=0;i<sol.length;i++)
			sol[i] =anonymization[i].getRandomAnonymization();

		return sol;

	}

	@Override
	public boolean isAnonymized() {

		return anonymization!=null;

	}





	public int getShift() {

		if(this.anonymization.length>this.length)
			return this.anonymization.length-this.length;
		else
			return this.length-this.anonymization.length;

	}
	
	
	
	
	
	
	
	

	
	

	public double[] detailedLeakage() {

		double [] leakage = new double[this.length];
		
		if(this.anonymization.length < leakage.length){
			for(int i=anonymization.length;i<leakage.length;i++)
				leakage[i] = 0.0;//the extra chars do not reveal any bits from the original input
		}
			
		char [] originalBuf = getValue();
		for(int i=0;i<anonymization.length&&i<leakage.length;i++)
			leakage[i] = anonymization[i].bitsLeaked(originalBuf[i]);
		
		return leakage;
	
	}
	
	
	@Override
	public double bitsLeaked() {

		double [] leakage = detailedLeakage();
		
		double totalLeak = 0;
		for(int i=0;i<leakage.length;i++)
			totalLeak+=leakage[i];
		
		return totalLeak;
		
	}

	
	
	@Override
	public double fractionBitsLeaked() {

		return bitsLeaked()/this.length;
	
	}
	
	
	public double[] detailedLeakageFraction() {

		double [] leakage = new double[this.length];
		
		if(this.anonymization.length < leakage.length){
			for(int i=anonymization.length;i<leakage.length;i++)
				leakage[i] = 0.0;//the extra chars do not reveal any bits from the original input
		}
			
		char [] originalBuf = getValue();
		for(int i=0;i<anonymization.length&&i<leakage.length;i++)
			leakage[i] = anonymization[i].fractionBitsLeaked(originalBuf[i]);
		
		return leakage;
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	public double[] detailedAnonymization() {
		double [] anonym = new double[this.length];
		
		if(this.anonymization.length < anonym.length){
			for(int i=anonymization.length;i<anonym.length;i++)
				anonym[i] = 0.0;//irrelevant?
		}
			
		char [] originalBuf = getValue();
		for(int i=0;i<anonymization.length&&i<anonym.length;i++)
			anonym[i] = anonymization[i].bitsAnonymized(originalBuf[i]);
		
		return anonym;
	}

	
	@Override
	public double bitsAnonymized() {
		double [] anonym = detailedAnonymization();
		
		double totalAnonym = 0;
		for(int i=0;i<anonym.length;i++)
			totalAnonym+=anonym[i];
		
		return totalAnonym;
	}

	@Override
	public double fractionBitsAnonymized() {

		return bitsAnonymized()/this.length;
	
	}




	
	
	
	
	
	public double[] detailedEntropyLeak() {

		double [] leakage = new double[this.length];
		
		if(this.anonymization.length < leakage.length){
			for(int i=anonymization.length;i<leakage.length;i++)
				leakage[i] = 0.0;//the extra chars do not reveal any bits from the original input
		}
			
		char [] originalBuf = getValue();
		for(int i=0;i<anonymization.length&&i<leakage.length;i++)
			leakage[i] = anonymization[i].getEntropyLeak(originalBuf[i]);
		
		return leakage;
		
		
	}
	
	
	public double entropyLeak() {
		
		double [] leakage = detailedEntropyLeak();
		
		double totalLeak = 0.0;
		for(int i=0;i<leakage.length;i++)
			totalLeak+=leakage[i];
		
		return totalLeak;
		
		
	}
	





}
