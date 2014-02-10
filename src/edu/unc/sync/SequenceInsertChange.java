package edu.unc.sync;

import java.util.*;

public class SequenceInsertChange extends ElementChange
{
	
   public Replicated insertedElt;
   public ObjectID eltID;

   public SequenceInsertChange(ObjectID obj_id, ObjectID eltID, Replicated value)
   {
      super(obj_id);
	  //System.out.println("in SequenceInsertChange constructor, obj_id = " + obj_id);
      this.eltID = eltID;
      insertedElt = value;      
   }

   public Change copy()
   {
      return new SequenceInsertChange(getObjectID(), eltID, insertedElt);
   }

   public Change getInverse(){
	   return new SequenceDeleteChange(getObjectID(), eltID);
   }

   public Object identifier()
   {
      return eltID;
   }

   public int opcode()
   {
      return ReplicatedSequence.INSERT;
   }

   // This method not used
   public ElementChange applyTo(Replicated obj)
   {
      return null;
      /*
      ReplicatedSequence seq = (ReplicatedSequence) obj;
      int ins_pt = seq.insertionPoint(eltID);
      for (int i = 0; i < ins_elts.size(); i++)
         seq.insert((Replicated) ins_elts.elementAt(i), ins_pt + i);
      return null;
      */
   }

   public void print()
   {
      System.out.println("Inserted element with ID " + eltID.toString());
      //System.out.println("weird");      
      System.out.println ("Inserted Element is " + insertedElt);
      Object obj = Sync.getObject(eltID);
   }
}