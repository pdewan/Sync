package edu.unc.sync;

import java.util.*;
import java.io.*;

public class RecordChangeSet extends ChangeSet
{
   protected Hashtable changes;

   public RecordChangeSet(ObjectID obj_id, int size)
   {
      super(obj_id);
      changes = new Hashtable(size < 5 ? 5 : size);
   }

   public ElementChange getChange(Object key)
   {
      return (ElementChange) changes.get(key);
   }

   public void addChange(ElementChange change)
   {
      addChange(change.identifier(), change);
   }

   public ElementChange getCorresponding(ElementChange change)
   {
      return getChange(change.identifier());
   }

   public boolean isEmpty()
   {
      return changes.isEmpty();
   }

   public void addChange(Object key, Change change)
   {
      if (change == null | (change instanceof NullChange) | (change instanceof NullElementChange)) return;

      Object old_value = changes.put(key, change);
   }

   public boolean containsKey(Object key)
   {
      return changes.containsKey(key);
   }

   public Enumeration keys()
   {
      return changes.keys();
   }

   public int size()
   {
      return changes.size();
   }

   public void clear()
   {
      changes.clear();
   }

   public Change copy()
   {
      GenericChangeSet this_copy = new GenericChangeSet(getObjectID(), changes.size());
      for (Enumeration e = changes.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Change elt_copy = ((Change) changes.get(key)).copy();
         this_copy.changes.put(key, elt_copy);
      }
      return this_copy;
   }

   public void print()
   {
      if (changes.size() == 0) {
         System.out.println("Empty");
      }
      else {
         for (Enumeration e = changes.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Change change = (Change) changes.get(key);
            System.out.println("Key: " + key.toString());
            change.print();
         }
      }
   }

   public Enumeration enumerateChanges()
   {
      return changes.elements();
   }

   public static Vector makeHashtableObjectPairs(Hashtable htr, Hashtable ht0)
   {
      if (htr.size() == 0 & ht0.size() == 0) return new Vector(5);

      // create union of hashtable keys
      Vector all_keys = new Vector(htr.size() + ht0.size());
      for (Enumeration e = htr.keys(); e.hasMoreElements(); )
         all_keys.addElement(e.nextElement());
      for (Enumeration e = ht0.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         if (!htr.containsKey(key)) all_keys.addElement(key);
      }
      Vector pairs = new Vector(all_keys.size());
      for (Enumeration e = all_keys.elements(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         ElementChange chr = htr.containsKey(key) ? (ElementChange) htr.get(key) : new NullElementChange();
         ElementChange ch0 = ht0.containsKey(key) ? (ElementChange) ht0.get(key) : new NullElementChange();
         ChangePair chpr = new ChangePair(ch0, chr, false);
         // System.out.println("Adding "+chpr.toString());
         pairs.addElement(chpr);
      }
      return pairs;
   }

   public Enumeration enumerateChangePairs(ChangeSet C0)
   {
      Vector pairs;
      if (C0 instanceof GenericChangeSet & !C0.isEmpty()) {
         Hashtable changes0 = ((GenericChangeSet) C0).changes;
         pairs = makeHashtableObjectPairs(changes, changes0);
      }
      else {  // C0 is instance of NullSet
         pairs = new Vector(size());
         for (Enumeration e = enumerateChanges(); e.hasMoreElements(); )
            pairs.addElement(new ChangePair(new NullElementChange(), (Change) e.nextElement(), false));
      }
      return pairs.elements();
   }
}
