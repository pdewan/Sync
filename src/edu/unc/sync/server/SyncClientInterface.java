package edu.unc.sync.server;

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import bus.uigen.widgets.MenuItemSelector;
import bus.uigen.widgets.MenuSelector;
import bus.uigen.widgets.VirtualMenu;
import bus.uigen.widgets.VirtualMenuBar;
import bus.uigen.widgets.VirtualMenuItem;
import bus.uigen.widgets.events.VirtualActionEvent;
import bus.uigen.widgets.events.VirtualActionListener;
import edu.unc.sync.Delegated;
import edu.unc.sync.ObjectID;
import edu.unc.sync.Replicated;
import edu.unc.sync.Sync;

public class SyncClientInterface extends ObjectServerInterface
{
   String client_home;
   SyncClient sync_client;
   //Properties properties;
   Hashtable remote_servers;
   static String local_subtree_name = "Local";
   SyncClientTreeRoot tree_root;
   //Vector serverDataList;
   //ServerProxyTable serverProxyTable;

   public SyncClientInterface(SyncClient client, PropertiesTable properties)
   {
      //super("Sync Client", client, properties);
   	  super (client.name, client, properties);
      //serverDataList = client.getServerDataList();
      //serverProxyTable = client.getServerProxyTable();
      sync_client = client;
      remote_servers = new Hashtable(4);
      //createUI("Sync Client");
      /*
      JMenuBar mb = myFrame.getJMenuBar();
      JMenu object = (JMenu) mb.getComponentAtIndex(0);

      JMenuItem replicate = new JMenuItem("Replicate");
      replicate.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            replicateAction();}});
      object.insert(replicate, GCPOS);  // position of "Garbage Collect"

      JMenuItem synchronize = new JMenuItem("Synchronize");
      synchronize.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            synchronizeAction();}});
      object.insert(synchronize, GCPOS + 1);

      object.insertSeparator(GCPOS + 2);

      JMenu server = new JMenu("Server");

      JMenu open_menu = new JMenu("Open Server");
      String servers = properties.getProperty("servers");
      for (StringTokenizer tokens  = new StringTokenizer(servers); tokens.hasMoreTokens(); ) {
         String srvr = tokens.nextToken();
         JMenuItem item = new JMenuItem(srvr);
         item.addActionListener(new OpenServerListener(srvr));
         open_menu.add(item);
      }
      JMenuItem other_server_item = new JMenuItem("Other...");
      other_server_item.addActionListener(new OpenServerListener(null));
      open_menu.add(other_server_item);
      server.add(open_menu);

      JMenuItem remove = new JMenuItem("Remove Server");
      remove.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            removeAction();}});
      server.add(remove);

      JMenuItem refresh = new JMenuItem("Refresh View");
      refresh.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            refreshAction();}});
      server.add(refresh);

      mb.add(server);

      myFrame.setVisible(true);
      ObjectEditor.treeBrowse(treeRoot, rootFolder);
      */
      /*
       * super("Sync Client", client, properties);
      //serverDataList = client.getServerDataList();
      serverProxyTable = client.getServerProxyTable();
      sync_client = client;
      remote_servers = new Hashtable(4);

      JMenuBar mb = myFrame.getJMenuBar();
      JMenu object = (JMenu) mb.getComponentAtIndex(0);

      JMenuItem replicate = new JMenuItem("Replicate");
      replicate.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            replicateAction();}});
      object.insert(replicate, GCPOS);  // position of "Garbage Collect"

      JMenuItem synchronize = new JMenuItem("Synchronize");
      synchronize.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            synchronizeAction();}});
      object.insert(synchronize, GCPOS + 1);

      object.insertSeparator(GCPOS + 2);

      JMenu server = new JMenu("Server");

      JMenu open_menu = new JMenu("Open Server");
      String servers = properties.getProperty("servers");
      for (StringTokenizer tokens  = new StringTokenizer(servers); tokens.hasMoreTokens(); ) {
         String srvr = tokens.nextToken();
         JMenuItem item = new JMenuItem(srvr);
         item.addActionListener(new OpenServerListener(srvr));
         open_menu.add(item);
      }
      JMenuItem other_server_item = new JMenuItem("Other...");
      other_server_item.addActionListener(new OpenServerListener(null));
      open_menu.add(other_server_item);
      server.add(open_menu);

      JMenuItem remove = new JMenuItem("Remove Server");
      remove.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            removeAction();}});
      server.add(remove);

      JMenuItem refresh = new JMenuItem("Refresh View");
      refresh.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            refreshAction();}});
      server.add(refresh);

      mb.add(server);

      myFrame.setVisible(true);
      ObjectEditor.treeBrowse(treeRoot, rootFolder);
       */
   }
   public SyncClient getClient() {
   	return sync_client;
   }
   
   public void createUI (String title) {
   	super.createUI(title);
   	//JMenuBar mb = myFrame.getJMenuBar();
   	VirtualMenuBar mb = myFrame.getMenuBar();
    VirtualMenu object = (VirtualMenu) mb.getComponentAtIndex(0);

    VirtualMenuItem replicate = MenuItemSelector.createMenuItem("Replicate");
    replicate.addActionListener(new VirtualActionListener() {
       public void actionPerformed(VirtualActionEvent e) {
          replicateAction();}});
    object.insert(replicate, GCPOS);  // position of "Garbage Collect"

    VirtualMenuItem synchronize = MenuItemSelector.createMenuItem("Synchronize");
    synchronize.addActionListener(new VirtualActionListener() {
       public void actionPerformed(VirtualActionEvent e) {
          synchronizeAction();}});
    object.insert(synchronize, GCPOS + 1);

    object.insertSeparator(GCPOS + 2);

    VirtualMenu server = MenuSelector.createMenu("Server");

    if (properties != null) {
    VirtualMenu open_menu = MenuSelector.createMenu("Open Server");
    String servers = properties.getProperty("servers");
    for (StringTokenizer tokens  = new StringTokenizer(servers); tokens.hasMoreTokens(); ) {
       String srvr = tokens.nextToken();
       VirtualMenuItem item = MenuItemSelector.createMenuItem(srvr);
       item.addActionListener(new OpenServerListener(srvr));
       open_menu.add(item);
    }
    VirtualMenuItem other_server_item = MenuItemSelector.createMenuItem("Other...");
    other_server_item.addActionListener(new OpenServerListener(null));
    open_menu.add(other_server_item);
    server.add(open_menu);
    }

    //JMenuItem remove = new JMenuItem("Remove Server");
    VirtualMenuItem remove = MenuItemSelector.createMenuItem("Remove Server");
    remove.addActionListener(new VirtualActionListener() {
       public void actionPerformed(VirtualActionEvent e) {
          removeAction();}});
    server.add(remove);

    VirtualMenuItem refresh = MenuItemSelector.createMenuItem("Refresh View");
    refresh.addActionListener(new VirtualActionListener() {
       public void actionPerformed(VirtualActionEvent e) {
          refreshAction();}});
    server.add(refresh);

    mb.add(server);

    myFrame.setVisible(true);
    //ObjectEditor.treeBrowse(treeRoot, rootFolder);
   }

   TreeNode createWindowRootTreeNode(Folder root)
   {
      rootFolder = root;
      rootFolder.createTreeNode("Local");
      tree_root = new SyncClientTreeRoot("Sync objects");
      tree_root.add(rootFolder.getTreeNode());
      return tree_root;
   }

   void shutdownAction()
   {
      String[] msg = new String[] {"This will shut down the Sync client.", "Are you sure you want to proceed?"};
      int resp = JOptionPane.showConfirmDialog((JFrame)myFrame.getPhysicalComponent(), msg, "Sync Client Shutdown", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
      if (resp == JOptionPane.OK_OPTION) {
         myFrame.dispose();
         sync_client.shutdown();
         System.exit(0);
      }
   }

   void removeAction()
   {
      TreePath selPath = (TreePath) objectTree.getSelectionPath();
      if(selPath != null) {
         Object selected = selPath.getLastPathComponent();
         if (selected instanceof FolderTreeNodeCopy) {
            String server_addr = ((FolderTreeNodeCopy) selected).getHome().toLowerCase();
            tree_root.remove((TreeNode) selected);
            remote_servers.remove(server_addr);
            RemoteSyncServer server = (RemoteSyncServer) remote_servers.get(server_addr);
         } else {
            JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"Only remote views can be removed"}, "Selection error", JOptionPane.ERROR_MESSAGE);
         }
      } else {
         JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"You must select an object for this operation"}, "Nothing selected", JOptionPane.ERROR_MESSAGE);
      }
   }

   void refreshAction()
   {
      TreePath selPath = (TreePath) objectTree.getSelectionPath();
      if(selPath != null) {
         Object selected = selPath.getLastPathComponent();
         if (selected instanceof FolderTreeNodeCopy) {
            TreeNode old_node = (TreeNode) selected;
            String server_addr = ((FolderTreeNodeCopy) selected).getHome().toLowerCase();
            RemoteSyncServer server = (RemoteSyncServer) remote_servers.get(server_addr);
            try {
               TreeNode new_node = server.getTreeRoot();
               tree_root.remove(old_node);
               tree_root.add(new_node);
            } catch (RemoteException ex) {
               String[] msgs = new String[] {"Error getting remote view:", ex.toString()};
               JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), msgs, "Remote server error", JOptionPane.ERROR_MESSAGE);
            }
         } else {
            JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"Only remote views need to be refreshed"}, "Selection error", JOptionPane.ERROR_MESSAGE);
         }
      } else {
         JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"You must select an object for this operation"}, "Nothing selected", JOptionPane.ERROR_MESSAGE);
      }
   }

   boolean replicatedInPath(Object[] path)
   {
      for (int i = 0; i < path.length; i++) {
         if (path[i] instanceof FolderTreeNodeCopy) {
            ObjectID oid = ((FolderTreeNodeCopy) path[i]).getObjectID();
            if (objectServer.hasObject(oid)) return true;
         }
      }
      return false;
   }

   String findUniqueName(FolderTreeNodeCopy node)
   {
      // finds a unique name in the local root

      String name = node.toString();
      if (!rootFolder.containsKey(name)) {
         return name;
      } else if (!rootFolder.containsKey(name + " (replica)")) {
         return name + " (replica)";
      } else {
         // if still duplicate, will replace existing element--that is okay
         return name + " (replica from " + node.getHome() + ")";
      }
   }

   void replicateAction()
   {
      TreePath selPath = (TreePath) objectTree.getSelectionPath();
      if(selPath != null) {
         Object selected = selPath.getLastPathComponent();
         if (selected instanceof TreeNode)
           replicateAction((TreeNode) selected);
         else
           System.out.println("**Treepath not a tree node");
      /*
         if (selected instanceof FolderTreeNodeCopy) {
            String server_addr = ((FolderTreeNodeCopy) selected).getHome();
            ObjectID oid = ((FolderTreeNodeCopy) selected).getObjectID();
            // before getting object make sure it is not already replicated
            // here.  Must also check the parents of the object because
            // they may be replicated and not yet synchronized.
            if (replicatedInPath(selPath.getPath())) {
               String[] msgs = new String[] {"This object, or a parent, is already replicated here.", "You may need to synchronize before views are consistent."};
               JOptionPane.showMessageDialog(this, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
               return;
            }
            RemoteSyncServer server = (RemoteSyncServer) remote_servers.get(server_addr);
            if (server == null) server = (RemoteSyncServer) remote_servers.get("localhost");
            try {
               Replicated obj = server.getObject(oid);
               obj.setHome(server_addr);
               // put the replica in the local root using, if possible, its remote name
               String name = findUniqueName((FolderTreeNodeCopy) selected);
               rootFolder.put(name, obj);
               //adding code for registering client
               server.synchronizeObject(null, null, sync_client.name, sync_client);
            } catch (RemoteException ex) {
               String[] msgs = new String[] {"Error replicating object", ex.toString()};
               JOptionPane.showMessageDialog(this, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
               return;
            }
         } else {
            JOptionPane.showMessageDialog(this, new String[] {"The Replicate operation works only on remote objects"}, "Selection error", JOptionPane.ERROR_MESSAGE);
         }
      */
      } else {
         JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"You must select an object for this operation"}, "Nothing selected", JOptionPane.ERROR_MESSAGE);
      }

   }

   void replicateAction(TreeNode selected)
   {
	   if (selected instanceof FolderTreeNodeCopy) {
	   	String name = findUniqueName((FolderTreeNodeCopy) selected);
		   String server_addr = ((FolderTreeNodeCopy) selected).getHome().toLowerCase();
		   ObjectID oid = ((FolderTreeNodeCopy) selected).getObjectID();
		   // before getting object make sure it is not already replicated
		   // here.  Must also check the parents of the object because
		   // they may be replicated and not yet synchronized.
		   /*
		   if (replicatedInPath(selPath.getPath())) {
			   String[] msgs = new String[] {"This object, or a parent, is already replicated here.", "You may need to synchronize before views are consistent."};
			   JOptionPane.showMessageDialog(this, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
			   return;
		   }
		   */
		   //System.out.println("in replicateAction(selected)432423");
		   //ObjectID selectedOid = ((FolderTreeNodeCopy) selected).getObjectID();
		   /*
		   if (objectServer.hasObject(selectedOid)){
			   String[] msgs = new String[] {"This object, or a parent, is already replicated here.", "You may need to synchronize before views are consistent."};
			   JOptionPane.showMessageDialog(myFrame, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
			   return;
		   }
		   

		   RemoteSyncServer server = (RemoteSyncServer) remote_servers.get(server_addr);
                   if (server == null) server = (RemoteSyncServer) remote_servers.get("localhost");
		   try {
			   Replicated obj = server.getObject(oid);
                           if (obj instanceof Delegated)
                             System.out.println("new delegator" + ((Delegated) obj).returnObject());
			   obj.setHome(server_addr);
			   // put the replica in the local root using, if possible, its remote name
			   String name = findUniqueName((FolderTreeNodeCopy) selected);
			   rootFolder.put(name, obj);
                           //just registering the client object
                          server.synchronizeObject(null, null, sync_client.name, sync_client);
		   } catch (RemoteException ex) {
			   String[] msgs = new String[] {"Error replicating object", ex.toString()};
			   JOptionPane.showMessageDialog(myFrame, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
			   return;
		   }
		   */
		   //replicateAction (oid, name, server_addr);
		    Frame errorPhysicalFrame = null;
		    if (errorMsgsFrame != null)
		    	errorPhysicalFrame = (Frame) errorMsgsFrame.getPhysicalComponent();
		   //replicateAction (oid, name, server_addr, objectServer,  sync_client, remote_servers,  (Frame) errorMsgsFrame.getPhysicalComponent(),  rootFolder);
		    replicateAction (oid, name, server_addr, objectServer,  sync_client, remote_servers,  errorPhysicalFrame,  rootFolder);
	   } else {
		   JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"The Replicate operation works only on remote objects"}, "Selection error", JOptionPane.ERROR_MESSAGE);
	   }
   }
   static ObjectID firstServerID = null;
   static void replicateAction(ObjectID oid, String name, String server_addr, 
   		SyncObjectServer objectServer, SyncClient sync_client,Hashtable remote_servers, Frame myFrame, Folder rootFolder )
   {
	   //if (selected instanceof FolderTreeNodeCopy) {
		   //String server_addr = ((FolderTreeNodeCopy) selected).getHome().toLowerCase();
		   //ObjectID oid = ((FolderTreeNodeCopy) selected).getObjectID();
		   // before getting object make sure it is not already replicated
		   // here.  Must also check the parents of the object because
		   // they may be replicated and not yet synchronized.
		   /*
		   if (replicatedInPath(selPath.getPath())) {
			   String[] msgs = new String[] {"This object, or a parent, is already replicated here.", "You may need to synchronize before views are consistent."};
			   JOptionPane.showMessageDialog(this, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
			   return;
		   }
		   */
		   //System.out.println("in replicateAction(selected)432423");
		   //ObjectID selectedOid = ((FolderTreeNodeCopy) selected).getObjectID();
   	       ObjectID selectedOid = oid;
   	       if (firstServerID == null) firstServerID = oid;
		   if (objectServer.hasObject(selectedOid)){
			   String[] msgs = new String[] {"This object, or a parent, is already replicated here.", "You may need to synchronize before views are consistent."};
			   if (myFrame != null)
			       JOptionPane.showMessageDialog((JFrame)myFrame, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
			   return;
		   }

		   RemoteSyncServer server = (RemoteSyncServer) remote_servers.get(server_addr);
		   /*
                   if (server == null) server = (RemoteSyncServer) remote_servers.get("localhost");
                   remote_servers.put(server_addr, server);
                   */
		   try {
			   Replicated obj = server.getObject(oid);
                           if (obj instanceof Delegated)
                             System.out.println("new delegator" + ((Delegated) obj).returnObject());
			   obj.setHome(server_addr);
			   // put the replica in the local root using, if possible, its remote name
			   //String name = findUniqueName((FolderTreeNodeCopy) selected);
			   if (rootFolder != null)
			   	//rootFolder.put(name, obj);
			   	rootFolder.put(server_addr, obj);
                           //just registering the client object
                          server.synchronizeObject(null, null, sync_client.name, sync_client.getExportedStub());
		   } catch (RemoteException ex) {
			   String[] msgs = new String[] {"Error replicating object", ex.toString()};
			   JOptionPane.showMessageDialog((JFrame)myFrame, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
			   return;
		   }
		   /*
	   } else {
		   JOptionPane.showMessageDialog(myFrame, new String[] {"The Replicate operation works only on remote objects"}, "Selection error", JOptionPane.ERROR_MESSAGE);
	   }
	   */
   }
    
   /*
   void replicateAction(RemoteSyncServer server)      {
                  if (server == null) server = (RemoteSyncServer) remote_servers.get("localhost");
                      try {
                              Replicated obj = server.getObject(oid);
                              obj.setHome(server_addr);
                              // put the replica in the local root using, if possible, its remote name
                              String name = findUniqueName((FolderTreeNodeCopy) selected);
                              rootFolder.put(name, obj);
                              //just registering the client object
                             server.synchronizeObject(null, null, sync_client.name, sync_client);
                      } catch (RemoteException ex) {
                              String[] msgs = new String[] {"Error replicating object", ex.toString()};
                              JOptionPane.showMessageDialog(this, msgs, "Replication error", JOptionPane.ERROR_MESSAGE);
                              return;
                      }

   }
   */

   ObjectID selectTopParent(Object[] path)
   {
      for (int i = path.length - 1; i >= 0; i--) {
         ObjectID oid = ((FolderTreeNode) path[i]).getObjectID();
         if (Sync.getObject(oid).getHome() != null) {
            Object[] sel_path = new Object[i + 1];
            for (int j = 0; j < sel_path.length; j++) sel_path[j] = path[j];
            objectTree.setSelectionPath(new TreePath(sel_path));
            return oid;
         }
      }
      return null;
   }

   public void synchronizeAction()
   {
      TreePath selPath = (TreePath) objectTree.getSelectionPath();
      if (selPath == null) {
      	//if (this.firstServerID == null) {
         JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"You must select an object for this operation"}, "Nothing selected", JOptionPane.ERROR_MESSAGE);
         return;
      	/*} else 
      		synchronizeAction(sync_client, firstServerID, firstServerID, myFrame);*/
      }
      // for now, can only synchronize things selected in tree, i.e., directories
      Object selected = selPath.getLastPathComponent();
      if (!(selected instanceof FolderTreeNode)) {
         JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), new String[] {"The Synchronize operation works only on local objects"}, "Selection error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      ObjectID oid = ((FolderTreeNode) selected).getObjectID();
      ObjectID parent_oid = selectTopParent(selPath.getPath());
      synchronizeAction(sync_client, oid, parent_oid, (Frame) errorMsgsFrame.getPhysicalComponent());
      /*
      ObjectID oid_to_sync;
      Replicated obj = Sync.getObject(oid);
      if (obj.getHome() == null) {
         //ObjectID parent_oid = selectTopParent(selPath.getPath());
         if (parent_oid == null) {
            JOptionPane.showMessageDialog(myFrame, new String[] {"Internal error: can't find parent to synchronize"}, "Selection error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         String msgs[] = new String[] {
            "This object may not be synchronized individually.",
            "Please synchronize its parent, which has been selected."};
         int resp = JOptionPane.showConfirmDialog(myFrame, msgs, "Synchronize parent", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
         if (resp == JOptionPane.CANCEL_OPTION) return;
         oid_to_sync = parent_oid;
      } else {
         oid_to_sync = oid;
      }
      String msgs[] = sync_client.synchronize(obj);
      if (msgs != null) {
         JOptionPane.showMessageDialog(myFrame, msgs, "Synchronization error", JOptionPane.ERROR_MESSAGE);
      }
      */
   }
   public  void synchronizeAction(ObjectID oid)
   {
	   Frame physicalComponent = null;
	   if (errorMsgsFrame != null)
		   physicalComponent =  (Frame)errorMsgsFrame.getPhysicalComponent();
		synchronizeAction (sync_client, oid, null, physicalComponent);  
   	//synchronizeAction (sync_client, oid, null, (Frame) errorMsgsFrame.getPhysicalComponent());
   	
   }
   public static void synchronizeAction(SyncClient sync_client, ObjectID oid, ObjectID parent_oid, Frame myFrame)
   {
   	/*
      TreePath selPath = objectTree.getSelectionPath();
      if (selPath == null) {
         JOptionPane.showMessageDialog(myFrame, new String[] {"You must select an object for this operation"}, "Nothing selected", JOptionPane.ERROR_MESSAGE);
         return;
      }
      // for now, can only synchronize things selected in tree, i.e., directories
      Object selected = selPath.getLastPathComponent();
      if (!(selected instanceof FolderTreeNode)) {
         JOptionPane.showMessageDialog(myFrame, new String[] {"The Synchronize operation works only on local objects"}, "Selection error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      ObjectID oid = ((FolderTreeNode) selected).getObjectID();
      */
      ObjectID oid_to_sync;
      
      Replicated obj = Sync.getObject(oid);
      if (obj.getHome() == null) {
         //ObjectID parent_oid = selectTopParent(selPath.getPatuh());
         if (parent_oid == null) {
         	if (myFrame != null)
            JOptionPane.showMessageDialog((JFrame)myFrame, new String[] {"Internal error: can't find parent to synchronize"}, "Selection error", JOptionPane.ERROR_MESSAGE);
            else
            	System.out.println("Internal error: can't find parent to synchronize");
         	return;
         }
         String msgs[] = new String[] {
                "This object may not be synchronized individually.",
                "Please synchronize its parent, which has been selected."};
         if (myFrame != null) {
         
         int resp = JOptionPane.showConfirmDialog((JFrame)myFrame, msgs, "Synchronize parent", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
         if (resp == JOptionPane.CANCEL_OPTION) return;
         } else {
         	System.out.println (msgs[0]);
         }
         oid_to_sync = parent_oid;
      } else {
         oid_to_sync = oid;
      }
      //Sync.supressImplicit = true;
      String msgs[] = sync_client.synchronize(obj);
      //Sync.supressImplicit = false;
      if (msgs != null) {
         JOptionPane.showMessageDialog((JFrame)myFrame, msgs, "Synchronization error", JOptionPane.ERROR_MESSAGE);
      }
   }
   
   
/*
   public String openServer(String addressAnyCase)
   // address is assumed to be a fully qualified internet machine name
   {
      String address = addressAnyCase.toLowerCase();
   	  String port = Sync.getProperty("sync.rmiregistry.port");
      String url = "rmi://" + address + ":" + port + "/SyncServer";
      try {
         RemoteSyncServer server = (RemoteSyncServer) Naming.lookup(url);
         remote_servers.put(address, server);
         tree_root.add(server.getTreeRoot());
         return null;
      } catch (Exception ex) {
         // ex.printStackTrace();
         return ex.toString();
      }
   }
   */
   //public  Vector serverDataList = new Vector();

   public String openServer(String addressAnyCase)
   
   // address is assumed to be a fully qualified internet machine name
   {
   	  try {
   	  	openServerHelper(addressAnyCase);
   	  	return null;
   	  } catch (Exception ex) {
   	  	return ex.toString();
   	  }
   	/*
   	String address = addressAnyCase.toLowerCase();
      String port = Sync.getProperty("sync.rmiregistry.port");
      String url = "rmi://" + address + ":" + port + "/SyncServer";
      try {
         RemoteSyncServer server = (RemoteSyncServer) Naming.lookup(url);
         //remote_servers.put(address, server);
		 TreeNode serverRoot = server.getTreeRoot();
         tree_root.add(serverRoot);
         String name = findUniqueName((FolderTreeNodeCopy) serverRoot);
         //taking care of localhost
		   String server_addr = ((FolderTreeNodeCopy) serverRoot).getHome().toLowerCase();
		   serverDataList.addElement(new ServerData((FolderTreeNodeCopy) serverRoot));
		   remote_servers.put(server_addr, server);
		 //replicateAction(serverRoot);
         return null;
      } catch (Exception ex) {
         // ex.printStackTrace();
         return ex.toString();
      }
      */
   }
   public ServerProxy openServerHelper(String addressAnyCase ) throws Exception
   // address is assumed to be a fully qualified internet machine name
   {
   	String address = addressAnyCase.toLowerCase();
   	int slashIndex = address.lastIndexOf('/');
   	String hostName;
   	String nameSpace;
   	if (slashIndex >= 0) {
   		nameSpace = address.substring(slashIndex + 1);
   		hostName = address.substring(0, slashIndex);
   	} else {
   		nameSpace = "SyncServer";
   		hostName = address;
   	}
   		//nameSpace = sync_client.getInitialServerID();
      //String port = Sync.getProperty("sync.rmiregistry.port");
      //String url = "rmi://" + address + ":" + port + "/SyncServer";
      //String url = "rmi://" + address + ":" + sync_client.getRMIPort()+ "/" + sync_client.getInitialServerID();
      
   		//String url = "rmi://" + hostName + ":" + sync_client.getRMIPort()+ "/" + nameSpace;
   		String serverName = SyncServer.SYNC_SERVER + "/" + nameSpace;
   		//try {
      	//old way         
         //RemoteSyncServer server = (RemoteSyncServer) Naming.lookup(url);
         //remote_servers.put(address, server);
      // sasa way
         Registry registry = SyncServer.getRegistry(hostName, Integer.parseInt(sync_client.getRMIPort()));
         RemoteSyncServer server = (RemoteSyncServer) registry.lookup(serverName);
		 Object cl = server.getClass();
         TreeNode serverRoot = server.getTreeRoot();
         tree_root.add(serverRoot);
         String name = findUniqueName((FolderTreeNodeCopy) serverRoot);
         //taking care of localhost
		   String server_addr = ((FolderTreeNodeCopy) serverRoot).getHome().toLowerCase();
		   Hashtable allClients = server.getClients();
		   ServerProxy serverData = new ServerProxy((FolderTreeNodeCopy) serverRoot, rootFolder, sync_client, allClients);
		   //serverData.setRealTimeSynchronize(Sync.getSyncMode());
		   //serverDataList.addElement(serverData);
		   
		   sync_client.getServerProxyTable().put(server_addr, serverData);
		   sync_client.getServerProxyTable().put(address, serverData);
		   remote_servers.put(server_addr, server);
		 //replicateAction(serverRoot);
         //return serverRoot;
		   return serverData;
         /*
      } catch (Exception ex) {
         // ex.printStackTrace();
         return ex.toString();
      }
      */
   }
   public ServerProxy openAndReplicateServer(String addressAnyCase)
   // address is assumed to be a fully qualified internet machine name
   {
   	ServerProxy serverProxy = null;
   	try {
   		serverProxy = openServerHelper(addressAnyCase);
   	  	replicateAction (serverProxy.getServerRoot());
   	  	return serverProxy;
   	  	//return null;
   	  } catch (Exception ex) {
     	  	System.out.println ("Could not open: " + addressAnyCase);
   		  ex.printStackTrace();
   	  	return null;
   	  }
   	/*
   	String address = addressAnyCase.toLowerCase();
      String port = Sync.getProperty("sync.rmiregistry.port");
      String url = "rmi://" + address + ":" + port + "/SyncServer";
      try {
         RemoteSyncServer server = (RemoteSyncServer) Naming.lookup(url);
         //remote_servers.put(address, server);
		 TreeNode serverRoot = server.getTreeRoot();
         tree_root.add(serverRoot);
         String name = findUniqueName((FolderTreeNodeCopy) serverRoot);
         //taking care of localhost
		   String server_addr = ((FolderTreeNodeCopy) serverRoot).getHome().toLowerCase();
		   serverDataList.addElement(new ServerData((FolderTreeNodeCopy) serverRoot));
		   remote_servers.put(server_addr, server);
		 replicateAction(serverRoot);
         return null;
      } catch (Exception ex) {
         // ex.printStackTrace();
         return ex.toString();
      }
      */
   }

	class OpenServerListener implements VirtualActionListener
	{
	   String specified;
	   public OpenServerListener(String server) {specified = server;}
	   public void actionPerformed(VirtualActionEvent evt)
	   {
	      String server_addr;
	      if (specified == null) {
            server_addr = JOptionPane.showInputDialog((JFrame)myFrame.getPhysicalComponent(), "Please enter the name of the Sync server to open:");
         } else {
            server_addr = specified;
         }
         String err_msg = openServer(server_addr);
         if (err_msg != null) {
            String[] msgs = new String[] {"Cannot open Sync server at " + server_addr, err_msg};
            JOptionPane.showMessageDialog((JFrame)myFrame.getPhysicalComponent(), msgs, "Open Sync Server Error", JOptionPane.ERROR_MESSAGE);
         }
	   }
	}
}