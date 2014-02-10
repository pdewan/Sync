package edu.unc.sync;

/**
* The class that defines the default merge matrix for ReplicatedAtomic.
* ReplicatedAtomic has a static reference to an object of this class, and
* each instance of ReplicatedAtomic may have its own reference.
*
* @see edu.unc.sync.ReplicatedAtomic
* @author Jon Munson
*/
public class AtomicMergeMatrix extends MergeMatrix
{
   /**
   * Instantiating AtomicMergeMatrix creates a new merge matrix with rows and
   * columns for NOOP, MODIFY, and READ (the latter presently unused).  
   * The entries of the merge matrix are set as follows:
   *
   * <pre>
   *   setEntry(Replicated.NOOP, Replicated.MODIFY, new BasicMergeActions.ColumnAction(false));
   *   setEntry(Replicated.NOOP, Replicated.READ, new BasicMergeActions.SameAction());
   *   setEntry(Replicated.MODIFY, Replicated.NOOP, new BasicMergeActions.RowAction(false));
   *   setEntry(Replicated.MODIFY, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
   *   setEntry(Replicated.MODIFY, Replicated.READ, new BasicMergeActions.ColumnAction(true));
   *   setEntry(Replicated.READ, Replicated.NOOP, new BasicMergeActions.RowAction(false));
   *   setEntry(Replicated.READ, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
   *   setEntry(Replicated.READ, Replicated.READ, new BasicMergeActions.SameAction());
   * </pre>
   */
   public AtomicMergeMatrix()
   {
      super(3);
      setEntry(Replicated.NOOP, Replicated.MODIFY, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.NOOP, Replicated.READ, new BasicMergeActions.NeitherAction(false));
      setEntry(Replicated.MODIFY, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.MODIFY, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.MODIFY, Replicated.READ, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.READ, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.READ, new BasicMergeActions.NeitherAction(false));
   }
}
