package edu.unc.sync.server;

public class SortedStringVector extends java.util.Vector
{
   public SortedStringVector() {super();}
   public SortedStringVector(int size) {super(size);}

   public void add(String str)
   {
      for (int i = 0; i < size(); i++) {
         String sorted_str = (String) elementAt(i);
         int cmp = str.compareTo(sorted_str);
         if (cmp <= 0) {
            insertElementAt(str, i);
            return;
         }
      }
      addElement(str);
   }

   public void remove(String str)
   {
      removeElement(str);
   }
   
   public void list()
   {
      for (java.util.Enumeration e = elements(); e.hasMoreElements(); ) {
         System.out.println(e.nextElement());
      }
   }
}