package respa.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Log {

	
	protected static ArrayList<String> log = new ArrayList<String>();
	
	
	
	
	
	
	
	
	public static void log(String message) {
		
		log.add(message);
		
	}
	
	public static void verboseLog(String message) {
		
		log.add(message);
		System.out.println(message);
		
	}
	
	public static void log(boolean verbose, String message) {
		
		log.add(message);
		if(verbose)
			System.out.println(message);
		
	}
	
	public static void save(String fname) {
		
		try {
			
			FileWriter fw = new FileWriter(new File(fname));
			
			for(String s: log)
				fw.write(s+"\n");
			
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
}
