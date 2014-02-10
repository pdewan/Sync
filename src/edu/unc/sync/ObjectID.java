package edu.unc.sync;

import java.io.*;
import java.net.*;
import java.util.*;

public class ObjectID implements Serializable
{
   static final long serialVersionUID = 4005632839482584934L;
   
   private static String hostAddress = null;
   /* crashes compiler
   {
      try {
         hostAddress = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
         hostAddress = "0.0.0.0";
      }
   }
   */
   private static long lastTime = (new Date()).getTime();
   
   private String primary, secondary, name_space;
   public String getPrimary() {
   		return primary;
   }
   public String getSecondary() {
		return secondary;
   }
   public String getNameSpace() {
	return name_space;
}

   private ObjectID(String p, String s, String the_user_given_id)
   {
      init (p, s, the_user_given_id);
   }
   void init (String p, String s, String the_user_given_id) {
    primary = p;
    secondary = s;
    name_space = the_user_given_id;
   }
   private ObjectID(String p, String s)
   {
   	  init (p, s, "");
   }
   
   public static ObjectID fromString(String oid_str)
   {
      /*
   	  int lastDot = oid_str.lastIndexOf('.');
      String pri = oid_str.substring(0, lastDot);
      String sec = oid_str.substring(lastDot + 1);
      return new ObjectID(pri, sec);
      */
   	int firstDot = oid_str.indexOf('.');
   	int lastDot = oid_str.lastIndexOf('.');
    String pri = oid_str.substring(0, firstDot);
    String ns = oid_str.substring(firstDot + 1, lastDot);
    String sec = oid_str.substring(lastDot + 1);
    return new ObjectID(pri, sec, ns);
   }
      
   public static ObjectID newObjectID(String the_user_given_id)
   {
      if (hostAddress == null) {
         try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
         } catch (UnknownHostException e) {
            hostAddress = "0.0.0.0";
         }
      }
      String pri = new String(hostAddress);
      long newTime = (new Date()).getTime();
      if (newTime <= lastTime) newTime = ++lastTime;
      else lastTime = newTime;
      String sec = String.valueOf(newTime);
      return new ObjectID(pri, sec, the_user_given_id);
   }
   public static ObjectID newObjectID()   	
   {
   	  return newObjectID("");
   	  /*
      if (hostAddress == null) {
         try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
         } catch (UnknownHostException e) {
            hostAddress = "0.0.0.0";
         }
      }
      String pri = new String(hostAddress);
      long newTime = (new Date()).getTime();
      if (newTime <= lastTime) newTime = ++lastTime;
      else lastTime = newTime;
      String sec = String.valueOf(newTime);
      return new ObjectID(pri, sec, the_user_given_id);
      */
   }

   public boolean equals(Object obj)
   {
      ObjectID other = (ObjectID) obj;
      return primary.equals(other.primary) & secondary.equals(other.secondary);
   }

   public int hashCode()
   {
      // satisfies requirement that hash codes of two objects are equal
      // if equal according to equals() method.
      return secondary.hashCode();
   }

   public String toString()
   {
      return primary + "." +  name_space  + "." + secondary;
   }
}
