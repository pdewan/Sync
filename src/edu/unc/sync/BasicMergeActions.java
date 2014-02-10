package edu.unc.sync;

import edu.unc.sync.server.SyncException;

/**
* The class that defines the basic merge actions. Each basic action is defined
* as a static inner class, which allows it to be referenced statically,
* without a reference to an instance of BasicMergeActions.  BasicMergeActions
* is basically a collector of separate classes.

* (Due to a bug in the 1.1.X versions of javadoc, documentation for the inner classes
* is not generated. This bug has reportedly been fixed for JDK 1.2.)
*
* @author Jon Munson
*/
public class BasicMergeActions
{
   private BasicMergeActions() {}

   /**
   * BothAction accepts both changes&#151;the server's are given to the client, and
   * the client's are given to the server.
   */
   public static class BothAction extends AbstractMergeAction
   {
      /**
      * Creates a new BothAction with <var>Conflicting</var> false.
      */
      public BothAction() {conflicting = false;}

      /**
      * Creates a new BothAction with <var>Conflicting</var> set to <code>conf</code>.
      */
      public BothAction(boolean conf) {conflicting = conf;}

      /**
      * Gives the client's operation to the server and the server's operation to
      * to the client.
      *
      * @param ch0 the change at the server replica
      * @param chr the change at the client (remote) replica
      * @return the pair [chr, ch0].
      */
      public ChangePair compute(Change ch0, Change chr)
      {
         return new ChangePair(chr, ch0, conflicting);
      }

      public String description()
      {
         return "Accept both changes, with "+(conflicting ? "conflict" : "no conflict");
      }
   }

   /**
   * The ColumnAction merge action returns only the server's operation (the column one)
   * to the client; the client's operation is not returned to the server.
   */
   public static class ColumnAction extends AbstractMergeAction
   {
      /**
      * Creates a new ColumnAction with <var>Conflicting</var> false.
      */
      public ColumnAction() {conflicting = false;}

      /**
      * Creates a new ColumnAction with <var>Conflicting</var> set to <code>conf</code>.
      */
      public ColumnAction(boolean conf) {conflicting = conf;}

      /**
      * Returns ch0 to be applied to the client, drops chr.
      *
      * @param ch0 the change at the server replica
      * @param chr the change at the client (remote) replica
      * @return the pair [null, ch0].
      */
      public ChangePair compute(Change ch0, Change chr)
      {
		  //System.out.println("in columnaction.compute, chr == null " + (chr == null));
		  if ((chr == null) || (chr.getObjectID() == null)){
			  //|| (!(ch0 instanceof ChangeSet)) || (!(chr instanceof ChangeSet))){
			   return new ChangePair(null, ch0, conflicting);
		  }
		  else{
			  Change clientCh = chr.getInverse();
			  //System.out.println("in columnaction.compute, chr.getClass() = " + chr.getClass());
			  //System.out.println(chr.getObjectID());
			  Replicated repl = Sync.getObject(chr.getObjectID());
			  try{
				  if ((null == clientCh)|| (clientCh instanceof NullChange)||
					  (clientCh instanceof NullElementChange) || (clientCh instanceof NullSet)){
					  clientCh = ch0;
				  }
				  else{
					  if (!((null == ch0)||(ch0 instanceof NullChange) ||
							(ch0 instanceof NullElementChange) || (ch0 instanceof NullSet))){
						  clientCh = repl.concatChanges(clientCh, ch0);
					  }
				  }
			  }
			  catch(Exception ex){
				  System.out.println(ex);
				  ex.printStackTrace();
			  }
			  //return new ChangePair(null, ch0, conflicting);
			  return new ChangePair(null, clientCh, conflicting);
		  }

      }

      public String description()
      {
         return "Accept column change, with "+(conflicting ? "conflict" : "no conflict");
      }
   }

   /**
   * The RowAction merge action returns only the client's operation (the row one)
   * to the client; the server's operation is not returned to the client.
   */
   public static class RowAction extends AbstractMergeAction
   {
      /**
      * Creates a new RowAction with <var>Conflicting</var> false.
      */
      public RowAction() {conflicting = false;}

      /**
      * Creates a new RowAction with <var>Conflicting</var> set to <code>conf</code>.
      */
      public RowAction(boolean conf) {conflicting = conf;}

      /**
      * Returns chr to be applied to the server, drops ch0.
      *
      * @param ch0 the change at the server replica
      * @param chr the change at the client (remote) replica
      * @return the pair [chr, null].
      */
      public ChangePair compute(Change ch0, Change chr)
      {
         return new ChangePair(chr, null, conflicting);
      }

      public String description()
      {
         return "Accept row change, with "+(conflicting ? "conflict" : "no conflict");
      }
   }

   /**
   * The NeitherAction merge action may be used when both sites have performed the same
   * operation concurrently, and thus neither site's operation needs to be applied at
   * the other. In this case, <var>Conflicting</var> should be set false.
   *
   * The NeitherAction merge action may also be used when both site's operations should
   * be nullified.  In this case, <var>Conflicting</var> should be set true.
   */
   public static class NeitherAction extends AbstractMergeAction
   {
      /**
      * Creates a new NeitherAction with <var>Conflicting</var> false.
      */
      public NeitherAction() {conflicting = false;}

      /**
      * Creates a new NeitherAction with <var>Conflicting</var> set to <code>conf</code>.
      */
      public NeitherAction(boolean conf) {conflicting = conf;}

      /**
      * Drops both changes.
      *
      * @param ch0 the change at the server replica
      * @param chr the change at the client (remote) replica
      * @return the pair [null, null].
      */
      public ChangePair compute(Change ch0, Change chr) throws ReplicationException
      {
         return new ChangePair(null, null, conflicting);
      }

      public String description()
      {
         return "Accept neither change, with "+(conflicting ? "conflict" : "no conflict");
      }
   }

   public static class DoMergeAction extends AbstractMergeAction
   {
      /**
      * Creates a new DoMergeAction. The <code>compute</code> method calls the
      * <code>mergeChanges</code> method on the modified element of this collection.
      * The <var>Conflicting</var> property of the changes
      * returned by the <code>compute</code> method is set according to the
      * <var>Conflicting</var> property of the changes returned by mergeChanges.
      */
      public DoMergeAction() {conflicting = false;}

      /**
      * Calls mergeChanges on the element of the collection identified by the
      * Change object's identifier() method. This method may return an object ID, but
      * may also return a field name or a hashtable key.
      *
      * @param ech0 the change at the server replica.
      * @param echr the change at the client (remote) replica.
      * @return a pair of changes that are the result of calling mergeChanges on
      * the merged changes to the collection element.
      */
      public ChangePair compute(Change ech0, Change echr) throws ReplicationException
      {
         ObjectID objectID, elementID;
         Object chID;
         //ReplicatedCollection obj = null;
         Replicated element;
         Change ch0, chr;

         // get reference to the object modified by these changes, but one of
         // the changes may be a NullElementChange, which does not identify the element
         if (ech0 instanceof NullElementChange) {
            ch0 = new NullChange();
            chr = ((ModifyChange) echr).change;
            objectID = echr.getObjectID();
            elementID = chr.getObjectID();
            chID = ((ElementChange) echr).identifier();
         }
         else if (echr instanceof NullElementChange) {
            ch0 = ((ModifyChange) ech0).change;
            chr = new NullChange();
            objectID = ech0.getObjectID();
            elementID = ch0.getObjectID();
            chID = ((ElementChange) ech0).identifier();
         }
         else {
            ch0 = ((ModifyChange) ech0).change;
            chr = ((ModifyChange) echr).change;
            objectID = ech0.getObjectID();
            elementID = ch0.getObjectID();
            chID = ((ElementChange) ech0).identifier();
         }
         try {
            element = Sync.getObject(elementID);
         } catch (SyncException ex) {
            throw new ReplicationException("Error in DoMergeAction: " + ex.getMessage());
         }

         ChangePair mergedChanges = element.mergeChanges(ch0, chr);

         ElementChange to0, tor;
         if (mergedChanges.central == null)
            to0 = null;
         else
            to0 = new ModifyChange(objectID, chID, mergedChanges.central);
         if (mergedChanges.remote == null)
            tor = null;
         else
            tor = new ModifyChange(objectID, chID, mergedChanges.remote);

         return new ChangePair(to0, tor, mergedChanges.getConflicting());
      }

      public String description()
      {
         return "Merge changes";
      }
   }
}