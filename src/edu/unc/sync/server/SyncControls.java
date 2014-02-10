package edu.unc.sync.server;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
public class SyncControls extends Observable implements Observer {
	PropertyChangeSupport propertyChange = new PropertyChangeSupport(this) ;
	ServerProxy server;
	public SyncControls (ServerProxy theServer) {
		server = theServer;
		server.addObserver(this);
	}	
	public boolean getRealTimeSynchronize() {
		return server.getRealTimeSynchronize();
	}
	public void setRealTimeSynchronize(boolean newVal) {
		server.setRealTimeSynchronize(newVal);
	}
	public Vector getCurrentCollaborators() {		
		return server.getOtherClientNames();
	}	
	public void synchronize() {
		server.synchronize();
	}
	 public void addPropertyChangeListener(PropertyChangeListener l) {        	
         propertyChange.addPropertyChangeListener(l);
     }
	 public void notifyObservers (Object arg) {
			setChanged();
			super.notifyObservers(arg);			
	 }
	 public void update (Observable observable, Object event ) {
		 notifyObservers(event);
	 }
}
