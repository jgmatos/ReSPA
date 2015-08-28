package respa.output.solve;

import java.util.HashMap;

import respa.input.InputVariable;
import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
import respa.main.ReSPAConfig;
import respa.output.IndividualTokensOuput;
import respa.output.Output;


/**
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 * 
 * Solve the path condition obtained by our search, extract
 * new input and build an output
 * 
 * If the SUT is a console application
 *
 */

public class ConsoleSolver extends Solver{

	
	
	
	
	public ConsoleSolver() {}

	
	
	
	
	
	@Override
	public Output construct(HashMap<String, InputVariable> input) {
		
		IndividualTokensOuput output = new IndividualTokensOuput(ReSPAConfig.input);
		for(InputVariable iv: input.values()/*Core.symbvars.values()*/){
			if(iv instanceof SymbolicInputString){
				SymbolicInputString sis = (SymbolicInputString)iv;
				sis.setOffset(sis.getOffset());
				if(sis.solution==null){
					sis.solution=new char[1];
					sis.solution[0]='x';
				}
				output.add(sis);
			//	System.out.println("banhada: "+sis+" ;; "+(new String(sis.solution)));
			}
			else if(iv instanceof SymbolicInputInt) {

				SymbolicInputInt sii = (SymbolicInputInt)iv;
				output.add(sii);
				
			}
		}
		
		
		return output;
		
	}

}
