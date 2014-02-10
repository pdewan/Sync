package edu.unc.sync.server;

import edu.unc.sync.*;

import java.util.Enumeration;
import java.util.Vector;
//import com.sun.java.swing.tree.*;
import javax.swing.tree.*;
import bus.uigen.ObjectEditor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.rmi.*;
import bus.uigen.*;
import bus.uigen.oadapters.HashtableAdapter;
//import bus.uigen.reflect.MethodProxy;
//import bus.uigen.uiBean;

import java.util.Observable;
import java.util.Observer;
@util.annotations.StructurePattern(util.annotations.StructurePatternNames.BEAN_PATTERN)
public class ServerProxy extends Observable 
{
      boolean realTimeSynchronize;
   FolderTreeNodeCopy folderTreeNodeCopy;
   String server_adr;
   Folder rootFolder/*, serverFolder*/;
   SyncClient syncClient;
   ApplicationName applicationName;
   //MethodProxy addObjectMethod;
   Method addObjectMethod;
   
   Hashtable allClients;
   Vector observers = new Vector();
   

   public ServerProxy (FolderTreeNodeCopy theFolderTreeNodeCopy/*, String theServer_adr*/,
   		Folder theRootFolder,
		SyncClient theSyncClient,
		Hashtable theClients)
   {
   	folderTreeNodeCopy = theFolderTreeNodeCopy;
   	//folderTreeNode = theFolderTreeNode;
   	folderTreeNodeCopy.setServerData(this);
   	rootFolder = theRootFolder;
   	syncClient = theSyncClient;
   	allClients = theClients;
	//if (syncClient.ui != null)
	applicationName = new ApplicationName(syncClient.ui.appNames);
	try {
		Class[] args = {String.class};
		addObjectMethod = bus.uigen.introspect.IntrospectUtility.getMethod(this.getClass(),"addObject",  Void.TYPE, args);
		if (syncClient.hasOE() && addObjectMethod != null) {
			String[] parameterNames = {"Instance Name"};
			ObjectEditor.registerParameterNames(addObjectMethod, parameterNames);
		}
	} catch (Exception e) {
		System.out.println("Could not find method: addObject in " + this.getClass());
	}
   	//server_adr = theServer_adr;
      
   }
   
   protected FolderTreeNodeCopy getServerRoot () {
   	return folderTreeNodeCopy;
   }
   public String getName () {
   	 //return server_adr;
   	return folderTreeNodeCopy.getHome();
   }
   @util.annotations.ComponentWidth(200)
   public boolean getRealTimeSynchronize() {
   	return realTimeSynchronize;
   }
   public void setRealTimeSynchronize(boolean newVal) {
   	realTimeSynchronize = newVal;
   	setChanged();
   	notifyObservers();
   }
   public ObjectID objectID () {
   	return folderTreeNodeCopy.getObjectID();
   }
   
   public String getAddress () {
   	 return folderTreeNodeCopy.getAddress();
   }
   
   public Folder getObjects() {
   	if (rootFolder == null) return null;
   	return (Folder) rootFolder.get(getName());  	
   }
   public Object initialModel() {
   	if (rootFolder == null) return null;
   	Folder folder =  (Folder) rootFolder.get(getName());  
   	Enumeration elements = folder.elements();
   	if (elements.hasMoreElements())
   		return unwrap((Replicated) elements.nextElement());
   	else return null;
   }
   Object unwrap(Replicated wrapped) {
   	 if (wrapped instanceof ReplicatedReference) {
   		 /*
   	 	Replicated replicated =  ((ReplicatedReference) wrapped).getReferencedObject();
   	 	if (replicated instanceof Delegated) 
   	 		return ((Delegated) replicated).returnObject(); 
   	 		*/
   		 wrapped = ((ReplicatedReference) wrapped).getReferencedObject();
   	 }
   	 if (wrapped instanceof Delegated) 
	 		return ((Delegated) wrapped).returnObject(); 
   	 return wrapped;
   }
   public Object getModel(String name) {
   	if (rootFolder == null) return null;
   	Folder folder =  (Folder) rootFolder.get(getName()); 
   	return unwrap(folder.get(name));
   	
   }
   public void addObject() {
   	   //SyncDriver driver = syncClient.getDriver();
   	 //if (driver == null) return;
   	 //newObject (driver.getApplicationName());
	 newObject (getApplicationName());
   		
   	 
   }
   public void addObject(String instanceName) {
	   //SyncDriver driver = syncClient.getDriver();
	 //if (driver == null) return;
	 //newObject (driver.getApplicationName());
	 newObject (instanceName, getApplicationName().getValue());
		
	 
}
   protected void newObject(ApplicationName appName) {  
   	SyncClientInterface ui = syncClient.ui;
  	 if (ui == null) return;
  	 ui.instantiateClass(appName.getValue(), getObjects());
  		
  	 
  }
   protected void newObject(String appName) {  
   	SyncClientInterface ui = syncClient.ui;
  	 if (ui == null) return;
  	 ui.instantiateClass(appName, getObjects());
  		
  	 
  }
   protected void newObject(String instanceName, String appName) {  
   	SyncClientInterface ui = syncClient.ui;
  	 if (ui == null) return;
  	 ui.instantiateClass(instanceName, appName, getObjects());
  		
  	 
  }
   protected void newObject (Class objectClass, 
   		Class editorClass, Folder targetDir) { 
	SyncClientInterface ui = syncClient.ui;
   	if (ui == null) return;
   	ui.instantiateClass (objectClass, 
   	   		editorClass, getObjects()); 
   }
   
   public void newObject (String objName, Class objectClass, 
   		Class editorClass) { 
	SyncClientInterface ui = syncClient.ui;
   	if (ui == null) return;
   	ui.instantiateClass (objName, objectClass, 
   	   		editorClass, getObjects()); 
   }
   protected void newObject (String objName, String objectClass, 
   		String editorClass) { 
	SyncClientInterface ui = syncClient.ui;
   	if (ui == null) return;
   	ui.instantiateClass (objName, objectClass, 
   	   		editorClass, getObjects()); 
   }
   
   public void newObject (String objName, Object object, 
	   		SyncApplication editor) { 
		SyncClientInterface ui = syncClient.ui;
	   	if (ui == null) return;
	   	ui.instantiateClass (objName, object, 
	   	   		editor, getObjects()); 
	   }
   protected void newObject (String objName, Object object, 
	   		String editor) { 
		SyncClientInterface ui = syncClient.ui;
	   	if (ui == null) return;
	   	ui.instantiateClass (objName, object, 
	   	   		editor, getObjects()); 
	   }
//   public boolean preSynchronize() {
//	   return !getRealTimeSynchronize();
//   }
   public void synchronize() {
   	
   	 syncClient.ui. synchronizeAction(objectID());
   }
 


/*
public Vector getApplicationNames() {
	return client.ui.getApplicationNames();
}
*/
public ApplicationName getApplicationName() {
	return applicationName;
}

public Vector getOtherClientNames() {
	return  HashtableAdapter.toVector (allClients.keys());
	
}
void clientJoined (String name, Remote obj)  {
	allClients.put(name, obj);
	notifyObservers(new AClientJoinedEvent(name));
}
void clientLeft (String name, Remote obj)  {
	allClients.remove(name);
	notifyObservers(new AClientLeftEvent(name));
}
/*
public void addObserver(Observer o) {
	
	//super.addObserver(o);
	if (observers.contains(o)) return;
	observers.addElement(o);
}
public void deleteObserver(Observer o) {
	//super.addObserver(o);
	 observers.removeElement(o);
}
*/
public void notifyObservers (Object arg) {
	setChanged();
	super.notifyObservers(arg);
	/*
	for (int i = 0; i < observers.size(); i++ ) {
		((Observer) observers.elementAt(i)).update( this, arg);
	}
	*/
}

}
   