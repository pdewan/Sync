package edu.unc.sync;

import java.io.*;

public class ReplicatedInteger extends ReplicatedAtomic
{
   protected int value;

   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedInteger()
   {}

   public ReplicatedInteger(int init)
   {
      value = init;
   }

   public ReplicatedInteger(Integer init)
   {
      value = init.intValue();
   }

   public void setValue(Serializable newValue)
   {
      value = ((Integer) newValue).intValue();
      setChanged();
   }

   public void setValue(int newValue)
   {
      value = newValue;
      setChanged();
   }

   public Serializable getValue()
   {
      return new Integer(value);
   }

   public int intValue()
   {
      return value;
   }
   public String toString() {
     return Integer.toString(value);
   }
   public boolean equals (Object o) {
	   return this == o 
	   || ((o instanceof ReplicatedInteger) && ((ReplicatedInteger) o).value == value) 
	   || o.equals(value);
   }
}
