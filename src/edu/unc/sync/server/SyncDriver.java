package edu.unc.sync.server;

import edu.unc.sync.*;
import bus.uigen.ObjectEditor;
//import budget.DelegationTest;
import java.util.*;

import util.models.Hashcodetable;

public class SyncDriver
{
	private SyncClient client;
	private PropertiesTable properties;
	private String myname;
	ApplicationName applicationName;

	public SyncDriver(SyncClient clnt){
		client = clnt;
		if (client.ui != null)
		    applicationName = new ApplicationName(clnt.ui.appNames);
		clnt.setDriver(this);
	}

	public boolean getSynchronousMode(){
		return Sync.getSyncMode();
	}

	public void setSynchronousMode(boolean mode){
		Sync.setSyncMode(mode);
	}
	/*
	public Vector getApplicationNames() {
		return client.ui.getApplicationNames();
	}
	*/
	public ApplicationName getApplicationName() {
		return applicationName;
	}

	public SyncClient returnClient(){
		return client;
	}

	public ServerProxy openAndReplicateServer(String serverName){
		if (null != client){
			return client.ui.openAndReplicateServer(serverName);
		}
		else{
			System.out.println("Could not replicate server: No SyncClient Object present");
			return null;
		}
	}

	public Hashcodetable showDelegatedTable(){
		return Sync.returnDelegatedTable();
	}

	public Delegated returnDelegate(Object o){
		return DelegatedUtils.findDelegate(o);
	}

	public MergeMatrix returnMergeMatrix(Object o){
		Replicated repl = (Replicated)(DelegatedUtils.findDelegate(o));
		if (null != repl){
			return repl.getMergeMatrix();
		}
		else
			return null;
	}
	public ServerProxyTable getServers() {
		return client.getServerProxyTable();
	}
	/*
	public PropertiesTable properties() {
		return client.getProperties();
	}
	*/
}
