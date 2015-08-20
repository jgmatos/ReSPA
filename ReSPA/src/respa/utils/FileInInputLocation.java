package respa.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import respa.main.Core;
import respa.search.miser.input.queuedInput;
import respa.search.miser.input.queuedInputInt;
import respa.search.miser.input.queuedInputString;
import respa.stateLabeling.Location;

public class FileInInputLocation extends InputLocation{


//	private PriorityQueue<queuedInput> queue = new PriorityQueue<queuedInput>();

	private LinkedList<queuedInput> queue = new LinkedList<queuedInput>();

	
	
	public FileInInputLocation(InputLocation il) {
		super();
		location=il.location;
		method = il.method;
		mile=il.mile;
		readerClass=il.readerClass;
		representation=il.representation;
		value="";
		variableName=il.variableName;
		variableType=il.variableType;
	}

	public void addLast(queuedInput qi) {
		queue.add(qi);
	}

	public void addLast(InputLocation il,String value) {
		addLast(new queuedInputString(il,value,queue.size()));
	}

	public void addLast(InputLocation il,int value) {
		addLast(new queuedInputInt(il,value,queue.size()));
	}

	
	
	
	public void addFirst(queuedInput qi) {
		queue.addFirst(qi);
	}
	
	public void addFirst(InputLocation il,String value) {
		addFirst(new queuedInputString(il,value,queue.size()));
	}
	
	public void addFirst(InputLocation il,int value) {
		addFirst(new queuedInputInt(il,value,queue.size()));
	}
	
	
	public int sizeQueue() {
		return queue.size();
	}


	public boolean isEmptyQueue() {
		return queue.isEmpty();
	}

	
	public queuedInput get(int i) {
		
		if(queue.size()<=i)
			return null;
		
		return queue.get(i);
	}
	
	
	
	
	

	public static FileInInputLocation FileIn_lines(File f) throws FileNotFoundException {



		Scanner sc = new Scanner(f);
		String delimiter = " ";
		String [] splits;
		InputLocation il;
		String firstLine=sc.nextLine();

		splits = firstLine.split(delimiter);
		il = new InputLocation();
		il.representation = firstLine;
		il.location = new Location(splits[2]);
		il.variableName = splits[0];
		il.variableType = splits[1];
		il.value="";

		FileInInputLocation fil = new FileInInputLocation(il);		

		int i=0;
		while(sc.hasNextLine()) 
			fil.addLast(new queuedInputString(il, sc.nextLine(),i++));
		sc.close();

		return fil;

	}


	public static FileInInputLocation FileIn_tokens(File f) throws FileNotFoundException {



		Scanner sc = new Scanner(f);

		String delimiter = " ";
		String [] splits;
		InputLocation il;
		String firstLine=sc.nextLine();

		splits = firstLine.split(delimiter);
		il = new InputLocation();
		il.representation = firstLine;
		il.location = new Location(splits[2]);
		il.variableName = splits[0];
		il.variableType = splits[1];
		il.value="";

		FileInInputLocation fil = new FileInInputLocation(il);		

		int i=0;
		while(sc.hasNext()) 
			fil.addLast(new queuedInputString(il, sc.next(),i++));
		sc.close();

		return fil;

	}

	public static FileInInputLocation FileIn_chars(File f) throws IOException {



		Scanner sc = new Scanner(f);
		String delimiter = " ";
		String [] splits;
		InputLocation il;
		String firstLine=sc.nextLine();

		splits = firstLine.split(delimiter);
		il = new InputLocation();
		il.representation = firstLine;
		il.location = new Location(splits[2]);
		il.variableName = splits[0];
		il.variableType = splits[1];
		il.value="";

		FileInInputLocation fil = new FileInInputLocation(il);		
		sc.close();

		FileReader fr = new FileReader(f);
		int c=0;
		
		while((c=fr.read())>=0 ){
			if(c==10)
				break;
		}//skip first line
		
		while((c=fr.read())>=0){
			fil.addLast(fil, c);
		}
		fr.close();
		
		
		return fil;
	}




	public static boolean checkFileIn() {

		File f = new File(Core.target_project+"/FileIn");
		if(f.exists())
			return true;

		return false;

	}

	public static HashMap<Location, FileInInputLocation> extractFileIn() throws IOException {

		if(!checkFileIn())
			return null;

		File f = new File(Core.target_project+"/FileIn");
		File [] list= f.listFiles();
		FileInInputLocation fil;
		
		HashMap<Location, FileInInputLocation> filein = new HashMap<Location, FileInInputLocation>();
		
		for(File fl:list) {
			if(fl.getName().contains("tokens")){
				fil = FileInInputLocation.FileIn_tokens(fl);
				
			}
			else if(fl.getName().contains("lines")) {
				fil = FileInInputLocation.FileIn_lines(fl);
			}
			else if(fl.getName().contains("chars")) {
				fil = FileInInputLocation.FileIn_chars(fl);				
			}
			else
				fil = FileInInputLocation.FileIn_lines(fl);
			filein.put(fil.location, fil);
		}
		
		return filein;
		
	}


}

