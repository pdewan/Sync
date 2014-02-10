package edu.unc.sync;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class ReplicatedRecord extends ReplicatedCollection
{
   private transient Field[] fields;
   protected static RecordMergeMatrix class_merge_matrix = new RecordMergeMatrix();
   protected RecordMergeMatrix inst_merge_matrix = null;
   
   static final long serialVersionUID = 7037530784112600988L;

   public ReplicatedRecord()
   {
      super();
      initializeReplicatedFields();
   }

   private void initializeReplicatedFields()
   {
      Class clss = getClass();
      Field[] field_arr = clss.getFields();
      Vector fields_temp = new Vector(field_arr.length);
      for (int i = 0; i < field_arr.length; i++) {
         Object fld_obj;
         try {
            fld_obj = field_arr[i].get(this);
            //System.out.println("Setting field:" + field_arr[i].getName());			
			System.out.println("Setting field:" + field_arr[i].getName() + " " + fld_obj);
			//System.out.println(fld_obj.getClass());
			if (fld_obj instanceof Replicated) {
			   System.out.println("111111111111111111111111");
			   System.out.println(fld_obj);
               fields_temp.addElement(field_arr[i]);
               //Sync.putObject((Replicated) fld_obj);
               ((Replicated) fld_obj).setParent(this);
            }
			else
				System.out.println("22222222222222");
         } catch (IllegalAccessException e) {
            System.err.println(e);
         }
      }
      fields = new Field[fields_temp.size()];
      fields_temp.copyInto(fields);
   }
/*
   public void enableReplicated(Replicated parent)
   {
      getReplicatedFields();
      super.enableReplicated(parent);
   }
*/
   /* In the nested class FieldEnumeration, "this" of ReplicatedRecord is required
    * however, "this" corresponds to FieldEnumeration when called within it
    * that's why I need this function
   */
   private ReplicatedRecord getthis()
   {
	   return this;
   }
   
   private class FieldEnumeration implements Enumeration
   {  
	   
      int i;
      FieldEnumeration() {
		  i = 0;
		  initializeReplicatedFields();
	      System.out.println(fields);
	  }
      public Object nextElement() {
         try {return fields[i++].get(getthis());}
         catch (IllegalAccessException e) {return null;} }
      public boolean hasMoreElements() {return i < fields.length;}
   }

   public Enumeration elements()
   {
      // return fields.elements();
      return new FieldEnumeration();
   }

   public Change getChange()
   {
	  initializeReplicatedFields();
      //if (!hasChanged()) return null;

      GenericChangeSet changes = new GenericChangeSet(getObjectID(), 5);
      for (int i = 0; i < fields.length; i++) {
         try {
            Replicated field_obj = (Replicated) fields[i].get(this);
            if (field_obj.hasChanged()) {
               Change field_change = field_obj.getChange();
               if (field_change != null) {
                  changes.addChange(new ModifyChange(getObjectID(), fields[i].getName(), field_change));
               }
            }
         } catch (Exception e) {
            return null;
         }
      }
      return (!changes.isEmpty()) ? changes : null;
   }

   public void clearChangeSet() {}

   public ElementChange concatElementChanges(ElementChange first, ElementChange second) throws ReplicationException
   {
      if (first instanceof ModifyChange) {
         if (second instanceof ModifyChange) {
            String field = (String) ((ModifyChange) first).identifier();
            Replicated elt = getField(field);
            Change first_change = ((ModifyChange) first).change;
            Change second_change = ((ModifyChange) second).change;
            Change concat_changes = elt.concatChanges(first_change, second_change);
            return new ModifyChange(getObjectID(), field, concat_changes);
         } else
            return first;
      } else {
         return second;
      }
   }

   public ChangeSet newChangeSet(int size)
   {
      return new GenericChangeSet(getObjectID(), size);
   }

   public ChangePair newChangeSetPair(ChangeSet cs0, ChangeSet csr)
   {
      ChangeSet A0 = new GenericChangeSet(getObjectID(), csr.size());
      ChangeSet Ar = new GenericChangeSet(getObjectID(), cs0.size());
      return new ChangePair(A0, Ar, false);
   }

   Replicated getField(String fieldname)
   {
      // return (Replicated) fields.get((String) field);
      try {
         return (Replicated) this.getClass().getField((String) fieldname).get(this);
      } catch (Exception e) {
         System.err.println("Could not identify record field element");
         return null;
      }
   }

   public MergeMatrix getClassMergeMatrix()
   {
      return class_merge_matrix;
   }

   public MergeMatrix getInstanceMergeMatrix()
   {
      return inst_merge_matrix;
   }

   public void setInstanceMergeMatrix(MergeMatrix mm)
   {
      inst_merge_matrix = (RecordMergeMatrix) mm;
   }

   public boolean instanceMergeMatrixExists()
   {
      return inst_merge_matrix != null;
   }
}
/*
   public TreeNode getChildAt(int childIndex)
   {
      Object child = fields[childIndex].get(this);
      String name = fields[childIndex].getName();
      if (child instanceof Replicated) {
         ((Replicated) child).setName(name)
         return (Replicated) child;
      } else {
         return new ObjectTreeNode(name, this);
      }
      return (TreeNode) ;
   }

   public int getChildCount()
   {
      return fields.length;
   }

   public int getIndex(TreeNode node)
   {
      int i = 0;
      for (Enumeration e = elements(); e.hasMoreElements(); ) {
         if (node.equals(e.nextElement())) return i;
         i++;
      }
      return -1;
   }

   public String getElementName(Object object)
   {
      for (int i = 0; i < fields.length; i++) {
         if (object.equals(fields[i].get(this)) return "." + fields[i].getName();
      }
      return ".";
   }
*/

