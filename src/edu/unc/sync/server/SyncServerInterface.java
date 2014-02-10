package edu.unc.sync.server;

import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import bus.uigen.widgets.MenuItemSelector;
import bus.uigen.widgets.VirtualMenu;
import bus.uigen.widgets.VirtualMenuBar;
import bus.uigen.widgets.VirtualMenuItem;
import bus.uigen.widgets.events.VirtualActionEvent;
import bus.uigen.widgets.events.VirtualActionListener;
import edu.unc.sync.Replicated;
import edu.unc.sync.ReplicationException;
import edu.unc.sync.Sync;

public class SyncServerInterface extends ObjectServerInterface
{
   SyncServer sync_server;
   SyncObjectServer object_server;
   Properties properties;
   DefaultTreeModel tree_model;
   JTree object_tree;
   FolderEditor dir_editor;

   public SyncServerInterface(SyncServer server, PropertiesTable properties)
   {
      //super("Sync Server", server, properties);
      super("Sync Server:" + server.getHostName(), server, properties);
      sync_server = server;
      //createUI("Sync Server");
      /*
      myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      // Use local system look and feel
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ex) {
         //System.err.println("Error loading Look&Feel: " + ex);
      }

      JMenuBar mb = myFrame.getJMenuBar();
      JMenu object = (JMenu) mb.getComponentAtIndex(0);

      JMenuItem commit = new JMenuItem("Commit");
      commit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            commitAction();}});
      object.insert(commit, GCPOS);  // position of "Garbage Collect"
      object.insertSeparator(GCPOS + 1);

      myFrame.setVisible(true);
      ObjectEditor.edit(rootFolder);
      */
   }
   public void createUI(String title) {
   	 super.createUI(title);
   	//super("Sync Server", server, properties);
    //sync_server = server;
    myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    // Use local system look and feel
    try {
       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
       //System.err.println("Error loading Look&Feel: " + ex);
    }

    //JMenuBar mb = myFrame.getJMenuBar();
    VirtualMenuBar mb = myFrame.getMenuBar();
    //JMenu object = (JMenu) mb.getComponentAtIndex(0);
    VirtualMenu object = (VirtualMenu) mb.getComponentAtIndex(0);

    //JMenuItem commit = new JMenuItem("Commit");
    VirtualMenuItem commit = MenuItemSelector.createMenuItem("Commit");
    commit.addActionListener(new VirtualActionListener() {
       public void actionPerformed(VirtualActionEvent e) {
          commitAction();}});
    object.insert(commit, GCPOS);  // position of "Garbage Collect"
    object.insertSeparator(GCPOS + 1);

    myFrame.setVisible(true);
    //ObjectEditor.edit(rootFolder);
   }

   TreeNode createWindowRootTreeNode(Folder root)
   {
       String home;
       try{
         home = java.net.InetAddress.getLocalHost().getHostName();
        // if (home.indexOf('.') == -1) home = home + "." + Sync.getProperty("domain");
         if (!Sync.getProperty("domain").trim().equals("") &&
          home.indexOf('.') == -1) home += "." + Sync.getProperty("domain");
       } catch (java.net.UnknownHostException ex) {
         home = "localhost";
       }
      root.createTreeNode(home);
      return root.getTreeNode();
   }

   void commitAction()
   {
      Object obj = getSelectedObject();
      if (obj != null && obj instanceof Replicated) try {
         sync_server.commitCurrentChanges((Replicated) obj);
      } catch (ReplicationException ex) {
         JOptionPane.showMessageDialog((JFrame) myFrame.getPhysicalComponent(), new String[] {ex.toString()}, "Internal Sync error", JOptionPane.ERROR_MESSAGE);
      }
   }

   void shutdownAction()
   {
      String[] msg = new String[] {"This will shut down the Sync server.", "Are you sure you want to proceed?"};
      int resp = JOptionPane.showConfirmDialog((JFrame) myFrame.getPhysicalComponent(), msg, "Sync Server Shutdown", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
      if (resp == JOptionPane.OK_OPTION) {
         myFrame.dispose();
         sync_server.shutdown();
         System.exit(0);
      }
   }
}
