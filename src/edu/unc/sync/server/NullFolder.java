package edu.unc.sync.server;

import edu.unc.sync.*;
import java.io.*;
import java.util.*;
//import com.sun.java.swing.tree.*;
//import com.sun.java.swing.table.*;
//import com.sun.java.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;
import edu.unc.sync.server.SyncTreeModel;

public class NullFolder implements TableModel {
	  String[] attr_names;
	  public NullFolder (String[] the_attr_names) {
	  	attr_names = the_attr_names;
	  }
      public int getRowCount() {return 0;}
      public int getColumnCount() {return attr_names.length;}
      public String getColumnName(int columnIndex) {return attr_names[columnIndex];}
      public Class getColumnClass(int columnIndex) {return String.class;}
      public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
      public Object getValueAt(int rowIndex, int columnIndex) {return null;}
      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
      public void addTableModelListener(TableModelListener l) {}
      public void removeTableModelListener(TableModelListener l) {}
   
}
