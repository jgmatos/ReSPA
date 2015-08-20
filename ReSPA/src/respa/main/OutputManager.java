package respa.main;

import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.HashMap;
import java.util.Random;

import respa.input.InputVariable;
import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
import respa.leak.MultiLeakyPath;
import respa.output.IndividualTokensOuput;
import respa.output.LeakyOutput;
import respa.output.Output;
import respa.output.XMLOutput;
import respa.solve.ConsoleSolver;
import respa.solve.Solver;
import respa.solve.XMLSolver;



/**
 * Generate new input
 * 
 * @author Joao Matos / GSD INESC-ID 
 *
 */
public class OutputManager {





	private String alternativeInputFile;
	private String alternativeInputFileExtension;
	private String alternativeInputDir;

	private String outputFile;




	public OutputManager() {

		try{

			this.alternativeInputFile = Core.properties.getProperty("alternative_input");
			this.alternativeInputFileExtension = Core.properties.getProperty("file_extension");
			this.alternativeInputDir = Core.properties.getProperty("alternative_input_dir");

			if(this.alternativeInputDir.equalsIgnoreCase("default"))
				this.alternativeInputDir = Core.target_project;


			outputFile = this.alternativeInputDir+"/"+this.alternativeInputFile+"."+this.alternativeInputFileExtension;



		}
		catch(Exception e) {

			e.printStackTrace();

		}


	}









	public void outputleaky(PathCondition pc) {

		Solver solver;
		Output output;



		if(Core.inputType.equals("xml")){
			solver = new XMLSolver();
			solver.solve(pc);
		}
		else{ 
			solver = new ConsoleSolver();
			solver.solve(pc);
		}




		double rleak = solver.getLeakyPath().getFastLeak();


		output = new LeakyOutput(solver.getLeakyPath().getLeakyVars());

		output.printToFile(outputFile);
		if(SystemOut.print_new_input)
			output.printToSystemin();

		if(Core.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
			OutputManager.residue = residue;
			OutputManager.residuePercent = (double)((Double.valueOf(residue)/Double.valueOf(Core.totalChars))*100);
		}

		System.out.println("[ReSPA][OutputManager] -->  Leakage: "+rleak+" bits; "+(rleak/8)+" Bytes; "+(rleak/(8.0*((double)Core.totalBytes)))*100.0+"%");
		OutputManager.rleak=rleak;
		OutputManager.rleakPercent=(rleak/(8.0*((double)Core.totalBytes)))*100.0;

	}

	public static double rleak=0.0;
	public static double rleakPercent=0.0;
	public static double residue = 0.0;
	public static double residuePercent = 0.0;




	public void outputleaky() {

		Solver solver;
		Output output;
		HashMap<String, InputVariable> map;


		PathCondition outputpath =  Core.allPaths.get(Core.allPaths.size()-1);

		if(Core.inputType.equals("xml")){
			solver = new XMLSolver();
			map = solver.solve(outputpath);
		}
		else{ 
			solver = new ConsoleSolver();
			map = solver.solve(outputpath);
		}
		//TODO: why do we need map?



		double leak = solver.getLeakyPath().getFastLeak();

		output = new LeakyOutput(solver.getLeakyPath().getLeakyVars());

		output.printToFile(outputFile);
		if(SystemOut.print_new_input)
			output.printToSystemin();

		if(Core.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
		}

		System.out.println("[ReSPA][OutputManager] -->  Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8.0*((double)Core.totalBytes)))*100.0+"%");

	}



	public void outputsimple() {

		Solver solver;
		Output output;
		HashMap<String, InputVariable> map;


		PathCondition outputpath = Core.allPaths.get(0);

		if(Core.inputType.equals("xml")){
			solver = new XMLSolver();
			map = solver.solve(outputpath);
			output = (XMLOutput)solver.construct(map);
		}
		else{ 
			solver = new ConsoleSolver();
			map = solver.solve(outputpath);
			output = (IndividualTokensOuput)solver.construct(map);
		}

		double oialeak = solver.getLeakyPath().getFastLeak();



		outputpath = Core.allPaths.get(Core.allPaths.size()-1);

		if(Core.inputType.equals("xml")){
			solver = new XMLSolver();
			map = solver.solve(outputpath);
			output = (XMLOutput)solver.construct(map);
		}
		else{ 
			solver = new ConsoleSolver();
			map = solver.solve(outputpath);
			output = (IndividualTokensOuput)solver.construct(map);
		}

		output.printToFile(outputFile);
		if(SystemOut.print_new_input)
			output.printToSystemin();



		double leak = solver.getLeakyPath().getFastLeak();

		if(Core.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
		}

		System.out.println("[ReSPA][OutputManager] -->  Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8.0*((double)Core.totalBytes)))*100.0+"%");
		System.out.println("[ReSPA][OutputManager] --> OIA Leakage: "+oialeak+" bits; "+(oialeak/8)+" Bytes; "+(oialeak/(8.0*((double)Core.totalBytes)))*100.0+"%");

	}





	public void outputnew() {


		Solver solver;
		Output output;
		HashMap<String, InputVariable> map;
		double additionalleak = 0.0;


		//sort a path amongs the ones found
		/*Random pathsorter = new Random();
		PathCondition outputpath;
		if(Core.allPaths.size()>1){
			int sortedpath = 1+ pathsorter.nextInt(Core.allPaths.size()-1); // we add 1 so that we never sort the original path
			outputpath = Core.allPaths.get(sortedpath);
		}
		else
			outputpath = Core.allPaths.get(0);*/
		Random pathsorter = new Random();
		PathCondition outputpath;

		outputpath = Core.allPaths.get(pathsorter.nextInt(Core.allPaths.size()));



		//create the output
		if(Core.inputType.equals("xml")){
			solver = new XMLSolver();
			map = solver.solve(outputpath);
			output = (XMLOutput)solver.construct(map);
			additionalleak = ((XMLOutput)output).getBitsNonAnonymized();
		}
		else{ 
			solver = new ConsoleSolver();
			map = solver.solve(outputpath);
			output = (IndividualTokensOuput)solver.construct(map);
			if(Core.residue_delimiters)
				additionalleak=Core.delimiters;
		}



		//print the output: uncomment this later
		output.printToFile(outputFile);
		if(SystemOut.print_new_input)
			output.printToSystemin();



		if(Core.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
			//Logger.addResidue(residue);
			//Logger.addResiduePercentage((double)((Double.valueOf(residue)/Double.valueOf(Core.totalChars))*100));	
		}



		//measure leak
		if(Core.measure_leak){

			MultiLeakyPath mlp = new MultiLeakyPath();
			double leak;



			//first measure for the oia phase only
			if(Core.inputType.equals("xml"))
				solver = new XMLSolver();
			else
				solver = new ConsoleSolver();
			map = solver.solve(Core.allPaths.get(0));
			if(Core.var_type.equals("int"))
				leak = solver.getLeakyPath().getLeakage();
			else
				leak = solver.getLeakyPath().getFactorizedLeakage();


			//			leak += (additionalleak*8);
			//	Logger.addLeakOIA(leak);
			//	Logger.addLeakPercentageOIA((leak/(8.0*((double)Core.totalBytes)))*100.0);	

			//System.out.println("under test: "+solver.getLeakyPath().getLeakage()+" ;; "+solver.getLeakyPath().getFactorizedLeakage());

			mlp.add(solver.getLeakyPath());

			//now measure for the union of all paths found
			for(int i=1;i<Core.allPaths.size(); i++) {
				if(Core.inputType.equals("xml"))
					solver = new XMLSolver();
				else
					solver = new ConsoleSolver();

				map = solver.solve(Core.allPaths.get(i));

				rleak = solver.getLeakyPath().getFactorizedLeakage();

				mlp.add(solver.getLeakyPath());
			}

			if(Core.var_type.equals("int"))
				leak = mlp.getLeakage();
			else
				leak = mlp.getFactorizedLeak();//mlp.getFactorizedLeak();

			if(leak == 0.0)
				leak = solver.getLeakyPath().getFastLeak();

			//			leak += (additionalleak*8);
			//		Logger.addLeak(leak);
			//		Logger.addLeakPercentage((leak/(8.0*((double)Core.totalBytes)))*100.0);
			System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8.0*((double)Core.totalBytes)))*100.0+"%");
			System.out.println("[ReSPA][OutputManager] --> Paths found: "+Core.allPaths.size());

		}


	}
















	public void output() {


		Solver solver;


		if(Core.inputType.equals("xml")){

			XMLSolver oiaSolver = new XMLSolver();
			HashMap<String, InputVariable> mapOIA = oiaSolver.solve(Core.previousPathCondition);
			MultiLeakyPath mlp = new MultiLeakyPath();
			mlp.add(oiaSolver.getLeakyPath());

			XMLOutput output;

			if(Core.currentPathCondition!=null) {

				solver = new XMLSolver();
				HashMap<String, InputVariable> map = solver.solve(Core.currentPathCondition);
				mlp.add(solver.getLeakyPath());




				output = (XMLOutput)solver.construct(map);

				if(SystemOut.print_new_input)
					output.printToSystemin();

				output.printToFile(outputFile);

				if(Core.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
				}


			}
			else {

				output = (XMLOutput)oiaSolver.construct(mapOIA);

				if(Core.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
				}

			}

			if(Core.measure_leak){

				double leak=mlp.getLeakage();

				leak+=output.getBitsNonAnonymized();
				System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8*Core.totalBytes))*100+"%");


				double oiaLeak = (oiaSolver.getLeakyPath().getLeakage()+output.getBitsNonAnonymized());
			}





		}
		else if(Core.inputType.equals("console")) {

			ConsoleSolver oiaSolver = new ConsoleSolver();
			HashMap<String, InputVariable> mapOIA = oiaSolver.solve(Core.previousPathCondition);
			MultiLeakyPath mlp = new MultiLeakyPath();
			mlp.add(oiaSolver.getLeakyPath());


			if(Core.currentPathCondition!=null){

				solver = new ConsoleSolver();
				HashMap<String, InputVariable> map = solver.solve(Core.currentPathCondition);
				mlp.add(solver.getLeakyPath());

				IndividualTokensOuput output = (IndividualTokensOuput)solver.construct(map);

				if(Core.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
				}

			}
			else{//LM phase not successful

				IndividualTokensOuput output = (IndividualTokensOuput)oiaSolver.construct(mapOIA);

				if(Core.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
				}

			}


			if(Core.measure_leak){
				double leak = mlp.getLeakage();
				if(Core.residue_delimiters)
					leak+=Core.delimiters;

				System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8*Core.totalBytes))*100+"% ");

				double oiaLeak = (oiaSolver.getLeakyPath().getLeakage()+Core.delimiters);

			}




		}
		else if(Core.inputType.equals("txt")) {



			ConsoleSolver oiaSolver = new ConsoleSolver();
			HashMap<String, InputVariable> mapOIA = oiaSolver.solve(Core.previousPathCondition);
			MultiLeakyPath mlp = new MultiLeakyPath();
			mlp.add(oiaSolver.getLeakyPath());



			if(Core.currentPathCondition!=null){

				solver = new ConsoleSolver();
				HashMap<String, InputVariable> map = solver.solve(Core.currentPathCondition);
				mlp.add(solver.getLeakyPath());

				IndividualTokensOuput output = (IndividualTokensOuput)solver.construct(map);

				if(Core.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
				}

			}
			else{//LM phase not successful

				IndividualTokensOuput output = (IndividualTokensOuput)oiaSolver.construct(mapOIA);

				if(Core.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(Core.totalChars))*100+"%");
				}

			}


			if(Core.measure_leak){
				double leak = mlp.getLeakage();
				System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8*Core.totalBytes))*100+"% ");

				double oiaLeak = (oiaSolver.getLeakyPath().getLeakage());

			}



		}

		//TODO: other types




	}

















	private double getLeakage(HashMap<String, InputVariable> map) {

		double leak = 0.0;

		SymbolicInputString sis;
		SymbolicInputInt sii;
		for(InputVariable iv: map.values()){

			if(iv instanceof SymbolicInputString) {

				sis = (SymbolicInputString)iv;
				if(sis.leakMeasure!=null)
					leak+=sis.leakMeasure.bitsLeaked();

			}
			else if(iv instanceof SymbolicInputInt) {

				sii = (SymbolicInputInt)iv;
				if(sii.leakMeasure!=null)
					leak+=sii.leakMeasure.bitsLeaked();

			}

		}


		return leak;

	}










}
