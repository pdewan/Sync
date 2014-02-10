package edu.unc.sync;

import java.util.*;

public class NullSet extends ChangeSet
{
   public NullSet() {super(null);}
   public int opcode() {return Replicated.MODIFY;}
   public void addChange(ElementChange ch) {}
   public boolean isEmpty() {return true;}
   public int size() {return 0;}
   public void print() {System.out.println("Null set");}
   public Change copy() {return this;}
   public ElementChange getCorresponding(ElementChange ch) {return new NullElementChange();}
   public Enumeration enumerateChanges() {return (new Vector(5)).elements();}
   public Enumeration enumerateChangePairs(ChangeSet C0) {
      Vector pairs = new Vector(C0.size());
      for (Enumeration e = C0.enumerateChanges(); e.hasMoreElements(); ) {
         pairs.addElement(new ChangePair((Change) e.nextElement(), new NullElementChange(), false));
      }
      return pairs.elements();
   }
}
