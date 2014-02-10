package edu.unc.sync;

public class RecordMergeMatrix extends MergeMatrix
{
   public RecordMergeMatrix()
   {
      super(3);
      setEntry(Replicated.NOOP, Replicated.MODIFY, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.NOOP, Replicated.READ, new BasicMergeActions.NeitherAction(false));
      setEntry(Replicated.MODIFY, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.MODIFY, Replicated.MODIFY, new BasicMergeActions.DoMergeAction());
      setEntry(Replicated.MODIFY, Replicated.READ, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.READ, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.READ, new BasicMergeActions.NeitherAction(false));
   }
}
