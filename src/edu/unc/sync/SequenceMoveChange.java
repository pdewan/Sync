package edu.unc.sync;

import java.util.*;

public class SequenceMoveChange extends SequenceInsertChange
{
   public SequenceMoveChange(ObjectID objID, ObjectID eltID)
   {
      super(objID, eltID, null);
   }

   public Change copy()
   {
      return new SequenceMoveChange(getObjectID(), eltID);
   }

   public ElementChange applyTo(Replicated obj)
   {
      return null;
   }

   public void print()
   {
      System.out.println("Moved element with ID " + eltID.toString());
   }
}