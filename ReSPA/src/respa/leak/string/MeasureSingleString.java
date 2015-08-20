package respa.leak.string;

import java.util.ArrayList;
import java.util.HashSet;

@Deprecated
public class MeasureSingleString {

	
	private String token;
	
	private HashSet<Integer> accessed;
	
	private ArrayList<MeasureSingleChar> string ;
	
	
	
	
	
	
	public MeasureSingleString(String token) {
		
		this.token = token;
		string = new ArrayList<MeasureSingleChar>(this.token.length());

		for(int i=0; i<token.length();i++)
			string.add(new MeasureSingleChar(token.charAt(i)));
		
		applyEquals(token);
		accessed = new HashSet<Integer>();
		
		
	}
	
	

	
	public void applyEquals(String other) {

		int i=0;
		for(i=0;i<string.size() && i<other.length(); i++){
			string.get(i).applyEquals(other.charAt(i));
			if(accessed!=null)
				accessed.add(i);
		}
		
//		for(;i<string.size();i++)
	//		string.get(i).totalRelaxation();
		
	}
	
	
	public void applyEquals(char c, int index) {
		
		if(index<string.size()){
			string.get(index).applyEquals(c);
			if(accessed!=null)
				accessed.add(index);
		}
		
	}
	
	
		
	public void applyNotEquals(String other) {
		
		for(int i=0;i<string.size() && i<other.length(); i++)
			string.get(i).applyNotEquals(other.charAt(i));
		
	}
	
	
	
	public void applyStartswith(String other) {
		
		//same as equals except it only goes until other.length()
		int i=0;
		for(i=0;i<other.length(); i++){
			string.get(i).applyEquals(other.charAt(i));
			if(accessed!=null)
				accessed.add(i);
		}
		
	}
	
	public void applyNotStartswith(String other) {
		
		for(int i=0;i<other.length(); i++)
			string.get(i).applyNotEquals(other.charAt(i));
		
	}
	
	
	
	public void totalRelaxation() {
		
		for(int i=0;i<string.size(); i++)
			string.get(i).totalRelaxation();
		
	}
	
	
	public double bitsLeaked() {
		
		double bitsLeaked = 0.0;
		
		for(int i=0;i<string.size(); i++){
			
			if(!accessed.contains(i))
				string.get(i).totalRelaxation();
				
			bitsLeaked+=string.get(i).bitsLeaked();
		
		}
		
		return bitsLeaked;
		
	}
	
	
	
	
	
	
	
	
	
	
}
