package edu.unc.sync;

/**
* Defines the interface for merge action objects. The compute() method of a MergeAction
* object takes the server's changes and the client's changes, and decides which
* changes of the server's are to be applied to the client, and which changes of the
* client are to be applied to the server.
*
* A merge action also has a <i>conflicting</i> property which sets the <i>conflicting</i>
* properties on the changes that it returns. This is generally a static property of the
* merge action but may also be computed when the compute() method is invoked.
*
* @see edu.unc.sync.Change
* @author Jon Munson
*/
public interface MergeAction
{
   /**
   * The method that does the work.
   *
   * @param ch0 the change made at the server.
   * @param chr the change made at the remote client.
   * @return a pair of changes: the first to be applied to the server site,
   * the second to be applied to the remote client.
   * @exception edu.unc.sync.ReplicationException
   * if the computation fails for some Sync-related reason.
   */
   public ChangePair compute(Change ch0, Change chr) throws ReplicationException;

   /**
   * Sets the <code>conflicting</code> property.  The <code>conflicting</code> property
   * of each change returned in
   * the change pair returned by <code>compute</code> is set to the value
   * of this property.
   *
   * @see edu.unc.sync.Change#getConflicting()
   */
   public void setConflicting(boolean conf);

   /**
   * Returns the <code>conflicting</code> property.
   *
   * @see edu.unc.sync.AbstractMergeAction#setConflicting
   */
   public boolean getConflicting();

   /**
   * Returns a brief description of the merge action, for use in
   * providing to users in a user interface.
   */
   public String description();
}
