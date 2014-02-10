package edu.unc.sync;

public class DictionaryPutChange extends ElementChange
{
   public Object key;
   public Replicated value;

   public DictionaryPutChange(ObjectID id, Object key, Replicated value)
   {
      super(id);
      this.key = key;
      this.value = value;
   }

   public Object identifier()
   {
      return key;
   }

   public int opcode()
   {
      return ReplicatedDictionary.PUT;
   }

   public ElementChange applyTo(Replicated obj)
   {
      Replicated replaced = ((ReplicatedDictionary) obj).put(key, value);
      if (getConflicting())
         return new DictionaryPutChange(getObjectID(), key, replaced);
      else
         return null;
   }

   public void print()
   {
      System.out.println("Dictionary put, key = "+key.toString());
   }
}