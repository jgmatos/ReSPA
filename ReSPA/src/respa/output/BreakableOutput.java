package respa.output;

import java.io.FileWriter;
import java.util.LinkedList;

import respa.input.SymbolicInputString;
import respa.main.Core;
import respa.output.BreakableOutput;


/**
 *	This class assists the anonymization process
 *
 *	It should be used only if the user input is the content of a file
 *	At this time we only support xml files
 *  Other types will be supported very soon
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class BreakableOutput implements Output{


	protected LinkedList<Block> input;




	public BreakableOutput(char [] input) {

		Block start = new Block(String.valueOf(input), false,0,input.length);

		this.input = new LinkedList<Block>();

		this.input.add(start);

	}




	public void add(SymbolicInputString sis) {

		Block symb = new Block(String.valueOf(sis.solution), true,sis.getOffset(),sis.getLength());

		Block dummy=null;
		for(Block b: this.input)
			if(b.contains(sis.getOffset())){
				dummy=b;
				break;
			}

		if(dummy!=null){

			Block[] splits = join(dummy,symb);
			LinkedList<Block> ll = new LinkedList<BreakableOutput.Block>();
			for(Block b: this.input){
				if(b.equals(dummy)){
					for(int i=0;i<splits.length;i++)
						ll.add(splits[i]);
				}
				else {
					ll.add(b);
				}
			}
			this.input = ll;

		}

	}




	/**
	 * @requires bigger.contains(smaller)=true in order to 
	 * function correctly
	 * 
	 * @param bigger The bigger block
	 * @param smaller The smaller block
	 * 
	 * @return The resulting splits
	 */
	public Block[] join(Block bigger,Block smaller) {

		Block [] splits = null;

		if(bigger.offset==smaller.offset && bigger.length==(smaller.offset+smaller.length)) {

			splits = new Block[1];
			splits[0] = smaller;

		}
		else if(bigger.offset==smaller.offset && bigger.length!=(smaller.offset+smaller.length)){

			splits = new Block[2];
			splits[0]= new Block(smaller.block, true, bigger.offset, smaller.length);
			splits[1]= new Block(bigger.block.substring(smaller.length), 
					false, bigger.offset+smaller.length, bigger.length-smaller.length);

		}
		else if(bigger.length==(smaller.offset+smaller.length) && bigger.offset!=smaller.offset){

			splits = new Block[2];
			splits[0]= new Block(bigger.block.substring(0,bigger.length-smaller.length), 
					false, bigger.offset, bigger.length-smaller.length);
			splits[1]= new Block(smaller.block, true, smaller.offset,smaller.length);

		}
		else {

			splits = new Block[3];
			splits[0]= new Block(bigger.block.substring(0,smaller.offset-bigger.offset), 
					false, bigger.offset, smaller.offset-bigger.offset);
			splits[1]= new Block(smaller.block, true, smaller.offset,smaller.length);
			splits[2]= new Block(bigger.block.substring(smaller.offset-bigger.offset+smaller.length,bigger.length),
					false, smaller.offset+smaller.length,(bigger.offset+bigger.length)-(smaller.offset+smaller.length));

		}

		return splits;

	}







	public void printToSystemin(){

		for(int i=0;i<this.input.size();i++)
			System.out.print(input.get(i).block);

		System.out.println("");

	}






	public void printToFile(String file) {

		try{

			FileWriter fw = new FileWriter(file);
			String content ="";
			for(int i=0;i<this.input.size();i++)
				content+=input.get(i).block;

			fw.write(content);

			fw.flush();
			fw.close();

			if(SystemOut.debug){
				for(int i=0;i<content.length();i++)
					System.out.println("output["+i+"] = "+content.charAt(i)+" ("+((int)content.charAt(i))+")");
			}

		}
		catch(Exception e) {

		}
	}







	public int getResidue() {


		int residue = 0;

/*		int size = 0;
		for(Block b:this.input)
			for(int l=0;l<b.block.length();l++)
				size++;*/

		//first part is easy:
		for(int i=0;i<this.input.size()-1;i++){
			Block dummy = this.input.get(i);
			if(!dummy.symbolic){//non anonymized blocks
				residue+=dummy.block.length();
			}
			else{//anonymized blocks
				for(int j=0;j<dummy.block.length()&&j<Core.input.length;j++){
					if(dummy.block.charAt(j)==Core.input[dummy.offset+j]){
						residue++;
					}
				}
			}
		}



		return residue;

	}








	protected class Block {



		public String block;


		public byte[] bytes;


		public boolean symbolic;


		public int offset;


		public int length;


		public Block(int offset,boolean symbolic) {
			this.offset = offset;
		}

		public Block(String block, boolean symbolic,int offset, int length) {

			this.block = block;
			this.symbolic = symbolic;
			this.offset = offset;
			this.length = length;
			bytes = new byte[length];

		}



		@Override
		public boolean equals(Object other) {

			if(other instanceof Block)
				if(offset==((Block)other).offset)
					return true;

			return false;

		}


		public boolean contains(int offset) {
			if(this.offset<=offset && (this.length+this.offset)>offset)
				return true;

			return false;

		}

		public boolean contains(Block other) {

			if(this.offset<=other.offset && (this.length+this.offset)>=(other.offset+other.length))
				return true;

			return false;

		}



	}





	@Override
	public double getLeakage() {
		// TODO Auto-generated method stub
		return 0;
	}


	
	
	
	
	
	
	
	
	

}
