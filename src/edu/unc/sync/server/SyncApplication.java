package edu.unc.sync.server;

import edu.unc.sync.Replicated;

public interface SyncApplication
{
   public void init(Object invoker); // invoker is sync client or server
   //public Replicated newObject(String classname, String name);
   public void addObject(Replicated object, String name); // replicated is what model gets converted to
   public void addObject(Object object, String name);
   public void doRefresh(); // Sync calls this whenever it updates model in case app does not listen to events
}
