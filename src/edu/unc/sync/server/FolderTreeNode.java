package edu.unc.sync.server;

import edu.unc.sync.*;
import java.util.Enumeration;
//import com.sun.java.swing.tree.*;
import javax.swing.tree.*;

public class FolderTreeNode implements TreeNode
{
   Folder folder;
   TreeNode parent;
   String name;

   public FolderTreeNode(Folder folder, String name)
   {
      this.folder = folder;
      parent = null;
      this.name = name;
   }
   
   public void setParent(TreeNode node)
   {
      parent = node;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String toString()
   {
      return name;
   }

   protected ObjectID getObjectID()
   {
      return folder.getObjectID();
   }
   
   public Folder getFolder()
   {
      return folder;
   }

   public Enumeration children() 
   {
      return new Enumeration() {
         int i = 0;
         public Object nextElement() {
            return ((Folder) folder.get((String) folder.folderNames.elementAt(i++))).getTreeNode();}
         public boolean hasMoreElements() {
            return i < folder.folderNames.size(); } };
   }

   public boolean getAllowsChildren()
   {
      return true;
   }

   public TreeNode getChildAt(int index)
   {
      String name = (String) folder.folderNames.elementAt(index);
      Folder subfolder = (Folder) folder.get(name);
      if (subfolder.getTreeNode() == null) subfolder.createTreeNode(name);
      return subfolder.getTreeNode();
   }
   /*
   public int getChildFolderCount()
   {
   	  
      return folder.folderNames.size();
   }
   */
   public int getChildCount()
   {
   	 
      return folder.folderNames.size();
   }

   public boolean isLeaf()
   {
      //return false;
   		return getChildCount() == 0;
   }
   public Object getUserObject() {
   	/*
   	 if (isLeaf()) {
   	 	return folder;
   	 } else
   	 */
   	 	return toString();
   }

   public int getIndex(TreeNode child)
   {
      return folder.folderNames.indexOf(child.toString());
   }

   public TreeNode getParent()
   {
      if (parent != null) return parent;
      else {
         Replicated parent_obj = folder.getParent();
         if (parent_obj == null || !(parent_obj instanceof Folder)) return null;
         else return ((Folder) parent_obj).getTreeNode();
      }
   }
}
