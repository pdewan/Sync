package edu.unc.sync;

/**
* A ChangePair is a pair of changes, with one tagged as belonging to the
* central, or server, site, and the other tagged as belonging to the remote,
* or client site.  A third field indicates whether these two changes were
* determined to be in conflict with each other in the merge process.
*/
public class ChangePair
{
   /**
   * The change for the central, or server, site.
   */
   public Change central;

   /**
   * The change for the remote, or client, site.
   */
   public Change remote;
   
   /**
   * A boolean which indicates whether the changes were found to be in
   * conflict with each other.  Corresponds to the <var>conflicting</var>
   * property of edu.unc.sync.Change and edu.unc.sync.MergeAction.
   *
   * @see edu.unc.sync.Change
   * @see edu.unc.sync.MergeAction
   */
   private boolean conflicting;

   /**
   * Constructs a pair of changes.
   */
   public ChangePair(Change c, Change r, boolean conf)
   {
      central = c;
      if (central != null) central.setConflicting(conf);
      remote = r;
      if (remote != null) remote.setConflicting(conf);
      conflicting = conf;
   }
   
   /**
   * Returns the <var>Conflicting</var> property of this pair of changes.
   *
   * @return <b>true</b> iff the changes conflict with each other as determined
   * by the merge process.
   */
   public boolean getConflicting() {return conflicting;}
   
   /**
   * Sets the <var>Conflicting</var> property of this pair of changes.
   *
   * @param conf A boolean to which the <var>Conflicting</var> property 
   * of this pair of changes will be set.
   */
   public void setConflicting(boolean conf) {conflicting = conf;}

   /**
   * Supplies some information about the change pair, for debugging.
   */
   public String toString()
   {
      String cop = (central == null ? "0" : Integer.toString(central.opcode()));
      String rop = (remote == null ? "0" : Integer.toString(remote.opcode()));
      return "central:"+cop+", remote:"+rop;
   }
}
