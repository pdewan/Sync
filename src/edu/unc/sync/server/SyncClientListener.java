package edu.unc.sync.server;

import edu.unc.sync.Replicated;

public interface SyncClientListener {
	public void synchronize(RemoteSyncServer server, SyncClient client, Replicated object, edu.unc.sync.Change change);

}
