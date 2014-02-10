package edu.unc.sync.server;

import edu.unc.sync.Replicated;
import java.awt.event.*;
import java.util.*;
//import com.sun.java.swing.event.*;
//import com.sun.java.swing.*;
import javax.swing.event.*;
import javax.swing.*;

import bus.uigen.ars.*;
import edu.unc.sync.Sync;
import edu.unc.sync.Sync;

public class FolderEditor
   extends JTable
{
   JPopupMenu edit_menu;
   public static int EDIT = 1;
   public static int VIEW = 2;
   String[] cutNames;
   Replicated[] cutValues;

   public FolderEditor(Folder root, int mode)
   {
      super(root);
      setShowGrid(false);
      //setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      if (mode == VIEW) {
         removeEditor();
      }
   }

   public void newFolder()
   {
      addObject(new Folder(), uniqueName());
   }

   public String uniqueName()
   {
   
   	
      Folder targetDir = (Folder) getModel();
      String hostName = "";
      /*
      try {
        hostName = java.net.InetAddress.getLocalHost().getHostName() + Sync.getSyncClient().id +  ":";
      } catch (Exception e) {};
      */
      try {
        hostName = java.net.InetAddress.getLocalHost().getHostName();
      } catch (Exception e) {};
      return uniqueName(targetDir);
      /*

      String base = hostName + "Unnamed";

      int suffix = 1;
      while (targetDir.containsKey(base + String.valueOf(suffix))) suffix++;
      return base + String.valueOf(suffix);
      */
   }
   public static String uniqueName(Folder targetDir)
   {
    String name = "";
    /*
    try {
      hostName = java.net.InetAddress.getLocalHost().getHostName() + Sync.getSyncClient().id +  ":";
    } catch (Exception e) {};
    */
    try {
      name = java.net.InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {};
    String hostName = name;
    if (Sync.getSyncClient() != null)
    	hostName = name + Sync.getSyncClient().client_id +  ":";
      //String hostName = name + Sync.getSyncClient().client_id +  ":";
     

      String base = hostName + "Unnamed";

      int suffix = 1;
      while (targetDir.containsKey(base + String.valueOf(suffix))) suffix++;
      return base + String.valueOf(suffix);
   }

   public void addObject(Replicated object, String name)
   {
      Folder targetDir = (Folder) getModel();
      if (!targetDir.containsKey(name)) targetDir.put(name, object);
      setModel(targetDir);
   }

   Replicated getSelectedObject()
   {
      if (getSelectedRowCount() != 1) return null;
      int index = getSelectedRow();
      String name = (String) getValueAt(index, 0);
      return ((Folder) getModel()).get(name);
   }

   void cut()
   {
      Folder dir = (Folder) getModel();
      if (dir == Folder.nullFolder | dir == null) return;
      int[] rows = getSelectedRows();
      cutNames = new String[rows.length];
      cutValues = new Replicated[rows.length];
      for (int i = 0; i < rows.length; i++) cutNames[i] = (String) dir.getValueAt(rows[i], 0);
      for (int i = 0; i < rows.length; i++) cutValues[i] = (Replicated) dir.remove(cutNames[i]);
   }

   void copy()
   {
      JOptionPane.showMessageDialog(this, new String[] {"The Copy command is not yet implemented"}, "Sorry...", JOptionPane.INFORMATION_MESSAGE);
   }

   void paste()
   {
      Folder dir;
      Replicated selected = getSelectedObject();
      if (selected instanceof Folder) dir = (Folder) selected;
      else dir = (Folder) getModel();
      if (dir == Folder.nullFolder | dir == null) return;
      if (cutNames == null || cutNames.length == 0) return;
      for (int i = 0; i < cutNames.length; i++) {
         String name = cutNames[i];
         if (dir.containsKey(name)) {
            int suffix = 2;
            while (dir.containsKey(name + String.valueOf(suffix))) suffix++;
            name = name + suffix;
         }
         dir.put(name, cutValues[i]);
      }
   }

   public void delete()
   {
      Folder dir = (Folder) getModel();
      if (dir == Folder.nullFolder | dir == null) return;
      int[] rows = getSelectedRows();
      String[] names = new String[rows.length];
      for (int i = 0; i < rows.length; i++) names[i] = (String) dir.getValueAt(rows[i], 0);
      for (int i = 0; i < names.length; i++) {Replicated gone = dir.remove(names[i]);}
   }
}

