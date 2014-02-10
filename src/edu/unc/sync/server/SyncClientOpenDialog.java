package edu.unc.sync.server;

import edu.unc.sync.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.*;
//import com.sun.java.swing.*;
import javax.swing.*;

import bus.uigen.ars.*;

public class SyncClientOpenDialog extends JDialog
{
   public static int OPEN = 1;
   public static int SAVE = 2;
   
   int mode;
   JPanel top_panel;
   JLabel showing;
   StringBuffer showing_buf;
   JButton up, down, okay, cancel;
   JTextField save_field; 
   FolderEditor dirEditor;
   Replicated selected;
   boolean canceled;
   
   // initialize with 
   public SyncClientOpenDialog(JFrame owner, int mode)
   {
      super(owner, "Open Sync Object", true);
      this.mode = mode;
      Container cont = getContentPane();

      showing_buf = new StringBuffer(100);
      showing_buf.append("Showing: " + java.io.File.separator);
      showing = new JLabel(showing_buf.toString());
      up = new JButton("Up");
      up.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            upAction();}});

      top_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      top_panel.add(up);
      top_panel.add(showing);
      cont.add(top_panel, BorderLayout.NORTH);
   
      Folder dir = (Folder) Sync.getObjectServer().getRootObject();
      dirEditor = new FolderEditor(dir, FolderEditor.VIEW);
      dirEditor.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) downAction();}});
      JScrollPane sp = JTable.createScrollPaneForTable(dirEditor);
      sp.setBackground(Color.white);
      cont.add(sp, BorderLayout.CENTER);
      
      okay = new JButton("OK");
      okay.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            okayAction();}});
      cancel = new JButton("Cancel");
      cancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            cancelAction();}});
      JPanel bottom_panel = new JPanel();
      bottom_panel.add(okay);
      bottom_panel.add(cancel);

      if (mode == SAVE) {
         JPanel save_panel = new JPanel(new BorderLayout());
         save_panel.add(new JLabel("Name:"), BorderLayout.WEST);
         save_field = new JTextField();
         save_panel.add(save_field, BorderLayout.CENTER);
         save_panel.add(bottom_panel, BorderLayout.SOUTH);
         cont.add(save_panel, BorderLayout.SOUTH);
      } else {
         cont.add(bottom_panel, BorderLayout.SOUTH);
      }
      pack();
      setSize(400, 200);
   }
   
   void upAction()
   {
      Folder new_dir = (Folder) ((Replicated) dirEditor.getModel()).getParent();
      if (new_dir != null) {
         selected = null;
         dirEditor.setModel(new_dir);
         // find last '/' or '\' before one at end
         int cutoff = showing_buf.toString().lastIndexOf(java.io.File.separatorChar, showing_buf.length() - 2);
         showing_buf.setLength(cutoff + 1);
         showing.setText(showing_buf.toString());
         top_panel.validate();
      }
   }
   
   void downAction()
   {
      int index = dirEditor.getSelectedRow();
      if (index == -1) return;
      String name = (String) dirEditor.getValueAt(index, 0);
      Folder dir = (Folder) dirEditor.getModel();
      Replicated value = (Replicated) dir.get(name);
      if (value instanceof Folder) {
         selected = null;
         dirEditor.setModel((Folder) value);
         showing_buf.append(name);
         showing_buf.append(java.io.File.separator);
         showing.setText(showing_buf.toString());
         top_panel.validate();
      } else if (value instanceof ReplicatedReference) {
         ObjectID selectedID = ((ReplicatedReference) value).getReferencedID();
         selected = Sync.getObject(selectedID);
         okayAction();
      } else {
         selected = value;
         okayAction();
      }
   }
   
   void okayAction()
   {
      if (mode == OPEN && getSelectedObject() == null) {
         JOptionPane.showMessageDialog(this, new String[] {"Please select an object, or Cancel"}, "Open Error", JOptionPane.ERROR_MESSAGE);
      } else if (mode == SAVE && getObjectName() == null) {
         JOptionPane.showMessageDialog(this, new String[] {"Please enter a name, or Cancel"}, "Save Error", JOptionPane.ERROR_MESSAGE);
      } else {
         canceled = false;
         dispose();
      }
   }
   
   void cancelAction()
   {
      canceled = true;
      dispose();
   }
   
   public boolean wasCanceled()
   {
      return canceled;
   }
   
   public Replicated getSelectedObject()
   {
      if (selected != null) {
         return selected;
      } else {
         int index = dirEditor.getSelectedRow();
         if (index == -1) return null;
         String name = (String) dirEditor.getValueAt(index, 0);
         Folder dir = (Folder) dirEditor.getModel();
         return (Replicated) dir.get(name);
      }
   }
   
   public Folder getFolder()
   {
      return (Folder) dirEditor.getModel();
   }
   
   public String getObjectName()
   {
      if (mode == SAVE) {
         String name = save_field.getText();
         return (name != null && name.length() != 0) ? name : null;
      } else {
         int index = dirEditor.getSelectedRow();
         if (index == -1) return null;
         return (String) dirEditor.getValueAt(index, 0);
      }
   }
}
