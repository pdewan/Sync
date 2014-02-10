package edu.unc.sync;

import edu.unc.sync.server.SyncException;
import edu.unc.sync.server.Folder;
import java.util.*;
import java.io.*;
//import com.sun.java.swing.tree.*;

/**
* The base class for all replicated classes. This class has several functions. The
* first is to provide an implementation for several properties. The second is to
* register a replicated object with Sync when it is created. The third is
* to handle propagation of the setChanged event to parent (collection) objects.
* The fourth is that it notifies Sync when a replicated object is written (serialized)
* or read (deserialized).
*/
public abstract class Replicated
   extends Observable
   implements Serializable
{
   /**
   * The NOOP opcode signifies that no operation was performed.
   */
   public static int NOOP = 0;

   /**
   * The MODIFY opcode signifies that the object was modified, either through
   * an atomic operation or a set of operations on its elements.
   */
   public static int MODIFY = 1;

   /**
   * The READ opcode signifies that the object's value, or the value of one or
   * more of its elements, was read. (Not currently used.)
   */
   public static int READ = 2;

   private ObjectID objectID;
   private ObjectID parentID = null;
   private String home;
   private int version;
   private Date lastModified;
   private boolean changed;  // a non-transient changed bit (the one
                             // inherited from Observable is transient)

   static final long serialVersionUID = -7816627650877788616L;

   /**
   * The Replicated constructor initializes the Replicated properties. The
   * <var>objectID</var> property is set by Sync when the constructor calls
   * <code>Sync.putObject(this)</code>.
   */
   Replicated()
   {
      super();
      Sync.putObject(this);
      version = 0;
      lastModified = new Date();
      changed = false;
      // clearChanged();  (caused crash 6/3/98 -- why not before?)
      // System.out.println("new " + this.getClass().getName() + ", OID = " + objectID);
   }

   /**
   * setChanged() invokes the setChanged() method of Observable and also sets its own,
   * non-transient, changed bit. <var>lastModified</var> is set to the current time.
   * The method then invokes setChanged() on its parent (after getting a reference to
   * its parent via <code>Sync.getObject(parentID)</code>).
   * Should be invoked if overridden.
   */
   public void setChanged()
   {
      super.setChanged();
      changed = true;
      lastModified = new Date();
      //System.out.println(objectID.toString()+" was changed");
      
      //if (Sync.getSyncClient() != null) {
        	 //System.out.println("hasChanged: " + hasChanged() + " count observers " + countObservers());
      	//System.out.println( "object: " + this +  "has changed " + hasChanged() + "count observers " + countObservers());
      	/*
      	if (countObservers() > 0) {
       	 notifyObservers(); // this will reset has
       	 super.setChanged();
       	 changed = true;
       	System.out.println( "has changed " + hasChanged() + "count observers " + countObservers());
      	}
      	*/
      	myNotifyObservers();
        //}
        
      	
      
      if (parentID != null) {
         Replicated parent = Sync.getObject(parentID);
         //System.out.println("this=" + this + " parent = " + parent + "pid" + parentID);
         parent.setChanged();
         if (parent.parentID == null) //replicated folder
         	Sync.implicitSynchronize(this.objectID);
         /*
         else if (Sync.getSyncClient() != null)
        	 notifyObservers();
        	 */
        	 
      }
      
      /*
	  else{
	  	
	  	//Sync.implicitSynchronize(this.objectID);
	  }
	  */
   }
   public void addObserver (Observer o) {
	   super.addObserver(o);
   }
   public void setChangedAndMaybeNotify(Object key, Replicated value)
   {
   	  setChanged();
   	 if (parentID != null) {
        Replicated parent = Sync.getObject(parentID);
        //System.out.println("this=" + this + " parent = " + parent + "pid" + parentID);
        parent.setChangedAndMaybeNotify(key, value);
     }
	  else  if (this instanceof Folder )
	  	Sync.implicitSynchronize(value.objectID);
	  		  
	  
   }

   /**
   * Returns false if object has <b>not</b> been changed. Returns true if setChanged()
   * has been called since the last clearChanged() call, and may return true regardless.
   * This holds true even if the object has been serialized and deserialized in the
   * interim.
   *
   * hasChanged() is intended to be a less computationally intensive way to check for
   * changes than checking for a null return from getChange(). But since for some objects
   * truly checking whether they have changed or not would require as much effort as
   * getChanged(), they may elect to return true regardless. Thus the meaning of
   * hasChanged() should properly be regarded as "may have changed".
   *
   * @return <b>false</b> if object has not changed.
   */
   public boolean hasChanged()
   {
      return changed;
   }

   /**
   * Clears both the Observable changed bit and the non-transient changed bit.
   * Should be invoked if overridden.
   */
   public void clearChanged()
   {
      super.clearChanged();
      changed = false;
   }

   /**
   * The <var>Home</var> property is the Internet domain name of the server that should
   * be contacted when the object is to me synchronized. At present, no provision is
   * made to change this property automatically if a server is relocated. The implementation
   * of this property should be changed to use some kind of directory service.
   *
   * @return a fully-qualified Internet domain name.
   */
   public String getHome() {return home;}

   /**
   * Sets the <var>Home</var> property.
   *
   * @param addr a valid, fully-qualified Internet domain name, on which there is
   * registered in a Java rmiregistry a RemoteSyncServer with the name "SyncServer".
   */
   public void setHome(String addr) {home = addr;}

   /**
   * Returns the version of the object. A version is an integer >= 0, and is used by
   * the Sync Server at merge time to determine what changes of other replicas should
   * be merged with the changes of this replica. After the merge, the version of the
   * server's replica is incremented, and the version of merging replica is set to this.
   *
   * @return an int >= 0, reflecting the current version of the object.
   */
   public int getVersion() {return version;}

   /**
   * Sets the current version of the object. Is invoked only by the Sync Client.
   *
   * @param newVersion the new version number of the object, as determined by
   * the Sync Server and set by the Sync Client.
   */
   public void setVersion(int newVersion) {version = newVersion;}

   /**
   * Returns true if the object has a parent (is a member of a collection). When a
   * ReplicatedCollection removes or deletes an object, it should set the <var>Parent</var>
   * property to null, which signals the garbage collection algorithm that the object
   * is no longer used.
   *
   * @return <b>true</b> iff the object has a (Replicated) parent object.
   */
   public boolean hasParent()
   {
      return parentID != null;
   }

   /**
   * Returns the object's parent object. Since it only keeps the object ID of its
   * parent, it must obtain an actual reference through a <code>Sync.getObject(parentID)</code>
   * call. This may cause the object to be loaded from disk, so getParentID() should
   * be used if an actual reference is not needed.
   *
   * @return a reference to the object's parent, or null if it has no parent.
   */
   public Replicated getParent()
   {
      return (parentID == null) ? null : Sync.getObject(parentID);
   }

   /**
   * Sets the <var>Parent</var> property of the object. The object actually keeps only
   * the object ID of its parent. This is obtained by calling getObjectID() on the parent.
   *
   * @param parent a reference to a Replicated object which contains this object as a member.
   */
   public void setParent(Replicated parent)
   {
      parentID = (parent == null) ? null : parent.getObjectID();
      //System.out.println("set parent on " + objectID + " to " + ((parentID != null) ? parentID.toString() : "null"));
   }

   /**
   * Returns the <var>Parent</var> property as the actual object ID. This prevents an
   * object lookup, which may cause the object to be loaded from disk.
   *
   * @return the object ID of the object's parent, or null if there is no parent.
   */
   public ObjectID getParentID() {return parentID;}

   /**
   * Sets the <var>Parent</var> property with the object ID of the parent.
   *
   * @param oid the object ID of a Replicated object which contains this object as a member.
   */
   public void setParentID(ObjectID oid)
   {
      parentID = oid;
      // System.out.println("set parent on " + objectID + " to " + ((parentID != null) ? parentID.toString() : "null"));
   }

   /**
   * Returns the <var>LastModified</var> property of the object as a java.util.Date.
   *
   * @return the time of the last setChanged() call.
   */
   public Date getLastModified() {return lastModified;}

   /**
   * Sets the <var>LastModified</var> property.
   *
   * @param date the time of the last call to clearChanged().
   */
   public void setLastModified(Date date) {lastModified = date;}

   /**
   * Returns all changes made to an object since its last synchronization. If applied
   * to a replica at version this.getVersion(), the changes returned should bring the
   * replica to an equivalent state as this replica.
   *
   * @return A Change object which represents all changes to the state of an object
   * since the last time it was synchronized, or null if there have been no changes.
   * @see edu.unc.sync.Change
   */
   public abstract Change getChange();

   /**
   * Applies a set of changes to the current state of an object. Should invoke
   * <code>setVersion(change.getToVersion())</code> to set the version of the object.
   * Should return whatever current changes were undone by applying these changes.
   * What is done with these undone changes is application-specific.
   *
   * @param change A set of changes to be applied to this object.
   * @return The set of changes that were undone by applying the input changes.
   * @exception edu.unc.sync.ReplicationException if the given changes cannot be applied,
   * or application of them fails for a Sync-related reason.
   * @see edu.unc.sync.Change
   */
   public abstract Change applyChange(Change change) throws ReplicationException;

   /**
   * Concatenates two sets of changes to an object. The changes are assumed to be
   * to the same object, and consecutive rather than concurrent. A new change set
   * is returned that, when applied to an object, is equivalent to the two
   * input change sets applied in series.
   *
   * @param first A non-empty change set.
   * @param second A non-empty change set assumed to have followed the first change set.
   * @return A non-empty change set that combines the effects of the two input change sets.
   * @exception edu.unc.sync.ReplicationException if the two change sets cannot be
   * concatenated.
   * @see edu.unc.sync.Change
   */
   public abstract Change concatChanges(Change first, Change second) throws ReplicationException;

   /**
   * Computes the merge of two concurrent change sets. The two changes given as input
   * must be concurrent changes to the same object. If one or the other replica has
   * no changes, a NullChange object should be sent in rather than null.
   *
   * @param central The change at to the central, or server, replica. This must
   * be a non-empty Change object, or a NullChange if there are no changes.
   * @param remote The change at to the remote, or client, replica. This is
   * assumed to be concurrent with the central changes, and must be a
   * non-empty Change object, or a NullChange if there are no changes.
   * @return A pair of change sets. The first element is to be applied to the
   * central, or server, replica, and the second element is to be applied to the
   * remote, or client, replica.
   * @exception edu.unc.sync.ReplicationException if the two change sets cannot be merged
   * or the merge fails for some Sync-related reason.
   * @see edu.unc.sync.Change
   * @see edu.unc.sync.ChangePair
   */
   public abstract ChangePair mergeChanges(Change central, Change remote) throws ReplicationException;

   /**
   * Returns the current merge matrix for the object. Returns the merge matrix for
   * the class unless one has been set for this instance.
   *
   * @return The current merge matrix. Should never return null.
   * @see edu.unc.sync.MergeMatrix
   */
   public MergeMatrix getMergeMatrix()
   {
      MergeMatrix mm;
      if ((mm = getInstanceMergeMatrix()) != null) return mm;
      else return getClassMergeMatrix();
   }

   /**
   * Sets the current merge matrix for this instance. Does not set the class merge
   * matrix&#151;this can only be done by the class programmer. If argument is null,
   * the class merge matrix will be used as the current merge matrix.
   *
   * @param mm A merge matrix appropriate for the class of this object, or null to
   * revert to class merge matrix.
   * @see edu.unc.sync.MergeMatrix
   */
   public void setMergeMatrix(MergeMatrix mm)
   {
      setInstanceMergeMatrix(mm);
   }

   /**
   * Returns the class merge matrix. Each class must implement this method. Intended
   * for use only by the getMergeMatrix() method.
   *
   * @return The merge matrix defined for the class of this object.
   * @see edu.unc.sync.MergeMatrix
   */
   protected abstract MergeMatrix getClassMergeMatrix();

   private MergeMatrix imm = null;

   /**
   * Returns the merge matrix set for the instance, or null if none set. Intended for
   * use only by getMergeMatrix().
   *
   * @return A merge matrix, or null if none set for this instance.
   * @see edu.unc.sync.MergeMatrix
   */
   protected MergeMatrix getInstanceMergeMatrix() {return imm;}

   /**
   * Sets the merge matrix defined for this instance. Intended for use only by
   * setMergeMatrix().
   *
   * @param mm A merge matrix appropriate for the class of this object, or null to
   * revert to class merge matrix.
   * @see edu.unc.sync.MergeMatrix
   */
   protected void setInstanceMergeMatrix(MergeMatrix mm) {imm = mm;}

   /**
   * Returns the object ID for this object. Guaranteed by Sync to never return null.
   * The object ID uniquely identifies this object and its replicas in the Sync
   * universe.
   *
   * @return An object ID, should never be null.
   * @see edu.unc.sync.ObjectID
   */
   public ObjectID getObjectID() {return objectID;}

   /**
   * Sets the object ID of an object. This method is intended only for use by Sync,
   * specifically the Sync object server. When an object subclassing Replicated is
   * instantiated, the Replicated constructor calls Sync.putObject(this), a side effect
   * of which is to assign the object its object ID.
   *
   * @param oid A non-null, unique object ID. Should only be generated by Sync.
   */
   public void setObjectID(ObjectID oid) {objectID = oid;}

   /**
   * Reads in object using the stream's defaultReadObject() method, then notifies Sync
   * that the object has been read in. This allows Sync to call either the object's
   * postTransmitRead() or postStorageRead() methods, depending on whether the object
   * has been read from storage or from the network.
   *
   * @see Replicated#postTransmitRead
   * @see Replicated#postStorageRead
   */
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
   {
      stream.defaultReadObject();
      Sync.notifyRead(this);
   }

   /**
   * Notifies Sync that the object is about to be written, then writes out object
   * using the stream's defaultWriteObject() method. This allows Sync to call either
   * the object's preTransmitWrite() or preStorageWrite() methods, depending on whether
   * the object is about to be written to storage or to the network.
   *
   * @see Replicated#postTransmitRead
   * @see Replicated#postStorageRead
   */
   private void writeObject(ObjectOutputStream stream) throws IOException
   {
      Sync.notifyWrite(this);
      stream.defaultWriteObject();
   }

   /**
   * This method is called before the object is sent out over the network, as when it
   * is replicated to another site. It is normally empty but is is provided so that an object may distinguish
   * between being written to the network and being written to disk.
   */
   public void preTransmitWrite() {}

   /**
   * This method is called before the object is written to storage.
   * It is normally empty but is is provided so that an object may distinguish
   * between being written to the network and being written to disk.
   */
   public void preStorageWrite() {}

   /**
   * This method is called after the object is read from the network, as when it
   * has been replicated from another site. It is normally empty but is provided so that an object may
   * distinguish between being read from the network and read from disk.
   */
   public void postTransmitRead() {}

   /**
   * This method is called after the object is read from storage.
   * It is normally empty but is provided so that an object may
   * distinguish between being read from the network and read from disk.
   */
   public void postStorageRead() {}
   
   //add support for per-object attributes

   transient Hashtable<String, Object> attributes = new Hashtable();
   
   public Object getAttribute (String attr) {
	   if (attributes == null) attributes = new Hashtable();
	   return attributes.get(attr);
   }
   public void setAttribute (String attr, Object val) {
	   if (attributes == null) attributes = new Hashtable();
	   attributes.put(attr, val);
   }
   
   // let us repeat the observer code
   transient Vector<Delegated> myObservers = new Vector();
   
   void myAddObserver(Delegated delegated) {
	   if (myObservers == null) myObservers = new Vector();
	   if (myObservers.contains(delegated)) return;
	   myObservers.add(delegated);
   }
   void myNotifyObservers() {
	   if (myObservers == null) myObservers = new Vector();
	   for (int i = 0; i < myObservers.size(); i++) {
		   myObservers.elementAt(i).update(this, null);
	   }
   }
   int myCountObservers() {
	   if (myObservers == null) myObservers = new Vector();
	   return myObservers.size();
   }
}
/*
   public abstract TreeNode getChildAt(int childIndex);
   public abstract int getChildCount();

   public TreeNode getParent()
   {
      return parent;
   }

   public abstract int getIndex(TreeNode node);
   public abstract boolean getAllowsChildren();
   public abstract boolean isLeaf();
   public abstract Enumeration children();

   public String toString()
   {
      return getName();
   }

   public String getName()
   {
      if (name != null) return name;
      else if (parent != null) return ((ReplicatedCollection) parent).getElementName();
      else return "?";
   }

   public void setName(String name)
   {
      this.name = name;
   }
*/
