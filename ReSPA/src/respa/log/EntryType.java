package respa.log;


public enum EntryType {

	
	
	logError("ERROR"){},
	logPhiGet("logPhiGet") {},
	logStartIteration("logStartIteration") {},
	logEndIteration("logEndIteration"){},
	logMaxIteration("logMaxIteration"){},
	logStepForward("logStepForward"){},
	logReproduced("logReproduced"){},
	logNotReproduced("logNotReproduced"){},
	logSuspect("logSuspect") {},
	logNewNm("logNewNm"){},
	logGBrecovered("logGBrecovered"),
	logGBadded("logGBadded"),
	logDijkstra("logDijkstra"),
	logPushNode("logPushNode"),
	logPopNode("logPopNode"),
	logUpdateNode("logUpdateNode"),
	logOutOfR("logOutOfR"),
	evaluate("evaluate"),
	verbose("verbose"),
	log("log");
	
	
	
	
	
	
	private String str;

	EntryType(String str){
		this.str= str;
	}


	@Override
	public String toString() {
		return str;
	}

}
