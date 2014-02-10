package edu.unc.sync;

import java.util.*;

import util.models.VectorMethodsListener;

public interface DelegationVectorInterface extends DelegationInterface
{
	public void addVectorMethodsListener(VectorMethodsListener l);
	public void addElement(Object c); 
	public void insertElementAt(Object element, int pos); 
	public void removeElement(Object c); 
	public void removeElementAt(int pos); 
	public void setElementAt(Object element, int pos);
	public int size();
	public Enumeration elements();
	public Object elementAt(int i);
	public int indexOf(Object o);
}
