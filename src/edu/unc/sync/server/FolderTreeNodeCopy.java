package edu.unc.sync.server;

import edu.unc.sync.*;
import java.util.Enumeration;
//import com.sun.java.swing.tree.*;
import javax.swing.tree.*;
import java.net.InetAddress;

public class FolderTreeNodeCopy implements TreeNode, java.io.Serializable
{
   TreeNode parent;
   FolderTreeNodeCopy[] my_children;
   String name;
   ObjectID objectID;
   String home;
   ServerProxy serverData;
   InetAddress inetAddress;
   String server_id;

   public FolderTreeNodeCopy(FolderTreeNode node, String the_server_id)
   {
      parent = null;
      server_id = the_server_id;
      try{
      	 inetAddress = java.net.InetAddress.getLocalHost();
      	 home = inetAddress.getHostName().toLowerCase();
         //home = java.net.InetAddress.getLocalHost().getHostName().toLowerCase();
         //if (home.indexOf('.') == -1) home = home + "." + Sync.getProperty("domain");
         if (!Sync.getProperty("domain").trim().equals("") &&
          home.indexOf('.') == -1) home += "." + Sync.getProperty("domain");
      } catch (java.net.UnknownHostException ex) {
         home = "localhost";
      }
      //name = node.toString();
      name = node.toString().toLowerCase();
      objectID = node.getFolder().getObjectID();
      my_children = new FolderTreeNodeCopy[node.getChildCount()];
      for (int i = 0; i < node.getChildCount(); i++) {
         my_children[i] = new FolderTreeNodeCopy((FolderTreeNode) node.getChildAt(i), server_id);
         my_children[i].setParent(this);
      }
      //new ServerData(this);
   }
   public void setServerData(ServerProxy theServerData) {
   	 serverData = theServerData;
   }

   public void setParent(TreeNode node)
   {
      parent = node;
   }

   public String toString()
   {
      return name;
   }

   public ObjectID getObjectID()
   {
      return objectID;
   }

   protected String getHome()
   {
      return home + "/" + server_id;
   }
   public String getAddress()
   {
      return inetAddress.getHostAddress(); 
   }
   public InetAddress getInetAddress()
   {
      return inetAddress;
   }

   public Enumeration children()
   {
      return new Enumeration() {
         int i = 0;
         public Object nextElement() {
            return my_children[i++]; }
         public boolean hasMoreElements() {
            return i < my_children.length; } };
   }

   public boolean getAllowsChildren()
   {
      return true;
   }

   public TreeNode getChildAt(int index)
   {
      return my_children[index];
   }

   public int getChildCount()
   {
      return my_children.length;
   }

   public boolean isLeaf()
   {
   	return getChildCount() == 0;
   	//return true;
   }

   public int getIndex(TreeNode child)
   {
      if (child instanceof FolderTreeNodeCopy) {
         for (int i = 0; i < my_children.length; i++) if (my_children[i].equals(child)) return i;
      }
      return -1;
   }

   public TreeNode getParent()
   {
      return parent;
   }
   public Object getUserObject() {
   	/*
  	 if (isLeaf()) {
  	 	//return toString();
  	 	return serverData;
  	 } else
  	 */
  	 	return toString();
  }
   /*
   public ServerData getServerInformation() {
   	 return serverData;
   }
   */
   public boolean getRealTimeSynchronize() {
   	return serverData.getRealTimeSynchronize();
   }
   public void setRealTimeSynchronize(boolean newVal) {
   	serverData.setRealTimeSynchronize(newVal);
   }
}
