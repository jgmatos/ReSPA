package respa.output;

import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.HashMap;
import java.util.Random;

import respa.input.InputVariable;
import respa.input.SymbolicInputInt;
import respa.input.SymbolicInputString;
import respa.leak.MultiLeakyPath;
import respa.main.ReSPAConfig;
import respa.output.solve.ConsoleSolver;
import respa.output.solve.Solver;
import respa.output.solve.XMLSolver;



/**
 * Generate new input
 * 
 * @author Joao Matos / GSD INESC-ID 
 *
 */
public class OutputManager {






	private String outputFile;




	public OutputManager() {

		try{




			outputFile = System.getProperty("user.dir")+"/.respa/tmp/altenativeInput.txt";



		}
		catch(Exception e) {

			e.printStackTrace();

		}


	}









	public void outputleaky(PathCondition pc) {

		Solver solver;
		Output output;



		if(ReSPAConfig.inputType.equals("xml")){
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
		output.printToSystemin();

		if(ReSPAConfig.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
			OutputManager.residue = residue;
			OutputManager.residuePercent = (double)((Double.valueOf(residue)/Double.valueOf(ReSPAConfig.totalChars))*100);
		}

		System.out.println("[ReSPA][OutputManager] -->  Leakage: "+rleak+" bits; "+(rleak/8)+" Bytes; "+(rleak/(8.0*((double)ReSPAConfig.totalBytes)))*100.0+"%");
		OutputManager.rleak=rleak;
		OutputManager.rleakPercent=(rleak/(8.0*((double)ReSPAConfig.totalBytes)))*100.0;

	}

	public static double rleak=0.0;
	public static double rleakPercent=0.0;
	public static double residue = 0.0;
	public static double residuePercent = 0.0;




	public void outputleaky() {

		Solver solver;
		Output output;
		//HashMap<String, InputVariable> map;


		PathCondition outputpath =  ReSPAConfig.allPaths.get(ReSPAConfig.allPaths.size()-1);

		if(ReSPAConfig.inputType.equals("xml")){
			solver = new XMLSolver();
		    solver.solve(outputpath);
		}
		else{ 
			solver = new ConsoleSolver();
			solver.solve(outputpath);
		}



		double leak = solver.getLeakyPath().getFastLeak();

		output = new LeakyOutput(solver.getLeakyPath().getLeakyVars());

		output.printToFile(outputFile);
			output.printToSystemin();

		if(ReSPAConfig.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
		}

		System.out.println("[ReSPA][OutputManager] -->  Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8.0*((double)ReSPAConfig.totalBytes)))*100.0+"%");

	}



	public void outputsimple() {

		Solver solver;
		Output output;
		HashMap<String, InputVariable> map;


		PathCondition outputpath = ReSPAConfig.allPaths.get(0);

		if(ReSPAConfig.inputType.equals("xml")){
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



		outputpath = ReSPAConfig.allPaths.get(ReSPAConfig.allPaths.size()-1);

		if(ReSPAConfig.inputType.equals("xml")){
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
			output.printToSystemin();



		double leak = solver.getLeakyPath().getFastLeak();

		if(ReSPAConfig.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
		}

		System.out.println("[ReSPA][OutputManager] -->  Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8.0*((double)ReSPAConfig.totalBytes)))*100.0+"%");
		System.out.println("[ReSPA][OutputManager] --> OIA Leakage: "+oialeak+" bits; "+(oialeak/8)+" Bytes; "+(oialeak/(8.0*((double)ReSPAConfig.totalBytes)))*100.0+"%");

	}





	public void outputnew() {


		Solver solver;
		Output output;
		HashMap<String, InputVariable> map;
		//double additionalleak = 0.0;


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

		outputpath = ReSPAConfig.allPaths.get(pathsorter.nextInt(ReSPAConfig.allPaths.size()));



		//create the output
		if(ReSPAConfig.inputType.equals("xml")){
			solver = new XMLSolver();
			map = solver.solve(outputpath);
			output = (XMLOutput)solver.construct(map);
		//	additionalleak = ((XMLOutput)output).getBitsNonAnonymized();
		}
		else{ 
			solver = new ConsoleSolver();
			map = solver.solve(outputpath);
			output = (IndividualTokensOuput)solver.construct(map);
			//if(ReSPAConfig.residue_delimiters)
			//	additionalleak=ReSPAConfig.delimiters;
		}



		//print the output: uncomment this later
		output.printToFile(outputFile);
			output.printToSystemin();



		if(ReSPAConfig.residue){
			int residue = output.getResidue();
			System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
			//Logger.addResidue(residue);
			//Logger.addResiduePercentage((double)((Double.valueOf(residue)/Double.valueOf(Core.totalChars))*100));	
		}



		//measure leak
		if(ReSPAConfig.measure_leak){

			MultiLeakyPath mlp = new MultiLeakyPath();
			double leak;



			//first measure for the oia phase only
			if(ReSPAConfig.inputType.equals("xml"))
				solver = new XMLSolver();
			else
				solver = new ConsoleSolver();
			map = solver.solve(ReSPAConfig.allPaths.get(0));
			if(ReSPAConfig.var_type.equals("int"))
				leak = solver.getLeakyPath().getLeakage();
			else
				leak = solver.getLeakyPath().getFactorizedLeakage();


			//			leak += (additionalleak*8);
			//	Logger.addLeakOIA(leak);
			//	Logger.addLeakPercentageOIA((leak/(8.0*((double)Core.totalBytes)))*100.0);	

			//System.out.println("under test: "+solver.getLeakyPath().getLeakage()+" ;; "+solver.getLeakyPath().getFactorizedLeakage());

			mlp.add(solver.getLeakyPath());

			//now measure for the union of all paths found
			for(int i=1;i<ReSPAConfig.allPaths.size(); i++) {
				if(ReSPAConfig.inputType.equals("xml"))
					solver = new XMLSolver();
				else
					solver = new ConsoleSolver();

				map = solver.solve(ReSPAConfig.allPaths.get(i));

				rleak = solver.getLeakyPath().getFactorizedLeakage();

				mlp.add(solver.getLeakyPath());
			}

			if(ReSPAConfig.var_type.equals("int"))
				leak = mlp.getLeakage();
			else
				leak = mlp.getFactorizedLeak();//mlp.getFactorizedLeak();

			if(leak == 0.0)
				leak = solver.getLeakyPath().getFastLeak();

			//			leak += (additionalleak*8);
			//		Logger.addLeak(leak);
			//		Logger.addLeakPercentage((leak/(8.0*((double)Core.totalBytes)))*100.0);
			System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8.0*((double)ReSPAConfig.totalBytes)))*100.0+"%");
			System.out.println("[ReSPA][OutputManager] --> Paths found: "+ReSPAConfig.allPaths.size());

		}


	}
















	public void output() {


		Solver solver;


		if(ReSPAConfig.inputType.equals("xml")){

			XMLSolver oiaSolver = new XMLSolver();
			HashMap<String, InputVariable> mapOIA = oiaSolver.solve(ReSPAConfig.previousPathCondition);
			MultiLeakyPath mlp = new MultiLeakyPath();
			mlp.add(oiaSolver.getLeakyPath());

			XMLOutput output;

			if(ReSPAConfig.currentPathCondition!=null) {

				solver = new XMLSolver();
				HashMap<String, InputVariable> map = solver.solve(ReSPAConfig.currentPathCondition);
				mlp.add(solver.getLeakyPath());




				output = (XMLOutput)solver.construct(map);

					output.printToSystemin();

				output.printToFile(outputFile);

				if(ReSPAConfig.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
				}


			}
			else {

				output = (XMLOutput)oiaSolver.construct(mapOIA);

				if(ReSPAConfig.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
				}

			}

			if(ReSPAConfig.measure_leak){

				double leak=mlp.getLeakage();

				leak+=output.getBitsNonAnonymized();
				System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8*ReSPAConfig.totalBytes))*100+"%");


		//		double oiaLeak = (oiaSolver.getLeakyPath().getLeakage()+output.getBitsNonAnonymized());
			}





		}
		else if(ReSPAConfig.inputType.equals("console")) {

			ConsoleSolver oiaSolver = new ConsoleSolver();
			HashMap<String, InputVariable> mapOIA = oiaSolver.solve(ReSPAConfig.previousPathCondition);
			MultiLeakyPath mlp = new MultiLeakyPath();
			mlp.add(oiaSolver.getLeakyPath());


			if(ReSPAConfig.currentPathCondition!=null){

				solver = new ConsoleSolver();
				HashMap<String, InputVariable> map = solver.solve(ReSPAConfig.currentPathCondition);
				mlp.add(solver.getLeakyPath());

				IndividualTokensOuput output = (IndividualTokensOuput)solver.construct(map);

				if(ReSPAConfig.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
				}

			}
			else{//LM phase not successful

				IndividualTokensOuput output = (IndividualTokensOuput)oiaSolver.construct(mapOIA);

				if(ReSPAConfig.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
				}

			}


			if(ReSPAConfig.measure_leak){
				double leak = mlp.getLeakage();
				if(ReSPAConfig.residue_delimiters)
					leak+=ReSPAConfig.delimiters;

				System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8*ReSPAConfig.totalBytes))*100+"% ");

			//	double oiaLeak = (oiaSolver.getLeakyPath().getLeakage()+ReSPAConfig.delimiters);

			}




		}
		else if(ReSPAConfig.inputType.equals("txt")) {



			ConsoleSolver oiaSolver = new ConsoleSolver();
			HashMap<String, InputVariable> mapOIA = oiaSolver.solve(ReSPAConfig.previousPathCondition);
			MultiLeakyPath mlp = new MultiLeakyPath();
			mlp.add(oiaSolver.getLeakyPath());



			if(ReSPAConfig.currentPathCondition!=null){

				solver = new ConsoleSolver();
				HashMap<String, InputVariable> map = solver.solve(ReSPAConfig.currentPathCondition);
				mlp.add(solver.getLeakyPath());

				IndividualTokensOuput output = (IndividualTokensOuput)solver.construct(map);

				if(ReSPAConfig.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
				}

			}
			else{//LM phase not successful

				IndividualTokensOuput output = (IndividualTokensOuput)oiaSolver.construct(mapOIA);

				if(ReSPAConfig.residue){
					int residue = output.getResidue();
					System.out.println("[ReSPA][OutputManager] --> Residue: "+residue+"; "+(residue/Double.valueOf(ReSPAConfig.totalChars))*100+"%");
				}

			}


			if(ReSPAConfig.measure_leak){
				double leak = mlp.getLeakage();
				System.out.println("[ReSPA][OutputManager] --> Leakage: "+leak+" bits; "+(leak/8)+" Bytes; "+(leak/(8*ReSPAConfig.totalBytes))*100+"% ");

		//		double oiaLeak = (oiaSolver.getLeakyPath().getLeakage());

			}



		}

		//TODO: other types




	}
















	@SuppressWarnings({"unused", "deprecation"})
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
