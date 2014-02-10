package edu.unc.sync;

import java.io.*;

public class ReplicatedBoolean extends ReplicatedAtomic
{
   protected boolean value;

   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedBoolean()
   {}

   public ReplicatedBoolean(boolean init)
   {
      value = init;
   }

   public ReplicatedBoolean(Boolean init)
   {
      value = init.booleanValue();
   }

   public void setValue(Serializable newValue)
   {
      value = ((Boolean) newValue).booleanValue();
      setChanged();
   }

   public void setValue(boolean newValue)
   {
      value = newValue;
      setChanged();
   }

   public Serializable getValue()
   {
      return new Boolean(value);
   }

   public boolean booleanValue()
   {
      return value;
   }
   public String toString() {
     return Boolean.toString(value);
   }
   public boolean equals (Object o) {
	   return this == o 
	   || ((o instanceof ReplicatedBoolean) && ((ReplicatedBoolean) o).value == value) 
	   || ((o != null) && o.equals(value));
   }
}
