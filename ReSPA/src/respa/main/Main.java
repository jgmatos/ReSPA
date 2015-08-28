package respa.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Properties;

public class Main {


	private static int EXPECTED_RESOURCES = 3;
	private static int MIN_ARGS = 2;
	private static int MAX_ARGS = 3;
	
	private static String target_dir="";
	private static String incident_dir="";

	public static void main(String [] args) {

		if(args.length<MIN_ARGS||args.length>MAX_ARGS){
			printUsage();
			System.exit(-1);
		}


		incident_dir = args[0];
		target_dir = args[1];


		try {

			createTMP();

		} catch (MissingResourceException e) {

			System.err.println("[ReSPA][Main] Missing Resources in "+args[0]);
			System.exit(-1);

		} catch (IOException e) {

			System.err.println("[ReSPA][Main] Failed to open files in "+args[0]);
			System.exit(-1);

		}


		if(args.length==2) 
			copyRespaProps(new File(System.getProperty("user.home")+"/.respa/respa.properties"));
		else
			copyRespaProps(new File(args[2]));

		try {
			launch();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}












	private static void printUsage() {



	}

	private static String getJPFdir() throws FileNotFoundException, IOException {

		Properties jpfsite = new Properties();
		jpfsite.load(new FileReader(System.getProperty("user.home")+"/.jpf/site.properties"));
		return jpfsite.getProperty("jpf-core");

	}


	private static void launch() throws IOException, InterruptedException {


		ProcessBuilder pb;

		System.out.println("[ReSPA][Main] Starting ReSPA... ");
		pb = new ProcessBuilder(
				"/Library/Java/JavaVirtualMachines/1.6.0_65-b14-462.jdk/Contents/Home/bin/java",
				"-Xmx2g",
				"-Dfile.encoding=UTF-8",
				"-jar",
				getJPFdir()+"/build/RunJPF.jar",
				"+shell.port=4242",
				incident_dir+"/ReSPA.jpf");


		//pb.directory(new File(System.getProperty("user.home")+"/.respa/tmp") );
		pb.directory(new File(target_dir) );

		
		Process process = pb.start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = "";

		while((line=br.readLine())!=null){
			System.err.println(line);
		}

		process.destroy();

	}


	private static void copyRespaProps(File respaprops) {

//		File dest = new File(System.getProperty("user.home")+"/.respa/tmp/respa.properties");
		File dest = new File(target_dir+"/.respa/tmp/respa.properties");

		try {

			copyFile(respaprops, dest);

		}
		catch (IOException e) {

			System.err.println("[ReSPA][Main] Failed to copy file "+respaprops+"      to      "+dest);
			System.exit(-1);

		}

	}



	private static void createTMP() throws MissingResourceException, IOException {


		//File tmp = new File(System.getProperty("user.home")+"/.respa/tmp");
		
		File respa = new File(target_dir+"/.respa");
		if(!respa.exists())
			respa.mkdir();
		
		File tmp = new File(respa.getAbsoluteFile()+"/tmp");
		
		String [] content;
		if(!tmp.exists())
			tmp.mkdir();
		else{
			content = tmp.list();
			for(String s: content)
				(new File(tmp.getAbsoluteFile()+"/"+s)).delete();
		}


		File incident = new File(incident_dir);
		content = incident.list();
		if(content.length<EXPECTED_RESOURCES)
			throw new MissingResourceException();

		for(String s: content)
			copyFile(new File(incident_dir+"/"+s),new File(tmp.getAbsoluteFile()+"/"+s));


	}

	private static void copyFile(File source, File dest)
			throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}






}
