package edu.unc.sync;

import java.io.*;
import java.util.*;
import java.beans.*;
import java.lang.reflect.*;

public class ReplicatedObject extends ReplicatedCollection
{
	//private transient Field[] fields;
	private ReplicatedDictionary fields;
	//private transient PropertyDescriptor[] properties;
	private ReplicatedDictionary properties;
	protected static ObjectMergeMatrix class_merge_matrix = new ObjectMergeMatrix();
	protected ObjectMergeMatrix inst_merge_matrix = null;
	
	static final long serialVersionUID = 7037530784112600988L;

	public ReplicatedObject()
	{
		super();
		fields = new ReplicatedDictionary();
		fields.setParent(this);
		properties = new ReplicatedDictionary();
		properties.setParent(this);
		initializeReplicatedFields();
		initializeReplicatedProperties();
	}

	private void initializeReplicatedFields()
	{
		Class clss = getClass();
		Field[] field_arr = clss.getFields();
		for (int i = 0; i < field_arr.length; i++) {
			Object fld_obj;
			Replicated old_field;
			String fld_name;
			try {
				fld_obj = field_arr[i].get(this);
				fld_name = field_arr[i].getName();
				//System.out.println("Setting field:" + field_arr[i].getName());			
				//System.out.println("Setting field:" + field_arr[i].getName() + " " + fld_obj);
				//System.out.println(fld_obj.getClass());
				if (fld_obj instanceof Replicated) {
					old_field = fields.get(fld_name);
					if ((old_field == null) ||
						(!((Replicated)fld_obj).getObjectID().equals(old_field.getObjectID()))){
							fields.put(fld_name, (Replicated) fld_obj);
					}
				}
			} catch (IllegalAccessException e) {
				System.err.println(e);
			}
		}
		//fields = new Field[fields_temp.size()];
		//fields_temp.copyInto(fields);
	}
	
	private void setReplicatedFields()
	{
		Class clss = getClass();
		Field[] field_arr = clss.getFields();
		for (int i = 0; i < field_arr.length; i++) {
			Object fld_obj;
			String fld_name;
			try {
				fld_obj = field_arr[i].get(this);
				fld_name = field_arr[i].getName();
				if (fld_obj instanceof Replicated) {
					Replicated obj = (Replicated)fields.get(fld_name);
					field_arr[i].set(this, obj);
				}
			}
			catch (Exception e){
			System.err.println(e);
			}
		}
	}
	
	private void initializeReplicatedProperties()
	{
		Class clss = getClass();
		
		try{
			BeanInfo beanInfo = Introspector.getBeanInfo(clss);
			PropertyDescriptor[] properties_arr = beanInfo.getPropertyDescriptors();
			Vector properties_temp = new Vector(properties_arr.length);
			
			//System.out.println("Properties_arr.length is " + properties_arr.length);
			
			for (int i=0; i < properties_arr.length; i++){
				Method readMethod = properties_arr[i].getReadMethod();
				Method writeMethod = properties_arr[i].getWriteMethod();
				Object prop_obj;
				String prop_name;
				Replicated old_prop;
				if ((readMethod != null) && (writeMethod != null)){
					prop_name = readMethod.getName();
					if (readMethod.getName().equals(new String("getParent")) == false)
					{
						prop_obj = readMethod.invoke(this, null);
						if (prop_obj instanceof Replicated){
							old_prop = properties.get(prop_name);
							if ((old_prop == null) ||
								(!((Replicated)prop_obj).getObjectID().equals(old_prop.getObjectID()))){
									properties.put(prop_name, (Replicated) prop_obj);
							}
						}
					}
				}
			}
			//properties = new PropertyDescriptor[properties_temp.size()];
			//properties_temp.copyInto(properties);
		}
		catch (Exception e){
			System.err.println(e);
		}
	}
	
	private void setReplicatedProperties()
	{
		Class clss = getClass();
		
		try{
			BeanInfo beanInfo = Introspector.getBeanInfo(clss);
			PropertyDescriptor[] properties_arr = beanInfo.getPropertyDescriptors();
			Vector properties_temp = new Vector(properties_arr.length);
			
			//System.out.println("Properties_arr.length is " + properties_arr.length);
			
			for (int i=0; i < properties_arr.length; i++){
				Method readMethod = properties_arr[i].getReadMethod();
				Method writeMethod = properties_arr[i].getWriteMethod();
				String prop_name;
				if ((readMethod != null) && (writeMethod != null)){
					prop_name = readMethod.getName();
					if (readMethod.getName().equals(new String("getParent")) == false)
					{
						Replicated obj = (Replicated)properties.get(readMethod.getName());
						Object[] args = new Object[1];
						args[0] = obj;
						writeMethod.invoke(this, args);
					}
				}
			}
		}
		catch (Exception e){
			System.err.println(e);
		}
	}

	/* In the nested class FieldEnumeration, "this" of ReplicatedRecord is required
	* however, "this" corresponds to FieldEnumeration when called within it
	* that's why I need this function
	*/
	private ReplicatedObject getthis()
	{
		return this;
	}
	
	private class FeatureEnumeration implements Enumeration
	{  
		
		int i;
		//boolean flag; // Whether field or property should be enumerated
		FeatureEnumeration() {
			i = 0;
			//flag = false;
			initializeReplicatedFields();
			initializeReplicatedProperties();
		}
		
		public Object nextElement() {
			switch (i){
			case 0:
				{
					i++;
					return fields;
				}
			case 1:
				{
					i++;
					return properties;
				}
			default:
				{
					return null;
				}
			}
		}
		
		public boolean hasMoreElements()
		{
			return i<2;
		}
	}

	public Enumeration elements()
	{
		// return fields.elements();
		return new FeatureEnumeration();
	}

	public Change getChange()
	{
		//if (!hasChanged()) return null;
		
		initializeReplicatedFields();
		initializeReplicatedProperties();
		
		//if (!hasChanged()) return null;

		GenericChangeSet changes = new GenericChangeSet(getObjectID(), 5);
		
		try{
			//if (fields.hasChanged())
			//{
				Change fields_change = fields.getChange();
				if (fields_change != null){
					changes.addChange(new ModifyChange(getObjectID(), new String("fields"), fields_change));
				}
			//}
		}
		catch (Exception e){
			return null;
		}
		
		try{
			//if (properties.hasChanged())
			//{
				Change properties_change = properties.getChange();
				if (properties_change != null){
					changes.addChange(new ModifyChange(getObjectID(), new String("properties"), properties_change));
				}
			//}
		}
		catch (Exception e){
			return null;
		}
		
		return (!changes.isEmpty()) ? changes : null;
	}

	public Change applyChange(Change change) throws ReplicationException{
		// Synchronize the hashtables for fields and properties
		Change ch = super.applyChange(change);
		// Now, just use the synchronized hashtables set fields and
		//properties to correct objects
		setReplicatedFields();
		setReplicatedProperties();
		return ch;
	}
	
	public void clearChangeSet() {}

	public ElementChange concatElementChanges(ElementChange first, ElementChange second) throws ReplicationException
	{
		if (first instanceof ModifyChange) {
			if (second instanceof ModifyChange) {
				String name = (String) ((ModifyChange) first).identifier();
				Replicated elt;
				if (name.equals(new String("fields"))){
					elt = fields;
				}
				else{
					elt = properties;
				}

				Change first_change = ((ModifyChange) first).change;
				Change second_change = ((ModifyChange) second).change;
				Change concat_changes = elt.concatChanges(first_change, second_change);
				return new ModifyChange(getObjectID(), name, concat_changes);
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
		inst_merge_matrix = (ObjectMergeMatrix) mm;
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

