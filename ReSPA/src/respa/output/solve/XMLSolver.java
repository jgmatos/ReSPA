package respa.output.solve;


import java.util.HashMap;

import respa.input.CharUtils;
import respa.input.InputVariable;
import respa.input.SymbolicInputString;
import respa.main.ReSPAConfig;
import respa.output.Output;
import respa.output.XMLOutput;


/**
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 * 
 * Solve the path condition obtained by our search, extract
 * new input and build an output
 *
 * If the SUT receives xml files as input
 *
 */
public class XMLSolver extends Solver{

	

	public XMLSolver() {}

	
	
	
	@Override
	public Output construct(HashMap<String, InputVariable> input) {

		
		XMLOutput output = new XMLOutput(ReSPAConfig.input);
		for(InputVariable iv: input.values()){
			if(iv instanceof SymbolicInputString){
				SymbolicInputString sis = (SymbolicInputString)iv;
				if(sis.solution==null){
					sis.solution=new char[1];
					sis.solution[0]='x';
				}
				else if(CharUtils.nonVisibleChar(sis.solution[0])||CharUtils.unsupportedXmlChar(sis.solution[0])) {
					sis.solution[0]='x';
				}
				else if(sis.solution.length>3&&sis.solution[0]=='h'&&
						sis.solution[1]=='t'&&sis.solution[2]=='t'&&
						sis.solution[3]=='p') {
					sis.solution=new char[1];
					sis.solution[0]='x';
				}
				
				output.add(sis);
			}
		}



		
		return output;
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
