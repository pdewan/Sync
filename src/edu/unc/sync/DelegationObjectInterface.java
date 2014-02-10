package edu.unc.sync;

import java.beans.PropertyChangeListener;

public interface DelegationObjectInterface extends DelegationInterface
{
	public void addPropertyChangeListener(PropertyChangeListener l);
}
