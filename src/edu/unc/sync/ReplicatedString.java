package edu.unc.sync;

import java.io.*;

public class ReplicatedString extends ReplicatedAtomic
{
   protected String val;
   
   static final long serialVersionUID = 7121073933149262225L;

   public ReplicatedString() {}

   public ReplicatedString(String str)
   {
      val = str;
	  setChanged();
   }

   public void setValue(Serializable new_value)
   {
      val = (String) new_value;
      setChanged();
   }

   /*
   public void setValue(String new_value)
   {
      value = new_value;
      setChanged();
   }
*/
   public Serializable getValue()
   {
      return val;
   }

   public String getStringValue()
   {
      return val;
   }
   public boolean equals (Object o) {
	   return this == o 
	   || ((o instanceof ReplicatedString) && ((ReplicatedString) o).val.equals(val) )
	   || ((o != null) && o.equals(val));
   }
}
