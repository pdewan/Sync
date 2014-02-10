package edu.unc.sync;

/**
* A DictionaryKeyChange represents the change where an existing element of the
* dictionary is re-associated with a different key. It is generated by the
* ReplicatedDictionary changeKey() method.
*
* DictionaryKeyChange subclasses DictionaryPutChange in order to inherit the
* Put operation's merge behavior.
*/
public class DictionaryKeyChange extends DictionaryPutChange
{
   Object oldKey;
   
   /**
   * A DictionaryKeyChange is identified by the new key for the element.
   */
   public DictionaryKeyChange(ObjectID id, Object newKey, Object oldKey)
   {
      super(id, newKey, null);
      this.oldKey = oldKey;
   }

   public Change concat(Change ch)
   {
      if (ch instanceof DictionaryPutChange)
         return ch;
      else if (ch instanceof DictionaryRemoveChange)
         return ch;
      else if (ch instanceof ModifyChange)
         // DictionaryPutChange includes a reference to the element, not a
         // copy of the element.  Thus when the Put change is transmitted to
         // another site, the value referred to will include the changes
         // contained in ModifyChange.
         return this;
      else if (ch instanceof DictionaryKeyChange) {
         oldKey = ((DictionaryKeyChange) ch).oldKey;
         return this;
      } else {
         System.err.println("Unknown change in DictionaryPutChange: "+ ch.getClass().getName());
         return this;
      }
   }

   public ElementChange applyTo(Replicated obj)
   {
      Replicated replaced = ((ReplicatedDictionary) obj).get(oldKey); // value is old key
      ((ReplicatedDictionary) obj).changeKey(oldKey, key);
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