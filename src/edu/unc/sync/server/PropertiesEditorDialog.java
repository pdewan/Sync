package edu.unc.sync.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
//import com.sun.java.swing.*;
import javax.swing.*;

import bus.uigen.ars.*;

public class PropertiesEditorDialog extends JDialog
{
   PropertiesTable properties;
   JTable propsTable;
   JTextField nameField, valueField;
   
   public PropertiesEditorDialog(JFrame frame, PropertiesTable props)
   {
      super(frame, "Properties Editor", true);
      properties = props;
      propsTable = new JTable(props);
      propsTable.setShowGrid(false);
      propsTable.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            setFieldsAction();}});

      getContentPane().add(JTable.createScrollPaneForTable(propsTable), BorderLayout.CENTER);
      JPanel buttons = new JPanel();
      
      JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      namePanel.add(new JLabel("Name"));
      namePanel.add(nameField = new JTextField(40));
      
      JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      valuePanel.add(new JLabel("Value"));
      valuePanel.add(valueField = new JTextField(40));
/*
      JPanel centerPanel = new JPanel(new BorderLayout());
      centerPanel.add(namePanel, BorderLayout.NORTH);
      centerPanel.add(valuePanel, BorderLayout.SOUTH);

      getContentPane().add(centerPanel, BorderLayout.CENTER);
*/      
      JButton set = new JButton("Set");
      set.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            setAction();}});
      buttons.add(set);

      JButton delete = new JButton("Delete");
      delete.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            deleteAction();}});
      buttons.add(delete);

      JButton okay = new JButton("Okay");
      okay.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            okayAction();}});
      buttons.add(okay);

      JButton cancel = new JButton("Cancel");
      cancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            cancelAction();}});
      buttons.add(cancel);
      
      JPanel southPanel = new JPanel(new GridLayout(3, 1));
      southPanel.add(namePanel);
      southPanel.add(valuePanel);
      southPanel.add(buttons);
      
      getContentPane().add(southPanel, BorderLayout.SOUTH);
      pack();
      Dimension size = getSize();
      setSize(new Dimension(size.width, 250));
      setVisible(true);
   }
   
   void setFieldsAction()
   {
      String name = properties.getPropertyName(propsTable.getSelectedRow());
      if (name == null) return;
      String value = properties.getProperty(name);
      nameField.setText(name);
      valueField.setText(value);
   }
   
   void setAction()
   {
      String name = nameField.getText();
      String value = valueField.getText();
      properties.put(name, value);
   }
   
   void deleteAction()
   {
      String name = nameField.getText();
      properties.remove(name);
   }
   
   void okayAction()
   {
      if (properties.hasFile()) try {
         properties.save();
      } catch (Exception e) {
         System.err.println("Error writing properties file: " + e);
      }
      dispose();
   }
   
   void cancelAction()
   {
      if (properties.hasFile()) try {
         properties.reload();
      } catch (Exception e) {
         System.err.println("Error reloading properties file: " + e);
      }
      dispose();
   }
}