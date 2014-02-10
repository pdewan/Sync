package edu.unc.sync.server;

import edu.unc.sync.*;

import java.io.*;
import java.util.*;
//import com.sun.java.swing.tree.*;
//import com.sun.java.swing.table.*;
//import com.sun.java.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;

import util.models.HashtableListenable;
import util.models.HashtableListener;
import edu.unc.sync.server.SyncTreeModel;
@util.annotations.StructurePattern(util.annotations.StructurePatternNames.TABLE_PATTERN)
public class Folder
   extends ReplicatedDictionary
   implements TableModel, HashtableListenable
{
   static String[] attr_names = {"Name", "Class", "Modified", "Base version", "Object ID"};
   transient SortedStringVector all_names;  // keys (assumed to be strings) sorted lexically
   transient SortedStringVector folderNames;  // just those keys that are Folder names

   //transient TableModelListener table_listener;
   transient Vector tableListeners = new Vector();
   transient Vector hashtableListeners = new Vector();
   transient TreeNode tree_node;

   static final long serialVersionUID = 8322944550379539412L;

   public Folder()
   {
      super(10);
      all_names = new SortedStringVector();
      folderNames = new SortedStringVector();
      isConsistent = true;
   }

   public boolean hasChanged()
   {
      //return super.hasChanged();
      return true;
   }

   public Replicated put(Object key, Replicated value)
   {
      String name = (String) key;
      Replicated replaced;
      if (value instanceof Folder) {
      	 
      	 
         replaced = (Replicated) super.put(name, value);
         folderNames.add(name);
         ((Folder) value).createTreeNode(name);
         int tree_index = folderNames.indexOf(name);
         DefaultTreeModel tree_model = SyncTreeModel.getTreeModel();
			if (tree_model != null)
				tree_model.nodesWereInserted(getTreeNode(),
						new int[] { tree_index });
			((Folder) value).openObject();
			//((Folder) value).makeSerializedObjectsConsisent();
		} else if (value instanceof ReplicatedReference) {

			replaced = (Replicated) super.put(name, value);
			ReplicatedReference reference = (ReplicatedReference) value;
			Replicated obj = reference.getReferencedObject();
			
			SyncClient client = Sync.getSyncClient();
			if (client != null && obj != null)
				client.ui.openObject(obj, name);
				
		} else {
			/*
			if (this.containsKey(name) && SyncClient.getNoDuplicates())
				return this.get(name);
				*/
			replaced = (Replicated) super.put(name, new ReplicatedReference(
					value));
			//openObject(value, name);
			
		}
      if (value instanceof Delegated)
    		((Delegated) value).makeSerializedObjectConsistent();
      all_names.add(name);
      int table_index = all_names.indexOf(name);      
      //if (table_listener != null) table_listener.tableChanged(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
      notifyTableModelListeners(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
      notifyKeyPut(key, value, this.size());
      return replaced;
   }
  transient boolean isConsistent = false;
  public void makeSerializedObjectsConsisent() {
	   if (isConsistent) return;
	   isConsistent = true;
	   Enumeration elements = this.keys();
	   while (elements.hasMoreElements()) {
		   Object value = get(elements.nextElement());
		   if (value instanceof ReplicatedReference)
			   value = ((ReplicatedReference) value).getReferencedObject();
		   if (value instanceof Delegated) {
			   ((Delegated) value).makeSerializedObjectConsistent();
		   }
	   }
   }
   
   void openObject (Replicated value, String name) {
   	SyncClient client = Sync.getSyncClient();

	if (client != null && value != null && client.getAutoOpen())
		client.ui.openObject(value, name);
   	
   }
   void openObject () {
   	Enumeration keys = this.keys();
   	
   	while (keys.hasMoreElements()) {
   		String name = (String) keys.nextElement();
   		Replicated value = (Replicated) get(name);
   		if (value instanceof Folder)
   			((Folder) value).openObject();
   		else
   			openObject(value, name);
   	}
   	
   }

   public Replicated get(Object key)
   {
      Replicated value = super.get(key);
      if (value instanceof ReplicatedReference) return ((ReplicatedReference) value).getReferencedObject();
      else return value;
   }

   Replicated getDirect(Object key) {return super.get(key);}

   public Replicated remove(Object key)
   {
      String name = (String) key;
      int table_index = all_names.indexOf(name);
      Replicated value = (Replicated) super.remove(name);
      all_names.remove(name);
      //if (table_listener != null) table_listener.tableChanged(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
      notifyTableModelListeners(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
      notifyKeyRemove(key, this.size());
      if (value instanceof Folder) {
         int tree_index = folderNames.indexOf(name);
         folderNames.remove(name);
         DefaultTreeModel tree_model = SyncTreeModel.getTreeModel();
         int[] indices = new int[] {tree_index};
         TreeNode[] children = new TreeNode[] {((Folder) value).getTreeNode()};
         if (tree_model != null) tree_model.nodesWereRemoved(getTreeNode(), indices, children);
      }
      return value;
   }

   public void changeKey(Object old_key, Object new_key)
   {
      String old_name = (String) old_key;
      String new_name = (String) new_key;
      Object value = get(old_name);
      super.changeKey(old_name, new_name);
      all_names.remove(old_name);
      all_names.add(new_name);      
      //if (table_listener != null) table_listener.tableChanged(new TableModelEvent(this, 0, size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
      notifyTableModelListeners(new TableModelEvent(this, 0, size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
      notifyKeyRemove (old_key, this.size() - 1);
      notifyKeyPut (new_key, value, this.size());
      if (value instanceof Folder) {
         int tree_index = folderNames.indexOf(old_name);
         folderNames.remove(old_name);
         folderNames.add(new_name);
         ((FolderTreeNode) ((Folder) value).getTreeNode()).setName(new_name);
         DefaultTreeModel tree_model = SyncTreeModel.getTreeModel();
         if (tree_model != null) tree_model.nodeStructureChanged(getTreeNode());
      }
   }

   public int getRowCount()
   {
      return size();
   }

   public int getColumnCount()
   {
      return attr_names.length - 1;
   }

   public String getColumnName(int columnIndex)
   {
      return attr_names[columnIndex];
   }

   
   public Class getColumnClass(int columnIndex)
   {
      switch (columnIndex) {
         case 0: return String.class;
         case 1: return String.class;
         case 2: return String.class;
         case 3: return Integer.class;
         case 4: return Object.class;
         default: return Object.class;
      }
   }

   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return (columnIndex == 0) ? true : false;
   }

   private String unqualifiedClassname(String classname)
   {
      int last_index = classname.lastIndexOf('.');
      if (last_index == -1) return classname;
      else return classname.substring(last_index + 1);
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      String name = (String) all_names.elementAt(rowIndex);
      Replicated obj = getDirect(name);
      Object delegator = Sync.getReplacedDelegator(obj);
      switch (columnIndex) {
         case 0: return name;
         case 1:
            String classname;
            //adding for delegator case
            //System.out.println("Delegator:" + delegator);
            if (delegator != null) classname = delegator.getClass().getName();
            else if (!(obj instanceof ReplicatedReference)) {
              /*
              delegator = Sync.getDelegator(obj);
              if (delegator != null) classname = delegator.getClass().getName();
              else
              */
              classname = obj.getClass().getName();
            } else {
              /*
              ReplicatedReference reference = (ReplicatedReference) obj;
              Replicated referant = reference.getReferencedObject();
              delegator = Sync.getDelegator(referant);
              if (delegator != null) classname = delegator.getClass().getName();
              else
              */
              classname = ((ReplicatedReference) obj).getReferenceClassname();
            }
            return unqualifiedClassname(classname);
         case 2:
            Date mod = obj.getLastModified();
            if (mod != null) return mod.toString();
            else return "(not modified)";
         case 3: return new Integer(obj.getVersion());
         case 4:
            if (!(obj instanceof ReplicatedReference)) return obj.getObjectID();
            else return ((ReplicatedReference) obj).getReferencedID();
         default: return null;
      }
   }

   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
   {
      if (columnIndex != 0) return;
      if (!(aValue instanceof String)) return;
      String name = (String) all_names.elementAt(rowIndex);
      changeKey(name, (String) aValue);
   }
   void maybeCreateListeners() {
   	if ((tableListeners != null) && (hashtableListeners != null)) return;
   	tableListeners = new Vector();
   	hashtableListeners = new Vector();
   }

   public void addTableModelListener(TableModelListener l)
   {
   		maybeCreateListeners();
      //table_listener = l;
      if (tableListeners.contains(l)) return;
      tableListeners.addElement(l);
   }

   public void removeTableModelListener(TableModelListener l)
   {
      //table_listener = null;
   	tableListeners.removeElement(l);
   }
   
   public void notifyTableModelListeners(TableModelEvent e) {
   	maybeCreateListeners();
   	for (int i = 0; i < tableListeners.size(); i++) {
   		((TableModelListener) tableListeners.elementAt(i)).tableChanged(e);
   	}
   }
   public void addHashtableListener(HashtableListener l) {
   	maybeCreateListeners();
    if (hashtableListeners.contains(l)) return;
    hashtableListeners.addElement(l);
   	
   }
   public void removeHashtableListener(HashtableListener l) {
    hashtableListeners.addElement(l);
   	
   }
   public void notifyKeyPut(Object key, Object value, int newSize) {
   	maybeCreateListeners();
   	for (int i = 0; i < hashtableListeners.size(); i++) {
   		((HashtableListener) hashtableListeners.elementAt(i)).keyPut(this, key, value, newSize);
   	}
   }
   public void notifyKeyRemove(Object key, int newSize) {
   	maybeCreateListeners();
   	for (int i = 0; i < hashtableListeners.size(); i++) {
   		((HashtableListener) hashtableListeners.elementAt(i)).keyRemoved(this, key, newSize);
   	}
   }
 
   //public void createTreeNode(String name)
   protected void createTreeNode(String name)
   {
      tree_node = new FolderTreeNode(this, name);
      for (Enumeration e = folderNames.elements(); e.hasMoreElements(); ) {
         String folderName = (String) e.nextElement();
         ((Folder) get(folderName)).createTreeNode(folderName);
         //System.out.println("Created node for " + dir_name);
      }
   }

   //public TreeNode getTreeNode()
   protected TreeNode getTreeNode()
   {
      return tree_node;
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
   {
      stream.defaultReadObject();
      all_names = new SortedStringVector(size());
      folderNames = new SortedStringVector(size());
      for (Enumeration e = keys(); e.hasMoreElements(); ) {
         String name = (String) e.nextElement();
         all_names.add(name);
         if (getDirect(name) instanceof Folder) {
            folderNames.add(name);
         }
      }
   }
   public Object getUserObject() {
  	 if (tree_node != null) return tree_node.toString();
  	 return toString();
  }
   protected static TableModel nullFolder = new NullFolder(attr_names);
/*
   public static TableModel nullFolder = new TableModel() {
      public int getRowCount() {return 0;}
      public int getColumnCount() {return attr_names.length;}
      public String getColumnName(int columnIndex) {return attr_names[columnIndex];}
      public Class getColumnClass(int columnIndex) {return String.class;}
      public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
      public Object getValueAt(int rowIndex, int columnIndex) {return null;}
      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
      public void addTableModelListener(TableModelListener l) {}
      public void removeTableModelListener(TableModelListener l) {}
   };
   */
}
