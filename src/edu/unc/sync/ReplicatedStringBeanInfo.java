package edu.unc.sync;
import java.beans.*;
import java.lang.reflect.*;

import bus.uigen.attributes.AttributeNames;

public class ReplicatedStringBeanInfo extends SimpleBeanInfo {
  public BeanDescriptor getBeanDescriptor() {
    try {
      Class c;
      Class[] params;
      c = edu.unc.sync.ReplicatedString.class;
      BeanDescriptor bd = new BeanDescriptor(c);
      return bd;
    } catch (Exception e) {
      return null;
    }
  }
  public MethodDescriptor[] getMethodDescriptors() {
    try {
      Class c;
      Class[] params;
      c = edu.unc.sync.ReplicatedString.class;
      Method m;
      MethodDescriptor[] array = new MethodDescriptor[36];
      MethodDescriptor md;
      params = new Class[1];
      params[0] = java.lang.String.class;
      m = c.getMethod("setHome",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Home ...");
      md.setValue("menuName", "Bean methods");
      array[0] = md;

      params = new Class[0];
      m = c.getMethod("preStorageWrite",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Pre Storage Write");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[1] = md;

      params = new Class[2];
      params[0] = edu.unc.sync.Change.class;
      params[1] = edu.unc.sync.Change.class;
      m = c.getMethod("concatChanges",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Concat Changes ...");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[2] = md;

      params = new Class[0];
      m = c.getMethod("getMergeMatrix",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Merge Matrix");
      md.setValue("menuName", "Bean methods");
      array[3] = md;

      params = new Class[0];
      m = c.getMethod("setChanged",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Changed");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[4] = md;

      params = new Class[2];
      params[0] = edu.unc.sync.Change.class;
      params[1] = edu.unc.sync.Change.class;
      m = c.getMethod("mergeChanges",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Merge Changes ...");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[5] = md;

      params = new Class[1];
      params[0] = java.util.Observer.class;
      m = c.getMethod("addObserver",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Add Observer ...");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Observable");
      array[6] = md;

      params = new Class[0];
      m = c.getMethod("clearChanged",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Clear Changed");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[7] = md;

      params = new Class[0];
      m = c.getMethod("getChange",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Change");
      md.setValue("menuName", "Bean methods");
      array[8] = md;

      params = new Class[0];
      m = c.getMethod("hasChanged",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Has Changed");
      md.setValue("menuName", "Observable");
      array[9] = md;

      params = new Class[0];
      m = c.getMethod("notifyObservers",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Notify Observers");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Observable");
      array[10] = md;

      params = new Class[0];
      m = c.getMethod("getParent",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Parent");
      md.setValue("menuName", "Bean methods");
      array[11] = md;

      params = new Class[1];
      params[0] = java.lang.Integer.TYPE;
      m = c.getMethod("setVersion",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Version ...");
      md.setValue("menuName", "Bean methods");
      array[12] = md;

      params = new Class[0];
      m = c.getMethod("getObjectID",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get ObjectID");
      md.setValue("menuName", "Bean methods");
      array[13] = md;

      params = new Class[0];
      m = c.getMethod("toString",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("To String");
      md.setValue("menuName", "Object");
      array[14] = md;

      params = new Class[1];
      params[0] = edu.unc.sync.Change.class;
      m = c.getMethod("applyChange",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Apply Change ...");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[15] = md;

      params = new Class[1];
      params[0] = edu.unc.sync.ObjectID.class;
      m = c.getMethod("setObjectID",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set ObjectID ...");
      md.setValue("menuName", "Replicated");
      array[16] = md;

      params = new Class[1];
      params[0] = java.lang.Object.class;
      m = c.getMethod("notifyObservers",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Notify Observers ...");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Observable");
      array[17] = md;

      params = new Class[1];
      params[0] = edu.unc.sync.MergeMatrix.class;
      m = c.getMethod("setMergeMatrix",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Merge Matrix ...");
      md.setValue("menuName", "Replicated");
      array[18] = md;

      params = new Class[0];
      m = c.getMethod("preTransmitWrite",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Pre Transmit Write");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[19] = md;

      params = new Class[0];
      m = c.getMethod("getHome",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Home");
      md.setValue("menuName", "Bean methods");
      array[20] = md;

      params = new Class[1];
      params[0] = java.util.Date.class;
      m = c.getMethod("setLastModified",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Last Modified ...");
      md.setValue("menuName", "Replicated");
      array[21] = md;

      params = new Class[0];
      m = c.getMethod("postStorageRead",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Post Storage Read");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[22] = md;

      params = new Class[0];
      m = c.getMethod("getParentID",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get ParentID");
      md.setValue("menuName", "Bean methods");
      array[23] = md;

      params = new Class[1];
      params[0] = java.io.Serializable.class;
      m = c.getMethod("setValue",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Value ...");
      md.setValue("menuName", "ReplicatedAtomic");
      array[24] = md;

      params = new Class[1];
      params[0] = edu.unc.sync.ObjectID.class;
      m = c.getMethod("setParentID",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set ParentID ...");
      md.setValue("menuName", "Replicated");
      array[25] = md;

      params = new Class[1];
      params[0] = edu.unc.sync.Replicated.class;
      m = c.getMethod("setParent",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Parent ...");
      md.setValue("menuName", "Replicated");
      array[26] = md;

      params = new Class[1];
      params[0] = java.util.Observer.class;
      m = c.getMethod("deleteObserver",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Delete Observer ...");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Observable");
      array[27] = md;

      params = new Class[0];
      m = c.getMethod("getVersion",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Version");
      md.setValue("menuName", "Bean methods");
      array[28] = md;

      params = new Class[0];
      m = c.getMethod("getStringValue",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get String Value");
      md.setValue("menuName", "Bean methods");
      array[29] = md;

      params = new Class[0];
      m = c.getMethod("getValue",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Value");
      md.setValue("menuName", "Bean methods");
      array[30] = md;

      params = new Class[0];
      m = c.getMethod("deleteObservers",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Delete Observers");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Observable");
      array[31] = md;

      params = new Class[0];
      m = c.getMethod("getLastModified",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Last Modified");
      md.setValue("menuName", "Bean methods");
      array[32] = md;

      params = new Class[0];
      m = c.getMethod("hasParent",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Has Parent");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[33] = md;

      params = new Class[0];
      m = c.getMethod("postTransmitRead",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Post Transmit Read");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Replicated");
      array[34] = md;

      params = new Class[0];
      m = c.getMethod("countObservers",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Count Observers");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "Observable");
      array[35] = md;

      return array;
    } catch (Exception e) {
      return null;
    }
  }
  public PropertyDescriptor[] getPropertyDescriptors() {

    try {
      Class c;
      Class[] params;
      PropertyDescriptor[] array = new PropertyDescriptor[1];
      PropertyDescriptor pd;
      pd = new PropertyDescriptor("value",
        edu.unc.sync.ReplicatedString.class
      );
      array[0] = pd;

      return array;
    }catch (Exception e) {return null;}
  }
}
