package edu.unc.sync.server;

import edu.unc.sync.*;
import java.util.*;
import java.io.*;

// This class stores the history of changes to an object as a
// vector of change sets.  The vector is indexed approximately
// by object version number, as follows: index i is the change set
// that took the object from version i to version i + 1.  Thus
// the index of the last element in the vector is one less than
// the current object version number.
// To permit the use of this indexing scheme without requiring
// the vector to store all change sets from version 0, the class
// keeps a "lowest" index which is the index of earliest change
// set stored.  The setLowest method "garbage collects" all
// change sets earlier than the specified index without changing
// how later change sets are indexed.

public class ChangeVector implements Serializable
{
   Vector v;
   int lowest;

   public ChangeVector()
   {
      this(0);
   }

   public ChangeVector(int init_lowest)
   {
      v = new Vector(50);
      lowest = init_lowest;
      //System.out.println("Change vector initialized with lowest = " + lowest);
   }

   public void addElement(Change c)
   {
      v.addElement(c);
   }

   public Change unionChangeSets(Replicated obj, int version) throws ReplicationException
   {
      // precondition: version >= lowest
      if (version < lowest) throw new ReplicationException("Changes below version " + String.valueOf(lowest) + " not kept.");
      int start_index = version - lowest;
      int end_index = v.size();
      Change base = ((Change) v.elementAt(start_index)).copy();
      for (int i = start_index + 1; i < v.size(); i++) {
         base = obj.concatChanges(base, (Change) v.elementAt(i));
      }
      return base;
   }

   public void setLowest(int new_lowest)
   {
      for (int i = 0; i < new_lowest - lowest; i++) v.removeElementAt(i);
      lowest = new_lowest;
   }

   public int getLowest()
   {
      return lowest;
   }
}