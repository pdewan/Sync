package edu.unc.sync;

import java.io.*;

/**
* ReplicatedAtomic is the base class for objects that support only atomic modification.
* The Sync-provided ReplicatedInteger, ReplicatedString, and
* ReplicatedFloat (yet to be implemented) classes subclass this class,
* as well as the ReplicatedFile class provided as an example.
*/
public abstract class ReplicatedAtomic extends Replicated
{
   protected static AtomicMergeMatrix classMergeMatrix = new AtomicMergeMatrix();

   static final long serialVersionUID = -7518593116346127213L;
   
   /**
   * Subclasses implement this method to allow their values to be set. 
   * Any subclass's setValue() should call setChanged(). This method is
   * called by the applyChange method.
   *
   * @param newValue Any serializable object.
   * @see edu.unc.sync.ReplicatedAtomic#applyChange
   */
   public abstract void setValue(Serializable newValue);

   /**
   * Returns the value of the object. It is called by the getChange method.
   *
   * @return A serializable object.
   * @see edu.unc.sync.ReplicatedAtomic#getChange
   */
   public abstract Serializable getValue();
   
   /**
   * Returns the new value of the object if it has changed, or null if it has not.
   *
   * @return An AtomicChange object containing the new value of the object, or null
   * if the object has not changed.
   * @see edu.unc.sync.AtomicChange
   */
   public Change getChange()
   {
      if (!hasChanged()) return null;
      else return new AtomicChange(getObjectID(), getValue());
   }

   /**
   * Applies a change to an atomic object. The change is assumed to be an AtomicChange.
   *
   * @param change An AtomicChange object, unless it is a NullChange or null.
   * @exception edu.unc.sync.ReplicationException if argument is not an AtomicChange or
   * a NullChange or null.
   */
   public Change applyChange(Change change) throws ReplicationException
   {
      // this method should be changed to undo an atomic change if conflictng property is true
      if (change instanceof NullChange | change == null) return null;
      if (!(change instanceof AtomicChange)) throw new ReplicationException("Argument to ReplicatedAtomic.applyChange must be an AtomicChange");
      Change reject = hasChanged() ? getChange() : null;
      setValue(((AtomicChange) change).getNewValue());
      //System.out.println("**Called setValue with " + ((AtomicChange) change).getNewValue().toString());
      return reject;
   }

   /**
   * Returns the effect of applying one atomic change after another, which is simply
   * the second change.
   *
   * @param first The first change made to the object.
   * @param second A later change made to the object (must be non-null).
   * @return The second parameter.
   * @exception edu.unc.sync.ReplicationException if argument is not an AtomicChange or
   * is a NullChange or is null.
   */
   public Change concatChanges(Change first, Change second) throws ReplicationException
   {
      if (second == null || second instanceof NullChange || !(second instanceof AtomicChange)) throw new ReplicationException("Argument to ReplicatedAtomic.concatChanges must be AtomicChange");
      return second;
   }

   /**
   * Merges two concurrent changes to an atomic object, which will typically be to
   * return the change made at the server and nullify the client's change.
   *
   * @param central The change made at the central, or server, replica.
   * @param remote The change made at the remote, or client, replica.
   * @return A pair of changes, the first to be applied at the server, the second to
   * be applied at the client.
   * @exception edu.unc.sync.ReplicationException if the merge matrix is of the wrong
   * class or something goes wrong in the merge.
   */
   public ChangePair mergeChanges(Change central, Change remote) throws ReplicationException
   {
      MergeMatrix mergeMatrix = getMergeMatrix();
      if (!(mergeMatrix instanceof AtomicMergeMatrix)) throw new ReplicationException("Merge matrix must be instance of AtomicMergeMatrix");
      return mergeMatrix.action(central, remote);
   }

   /**
   * Returns the default class merge matrix, which is an AtomicMergeMatrix. This method
   * is intended to be used only by getMergeMatrix().
   *
   * @return The default merge matrix for ReplicatedAtomic, which is an AtomicMergeMatrix.
   * @see edu.unc.sync.AtomicMergeMatrix
   */
   protected MergeMatrix getClassMergeMatrix()
   {
      return classMergeMatrix;
   }
}
/*
   public MutableTreeNode createTreeNode(String name)
   {
      return new ObjectTreeNode(name, getObjectID());
   }

   public TreeNode getChildAt(int childIndex)
   {
      return null;
   }

   public int getChildCount()
   {
      return 0;
   }

   public int getIndex(TreeNode node)
   {
      return 0;
   }

   public boolean getAllowsChildren()
   {
      return false;
   }

   public boolean isLeaf()
   {
      return true;
   }

   public Enumeration children()
   {
      return null;
   }
*/
