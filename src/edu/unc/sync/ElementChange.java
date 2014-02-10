package edu.unc.sync;

import java.io.*;

public abstract class ElementChange extends Change
{
   public ElementChange(ObjectID id) {super(id);}
   public abstract ElementChange applyTo(Replicated obj) throws ReplicationException;
   public abstract Object identifier();
   public Change copy() {return this;}
}
