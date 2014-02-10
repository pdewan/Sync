package edu.unc.sync;

import java.io.*;

public class ReplicatedEnum extends ReplicatedAtomic
{
   protected int ordinal;
   Object value; 
   Object[] constants = null;

   static final long serialVersionUID = 6118777884732864222L;

   public ReplicatedEnum()
   {}

   public ReplicatedEnum(int init)
   {
      ordinal = init;
   }
   int toOrdinal(Object val) {
	   if (constants == null) return -1;
	   for (int i = 0; i < constants.length; i++) {
		   if (constants[i] == val) return i;
	   }
	   return -1;
   }
   public ReplicatedEnum(Integer init)
   {
      ordinal = init.intValue();
   }
   public ReplicatedEnum(Object init)
   {
	   basicSetValue (init);
	   /*
	   if (init.getClass().isEnum()) {
		   value = init;
		   ordinal = toOrdinal(init);
		   
	   }
	   */
   }
   
   void initConstants () {
	   if (constants == null) 
		   constants = value.getClass().getEnumConstants();
   }
   void basicSetValue (Object init) {
	   if (init.getClass().isEnum()) {
		   value = init;
		   initConstants();
		   ordinal = toOrdinal(init);
		   
		   
	   }
   }

   public void setValue(Serializable newValue)
   {
	   //basicSetValue(newValue);
	   
      ordinal = ((Integer) newValue).intValue();    	  
      value = constants[ordinal];
      setChanged();
      
   }

   public void setEnumValue(Object newValue)
   {
      //ordinal = newValue;
	   basicSetValue(newValue);
      setChanged();
   }

   public Serializable getValue()
   {
      return new Integer(ordinal);
   }
   public Object getEnumValue()
   {
      return value;
   }

   
   public String toString() {
     return value.toString();
   }
   public boolean equals (Object o) {
	   return this == o
	   || value == o 
	   || o instanceof ReplicatedEnum && ((ReplicatedEnum) o).value == value 
	   || o instanceof ReplicatedEnum && ((ReplicatedEnum) o).ordinal == ordinal;
   }
}
