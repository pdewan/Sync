package edu.unc.sync.server;

import bus.uigen.uiFrame;
import bus.uigen.undo.ListeningUndoer;
import edu.unc.sync.Replicated;

public interface UndoingSyncApplication extends RT_SyncApplication
{
	public ListeningUndoer getUndoer() ;
	public void setListentingUndoer(ListeningUndoer  theUndoer);
	public ListeningUndoer getUndoer(Object o) ;
	public void setListentingUndoer(Object o, ListeningUndoer  theUndoer);
}
