package respa.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import respa.search.miser.input.ManualInputLocation;
import respa.search.miser.input.queuedInputString;
import respa.stateLabeling.Location;
import respa.stateLabeling.Trace;
import respa.stateLabeling.VerboseMile;


public class Loader {


	public static HashSet<String> getSources(String dir) {

		try {

			HashSet<String> sources = new HashSet<String>();
			Scanner scan = new Scanner(new File(dir));	

			while(scan.hasNextLine()) 
				sources.add(scan.nextLine().trim());


			scan.close();

			return sources;

		}
		catch(Exception e) {

			return null;

		}		

	}


	public static VerboseMile getCrashMilestone(String dir) {

		try {

			Scanner scan = new Scanner(new File(dir));	

			Location crashLocation = null;
			Trace crashTrace = null;

			crashLocation = new Location(scan.nextLine());
			String stringTrace = "(";
			while(scan.hasNextLine()) {
				stringTrace =stringTrace+scan.nextLine();
				stringTrace = stringTrace+" <<< ";
			}
			if(stringTrace.contains(" <<< ")) 
				stringTrace = stringTrace.substring(0,stringTrace.length()-5);

			stringTrace.trim();
			stringTrace = stringTrace+")";

			scan.close();
			crashTrace = new Trace(stringTrace);
			return new VerboseMile(crashLocation,crashTrace);

		}
		catch(Exception e) {
			return null;
		}



	}




	public static VerboseMile getStackTrace(String dir,HashSet<String> sources) {

		try {

			String content="",line="";
			Scanner scan = new Scanner(new File(dir));	
			while(scan.hasNextLine()){
				line = scan.nextLine();
				content=content+line+"\n";
				if(line.contains("Caused")||line.contains("Exception"))
					content="";
			}
			scan.close();

			scan = new Scanner(content);

			Location crashLocation = null;
			Trace crashTrace = null;
			line = scan.nextLine();
			line = processStackTraceLine(line);

			crashLocation = new Location(line);

			String stringTrace = "(",token ="";
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				if(line.contains("at ")&&line.contains(":")){
					token=processStackTraceLine(line);
					if(sources.contains(token.split(":")[0])){
						stringTrace =stringTrace+token;
						stringTrace = stringTrace+" <<< ";
					}
				}
			}
			if(stringTrace.contains(" <<< ")) 
				stringTrace = stringTrace.substring(0,stringTrace.length()-5);

			stringTrace.trim();
			stringTrace = stringTrace+")";

			scan.close();
			crashTrace = new Trace(stringTrace);
			return new VerboseMile(crashLocation,crashTrace);



		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}



	}



	public static String processStackTraceLine(String line) {

		String newline="";

		String number = line.substring(line.indexOf(":"),line.length()-1);

		newline = line.substring(line.indexOf("at ")+3);
		newline = newline.substring(0,newline.lastIndexOf("."));
		newline = newline.substring(0,newline.lastIndexOf("."));
		newline = newline+number;

		return newline;

	}





	public static HashSet<Location> getIgnoredLocations(String dir) {

		try {

			HashSet<Location> ignoredLocations = new HashSet<Location>();
			Scanner scan = new Scanner(new File(dir));

			while(scan.hasNextLine()) 
				ignoredLocations.add(new Location(scan.nextLine().trim()));

			scan.close();

			return ignoredLocations;


		} catch (FileNotFoundException e) {

			return null;

		}

	}






	public static HashMap<Location, InputLocation> getInputLocations(String dir) {

		try {


			Scanner sc = new Scanner(new File(dir));

			ArrayList<String> dummy = new ArrayList<String>();
			while(sc.hasNextLine()) 
				dummy.add(sc.nextLine());

			sc.close();

			String delimiter = " ";
			String [] splits;
			InputLocation il;
			HashMap<Location, InputLocation> inputLocationsSet = new HashMap<Location, InputLocation>();
			for(String s: dummy) {

				if(s.contains("&"))
					delimiter = "&";

				splits = s.split(delimiter);
				il = new InputLocation();
				il.representation = s;
				il.location = new Location(splits[2]);
				il.variableName = splits[0];
				il.variableType = splits[1];
				if(splits.length==4)
					il.value = splits[3];
				else{
					il.value="";
				}

				inputLocationsSet.put(il.location,il);

			}

			return inputLocationsSet;


		}
		catch(Exception e) {e.printStackTrace();
			return null;
		}


	}


	
	
	@Deprecated
	public static HashMap<Location, ManualInputLocation> getManualInputLocations(String dir) {
		try {


			Scanner sc = new Scanner(new File(dir));

			ArrayList<String> dummy = new ArrayList<String>();
			while(sc.hasNextLine()) 
				dummy.add(sc.nextLine());

			sc.close();

			String delimiter = " ";
			String [] splits;
			InputLocation il;
			HashMap<Location, ManualInputLocation> inputLocationsSet = new HashMap<Location, ManualInputLocation>();
			for(String s: dummy) {

				delimiter = ";;;";

				splits = s.split(delimiter);
				il = new InputLocation();
				il.representation = s;
				il.location = new Location(splits[2]);
				il.variableName = splits[0];
				il.variableType = splits[1];
				if(splits.length>=4)
					il.value = splits[3];


				ManualInputLocation mil = new ManualInputLocation();
				mil.setIl(il);
				for(int i=3;i<splits.length;i++)
					mil.add(new queuedInputString(il, splits[i],i-2));

				inputLocationsSet.put(il.location,mil);
			}

			return inputLocationsSet;
		
		}
		catch(Exception e) {
			return null;
		}

		
	}


	public static void getAWTFiles() {

		try {

			Runtime.getRuntime().exec("cp "+System.getProperty("user.home")+"/.reap/input.txt "+System.getProperty("user.dir"));

			//TODO: fix this issue asap
			Runtime.getRuntime().exec("cp "+System.getProperty("user.home")+"/.reap/awtscript.es "+System.getProperty("user.dir"));

		} catch (IOException e) {
			e.printStackTrace();
		}



	}





}
