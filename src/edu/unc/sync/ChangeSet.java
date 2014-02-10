package edu.unc.sync;

import java.util.*;

/**
* A Change class specifically for collection objects that may have a set of changes
* made to them.
*/
public abstract class ChangeSet extends Change
{
   /**
   * Constructs an empty change set.
   */
   public ChangeSet(ObjectID id)
   {
      super(id);
   }

   /**
   * Implementation of the <var>AtomicCommit</var> property.
   */
   private boolean atomicCommit = false;

   /**
   * The <var>AtomicCommit</var> property specifies that an entire change set should
   * be regarded as in conflict with a concurrent change set if any one of their
   * constituent changes are in conflict. See the mergeChanges method in
   * ReplicatedCollection.
   *
   * @see edu.unc.sync.ReplicatedCollection#mergeChanges
   */
   public boolean getAtomicCommit()
   {
      return atomicCommit;
   }

   /**
   * Sets the <var>AtomicCommit</var> property to the given boolean.
   *
   * @param ac a boolean to which the <var>AtomicCommit</var> property will be set.
   */
   public void setAtomicCommit(boolean ac)
   {
      atomicCommit = ac;
   }

   /**
   * Returns the size (number of changes) in the change set.
   *
   * @return the number of changes in the change set (an int >= 0).
   */
   public abstract int size();

   /**
   * Returns an int that identifies this as a MODIFY change.
   *
   * @return Replicated.MODIFY
   * @see Replicated#MODIFY
   */
   public int opcode()
   {
      return Replicated.MODIFY;
   }

   /**
   * Adds a change to a change set. The addition of this change may increase the size
   * by one, it may not change the size at all, or it may decrease the size if the
   * added change cancels an existing change.
   *
   * @param ch the change to be added to the change set.
   */
   public abstract void addChange(ElementChange ch);

   /**
   * Returns whether the change set is empty or not (size() == 0).
   *
   * @return <b>true</b> if there are no changes in the change set.
   */
   public abstract boolean isEmpty();

   /**
   * Prints the change set to System.out.
   */
   public abstract void print();

   /**
   * Given a change from another change set, identifies the corresponding change
   * in this change set. The corresponding change is the change (if it exists) that
   * should be paired with the given change in the object's merge matrix. It will
   * typically be the change to the same collection element.
   *
   * @param an ElementChange from another change set.
   * @return the corresponding change if found, or null if not.
   */
   public abstract ElementChange getCorresponding(ElementChange ch);

   /**
   * Enumerates the changes in the change set.
   *
   * @return an Enumeration of ElementChange objects.
   */
   public abstract Enumeration enumerateChanges();

   /**
   * Returns an enumeration of ChangePairs, each of which contains an element change
   * from this change set and a corresponding element change from the input change set.
   * If there is no corresponding change in the input change set, a
   * NullElementChange object is in its place. Likewise, if there is a change in the
   * input change set that does not have a corresponding change in this change set,
   * a NullElementChange object is in its place. No pair has two NullElementChanges.
   *
   * @param ch a ChangeSet of changes to the same collection object. In making ChangePairs,
   * this change set is assumed to be that from the central, or server, site.
   * @return an Enumeration of ChangePair objects.
   */
   public abstract Enumeration enumerateChangePairs(ChangeSet ch);

   public void fixSelf() {
   }

   public void fix(){
	   //System.out.println("fix() called in " + this.getClass());
	   fixSelf();
	   for (Enumeration e=this.enumerateChanges(); e.hasMoreElements();){
		   Object o = e.nextElement();
		   //System.out.println("in fix, o.class is " + o.getClass());
		   ((Change)o).fix();
	   }
   }

   public ChangeSet createNullSet(){
	   return new NullSet();
   }
   /*
   public Change getInverse(){
	   Replicated repl = Sync.getObject(getObjectID());
	   ChangeSet chSet = this.createNullSet();
	   for (Enumeration e=this.enumerateChanges(); e.hasMoreElements();){
		   ElementChange ch1 = (ElementChange)(e.nextElement());
		   try{
			   chSet.addChange(ch1);
		   }
		   catch(Exception ex){
			   System.out.println(ex);
			   ex.printStackTrace();
		   }
	   }
	   return chSet;
   }
   */
   public Change getInverse(){
	   Replicated repl = Sync.getObject(getObjectID());
	   Change ch = new NullChange();
	   for (Enumeration e=this.enumerateChanges(); e.hasMoreElements();){
		   ElementChange ch1 = (ElementChange)(e.nextElement());
		   try{
			   ch = repl.concatChanges(ch, ch1);
		   }
		   catch(Exception ex){
			   System.out.println(ex);
			   ex.printStackTrace();
		   }
	   }
	   return ch;
   }

}
