package edu.unc.sync;

import java.io.*;

public class ReplicatedShort extends ReplicatedAtomic
{
   protected short value;

   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedShort()
   {}

   public ReplicatedShort(short init)
   {
      value = init;
   }

   public ReplicatedShort(Short init)
   {
      value = init.shortValue();
   }

   public void setValue(Serializable newValue)
   {
      value = ((Short) newValue).shortValue();
      setChanged();
   }

   public void setValue(short newValue)
   {
      value = newValue;
      setChanged();
   }

   public Serializable getValue()
   {
      return new Short(value);
   }

   public short shortValue()
   {
      return value;
   }
   public String toString() {
     return Short.toString(value);
   }
   public boolean equals (Object o) {
	   return this == o 
	   || ((o instanceof ReplicatedShort) && ((ReplicatedShort) o).value == value) 
	   || ((o != null) && o.equals(value));
   }
}
