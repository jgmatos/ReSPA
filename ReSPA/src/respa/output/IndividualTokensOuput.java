package respa.output;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
import respa.leak.string.MeasureSingleString;
import respa.main.ReSPAConfig;


/**
 *	This class represents console input
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class IndividualTokensOuput implements Output{


	private ArrayList<String> tokens;

	private String []  newin;




	public IndividualTokensOuput(char [] input) {

		tokens = new ArrayList<String>();
		Scanner scan = new Scanner(String.valueOf(input));

		while(scan.hasNextLine()){

			tokens.add(scan.nextLine());

		}

		newin = new String[tokens.size()*2];
		for(int i=0;i<newin.length;i++)
			newin[i]="";
	}



	public void add(SymbolicInputString sis) {

		

			if(sis.getOffset()>=newin.length)
				resize();

			if(sis.getOffset()>=0)
				newin[sis.getOffset()]= String.valueOf(sis.solution);
			else
				newin[0]= String.valueOf(sis.solution);
	}

	public void add(SymbolicInputInt sii) {

		newin[sii.getIndex()]= String.valueOf(sii.getSolution());


	}

	@Override
	public void printToSystemin() {

		for(String s: newin)
			System.out.println(s);


	}

	@Override
	public void printToFile(String file) {

		try{

			FileWriter fw = new FileWriter(file);

			for(String s:newin)
				fw.write(s+"\n");


			fw.flush();
			fw.close();
			System.out.println("[ReSPA][IndividualTokensOutput] --> Generated alternative input: "+file);

		}
		catch(Exception e) {
			System.out.println("[ReSPA][IndividualTokensOutput] --> The alternative input could not be printed to file");
		}



	}

	public void printObjectOutputStream(String file) {

		try{

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			LinkedList<String> ll = new LinkedList<String>();

			for(int i = 0; i<newin.length;i++)
				ll.add(newin[i]);




			oos.writeObject(ll);
			oos.close();
			System.out.println("[ReSPA][IndividualTokensOutput] --> Generated alternative input: "+file);

		}
		catch(Exception e) {

		}
	}

	@Override
	public int getResidue() {

		int residue = 0;
		if(tokens!=null&&newin!=null){

			for(int i=0;i<tokens.size();i++)
				for(int j=0;j<tokens.get(i).length()&&j<newin[i].length();j++)
					if(tokens.get(i).charAt(j)==newin[i].charAt(j))
						residue++;

			if(ReSPAConfig.residue_delimiters)
				residue+=ReSPAConfig.delimiters;//residue+=(tokens.size()-1);

		}

		return residue;

	}

	public int inputSize() {

		int size = 0;
		for(String s: tokens)
			size +=s.length();

		if(ReSPAConfig.residue_delimiters)
			size+=(tokens.size()-1);

		return size;

	}



	public String[] getNewInput() {

		return this.newin;

	}



	@Override
	public double getLeakage() {

		double leak=0.0;







		return leak;

	}




	private void resize() {


		String [] resized  = new String[newin.length*2]; 
		for(int i=0;i<newin.length;i++)
			resized[i] = newin[i];

		newin = resized;

	}




}
