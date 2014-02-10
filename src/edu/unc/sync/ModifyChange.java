package edu.unc.sync;

import edu.unc.sync.server.SyncException;

public class ModifyChange extends ElementChange
{
   public Change change;
   public Object eltID;

   public ModifyChange(ObjectID id, Object eltID, Change change)
   {
      super(id);
      this.eltID = eltID;
      this.change = change;
   }

   public int opcode()
   {
      return Replicated.MODIFY;
   }

   public Change getInverse(){
	   return new ModifyChange(getObjectID(), eltID, change.getInverse());
   }
   
   public ElementChange applyTo(Replicated obj) throws ReplicationException
   {
	   ObjectID oid = change.getObjectID();
	   Replicated element;
	   Change rejected = null;
	   if (null != oid){
		   try {
			   element = Sync.getObject(oid);
		   } catch (SyncException ex) {
			   System.out.println("@@@@@@@@@@@@@@@@@");
			   throw new ReplicationException("Error applying changes to " + change.getObjectID() + ": " + ex);
		   }
		   rejected = element.applyChange(change);
	   }
	   return (rejected == null) ? null : new ModifyChange(getObjectID(), eltID, rejected);
   }
   
   public Object identifier()
   {
      return eltID;
   }
   
   public void print()
   {
      System.out.println("Modified " + eltID + ", oid = " + getObjectID());
      change.print();
   }
   
   public void fix(){
	   change.fix();
   }
}

