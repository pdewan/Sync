package edu.unc.sync;

import java.io.*;

public class ReplicatedDouble extends ReplicatedAtomic
{
   protected double value;
   
   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedDouble()
   {}

   public ReplicatedDouble(double init)
   {
      value = init;
   }

   public ReplicatedDouble(Double init)
   {
      value = init.doubleValue();
   }

   public void setValue(Serializable newValue)
   {
      value = ((Double) newValue).doubleValue();
      setChanged();
   }

   public void setValue(double newValue)
   {
      value = newValue;
      setChanged();
   }

   public Serializable getValue()
   {
      return new Double(value);
   }

   public double doubleValue()
   {
      return value;
   }
   public boolean equals (Object o) {
	   return this == o 
	   || ((o instanceof ReplicatedDouble) && ((ReplicatedDouble) o).value == value) 
	   || o.equals(value);
   }
}
