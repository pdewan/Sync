package edu.unc.sync;

import java.io.*;

public class ReplicatedFloat extends ReplicatedAtomic
{
   protected float value;
   
   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedFloat()
   {}

   public ReplicatedFloat(float init)
   {
      value = init;
   }

   public ReplicatedFloat(Float init)
   {
      value = init.floatValue();
   }

   public void setValue(Serializable newValue)
   {
      value = ((Float) newValue).floatValue();
      setChanged();
   }

   public void setValue(float newValue)
   {
      value = newValue;
      setChanged();
   }

   public Serializable getValue()
   {
      return new Float(value);
   }

   public float floatValue()
   {
      return value;
   }
   public boolean equals (Object o) {
	   return this == o 
	   || ((o instanceof ReplicatedFloat) && ((ReplicatedFloat) o).value == value) 
	   || ((o != null) && o.equals(value));
   }
}
