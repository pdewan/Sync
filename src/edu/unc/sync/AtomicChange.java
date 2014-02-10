package edu.unc.sync;

import java.io.*;

/**
* The Change class for all replicated classes that extend ReplicatedAtomic.
* Simply calls the setValue method of ReplicatedAtomic.
*
* @see edu.unc.sync.Change
* @see edu.unc.sync.ReplicatedAtomic
* @author Jon Munson
*/
public class AtomicChange extends Change
{
   /**
   * Holds the new value of atomic object
   */
   private Serializable newValue;

   /**
   * Instantiate with the object ID of the object changed.
   *
   * @param objectID the object ID of the object changed.
   * @param newValue the new value of the atomic object.
   */
   public AtomicChange(ObjectID objectID, Serializable newValue)
   {
      super(objectID);
      this.newValue = newValue;
   }

   /**
   * Returns the new value of the atomic object.
   *
   * @return the value stored in newValue.
   */
   public Serializable getNewValue()
   {
      return newValue;
   }
   
   /**
   * A method used for debugging.  Prints a (preferrably one-line)
   * representation of the change.
   */
   public void print()
   {
      // System.out.println("New value: " + new_value.toString());
      System.out.println("Atomic change to " + getObjectID().toString());
   }

   /**
   * Applies the change to the object by calling its setValue() method.  
   * Returns null if the object was previously unchanged or its old value 
   * if it was changed.
   *
   * @param obj the object to which the change is to be applied.
   * @return null if the object was unchanged, or the object's old value
   * if it was changed.
   */
   public Change applyTo(Replicated obj)
   {
      Change ret = obj.hasChanged() ? obj.getChange() : null;
      ((ReplicatedAtomic) obj).setValue(newValue);
      return ret;
   }

   /**
   * The integer which identifies the operation's place in the merge matrix.
   * Returns Replicated.MODIFY.
   *
   * @return Replicated.MODIFY
   * @see edu.unc.sync.Replicated#MODIFY
   */
   public int opcode()
   {
      return Replicated.MODIFY;
   }

   /**
   * Makes a copy of the change.  In this instance a shallow copy is made
   * because the newValue field is never written to, and there is thus
   * no danger of overwriting a previous change's newValue.
   *
   * @return a copy of the change.
   */
   public Change copy()
   {
      Change cpy = null;
      try {
         cpy = (Change) this.clone();
      } catch (CloneNotSupportedException e) {
         System.err.println("AtomicChange.copy: " + e);
      }
      return cpy;
   }
}
