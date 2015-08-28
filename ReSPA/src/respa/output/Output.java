package respa.output;




/**
 * 
 * @author Joao Matos / GSD INESC-ID
 * 
 * Input -> ReSPA: Find Alternative input -> Output
 *
 */
public interface Output {

	
	public void printToSystemin();
	
	
	public void printToFile(String file);
	
	
	public int getResidue();
	
	public double getLeakage();
	
	
}
