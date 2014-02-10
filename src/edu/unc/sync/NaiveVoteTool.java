package edu.unc.sync;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;

import util.models.AListenableHashtable;
import util.models.AListenableString;
import util.models.HashtableInterface;

import edu.unc.sync.server.ServerProxy;
import edu.unc.sync.server.SyncClient;

//import budget.ConcertExpense;
public class NaiveVoteTool implements Serializable {
	transient PropertyChangeSupport propertyChange;	
	
	int yesVotes = 0;
	int noVotes = 0;
	transient boolean myVote = false;
	public boolean getMyVote() {
		return myVote;
	}	
	public void setMyVote(boolean newVal) {
		if (newVal == myVote) return;
		myVote = newVal;
		if (newVal) {
			yesVotes++;
			noVotes--;
		} else {
			noVotes++;
			yesVotes--;
		}			
	}
		
	String issue = " ";	
	
	public String getIssue() {
		return issue;
	}
	public void setIssue(String newVal) {
		if (issue.equals(newVal)) return;
		String oldVal = issue;
		issue = newVal;
		propertyChange.firePropertyChange("issue", oldVal, newVal);
	}
	
	public int getYesVotes() {
		return yesVotes;
	}
	public void setYesVotes(int newVal) {
		yesVotes = newVal;
	}
	
	public int getNoVotes() {
		return noVotes;
	}
	public void setNoVotes(int newVal) {
		noVotes = newVal;
	}
	
		
	public static SyncClient replicateWithVoteTool (String server,  String modelName, Class modelClass, Class editorClass, String clientName, String issue) {
		SyncClient client = Sync.replicate (server, modelName, modelClass, editorClass, clientName); 
		ServerProxy proxy = client.getServerProxy(server);		
		Sync.register(VoteTool.class, new VoteToolFactory(issue));
		String voteToolName = modelName+ "VoteTool for:" + issue;
		proxy.newObject(voteToolName, VoteTool.class, SyncObjectEditor.class);
		VoteTool voteTool = (VoteTool) proxy.getModel(voteToolName);
		//voteTool.initUserName(clientName);
		return client;	
				
		   
		   
		   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
	   }
	public void addPropertyChangeListener(PropertyChangeListener l) {        	
        propertyChange.addPropertyChangeListener(l);
    }
 
}
