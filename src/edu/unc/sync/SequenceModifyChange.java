package edu.unc.sync;

import edu.unc.sync.server.SyncException;

public class SequenceModifyChange extends ModifyChange
{
   ObjectID eltID;

   public SequenceModifyChange(ObjectID id, ObjectID eltID, Change change)
   {
      super(id, eltID, change);
      this.eltID = eltID;
   }

   public ElementChange applyTo(Replicated obj) throws ReplicationException
   {
      ReplicatedSequence seq = (ReplicatedSequence) obj;
      Replicated elt;
      try {
         elt = Sync.getObject(eltID);
      } catch (SyncException ex) {
         throw new ReplicationException(ex.getMessage());
      }

      Change rejected = elt.applyChange(change);
      if (rejected != null) return new SequenceModifyChange(getObjectID(), eltID, rejected);
      else return null;
   }

   public Object identifier()
   {
      return eltID;
   }

   public void print()
   {
      System.out.println("Modified ID "+ eltID.toString()+"--"+super.toString());
      change.print();
   }
}

