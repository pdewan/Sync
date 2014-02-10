package edu.unc.sync;

public class SequenceMergeMatrix extends MergeMatrix
{
   public SequenceMergeMatrix()
   {
      super(5);
      setEntry(Replicated.NOOP, Replicated.MODIFY, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.NOOP, Replicated.READ, new BasicMergeActions.NeitherAction(false));
      setEntry(Replicated.NOOP, ReplicatedSequence.DELETE, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.NOOP, ReplicatedSequence.INSERT, new BasicMergeActions.ColumnAction(false));
      setEntry(Replicated.MODIFY, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.MODIFY, Replicated.MODIFY, new BasicMergeActions.DoMergeAction());
      setEntry(Replicated.MODIFY, Replicated.READ, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.MODIFY, ReplicatedSequence.DELETE, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(Replicated.READ, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(Replicated.READ, Replicated.READ, new BasicMergeActions.NeitherAction(false));
      setEntry(Replicated.READ, ReplicatedSequence.DELETE, new BasicMergeActions.RowAction(true));
      setEntry(ReplicatedSequence.DELETE, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(ReplicatedSequence.DELETE, Replicated.MODIFY, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedSequence.DELETE, Replicated.READ, new BasicMergeActions.ColumnAction(true));
      setEntry(ReplicatedSequence.DELETE, ReplicatedSequence.DELETE, new BasicMergeActions.NeitherAction(false));
      setEntry(ReplicatedSequence.INSERT, Replicated.NOOP, new BasicMergeActions.RowAction(false));
      setEntry(ReplicatedSequence.INSERT, ReplicatedSequence.INSERT, new BasicMergeActions.BothAction(false));
   }
/*
   public class InsertBothAction implements MergeAction
   {
      public ChangePair action(Change remote, Change central)
      {
         // In order to ensure that the final order of merged insertions
         // is the same at both central and remote sites, the insertion
         // point of one set must be adjusted so that it is placed in
         // front of the other set.  The final order will be the remote
         // inserted elements followed by the central inserted elements.
         // Thus we adjust the insertion point of the insertion received
         // from the remote site, so that it is inserted in front of the
         // insertion at the central site.

         SequenceInsertChange rem_ins = (SequenceInsertChange) remote;
         SequenceInsertChange cen_ins = (SequenceInsertChange) central;

         ObjectID first_rem_elt = ((Replicated) rem_ins.ins_elts.elementAt(0)).object_id;
         SequenceInsertChange new_cen_ins = new SequenceInsertChange(rem_ins.object_id, first_rem_elt, rem_ins.ins_elts);

         // original central insertion change goes to remote (first arg);
         // adjusted remote insertion change goes to central (second arg);
         return new ChangePair(cen_ins, new_cen_ins, false);
      }

      public String description()
      {
         return "AcceptBothInsertions";
      }
   }
*/
}
