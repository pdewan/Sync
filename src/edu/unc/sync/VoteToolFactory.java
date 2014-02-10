package edu.unc.sync;

//import budget.ConcertExpense;

public class VoteToolFactory implements ObjectFactory {
	String issue = "";
	public VoteToolFactory (String theIssue) {
		issue = theIssue;
	}
	public Object newInstance (Class c) {
		VoteTool voteTool = new VoteTool();
		voteTool.init(issue);
		return voteTool;
	}

}
