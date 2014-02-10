package edu.unc.sync.server;

import edu.unc.sync.*;
import java.rmi.*;
//import javax.swing.tree.*;
//import javax.swing.*;
//import com.sun.java.swing.tree.TreeNode;
import javax.swing.tree.TreeNode;
import java.util.Hashtable;

public interface RemoteSyncServer extends Remote
{
   //public Object getObject(java.net.URL url) throws RemoteException;
   public Replicated getObject(ObjectID id) throws RemoteException;
   public ObjectID putObject(Replicated object) throws RemoteException;
   //public Object getAttribute(ObjectID id, String attr) throws RemoteException;
   //public void setAttribute(ObjectID id, String attr, Object value) throws RemoteException;
  // public Change synchronizeObject(ObjectID oid, Change changes, String name, Remote obj) throws RemoteException;
   public Change synchronizeObject(ObjectID oid, Change changes, String name, RemoteSyncClient obj) throws RemoteException;
   public TreeNode getTreeRoot() throws RemoteException;
   public Hashtable getClients() throws RemoteException;
}
