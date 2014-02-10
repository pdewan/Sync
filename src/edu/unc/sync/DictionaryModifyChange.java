package edu.unc.sync;

public class DictionaryModifyChange extends ModifyChange
{
   public Object key;

   public DictionaryModifyChange(ObjectID id, Object key, Change change)
   {
      super(id, key, change);
      this.key = key;
   }

   public Object identifier()
   {
      return key;
   }
/*
   public Change concat(Change concat_change)
   {
      if (concat_change instanceof DictionaryModifyChange) {
         return new DictionaryModifyChange(object_id, key, change.concat(((ModifyChange) concat_change).change));
      }
      // else if (change instanceof DictionaryReadChange)
      //    return this;
      else
         // is remove or put
         return change;
   }
*/
   public ElementChange applyTo(Replicated obj) throws ReplicationException
   {
      ReplicatedDictionary dict = (ReplicatedDictionary) obj;
      Replicated elt = (Replicated) dict.get(key);

      Change rejected = elt.applyChange(change);
      if (rejected != null)
         return new DictionaryModifyChange(getObjectID(), key, rejected);
      else
         return null;
   }

   public void print()
   {
      System.out.println("Dictionary modify, key = "+key.toString());
      change.print();
   }
}

