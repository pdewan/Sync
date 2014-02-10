/*
 * Created on Feb 9, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.unc.sync.server;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 * @author dewan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerProxyTable {
	 SyncClient syncClient;
	 public ServerProxyTable (SyncClient theSyncClient) {
	 	syncClient = theSyncClient;
	 	
	 }

	Hashtable contents = new Hashtable();	
	public Enumeration keys() {
		return contents.keys();
	}
	public Enumeration elements()  {
		return contents.elements();
	}
	public ServerProxy get (String key) {
		return (ServerProxy) contents.get(key);
		
	}
	public ServerProxy put (String key, ServerProxy val) {
		//System.out.println ("Put: " + key);
		return (ServerProxy) contents.put(key, val);
		
	}
	public ServerProxy remove (String key) {
		System.out.println ("Remove: " + key);
		return (ServerProxy) contents.remove(key);
		
	}
	public void openAndReplicateServer(String serverName){
		syncClient.driver.openAndReplicateServer(serverName);
		
	}
	public PropertiesTable registeredApplications () {
		return syncClient.getPropertyTable();
	}
	/*
	Object userObject = "foo";
	public Object getUserObject() {
		return userObject;
	}
	public void setUserObject (Object newVal) {
		userObject = newVal;
	}
	*/
}
