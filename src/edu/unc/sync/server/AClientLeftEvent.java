/*
 * Created on Feb 23, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.unc.sync.server;

/**
 * @author dewan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AClientLeftEvent implements ClientEvent {
	String clientName;
	public  AClientLeftEvent (String theClientName) {
		clientName = theClientName;		
	}
	public String getClientName() {
		return clientName;
	}

}
