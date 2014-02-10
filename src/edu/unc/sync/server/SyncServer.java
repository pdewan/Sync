package edu.unc.sync.server;

import edu.unc.sync.*;
import bus.uigen.ObjectEditor;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
//import javax.swing.*;
//import javax.swing.tree.*;
//import com.sun.java.swing.*;
//import com.sun.java.swing.tree.*;
//import javax.swing.*;
import javax.swing.tree.*;

import util.misc.MainArgsProcessor;

public class SyncServer
   //extends UnicastRemoteObject
   implements RemoteSyncServer
{
	
	public static final String UI = "--ui";
	public static final String OE = "--oe";
	public static final String RMI_PORT = "--rmi_port";
	public static final String TRACE = "--trace";
	public static final String SERVER_ID = "--server_id";
	public static final String[] boolFlags = {UI, OE, TRACE};
	public static final String[] regFlags = {RMI_PORT, SERVER_ID};
   SyncObjectServer object_server;
   SyncServerInterface ui;
   File deltas_file, deltas_dir;
   Hashtable deltas = null;
   public Vector request_log;
   Hashtable clientnames = new Hashtable();
   String server_id = "";
   String rmiName = "";
   String hostName = "";
   //public boolean trace = false;
   static String globalSourceName;
   static String globalId;
   public static String getFullName() {
	   return globalSourceName;
   }
   public static String getServerId() {
	   return globalId;
   }

   public SyncServer(PropertiesTable properties, String theHostName, String theRMIName, String the_server_id) throws RemoteException
   {
      super(); 
      if (Sync.getTrace())
    	  System.out.println("Sync Server of Sep 21, 06, 7:05pm" );
      Sync.setIsServer(true);
      globalSourceName = theHostName + "(" + the_server_id + ")";
      rmiName = theRMIName;
      hostName = theHostName;
      server_id = the_server_id;
      globalId = server_id;
      object_server = Sync.getObjectServer();
      ui = new SyncServerInterface(this, properties);
      // new BasicMergeActions();  // not used anymore

      String server_home = Sync.getProperty("sync.server.home");
      //System.out.println("Abt to read deltas file:");
      try {
      deltas_file = new File(server_home, "deltas.sjo");
      //System.out.println("Did file operation:");
      if (deltas_file.exists()) {
       //System.out.println("Checked exists");
         try {
            deltas = (Hashtable) (new ObjectInputStream(new FileInputStream(deltas_file))).readObject();
         } catch (Exception e) {
            throw new SyncException("Error instantiating object store: " + e.toString());
         }
      } else {
         deltas = new Hashtable(100);
      }
      deltas_dir = new File(server_home, "deltas");
      if (!deltas_dir.exists()) deltas_dir.mkdir();
      } catch (Exception e) {
    	  //e.printStackTrace();
    	  //System.out.println("could not read deltas file");
    	  deltas = new Hashtable(100);
      }
   }
   void setRMIName(String newVal) {
   	rmiName = newVal;
   }
   String getRMIName() {
   	return rmiName;
   }
  
   void setHostName (String newVal) {
    hostName = newVal;
   }
   String getHostName() {
   	return hostName;
   }
   
   public Hashtable getClients() throws RemoteException {
   	return clientnames;
   }
   
   public String getServerID() {
   	return server_id;
   }

   public Replicated getObject(ObjectID oid) throws RemoteException
   {
      try {
         Replicated obj = object_server.getObject(oid);
         if (obj.hasChanged()) commitCurrentChanges(obj);
         return obj;
      } catch(Exception ex) {
         throw new RemoteException(ex.toString());
      }
   }

   public ObjectID putObject(Replicated object) throws RemoteException
   {
      object_server.putObject(object);
      return object.getObjectID();
   }
/*
   public Object getAttribute(ObjectID oid, String attr) throws RemoteException
   {
      try {
         return object_server.getAttribute(oid, attr);
      } catch(SyncException ex) {
         throw new RemoteException(ex.toString());
      }
   }

   public void setAttribute(ObjectID oid, String attr, Object value) throws RemoteException
   {
      try {
         object_server.setAttribute(oid, attr, value);
      } catch(SyncException ex) {
         throw new RemoteException(ex.toString());
      }
   }
*/
   public TreeNode getTreeRoot() throws RemoteException
   {
      Folder root = (Folder) object_server.getRootObject();
      return new FolderTreeNodeCopy((FolderTreeNode) root.getTreeNode(), this.getServerID());
   }

   public ChangeVector getChangeVector(ObjectID oid)
   {
      ChangeVector V;
      Object obj = deltas.get(oid);
      if (obj == null) {
         V = new ChangeVector(Sync.getObject(oid).getVersion());
         deltas.put(oid, V);
         return V;
      } else if (obj instanceof ChangeVector) {
         return (ChangeVector) obj;
      } else if (obj instanceof File) {
         try {
            V = (ChangeVector) (new ObjectInputStream(new FileInputStream((File) obj))).readObject();
            deltas.put(oid, V);
            return V;
         } catch (Exception ex) {
            System.err.println("Error reading change vector for object " + oid + ": " + ex);
            V = new ChangeVector(Sync.getObject(oid).getVersion());
            deltas.put(oid, V);
            return V;
         }
      } else {
         System.err.println("Error: unexpected type in getChangeVector: " + obj.getClass().getName());
         return null;
      }
   }
   public synchronized Change synchronizeObject(ObjectID oid, Change client_changes) throws RemoteException
  {


     Replicated obj = null;
     try {
        obj = object_server.getObject(oid);
     } catch (SyncException ex) {
        throw new RemoteException("Error synchronizing object: " + ex.toString());
     }
     if (Sync.getTrace()) {
     System.out.println("Object for OID" + oid);
     client_changes.print();
     printChangeInfo(obj);
     client_changes.print();
     }
     //System.out.println("Got reference for object " + oid);
     if (obj.hasChanged()) {
        //System.out.println("Committing current changes");
        try {
           commitCurrentChanges(obj);
        } catch (ReplicationException ex) {
           System.err.println("Error committing current changes");
           throw new RemoteException(ex.toString());
        }
        //System.out.println("Committed current changes");

     } /*
        else //PD: adding this because client changes seem to be in the set
       obj.clearChanged();
        */
     int client_version = client_changes.getFromVersion();
     int server_version = obj.getVersion();

     //System.out.println("Client at version " + client_version + ", server at version " + server_version);

     Change server_changes = null;
     if (client_version == server_version) {
        server_changes = new NullChange();
     } else if (client_version > server_version) {
        throw new RemoteException("Error synchronizing object: client version should not be greater than server version.  Reload object.");
     } else {
        ChangeVector V = getChangeVector(oid);
        try {server_changes = V.unionChangeSets(obj, client_version);}
        catch (ReplicationException ex) {
           throw new RemoteException("Error concatenating server change sets: " + ex);
        }
        if (server_changes instanceof ChangeSet && ((ChangeSet) server_changes).isEmpty())
          server_changes = new NullChange();
     }
     /*
     System.out.println("Server changes:");
     server_changes.print();
     */



     Change to_client = null;
     try {
        to_client = mergeChanges((Replicated) obj, server_changes, client_changes);
     } catch (Exception ex) {
        System.err.println("Error merging changes to object " + oid + ": " + ex);
        ex.printStackTrace();
        throw new RemoteException(ex.toString());
     }
     to_client.setFromVersion(client_version);
     to_client.setToVersion(obj.getVersion());
     /*
     System.out.println("HasChanged" + obj.hasChanged());
     if (obj.getChange() != null)
       obj.getChange().print();
     */
     //System.out.println("Before Refresh");
     //printChangeInfo(obj);
     ui.doRefresh();
     /*




         //
         to_client.fix();
     //System.out.println("Before Returning");
     //printChangeInfo(obj);
     //System.out.println("Returning to " + name + "oid " + oid + ".  Changes:");
     to_client.print();
     */
     to_client.fix(); // moved this before notification
     return to_client;

   }

   public Change synchronizeObject(ObjectID oid, Change client_changes, String name, RemoteSyncClient remote_obj) throws RemoteException
   {
     synchronized (this) {

     /*
         System.out.println("@@@@@@@@@@@@");
         System.out.println("Registering client: " + name);
         System.out.println("Object: " + remote_obj);
     */
     	//String clientName = "rmi:" + name;
     	String clientName = name;
     if (!clientnames.containsKey(clientName)) {
     	notifyClientsNewClient(clientName, (RemoteSyncClient) remote_obj, true);
     }
     if (clientnames.get(clientName) != remote_obj) {
     	clientnames.put(clientName, remote_obj);
     	//notifyClientsNewClient(clientName, (RemoteSyncClient) remote_obj, true);
     }
     
          // null args provided to allow clients to simply register themselves
          if (oid == null || client_changes == null) return null;
      if (Sync.getTrace()) {
      	System.out.println("Received request for synchronizing from " + name + "oid " + oid + ".  Changes:");
      	client_changes.print();
      }



/*
	  for (Enumeration e = clientnames.elements(); e.hasMoreElements(); ){
		  RemoteSyncClient client = (RemoteSyncClient) e.nextElement();
		  System.out.println("12121212 " + client);
		  if (!client.equals((RemoteSyncClient)remote_obj)){
			  System.out.println("client not same, will call notifyAction");
			  try{
				  client.notifyAction();
			  }
			  catch (Exception ex){
				  System.out.println("Error when calling notifyAction for client " + client);
				  ex.printStackTrace();
			  }
		  }
		  else
		  {
			  System.out.println("client same, will do nothing");
		  }
	  }
*/
      Change to_client = synchronizeObject(oid, client_changes);
      /*

      Replicated obj = null;
      try {
         obj = object_server.getObject(oid);
      } catch (SyncException ex) {
         throw new RemoteException("Error synchronizing object: " + ex.toString());
      }
      //System.out.println("Object for OID" + oid);
      //printChangeInfo(obj);
      //System.out.println("Got reference for object " + oid);
      if (obj.hasChanged()) {
         //System.out.println("Committing current changes");
         try {
            commitCurrentChanges(obj);
         } catch (ReplicationException ex) {
            System.err.println("Error committing current changes");
            throw new RemoteException(ex.toString());
         }
         //System.out.println("Committed current changes");

      }
      int client_version = client_changes.getFromVersion();
      int server_version = obj.getVersion();

      //System.out.println("Client at version " + client_version + ", server at version " + server_version);

      Change server_changes = null;
      if (client_version == server_version) {
         server_changes = new NullChange();
      } else if (client_version > server_version) {
         throw new RemoteException("Error synchronizing object: client version should not be greater than server version.  Reload object.");
      } else {
         ChangeVector V = getChangeVector(oid);
         try {server_changes = V.unionChangeSets(obj, client_version);}
         catch (ReplicationException ex) {
            throw new RemoteException("Error concatenating server change sets: " + ex);
         }
         if (server_changes instanceof ChangeSet && ((ChangeSet) server_changes).isEmpty())
           server_changes = new NullChange();
      }




      Change to_client = null;
      try {
         to_client = mergeChanges((Replicated) obj, server_changes, client_changes);
      } catch (Exception ex) {
         System.err.println("Error merging changes to object " + oid + ": " + ex);
         ex.printStackTrace();
         throw new RemoteException(ex.toString());
      }
      to_client.setFromVersion(client_version);
      to_client.setToVersion(obj.getVersion());

      //System.out.println("Before Refresh");
      //printChangeInfo(obj);
      ui.doRefresh();
      */

      /*
      System.out.println("Returning to " + name + "oid " + oid + ".  Changes:");
      to_client.print();
      */




	  //
          if (!(client_changes instanceof NullChange))
            notifyClients(name, remote_obj, oid);
            /*
	  for (Enumeration e = clientnames.elements(); e.hasMoreElements(); ){
		  RemoteSyncClient client = (RemoteSyncClient) e.nextElement();
		  //System.out.println("12121212 " + client);

		  if (!client.equals((RemoteSyncClient)remote_obj)){
			  //System.out.println("client not same, will call notifyAction");

                            asyncNotifyClient(client);

		  }
		  else
		  {
			  //System.out.println("client same, will do nothing");
		  }
	  }
            */
	  // not sure why this is after notifying - commenting it out
	  //to_client.fix();
      //System.out.println("Before Returning");
      //printChangeInfo(obj);
      if (Sync.getTrace()) {
      System.out.println("Returning to " + name + "oid " + oid + ".  Changes:");
      to_client.print();
      }

      return to_client;
     }
   }
void notifyClients (String name, RemoteSyncClient remote_obj, ObjectID oid) {

          //for (Enumeration e = clientnames.elements(); e.hasMoreElements(); ){
		    for (Enumeration keys= clientnames.keys(); keys.hasMoreElements();) {
		    	  String clientName =  (String) keys.nextElement();
		    	  RemoteSyncClient client = (RemoteSyncClient) clientnames.get(clientName);
                  //RemoteSyncClient client = (RemoteSyncClient) e.nextElement();
                  //System.out.println("12121212 " + client);

                  if (!client.equals((RemoteSyncClient)remote_obj)){
                          //System.out.println("client not same, will call notifyAction");
                          /*
                          if (Sync.getSyncMode())
                            notifyClient(client);
                         // else
                           //*/
                            asyncNotifyClientNewChange(clientName, client, name, remote_obj, oid);
                  		
                          /*
                          try{
                                  client.notifyAction();
                          }
                          catch (Exception ex){
                                  System.out.println("Error when calling notifyAction for client " + client);
                                  ex.printStackTrace();
                          }
                          */
                  }
                  else
                  {
                          //System.out.println("client same, will do nothing");
                  }
	  }
}
void notifyClientsNewClient (String newClientName, RemoteSyncClient newClient, boolean entered) {

    //for (Enumeration e = clientnames.elements(); e.hasMoreElements(); ){
	    for (Enumeration keys= clientnames.keys(); keys.hasMoreElements();) {
	    	  String clientName =  (String) keys.nextElement();
	    	  RemoteSyncClient client = (RemoteSyncClient) clientnames.get(clientName);
	    	  //if (clientName.equals(newClientName)) continue;
	    	  asyncNotifyClientNewClient(newClientName, newClient, client, entered);
	    }
}

   void notifyClientNewChange(String clientName, RemoteSyncClient client, String theSourceName, RemoteSyncClient theSource, ObjectID theOid) {
     try{
     	if (Sync.getTrace())
     	System.out.println ("notifying clients");
       //client.notifyAction(((FolderTreeNodeCopy)this.getTreeRoot()).getObjectID());
    	client.notifyAction(((FolderTreeNodeCopy)this.getTreeRoot()).getObjectID(), theSourceName, theSource, theOid);
     }
     catch (Exception ex){
     	synchronized (this) {
       if (clientnames.containsKey(clientName)) {
       	if (Sync.getTrace()) {
       	System.out.println("Error when calling notifyAction for client " + clientName);
       	System.out.println("Dropping the client ");
       	}
       	clientnames.remove(clientName);
       	notifyClientsNewClient(clientName, client, false);
       }
     	}
     }

   }
   void notifyClientNewClient(String clientName, RemoteSyncClient client, RemoteSyncClient theDestination, boolean entered) {
    //Serializable foo = client;
   	try{
    	if (Sync.getTrace())
    	System.out.println ("notifying clients");
      //client.notifyAction(((FolderTreeNodeCopy)this.getTreeRoot()).getObjectID());
    	if (entered)
    		theDestination.clientJoined (((FolderTreeNodeCopy)this.getTreeRoot()).getObjectID(), clientName, client);
    	else
    		theDestination.clientLeft (((FolderTreeNodeCopy)this.getTreeRoot()).getObjectID(), clientName, client);
    }
    catch (Exception ex){
    	

       Object obj = clientnames.remove(clientName);
       if (obj != null) {
      System.out.println("Error when calling notifyAction for client " + clientName);
      System.out.println("Dropping the client ");
       }
      //ex.printStackTrace();
    }

  }

   class ClientNewChangeNotifier implements Runnable {
       RemoteSyncClient client;
       String clientName;
       RemoteSyncClient source;
       ObjectID oid;
       String sourceName;
       public ClientNewChangeNotifier(String theClientName, RemoteSyncClient theClient, String theSourceName, RemoteSyncClient theSource, ObjectID theOid) {
         client = theClient;
         clientName = theClientName;
         source = theSource;
         oid = theOid;
         sourceName = theSourceName;
       }
       public void run() {
         notifyClientNewChange(clientName, client, sourceName, source, oid);
       }
   }
   void asyncNotifyClientNewChange(String clientName, RemoteSyncClient client, String sourceName,  RemoteSyncClient source, ObjectID oid) {
     (new Thread(new ClientNewChangeNotifier(clientName, client, sourceName, source, oid))).start();
   }
   class ClientNewClientNotifier implements Runnable {
    RemoteSyncClient client;
    String clientName;
    RemoteSyncClient destination;
    boolean entered;
    public ClientNewClientNotifier(String theClientName, RemoteSyncClient theClient, RemoteSyncClient theDestination, boolean theEntered) {
      client = theClient;
      clientName = theClientName;
      destination = theDestination;
      entered = theEntered;
    }
    public void run() {
      notifyClientNewClient(clientName, client, destination, entered);
    }
}
void asyncNotifyClientNewClient(String clientName, RemoteSyncClient client,  RemoteSyncClient destination, boolean entered) {
  (new Thread(new ClientNewClientNotifier(clientName, client, destination, entered))).start();
}



   public static void printChangeInfo (Replicated object) {
     System.out.println("Object: " + object);
     System.out.println("HasChanged: " + object.hasChanged());
     System.out.println("Changes:");
     System.out.println(object.getChange());
     if (object.getChange() != null)
       object.getChange().print();
     System.out.println("End Change Info" );
   }
   protected void commitCurrentChanges(Replicated object) throws ReplicationException
   {
      //System.out.println("object class: " + object.getClass().getName());
      //System.out.println("Object" + object);
      //System.out.println("before commit change");
      //printChangeInfo(object);
      Change current = object.getChange();
      ChangeVector V = getChangeVector(object.getObjectID());
      if (current != null) {
      	if (Sync.getTrace()) {
         System.out.println("Current changes, version = " + object.getVersion());
         current.print();
      	}
         V.addElement(current);
         object.setVersion(object.getVersion() + 1);
      } else {
         //System.out.println("No current changes after all");
      }
      object.clearChanged();
      //System.out.println("After commit change");
      //printChangeInfo(object);
   }

   private void commitChanges(Replicated object, Change changes) throws ReplicationException
   {
      //System.out.println("In commitChanges. Changes to commit:");
      //changes.print();
      Change rejected = object.applyChange(changes);
      //System.out.println("In commitChanges. Changes rejected:");
      //if (rejected != null) rejected.print();
      //else System.out.println("none");
      ChangeVector V = getChangeVector(object.getObjectID());
      V.addElement(changes);
      object.clearChanged();
      object.setVersion(object.getVersion() + 1);
      //System.out.println("After clear change");
      //printChangeInfo(object);
   }

   private Change mergeChanges(Replicated object, Change C0, Change Cr) throws ReplicationException
   {
      // C0: changes made to central replica since last synchronization
      //     of remote replica
      // Cr: changes made to remote replica since its last synchronization.
      // M:  merged changes (pair: changes to apply to central replica
      //     and changes to apply to remote replica
      // A0: changes to apply to central replica
      // Ar: changes to apply to remote replica
      // i:  current version of remote replica
      // j:  current version of central replica

      Change A0, Ar;
      ChangePair M;

      if (C0 instanceof NullChange & Cr instanceof NullChange) {
         A0 = null;
         Ar = null;
      }
      else {
         M = object.mergeChanges(C0, Cr);
         A0 = M.central;
         Ar = M.remote;
      }

      if (A0 != null & !(A0 instanceof NullChange)) {
         // for correctness, this method must execute atomically
         commitChanges(object, A0);
      }

      return (Ar != null) ? Ar : new NullChange();
   }

   void saveDeltas()
   {
      for (Enumeration e = deltas.keys(); e.hasMoreElements(); ) {
         ObjectID oid = (ObjectID) e.nextElement();
         Object obj = deltas.get(oid);
         File file = new File(deltas_dir, oid.toString());
         try {
            (new ObjectOutputStream(new FileOutputStream(file))).writeObject(obj);
            deltas.put(oid, file);
         } catch (IOException ex) {
            System.err.println("Error saving changes to object " + oid.toString() + ": " + ex);
            deltas.remove(oid);
         }
      }
      try {
         (new ObjectOutputStream(new FileOutputStream(deltas_file))).writeObject(deltas);
      } catch (IOException ex) {
         System.err.println("Error saving object changes: " + ex);
      }
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
      saveDeltas();
   }
    static PropertiesTable getServerProps(Properties sync_props, String server_home) {
     PropertiesTable server_props = null;
      try {
         File props_file = new File(server_home, "server.jprops");
         server_props = SyncClient.getProps(sync_props, props_file);
         /*
         // add client properties to Sync properties so they are accessible
         // from within Replicated objects via static Sync.getProperty method
         sync_props.load(new FileInputStream(props_file));
         client_props = new PropertiesTable(props_file);
         */
      } catch (Exception ex) {
      	if (Sync.getTrace())
      System.err.println("Error loading properties: " + ex);
      return null;
      //System.exit(1);
      }
     return server_props;
   }
    static Registry myRegistry;
    public static Registry getRegistry (String host, int port) {
    	if (myRegistry != null)
    		return myRegistry;
    	Registry registry = null;
    	try {
            
    		 
            registry = LocateRegistry.getRegistry( host, port );
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    	myRegistry = registry;
        return registry;
    	
    }
    public static Registry getOrCreateRegistry(String host, int port) {
		if (myRegistry != null)
			return myRegistry;
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry(host, port);
			try {
				registry.lookup("Dummy");
			} catch (NotBoundException e) {
			} catch (Exception e) {
				// NotBoundException indicates that the registry exists, so we
				// should
				// not attempt to recreate it
				System.err.println("SessionRegistry: STARTING ...");
				registry = LocateRegistry.createRegistry(port);
			}
		} catch (Exception e) {
			System.err.println("SessionRegistry::startRegistry Error: "
					+ e.getMessage());
			e.printStackTrace();
		}
		myRegistry = registry;
		return registry;

	}
    public static String SYNC_SERVER = "SyncServer";
    static SyncServer createSyncServer(Properties sync_props, PropertiesTable server_props, Hashtable argsTable) {
     SyncServer syncServer = null;
     System.setSecurityManager(new RMISecurityManager());
     String port = (String) argsTable.get(RMI_PORT);
     if (port == null && sync_props != null)     
     	port = sync_props.getProperty("sync.rmiregistry.port");
     if (port == null)
    	 port = Sync.DEFAULT_RMI_PORT;
     String host_name = SyncClient.getHostName(sync_props);
     try {
        String server_id = getServerID(argsTable); 
        //String name = "//" + host_name + ":" + port + "/SyncServer";
        // old way, dunno  name should not be passed to SyncServer
        //String name = "//" + host_name + ":" + port + "/" + server_id;  
        String name = SYNC_SERVER +  "/" + server_id;
        SyncServer obj = new SyncServer(server_props, name, host_name, server_id);
        syncServer = obj;

       // old place
       // SyncServer exportedSyncServer = (SyncServer) UnicastRemoteObject.exportObject(syncServer, 0);
        
        // sasa way
        //System.err.println( "SessionRegistry: STARTING ..." );
        int portNumber = Integer.parseInt(port);
        
        Registry registry = getOrCreateRegistry( host_name, portNumber );
        //Remote exportedSyncServer =  UnicastRemoteObject.exportObject((SyncServer) syncServer, 0);
        RemoteSyncServer exportedSyncServer = (RemoteSyncServer) UnicastRemoteObject.exportObject( syncServer, 0);
        // old way
        //Naming.rebind(name, obj);
        // sasa way
        registry.rebind(name, exportedSyncServer);
        obj.setRMIName(name);
        obj.setHostName(host_name);
        if (Sync.getTrace())
        System.out.println(name + " bound in registry");

        // Sync.setSyncServer(obj);
     } catch (Exception e) {
        System.out.println("SyncServer could not be bound at specified port: " + e.getMessage());
        return syncServer;
        //e.printStackTrace();
      }
     
     return syncServer;

   }
    public static void createSyncDriver(Hashtable argsTable, SyncServer server) {
        SyncDriver theDriver = null;
        bus.uigen.uiFrame frame = null;
       
        if ((argsTable.containsKey(OE))) {
          //theDriver = new SyncDriver(server.getTreeRoot());
          //frame = ObjectEditor.edit(client.getServerProxyTable());       
          frame = ObjectEditor.edit(server.ui.getRootFolder());
          frame.setTitle("Sync Server:" + server.getHostName()+ "/" + server.getServerID());
        }

        //return theDriver;
      }
    public static void createUI(SyncServerInterface ui, Hashtable argsTable) {
 		
 			String uiFlag = (String) argsTable.get(UI);
 			if (uiFlag == null || ui == null)
 				return;
 			//ui.createUI("Sync Server");
 			ui.createUI();

 	}
    

   public static String getHomeDirectory(Properties sync_props) {
    	if (sync_props == null) return ".";
    	else
    	return sync_props.getProperty("sync.server.home");
    	
    }
   /*
   public void setTrace(boolean newVal) {
   	 trace = newVal;
   }
   public boolean getTrace() {
   	return trace;
   }
   */
   public static void setTrace(Hashtable argsTable) {
   	if (argsTable.containsKey(TRACE))
   		Sync.setTrace(true);
   }
   public static String getServerID(Hashtable argsTable) {
   	String retVal = "SyncServer";
   	if (argsTable.containsKey(SERVER_ID))
   		retVal = (String) argsTable.get(SERVER_ID);
   	return retVal.toLowerCase();
   }
   public static void instantiate(String[] args)
   {
     Properties sync_props = SyncClient.getSyncProps();
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


     String host_name = SyncClient.getHostName(sync_props);
     /*
      String host_name;
      try {
         host_name = java.net.InetAddress.getLocalHost().getHostName();
         if (!sync_props.getProperty("domain").trim().equals("") &&
          host_name.indexOf('.') == -1) host_name = host_name + "." + sync_props.getProperty("domain");
      } catch (java.net.UnknownHostException ex) {
         host_name = "localhost";
      }

      System.out.println("host_name is " + host_name);
     */
      // String host_name = sync_props.getProperty("localhost");
      //String port = sync_props.getProperty("sync.rmiregistry.port");
      //String server_home = sync_props.getProperty("sync.server.home");
       String server_home = getHomeDirectory(sync_props);
       Hashtable argsTable = MainArgsProcessor.toTable(regFlags, boolFlags, new String[0], args);
      SyncClient.createSyncObjectServer(server_home, getServerID(argsTable));
      /*
      try {
         // create object server and do Sync.setObjectServer() within
         new SyncObjectServer(server_home);
      } catch (Exception e) {
         System.err.println(e);
         e.printStackTrace();
         System.exit(1);
      }
      */
      PropertiesTable server_props = getServerProps(sync_props, server_home);
      /*
      PropertiesTable server_props = null;
      try {
         File props_file = new File(server_home, "server.jprops");
         server_props = new PropertiesTable(props_file);
         // add client properties to Sync properties so they are accessible
         // from within Replicated objects via static Sync.getProperty method
         sync_props.load(new FileInputStream(props_file));
      } catch (Exception ex) {
         System.err.println("Error loading properties: " + ex);
      }
      */

      // Create and install a security manager

      
      setTrace (argsTable);      
      SyncServer server = createSyncServer(sync_props, server_props, argsTable);
      
      createUI (server.ui, argsTable);
      //SyncClient.createSyncDriver(args, null);
      SyncServer.createSyncDriver(argsTable, server);
      /*
      System.setSecurityManager(new RMISecurityManager());
      try {
         SyncServer obj = new SyncServer(server_props);
         String name = "//" + host_name + ":" + port + "/SyncServer";
         Naming.rebind(name, obj);
         System.out.println(name + " bound in registry");
                 if (args.length >=1){
                         if (args[0].equals(new String("driver"))){
                                 SyncDriver driver = new SyncDriver(null);
                                 ObjectEditor.edit(driver);
                         }
                 }
         // Sync.setSyncServer(obj);
      } catch (Exception e) {
         System.out.println("SyncServer error: " + e.getMessage());
         e.printStackTrace();
      }
      */
   }

   public static void main(String[] args)
   {
	 System.out.println("Is this the  sync") ;
     instantiate(args);
   }
}
