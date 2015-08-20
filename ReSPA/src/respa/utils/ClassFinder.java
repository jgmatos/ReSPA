package respa.utils;


import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author Joao Gouveia Matos /GSD INESC-ID
 * 
 * This class provides finds all class files in user.dir and
 * i) returns a hashset with their signatures, ii) creates a
 * txt file with these signatures in user.dir
 *
 */
public class ClassFinder {



	private static ArrayList<File> files = new ArrayList<File>();







	public static HashSet<String> getClasses() {

		search();//get the files

		HashSet<String> classes = new HashSet<String>();
		String dummy="";
		for(File f: files){
			//clean and add
			dummy = f.getAbsolutePath();
			dummy=dummy.replace(System.getProperty("user.dir"), "");
			dummy = dummy.replace("/bin/", "");
			dummy=dummy.replace(".class", "");
			dummy=dummy.replace("/", ".");
			classes.add(dummy.trim());
		}
		
		createFile(classes);//create the file

		return classes;

	}





	private static void search() {

		searchFiles(new File(System.getProperty("user.dir")));

	}

	private static void searchFiles(File dir) {

		FileFilter filterDirs = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		FileFilter filterFiles = new FileFilter() {
			public boolean accept(File file) {
				return !file.isDirectory();
			}
		};

		File [] subdirs = dir.listFiles(filterDirs);
		File [] subfiles = dir.listFiles(filterFiles);

		if(subfiles!=null)
			for(int i=0; i<subfiles.length;i++)
				if(subfiles[i].getName().contains(".class") && !subfiles[i].getName().contains("svn-base"))
					files.add(subfiles[i]);

		if(subdirs!=null)
			for(int j=0;j<subdirs.length;j++)
				searchFiles(subdirs[j]);

	}





	

	

	private static void createFile(HashSet<String> classes) {
		
		try {

			String destDirectory = System.getProperty("user.dir")+"/classNames.txt";
			FileWriter writter = new FileWriter(new File(destDirectory));
			for(String s:classes)
				writter.write(s+"\n");
			writter.flush();
			writter.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




}
