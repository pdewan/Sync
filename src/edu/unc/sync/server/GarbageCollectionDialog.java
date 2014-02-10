package edu.unc.sync.server;

import edu.unc.sync.*;

import java.awt.BorderLayout;
import java.awt.event.*;
//import com.sun.java.swing.*;
import javax.swing.*;

import bus.uigen.ars.*;

public class GarbageCollectionDialog extends JDialog
{
   SyncObjectServer objectServer;
   public GarbageCollectionDialog(JFrame owner, SyncObjectServer server)
   {
      super(owner, "Object Server Garbage Collection", true);
      objectServer = server;
      
      JButton firstLevel = new JButton("Do First Level Collection");
      firstLevel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            firstLevelAction();}});
      getContentPane().add(firstLevel, BorderLayout.NORTH);

      JButton secondLevel = new JButton("Do Second Level Collection");
      secondLevel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            secondLevelAction();}});
      getContentPane().add(secondLevel, BorderLayout.CENTER);

      JButton close = new JButton("Close");
      close.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            closeAction();}});
      JButton help = new JButton("Help");
      help.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            helpAction();}});
      JPanel bottom_panel = new JPanel();
      bottom_panel.add(help);
      bottom_panel.add(close);
      getContentPane().add(bottom_panel, BorderLayout.SOUTH);
      pack();
      setVisible(true);
      //setSize(400, 200);
   }
   
   void firstLevelAction()
   {
      Object[] options = { "OK", "CANCEL" };
      String[] warnmsg = {"If you have open applications with unsaved data",
      "this action may result in their loss"};
      int choice = JOptionPane.showOptionDialog(this, warnmsg, "Warning", 
      JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
      if (choice != 0) return;
      int gcCount = objectServer.firstLevelGarbageCollection();
      String[] msg = {gcCount + " orphan objects removed."};
      JOptionPane.showMessageDialog(this, msg, "First level garbage collection", JOptionPane.INFORMATION_MESSAGE);
   }
   
   void secondLevelAction()
   {
      int gcCount = objectServer.secondLevelGarbageCollection();
      String[] msg = {gcCount + " persistent storage files deleted."};
      JOptionPane.showMessageDialog(this, msg, "Second level garbage collection", JOptionPane.INFORMATION_MESSAGE);
   }
   
   void closeAction()
   {
      dispose();
   }
   
   void helpAction()
   {
      String[] text = new String[] {
         "First level garbage collection removes all orphan objects from the",
         "object server's internal table.  These objects may still be reclaimed",
         "from their corresponding persistent storage.  This will be done automatically",
         "if they are referenced (by their object identifier).",
         "",
         "Second level garbage collection deletes all persistent storage files of objects",
         "that no longer exist in the object server's internal table.  These objects",
         "are gone forever on this client."};
      JOptionPane.showMessageDialog(this, text, "Garbage collection help", JOptionPane.INFORMATION_MESSAGE);
   }
}
