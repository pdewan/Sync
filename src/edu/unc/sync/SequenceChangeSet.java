package edu.unc.sync;

import java.io.*;
import java.util.*;

public class SequenceChangeSet extends ChangeSet
{
   public Hashtable ins_mov;
   public Hashtable del_mod;
   public ObjectID[] final_order;

   public SequenceChangeSet(int size_im, int size_dm, ObjectID seq_id, ObjectID[] elt_ids)
   {
      super(seq_id);
      ins_mov = new Hashtable(size_im < 5 ? 5 : size_im);
      del_mod = new Hashtable(size_dm < 5 ? 5 : size_dm);
      final_order = elt_ids;
   }

   public SequenceChangeSet(Hashtable ins_mov, Hashtable del_mod, ObjectID seq_id, ObjectID[] elt_ids)
   {
      super(seq_id);
      this.ins_mov = ins_mov;
      this.del_mod = del_mod;
      this.final_order = elt_ids;
   }

   public ChangeSet createNullSet(){
	   return new SequenceChangeSet(0, 0, getObjectID(), final_order);
   }
   
   public int size()
   {
      return ins_mov.size() + del_mod.size();
   }

   public boolean isEmpty()
   {
      return ins_mov.isEmpty() & del_mod.isEmpty();
   }

   public Change copy()
   {
      SequenceChangeSet my_copy = new SequenceChangeSet(ins_mov.size(), del_mod.size(), getObjectID(), final_order);
      for (Enumeration e = ins_mov.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Change elt_copy = ((Change) ins_mov.get(key)).copy();
         my_copy.ins_mov.put(key, elt_copy);
      }
      for (Enumeration e = del_mod.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Change elt_copy = ((Change) del_mod.get(key)).copy();
         my_copy.del_mod.put(key, elt_copy);
      }
      return my_copy;
   }

   public void addChange(ElementChange ch)
   {
      if (ch instanceof SequenceInsertChange)
         ins_mov.put(ch.identifier(), ch);
      else
         del_mod.put(ch.identifier(), ch);
   }

   private boolean isExistingID(ObjectID id)
   {
      if (id.equals(getObjectID()))
         return true;
      else {
         for (int i = 0; i < final_order.length; i++)
            if (id.equals(final_order[i])) return true;
         return false;
      }
   }

   public ElementChange getCorresponding(ElementChange change)
   {
      ElementChange corr;

      if (change instanceof SequenceInsertChange)
         corr = (ElementChange) ins_mov.get(change.identifier());
      else
         corr = (ElementChange) del_mod.get(change.identifier());

      return (corr != null) ? corr : new NullElementChange();
   }

   public void print()
   {
      System.out.println("Sequence changes:");
      for (Enumeration e = enumerateChanges(); e.hasMoreElements(); )
         ((ElementChange) e.nextElement()).print();
   }

   public void fixSelf(){
	   ReplicatedSequence replSeq = (ReplicatedSequence)Sync.getObject(this.getObjectID());
	   replSeq.fixEltIds(this);
   }
   
   public Enumeration enumerateChanges()
   {
      Vector changes = new Vector(ins_mov.size() + del_mod.size());
      for (Enumeration e = del_mod.elements(); e.hasMoreElements(); )
         changes.addElement(e.nextElement());
      for (Enumeration e = ins_mov.elements(); e.hasMoreElements(); )
         changes.addElement(e.nextElement());

      return changes.elements();
   }

   public Enumeration enumerateChangePairs(ChangeSet CS0)
   {
      Vector pairs;

      if (CS0 instanceof SequenceChangeSet & !CS0.isEmpty()) {
         SequenceChangeSet SCS0 = (SequenceChangeSet) CS0;
         pairs = GenericChangeSet.makeHashtableObjectPairs(ins_mov, SCS0.ins_mov);
         // System.out.println("#ins_mov: "+pairs.size());
         Vector delmod_pairs = GenericChangeSet.makeHashtableObjectPairs(del_mod, SCS0.del_mod);
         // System.out.println("#del_mod: "+delmod_pairs.size());
         // pairs.setSize(pairs.size() + delmod_pairs.size());
         for (Enumeration e = delmod_pairs.elements(); e.hasMoreElements(); ) {
            ChangePair chpr = (ChangePair) e.nextElement();
            // System.out.println("delmod: "+(chpr == null ? "null" : chpr.toString()));
            pairs.addElement(chpr);
         }
         // for (Enumeration e = pairs.elements(); e.hasMoreElements(); )
         //    System.out.println(((ChangePair) e.nextElement()).toString());
      }
      else {  // CS0 is instance of NullSet or is empty
         pairs = new Vector(size());
         for (Enumeration e = enumerateChanges(); e.hasMoreElements(); )
            pairs.addElement(new ChangePair(new NullElementChange(), (Change) e.nextElement(), false));
      }

      // for (Enumeration e = pairs.elements(); e.hasMoreElements(); )
      //    System.out.println(((ChangePair) e.nextElement()).toString());

      return pairs.elements();
   }
}
