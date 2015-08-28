package respa.output;

import java.io.FileWriter;

import respa.input.CharUtils;
import respa.main.ReSPAConfig;


/**
 * 
 * @author Joao Matos / GSD INESC-ID
 * 
 * If the SUT is an application such a xml parser, the alternative
 * input must comply with xml structure
 *
 */
public final class XMLOutput extends BreakableOutput {

	public XMLOutput(char[] input) {
		super(input);
	}

	
	
	@Override
	public void printToFile(String file) {

		try{

			FileWriter fw = new FileWriter(file);
			String content ="";
			for(int i=0;i<input.size()-1;i++)
				content+=input.get(i).block;

			//the last block left can be anonymized except for special chars
			String lastblock = input.get(input.size()-1).block;
			char [] lastBlockArray = lastblock.toCharArray();


			for(int i=0;i<lastBlockArray.length;i++)
				if(!CharUtils.isXmlSpecialChar(lastBlockArray[i]))
					lastBlockArray[i]='x';

			lastblock = String.valueOf(lastBlockArray);
			content+=lastblock;

			String filtered = "";
			for(int i=0;i<content.length();i++)
				if(!CharUtils.unsupportedXmlChar(content.charAt(i)))
					filtered+=content.charAt(i);

			if(ReSPAConfig.inputType.equals("xml")){

				for(int i=0;i<filtered.length();i++)
					if(CharUtils.unsupportedXmlChar(filtered.charAt(i)))
						filtered.replace(filtered.charAt(i), 'x');
				
/*				for(int i=0;i<filtered.length();i++)
					if(!(CharUtils.isXmlSpecialChar(filtered.charAt(i))||
							CharUtils.isLetter(filtered.charAt(i))||
							CharUtils.isNumber(filtered.charAt(i))))*/
						filtered.replace('^', 'x');
				
			}

			fw.write(filtered);
			//fw.write(content.trim());

			fw.flush();
			fw.close();

		/*	if(SystemOut.debug){
				for(int i=0;i<content.length();i++)
					System.out.println("output["+i+"] = "+content.charAt(i)+" ("+((int)content.charAt(i))+")");
			}*/

		}
		catch(Exception e) {

		}
	}
	
	
	
	
	
	@Override
	public void printToSystemin(){

		System.out.println("[ReSPA][XMLOutput] --> Alternative input generated: ");
		
		for(int i=0;i<this.input.size()-1;i++)
			System.out.print(input.get(i).block);

		//the last block left can be anonymized except for special chars
		String lastblock = input.get(input.size()-1).block;
		char [] lastBlockArray = lastblock.toCharArray();


		for(int i=0;i<lastBlockArray.length;i++)
			if(!CharUtils.isXmlSpecialChar(lastBlockArray[i]))
				lastBlockArray[i]='x';

		lastblock = String.valueOf(lastBlockArray);
		System.out.print(lastblock);
		System.out.println("");

	}
	
	
	
	
	@Override
	public int getResidue() {


		int residue = 0;

		int size = 0;
		for(Block b:this.input)
			for(int l=0;l<b.block.length();l++)
				size++;

		//first part is easy:
		for(int i=0;i<this.input.size()-1;i++){
			Block dummy = this.input.get(i);
			if(!dummy.symbolic){//non anonymized blocks
				residue+=dummy.block.length();
			}
			else{//anonymized blocks
				for(int j=0;j<dummy.block.length()&&j<ReSPAConfig.input.length;j++){
					if(dummy.block.charAt(j)==ReSPAConfig.input[dummy.offset+j]){
						residue++;
					}
				}
			}
		}

		//last block:
		String lastblock = input.get(input.size()-1).block;
		char [] lastBlockArray = lastblock.toCharArray();

		for(int k=0;k<lastBlockArray.length;k++)
			if(CharUtils.isXmlSpecialChar(lastBlockArray[k]))
				residue++;


		return residue;

	}
	
	
	
	
	public double getBitsNonAnonymized() {
		
		int leak = 0;

		

		for(int i=0;i<this.input.size()-1;i++){
			Block dummy = this.input.get(i);
			if(!dummy.symbolic){//non anonymized blocks
				leak+=dummy.block.length();
			}
			
		}

		//last block:
		String lastblock = input.get(input.size()-1).block;
		char [] lastBlockArray = lastblock.toCharArray();

		for(int k=0;k<lastBlockArray.length;k++)
			if(CharUtils.isXmlSpecialChar(lastBlockArray[k]))
				leak++;


		return (double)leak*8.0;
		
		
	}
	
	
	
	
	
	
	
	
	
	
}
