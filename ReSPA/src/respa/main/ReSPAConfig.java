package respa.main;

import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;

import respa.input.InputBuffer;
import respa.input.InputVariable;
import respa.main.ReSPAConfig;
import respa.stateLabeling.Location;
import respa.utils.FileInInputLocation;
import respa.utils.InputLocation;

/**
 * 
 * @author Joao Gouveia de Matos /GSD INESC-ID
 * 
 * This class holds many variables shared by
 * 	the main classes of ReSPA
 *
 */
public class ReSPAConfig {



	///////////////////////////////       ReSPA       //////////////////////////////////

	public static int radius;





	public static int maxAttempts;


	public static boolean oia_sourcepath;

	///////////////////////////////       ReSPA       //////////////////////////////////







	///////////////////////////////       Input Detection       //////////////////////////////////


	public static boolean automaticInputDetection;



	public static char[] input;



	//public static ArrayList<InputBuffer> inputbuffers;
	public static HashMap <String,InputLocation> inputLocationsSet_;
	public static HashSet<InputLocation> alreadyCreated;

	public static HashMap<Location,FileInInputLocation> manualInputLocationsSet = new HashMap<Location, FileInInputLocation>();

	public static String inputType;

	public static InputBuffer inputBuffer;//lets assume only one input source
	public static ArrayList<InputBuffer> inputbuffers;//instead of several
	public static boolean singleInputSource;//should be true in most cases

	public static HashMap<String, InputVariable> symbvars;
	public static HashMap<String, InputVariable> symbvars_;
	public static boolean runningConcrete;

	public static boolean ignoreNumeric=false;
	public static boolean ignoreString=false;

	public static boolean ignoreNumericPC;
	public static boolean ignoreStringPC;


	public static String var_type = "string";


	public static int totalBytes=0;
	public static int totalChars=0;	
	///////////////////////////////       Input Detection       //////////////////////////////////


	public static boolean verbose;






	///////////////////////////////       Search       //////////////////////////////////



	public static ArrayList<PathCondition> allPaths = new ArrayList<PathCondition>();

	public static PathCondition currentPathCondition=null;
	public static PathCondition previousPathCondition=null;

	public static Properties properties;


	public static boolean symbString=true;
	public static boolean symbInt=true;











	public static long timeout;

	public static boolean intToChar=false;

	public static boolean boundMemory = false;
	public static long memoryBound = 1190695928;
	///////////////////////////////       Search       //////////////////////////////////






	///////////////////////////////       Output options       //////////////////////////////////




	public static boolean residue;

	public static boolean residue_delimiters;

	public static boolean measure_leak;

	///////////////////////////////       Output options       //////////////////////////////////







	///////////////////////////////       Crash info       //////////////////////////////////


	protected static String stackTrace;
	public static boolean F=false;


	///////////////////////////////       Crash info       //////////////////////////////////




































	public static void load() {

		try{


			properties=new Properties();
			properties.load(new FileReader(System.getProperty("user.dir")+"/.respa/tmp/respa.properties"));


			try{
				maxAttempts = Integer.valueOf(properties.getProperty("maxAttempts"));
			}
			catch(Exception e) {
				maxAttempts = Integer.MAX_VALUE;
			}



			String strR = properties.getProperty("radius");
			if(strR.equals("max"))
				ReSPAConfig.radius=Integer.MAX_VALUE;
			else
				ReSPAConfig.radius=Integer.valueOf(strR);




			try{
				ReSPAConfig.intToChar=Boolean.valueOf(properties.getProperty("intToChar"));
			}catch(Exception e){}


			////////////////    ReSPA    /////////////////////










			////////////////    Input Detection     /////////////////////
			automaticInputDetection = Boolean.valueOf(properties.getProperty("input_detection"));




			singleInputSource = true;//later we may consider to include several
			inputBuffer=null;
			inputbuffers = new ArrayList<InputBuffer>();


			symbvars = new HashMap<String, InputVariable>();
			symbvars_ = new HashMap<String, InputVariable>();
			runningConcrete = true;
			inputLocationsSet_ = new HashMap<String, InputLocation>();
			alreadyCreated = new HashSet<InputLocation>();
			inputType=properties.getProperty("input_type");

			var_type = properties.getProperty("var_type");
			if(!var_type.equals("string")&&!var_type.equals("int"))
				var_type = "string";

			loadInput();
			////////////////    Input Detection     /////////////////////


			////////////////    Search Options     /////////////////////
			verbose = Boolean.valueOf(properties.getProperty("verbose"));
			symbString = Boolean.valueOf(properties.getProperty("symb_string"));
			symbInt = Boolean.valueOf(properties.getProperty("symb_int"));


			ignoreString = Boolean.valueOf(ReSPAConfig.properties.getProperty("ignore_string"));
			ignoreNumeric= Boolean.valueOf(ReSPAConfig.properties.getProperty("ignore_numeric"));

			ignoreNumericPC =  Boolean.valueOf(ReSPAConfig.properties.getProperty("ignore_numeric_pc"));
			ignoreStringPC =  Boolean.valueOf(ReSPAConfig.properties.getProperty("ignore_string_pc"));

			try{
				timeout = Integer.valueOf(properties.getProperty("timeout"));
			}
			catch(Exception e) {
				timeout = 1800000;//10 minutes
			}


			try {
				boundMemory = Boolean.valueOf(ReSPAConfig.properties.getProperty("boundMemory"));
				memoryBound = Long.valueOf(ReSPAConfig.properties.getProperty("memoryBound"));
			}
			catch(Exception e) {
			}
			////////////////    Search Options     /////////////////////

			//////////////// 	Output options	   /////////////////////
			residue = Boolean.valueOf(properties.getProperty("residue"));
			residue_delimiters = Boolean.valueOf(properties.getProperty("residue_delimiters"));
			measure_leak = Boolean.valueOf(properties.getProperty("measure_leak"));
			//////////////// 	Output options	   /////////////////////


		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}


	}





	/**
	 * Clear all information about symbolic variables created so far
	 */
	public static void clearSymb() {
		symbvars.clear();
		symbvars_.clear();
		inputbuffers.clear();
		inputBuffer=null;

		inputLocationsSet_.clear();
		alreadyCreated.clear();
		BytecodeUtils.clearSymVarCounter();
	}








	/**
	 * Load the input
	 */
	private static void loadInput() {

		try {

			String concatInput="";
			File f;			

			f= new File(System.getProperty("user.dir")+"/.respa/tmp/input.txt");
			FileReader fr = new FileReader(f);
			input=new char[(int)f.length()];
			fr.read(input);
			fr.close();



			if(var_type.equals("int")) {
				Scanner scan = new Scanner(new FileReader(f));
				String line ="";
				while(scan.hasNextLine()){
					totalBytes+=4;
					line = scan.nextLine();
					totalChars+=line.length();
					concatInput = concatInput+line;
				}

			}
			else if(inputType.equals("console")||inputType.equals("txt")){

				Scanner scan = new Scanner(new FileReader(f));
				ArrayList<String> inputConsole = new ArrayList<String>();

				String token="";
				while(scan.hasNextLine()){
					token = scan.nextLine();
					inputConsole.add(token);
					concatInput = concatInput+token.trim();
					if(scan.hasNextLine())//always add a whitespace delimiter except for the last word.
						concatInput = concatInput+" ";
					totalBytes+=token.length();
					totalChars+=token.length();
				}

				if(residue_delimiters){
					totalChars+=(inputConsole.size()-1);
					totalBytes+=(inputConsole.size()-1);
				}

				scan.close();

			}
			else if(inputType.equals("xml")) {
				totalBytes=ReSPAConfig.input.length;
				totalChars=ReSPAConfig.input.length;
				concatInput=String.valueOf(ReSPAConfig.input);
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}


	}


	public static int delimiters=0;








}
