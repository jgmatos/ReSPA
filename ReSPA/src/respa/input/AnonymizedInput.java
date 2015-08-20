package respa.input;




import respa.input.CharUtils;
import respa.input.SymbolicInputString;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *	This class assists the anonymization process
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
@Deprecated
public class AnonymizedInput {

	//linked list is more efficient in this case
	//because we need to shift elements to the right
	//very often 
	private LinkedList<Character> input;
	
	private HashSet<Integer> relevantIndexes;
	private int startRelevance=Integer.MAX_VALUE;
	private int endRelevance=Integer.MIN_VALUE;
	
	
	private int currentShifting;
	
	
	
	public AnonymizedInput(char [] values) {
		
		input = new LinkedList<Character>();
		relevantIndexes = new HashSet<Integer>();
		currentShifting = 0;
		
		for(int i=0; i<values.length; i++)
			input.add(i, values[i]);
			
		endRelevance = input.size()-1;
		
	}
	
	
	
	public void addAndShift(char [] var,int offset) {

		int newOffset = offset+currentShifting;
		
		for(int i=0;i<var.length;i++){
			input.add(i+newOffset,var[i]);
			relevantIndexes.add(i+newOffset);
		}

		if(newOffset<startRelevance)
			startRelevance = newOffset;

		int newLength = newOffset+var.length;
		if(newLength>endRelevance)
			endRelevance = newLength;
		
		currentShifting+=var.length;
		

		
	}
	
	public void addAndShift(String var,int offset) {
		
		addAndShift(var.toCharArray(),offset);
		
	}
	
	public void addAndShift(SymbolicInputString var,int offset) {
		
		if(var.isAnonymized()) 
			addAndShift(var.getDefaultAnonymization(),offset);
		else
			addAndShift(var.getValue(), offset);
		
	}

	public void add(SymbolicInputString var) {
		
		//char[] anonymization = var.getDefaultAnonymization(); 
		char[] anonymization;
		if(var.solution==null || var.solution.length==0){
			anonymization= new char[1];
			anonymization[0]='x';
		}
		else
			anonymization= var.solution;
		
		char[] replacements = new char[var.getLength()];
		char[] shifters = new char[0];
		
		for(int i=0;i<replacements.length&&i<anonymization.length;i++)
			replacements[i] = anonymization[i];
		
		addAndReplace(replacements, var.getOffset());
		
		if(anonymization.length > replacements.length) {
			shifters= new char[anonymization.length-replacements.length];
			
			for(int i=0;i<shifters.length;i++)
				shifters[i] = anonymization[i+replacements.length];
			
			addAndShift(shifters, var.getOffset()+replacements.length);
		}
	

		
	}
	
	public void addAndReplace(char [] var, int offset) {
		
		int newOffset = offset+currentShifting;
		for(int i=0; i<var.length;i++){
			input.set(newOffset+i,var[i]);//shifting?
			this.relevantIndexes.add(newOffset+i);
		}
		
		if(newOffset<startRelevance)
			startRelevance = newOffset;
		
		int newLength = newOffset+var.length;
		if(newLength>endRelevance)
			endRelevance = newLength;
		
	}
	
	
	public char [] getNewInput() {
		
		char [] input = new char[this.input.size()];
		
		for(int i=0;i<input.length;i++)
			input[i] = this.input.get(i);
		
		return input;
		
	}
	
	public void addRelevantIndex(int i) {
		
		this.relevantIndexes.add(i+currentShifting);
		
	}
	
/*	public void addRelevantIndexes(List <Integer> indexes) {
		
		this.relevantIndexes.addAll(indexes);

	}*/
	
	
	public void addShifting(int shift) {
		
		this.currentShifting+=shift;
		
	}
	
	
	
	public void printToSystemIn() {
		
/*		System.out.println("");
		for(Character c: this.input)
			System.out.print(c);
		System.out.println("");*/

		System.out.println("");
		for(int i=startRelevance;i<=endRelevance;i++)
			System.out.print(this.input.get(i));
		System.out.println("");
	}
	
	public void printToSystemIn(int startRelevance,int endRelevance) {
		
		System.out.println("");
		for(int i=startRelevance;i<=endRelevance;i++)
			System.out.print(this.input.get(i));
		System.out.println("");
		
	}
	
	public void printToFile(String file) {
		
		try{
		
			FileWriter fw = new FileWriter(file);
			
		//	for(int i=startRelevance;i<=endRelevance;i++)
		//		fw.write(this.input.get(i));
			java.util.Scanner s = new java.util.Scanner(
					new FileReader("/Users/jmatos/Documents/workspace-classic/xerces-v142/JmatosTests/test-4026-anonim.xml"));

			while(s.hasNextLine())
				fw.write(s.nextLine()+"\n");
			//for(int i=0; i<this.input.size(); i++)
			//	fw.write(this.input.get(i));
			
			fw.flush();
			fw.close();
		
		}
		catch(Exception e) {
			
		}
	}
	
	
	
	public void anonymize() {
		
		for(int i=0; i<this.input.size();i++) 
			if(CharUtils.isXmlSpecialChar(this.input.get(i))) 
				this.relevantIndexes.add(i);

		for(int i=0; i<this.input.size();i++) 
			if(!relevantIndexes.contains(i))
				this.input.set(i, new Character('x'));
		
		
		//removing this leaks less information
	/*	LinkedList <Character> ll = new LinkedList<Character>(); 
		for(int i=0; i<this.input.size();i++) 
			if(relevantIndexes.contains(i))
				ll.add(input.get(i));
		
		input = ll;
		*/		
	}
	
	
	public int getSize(){
		return input.size();
	}
	
	public int getStartRelevance() {
		
		return this.startRelevance;
		
	}

	public int getEndRelevance() {
		
		return this.endRelevance;
		
	}
	
	public void setDefaultRelevance() {
		
		startRelevance =0;
		endRelevance = this.input.size()-1;
		
	}

	public void setStartRelevance(int start) {
		
		this.startRelevance = start;
	
	}

	public int getCurrentShifting() {
		
		return this.currentShifting;
		
	}

	
	public void updateEndRelevance() {
		
		int endIndex=endRelevance+1;
		Character special = this.input.get(endIndex);
		while(CharUtils.isXmlSpecialChar(special)&&endIndex<input.size()){
			endIndex++;
			special = this.input.get(endIndex);
		}
		this.endRelevance = endIndex-1;
		
	}


	
}
