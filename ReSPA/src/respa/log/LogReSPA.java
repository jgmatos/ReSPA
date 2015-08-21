package respa.log;

import java.io.FileReader;
import java.util.EnumMap;
import java.util.Properties;

public class LogReSPA extends Log{



	private static EnumMap<EntryType, Boolean> logOptions = new EnumMap<EntryType, Boolean>(EntryType.class);




	public static String optionsDir="";
	public static String outDir="";
	
	public static boolean evaluate=false;
	public static boolean verbose=false;
	

	public static void checkForOptions(String filename) {


		try {

			Properties options = new Properties();
			options.load(new FileReader(filename));

			if(options.contains("logPhiGet"))
				logOptions.put(EntryType.logPhiGet,Boolean.valueOf(options.getProperty("logPhiGet")));

			if(options.contains("logStartIteration"))
				logOptions.put(EntryType.logStartIteration,Boolean.valueOf(options.getProperty("logStartIteration")));

			if(options.contains("logEndIteration"))
				logOptions.put(EntryType.logEndIteration,Boolean.valueOf(options.getProperty("logEndIteration")));

			if(options.contains("logMaxIteration"))
				logOptions.put(EntryType.logEndIteration,Boolean.valueOf(options.getProperty("logEndIteration")));

			if(options.contains("logStepForward"))
				logOptions.put(EntryType.logStepForward,Boolean.valueOf(options.getProperty("logStepForward")));

			if(options.contains("logReproduced"))
				logOptions.put(EntryType.logReproduced,Boolean.valueOf(options.getProperty("logReproduced")));

			if(options.contains("logNotReproduced"))
				logOptions.put(EntryType.logNotReproduced,Boolean.valueOf(options.getProperty("logNotReproduced")));

			if(options.contains("logSuspect"))
				logOptions.put(EntryType.logSuspect,Boolean.valueOf(options.getProperty("logSuspect")));

			if(options.contains("logNewNm"))
				logOptions.put(EntryType.logNewNm,Boolean.valueOf(options.getProperty("logNewNm")));

			if(options.contains("logGBrecovered"))
				logOptions.put(EntryType.logGBrecovered,Boolean.valueOf(options.getProperty("logGBrecovered")));

			if(options.contains("logGBadded"))
				logOptions.put(EntryType.logGBadded,Boolean.valueOf(options.getProperty("logGBadded")));

			if(options.contains("logDijkstra"))
				logOptions.put(EntryType.logDijkstra,Boolean.valueOf(options.getProperty("logDijkstra")));

			if(options.contains("logPushNode"))
				logOptions.put(EntryType.logPushNode,Boolean.valueOf(options.getProperty("logPushNode")));

			if(options.contains("logPopNode"))
				logOptions.put(EntryType.logPopNode,Boolean.valueOf(options.getProperty("logPopNode")));

			if(options.contains("logUpdateNode"))
				logOptions.put(EntryType.logUpdateNode,Boolean.valueOf(options.getProperty("logUpdateNode")));

			if(options.contains("logOutOfR"))
				logOptions.put(EntryType.logOutOfR,Boolean.valueOf(options.getProperty("logOutOfR")));
			
			if(options.contains("evaluate"))
				logOptions.put(EntryType.evaluate,Boolean.valueOf(options.getProperty("evaluate")));
			
			if(options.contains("verbose"))
				logOptions.put(EntryType.verbose,Boolean.valueOf(options.getProperty("verbose")));

			logOptions.put(EntryType.logError,true);

		}
		catch(Throwable t) {
			setDefault();
			System.out.println("[ReSPA][LogReSPA][Warning]: there was a problem loading logging options file. Nothing will be logged.");
		}



	}



	private static void setDefault() {
			logOptions.put(EntryType.logPhiGet,false);

			logOptions.put(EntryType.logStartIteration,false);

			logOptions.put(EntryType.logEndIteration,false);

			logOptions.put(EntryType.logEndIteration,false);

			logOptions.put(EntryType.logStepForward,false);

			logOptions.put(EntryType.logReproduced,false);

			logOptions.put(EntryType.logNotReproduced,false);

			logOptions.put(EntryType.logSuspect,false);

			logOptions.put(EntryType.logNewNm,false);

			logOptions.put(EntryType.logGBrecovered,false);

			logOptions.put(EntryType.logGBadded,false);

			logOptions.put(EntryType.logDijkstra,false);

			logOptions.put(EntryType.logPushNode,false);

			logOptions.put(EntryType.logPopNode,false);

			logOptions.put(EntryType.logUpdateNode,false);

			logOptions.put(EntryType.logOutOfR,false);
		
			logOptions.put(EntryType.evaluate,false);
		
			logOptions.put(EntryType.verbose,false);
		
			logOptions.put(EntryType.logError,true);

	}
	
	

	public static void handleEvent(EntryType et,String event) {
		
		if(logOptions.get(et))
			log(logOptions.get(EntryType.verbose), "[ReSPA][event]["+et.toString()+"] "+event);
		
		
	}

	public static boolean evaluating() {
		
		return logOptions.get(EntryType.evaluate);
		
	}







}