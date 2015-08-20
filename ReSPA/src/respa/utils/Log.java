package respa.utils;

import java.util.ArrayList;

public class Log {

	
	private String leak=null;
	
	private String percentageLeak=null;
	
	private String leakOIA=null;
	
	private String percentageLeakOIA=null;
	
	private String percentageResidue=null;
	
	private String residue=null;
	
	private String reapElapsedTime=null;
	
	private String searchElapsedTime=null;
	
	private String oiaTime=null;
	
	private String restarted=null;
	
	private String memory=null;
	
	private String detoursSuccess=null;
	
	private String detoursFailed=null;
	
	private String detoursAttempted=null;
	
	private String detoursNotAttempted=null;
	
	private String numConstraintsLM=null;
	
	private String numConstraintsOIA=null;
	
	
	
	
	
	private ArrayList<String> log=null;
	
	
	
	public Log() {
		
		log = new ArrayList<String>();
		
	}



	public String getLeak() {
		return leak;
	}



	public void setLeak(String leak) {
		this.leak = leak;
		log.add("Leak: "+leak);
	}



	public String getPercentageLeak() {
		return percentageLeak;
	}



	public void setPercentageLeak(String percentageLeak) {
		this.percentageLeak = percentageLeak;
		log.add("PercentageLeak: "+leak);

	}



	public String getLeakOIA() {
		return leakOIA;
	}



	public void setLeakOIA(String leakOIA) {
		this.leakOIA = leakOIA;
		log.add("OIALeak: "+leak);
	}



	public String getPercentageLeakOIA() {
		return percentageLeakOIA;
	}



	public void setPercentageLeakOIA(String percentageLeakOIA) {
		this.percentageLeakOIA = percentageLeakOIA;
		log.add("OIALeakPercentage: "+leak);
	}



	public String getPercentageResidue() {
		return percentageResidue;
	}



	public void setPercentageResidue(String percentageResidue) {
		this.percentageResidue = percentageResidue;
		log.add("PercentageResidue: "+leak);

	}



	public String getResidue() {
		return residue;
	}



	public void setResidue(String residue) {
		this.residue = residue;
		log.add("Residue: "+leak);

	}



	public String getReapElapsedTime() {
		return reapElapsedTime;
	}



	public void setReapElapsedTime(String reapElapsedTime) {
		this.reapElapsedTime = reapElapsedTime;
		log.add("ElapsedTimeREAP: "+leak);

	}



	public String getSearchElapsedTime() {
		return searchElapsedTime;
	}



	public void setSearchElapsedTime(String searchElapsedTime) {
		this.searchElapsedTime = searchElapsedTime;
		log.add("ElapsedTimeSearch: "+leak);

	}



	public String getOiaTime() {
		return oiaTime;
	}



	public void setOiaTime(String oiaTime) {
		this.oiaTime = oiaTime;
		log.add("ElapsedTimeOIA: "+leak);

	}



	public String getRestarted() {
		return restarted;
	}



	public void setRestarted(String restarted) {
		this.restarted = restarted;
		log.add("Restarted: "+leak);

	}



	public String getMemory() {
		return memory;
	}



	public void setMemory(String memory) {
		this.memory = memory;
		log.add("maxMemory: "+leak);

	}



	public String getDetoursSuccess() {
		return detoursSuccess;
	}



	public void setDetoursSuccess(String detoursSuccess) {
		this.detoursSuccess = detoursSuccess;
		log.add("detoursSuccess: "+leak);

	}



	public String getDetoursFailed() {
		return detoursFailed;
	}



	public void setDetoursFailed(String detoursFailed) {
		this.detoursFailed = detoursFailed;
		log.add("detoursFailed: "+leak);

	}



	public String getDetoursAttempted() {
		return detoursAttempted;
	}



	public void setDetoursAttempted(String detoursAttempted) {
		this.detoursAttempted = detoursAttempted;
		log.add("detoursAttempted: "+leak);

	}



	public String getDetoursNotAttempted() {
		return detoursNotAttempted;
	}



	public void setDetoursNotAttempted(String detoursNotAttempted) {
		this.detoursNotAttempted = detoursNotAttempted;
		log.add("detoursNotAttempted: "+leak);

	}



	public String getNumConstraintsLM() {
		return numConstraintsLM;
	}



	public void setNumConstraintsLM(String numConstraintsLM) {
		this.numConstraintsLM = numConstraintsLM;
		log.add("ConstraintsLM: "+leak);

	}



	public String getNumConstraintsOIA() {
		return numConstraintsOIA;
	}



	public void setNumConstraintsOIA(String numConstraintsOIA) {
		this.numConstraintsOIA = numConstraintsOIA;
		log.add("ConstraintsOIA: "+leak);

	}



	public ArrayList<String> getLog() {
		return log;
	}



	public void setLog(ArrayList<String> log) {
		this.log = log;
	}
	
	
	
	
	
	@Override
	public String toString() {
		
		String tostring="";
		
		for(String s: log){
			tostring=tostring+s;
			tostring = tostring+"\n";
		}
		
		return tostring;
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
