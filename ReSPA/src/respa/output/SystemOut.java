package respa.output;

import java.io.FileReader;
import java.util.Properties;


/**
 * 
 * @author Joao Gouveia de Matos / GSD INESC-ID
 * 
 * This class controls what ReSPA prints in the console
 *
 */
@Deprecated
public class SystemOut {

	
	/**
	 * Print everything!
	 * 
	 * If true, the rest of the properties do not matter
	 */
	public static boolean debug;
	
	
	
	
	
	
	/**
	 * RECOMENDED
	 */
	
	
	
	
	
	/**
	 * Print created symbolic variables
	 */
	public static boolean print_new_symb=true;
	
	
	/**
	 * Let us know when the current phase finishes and when a new phase begins
	 */
	public static boolean print_new_phase=true;
	
	
	/**
	 * Print loading when ReSPA starts
	 */
	public static boolean print_loading=true;
	
	
	
	
	
	
	
	
	
	/**
	 * Unimportant
	 */
	
	
	/**
	 * print path conditions
	 */
	public static boolean print_path_conditions=false;;

	/**
	 * print new input generated
	 */
	public static boolean print_new_input=false;;

	/**
	 * print time stamps
	 */
	public static boolean print_timestamps=false;;

	/**
	 * print residue results
	 */
	public static boolean print_residue=false;;

	/**
	 * print leak (not yet available)
	 */
	public static boolean print_leak=false;;

	/**
	 * store and restore
	 */
	public static boolean print_store_restore=false;;

	
	
	
	
	
	
	
	
	
	
	/**
	 * the console output will become excessively verbose with these options
	 * used for debug purposes only
	 */
	

	/**
	 * print decision at every branch
	 */
	public static boolean print_decisions=false;

	/**
	 * print current constraint
	 */
	public static boolean print_constraints=false;

	/**
	 * print user input when ReSPA starts
	 */
	public static boolean print_input=false;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void load() {
		load(System.getProperty("user.dir")+"/systemout.properties");
	}
	
	
	
	
	public static void load(String file) {
		
		try {
		
			Properties properties = new Properties();
		
			properties.load(new FileReader(file));
			
			debug = Boolean.valueOf(properties.getProperty("debug"));
			
			if(debug)
				debug();
			else{
				
				print_new_symb=Boolean.valueOf(properties.getProperty("print_new_symb"));

				print_new_phase=Boolean.valueOf(properties.getProperty("print_new_phase"));

				print_loading=Boolean.valueOf(properties.getProperty("print_loading"));
				
				print_path_conditions=Boolean.valueOf(properties.getProperty("print_path_conditions"));

				print_new_input=Boolean.valueOf(properties.getProperty("print_new_input"));

				print_timestamps=Boolean.valueOf(properties.getProperty("print_timestamps"));

				print_residue=Boolean.valueOf(properties.getProperty("print_residue"));

				print_leak=Boolean.valueOf(properties.getProperty("print_leak"));

				print_store_restore=Boolean.valueOf(properties.getProperty("print_store_restore"));
				
				print_decisions=Boolean.valueOf(properties.getProperty("print_decisions"));

				print_constraints=Boolean.valueOf(properties.getProperty("print_constraints"));

				print_input=Boolean.valueOf(properties.getProperty("print_input"));
				
			}
				
			
		} catch (Exception e) {
			defaultOptions();
		}
		
	}
	
	
	
	public static void debug() {
		
		
		print_new_symb=true;

		print_new_phase=true;

		print_loading=true;
		
		print_path_conditions=true;

		print_new_input=true;

		print_timestamps=true;

		print_residue=true;

		print_leak=true;

		print_store_restore=true;
		
		print_decisions=true;

		print_constraints=true;

		print_input=true;
		
	}
	
	public static void defaultOptions() {
		
		print_new_symb=true;

		print_new_phase=true;

		print_loading=true;
		
		print_path_conditions=false;

		print_new_input=false;

		print_timestamps=false;

		print_residue=false;

		print_leak=false;

		print_store_restore=false;
		
		print_decisions=false;

		print_constraints=false;

		print_input=false;
		
		
	}
	
	
	
}
