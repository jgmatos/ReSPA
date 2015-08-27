package respa.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import respa.leak.LeakyVariable;
import respa.main.ReSPAConfig;

/**
 * Given an ordered Collection of LeakyVariables L, this class produces an ordered Collection of 
 * inputs that satisfy L.
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID 
 *
 */
public class LeakyOutput implements Output{

	
	
	private Collection<LeakyVariable> lv;
	private boolean built=false;
	private String output="";
	private String delimiter=null;
	
	public LeakyOutput(Collection<LeakyVariable> lv) {
		
		this.lv = lv;
		
	}

	public LeakyOutput(Collection<LeakyVariable> lv,String delimiter) {
		
		this.lv = lv;
		this.delimiter = delimiter;
		
	}

	
	private void build() {
		
		for(LeakyVariable v: this.lv){
			output = output+v.getRandomSolution();
			if(delimiter!=null)
				output = output+delimiter;
		}
		
		built=true;
		
	}
	
	
	
	@Override
	public void printToSystemin() {
		
		if(!built)
			build();
		
		System.out.println("\n\n"+output+"\n\n");
		
		
	}

	@Override
	public void printToFile(String file) {
		
		if(!built)
			build();
	
		try {
			
			FileWriter fw = new FileWriter(new File(System.getProperty("user.home")+"/.respa/tmp/alternativeInput.txt"));
			fw.write(output);
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public int getResidue() {
		
		if(!built)
			build();

		int residue = 0;
		
		for(int i=0; i<output.length()&&i<ReSPAConfig.input.length; i++){
			if(output.charAt(i)==ReSPAConfig.input[i])
				residue++;
		}
		
		return residue;
	}

	@Override
	public double getLeakage() {
		double leakage = 0.0;
		
		for(LeakyVariable v: lv)
			leakage = leakage+v.getFastLeak();
		
		return leakage;
	}

	
	
	
	public String getOutput() {
		
		return output;
		
	}
	
	
	
	
	
}
