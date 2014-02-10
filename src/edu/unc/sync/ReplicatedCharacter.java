package edu.unc.sync;

import java.io.*;

public class ReplicatedCharacter extends ReplicatedAtomic
{
   protected char value;

   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedCharacter()
   {}

   public ReplicatedCharacter(char init)
   {
      value = init;
   }

   public ReplicatedCharacter(Character init)
   {
      value = init.charValue();
   }

   public void setValue(Serializable newValue)
   {
      value = ((Character) newValue).charValue();
      setChanged();
   }

   public void setValue(char newValue)
   {
      value = newValue;
      setChanged();
   }

   public Serializable getValue()
   {
      return new Character(value);
   }

   public char charValue()
   {
      return value;
   }
   public String toString() {
     return Character.toString(value);
   }
}
