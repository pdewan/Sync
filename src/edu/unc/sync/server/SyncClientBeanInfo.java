package edu.unc.sync.server;
import java.beans.*;
import java.lang.reflect.*;

import bus.uigen.attributes.AttributeNames;

public class SyncClientBeanInfo extends SimpleBeanInfo {
  public BeanDescriptor getBeanDescriptor() {
    try {
      Class c;
      Class[] params;
      c = edu.unc.sync.server.SyncClient.class;
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
      c = edu.unc.sync.server.SyncClient.class;
      Method m;
      MethodDescriptor[] array = new MethodDescriptor[6];
      MethodDescriptor md;
      params = new Class[0];
      m = c.getMethod("shutdown",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Shutdown");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "SyncClient");
      array[0] = md;

      params = new Class[1];
      params[0] = java.rmi.Remote.class;
      m = c.getMethod("exportObject",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Export Object ...");
      md.setValue("menuName", "UnicastRemoteObject");
      array[1] = md;

      params = new Class[1];
      params[0] = edu.unc.sync.Replicated.class;
      m = c.getMethod("synchronize",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Synchronize ...");
      md.setValue("rightMenu", new java.lang.Boolean(true));
      md.setValue("menuName", "SyncClient");
      array[2] = md;

      params = new Class[0];
      m = c.getMethod("toString",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("To String");
      md.setValue("visible", new java.lang.Boolean(true));
      md.setValue("menuName", "Object");
      array[3] = md;

      params = new Class[0];
      m = c.getMethod("getLog",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Get Log");
      md.setValue("menuName", "Bean methods");
      array[4] = md;

      params = new Class[1];
      params[0] = java.io.OutputStream.class;
      m = c.getMethod("setLog",params);
      md = new MethodDescriptor(m);
      md.setDisplayName("Set Log ...");
      md.setValue("menuName", "RemoteServer");
      array[5] = md;

      return array;
    } catch (Exception e) {
      return null;
    }
  }

  public PropertyDescriptor[] getPropertyDescriptors(){

    try{
      Class c;
      Class[] params;
      PropertyDescriptor[] array = new PropertyDescriptor[0];	  System.out.println("######### in SyncClientBeanInfo");
      /*
      PropertyDescriptor pd;
      pd = new PropertyDescriptor("NOOP", edu.unc.sync.Replicated.class);
      array[0] = pd;
*/
      return array;
      //return array;
    }
    catch(Exception e){
      return null;
    }
    }
}
