package edu.unc.sync.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import util.trace.Tracer;
import bus.uigen.widgets.FrameSelector;
import bus.uigen.widgets.MenuBarSelector;
import bus.uigen.widgets.MenuItemSelector;
import bus.uigen.widgets.MenuSelector;
import bus.uigen.widgets.ScrollPaneSelector;
import bus.uigen.widgets.VirtualComponent;
import bus.uigen.widgets.VirtualFrame;
import bus.uigen.widgets.VirtualMenu;
import bus.uigen.widgets.VirtualMenuBar;
import bus.uigen.widgets.VirtualMenuItem;
import bus.uigen.widgets.VirtualToolkit;
import bus.uigen.widgets.events.VirtualActionEvent;
import bus.uigen.widgets.events.VirtualActionListener;
import bus.uigen.widgets.swing.SwingSplitPane;
import bus.uigen.widgets.swing.SwingToolkit;
import bus.uigen.widgets.tree.TreeSelector;
import bus.uigen.widgets.tree.VirtualTree;
import edu.unc.sync.Delegated;
import edu.unc.sync.DelegatedUtils;
import edu.unc.sync.ObjectFactory;
import edu.unc.sync.ObjectID;
import edu.unc.sync.Replicated;
import edu.unc.sync.Sync;

public
abstract class ObjectServerInterface //extends com.sun.java.swing.JFrame
{
   Object invoker; // will be SyncClient or SyncServer
   SyncObjectServer objectServer;
   Folder rootFolder;
   TreeNode treeRoot;
   PropertiesTable properties;
   Hashtable remoteServers;
   //JTree objectTree;
   VirtualTree objectTree;
   FolderEditor folderEditor;
   JPopupMenu folderEditMenu;
   JSplitPane splitPane;
   //JFrame myFrame = new JFrame();
   VirtualFrame myFrame; /* = new JFrame();*/
   Vector appNames = new Vector();
   int GCPOS;
   private Vector apps = new Vector(1);
   VirtualFrame errorMsgsFrame;
   
   public Folder getRootFolder() {
   	return rootFolder;
   }
   public TreeNode getTreeRoot() {
   	return treeRoot;
   }
   String title;

   public ObjectServerInterface(String theTitle, Object invoker, PropertiesTable propsTbl)
   {
		VirtualToolkit.setDefaultToolkit(new SwingToolkit());

   	title = theTitle;
      //super(title);
   	//myFrame.setTitle(title);
      this.invoker = invoker;
      this.properties = propsTbl;
      //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      //myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      // Use local system look and feel
      /*
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ex) {
         //System.err.println("Error loading Look&Feel: " + ex);
      }
      */
      objectServer = Sync.getObjectServer();
      rootFolder = (Folder) objectServer.getRootObject();
      folderEditor = new FolderEditor(rootFolder, FolderEditor.EDIT);
      //folderEditMenu = new FolderEditMenu();
      //folderEditor.add(folderEditMenu);
      //folderEditor.addMouseListener(new FolderMouseAdapter());
      /*
      JMenuBar mb = new JMenuBar();
      //setJMenuBar(mb);
      myFrame.setJMenuBar(mb);

      JMenu object = new JMenu("Object");
      mb.add(object);

      JMenu new_menu = new JMenu("New");
      JMenuItem folderItem = new JMenuItem("Folder");
         folderItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               folderEditor.newFolder();}});
      new_menu.add(folderItem);
      new_menu.addSeparator();
      */
      if (properties != null) {

      String objects = properties.getProperty("objects");
      for (StringTokenizer tokens  = new StringTokenizer(objects); tokens.hasMoreTokens(); ) {
         String obj = tokens.nextToken();
         /*
         JMenuItem item = new JMenuItem(obj.replace('_', ' '));
         item.addActionListener(new NewObjectListener(obj));
         new_menu.add(item);
         */
         appNames.addElement(obj);
      }
      }
      /*
      object.add(new_menu);

      JMenu apps = new JMenu("Application");
      String appList = properties.getProperty("applications");
      for (StringTokenizer tokens  = new StringTokenizer(appList); tokens.hasMoreTokens(); ) {
         String appName = tokens.nextToken();
         JMenuItem item = new JMenuItem(appName);
         item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               startApplication(evt.getActionCommand());}});
         apps.add(item);
      }
      object.add(apps);

      object.addSeparator();

      JMenuItem garbage = new JMenuItem("Garbage Collect...");
      garbage.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            //new GarbageCollectionDialog(ObjectServerInterface.this, objectServer);}});
      		new GarbageCollectionDialog(myFrame, objectServer);}});
      object.add(garbage);
      GCPOS = object.getItemCount() - 1;

      JMenuItem shutdown = new JMenuItem("Shutdown");
      shutdown.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            shutdownAction();}});
      object.add(shutdown);

      JMenu edit = new JMenu("Edit");
      mb.add(edit);

      JMenuItem cut = new JMenuItem("Cut");
      cut.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.cut();}});
      edit.add(cut);

      JMenuItem copy = new JMenuItem("Copy");
      copy.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.copy();}});
      edit.add(copy);

      JMenuItem paste = new JMenuItem("Paste");
      paste.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.paste();}});
      edit.add(paste);

      JMenuItem delete = new JMenuItem("Delete");
      delete.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.delete();}});
      edit.add(delete);

      edit.addSeparator();

      JMenuItem props = new JMenuItem("Properties...");
      props.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            //new PropertiesEditorDialog(ObjectServerInterface.this, properties);}});
      		new PropertiesEditorDialog(myFrame, properties);}});
      edit.add(props);
      */

      treeRoot = createWindowRootTreeNode(rootFolder);
      //ObjectEditor.treeBrowse(treeRoot, rootFolder);
      DefaultTreeModel dtm = new DefaultTreeModel(treeRoot, true);
      SyncTreeModel.setTreeModel(dtm);
      //objectTree = new JTree(dtm);
      objectTree = TreeSelector.createTree();
      objectTree.setModel(dtm);
      /*
      objectTree.addMouseListener(new ObjectTreeListener());

      Component treePane = new JScrollPane(objectTree);
      Component folderPane = JTable.createScrollPaneForTable(folderEditor);

      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, folderPane);
      splitPane.getRightComponent().setBackground(Color.white);
      //getContentPane().add(splitPane);
      myFrame.getContentPane().add(splitPane);

      //addWindowListener(new WindowAdapter() {
      myFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {shutdownAction();}});

      //pack();
      myFrame.pack();
      //setSize(600, 300);
      myFrame.setSize(600, 300);
      */
      /*
       *  //super(title);
   	myFrame.setTitle(title);
      this.invoker = invoker;
      this.properties = propsTbl;
      //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      // Use local system look and feel
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ex) {
         //System.err.println("Error loading Look&Feel: " + ex);
      }
      objectServer = Sync.getObjectServer();
      rootFolder = (Folder) objectServer.getRootObject();
      folderEditor = new FolderEditor(rootFolder, FolderEditor.EDIT);
      folderEditMenu = new FolderEditMenu();
      folderEditor.add(folderEditMenu);
      folderEditor.addMouseListener(new FolderMouseAdapter());

      JMenuBar mb = new JMenuBar();
      //setJMenuBar(mb);
      myFrame.setJMenuBar(mb);

      JMenu object = new JMenu("Object");
      mb.add(object);

      JMenu new_menu = new JMenu("New");
      JMenuItem folderItem = new JMenuItem("Folder");
         folderItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               folderEditor.newFolder();}});
      new_menu.add(folderItem);
      new_menu.addSeparator();

      String objects = properties.getProperty("objects");
      for (StringTokenizer tokens  = new StringTokenizer(objects); tokens.hasMoreTokens(); ) {
         String obj = tokens.nextToken();
         JMenuItem item = new JMenuItem(obj.replace('_', ' '));
         item.addActionListener(new NewObjectListener(obj));
         new_menu.add(item);
         appNames.addElement(obj);
      }
      
      object.add(new_menu);

      JMenu apps = new JMenu("Application");
      String appList = properties.getProperty("applications");
      for (StringTokenizer tokens  = new StringTokenizer(appList); tokens.hasMoreTokens(); ) {
         String appName = tokens.nextToken();
         JMenuItem item = new JMenuItem(appName);
         item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               startApplication(evt.getActionCommand());}});
         apps.add(item);
      }
      object.add(apps);

      object.addSeparator();

      JMenuItem garbage = new JMenuItem("Garbage Collect...");
      garbage.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            //new GarbageCollectionDialog(ObjectServerInterface.this, objectServer);}});
      		new GarbageCollectionDialog(myFrame, objectServer);}});
      object.add(garbage);
      GCPOS = object.getItemCount() - 1;

      JMenuItem shutdown = new JMenuItem("Shutdown");
      shutdown.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            shutdownAction();}});
      object.add(shutdown);

      JMenu edit = new JMenu("Edit");
      mb.add(edit);

      JMenuItem cut = new JMenuItem("Cut");
      cut.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.cut();}});
      edit.add(cut);

      JMenuItem copy = new JMenuItem("Copy");
      copy.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.copy();}});
      edit.add(copy);

      JMenuItem paste = new JMenuItem("Paste");
      paste.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.paste();}});
      edit.add(paste);

      JMenuItem delete = new JMenuItem("Delete");
      delete.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            folderEditor.delete();}});
      edit.add(delete);

      edit.addSeparator();

      JMenuItem props = new JMenuItem("Properties...");
      props.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            //new PropertiesEditorDialog(ObjectServerInterface.this, properties);}});
      		new PropertiesEditorDialog(myFrame, properties);}});
      edit.add(props);

      treeRoot = createWindowRootTreeNode(rootFolder);
      //ObjectEditor.treeBrowse(treeRoot, rootFolder);
      DefaultTreeModel dtm = new DefaultTreeModel(treeRoot, true);
      SyncTreeModel.setTreeModel(dtm);
      objectTree = new JTree(dtm);
      objectTree.addMouseListener(new ObjectTreeListener());

      Component treePane = new JScrollPane(objectTree);
      Component folderPane = JTable.createScrollPaneForTable(folderEditor);

      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, folderPane);
      splitPane.getRightComponent().setBackground(Color.white);
      //getContentPane().add(splitPane);
      myFrame.getContentPane().add(splitPane);

      //addWindowListener(new WindowAdapter() {
      myFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {shutdownAction();}});

      //pack();
      myFrame.pack();
      //setSize(600, 300);
      myFrame.setSize(600, 300);
      
       */      
   }
   public VirtualFrame getErrorMsgsFrame() {
   	return errorMsgsFrame;
   }
   public void setErrorMsgsFrame(VirtualFrame newVal) {
   	errorMsgsFrame = newVal;
   }
   public void setErrorMsgsFrameIfUndefined(VirtualFrame newVal) {
   	if (errorMsgsFrame == null) errorMsgsFrame = newVal;
   }
   public void createUI() {
   	createUI(title);
   }
   public void createUI(String title) {
   	
   	   //myFrame = new JFrame();
   	   myFrame = FrameSelector.createFrame();
   	   errorMsgsFrame = myFrame;
       myFrame.setTitle(title);
       //this.invoker = invoker;
       //this.properties = propsTbl;
       //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
       myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
       // Use local system look and feel
       try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
       } catch (Exception ex) {
          //System.err.println("Error loading Look&Feel: " + ex);
       }
       //objectServer = Sync.getObjectServer();
       //rootFolder = (Folder) objectServer.getRootObject();
       //folderEditor = new FolderEditor(rootFolder, FolderEditor.EDIT);
       folderEditMenu = new FolderEditMenu();
       folderEditor.add(folderEditMenu);
       folderEditor.addMouseListener(new FolderMouseAdapter());

       //JMenuBar mb = new JMenuBar();
       VirtualMenuBar mb = MenuBarSelector.createMenuBar();
       //setJMenuBar(mb);
       //myFrame.setJMenuBar(mb);
       myFrame.setMenuBar(mb);

       //JMenu object = new JMenu("Object");
       VirtualMenu object = MenuSelector.createMenu("Object");
       mb.add(object);

       //JMenu new_menu = new JMenu("New");
       VirtualMenu new_menu = MenuSelector.createMenu("New");
       //JMenuItem folderItem = new JMenuItem("Folder");
       VirtualMenuItem folderItem = MenuItemSelector.createMenuItem("Folder");
       MenuItemSelector.createMenuItem("Folder");
          folderItem.addActionListener(new VirtualActionListener() {
             public void actionPerformed(VirtualActionEvent evt) {
                folderEditor.newFolder();}});
       new_menu.add(folderItem);
       new_menu.addSeparator();
       /*
       String objects = properties.getProperty("objects");
       for (StringTokenizer tokens  = new StringTokenizer(objects); tokens.hasMoreTokens(); ) {
          String obj = tokens.nextToken();
          JMenuItem item = new JMenuItem(obj.replace('_', ' '));
          item.addActionListener(new NewObjectListener(obj));
          new_menu.add(item);
          appNames.addElement(obj);
       }
       */
       for (int i = 0; i < appNames.size(); i++) {
       	String obj = (String) appNames.elementAt(i);
       	//JMenuItem item = new JMenuItem(obj.replace('_', ' '));
       	VirtualMenuItem item = MenuItemSelector.createMenuItem(obj.replace('_', ' '));
        item.addActionListener(new NewObjectListener(obj));
        new_menu.add(item);
       }
       
       /*
       JMenuItem other_new_item = new JMenuItem("Other...");
       other_new_item.addActionListener(new NewObjectListener(null));
       new_menu.add(other_new_item);
       */
       object.add(new_menu);
       if (properties != null) {
       //JMenu apps = new JMenu("Application");
       VirtualMenu apps =  MenuSelector.createMenu("Application");
      
       String appList = properties.getProperty("applications");
       for (StringTokenizer tokens  = new StringTokenizer(appList); tokens.hasMoreTokens(); ) {
          String appName = tokens.nextToken();
          VirtualMenuItem item = MenuItemSelector.createMenuItem(appName);
          item.addActionListener(new VirtualActionListener() {
             public void actionPerformed(VirtualActionEvent evt) {
                startApplication(evt.getActionCommand());}});
          apps.add(item);
       }
       object.add(apps);
       //object.add(new_menu);

       object.addSeparator();
       }
       //JMenuItem garbage = new JMenuItem("Garbage Collect...");
       VirtualMenuItem garbage = MenuItemSelector.createMenuItem("Garbage Collect...");
       garbage.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             //new GarbageCollectionDialog(ObjectServerInterface.this, objectServer);}});
       		new GarbageCollectionDialog((JFrame) myFrame.getPhysicalComponent(), objectServer);}});
       object.add(garbage);
       GCPOS = object.getItemCount() - 1;

       VirtualMenuItem shutdown = MenuItemSelector.createMenuItem("Shutdown");
       shutdown.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             shutdownAction();}});
       object.add(shutdown);
       VirtualMenuItem exit = MenuItemSelector.createMenuItem("Exit");
       exit.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             exitAction();}});
       object.add(exit);

       VirtualMenu edit = MenuSelector.createMenu("Edit");
       mb.add(edit);

       VirtualMenuItem cut = MenuItemSelector.createMenuItem("Cut");
       cut.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             folderEditor.cut();}});
       edit.add(cut);

       VirtualMenuItem copy = MenuItemSelector.createMenuItem("Copy");
       copy.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             folderEditor.copy();}});
       edit.add(copy);

       VirtualMenuItem paste = MenuItemSelector.createMenuItem("Paste");
       paste.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             folderEditor.paste();}});
       edit.add(paste);

       VirtualMenuItem delete = MenuItemSelector.createMenuItem("Delete");
       delete.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             folderEditor.delete();}});
       edit.add(delete);

       edit.addSeparator();

       VirtualMenuItem props = MenuItemSelector.createMenuItem("Properties...");
       props.addActionListener(new VirtualActionListener() {
          public void actionPerformed(VirtualActionEvent e) {
             //new PropertiesEditorDialog(ObjectServerInterface.this, properties);}});
       		new PropertiesEditorDialog((JFrame) myFrame.getPhysicalComponent(), properties);}});
       edit.add(props);
       /*
       treeRoot = createWindowRootTreeNode(rootFolder);
       //ObjectEditor.treeBrowse(treeRoot, rootFolder);
       DefaultTreeModel dtm = new DefaultTreeModel(treeRoot, true);
       SyncTreeModel.setTreeModel(dtm);
       objectTree = new JTree(dtm);
       */
       objectTree.addMouseListener(new ObjectTreeListener());
 /*
       Component treePane = new JScrollPane(objectTree);
       Component folderPane = JTable.createScrollPaneForTable(folderEditor);
       */
       VirtualComponent treePane = ScrollPaneSelector.createScrollPane( objectTree);
       Component folderPane = JTable.createScrollPaneForTable(folderEditor);
       //VirtualComponent folderPane = TableSelector.createScrollPaneForTable(folderEditor);

       splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, (Component) treePane.getPhysicalComponent(), folderPane);
       //splitPane = SplitPaneSelector.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, folderPane);
       splitPane.getRightComponent().setBackground(Color.white);
       //getContentPane().add(splitPane);
       myFrame.getContentPane().add(SwingSplitPane.virtualSplitPane(splitPane));

       //addWindowListener(new WindowAdapter() {
       myFrame.addWindowListener(new WindowAdapter() {
          //public void windowClosing(WindowEvent e) {shutdownAction();}});
    	 public void windowClosing(WindowEvent e) {exitAction();}});

       //pack();
       myFrame.pack();
       //setSize(600, 300);
       myFrame.setSize(600, 300);
    
   }
   
   public Vector getApplicationNames() {
   	return appNames;
   }
   public JFrame getJFrame() {
   	return (JFrame) myFrame.getPhysicalComponent();
   }
   public VirtualFrame getFrame() {
	   	return myFrame;
	   }

   abstract TreeNode createWindowRootTreeNode(Folder root);

   abstract void shutdownAction();
   void exitAction() {
	   System.out.println("Exiting without saving");
	   System.exit(-1);
   }

   ObjectID getSelectedObject()
   {
      TreePath path = (TreePath) objectTree.getSelectionPath();
      if (path == null) return null;
      Object obj = path.getLastPathComponent();
      return ((FolderTreeNode) obj).getObjectID();
   }

   // override this method to set Sync client or server also
   void startApplication(String name)
   {
      if (name == null) return;
      String prop_name = name + ".class";
      String class_name = properties.getProperty(prop_name);
      if (class_name == null) {
         //JOptionPane.showMessageDialog(this, new String[] {"Couldn't find \"" + prop_name + "\" in client properties"}, "Open Application Error", JOptionPane.ERROR_MESSAGE);
         JOptionPane.showMessageDialog(getJFrame(), new String[] {"Couldn't find \"" + prop_name + "\" in client properties"}, "Open Application Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      try {
         Class theClass = Class.forName(class_name);
         SyncApplication app = (SyncApplication) theClass.newInstance();
		 apps.addElement(app);
         app.init(invoker);
      } catch (Exception ex) {
         String[] msgs = new String[] {"Error starting application:", ex.toString()};
         //JOptionPane.showMessageDialog(ObjectServerInterface.this, msgs, "Open Application Error", JOptionPane.ERROR_MESSAGE);
         JOptionPane.showMessageDialog(getJFrame(), msgs, "Open Application Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      return;
   }
   /*

   public class NewObjectListener implements ActionListener
   {
      String nickname;

      public NewObjectListener(String name)
      {
         nickname = name;
      }

      public void actionPerformed(ActionEvent evt)
      {
         if (folderEditor.getModel() == null | folderEditor.getModel() == Folder.nullFolder) {
            JOptionPane.showMessageDialog(myFrame, "Please open a folder for this operation", "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         Replicated newObject = null;
         //Object delegator = null;
         String objName = folderEditor.uniqueName();
         String classname = properties.getProperty(nickname + ".class");
         //String delegatorClassName = null;
         if (classname == null) {
            JOptionPane.showMessageDialog(myFrame, "Could not find property naming class for " + nickname, "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         String editorClassname = properties.getProperty(classname + ".editor");
         System.out.println("Nickname:" + nickname + ";Classname:" + classname+ ";Editor:" + editorClassname);
         if (editorClassname != null) {
            try {
              //new wordpad.WordPadEditor();
              //new syncprojectmanager.SyncPMObjectEditor();
              //new syncprojectmanager.SyncPMObjectEditor();
              //Class tryClass = Class.forName("syncprojectmanager.SyncPMObjectEditor");
               System.out.println("Abt to get class for name:" + editorClassname);
               Class theClass = Class.forName(editorClassname);
               System.out.println("Instantiated:" + editorClassname);
               SyncApplication app = (SyncApplication) theClass.newInstance();
			   apps.addElement(app);
               app.init(invoker);
               newObject = app.newObject(classname, objName);
            } catch (Exception e) {
               String[] messages = {"Error instantiating " + classname, e.toString()};
               e.printStackTrace();
               JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
               return;
            }
         } else {
            try {
               Class theClass = Class.forName(classname);
               newObject = (Replicated) theClass.newInstance();
            } catch (Exception e) {
               String[] messages = {"Error instantiating " + classname, e.getMessage()};
               JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         //delegator = Sync.getDelegator(newObject);
         //System.out.println("In ObjectServerInterface: adding object " + newObject.getObjectID() + " to folder");
         if (newObject != null) folderEditor.addObject(newObject, objName);
         else JOptionPane.showMessageDialog(myFrame, "Object returned from editor was null", "Instantiation Error", JOptionPane.ERROR_MESSAGE);
      }
   }
   */

   public void instantiateClass (String nickname, Folder targetDir) {
   	instantiateClass (FolderEditor.uniqueName(targetDir), nickname, targetDir);
   	
   }
   public void instantiateClass (Class objectClass, 
   		Class editorClass, Folder targetDir) {
   		instantiateClass (FolderEditor.uniqueName(targetDir),objectClass, 
   	   		editorClass, targetDir);   
   }
   public SyncClient getClient() {
   	return null;
   }
   public SyncApplication getEditor (String editorClass) {
	   Class c;
	   if (editorClass == null) return null;
	   try {
		   c = Class.forName(editorClass);
		   return newEditor(c);
		   
	   } catch (Exception e) {
		  return null;
		   
	   }
	   
   }
   public SyncApplication newEditor (Class editorClass) {
	   SyncClient client = getClient();
  		if (editorClass == null && client != null)
  			try {
  				if (client.getEditorClass() != null)
  			editorClass = Class.forName (client.getEditorClass());
  			} catch (Exception e) {
  				System.out.println( "Cannot instantiate: " + client.globalEditorClass);
  			}
  		
  		if (editorClass != null) {
  			try {
  				ObjectFactory factory = Sync.getObjectFactory(editorClass);
  				if (factory != null)
  					return (SyncApplication) factory.newInstance(editorClass);
  				//Class clss = Class.forName(className);
  				else
  					return(SyncApplication) editorClass.newInstance();
  			
  		} catch (Exception e) {
  			System.out.println( e + "when  instantiating: " + editorClass);
  		
	           return null;
			}
  		}
  		return null;
	   
   }
   public void instantiateClass (String objName, Class objectClass, 
		   Class editorClass, Folder targetDir) {
	   if (objName == null) {
		   instantiateClass (objectClass, editorClass, targetDir);
		   return;
	   }
	   if (targetDir.containsKey(objName) && getClient().getNoDuplicates()) return;
	   Replicated newObject = null;
	   /*
	   SyncClient client = getClient();
	   if (editorClass == null && client != null)
		   try {
			   editorClass = Class.forName (client.getEditorClass());
		   } catch (Exception e) {
			   System.out.println( ("Cannot instantiate: " + client.globalEditorClass));
		   }
		   */
		   /*
		   if (editorClass != null) {
			   try {
				   SyncApplication app = (SyncApplication) editorClass.newInstance();
				   apps.addElement(app);
				   app.init(invoker);
				   //newObject = app.newObject(objectClass.getName(), objName);
				   newObject = newObject(objectClass);
				   addObject (newObject, objName, app);
				   
			   } catch (Exception e) {
				   String[] messages = {"Error instantiating " + objectClass.getName(), e.toString()};
				   e.printStackTrace();
				   JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
				   return;
			   }
			   
			   
			   
		   } else  {
			   try {
				   
				   //newObject = (Replicated) objectClass.newInstance();
				   newObject = (Replicated) newObject(objectClass);
			   } catch (Exception e) {
				   String[] messages = {"Error instantiating " + objectClass.getName(), e.toString()};
				   e.printStackTrace();
				   JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
				   return;
			   }
			   
		   } 
		   */
	   		SyncApplication app = newEditor(editorClass);
	   		if (app != null) {
	   			apps.addElement(app);
				   app.init(invoker);
				   //newObject = app.newObject(objectClass.getName(), objName);
				   newObject = newObject(objectClass);
				   addObject (newObject, objName, app);
	   			
	   		} else
	   			newObject = (Replicated) newObject(objectClass);
	   			
		   if (newObject != null) {
			   //folderEditor.addObject(newObject, objName);
			   //if (!targetDir.containsKey(objName) || !SyncClient.getNoDuplicates()) 
			   targetDir.put(objName, newObject);
		   }
		   else if(myFrame != null)
			   JOptionPane.showMessageDialog(getJFrame(), "Object returned from editor was null", "Instantiation Error", JOptionPane.ERROR_MESSAGE);
		   
   
   }
   public void instantiateClass (String objName, Object object, 
	   		SyncApplication app, Folder targetDir) {
	   		if (objName == null) {
	   			//instantiateClass (objectClass, editorClass, targetDir);
	   			return;
	   		}
	   		try {
	   		if (targetDir.containsKey(objName) && getClient().getNoDuplicates()) return;
	   		Replicated newObject = null;
	   		SyncClient client = getClient();   		
	   		
	 		   apps.addElement(app);
	           app.init(invoker);
	           //newObject = app.newObject(objectClass.getName(), objName);
	           newObject = newObject(object);
	           addObject(newObject, objName, app);
	           //app.addObject(newObject, objName);
	   		} catch (Exception e) {
	   			e.printStackTrace();
	   		}	   			
	   	
	   }
   public void instantiateClass (String objName, Object object, 
	   		String editorClass, Folder targetDir) {
	   		if (objName == null) {
	   			//instantiateClass (objectClass, editorClass, targetDir);
	   			return;
	   		}
	   		try {
	   		if (targetDir.containsKey(objName) && getClient().getNoDuplicates()) return;
	   		Replicated newObject = null;
	   		//SyncClient client = getClient();
	   		/*
	   		SyncApplication app = getEditor(editorClass);	   		
	 		   apps.addElement(app);
	           app.init(invoker);
	           //newObject = app.newObject(objectClass.getName(), objName);
	            * 
	            */
	           newObject = newObject(object);
	           if (newObject == null) return;
	           //if (newObject != null) {
	        	   SyncApplication app = getEditor(editorClass);
	        	   if (app != null) {
	        		   apps.addElement(app);
	        		   app.init(invoker);
	        		   addObject(newObject, objName, app);
	        	   }
	           
				   targetDir.put(objName, newObject);
			   
	           //}
	           //app.addObject(newObject, objName);
	   		} catch (Exception e) {
	   			e.printStackTrace();
	   		}	   			
	   	
	   }
   
   	public Replicated newObject(Class clss)
	{
		//b = new DelegationTest();
   		Object b = null;
   		
		try{
			ObjectFactory factory = Sync.getObjectFactory(clss);
			if (factory != null) {
				b = factory.newInstance(clss);
				if (b == null) {
					Tracer.error ("factory " + factory + " returned null object");
					return null;
				}
			//Class clss = Class.forName(className);
			} else {
				b = clss.newInstance();
				if (b == null) {
					Tracer.error(" class " + clss + " does not have a null constructor. Use a factory.");
					return null;
				}
			}
		}
		catch(Exception e){
			Tracer.error("Could not successfully call parameterless constructor in Class: " + clss);
			e.printStackTrace();
		}
		
		
		Replicated newObject = DelegatedUtils.convertObject((Object) b);
		
   			
		/*
		//DelegatedReplicatedObject replB = new DelegatedReplicatedObject(b);
		//((Delegated)repl).registerAsListener();
		Sync.delegatedTable.put(b, repl);
		
		uiFrame editor = uiGenerator.generateUIFrame(b);
		editors.addElement(editor);
		System.out.println("new object " +  className + " " + name); 		  
		editor.setVisible(true);
		*/
		return newObject;
   	
   }
   	public void addObject(Replicated newObject, String objName, SyncApplication app)
	{
		//b = new DelegationTest();   		
		if (newObject instanceof Delegated) {
       		Delegated obj = (Delegated) newObject;  
       		Object model = obj.returnObject();
       		app.addObject(model, objName);
           } else
           app.addObject(newObject, objName);
   		
   	
   }
	public Replicated newObject(Object b)
	{
		
		
		Replicated repl = DelegatedUtils.convertObject((Object) b);
		/*
		//DelegatedReplicatedObject replB = new DelegatedReplicatedObject(b);
		//((Delegated)repl).registerAsListener();
		Sync.delegatedTable.put(b, repl);
		
		uiFrame editor = uiGenerator.generateUIFrame(b);
		editors.addElement(editor);
		System.out.println("new object " +  className + " " + name); 		  
		editor.setVisible(true);
		*/
		return repl;
   	
   }
  
   public void instantiateClass (String objName, String classname, 
   		String editorClassname, Folder targetDir) {
   	Class editorClass, objectClass;
   	editorClass = null;
   	objectClass = null;
    try {
        if (editorClassname != null)
        editorClass = Class.forName(editorClassname);
        if (classname != null)
        	objectClass = Class.forName(classname);
    
        
      } catch (Exception e) {
         String[] messages = {"Error instantiating " + classname, e.toString()};
         e.printStackTrace();
         JOptionPane.showMessageDialog(getJFrame(), messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
   
    instantiateClass (objName, objectClass,   editorClass, targetDir);
   	
   	/*
   	Replicated newObject = null;
   	if (editorClassname != null) {
        try {
          //new wordpad.WordPadEditor();
          //new syncprojectmanager.SyncPMObjectEditor();
          //new syncprojectmanager.SyncPMObjectEditor();
          //Class tryClass = Class.forName("syncprojectmanager.SyncPMObjectEditor");
           System.out.println("Abt to get class for name:" + editorClassname);
           Class theClass = Class.forName(editorClassname);
           System.out.println("Instantiated:" + editorClassname);
           SyncApplication app = (SyncApplication) theClass.newInstance();
 		   apps.addElement(app);
           app.init(invoker);
           newObject = app.newObject(classname, objName);
        } catch (Exception e) {
           String[] messages = {"Error instantiating " + classname, e.toString()};
           e.printStackTrace();
           JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
           return;
        }
     } else {
        try {
           Class theClass = Class.forName(classname);
           newObject = (Replicated) theClass.newInstance();
        } catch (Exception e) {
           String[] messages = {"Error instantiating " + classname, e.getMessage()};
           JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
           return;
        }
     }
   	if (newObject != null) {
    	//folderEditor.addObject(newObject, objName);
        if (!targetDir.containsKey(objName)) targetDir.put(objName, newObject);
    }
    else if(myFrame != null)
    	JOptionPane.showMessageDialog(myFrame, "Object returned from editor was null", "Instantiation Error", JOptionPane.ERROR_MESSAGE);
   	*/
   }
  
   
   public void instantiateClass (String objName, String nickname, Folder targetDir) {
   	Replicated newObject = null;
    //Object delegator = null;
    //String objName = folderEditor.uniqueName();
    String classname = properties.getProperty(nickname + ".class");
    //String delegatorClassName = null;
    if (classname == null) {
       JOptionPane.showMessageDialog(getJFrame(), "Could not find property naming class for " + nickname, "Error", JOptionPane.ERROR_MESSAGE);
       return;
    }
    String editorClassname = properties.getProperty(classname + ".editor");
    if (Sync.getTrace())
    	System.out.println("Nickname:" + nickname + ";Classname:" + classname+ ";Editor:" + editorClassname);
     instantiateClass (objName, classname, 
       		editorClassname, targetDir);
    /*
    if (editorClassname != null) {
       try {
         //new wordpad.WordPadEditor();
         //new syncprojectmanager.SyncPMObjectEditor();
         //new syncprojectmanager.SyncPMObjectEditor();
         //Class tryClass = Class.forName("syncprojectmanager.SyncPMObjectEditor");
          System.out.println("Abt to get class for name:" + editorClassname);
          Class theClass = Class.forName(editorClassname);
          System.out.println("Instantiated:" + editorClassname);
          SyncApplication app = (SyncApplication) theClass.newInstance();
		   apps.addElement(app);
          app.init(invoker);
          newObject = app.newObject(classname, objName);
       } catch (Exception e) {
          String[] messages = {"Error instantiating " + classname, e.toString()};
          e.printStackTrace();
          JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
          return;
       }
    } else {
       try {
          Class theClass = Class.forName(classname);
          newObject = (Replicated) theClass.newInstance();
       } catch (Exception e) {
          String[] messages = {"Error instantiating " + classname, e.getMessage()};
          JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
          return;
       }
    }

    //delegator = Sync.getDelegator(newObject);
    //System.out.println("In ObjectServerInterface: adding object " + newObject.getObjectID() + " to folder");
    if (newObject != null) {
    	//folderEditor.addObject(newObject, objName);
        if (!targetDir.containsKey(objName)) targetDir.put(objName, newObject);
    }
    else 
    	JOptionPane.showMessageDialog(myFrame, "Object returned from editor was null", "Instantiation Error", JOptionPane.ERROR_MESSAGE);
   */
   
   }
   public void instantiateClass (String objName, String nickname) {
   	/*
     Replicated newObject = null;
     //Object delegator = null;
     //String objName = folderEditor.uniqueName();
     String classname = properties.getProperty(nickname + ".class");
     //String delegatorClassName = null;
     if (classname == null) {
        JOptionPane.showMessageDialog(myFrame, "Could not find property naming class for " + nickname, "Error", JOptionPane.ERROR_MESSAGE);
        return;
     }
     String editorClassname = properties.getProperty(classname + ".editor");
     System.out.println("Nickname:" + nickname + ";Classname:" + classname+ ";Editor:" + editorClassname);
     if (editorClassname != null) {
        try {
          //new wordpad.WordPadEditor();
          //new syncprojectmanager.SyncPMObjectEditor();
          //new syncprojectmanager.SyncPMObjectEditor();
          //Class tryClass = Class.forName("syncprojectmanager.SyncPMObjectEditor");
           System.out.println("Abt to get class for name:" + editorClassname);
           Class theClass = Class.forName(editorClassname);
           System.out.println("Instantiated:" + editorClassname);
           SyncApplication app = (SyncApplication) theClass.newInstance();
		   apps.addElement(app);
           app.init(invoker);
           newObject = app.newObject(classname, objName);
        } catch (Exception e) {
           String[] messages = {"Error instantiating " + classname, e.toString()};
           e.printStackTrace();
           JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
           return;
        }
     } else {
        try {
           Class theClass = Class.forName(classname);
           newObject = (Replicated) theClass.newInstance();
        } catch (Exception e) {
           String[] messages = {"Error instantiating " + classname, e.getMessage()};
           JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
           return;
        }
     }

     //delegator = Sync.getDelegator(newObject);
     //System.out.println("In ObjectServerInterface: adding object " + newObject.getObjectID() + " to folder");
     if (newObject != null) folderEditor.addObject(newObject, objName);
     else JOptionPane.showMessageDialog(myFrame, "Object returned from editor was null", "Instantiation Error", JOptionPane.ERROR_MESSAGE);
     */
   	Folder targetDir = (Folder) folderEditor.getModel();
   	instantiateClass (objName, nickname, targetDir );
   	folderEditor.setModel(targetDir);
   	
   }
   
   public class NewObjectListener implements VirtualActionListener
   {
      String nickname;

      public NewObjectListener(String name)
      {
         nickname = name;
      }

      public void actionPerformed(VirtualActionEvent evt)
      {
         if (folderEditor.getModel() == null | folderEditor.getModel() == Folder.nullFolder) {
            JOptionPane.showMessageDialog(getJFrame(), "Please open a folder for this operation", "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         Replicated newObject = null;
         //Object delegator = null;
         String objName = folderEditor.uniqueName();
         instantiateClass(objName, nickname);
         /*
         String classname = properties.getProperty(nickname + ".class");
         //String delegatorClassName = null;
         if (classname == null) {
            JOptionPane.showMessageDialog(myFrame, "Could not find property naming class for " + nickname, "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         String editorClassname = properties.getProperty(classname + ".editor");
         System.out.println("Nickname:" + nickname + ";Classname:" + classname+ ";Editor:" + editorClassname);
         if (editorClassname != null) {
            try {
              //new wordpad.WordPadEditor();
              //new syncprojectmanager.SyncPMObjectEditor();
              //new syncprojectmanager.SyncPMObjectEditor();
              //Class tryClass = Class.forName("syncprojectmanager.SyncPMObjectEditor");
               System.out.println("Abt to get class for name:" + editorClassname);
               Class theClass = Class.forName(editorClassname);
               System.out.println("Instantiated:" + editorClassname);
               SyncApplication app = (SyncApplication) theClass.newInstance();
			   apps.addElement(app);
               app.init(invoker);
               newObject = app.newObject(classname, objName);
            } catch (Exception e) {
               String[] messages = {"Error instantiating " + classname, e.toString()};
               e.printStackTrace();
               JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
               return;
            }
         } else {
            try {
               Class theClass = Class.forName(classname);
               newObject = (Replicated) theClass.newInstance();
            } catch (Exception e) {
               String[] messages = {"Error instantiating " + classname, e.getMessage()};
               JOptionPane.showMessageDialog(myFrame, messages, "Instantiation Error", JOptionPane.ERROR_MESSAGE);
               return;
            }
         }

         //delegator = Sync.getDelegator(newObject);
         //System.out.println("In ObjectServerInterface: adding object " + newObject.getObjectID() + " to folder");
         if (newObject != null) folderEditor.addObject(newObject, objName);
         else JOptionPane.showMessageDialog(myFrame, "Object returned from editor was null", "Instantiation Error", JOptionPane.ERROR_MESSAGE);
      */
      }
      
   }

   public class FolderEditMenu extends JPopupMenu
   {
      public FolderEditMenu()
      {
         super();

         VirtualMenuItem cut = MenuItemSelector.createMenuItem("Cut");
         cut.addActionListener(new VirtualActionListener() {
            public void actionPerformed(VirtualActionEvent e) {
               folderEditor.cut();}});
         add((JMenuItem) cut.getPhysicalComponent());

         VirtualMenuItem copy = MenuItemSelector.createMenuItem("Copy");
         copy.addActionListener(new VirtualActionListener() {
            public void actionPerformed(VirtualActionEvent e) {
               folderEditor.copy();}});
         add((JMenuItem)copy.getPhysicalComponent());

         VirtualMenuItem paste = MenuItemSelector.createMenuItem("Paste");
         paste.addActionListener(new VirtualActionListener() {
            public void actionPerformed(VirtualActionEvent e) {
               folderEditor.paste();}});
         add((JMenuItem) paste.getPhysicalComponent());

         VirtualMenuItem delete = MenuItemSelector.createMenuItem("Delete");
         delete.addActionListener(new VirtualActionListener() {
            public void actionPerformed(VirtualActionEvent e) {
               folderEditor.delete();}});
         add((JMenuItem) delete.getPhysicalComponent());
      }
   }

   public class FolderMouseAdapter extends MouseAdapter
   {
      public void mouseClicked(MouseEvent evt) {
         int index = folderEditor.getSelectedRow();
         if (index == -1) return;
         String name = (String) folderEditor.getValueAt(index, 0);
         Folder folder = (Folder) folderEditor.getModel();
         Replicated value = folder.get(name);
         System.out.println("Value to open is " + value.getObjectID());
         if (evt.getClickCount() == 2) {
            if (value instanceof Folder) {
               folderEditor.setModel((Folder) value);
            } else {
               openObject(value, name);
            }
         } else if (evt.getClickCount() == 1 & SwingUtilities.isRightMouseButton(evt)) {
            folderEditMenu.show(folderEditor, evt.getX(), evt.getY());
            // evt.isPopupTrigger() does not seem to work but would be preferrable
         }
      }
   }

   void openObject(Replicated obj, String name)
   {
      if (obj == null) throw new NullPointerException("Null argument to openObject");
      Object delegator = Sync.getDelegator(obj);
      String classname;
      if (delegator == null)
        classname = obj.getClass().getName();
      else
        classname = delegator.getClass().getName();
      SyncClient client = getClient();
      //String classname = obj.getClass().getName();
      String editorClassname = null;
      if (properties != null)
          editorClassname = properties.getProperty(classname + ".editor");
      if (editorClassname == null && client != null)
			editorClassname = client.getEditorClass();   
     
      if (editorClassname == null) {
         //JOptionPane.showMessageDialog(myFrame, "Could not find property naming class for " + classname, "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      try {
		 //System.out.println("0000 " + editorClassname);
    	  SyncApplication app = getEditor(editorClassname);
    	  /*
         Class editorClass = Class.forName(editorClassname);
		 //System.out.println("0000");
         SyncApplication app = (SyncApplication) editorClass.newInstance();
         */
		 //System.out.println("0000");
         if (obj instanceof Delegated) {
        	 Delegated delegated = (Delegated) obj;
        	 //uiBean.invokeInitSerializedObject(delegated.returnObject());
        	 //delegated.registerAsListener();
        	 System.out.println ("Opening " + delegated.returnObject() + " replciated " + delegated + " id " + obj.getObjectID());
         }
         
		 apps.addElement(app);
		 //System.out.println("0000");
         app.init(invoker);
         //System.out.println("Passing object " + obj.getObjectID() + " to application");
         addObject (obj, name, app);
         //app.addObject(obj, name);
      } catch (Exception e) {
         String[] messages = {"Error instantiating \"" + classname + "\":", e.getMessage()};
		 System.out.println(e);
		 e.printStackTrace();
         JOptionPane.showMessageDialog(getJFrame(), messages, "Sync Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   public void doRefresh()
   {
	   SyncApplication app;
	   for (Enumeration e=apps.elements(); e.hasMoreElements();)
	   {
		   app = (SyncApplication)e.nextElement();
		   try
		   {
			   /*
			   if (app instanceof RT_SyncApplication){
				   ((RT_SyncApplication)app).doRefresh();
			   }
			   */
			   app.doRefresh();
		   }
		   catch (Exception ex)
		   {
		   }
	   }
   }

   class ObjectTreeListener extends MouseAdapter
   {
      public void mouseClicked(MouseEvent e)
      {
         TreePath selPath = (TreePath) objectTree.getPathForLocation(e.getX(), e.getY());
         if(selPath != null) {
            Object selected = selPath.getLastPathComponent();
            if (selected instanceof FolderTreeNode) {  // local node
               Folder folder = ((FolderTreeNode) selected).getFolder();
               folderEditor.setModel(folder);
            } else {  // remote node, set editor pane to empty directory
               folderEditor.setModel(Folder.nullFolder);
            }
	      }
	   }
	}
}