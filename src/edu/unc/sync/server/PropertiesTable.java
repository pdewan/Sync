package edu.unc.sync.server;

import java.io.*;
import java.util.*;
//import com.sun.java.swing.*;
//import com.sun.java.swing.table.*;
//import com.sun.java.swing.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

/**
* The names column in this table is not set until the properties are loaded.
*/
public class PropertiesTable extends Properties implements TableModel
{
   String[] headers = {"Property", "Value"};
   SortedStringVector names;  // keys (assumed to be strings) sorted lexically
   File propsFile = null;
   TableModelListener table_listener;
   String descr = "Properties table";

   public PropertiesTable()
   {
      super();
      names = new SortedStringVector();
   }
   
   public PropertiesTable(File file) throws FileNotFoundException, IOException
   {
      super();
      propsFile = file;
      names = new SortedStringVector();
      load(new FileInputStream(file));
   }
   
   protected void setFile(File file)
   {
      propsFile = file;
   }
   
   protected File getFile()
   {
      return propsFile;
   }
   
   protected boolean hasFile()
   {
      return propsFile != null;
   }
   
   protected void load() throws IOException, FileNotFoundException
   {
      super.load(new FileInputStream(propsFile));
   }
   
   protected void reload() throws IOException, FileNotFoundException
   {
      if (propsFile == null) return;
      clear();
      names.removeAllElements();
      load();
   }
   
   protected synchronized void save() throws IOException, FileNotFoundException
   {
      if (propsFile == null) return;
      super.save(new FileOutputStream(propsFile), descr);
   }
   
   protected String getPropertyName(int index)
   {
      if (index < 0 | index >= names.size()) return null;
      else return (String) names.elementAt(index);
   }
   
   public Object put(Object key, Object value)
   {
      Object replaced = super.put(key, value);
      if (!(key instanceof String)) return replaced;
      int table_index = names.indexOf(key);
      if (replaced == null) {
         names.add((String) key);
         if (table_listener != null) table_listener.tableChanged(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
      } else {
         if (table_listener != null) table_listener.tableChanged(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
      }
      return replaced;
   }

   public Object remove(Object key)
   {
      if (!(key instanceof String)) return super.remove(key);
      int table_index = names.indexOf(key);
      Object value = super.remove(key);
      if (getProperty((String) key) == null) {  // may return default value
         names.remove((String) key);
         if (table_listener != null) table_listener.tableChanged(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
      } else {
         if (table_listener != null) table_listener.tableChanged(new TableModelEvent(this, table_index, table_index, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
      }
      return value;
   }

   public int getRowCount()
   {
      return names.size();
   }

   public int getColumnCount()
   {
      return headers.length;
   }

   public String getColumnName(int columnIndex)
   {
      return headers[columnIndex];
   }

   public Class getColumnClass(int columnIndex)
   {
      return String.class;
   }

   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return false;
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      String name = (String) names.elementAt(rowIndex);
      String property = getProperty(name);
      switch (columnIndex) {
         case 0: return name;
         case 1: return property;
         default: return null;
      }
   }

   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
   {
   }

   public void addTableModelListener(TableModelListener l)
   {
      table_listener = l;
   }

   public void removeTableModelListener(TableModelListener l)
   {
      table_listener = null; 
   }
}
