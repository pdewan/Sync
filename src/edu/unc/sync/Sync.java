package edu.unc.sync;

import edu.unc.sync.server.*;

import java.io.*;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;

import util.trace.Tracer;
import util.models.Hashcodetable;

import bus.uigen.ObjectEditor;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.introspect.ClassDescriptorCache;
import bus.uigen.introspect.ClassDescriptorInterface;


public final class Sync
{
   // private static File properties_file = new File(System.getProperty("user.home"), "sync.properties");
   private static Properties properties = null;
   private static SyncObjectServer objectServer = null;
   private static boolean nested = false;
   public static boolean supressImplicit = false;
   private static boolean mode = false;
   public static String DEFAULT_RMI_PORT = "1099";

   // trying something -- Vibhor
   private /*protected*/ static SyncClient client = null;
   //

   public static Hashcodetable delegatedTable = new Hashcodetable();
   static Hashtable<Class,ObjectFactory> classToFactory = new Hashtable();
   private Sync() {}

   public static boolean getSyncMode(){
	   return mode;
   }

   public static void setSyncMode(boolean syncMode){
	   mode = syncMode;
   }

   //trying something --Vibhor
   public static void setSyncClient(SyncClient clnt){
	   client = clnt;
   }
   static boolean isServer;
   public static void setIsServer(boolean newVal) {
	   isServer = newVal;
   }
   public static boolean isServer() {
	   return isServer;
   }

   public static SyncClient getSyncClient(){
	   return client;
   }
   static boolean firstTime = true;
   public static void implicitSynchronize(ObjectID objectID) {
   		implicitSynchronize(objectID, false);
   	
   }
   static boolean notifyMode = false;
   public static boolean isRemote() {
	   return SyncClient.isRemote();
   }
   public static String getRemoteSourceName() {
	   return SyncClient.getRemoteSourceName();
   }
   public static String getSourceName() {
	   if (isServer)
		   return SyncServer.getFullName();
	   return SyncClient.getSourceName();
   }
   public static String getClientId() {
	   if (isServer)
		   return SyncServer.getServerId();
	   return SyncClient.getGlobalClientId();
   }
   public static String getSource() {
	   String retVal = getRemoteSourceName();
	   if (retVal == null)
		   return getClientId();
	   return retVal;
   }
   
   public static void implicitSynchronize(ObjectID objectID, boolean notified) { 
   	if (client == null) return;
   	if (notified) notifyMode = true;
   	if (/*(!nested || !notifyMode) &&*/ !supressImplicit )
   		//	&&    			!firstTime)
   	{
   		if (notifyMode)
   		//firstTime = false;
   			nested = true;
		
   			/*
			for (int i = 0; i < client.getServerProxyTable().size(); i++) {
				ServerProxy serverData = (ServerProxy) client.getServerProxyTable()
						.elementAt(i);
				if (serverData.objectID().getPrimary().equals(objectID.getPrimary()) && 
						(serverData.getRealTimeSynchronize() || Sync.getSyncMode())) {
				
					SyncClientInterface.synchronizeAction(client, serverData
						.objectID(), serverData.objectID(), null);
					break;
				}
			}
			*/
			for (Enumeration elements = client.getServerProxyTable().elements(); elements.hasMoreElements();) {
				ServerProxy serverData = (ServerProxy) elements.nextElement();
				if (serverData.objectID().getPrimary().equals(objectID.getPrimary()) && 
						serverData.objectID().getNameSpace().equals(objectID.getNameSpace()) &&
						(serverData.getRealTimeSynchronize() || Sync.getSyncMode())) {
				
					SyncClientInterface.synchronizeAction(client, serverData
						.objectID(), serverData.objectID(), null);
					break;
				}
			}
		
	} else {
		if (notifyMode)
			nested = false;
   		firstTime = false;
	}
   
   }
  
   
   /*
   public static void synchronize() {
		if (!nested) {
			nested = true;
			if (client.ui != null)
				client.ui.synchronizeAction();
			else {

				for (int i = 0; i < SyncClientInterface.serverDataList.size(); i++) {
					ServerData serverData = (ServerData) SyncClientInterface.serverDataList
							.elementAt(i);
					
					SyncClientInterface.synchronizeAction(client, serverData
							.objectID(), serverData.objectID(), null);
				}
			}
		} else
			nested = false;
	}
	*/
   /*
   public static void synchronize(){
	   if (!nested){
		   nested = true;
		   if (client != null){
			   client.ui.synchronizeAction();
		   }
		   else{
			   //System.out.println("client null in Sync.synchronize(), IGNORE if SyncServer");
		   }
		   nested = false;
	   }
   }
   */
   /*
   static Runnable syncThread = new SynchronizeThread();
   public static void asyncSynchronize() {
     new Thread(new SynchronizeThread()).start();
   }
   public class SynchronizeThread extends Thread  {
     public void run() {
       //client.ui.synchronizeAction();
     }
   }
   */


   //

   public static void setObjectServer(SyncObjectServer srvr)
   {
      objectServer = srvr;
   }

   public static SyncObjectServer getObjectServer()
   {
      return objectServer;
   }

   public static Replicated getObject(ObjectID oid) throws SyncException
   {
      return (objectServer != null) ? objectServer.getObject(oid) : null;
   }

   public static void putObject(Replicated obj)
   {
      if (objectServer != null) objectServer.putObject(obj);
   }

   /*
   public static Hashtable returnObjectTable(){
       if (objectServer != null) return objectServer.objects;
       else return null;
   }
   */

   public static Hashcodetable returnDelegatedTable(){
       return delegatedTable;
   }
   public static Replicated getReplicated(Object o) {
	  return (Replicated) Sync.delegatedTable.get(o);
   }
   public static Object getKey(Hashtable table, Object value) {
     Enumeration keys = table.keys();
     while (keys.hasMoreElements()) {
       Object key = keys.nextElement();
       if (table.get(key) == value) return key;
     }
     return null;
   }
   public static Object getDelegator(Object value) {
     if (value instanceof Delegated) {
       Delegated proxy = (Delegated) value;
       return proxy.returnObject();
     } else return null;
     //return getKey(delegatedTable, value);
   }
   public static Object getReplacedDelegator (Object obj) {

     if (!(obj instanceof ReplicatedReference)) {
              return Sync.getDelegator(obj);
    } else {
         ReplicatedReference reference = (ReplicatedReference) obj;
         Replicated referant = reference.getReferencedObject();
         return Sync.getDelegator(referant);
    }
   }

   public static void removeObject(ObjectID oid)
   {
      if (objectServer != null) objectServer.removeObject(oid);
   }
   public static boolean hasProperties() {
   	return properties != null;
   }
   public static String getProperty(String prop)
   {
   	if (properties == null) return "";
      return properties.getProperty(prop);
   }
   static boolean trace = false;
   public static void  setTrace(boolean newVal) {
  	 trace = newVal;
  }
  public static boolean getTrace() {
  	return trace;
  }
/*
   public static Object getAttribute(ObjectID oid, String attr) throws SyncException
   {
      return objectServer.getAttribute(oid, attr);
   }

   public static void setAttribute(ObjectID oid, String attr, Object value) throws SyncException
   {
      objectServer.setAttribute(oid, attr, value);
   }
*/
   public static Properties getProperties()
   {
      return properties;
   }

   public static void setProperties(Properties props)
   {
      properties = props;
      // properties.save(new FileOutputStream(properties_file), "# Sync properties");
   }

   public static void notifyRead(Replicated obj)
   {
      if (objectServer != null) objectServer.notifyRead(obj);
   }

   public static void notifyWrite(Replicated obj)
   {
      if (objectServer != null) objectServer.notifyWrite(obj);
   }

   // public static GenericLog requests = new GenericLog(50);
   // public static GenericLog merges = new GenericLog(50);
   
   public static void register (Class c, ObjectFactory factory) {
	   classToFactory.put(c, factory);
   }   
   public static ObjectFactory getObjectFactory(Class c) {
	   return classToFactory.get(c);
   }
   public static void setReplicateOutOfAllProperties (Class c, boolean newVal ) {
	   		ObjectEditor.setAttributeOfAllProperties(c, SyncAttributeNames.REPLICATE_OUT, newVal);
//	   		
//			ClassDescriptorInterface  cd = ClassDescriptorCache.getClassDescriptor(c);
//			cd.setAttribute (SyncAttributeNames.REPLICATE_OUT, newVal);		
		
   }
   // determines if changes to r are sent to the server
   public static void setReplicateOut (Replicated r, boolean newVal) {
	   r.setAttribute(SyncAttributeNames.REPLICATE_OUT, newVal);
   }
   // determines if insertions into a sequence r are sent to the server
   public static void setInsertOut (ReplicatedSequence r, boolean newVal) {
	   r.setAttribute(SyncAttributeNames.INSERT_OUT, newVal);
   }
  // determines if deletions from a sequence r are sent to the server
   public static void setDeleteOut (ReplicatedSequence r, boolean newVal) {
	   r.setAttribute(SyncAttributeNames.DELETE_OUT, newVal);
   }
   // determines if deletions from a hashtable r are sent to the server
   public static void setDeleteOut (ReplicatedDictionary r, boolean newVal) {
	   r.setAttribute(SyncAttributeNames.DELETE_OUT, newVal);
   }
// determines if modifications of elements of a sequence r are sent to the server
   public static void setModifyOut (ReplicatedSequence r, boolean newVal) {
	   r.setAttribute(SyncAttributeNames.MODIFY_OUT, newVal);
   }
// determines if puts into a hashtable r are sent to the server
   public static void setPutOut (ReplicatedDictionary r, boolean newVal) {
	   r.setAttribute(SyncAttributeNames.PUT_OUT, newVal);
   }
// determines if additions (appends) to a sequence r are sent to the server
   public static void setAddOut (ReplicatedSequence r, boolean newVal) {
	   r.setAttribute(SyncAttributeNames.ADD_OUT, newVal);
   }
   public static void setReplicateInOfAllProperties (Class c, boolean newVal ) {
	   ObjectEditor.setAttributeOfAllProperties(c, SyncAttributeNames.REPLICATE_IN, newVal);
//	   ClassDescriptorInterface  cd = ClassDescriptorCache.getClassDescriptor(c);
//		cd.setAttribute (SyncAttributeNames.REPLICATE_IN, newVal);	
	   
   }
   public static void setReplicateOfAllProperties (Class c, boolean newVal ) {
	   setReplicateOutOfAllProperties (c, newVal);
	   setReplicateOutOfAllProperties (c, newVal);
	   
   }
   public static void setReplicateOut (Class c, String property, boolean newVal ) {
	   ClassDescriptorInterface  cd = ClassDescriptorCache.getClassDescriptor(c);
		cd.setPropertyAttribute (property, SyncAttributeNames.REPLICATE_OUT, newVal);
	   
   }
   public static void setReplicateIn (Class c, String property, boolean newVal ) {
	   ClassDescriptorInterface  cd = ClassDescriptorCache.getClassDescriptor(c);
		cd.setPropertyAttribute (property, SyncAttributeNames.REPLICATE_IN, newVal);
	   
   }
   public static void setReplicate (Class c, String property, boolean newVal ) {
	   setReplicateOut (c, property, newVal);
	   setReplicateIn (c, property, newVal);
	   
   }
   public static void refreshAtributes (Class c ) {
	   ObjectEditor.refreshAttributes(c);
	   
   }
   
   public static SyncClient replicate (String server,  String modelName, Class modelClass, Class editorClass, String clientName) {
	   return SyncClient.replicate(server,  modelName,  modelClass,  editorClass,  clientName);
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static Object replicateOrLookup (String server,  String modelName, Class modelClass, Class editorClass, String clientName, String[] userArgs) {
	   SyncClient client = SyncClient.replicate(server,  modelName,  modelClass,  editorClass,  clientName, userArgs);
	   ServerProxy serverProxy = client.getServerProxy(server);
	   if (serverProxy == null) {
		   Tracer.error("Could not connect to server. Please start server and give it connect permissions using the correct java.policy file. Returning null model");
		   return null;
	   }
	   return serverProxy.getModel(modelName); 	  
   }
   public static Object replicateOrLookup (String server,  String modelName, Class modelClass, String clientName, String[] userArgs) {
	   return replicateOrLookup(server, modelName, modelClass, null, clientName, userArgs);
   }
   
   public static Object[] replicateOrLookup (String server,  String[] modelNames, Class[] modelClasses, String clientName, String[] userArgs) {
	   if (modelNames.length != modelClasses.length) {
		   Tracer.error("replicateOrLookup: no of modelNames not the same as number of model classes");		   
	   }
	   if (modelNames.length == 0) {
		   Tracer.warning("replicateOrLookup: no model names passed");
	   }
	   Object models[] = new Object[modelNames.length];
	   models[0] = replicateOrLookup(server, modelNames[0], modelClasses[0], null, clientName, userArgs);
	   ServerProxy proxy = client.getServerProxy(server);
	   
	   for (int i = 1; i < modelNames.length; i++) {
		   if (proxy == null)
			   models[i] = null;
		   else {
		   proxy.newObject(modelNames[i], modelClasses[i], (Class) null);
		   models[i] = proxy.getModel(modelNames[i]);
		   }
		   
	   }
	   return models;
	   //return replicateOrLookup(server, modelName, modelClass, null, clientName, userArgs);
   }
   
   public static SyncClient replicate (String server,  String modelName, Class modelClass,  String clientName) {
	   return SyncClient.replicate(server,  modelName,  modelClass,  null,  clientName);
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient replicate (String server,  String modelName, Object model, Class editorClass, String clientName) {
	   return SyncClient.replicate ( server,   modelName,  model,  editorClass,  clientName);
	   }
	 
	   
	 
   public static SyncClient replicate (String server,  String modelName, Object model, String clientName) {
	   return SyncClient.replicate (server, modelName, model, clientName);
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient replicate (String server,  String modelName, Object model, String clientName, String[] userArgs) {
	   return SyncClient.replicate (server, modelName, model, null, clientName, userArgs);
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   
   public static SyncClient open (String server,  String modelName,  Class editorClass, String clientName) {
	   return SyncClient.open(server, modelName, editorClass, clientName);
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static Object lookup (String server,  String modelName,   String clientName) {
	   SyncClient client = SyncClient.open(server, modelName, clientName);
	   ServerProxy serverProxy = client.getServerProxy(server);
	   return serverProxy.getModel(modelName); 	   
   }
   
   public static SyncClient open (String server,  String modelName,   String clientName) {
	   return SyncClient.open (server, modelName, clientName);
	   
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static SyncClient open (String server,  String modelName,   String clientName, String[] userArgs) {
	   return SyncClient.open (server, modelName, null, clientName, userArgs);
	   
	   
	   
	   
	   // --server localhost/A --client_id bob   --rmi_port 1099    --no_duplicates --synchronous --model  budget.ConcertExpense --editor SyncObjectEditor --instance demoBudget --trace --auto_open --oe
   }
   public static Object lookup (String server,  String modelName,   String clientName, String[] userArgs) {
	   SyncClient client = SyncClient.open(server, modelName, null, clientName, userArgs);
	   ServerProxy serverProxy = client.getServerProxy(server);
	   return serverProxy.getModel(modelName); 	   
   }

}
