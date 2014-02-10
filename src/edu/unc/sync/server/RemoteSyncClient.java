package edu.unc.sync.server;

import edu.unc.sync.*;

import java.rmi.*;
//import javax.swing.tree.*;
//import javax.swing.*;
//import java.util.Hashtable;
//import com.sun.java.swing.tree.TreeNode;

public interface RemoteSyncClient extends Remote
{
/*
   public void notifyAction(ObjectID objectID, String theSourceName, Remote theSource, ObjectID theOid) throws RemoteException;
   public void clientJoined(ObjectID serverObjectID, String name, Remote obj) throws RemoteException;
   public void clientLeft(ObjectID serverObjectID, String name, Remote obj) throws RemoteException;
   */
   public void notifyAction(ObjectID objectID, String theSourceName, RemoteSyncClient theSource, ObjectID theOid) throws RemoteException;
   public void clientJoined(ObjectID serverObjectID, String name, RemoteSyncClient obj) throws RemoteException;
   public void clientLeft(ObjectID serverObjectID, String name, RemoteSyncClient obj) throws RemoteException;
}
