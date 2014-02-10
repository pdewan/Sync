package edu.unc.sync;

public class NullChange extends Change
{
   public NullChange() {super(null);}
   public void print() {System.out.println("Null change");}
   public int opcode() {return Replicated.NOOP;}
   public Change concat(Change change) {return change;}
   public Change applyTo(Replicated obj) {return null;}
   public Change copy() {return this;}
}
