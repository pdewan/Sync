package edu.unc.sync;

public class DictionaryRemoveChange extends ElementChange
{
   Object key;

   public DictionaryRemoveChange(ObjectID id, Object key)
   {
      super(id);
      this.key = key;
   }

   public Object identifier()
   {
      return key;
   }

   public int opcode()
   {
      return ReplicatedDictionary.REMOVE;
   }
/*
   public Change concat(Change change)
   {
      System.err.println("Error in DictionaryRemoveChange: a remove operation should not be replaceable.");
      return change;
   }
*/
   public ElementChange applyTo(Replicated obj)
   {
      Replicated removed = ((ReplicatedDictionary) obj).remove(key);
      if (removed != null & getConflicting())
         return new DictionaryPutChange(getObjectID(), key, removed);
      else
         return null;
   }

   public void print()
   {
      System.out.println("Dictionary remove, key = "+key.toString());
   }
}