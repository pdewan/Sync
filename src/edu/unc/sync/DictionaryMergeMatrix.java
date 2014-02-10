package edu.unc.sync;

public class DictionaryMergeMatrix extends MergeMatrix
{
   public DictionaryMergeMatrix()
   {
      super(5);
      setEntry(Replicated.NOOP, Replicated.MODIFY, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.NOOP, Replicated.READ, new BasicMergeActions.NeitherAction(false));
      setEntry(Replicated.NOOP, ReplicatedDictionary.REMOVE, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.NOOP, ReplicatedDictionary.PUT, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.MODIFY, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.MODIFY, Replicated.MODIFY, new BasicMergeActions.DoMergeAction());
      setEntry(Replicated.MODIFY, Replicated.READ, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.MODIFY, ReplicatedDictionary.REMOVE, new BasicMergeActions.RowAction(true));
      setEntry(Replicated.MODIFY, ReplicatedDictionary.PUT, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.READ, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.READ, new BasicMergeActions.NeitherAction(false));
      setEntry(Replicated.READ, ReplicatedDictionary.REMOVE, new BasicMergeActions.RowAction(true));
      setEntry(Replicated.READ, ReplicatedDictionary.PUT, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedDictionary.REMOVE, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(ReplicatedDictionary.REMOVE, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedDictionary.REMOVE, Replicated.READ, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedDictionary.REMOVE, ReplicatedDictionary.REMOVE, new BasicMergeActions.NeitherAction(false));
      setEntry(ReplicatedDictionary.REMOVE, ReplicatedDictionary.PUT, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedDictionary.PUT, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(ReplicatedDictionary.PUT, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedDictionary.PUT, Replicated.READ, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedDictionary.PUT, ReplicatedDictionary.REMOVE, new BasicMergeActions.RowAction(true));
      setEntry(ReplicatedDictionary.PUT, ReplicatedDictionary.PUT, new BasicMergeActions.ColumnAction(true));
   }
}
