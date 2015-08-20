package respa.stateLabeling;

import respa.stateLabeling.Milestone;
import respa.stateLabeling.StateLabel;

import java.io.Serializable;




/**
 *	This class labels a JPF state
 * 
 * @author Joao Matos / GSD INESC-ID
 *
 */
public class StateLabel implements Serializable{











	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;









	private Milestone begin;



	private Milestone crucial;



	private String constraint=null;



	
	
	
	
	
	
	/**
	 * Sometimes a linkedlist helps to keep track of the paths
	 */
	private StateLabel next = null;
	
	
	
	public StateLabel prev = null;
	
	
	
	
	
	



	public StateLabel(Milestone crucial, Milestone begin) {

		setCrucial(crucial);
		this.begin = begin;

	}







	public void setCrucial(Milestone crucial) {

		this.crucial = crucial;

	}


	
	public StateLabel(Milestone crucial, Milestone begin,String constraint) {
		
		setCrucial(crucial);
		this.begin = begin;
		this.constraint=constraint;
		
	}

	
	
	
	
	
	
	
	public Milestone getBegin() {
		return begin;
	}



	public void setBegin(Milestone begin) {
		this.begin = begin;

		if(this.begin ==null)
			this.begin = this.crucial();

	}


	public Milestone crucial() {

		return this.crucial;

	}







	public String getConstraint() {
		return constraint;
	}


	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}



	




	
	
	
	
	
	
	
	
	
	
	
	
	



	@Override
	public String toString() {

		String s = "BB-";//Basic Block

		if(this.begin != null)
			s = s.concat(this.begin.toString());
		else
			s.concat("?");

		s = s.concat("-");
		s = s.concat(this.crucial.toString());
		s = s.concat("-");

		return s;

	}


	@Override
	public int hashCode() {

		if(constraint==null)
			return (String.valueOf(this.begin)+"-"+String.valueOf(this.crucial)).hashCode();
		else
			return (String.valueOf(this.begin)+"-"+String.valueOf(this.crucial)+"-"+constraint).hashCode();
		
	}


	@Override
	public boolean equals(Object other) {

		if(other instanceof StateLabel){
			StateLabel otherLabel = (StateLabel)other;
			
			if(!checkConstraints(otherLabel))
				return false;
			
			if(constraint==null){
				if(otherLabel.crucial().equals(this.crucial) && 
						otherLabel.begin.equals(this.begin))
					return true;
			}
			else{
				if(otherLabel.crucial().equals(this.crucial) && 
						otherLabel.begin.equals(this.begin)&&
						otherLabel.getConstraint().equals(this.constraint))
					return true;
			}
		}		

		return false;

	}


	private boolean checkConstraints(StateLabel otherLabel) {
		
		if(otherLabel.getConstraint()!=null&&constraint==null)
			return false;
		
		if(otherLabel.getConstraint()==null&&constraint!=null)
			return false;
		
		return true;
		
	}
	
	
	
	@Override
	public Object clone() {
		
		StateLabel clone = new StateLabel(crucial.copy(), begin.copy(), constraint);
		
		if(prev!=null)
			clone.prev = (StateLabel)prev.clone();
		
		return clone;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	




	public StateLabel getNext() {
		return next;
	}



	public void setNext(StateLabel next) {
		this.next = next;
	}




















	















}
