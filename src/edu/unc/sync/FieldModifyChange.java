package edu.unc.sync;

public class FieldModifyChange extends ModifyChange
{
   String field;

   public FieldModifyChange(ObjectID id, String field, Change change)
   {
      super(id, field, change);
      this.field = field;
   }
/*
   public Change concat(Change change)
   {
      if (change instanceof FieldModifyChange)
         return new FieldModifyChange(object_id, field, this.change.concat(((FieldModifyChange) change).change));
      // else if (change instanceof FieldReadChange)
      //    return this;
      else
         // is Replace
         return change;
   }
*/
   public ElementChange applyTo(Replicated obj) throws ReplicationException
   {
      Replicated fld_obj = ((ReplicatedRecord) obj).getField(field);
      ChangeSet rejected = (ChangeSet) fld_obj.applyChange(this);
      if (rejected != null)
         return new FieldModifyChange(getObjectID(), field, rejected);
      else
         return null;
   }

   public Object identifier()
   {
      return field;
   }

   public void print()
   {
      System.out.println("Modified field "+field);
      change.print();
   }
}

