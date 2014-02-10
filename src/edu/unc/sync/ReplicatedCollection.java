package edu.unc.sync;

import java.util.*;
import java.io.*;

/**
* The ReplicatedCollection class is the base class for ReplicatedRecord,
* ReplicatedSequence, and ReplicatedDictionary. The class provides default implementations
* of applyChange() and mergeChange(), as well as some others. It adds new methods for
* handling change sets.
*
* @author Jon Munson
* @see edu.unc.sync.Replicated
*/
public abstract class ReplicatedCollection extends Replicated
{
   /**
   * An AtomicMergeMatrix is used in case two change sets conflict and AtomicCommit
   * on either change set is true.
   */
   static MergeMatrix atomicMergeMatrix = new AtomicMergeMatrix();

   static final long serialVersionUID = -2226412076496370727L;

   /**
   * Clears the changed bit for this object and clears the change set. Calls
   * clearChanged() on all children.
   */
   public void clearChanged()
   {
      super.clearChanged();

      //System.out.println("before clear changes " + this);
      /*
      if (this.getChange() != null)
        this.getChange().print();
      */
      for (Enumeration e = elements(); e.hasMoreElements(); )
          ((Replicated) e.nextElement()).clearChanged();
      clearChangeSet();
      //edu.unc.sync.SyncServer.printChangeInfo(this);
      /*
      System.out.println("after clear change");
      if (this.getChange() != null)
        this.getChange().print();
      */
   }

   /**
   * Clears the collection's set of changes.
   */
   public abstract void clearChangeSet();

   /**
   * Apply a set of changes to a collection object. The argument is assumed to be
   * a ChangeSet, or a NullChange or null. The changes are applied by calling
   * each change's applyTo() method, passing this object as an argument. Calls
   * newChangeSet() to hold the changes that will be undone.
   *
   * @param change The set of changes to apply to this object.
   * @return A set of previous changes that were undone as a result of applying
   * the given changes, or null if no changes were undone.
   * @exception edu.unc.sync.ReplicationException if the given changes could not be applied.
   * @see edu.unc.sync.ChangeSet
   */
   public Change applyChange(Change change) throws ReplicationException
   {
      //System.out.println("Apply change: " + this);
      if (change instanceof NullChange | change == null) return null;
      if (!(change instanceof ChangeSet)) throw new ReplicationException("Argument must be a ChangeSet");

      ChangeSet changes = (ChangeSet) change;
      ChangeSet rejectedChanges = newChangeSet(changes.size());

      for (Enumeration e = changes.enumerateChanges(); e.hasMoreElements(); ) {
         ElementChange ch = (ElementChange) e.nextElement();
         //System.out.println("**Calling applyTo on ElementChange in ReplicationCollection");
         ElementChange reject = ch.applyTo(this);
         if (reject != null) rejectedChanges.addChange(reject);
      }
      setChanged();

      return rejectedChanges.isEmpty() ? null : rejectedChanges;
   }

   /**
   * Concatenates two sets of changes to this object. Returns a new change set
   * that, when applied to an object, is equivalent to the two
   * input change sets applied in series. Calls newChangeSet() to get this change set.
   *
   * @param first A non-empty change set.
   * @param second A non-empty change set assumed to have followed the first change set.
   * @return A non-empty change set that combines the effects of the two input change sets.
   * @exception edu.unc.sync.ReplicationException if the two change sets cannot be
   * concatenated.
   * @see edu.unc.sync.ChangeSet
   */
   public Change concatChanges(Change first, Change second) throws ReplicationException
   {
      if (first == null || first instanceof NullChange || !(first instanceof ChangeSet)) throw new ReplicationException("Argument to ReplicationCollection.concatChanges must be ChangeSet");
      if (second == null || second instanceof NullChange || !(second instanceof ChangeSet)) throw new ReplicationException("Argument to ReplicationCollection.concatChanges must be ChangeSet");
      ChangeSet firstSet = (ChangeSet) first;
      ChangeSet secondSet = (ChangeSet) second;

      // this vector for collecting changes to return; sized to maximum
      ChangeSet concatSet = newChangeSet(firstSet.size() + secondSet.size());

      // add first set
      for (Enumeration e = firstSet.enumerateChanges(); e.hasMoreElements(); ) {
         ElementChange ch = (ElementChange) e.nextElement();
         concatSet.addChange(ch);
      }
      // add second set, overriding duplicates from first set
      for (Enumeration e = secondSet.enumerateChanges(); e.hasMoreElements(); ) {
         ElementChange secondCh = (ElementChange) e.nextElement();
         ElementChange firstCh = concatSet.getCorresponding(secondCh);
         concatSet.addChange(concatElementChanges(firstCh, secondCh));
      }
      return concatSet;
   }

   /**
   * Computes the merge of two concurrent change sets. The current merge matrix is
   * used. (Presently, there is no atomic merge option.) Calls newChangeSetPair()
   * to get a properly sized pair of change sets in which to put the merge results.
   * For a description of the merge algorithm, see
   * <a href="http://www.cs.unc.edu/~munson/sync/merge_algorithm.html">The Sync Merge Algorithm</a>.
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
   public ChangePair mergeChanges(Change central, Change remote) throws ReplicationException
   {
      ChangeSet C0 = (central instanceof NullChange ? new NullSet() : (ChangeSet) central);
      ChangeSet Cr = (remote instanceof NullChange ? new NullSet() : (ChangeSet) remote);

      // these vectors for collecting changes to return
      ChangePair A0Ar = newChangeSetPair(C0, Cr);
      ChangeSet A0 = (ChangeSet) A0Ar.central;
      ChangeSet Ar = (ChangeSet) A0Ar.remote;

      MergeMatrix mergeMatrix = getMergeMatrix();

      //System.out.println("Beginning merge loop in ReplicatedCollection.mergeChanges");
      //System.out.println(this);

      // for each pair of changes...
      for (Enumeration e = Cr.enumerateChangePairs(C0); e.hasMoreElements(); ) {
         ChangePair chprIn = (ChangePair) e.nextElement();
         // System.out.println("chprIn is "+(chprIn == null ? "null" : "not null"));
         ChangePair chprOut = mergeMatrix.action(chprIn.central, chprIn.remote);
         if (chprOut.getConflicting() & (C0.getAtomicCommit() | Cr.getAtomicCommit())) {
            ChangePair result = atomicMergeMatrix.action(C0, Cr);
            result.setConflicting(true);
            //System.out.println("Returning from ReplicatedCollection.mergeChanges (conflict)");
            return result;
         }
         if (chprOut.central != null) A0.addChange((ElementChange) chprOut.central);
         if (chprOut.remote != null) Ar.addChange((ElementChange) chprOut.remote);
      }
      if (A0.size() == 0) A0Ar.central = null;
      if (Ar.size() == 0) A0Ar.remote = null;

      //System.out.println("Returning from ReplicatedCollection.mergeChanges");
      //System.out.println(this);

      return A0Ar;
   }

   /**
   * Returns an enumeration of this collection's elements, whether the elements
   * of a ReplicatedSequence or ReplicatedDictionary, or the fields of a
   * ReplicatedRecord.
   *
   * @return An Enumeration of the collection elements.
   */
   public abstract Enumeration elements();

   /**
   * Returns a change set appropriate for the subclass, given a hint as to the
   * expected size necessary. This is called by concatChanges().
   *
   * @param An int to be used as a suggestion as the size that may be needed.
   * @return A ChangeSet sized approximately to the input argument.
   */
   public abstract ChangeSet newChangeSet(int size);

   /**
   * Returns a pair of empty change sets, each sized according to the input change sets
   * (the change sets are not modified). This is called by mergeChanges().
   * In sizing the output change sets, assume that there will be few conflicts
   * in the merge and that the size of the change set sent to the server will
   * be approximately the size of the client's change set, and vice versa.
   *
   * @param cs0 The change set from the server.
   * @param csr The change set from the client.
   * @return A pair of empty change sets.
   */
   public abstract ChangePair newChangeSetPair(ChangeSet cs0, ChangeSet csr);

   /**
   * Returns a change to an element of the collection that has the same effect of two
   * successive changes.
   *
   * @param first A change to an element of the collection.
   * @param second A later change to the same element of the collection.
   * @return A change that has the same effect of the two changes applied in series.
   */
   public abstract ElementChange concatElementChanges(ElementChange first, ElementChange second) throws ReplicationException;

   /**
   * Returns a ModifyChange specific to the particular ReplicatedCollection subclass.
   * Called by the DoMergeAction merge action.
   *
   * @param oid The object ID of this collection object.
   * @param eltID The identifier (in a form specfic to the ReplicatedCollection subclass)
   * of the collection element to be modified.
   * @param ch The change to be applied to the element.
   * @return An ElementChange object specific to the ReplicatedCollection subclass.
   */
   // public abstract ElementChange newModifyChange(ObjectID oid, Object eltID, Change ch);

   /**
   * Given a subclass-specific identifier (e.g., a dictionary key or record field name),
   * returns the element.
   *
   * @param eltID An object which will uniquely identify an element of the collection.
   * @return An element of the collection, or null if the element cannot be found.
   */
   // public abstract Replicated identifyElement(Object eltID);
}
