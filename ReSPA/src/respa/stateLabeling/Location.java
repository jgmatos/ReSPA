package respa.stateLabeling;

import gov.nasa.jpf.jvm.bytecode.Instruction;
import respa.stateLabeling.Location;

import java.io.Serializable;

/**
 *	This class labels a specific point in the code:
 *	class signature + line number
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class Location implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private String className;

	private int line = -1;



	private String representation;









	public Location(String className, int line){

		this.className = className.trim();

		this.line = line;

		refreshRepresentation();

	}


	public Location(String locationString) {


		if(!locationString.contains("[synthetic] [clinit]")&&!locationString.contains("[synthetic] [main]")){

			/*		String [] splits = locationString.split(":");

		if(splits[0].contains(".java"))
			this.className = splits[0].split(".j")[0];
		else
			this.className = splits[0];

		if(this.className.contains(new String("/")))
			this.className = this.className.replace("/",".");

		this.line = Integer.valueOf(splits[1]);

		this.representation = this.className+":"+this.line;//refreshRepresentation();*/

			String [] splits;

			if(locationString.contains(".java:"))
				splits= locationString.split(".java:");
			else
				splits = locationString.split(":");

			if(splits.length!=2){
				this.className = "empty";
				this.line = -1;
			}
			else {
				this.className = splits[0];

				if(this.className.contains(new String("/")))
					this.className = this.className.replace("/",".");

				this.line = Integer.valueOf(splits[1]);

				//TODO CHECK THIS!
				if(className.contains("$")){
					className = className.split("\\$")[0];
					//	System.out.println("happened "+className);
				}

			}

		}
		else {
			this.className = "empty";
			this.line = -1;
		}

		this.representation = this.className+":"+this.line;//refreshRepresentation();

	}


	//TODO TEST THIS
	public Location(Instruction insn) {

		new Location(insn.getFileLocation());

	}







	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		refreshRepresentation();
	}




	public int getLine() {
		return line;
	}

	public void setLine(int line) {

		this.line = line;
		refreshRepresentation();

	}






	@Override
	public boolean equals(Object other) {

		if(other instanceof Location)
			if(samePlace((Location)other))
				return true;

		return false;

	}

	@Override
	public int hashCode(){

		return (this.className+":"+String.valueOf(this.line)).hashCode();

	}

	@Override
	public String toString() {

		return this.representation;

	}








	public boolean samePlace(Location otherMile) {

		if(sameClass(otherMile) && 
				sameLine(otherMile))
			return true;

		return false;

	}


	public boolean sameClass(Location otherMile) {

		if(this.className.equals(otherMile.getClassName()))
			return true;

		return false;

	}


	public boolean sameLine(Location otherMile) {

		if(this.line==otherMile.getLine())
			return true;

		return false;


	}





	public void shiftDown() {

		this.line += 2;

		refreshRepresentation();

	}






	private void refreshRepresentation() {

		if(this.className!=null)
			this.representation = className;
		else
			this.representation = "?";

		this.representation = this.representation.concat(":");

		this.representation = this.representation.concat(String.valueOf(line));

	}


	public Location copy() {

		return new Location(String.valueOf(this.className),Integer.valueOf(line));

	}




}
