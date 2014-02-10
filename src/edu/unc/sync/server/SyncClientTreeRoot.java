package edu.unc.sync.server;

import java.util.*;
//import com.sun.java.swing.tree.*;
import javax.swing.tree.*;
import edu.unc.sync.server.SyncTreeModel;

public class SyncClientTreeRoot implements TreeNode
{
   Vector nodes;
   String name;
   
   public SyncClientTreeRoot(String name)
   {
      nodes = new Vector();
      this.name = name;
   }
   
   public void add(TreeNode node)
   {
      int index;
      for (index = 0; index < nodes.size(); index++) {
         String sorted_str = nodes.elementAt(index).toString();
         int cmp = node.toString().compareTo(sorted_str);
         if (cmp <= 0) {  // new node name comes before existing node name
            nodes.insertElementAt(node, index);
            break;
         }
      }
      if (index == nodes.size()) nodes.addElement(node);
      
      if (node instanceof FolderTreeNode) ((FolderTreeNode) node).setParent(this);
      else if (node instanceof FolderTreeNodeCopy) ((FolderTreeNodeCopy) node).setParent(this);
      
      DefaultTreeModel tree_model = SyncTreeModel.getTreeModel();
      if (tree_model != null) tree_model.nodesWereInserted(this, new int[] {index});
   }
   
   public void remove(TreeNode node)
   {
      int index = nodes.indexOf(node);
      Object removed = nodes.elementAt(index);
      nodes.removeElementAt(index);

      DefaultTreeModel tree_model = SyncTreeModel.getTreeModel();
      if (tree_model != null) tree_model.nodesWereRemoved(this, new int[] {index}, new Object[] {removed});
   }

   public Enumeration children() 
   {
      return nodes.elements();
   }

   public boolean getAllowsChildren()
   {
      return true;
   }

   public TreeNode getChildAt(int index)
   {
      return (TreeNode) nodes.elementAt(index);
   }

   public int getChildCount()
   {
      return nodes.size();
   }

   public boolean isLeaf()
   {
      return false;
   }

   public int getIndex(TreeNode child)
   {
      return nodes.indexOf(child);
   }

   public TreeNode getParent()
   {
      return null;
   }
   
   public String toString()
   {
      return name;
   }
}
