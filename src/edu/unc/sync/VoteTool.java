package edu.unc.sync;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import util.models.ADelegatingListenableHashtable;
import util.models.AListenableHashtable;
import util.models.AListenableString;
import util.models.HashtableListener;
import util.models.ListenableHashtable;

import edu.unc.sync.server.ServerProxy;
import edu.unc.sync.server.SyncClient;

//import budget.ConcertExpense;
public class VoteTool implements Serializable, HashtableListener {
	transient PropertyChangeSupport propertyChange;	
	public VoteTool (String theIssue) {
		init(theIssue);
	}
	public VoteTool () {
		
	}
	
	//AListenableHashtable<String, Boolean> votes = new AListenableHashtable();
	//ListenableHashtable votes = new ADelegatingListenableHashtable();
	ListenableHashtable<String, Boolean> votes = new AListenableHashtable();
	//Hashtable votes = new Hashtable();
	//AListenableHashtable votes = new Hashtable();
	@util.annotations.Visible(false)
	public ListenableHashtable getVotes(){
	//public Hashtable getVotes(){
		return votes;
	}	
	public void setVotes(ListenableHashtable newVal) {
	//public void setVotes(Hashtable newVal) {
		votes = newVal;
	}
	
	//transient boolean myVote = false;
	public boolean getMyVote() {
		Boolean myVote = votes.get(Sync.getClientId());
		if (myVote == null) return false;
		return myVote;
	}	
	public void setMyVote(boolean newVal) {
		boolean oldVal = getMyVote();
		if (newVal == oldVal) return;
		//votes.put(userName, new Boolean(newVal));
		votes.put(Sync.getClientId(), new Boolean(newVal));
		int newYesVotes = getYesVotes();
		if (oldYesVotes !=  getYesVotes())
			propertyChange.firePropertyChange("yesVotes", oldYesVotes, newYesVotes);		
		oldYesVotes = newYesVotes;		
		propertyChange.firePropertyChange("myVote", oldVal, newVal);		
	}
			
	transient String userName = "Anonymous";
	public void initUserName (String initVal) {
		//initSerializedObject();
		userName = initVal;			
		//votes.put(userName, new Boolean(myVote));
		
	}		
	String issue = "";
	public void init (String theIssue) {
		issue = theIssue;
		initSerializedObject();
	}
	
	public String getIssue() {
		return issue;
	}
	public void setIssue(String newVal) {
		if (issue.equals(newVal)) return;
		String oldVal = issue;
		issue = newVal;
		propertyChange.firePropertyChange("issue", oldVal, newVal);
	}
	int oldYesVotes;
	public int getYesVotes() {
		Enumeration elements = votes.elements();
		int retVal = 0;
		while (elements.hasMoreElements()) {
			if (((Boolean) elements.nextElement()).equals(true))
				retVal++;
		}
		//oldYesVotes = retVal;
		return retVal;
	}
//	public int getNoVotes() {
//		Enumeration elements = votes.elements();
//		int retVal = 0;
//		while (elements.hasMoreElements()) {			
//			if (((Boolean) elements.nextElement()).equals(false))
//				retVal++;
//		}
//		return retVal;
//	}
		
	/*
	int yesVotes, noVotes;
	public int getYesVotes() {
		return yesVotes;
	}
	public void setYesVotes(int newVal) {
		if (yesVotes == newVal)
			return;
		int oldVal = yesVotes;
		propertyChange.firePropertyChange("yesVotes", oldVal, newVal);
	}
	public int getNoVotes() {
		return noVotes;
	}
	public void setNoVotes(int newVal) {
		if (noVotes == newVal)
			return;
		int oldVal = noVotes;
		propertyChange.firePropertyChange("noVotes", oldVal, newVal);
	}
	*/
	public void initSerializedObject() {
		propertyChange = new PropertyChangeSupport(this);
		initUserName(Sync.getClientId());
		votes.addHashtableListener(this);
	}
	public static SyncClient replicateWithVoteTool (String server,  String modelName, Class modelClass, Class editorClass, String clientName, String issue) {
		SyncClient client = Sync.replicate (server, modelName, modelClass, editorClass, clientName); 
		ServerProxy proxy = client.getServerProxy(server);		
		Sync.register(VoteTool.class, new VoteToolFactory(issue));
		String voteToolName = modelName+ "VoteTool for:" + issue;
		proxy.newObject(voteToolName, VoteTool.class, SyncObjectEditor.class);
		VoteTool voteTool = (VoteTool) proxy.getModel(voteToolName);
		voteTool.initUserName(clientName);
		return client;	
				
		   
		   
		   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
	   }
	public void addPropertyChangeListener(PropertyChangeListener l) {        	
        propertyChange.addPropertyChangeListener(l);
    }
	static {
		// mak 
		Sync.setReplicate(VoteTool.class, "myVote", false);
		
	}
	void fireNumberOfVotesIfNecessary() {
		int newYesVotes = getYesVotes();
		if (oldYesVotes !=  getYesVotes())
			propertyChange.firePropertyChange("yesVotes", oldYesVotes, newYesVotes);		
		oldYesVotes = newYesVotes;		
	}
	@Override
	public void keyPut(Object source, Object key, Object value, int newSize) {
		fireNumberOfVotesIfNecessary();		
	}
	@Override
	public void keyRemoved(Object source, Object key, int newSize) {
		// TODO Auto-generated method stub
		
	}
 
}
