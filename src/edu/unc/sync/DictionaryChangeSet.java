package edu.unc.sync;

import java.util.*;
import java.io.*;

public class DictionaryChangeSet extends GenericChangeSet
{
   static final long serialVersionUID = -4892630612066255254L;

   public DictionaryChangeSet(ObjectID obj_id, int size)
   {
      super(obj_id, size);
   }

   public ChangeSet createNullSet(){
	   return new DictionaryChangeSet(getObjectID(), 0);
   }

   public void addChange(ElementChange change)
   {
      if (change == null | (change instanceof NullElementChange)) return;
      //System.out.println("DictionaryChangeSet.addChange: " + change.getClass().getName());
      // add changes according to what changes are already in change set
      if (change instanceof DictionaryKeyChange) {
         // see if key change to just-added element.
         // if so, add Put change with new identifier and remove previous change
         Object oldKey = ((DictionaryKeyChange) change).oldKey;
         Object newKey = ((DictionaryKeyChange) change).identifier();
         ElementChange prev = (ElementChange) changes.get(oldKey);
         if (prev instanceof DictionaryPutChange) {
            changes.remove(oldKey);
            changes.put(newKey, new DictionaryPutChange(getObjectID(), newKey, ((DictionaryPutChange) prev).value));
         } else {
            changes.put(newKey, new DictionaryKeyChange(getObjectID(), newKey, oldKey));
         }
      } else if (change instanceof DictionaryPutChange) {
         changes.put(change.identifier(), change);
      } else if (change instanceof DictionaryRemoveChange) {
         // do not record deletions of elements added since last synchronization
         if (changes.get(change.identifier()) instanceof DictionaryPutChange) {
            changes.remove(change.identifier());
         } else {
            changes.put(change.identifier(), change);
         }
      } else if (change instanceof ModifyChange) {
         // add change unless a change with the same identifier already in set.
         ElementChange prev = (ElementChange) changes.get(change.identifier());
         if (prev == null) {
            changes.put(change.identifier(), change);
         } else if (prev instanceof DictionaryKeyChange) {
            // if added previously as a DictionaryKeyChange, add new change under old key
            changes.put(((DictionaryKeyChange) prev).oldKey, change);
         } else if (prev instanceof DictionaryPutChange) {
            // do not add the change; modifications to a new element are not
            // included in change set.
         } else {
            // other cases should not happen but override anyway.
            changes.put(change.identifier(), change);
         }
      }
   }

   public boolean isNewElement(Object id)
   {
      return changes.get(id) instanceof DictionaryPutChange;
   }
}
