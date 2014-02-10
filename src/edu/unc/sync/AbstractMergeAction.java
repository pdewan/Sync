package edu.unc.sync;

/**
* A base class that implements MergeAction and implements the <var>Conflicting</var>
* property. Used by all basic merge actions.
*
* @see MergeAction
* @see BasicMergeActions
* @author Jon Munson
*/
public abstract class AbstractMergeAction implements MergeAction
{
   /**
   * Implementation of <var>Conflicting</var> property.
   */
   protected boolean conflicting;
   
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
   public abstract ChangePair compute(Change ch0, Change chr) throws ReplicationException;
   
   /**
   * Sets the <var>Conflicting</var> property.  The <var>Conflicting</var> property
   * of each change returned in
   * the change pair returned by <code>compute</code> is set to the value
   * of this property.
   *
   * @see edu.unc.sync.Change#getConflicting()
   */
   public void setConflicting(boolean conf) {conflicting = conf;}
   
   /**
   * Returns the <var>Conflicting</var> property.
   *
   * @see edu.unc.sync.AbstractMergeAction#setConflicting
   */
   public boolean getConflicting() {return conflicting;}
   
   /**
   * Returns a brief description of the merge action, for use in
   * providing to users in a user interface.
   */
   public abstract String description();
}
