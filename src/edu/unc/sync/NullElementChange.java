package edu.unc.sync;

public class NullElementChange extends ElementChange
{
   public NullElementChange() {super(null);}
   public int opcode() {return 0;}
   public Object identifier() {return null;}
   public void print() {}
   public Change concat(Change change) {return change;}
   public ElementChange applyTo(Replicated obj) {return null;}
}