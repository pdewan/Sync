package edu.unc.sync;

import java.io.*;

public class ReplicatedLong extends ReplicatedAtomic
{
   protected long value;

   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedLong()
   {}

   public ReplicatedLong(long init)
   {
      value = init;
   }

   public ReplicatedLong(Long init)
   {
      value = init.longValue();
   }

   public void setValue(Serializable newValue)
   {
      value = ((Long) newValue).longValue();
      setChanged();
   }

   public void setValue(long newValue)
   {
      value = newValue;
      setChanged();
   }

   public Serializable getValue()
   {
      return new Long(value);
   }

   public long longValue()
   {
      return value;
   }
   public String toString() {
     return Long.toString(value);
   }
   public boolean equals (Object o) {
	   return this == o 
	   || ((o instanceof ReplicatedLong) && ((ReplicatedLong) o).value == value) 
	   || ((o != null) && o.equals(value));
   }
}
