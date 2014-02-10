package edu.unc.sync;

import java.util.*;
import java.io.*;

import edu.unc.sync.server.Folder;

public class ReplicatedDictionary
   extends ReplicatedCollection
{
   private Hashtable dict;
   private DictionaryChangeSet changes;

   public static int REMOVE = 3;
   public static int PUT    = 4;

   protected static DictionaryMergeMatrix classMergeMatrix = new DictionaryMergeMatrix();

   //static final long serialVersionUID = 1675505099281819924L;

   // constructors and methods are duplicates of Hashtable

   public ReplicatedDictionary(int initialCapacity)
   {
      super();
      dict = new Hashtable(initialCapacity);
      changes = new DictionaryChangeSet(getObjectID(), 5);
   }

   public ReplicatedDictionary()
   {
      this(10);
   }

   // Hashtable methods

   public void clear()
   // treated as an individual removal of each element in the hashtable
   {
      for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         Object removed = remove(key);
      }
      dict.clear();
   }
   public void print()
   // treated as an individual removal of each element in the hashtable
   {
      System.out.println("Printing " + this);
      for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         System.out.println("key: " + key);
      }
   }

   public boolean contains(Object obj)
   {
      return dict.contains(obj);
   }

   public boolean containsKey(Object key)
   {
      return dict.containsKey(key);
   }

   public Enumeration elements()
   {
      return dict.elements();
   }

   public Replicated get(Object key)
   {
      return (Replicated) dict.get(key);
   }

   public boolean isEmpty()
   {
      return dict.isEmpty();
   }

   public Enumeration keys()
   {
      return dict.keys();
   }

   public Replicated put(Object key, Replicated value)
   // record 'put' in changes
   {

      changes.addChange(new DictionaryPutChange(getObjectID(), key, value));
      //System.out.println("Added dict change to " + this + "  Key: "+  key + " Value: " + value);
      //changes.print();
      value.setParent(this);
      //pd: moving this down
      //setChanged();
      Replicated retVal = (Replicated) dict.put(key, value);
      //System.out.println("After put: ");
      //print();
      setChanged();
      //if (Sync.getSyncClient() == null) return retVal;
      Replicated candidate = value;
      if (value instanceof ReplicatedReference )
    	  candidate = ((ReplicatedReference) value).getReferencedObject();
      if (candidate instanceof Delegated)
  		((Delegated) candidate).makeSerializedObjectConsistent();
      else if (candidate instanceof Folder)
    	  ((Folder) candidate).makeSerializedObjectsConsisent();
    	  
      //setChangedAndMaybeNotify(key, value);
      return retVal;
      //return (Replicated) dict.put(key, value);
   }

   public Replicated remove(Object key)
   // record 'remove' in changes
   {
      System.out.println("removoing from: " + this + "key " + key);
      changes.addChange(new DictionaryRemoveChange(getObjectID(), key));
      setChanged();
      Replicated elt = (Replicated) dict.remove(key);
      if (elt != null && elt.getParentID().equals(getObjectID())) elt.setParent(null);
      return elt;
   }

   public void changeKey(Object oldKey, Object newKey)
   {
      Replicated value = (Replicated) dict.remove(oldKey);
      dict.put(newKey, value);
      setChanged();
      changes.addChange(new DictionaryKeyChange(getObjectID(), newKey, oldKey));
   }

   public int size()
   {
      return dict.size();
   }

   // Replication methods

   public void clearChangeSet()
   {
      //System.out.println("Cleared Change Set in object " + this);
      /*
      for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         //if (changes.isNewElement(key)) continue;
         Replicated obj = (Replicated) dict.get(key);
         obj.clearChanged();
                 Change ch = obj.getChange();
                 //System.out.println("obj.getChanged() is " + ch);
                 //System.out.println("obj is " + ((ch == null) ? "not " : "") + "changed");
                 if (ch != null) {
                         changes.addChange(new ModifyChange(this.getObjectID(), key, ch));
                         //System.out.println("Added modify change for " + key + ": " + ch);
                 }
         //}
      }
      //System.out.println("changes.isEmpty() is " + changes.isEmpty());
      return (!changes.isEmpty()) ? changes : null;
      */

      changes = new DictionaryChangeSet(getObjectID(), 5);
   }

   public ChangeSet newChangeSet(int size)
   {
      return new DictionaryChangeSet(getObjectID(), size);
   }

   public ChangePair newChangeSetPair(ChangeSet cs0, ChangeSet csr)
   {
      ChangeSet A0 = new DictionaryChangeSet(getObjectID(), csr.size());
      ChangeSet Ar = new DictionaryChangeSet(getObjectID(), cs0.size());
      return new ChangePair(A0, Ar, false);
   }

   public Change getChange()
   {
      //System.out.println("In ReplicatedDictionary.getChange");
      //System.out.println(changes);
      //System.out.println("hasChanged " + hasChanged());

      //if (!hasChanged()) return null;
      //PD: honoring hasChanged()

      // add modifications
      for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
         Object key = e.nextElement();
         if (changes.isNewElement(key)) continue;
         Replicated obj = (Replicated) dict.get(key);
         //System.out.println("Checking changes for " + key);
         //System.out.println("obj is a " + obj.getClass().getName());
         //System.out.println("obj.hasChanged is " + obj.hasChanged());
         //System.out.println("obj is" + obj);

         //System.out.println("changes.getChange(key) == null is " + (changes.getChange(key) == null));
         //if (obj.hasChanged()) {
            //System.out.println("Calling getChange");
		 Change ch = obj.getChange();
                 //System.out.println("obj.getChanged() is " + ch);
		 //System.out.println("obj is " + ((ch == null) ? "not " : "") + "changed");
		 if (ch != null) {
			 changes.addChange(new ModifyChange(this.getObjectID(), key, ch));
			 //System.out.println("Added modify change for " + key + ": " + ch);
		 }
         //}
      }
      //System.out.println("changes.isEmpty() is " + changes.isEmpty());
      return (!changes.isEmpty()) ? changes : null;
   }

   public ElementChange concatElementChanges(ElementChange first, ElementChange second) throws ReplicationException
   {
      if (first instanceof DictionaryPutChange) {
         if (second instanceof DictionaryPutChange) {
            return second;
         } else if (second instanceof DictionaryRemoveChange) {
            return second;
         } else if (second instanceof ModifyChange) {
            // DictionaryPutChange includes a reference to the element, not a
            // copy of the element.  Thus when the Put change is transmitted to
            // another site, the value referred to will include the changes
            // contained in ModifyChange.
            return first;
         } else
            return first;  // shouldn't ever happen
      } else if (first instanceof DictionaryRemoveChange) {
         // shouldn't have any changes after a Remove but just in case...
         return second;
      } else if (first instanceof ModifyChange) {
         if (second instanceof DictionaryPutChange) {
            return second;
         } else if (second instanceof DictionaryRemoveChange) {
            return second;
         } else if (second instanceof ModifyChange) {
            Object key = ((ModifyChange) first).identifier();
            Replicated elt = (Replicated) dict.get(key);
            Change first_change = ((ModifyChange) first).change;
            Change second_change = ((ModifyChange) second).change;
            Change concat_changes = elt.concatChanges(first_change, second_change);
            return new ModifyChange(getObjectID(), key, concat_changes);
         } else
            return first;  // shouldn't ever happen
      } else {
         return second;
      }
   }

   public MergeMatrix getClassMergeMatrix()
   {
      return classMergeMatrix;
   }
}
