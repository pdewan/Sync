package edu.unc.sync;

import java.io.*;
import java.util.Date;

public class ReplicatedReference extends Replicated
{
   private String classname;
   private int version;  // will this be correctly updated?
   private ObjectID idOfObject;
   private Replicated theObject = null;  // this variable is used for transmitting
                                         // the reference to another site.  see
                                         // preTransmitWrite and postTransmitRead.

   static final long serialVersionUID = -837006239238064255L;

   public ReplicatedReference(Replicated obj)
   {
      //System.out.println("replicated reference created for: " + obj);
      if (obj instanceof ReplicatedReference) throw new RuntimeException("Internal error: A ReplicatedReference cannot refer to another ReplicatedReference");
      idOfObject = obj.getObjectID();
      classname = obj.getClass().getName();
      version = obj.getVersion();
      obj.setParent(this);
   }

   public ObjectID getReferencedID()
   {
      return idOfObject;
   }

   public Replicated getReferencedObject()
   {
      return Sync.getObject(idOfObject);
   }

   public String getReferenceClassname()
   {
      return classname;
   }

   public void setParentID(ObjectID parentID)
   {
      super.setParentID(parentID);
      if (parentID == null) {
         Replicated obj = Sync.getObject(idOfObject);
         obj.setParentID(null);
      }
   }

   public void setVersion(int v)
   {
      super.setVersion(v);
      Replicated obj = Sync.getObject(idOfObject);
      obj.setVersion(v);
   }

   public void setHome(String addr)
   {
      super.setHome(addr);
      Replicated obj = Sync.getObject(idOfObject);
      obj.setHome(addr);
   }

   public void setLastModified(Date date)
   {
      super.setLastModified(date);
      Replicated obj = Sync.getObject(idOfObject);
      obj.setLastModified(date);
   }

   public boolean hasChanged()
   {
      Replicated obj = Sync.getObject(idOfObject);
      return obj.hasChanged();
   }

   public void clearChanged()
   {
      Replicated obj = Sync.getObject(idOfObject);
      obj.clearChanged();
   }

   public Change getChange()
   {
      Replicated obj = Sync.getObject(idOfObject);
      return obj.getChange();
   }

   public Change applyChange(Change change) throws ReplicationException
   {
      Replicated obj = Sync.getObject(idOfObject);
      return obj.applyChange(change);
   }

   public Change concatChanges(Change first, Change second) throws ReplicationException
   {
      Replicated obj = Sync.getObject(idOfObject);
      return obj.concatChanges(first, second);
   }

   public ChangePair mergeChanges(Change central, Change remote) throws ReplicationException
   {
      Replicated obj = Sync.getObject(idOfObject);
      return obj.mergeChanges(central, remote);
   }

   protected MergeMatrix getClassMergeMatrix()
   {
      Replicated obj = Sync.getObject(idOfObject);
      return obj.getClassMergeMatrix();
   }

   public void postTransmitRead()
   {
     //System.out.println("post transmit  read called");
      // in reading the reference, the referenced object will also be read.
      // theObject variable may be reset to null.
      theObject = null;
       //System.out.println("post transmit  read ended");
   }

   public void preTransmitWrite()
   {
      //System.out.println("pre transmit  write called");
      // setting theObject variable to the referenced object will cause the
      // object to be transmitted along with the reference.
      theObject = getReferencedObject();
      //System.out.println("pre tarnsmit write ended");
   }

   public void preStorageWrite()
   {
     //System.out.println("pre storage write called" );
      // setting theObject variable to the referenced object will cause the
      // object to be transmitted along with the reference.
      theObject = null;
      //System.out.println("pre storage write ended" );
   }
}
