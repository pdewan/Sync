package edu.unc.sync.server;

import edu.unc.sync.*;

import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JTable;

import util.misc.MainArgsProcessor;
import util.models.AListenableHashtable;
//import com.sun.java.swing.*;
//import com.sun.java.swing.tree.*;
import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;

public class SyncClient 
	//extends UnicastRemoteObject 
	implements RemoteSyncClient
{
   SyncObjectServer object_server;
   public SyncClientInterface ui;
   String name = "";
   static String globalClientId = "";
   String client_id = "";   
   public static final String SERVER = "--server";
   public static final String NO_DUPLICATES = "--no_duplicates";
   public static final String CLIENT_ID = "--client_id";
   //public static final String UI = "--ui";
   public static final String RT = "--synchronous";
   public static final String MODEL_CLASS = "--model";
   public static final String EDITOR_CLASS = "--editor";
   public static final String APPLICATION = "--app";
   public static final String SYNC_CONTROLS = "--sc";
   //public static final String OE = "--oe";
   public static final String MODEL_NAME = "--instance";
   public static final String AUTO_OPEN = "--auto_open";
   public static final String HELP = "--help";
   //public static final String TRACE = "--trace";
   //public static final String RMI_PORT = "--rmi_port";
   public static final String[] regFlags = {SyncServer.RMI_PORT, SERVER, CLIENT_ID,  MODEL_CLASS, EDITOR_CLASS, MODEL_NAME, APPLICATION};
   public static final String[] boolFlags = {SyncServer.OE,  SyncServer.UI, SyncServer.TRACE,  RT, AUTO_OPEN, NO_DUPLICATES, SYNC_CONTROLS};
   
   String initialServerID = "SyncServer";
   ServerProxy initialServerProxy = null;
   
   public SyncClient(String myname) throws RemoteException {
   	init(myname);
   }
   PropertiesTable propertyTable;
   public String getServerID() {
   	return initialServerID;
   }
   public void setServerID(String newVal) {
   	initialServerID = newVal;
   }
   public ServerProxy getServerProxy() {
   	return initialServerProxy;
   }
   public void setServerProxy(ServerProxy newVal) {
   	  initialServerProxy = newVal;
   }
   RemoteSyncClient exportedThis;
   public RemoteSyncClient getExportedStub() {
	   return exportedThis;
   }
   public void setExportedStub(RemoteSyncClient newVal) {
	   exportedThis = newVal;
   }

   public SyncClient(PropertiesTable properties, String myname) throws RemoteException
   {
   	 //System.out.println ("starting sync client");
   	RemoteSyncClient exportedThis = (RemoteSyncClient) UnicastRemoteObject.exportObject(this, 0);
    this.setExportedStub(exportedThis);
	  //name = myname;
      //object_server = Sync.getObjectServer();;
   	  init (myname);
      createUIDataStructures(properties);
      propertyTable = properties;
      //ui = new SyncClientInterface(this, properties);
      // new BasicMergeActions();  // not used anymore
   }
   //public  Vector serverDataList = new Vector();
   ServerProxyTable serverProxyTable = new ServerProxyTable(this);
   
   public ServerProxyTable getServerProxyTable() {
   	//return serverDataList;
   	return serverProxyTable;
   }
   public ServerProxy getServerProxy(String hostName) {
	   ServerProxyTable table = getServerProxyTable();
	   return table.get(hostName.toLowerCase());
	   
   }
   /*
   public Vector getServerDataList() {
   	return serverDataList;
   }
   */
   String rmiPort;
   public void setRMIPort(String newVal) {
   	rmiPort = newVal;
   }
   public String getRMIPort() {
   	return rmiPort;
   }
   
   void createUIDataStructures (PropertiesTable properties) {
   	ui = new SyncClientInterface(this, properties);
   }
   public PropertiesTable getPropertyTable() {
   	return propertyTable;
   }
   ServerProxy openAndReplicateServer(String server_name) {
   	if (ui != null) {
   		return ui.openAndReplicateServer(server_name);
   		
   	}
   	else return null;
   }
   void doRefresh() {
   	if (ui != null)
   		ui.doRefresh();
   }
   public static String getGlobalClientId() {
	   return globalClientId;
   }
   public SyncClient(String myid, PropertiesTable properties, String myname) throws RemoteException
   {
	   //System.out.println ("starting sync client");
	   	RemoteSyncClient exportedThis = (RemoteSyncClient) UnicastRemoteObject.exportObject(this, 0);
	   	setExportedStub(exportedThis);
     client_id = myid;
     globalClientId = myid;
          //name = myname;
      //object_server = Sync.getObjectServer();
      init (myname);
      createUIDataStructures(properties);
      propertyTable = properties;
      //ui = new SyncClientInterface(this, properties);
      // new BasicMergeActions();  // not used anymore
   }
   
   void init (String myname) {
   	name = myname;
    object_server = Sync.getObjectServer();
   }

   public SyncClient(PropertiesTable properties, String myname, String server_name) throws RemoteException
   {
	   //name = myname;
	   //object_server = Sync.getObjectServer();
	   //System.out.println ("starting sync client");
	   	RemoteSyncClient exportedThis = (RemoteSyncClient) UnicastRemoteObject.exportObject(this, 0);
	   	setExportedStub(exportedThis);
   	   init (myname);
	   createUIDataStructures(properties);
	   //ui = new SyncClientInterface(this, properties);
	   //ui.openAndReplicateServer(server_name);
	   openAndReplicateServer(server_name);
   }
   public void clientJoined(ObjectID objectID, String name, RemoteSyncClient obj) throws RemoteException {
   	//System.out.println("name joined");
   	for (Enumeration elements = getServerProxyTable().elements(); elements.hasMoreElements();) {
		ServerProxy serverData = (ServerProxy) elements.nextElement();
		if (serverData.objectID().getPrimary().equals(objectID.getPrimary()) && 
				serverData.objectID().getNameSpace().equals(objectID.getNameSpace())) {
		
			serverData.clientJoined (name, obj);
			break;
		}
	}
   }
   
   public void clientLeft(ObjectID objectID, String name, RemoteSyncClient obj) throws RemoteException {
   	//System.out.println("name left");
	for (Enumeration elements = getServerProxyTable().elements(); elements.hasMoreElements();) {
		ServerProxy serverData = (ServerProxy) elements.nextElement();
		if (serverData.objectID().getPrimary().equals(objectID.getPrimary()) && 
				serverData.objectID().getNameSpace().equals(objectID.getNameSpace())) {
		
			serverData.clientLeft (name, obj);
			break;
		}
	}
   }
   // should make this non static at some pt, indexed by model
   static String remoteSourceName;
   public static String getRemoteSourceName() {
	   return remoteSourceName;
   }
   public static String getSourceName() {	   
	   if (isRemote())
		   return getRemoteSourceName();
	   else
		   return getFullName();
   }
   
   public void notifyAction(ObjectID serverObjectID, String theSourceName, RemoteSyncClient theSource, ObjectID theOid) throws RemoteException{
   	//public void notifyAction() throws RemoteException{
           if (this == theSource) return;
   	synchronized (this) {
           	if (Sync.getTrace())
	   System.out.println("notifyAction() from: " + theSourceName);
           	remoteSourceName = theSourceName;
	   //if (Sync.getSyncMode()){
		   try{
			   //Sync.synchronize();
		   	    Sync.implicitSynchronize(serverObjectID, true);
		   	    /*
		   	    if (ui != null)
		   	    	ui.doRefresh();
		   	    	*/
		   }
		   catch(Exception ex){
			   throw new RemoteException(ex.toString());
		   }
	   //}
		   if (Sync.getTrace())
           System.out.println("notifyAction() ended");
           }
   	remoteSourceName = null;
   }
   /*
   public void newClient(String name, Remote obj) {
   	System.out.println ("New Client:" + name);
   }
   */

   // handles all synchronization tasks
   // return value is used for error messages, null for success
   Vector<SyncClientListener>  listeners= new Vector();
   public void addListener(SyncClientListener listener) {
	   if (listeners.contains(listener)) return;
	   listeners.add(listener);
	   
   }
   public void notifyListnersSynchronize(RemoteSyncServer server, Replicated obj, Change change) {
	   for (int i=0; i<listeners.size(); i++)
		   listeners.elementAt(i).synchronize(server, this, obj, change);
	   
   }
   static boolean isRemote = false;
   public static boolean isRemote() {
	   return isRemote;
   }
   public synchronized String[] synchronize(Replicated obj)
   {
      //System.out.println("Starting synchronize() method:");
      //((ReplicatedDictionary) obj).print();
   	  //Sync.nested = true;
      Sync.supressImplicit = true;
      long initTimeMillis = System.currentTimeMillis();
      String address = obj.getHome();
      int slashIndex = address.lastIndexOf('/');
     	String hostName;
     	String nameSpace;
     	if (slashIndex >= 0) {
     		nameSpace = address.substring(slashIndex + 1);
     		hostName = address.substring(0, slashIndex);
     	} else {
     		nameSpace = "SyncServer";
     		hostName = address;
     	}
     		//nameSpace = sync_client.getInitialServerID();
        //String port = Sync.getProperty("sync.rmiregistry.port");
        //String url = "rmi://" + address + ":" + port + "/SyncServer";
        //String url = "rmi://" + address + ":" + sync_client.getRMIPort()+ "/" + sync_client.getInitialServerID();
     	//old way
        //String url = "rmi://" + hostName + ":" + getRMIPort()+ "/" + nameSpace;
        String serverName = SyncServer.SYNC_SERVER + "/" + nameSpace;
      //String port = Sync.getProperty("sync.rmiregistry.port");
      //String url = "rmi://" + server_addr + ":" + port + "/SyncServer";
      //String url = "rmi://" + server_addr + ":" + getRMIPort() + "/" + getInitialServerID();
      try {
         //RemoteSyncServer server = (RemoteSyncServer) Naming.lookup(url);
    	  Registry registry = SyncServer.getRegistry(hostName, Integer.parseInt( getRMIPort()));
         RemoteSyncServer server = (RemoteSyncServer) registry.lookup(serverName);
         Change change = obj.getChange();
         if (change == null) change = new NullChange();
         //System.out.println("Changes sent for synchronization:");
         //change.print();
         change.setFromVersion(obj.getVersion());
         if (Sync.getTrace()) {
         System.out.println ("Printing client changes");
         change.print();
         }
         //System.out.println("Before synchronize object method:");
         //((ReplicatedDictionary) obj).print();
         notifyListnersSynchronize(server, obj, change);
         
         //Change apply = server.synchronizeObject(obj.getObjectID(), change, name, this);
         Change apply = server.synchronizeObject(obj.getObjectID(), change, name, this.getExportedStub());
         //System.out.println("After synchrinize object method");
         //((ReplicatedDictionary) obj).print();
         //System.out.println("Changes received in reply:");
         //apply.print();
         if (!(apply instanceof NullChange)) {
        	 isRemote = true;
            Change rejected = obj.applyChange(apply);
            isRemote = false;
            //System.out.println("Changes rejected:");
            /*
            if (rejected != null) rejected.print();
            else System.out.println("none");
            */
         }
         //System.out.println("Before setVersion");
         //((ReplicatedDictionary) obj).print();
         obj.setVersion(apply.getToVersion());
         //System.out.println("After setVersion");
         //((ReplicatedDictionary) obj).print();
      obj.clearChanged();
      //System.out.println("After clearChanged");
      //((ReplicatedDictionary) obj).print();
         /*

         change = obj.getChange();
         if (change == null) change = new NullChange();
         System.out.println("Changes after  synchronization:");
         change.print();
         */
      	 doRefresh();
		 //ui.doRefresh();
      } catch (Exception ex) {
		  //Sync.nested = false;
      	Sync.supressImplicit = false;
		  System.out.println("#############");
		  ex.printStackTrace();
		  return new String[] {"Error synchronizing object", ex.toString()};
	  }
	  //Sync.nested = false;
  	  Sync.supressImplicit = false;
      long endTimeMillis = System.currentTimeMillis();
      if (Sync.getTrace())
      System.out.println("%%%%% Time for synchronization in milliseconds = " + (endTimeMillis - initTimeMillis));
      return null;
   }

   public void shutdown()
   {
      if (object_server != null) {
         try {
            object_server.shutdown();
         } catch (SyncException ex) {
            System.err.println("Error shutting down Sync object server: " + ex);
         }
      }
   }

    static Properties getSyncProps() {
     Properties sync_props = new Properties();
      File sync_props_file = new File(System.getProperty("user.home") + File.separator + "sync.jprops");
      try {
         sync_props.load(new FileInputStream(sync_props_file));
         Sync.setProperties(sync_props);
      } catch (Exception ex) {
      	if (Sync.getTrace())
      		System.err.println(ex);
         return null;
         //System.exit(1);
      }
      return sync_props;
   }

    static PropertiesTable getClientProps(Properties sync_props, String client_home) {
     PropertiesTable client_props = null;
      try {
         File props_file = new File(client_home, "client.jprops");
         client_props = getProps(sync_props, props_file);
         /*
         // add client properties to Sync properties so they are accessible
         // from within Replicated objects via static Sync.getProperty method
         sync_props.load(new FileInputStream(props_file));
         client_props = new PropertiesTable(props_file);
         */
      } catch (Exception ex) {
      System.err.println("Error loading properties: " + ex);
      //return null;
      System.exit(1);
      }
     return client_props;
   }

    static PropertiesTable getProps(Properties sync_props, File props_file) {
     PropertiesTable props = null;
      try {
         sync_props.load(new FileInputStream(props_file));
         props = new PropertiesTable(props_file);
      } catch (Exception ex) {
      	if (Sync.getTrace())
      System.err.println("Error loading properties: " + ex);
      //System.exit(1);
      return null;
      }
     return props;
   }

    static String getHostName(Properties sync_props) {
     String host_name;
     try {
       host_name = java.net.InetAddress.getLocalHost().getHostName();
       if (sync_props != null && !sync_props.getProperty("domain").trim().equals("") &&
           host_name.indexOf('.') == -1) host_name = host_name + "." + sync_props.getProperty("domain");
       //if (host_name.indexOf('.') == -1) host_name = host_name + "." + sync_props.getProperty("domain");
     } catch (java.net.UnknownHostException ex) {
       host_name = "localhost";
     }
     return host_name;

   }
    static String globalSourceName;
    public static String getFullName() {
    	return globalSourceName;
    }
    //String id = "";
   public static SyncClient createSyncClient(Hashtable argsTable, Properties sync_props,
       PropertiesTable client_props) {
	   
   	String port = (String) argsTable.get(SyncServer.RMI_PORT);
   	
    if (port == null && sync_props != null)  
		//String port = sync_props.getProperty("sync.rmiregistry.port");
    	port = sync_props.getProperty("sync.rmiregistry.port");
    if (port == null)
    	port = Sync.DEFAULT_RMI_PORT;
    

		String host_name = getHostName(sync_props);
		SyncClient client = null;

		// Create and install a security manager
		//System.setSecurityManager(new RMISecurityManager());
		String id = "SyncClient";

		try {
			
			if (argsTable.containsKey(CLIENT_ID))
				id = (String) argsTable.get(CLIENT_ID);
			String name =  host_name + "(" + id + ")";
			globalSourceName = name;
			/*
			String name = "//" + host_name + ":" + port + "/SyncClient";
			String suffix = (String) argsTable.get(CLIENT_ID);
			if (suffix != null) {
				name += suffix;
				id = suffix;
			}
			*/
			/*
			if (args.length >= 3) {
				name += args[2];
				id = args[2];
			}
			*/
			//client = new SyncClient(id, client_props, name);
			client = new SyncClient(id, client_props, name);
			client.setRMIPort(port);
		} catch (Exception e) {
			System.out.println("111111111111");
			System.out.println("SyncClient error: " + e.getMessage());
			e.printStackTrace();
		}
		if (Sync.getTrace())
		System.out.println("Started Sync client");
		return client;

	}
   public static void createUI(SyncClientInterface ui, Hashtable argsTable) {
 		
 			String uiFlag = (String) argsTable.get(SyncServer.UI);
 			if (uiFlag == null || ui == null)
 				return;
 			//ui.createUI("Sync Client");'
 			ui.createUI();

 	}
   
   
   /*
    * public static SyncClient createSyncClient(String args[], Properties
    * sync_props, PropertiesTable client_props) { String port =
    * sync_props.getProperty("sync.rmiregistry.port");
    * 
    * String host_name = getHostName(sync_props); SyncClient client = null;
    *  // Create and install a security manager //System.setSecurityManager(new
    * RMISecurityManager()); String id = "";
    * 
    * try{ String name = "//" + host_name + ":" + port + "/SyncClient"; if
    * (args.length >=3) { name += args[2]; id = args[2]; } client = new
    * SyncClient(id, client_props, name); } catch (Exception e) {
    * System.out.println("111111111111"); System.out.println("SyncClient error: " +
    * e.getMessage()); e.printStackTrace(); } System.out.println("Started Sync
    * client"); return client;
    *  }
    */
   SyncDriver driver;
   protected void setDriver (SyncDriver theDriver) {
   	 driver = theDriver;
   	
   }
   protected SyncDriver getDriver() {
   	return driver;
   	
   }
   /*
   public static SyncDriver createSyncDriver(String args[], SyncClient client) {
     SyncDriver theDriver = null;
     bus.uigen.uiFrame frame = null;
     if ((args.length >=1) &&
         (args[0].equals(new String("driver")))){
       theDriver = new SyncDriver(client);
       frame = ObjectEditor.edit(client.getServerProxyTable());       
       //frame = ObjectEditor.edit(theDriver);
     }

     return theDriver;
   }
   */
   public static SyncDriver createSyncDriver(Hashtable argsTable, SyncClient client) {
    SyncDriver theDriver = null;
    bus.uigen.uiFrame frame = null;
    /*
    if ((args.length >=1) &&
        (args[0].equals(new String("driver")))){
        */ 
    theDriver = new SyncDriver(client);
    if ((argsTable.containsKey(RT))) {
    	//if (serverProxy == null)
    		theDriver.setSynchronousMode(true);
    	//else
    		//serverProxy.setRealTimeSynchronize(true);
    }
    /*
    if ((argsTable.containsKey(OE))) {
      //theDriver = new SyncDriver(client);
      frame = ObjectEditor.edit(client.getServerProxyTable());       
      //frame = ObjectEditor.edit(theDriver);
    }
    */
    return theDriver;
  }
   boolean hasOE = false;
   public void setOE (boolean newVal) {
   	 hasOE = true;   	
   }
   public boolean hasOE() {
   	return hasOE;
   }
   public static void createOEUI (ServerProxy serverProxy, Hashtable argsTable, SyncClient client) {
   
    if ((argsTable.containsKey(SyncServer.OE))) {
    	client.setOE(true);
//		ObjectEditor.setPreferredWidget(AListenableHashtable.class, JTable.class);

      //theDriver = new SyncDriver(client);
    	Enumeration elements = client.getServerProxyTable().elements();
    	uiFrame editor1;
    	if (serverProxy != null) {
    		editor1 = ObjectEditor.edit(serverProxy) ;
    	} else if (elements.hasMoreElements())
    		editor1 = ObjectEditor.treeBrowse(client.getServerProxyTable(), elements.nextElement());
    	else    	
			editor1 = ObjectEditor.edit(client.getServerProxyTable()); 
    	if (client.ui == null) return;
    	//uiFrame editor2 = ObjectEditor.treeBrowse(client.ui.getTreeRoot(), client.ui.getRootFolder());
		client.ui.setErrorMsgsFrameIfUndefined(editor1.getFrame());
		String clientTitle = "Sync Client:" + client.name;
		editor1.setTitle(clientTitle);
		//editor2.setTitle(clientTitle);
      //frame = ObjectEditor.edit(theDriver);
    }

    //return theDriver;
  }
   public static void createSyncControlsUI (ServerProxy serverProxy, Hashtable argsTable) {
	   
	    if ((argsTable.containsKey(SYNC_CONTROLS))) {
	    	SyncControls controls = new SyncControls(serverProxy);
	    	ObjectEditor.edit(controls);
	    }
	    
	  }

   public static SyncObjectServer createSyncObjectServer(String client_home, String the_name_space) {
     SyncObjectServer syncObjectServer = null;
     try {
         syncObjectServer = new SyncObjectServer(client_home, the_name_space);
      } catch (Exception e) {
         System.err.println(e);
         e.printStackTrace();
         System.exit(1);
      }
      return syncObjectServer;

   }
   /*
   public static void openAndReplicateInitialServer(String args[], SyncDriver driver) {
     if ((driver != null) && (args.length >=2))
                                         {
                                                 String server_name = args[1];
                                                 driver.openAndReplicateServer(server_name);
					 }

   }
   */
   public static ServerProxy openAndReplicateInitialServer(Hashtable argsTable, SyncDriver driver) {
    //if ((driver != null) && (args.length >=2))
   	String server_name = (String) argsTable.get(SERVER);
   	ServerProxy retVal = null;
   	if (server_name != null )
                                        {
                                                //String server_name = args[1];
                                                retVal = driver.openAndReplicateServer(server_name);
					 }
   	return retVal;

  }
   public static void  createRegisteredApplication (ServerProxy serverProxy, Hashtable argsTable) {
    //if ((driver != null) && (args.length >=2))
   	String appName = (String) argsTable.get(APPLICATION);
   	if (appName ==null) return;
   	if (serverProxy == null) {
   		System.out.println ("Warning: application argument ignored as valid server not specified");
   		return;
   	}
   	String instanceName = (String) argsTable.get(MODEL_NAME);
   	if (instanceName == null) {
   		serverProxy.newObject(appName);
   	} else {
   		serverProxy.newObject(instanceName, appName);
   	}
  }
   String globalEditorClass;
   public void setEditorClass (String theClass) {
   	globalEditorClass = theClass;
   }
   public String getEditorClass() {
   	return globalEditorClass;
   }
   boolean autoOpen = false;
   public void setAutoOpen (boolean newVal) {
   	autoOpen = newVal;
   }
   public boolean getAutoOpen() {
   	return autoOpen;
   }
   public static void  processAutoOpen (SyncClient client, Hashtable argsTable) {
    //if ((driver != null) && (args.length >=2))
   	client.setAutoOpen (argsTable.get(AUTO_OPEN)!= null);
  }
    boolean noDuplicates = false;
   public  void setNoDuplicates (boolean newVal) {
   	noDuplicates = newVal;
   }
   public  boolean getNoDuplicates() {
   	return noDuplicates;
   }
   public static void  processNoDuplicates (SyncClient client, Hashtable argsTable) {
    //if ((driver != null) && (args.length >=2))
   	client.setNoDuplicates (argsTable.get(NO_DUPLICATES)!= null);
  }
   public static void  registerEditorClass (SyncClient client, Hashtable argsTable) {
    //if ((driver != null) && (args.length >=2))
   	//String modelClass = (String) argsTable.get(MODEL_CLASS);
   	String editorClass = (String) argsTable.get(EDITOR_CLASS);
   
   	if (editorClass != null) {
   		client.setEditorClass(editorClass);
   	}
  }
   
  public static void setTrace(Hashtable argsTable) {
  	if (argsTable.containsKey(SyncServer.TRACE))
  		Sync.setTrace(true);
  }
  public static String getClientID(Hashtable argsTable) {
  	if (argsTable.containsKey(CLIENT_ID))
  		return (String) argsTable.get(CLIENT_ID);
  	return "";
  }
   
   public static void  createUnregisteredApplication (ServerProxy serverProxy, Hashtable argsTable, Object model, SyncApplication editor) {
    //if ((driver != null) && (args.length >=2))
   	String modelClass = (String) argsTable.get(MODEL_CLASS);
   	String editorClass = (String) argsTable.get(EDITOR_CLASS);
   	String instanceName = (String) argsTable.get(MODEL_NAME);
   	
   	//if (modelClass == null) /*&& (editorClass == null))*/ return;
   	/*
   	if (modelClass == null) {
   		System.out.println ("Warning: must specify model class for editor class");
   	}
   	*/
   	/*
   	if (modelClass != null) {
   		serverProxy.syncClient.setModelClass(modelClass);
   	}
   	*/
   	
   	if (serverProxy == null) {
   	 System.out.println ("Warning: request to create model ignored as valid server not specified");
   	  return;
   	} 
   	if (editorClass != null) {
   		serverProxy.syncClient.setEditorClass(editorClass);
   	}
   	if (model != null && instanceName != null && editor == null) {
   		serverProxy.newObject(instanceName, model, editorClass);
   		return;
   	} else if (model != null && instanceName != null && editor != null) {
   		serverProxy.newObject(instanceName, model, editor);
   		return;
   	}
   	if (modelClass == null) /*&& (editorClass == null))*/ return;
   	if (instanceName == null) {
   		serverProxy.newObject (null, modelClass, editorClass);
   	} else 
   		serverProxy.newObject(instanceName, modelClass, editorClass); 
  }
   static PropertiesTable client_props;
   public static PropertiesTable getPropertiesTable() {
   	return client_props;
   }
   public static String getHomeDirectory(Properties sync_props) {
	if (sync_props == null) return ".";
	else
	return sync_props.getProperty("sync.client.home");
	
}
   public static SyncClient instantiate(String[] args) {
	   return instantiate (args, null, null);
   }
   public static SyncClient instantiate(String[] args, Object model) {
	   return instantiate (args, model, null);
   }
    static SyncClient instantiate(String[] args, Object model, SyncApplication editor) // will add model processing later
   {
     /*
      Properties sync_props = new Properties();
      File sync_props_file = new File(System.getProperty("user.home") + File.separator + "sync.jprops");
      try {
         sync_props.load(new FileInputStream(sync_props_file));
         Sync.setProperties(sync_props);
      } catch (Exception ex) {
         System.err.println(ex);
         System.exit(1);
      }
     */
     Properties sync_props = getSyncProps();

     //String client_home = sync_props.getProperty("sync.client.home");
     String client_home = getHomeDirectory(sync_props);
      /*
      try {
         new SyncObjectServer(client_home);
      } catch (Exception e) {
         System.err.println(e);
         e.printStackTrace();
         System.exit(1);
      }
      */
     /*PropertiesTable*/ client_props = getClientProps(sync_props, client_home);

      /*
      try {
         File props_file = new File(client_home, "client.jprops");
     // add client properties to Sync properties so they are accessible
     // from within Replicated objects via static Sync.getProperty method
         sync_props.load(new FileInputStream(props_file));
         PropertiesTable client_props = new PropertiesTable(props_file);
         */
     //String host_name = getHostName(sync_props);
      /*

   String host_name;
   try {
    host_name = java.net.InetAddress.getLocalHost().getHostName();
                         if (!sync_props.getProperty("domain").trim().equals("") &&
          host_name.indexOf('.') == -1) host_name = host_name + "." + sync_props.getProperty("domain");
     //if (host_name.indexOf('.') == -1) host_name = host_name + "." + sync_props.getProperty("domain");
   } catch (java.net.UnknownHostException ex) {
    host_name = "localhost";
   }
      */
     Hashtable argTable = MainArgsProcessor.toTable(regFlags, boolFlags, new String[0], args);

     createSyncObjectServer(client_home, getClientID(argTable));
     setTrace(argTable);
     //SyncClient client = createSyncClient(args, sync_props, client_props);
     SyncClient client = createSyncClient(argTable, sync_props, client_props);
     client.setServerID(SyncServer.getServerID(argTable));
     Sync.setSyncClient(client);
     
     createUI (client.ui, argTable);
     
      /*

     // String host_name = sync_props.getProperty("localhost");
   String port = sync_props.getProperty("im);
   SyncClient client = null;

     // Create and install a security manager
     //System.setSecurityManager(new RMISecurityManager());
                 String id = "";

   try{
    String name = "//" + host_name + ":" + port + "/SyncClient";
                         if (args.length >=3) {
                           name += args[2];
                           id = args[2];
                         }
    client = new SyncClient(id, client_props, name);
      */
     //SyncDriver driver = createSyncDriver(args, client);
     SyncDriver driver = createSyncDriver(argTable, client);
     processAutoOpen(client, argTable);
     processNoDuplicates(client, argTable);
     registerEditorClass(client, argTable);
     //openAndReplicateInitialServer(args, driver);
     ServerProxy serverProxy = openAndReplicateInitialServer(argTable, driver);
     if (serverProxy != null) {
     	client.setServerProxy(serverProxy);
     //ServerProxy serverProxy = client.getServerProxyTable().get(serverName);
     createRegisteredApplication (serverProxy, argTable);
     createUnregisteredApplication (serverProxy, argTable, model, editor);
     if (argTable.containsKey(RT)) {
    	 serverProxy.setRealTimeSynchronize(true);
     	 driver.setSynchronousMode(false);
     }
     } 
     //SyncDriver driver = createSyncDriver(serverProxy, argTable, client);
     createOEUI(serverProxy, argTable, client);
     createSyncControlsUI(serverProxy, argTable);
     
     
      /*

    if (args.length >=1){
     if (args[0].equals(new String("driver"))){
      SyncDriver driver = new SyncDriver(client);
      ObjectEditor.edit(driver);

      if (args.length >=2)
      {
       String server_name = args[1];
       driver.openAndReplicateServer(server_name);
      }


     }
    }
      */

    /*
    if (args.length >=1)
    {
     if (args[0].equals(new String("s")))
      Sync.setSyncMode(true);
     else
      Sync.setSyncMode(false);

     if (args.length >=2)
     {
      String server_name = args[1];
      client = new SyncClient(client_props, name, server_name);
     }
     else
      client = new SyncClient(client_props, name);
    }
    else
    {
     Sync.setSyncMode(false);
     client = new SyncClient(client_props, name);
    }
    */

     //System.out.println("will try to rebind");
     //Naming.rebind(name, client);
     //System.out.println(name + " bound in registry");
     //}
                 /*
   catch (Exception e) {
    System.out.println("111111111111");
    System.out.println("SyncClient error: " + e.getMessage());
    e.printStackTrace();
   }
                 */

     // trying something new -- Vibhor
     //Sync.setSyncClient(client);
     //
     //System.out.println("Started Sync client");
         /*
      } catch (Exception ex) {
         System.err.println("Error loading properties: " + ex);
         System.exit(1);
      }
         */
     return client;
   }
  
   public static void main(String[] args)
   {
	
     instantiate(args);
   }
   static void addServer (Vector<String> args, String server) {
	   args.add(SERVER);
	   args.add(server);
   }
   static void addClientID (Vector<String> args, String id) {
	   args.add(CLIENT_ID);
	   args.add(id);
   }
   static void addRMIPort (Vector<String> args, String rmiPort) {
	   args.add(SyncServer.RMI_PORT);
	   args.add(rmiPort);
   }
   static void addEditorClass (Vector<String> args, Class editor) {
	   if (editor == null) return;
	   args.add(EDITOR_CLASS);
	   args.add(editor.getName());
   }
   static void addModelClass (Vector<String> args, Class model) {
	   args.add(MODEL_CLASS);
	   args.add(model.getName());
   }
   static void addModelName (Vector<String> args, String name) {
	   args.add(MODEL_NAME);
	   args.add(name);
   }
   static void addNoDuplicates (Vector<String> args) {
	   args.add(NO_DUPLICATES);
   }
   static void addSynchronous (Vector<String> args) {
	   args.add(RT);
   }
   static void addTrace (Vector<String> args) {
	   args.add(SyncServer.TRACE);
   }
   static void addAutoOpen (Vector<String> args) {
	   args.add(AUTO_OPEN);
   }
   static void addOE (Vector<String> args) {
	   args.add(SyncServer.OE);
   }
   static void addDefaults(Vector<String> args) {
	   addNoDuplicates(args);
	   addSynchronous(args);
	   //addTrace(args);
	   addAutoOpen(args);
	   //addOE(args);
   }
   static String[]  toArray(Vector<String> v) {
	   String[] retVal = new String[v.size()];
	   for (int i = 0; i < v.size(); i++)
		   retVal[i] = v.elementAt(i);
	   return retVal;
   }
   public static SyncClient replicate (String server,  String modelName, Class modelClass, Class editorClass, String clientName) {
	   Vector<String> args = new Vector();
	   addDefaults(args);
	   addServer(args, server);
	   addModelName(args, modelName);
	   addClientID(args, clientName);
	   if (editorClass != null) {
		   addEditorClass (args, editorClass);
		   //addAutoOpen(args);
	   }
	   addModelClass(args, modelClass);
	   return instantiate (toArray(args));
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient replicate (String server,  String modelName, Class modelClass, Class editorClass, String clientName, String[] userArgs) {
	   Vector<String> args = new Vector();
	   addDefaults(args);
	   addServer(args, server);
	   addModelName(args, modelName);
	   addClientID(args, clientName);
	   if (editorClass != null) {
		   addEditorClass (args, editorClass);
		   //addAutoOpen(args);
	   }
	   addModelClass(args, modelClass);
	   for (int i = 0; i < userArgs.length; i++)
		   args.add(userArgs[i]);
	   return instantiate (toArray(args));
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient replicate (String server,  String modelName, Object model, Class editorClass, String clientName) {
	   Vector<String> args = new Vector();
	   addDefaults(args);
	   addServer(args, server);
	   addModelName(args, modelName);
	   addClientID(args, clientName);if (editorClass != null) {
		   addEditorClass (args, editorClass);
		   //addAutoOpen(args);
	   }
	   return instantiate (toArray(args), model);
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient replicate (String server,  String modelName, Object model, Class editorClass, String clientName, String[] userArgs) {
	   Vector<String> args = new Vector();
	   addDefaults(args);
	   addServer(args, server);
	   addModelName(args, modelName);
	   addClientID(args, clientName);if (editorClass != null) {
		   addEditorClass (args, editorClass);
		   //addAutoOpen(args);
	   }
	   for (int i = 0; i < userArgs.length; i++)
		   args.add(userArgs[i]);
	   return instantiate (toArray(args), model);
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient replicate (String server,  String modelName, Object model, String clientName) {
	   return replicate (server, modelName, model, null, clientName);
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient open (String server,  String modelName,  Class editorClass, String clientName) {
	   Vector<String> args = new Vector();
	   addDefaults(args);
	   addServer(args, server);
	   addModelName(args, server);
	   addClientID(args, clientName);
	   addEditorClass (args, editorClass);
	   return instantiate (toArray(args));
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient open (String server,  String modelName,  Class editorClass, String clientName, String[] userArgs) {
	   Vector<String> args = new Vector();
	   addDefaults(args);
	   addServer(args, server);
	   addModelName(args, server);
	   addClientID(args, clientName);
	   addEditorClass (args, editorClass);
	   for (int i = 0; i < userArgs.length; i++) {
		   args.add(userArgs[i]);		   
	   }
	   return instantiate (toArray(args));
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient open (String server,  String modelName,   String clientName) {
	   return open (server, modelName, null, clientName);
	   /*
	   Vector<String> args = new Vector();
	   addDefaults(args);
	   addServer(args, server);
	   addModelName(args, server);
	   addClientID(args, clientName);
	   addEditorClass (args, editorClass);
	   return instantiate (toArray(args));
	   */
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   
}
