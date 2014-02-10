package edu.unc.sync.server;

import edu.unc.sync.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class SyncObjectServer
{
   protected Replicated root;
   public Hashtable objects;
   //public Hashtable delegatedObjects = new Hashtable();
   // protected Hashtable attrs;
   File objects_home, attrs_home, objects_file, attrs_file, root_file;
   boolean writingToDisk = false;
   boolean readingFromDisk = false;
   String user_given_id = "";

   public SyncObjectServer(String home_dir, String the_user_given_id) throws SyncException
   {
      user_given_id = the_user_given_id;
   	  objects_home = new File(home_dir, "objects");
      if (!objects_home.exists()) objects_home.mkdir();
      // attrs_home = new File(home_dir, "attributes");
      // if (!attrs_home.exists()) attrs_home.mkdir();
      
      objects_file = new File(home_dir, "objects.sjo");
      if (objects_file.exists()) {
         try {
            objects = (Hashtable) (new ObjectInputStream(new FileInputStream(objects_file))).readObject();
         } catch (Exception e) {
            e.printStackTrace();
            throw new SyncException("Error instantiating object store: " + e.toString());
         }
      } else {
         objects = new Hashtable(100);
      }
/*
      attrs_file = new File(home_dir, "attributes.sjo");
      if (attrs_file.exists()) {
         try {
            attrs = (Hashtable) (new ObjectInputStream(new FileInputStream(attrs_file))).readObject();
         } catch (Exception e) {
            throw new SyncException("Error instantiating attributes store: " + e.toString());
         }
      } else {
         attrs = new Hashtable(100);
      }
*/      
      // must set the object server here because 
      // will need it in instantiating the root next
      Sync.setObjectServer(this);

      root_file = new File(home_dir, "rootOID.sjo");
      if (root_file.exists()) {
         ObjectID root_oid;
         try {
            root_oid = (ObjectID) (new ObjectInputStream(new FileInputStream(root_file))).readObject();
         } catch (Exception e) {
            throw new SyncException("Error instantiating root object identifier: " + e.toString());
         }
         root = getObject(root_oid);
      } else {
         root = new Folder();
         // root is put in object server through Replicated() constructor,
         // which by convention must be called in any Replicated class.
      }
      if (Sync.getTrace())
      	System.out.println("Sync object server started");
   }

   public Replicated getRootObject()
   {
      return root;
   }

   boolean isObjectLoaded(ObjectID oid)
   {
      return (objects.get(oid) instanceof Replicated);
   }
   
   /**
   * loadObject(oid) loads the object associated with oid from disk, as follows.
   * If the entry for oid in the objects table is an ObjectID, then the entry is a
   * parent that has yet to be loaded, and loadObject calls itself with this
   * object ID.  If the entry is a URL (for now assumed to be a file URL), then
   * the object is loaded by reading in its serialized form in the file of the URL.
   */
   private void loadObject(ObjectID oid) throws SyncException
   {
      Object obj = objects.get(oid);
      if (obj == null) {
         throw new SyncException("SyncObjectServer.loadObject: object " + oid.toString() + " not found");
      } else if (obj instanceof Replicated) {
         return;
      } else if (obj instanceof ObjectID) {
         loadObject((ObjectID) obj);
         if (!isObjectLoaded(oid)) throw new SyncException("Loading parent " + obj + " did not load child " + oid);
      } else if (obj instanceof URL) {
         readFromDisk((URL) obj);
         if (!isObjectLoaded(oid)) throw new SyncException("readFromDisk did not load object");
      } else {
         throw new SyncException("SyncObjectServer.loadObject: unexpected type in parent: " + obj.getClass().getName());
      }
   }
   
   void readFromDisk(URL url) throws SyncException
   {
      if (url.getProtocol().equals("file")) try {
         File objfile = new File(url.getFile());
         readingFromDisk = true;
         Object object = (new ObjectInputStream(new FileInputStream(objfile))).readObject();
         // object should now be in internal table, because of notifyRead being called
         // in course of object deserialization
         readingFromDisk = false;
      } catch (Exception e) {
         readingFromDisk = false;
         throw new SyncException("Error reading object file: " + e);
      } else {
         throw new SyncException("Cannot handle URLs with " + url.getProtocol() + " protocol");
      }
   }
   
   /**
   * saveObject(obj) writes obj to disk, according to the following algorithm.  
   * If obj's parent has already been written to disk, or if obj has no parent, 
   * or its parent is a ReplicatedReference, saveObject will write obj to
   * disk directly in a file named by obj's object ID.  If obj's parent has not
   * yet been written to disk, saveObject will recursively call itself on the 
   * parent until it finds a parent that *has* been written.  It then writes the 
   * child to disk, which, through the Java object serialization, will eventually 
   * result in the object of the original call being written.
   */
   public void saveObject(Replicated obj)
   {
      // if (!hasObject(obj)) putObject(obj);
      System.out.println("saveObject " + obj.getObjectID());
      ObjectID parentID = obj.getParentID();
      if (parentID == null) {
         writeToDisk(obj);
      } else {
         Object parent = objects.get(parentID);
         if (parent instanceof URL | parent instanceof ObjectID | parent instanceof ReplicatedReference) {
            writeToDisk(obj);
         } else if (parent instanceof Replicated) {
            saveObject((Replicated) parent);
         }
      }
   }
   
   void writeToDisk(Replicated obj)
   {
      ObjectID oid = obj.getObjectID();      
      System.out.println("writeToDisk " + oid);
      File objfile = new File(objects_home, oid.toString());
      try {
         writingToDisk = true;
         (new ObjectOutputStream(new FileOutputStream(objfile))).writeObject(obj);
         writingToDisk = false;
         objects.put(oid, new URL("file:///" + objfile.getPath()));
      } catch (Exception ex) {
         writingToDisk = false;
         System.err.println("Error saving object " + oid.toString() + ": " + ex);
      }
   }
   
   public Replicated getObject(ObjectID oid) throws SyncException
   {
      Object obj = objects.get(oid);
      if (obj == null) {
         throw new SyncException("SyncObjectServer.getObject: no object with ID " + oid.toString());
      } else if (obj instanceof Replicated) {
         return (Replicated) obj;
      } else {
         // object must be loaded from disk.  May be in file of some parent object.
         loadObject(oid);
         // object will now be in table
         return (Replicated) objects.get(oid);
      }
   }
   
   public boolean hasObject(ObjectID oid)
   {
      return objects.containsKey(oid);
   }
   
   public boolean hasObject(Replicated obj)
   {
      ObjectID oid = obj.getObjectID();
      return oid != null && objects.containsKey(oid);
   }

   public void putObject(Replicated obj)
   {
      ObjectID oid;
      oid = obj.getObjectID();
      if (oid == null) {
         oid = ObjectID.newObjectID(user_given_id);
         obj.setObjectID(oid);
      }
      objects.put(oid, obj);
      // attrs.put(oid, new Hashtable(10));
   }

   public void removeObject(ObjectID oid)
   {
      Replicated obj;
      try {obj = getObject(oid);}
      catch (SyncException ex) {return;}
      if (obj instanceof ReplicatedCollection) {
         for (Enumeration e = ((ReplicatedCollection) obj).elements(); e.hasMoreElements(); ) {
            removeObject(((Replicated) e.nextElement()).getObjectID());
         }
      }
      objects.remove(oid);
      // The following two lines commented out because final removal will
      // be done in a separate process.
      // File obj_file = new File(objects_home, oid.toString());
      // if (obj_file.exists()) {boolean success = obj_file.delete();}
      
      // attrs.remove(oid);
      // File attrs_file = new File(attrs_home, oid.toString());
      // if (attrs_file.exists()) {boolean success = attrs_file.delete();}
   }

   /** 
   * saveObjects writes objects to disk prior to a SyncObjectServer shutdown.
   * When saveObjects writes an object to disk, other objects held in the
   * server may also be written because they are referenced by the object
   * written first.  Thus these objects should not also be written
   * by saveObjects.  To ensure this, each object, when it is written, notifies
   * the server that is has been written by calling notifyWasWritten.  The server
   * will then replace the object's entry in the table by the object ID of its
   * parent.  The algorithm will then bypass these entries in its loop over all
   * objects.  The algorithm also bypasses objects whose parent property is null.
   * These will be garbage collected separately.
   */
   private void saveObjects() throws SyncException
   {
      // Write root directory to disk first
      
      saveObject(getRootObject());
      
      // Loop over objects in hashtable.  Because hashtable will be modified
      // during loop (but not added to or removed from) copy all keys to array
      // and then loop over array.

      ObjectID[] keys = new ObjectID[objects.size()];
      Enumeration e = objects.keys();
      for (int i = 0; i < keys.length; i++) keys[i] = (ObjectID) e.nextElement();
      
      System.out.println("looping over object IDs:");
      for (int i = 0; i < keys.length; i++) {
         Object obj = objects.get(keys[i]);
         System.out.println("ID " + keys[i] + " is " + obj.getClass().getName());
         if (obj instanceof Replicated) saveObject((Replicated) obj);
      }
   }
   
   /**
   * checkDanglingReferences ensures that there are no Replicated objects in hashtable.
   * if there are, server will crash on next startup because when hashtable
   * is read back in, any Replicated objects will attempt to register themselves
   * with the server, which does not yet exist.  Replicated objects are written to
   * disk and replaced in the table with their URL.  They can be garbage collected later.
   */
   public void checkDanglingReferences()
   {
      Vector replace = new Vector();
      for (Enumeration e1 = objects.elements(); e1.hasMoreElements(); ) {
         Object obj = e1.nextElement();
         if (obj instanceof Replicated) replace.addElement(obj);
      }
      for (Enumeration e2 = replace.elements(); e2.hasMoreElements(); ) {
         writeToDisk((Replicated) e2.nextElement());
      }
   }
   
   public void shutdown() throws SyncException
   {
      try {
         // save persistent objects
         saveObjects();
         // check to make sure objects hashtable has no Replicated objects in it.
         checkDanglingReferences();
         // write objects hashtable to disk
         System.out.println("Writing internal table to disk");
         (new ObjectOutputStream(new FileOutputStream(objects_file))).writeObject(objects);
         // write attributes hashtable to disk
         //(new ObjectOutputStream(new FileOutputStream(attrs_file))).writeObject(attrs);
         // write root OID to disk
         (new ObjectOutputStream(new FileOutputStream(root_file))).writeObject(root.getObjectID());
      } catch (Exception e) {
         e.printStackTrace();
         throw new SyncException("Error shutting down object server: " + e.toString());
      }
   }
   
   /**
   * Removes from internal table all entries whose parent is null, returning number
   * of such objects removed.
   * In Sync object lifecycle, moves objects to Inactive state.  
   */
   public int firstLevelGarbageCollection()
   {
      Vector toBeDeleted = new Vector(50);
      for (Enumeration e = objects.keys(); e.hasMoreElements(); ) {
         ObjectID key = (ObjectID) e.nextElement();
         // note: next line requires object to be read into memory.
         // code should be added so that object is removed from memory 
         // if it was read from disk just for this.
         Replicated obj = null;
         try {
            obj = getObject(key);
         } catch (SyncException ex) {
            System.err.println("Error getting object for garbage collection: " + ex);
            continue;
         }
         if (obj.getParentID() == null & !obj.equals(root)) {
            toBeDeleted.addElement(key);
            if (obj instanceof ReplicatedReference) toBeDeleted.addElement(((ReplicatedReference) obj).getReferencedID());
         }
      }
      for (Enumeration e = toBeDeleted.elements(); e.hasMoreElements(); ) {
         objects.remove(e.nextElement());
      }
      return toBeDeleted.size();
   }
   
   /**
   * Deletes from disk all files whose associated object is not in the server table,
   * returning number of such files deleted.
   * In Sync object lifecycle, moves object to Gone state (final).
   */
   public int secondLevelGarbageCollection()
   {
      int count = 0;
      String[] ids = objects_home.list();
      for (int i = 0; i < ids.length; i++) {
         ObjectID oid = ObjectID.fromString(ids[i]);
         if (!hasObject(oid)) {
            File temp = new File(objects_home, ids[i]);
            temp.delete();
            count++;
         }
      }
      return count;
   }

   public void notifyRead(Replicated obj)
   {
      putObject(obj);
      // System.out.println("notifyWasRead from " + obj.getObjectID() + " with parent ID " + obj.getParentID());
      if (readingFromDisk) {
         obj.postStorageRead();
      } else {
         obj.postTransmitRead();
      }
   }

   public void notifyWrite(Replicated obj)
   {
      ObjectID parentID = obj.getParentID();
      // System.out.println("notifyWasWritten from " + obj.getObjectID() + " with parent ID " + parentID);
      if (writingToDisk) {
         obj.preStorageWrite();
         if (parentID != null) objects.put(obj.getObjectID(), parentID);
      } else {
         obj.preTransmitWrite();
      }
   }
/*   
   public Object getAttribute(ObjectID oid, String attr) throws SyncException
   {
      Hashtable attrset;
      Object obj = attrs.get(oid);
      if (obj == null) {
         throw new SyncException("SyncObjectServer.getAttribute: no object with ID " + oid.toString());
      } else if (!(obj instanceof File)) {
         attrset = (Hashtable) obj;
      } else {  // obj is instance of file
         try {
            File attrfile = (File) obj;
            attrset = (Hashtable) (new ObjectInputStream(new FileInputStream(attrfile))).readObject();
            attrs.put(oid, attrset);
         } catch (Exception e) {
            throw new SyncException("Error reading object file: " + e.getMessage());
         }
      }
      return attrset.get(attr);
   }

   public void setAttribute(ObjectID oid, String attr, Object value) throws SyncException
   {
      Hashtable attrset = (Hashtable) attrs.get(oid);
      if (attrset == null) throw new SyncException("SyncObjectServer.setAttribute: no object with ID " + oid.toString());
      if (attr == null) throw new SyncException("SyncObjectServer.setAttribute: attribute name is null");
      if (value == null) throw new SyncException("SyncObjectServer.setAttribute: attribute value is null");
      attrs.put(attr, value);
   }

   public void clearAttribute(ObjectID oid, String attr) throws SyncException
   {
      Hashtable attrset = (Hashtable) attrs.get(oid);
      if (attrset == null) throw new SyncException("SyncObjectServer.getAttribute: no object with ID " + oid.toString());
      else attrset.remove(attr);
   }
*/
}
