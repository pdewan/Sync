package edu.unc.sync;

import java.util.*;
import java.io.*;

/**
* The base class for all changes made to a replicated object. Change implements
* all required identifying information of a change, including the object ID
* the change applies to, the version of the object the change was made from
* (if the change is the result of calling getChange), the version that
* applying the change will take the object to, and whether the change
* conflicted with a concurrent change at another site, as discovered in a
* merge.
*
* @author Jon Munson
* @see edu.unc.sync.Replicated
*/
public abstract class Change implements Serializable, Cloneable
{
   /**
   * Implements the <var>objectID</var> property.
   *
   * @see Change#getObjectID
   */
   private ObjectID objectID;

   /**
   * Implements the <var>timestamp</var> property.
   *
   * @see Change#getTimestamp
   */
   private Date timestamp;

   /**
   * Implements the <var>fromVersion</var> property.
   *
   * @see Change#getFromVersion
   */
   private int fromVersion;

   /**
   * Implements the <var>toVersion</var> property.
   *
   * @see Change#getToVersion
   */
   private int toVersion;

   /**
   * Implements the <var>conflicting</var> property.
   *
   * @see Change#getConflicting
   */
   private boolean conflicting = false;

   /**
   * Implements the <var>conflictResolution</var> property.
   *
   * @see Change#getConflictResolution
   */
   private Object conflictResolution;

   /**
   * Creates a new Change object for the object. Must be super'ed by all Change subclasses.
   *
   * @param oid the object ID of the object this change applies to.
   */
   public Change(ObjectID oid)
   {
      objectID = oid;
      timestamp = new Date();
   }
   
   /**
   * Returns the object ID of the object the change was made to, or should be applied to.
   *
   * @return the <var>objectID</var> property.
   */
   public ObjectID getObjectID() {return objectID;}
   

   /**
   * Returns the time at which this change was recorded (but not necessarily the time
   * at which the change was made).
   *
   * @return the time (as a java.util.Date) this Change object was created.
   */
   public Date getTimestamp() {return timestamp;}   

   /**
   * Returns the version of the object from which this change was made.
   * (If <var>obj<sub>fromVersion</sub></var> is the state of the object at
   * <var>fromVersion</var>, and <var>change</var> is the current set
   * of changes, then the current state of the object should be equal
   * to <var>obj<sub>fromVersion</sub>.applyChange(change)</var>.
   *
   * @return an int >= 0.
   */
   public int getFromVersion() {return fromVersion;}

   /**
   * Sets the <var>fromVersion</var> property. This is called by the Sync Client
   * when the change is sent to the Sync Server for merging. Currently, this property
   * is set only for the top-level change of a change set. That is, subchanges that
   * may comprise the top-level change set do not get this property set.
   */
   public void setFromVersion(int v) {fromVersion = v;}

   /**
   * The version of the object that applying this change will take the object to.
   *
   * @return an int >= 0.
   */
   public int getToVersion() {return toVersion;}

   /**
   * Sets the <var>toVersion</var> property. This is called by the Sync Server when the
   * change is sent to the Sync Client for application at the remote replica. Currently,
   * this property is set only for top-level change of a change set.  That is, subchanges
   * that may comprise the top-level change set do not get this property set.
   */
   public void setToVersion(int v) {toVersion = v;}   

   /**
   * Returns the <var>conflicting</var> property. If true, indicates that this change
   * conflicted with a concurrent change in another replica. This information may be used
   * by the replicated object in applying this change. For example, a NullChange with
   * <var>conflicting</var> true tells the object that it should undo its 
   * corresponding change.
   *
   * @return the <var>conflicting</var> property as a boolean.
   */
   public boolean getConflicting() {return conflicting;}

   /**
   * Sets the <var>conflicting</var> property. Called by merge actions.
   */
   public void setConflicting(boolean conf) {conflicting = conf;}   

   /**
   * Returns the <var>conflictResolution</var> property.  This property
   * provides a way for the
   * merge action to communicate to the object how a conflict may be resolved. For
   * example, a merge action may handle concurrent ReplicatedDictionary <var>put</var> 
   * operations by accepting one, choosing an alternate key for the other, and returning
   * the alternate key in the <var>conflictResolution</var> property.
   *
   * @return an object, the meaning of which is class-specific.
   */
   public Object getConflictResolution() {return conflictResolution;}

   /**
   * Sets the <var>conflictResolution</var> property. Called by merge actions.
   */
   public void setConflictResolution(Object confRes) {conflictResolution = confRes;}

   /**
   * This method shortcuts the normal object equality determination and simply says
   * that two change objects are equal if they were made to the same object at the
   * same time. This is <b>not</b> a test for "equivalent effect."
   *
   * @return <b>true</b> iff object IDs are identical <em>and</em> timestamps are identical.
   */
   public boolean equals(Change change)
   {
      return objectID.equals(change.getObjectID()) & timestamp.equals(change.getTimestamp());
   }

   /**
   * Identfies the change in a terse way that will nonetheless distinguish it from
   * other changes.
   *
   * @return a String that identfies the change in a human-readable manner.
   */
   public String toString()
   {
      return "Change to object " + objectID.toString();
   }

   /**
   * The <var>opcode</var> of a change is its row and column position in the replicated
   * object's merge matrix.
   *
   * @return an int >= 0.
   */
   public abstract int opcode();

   /**
   * Prints the change to System.out. Used for debugging.
   */
   public abstract void print();

   /**
   * Makes a deep copy of the change. This is used by the Replicated class's concatChanges
   * method. Because the concatenation process may alter a change set, the deep copy
   * ensures that the original change set is not corrupted.
   *
   * @return a deep copy of the change.
   */
   public abstract Change copy();
   
   public void fix(){
   }
   
   public Change getInverse(){
	   return new NullChange();
   }
}
