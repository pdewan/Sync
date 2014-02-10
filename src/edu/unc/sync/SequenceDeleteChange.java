package edu.unc.sync;

import edu.unc.sync.server.SyncException;
import java.util.*;

public class SequenceDeleteChange extends ElementChange
{
	ObjectID eltID;

	public SequenceDeleteChange(ObjectID objectID, ObjectID eltID)
	{
		super(objectID);
		this.eltID = eltID;
	}

	public int opcode()
	{
		return ReplicatedSequence.DELETE;
	}

	public Change getInverse(){
		return new SequenceInsertChange(getObjectID(), eltID, Sync.getObject(eltID));
	}

	public ElementChange applyTo(Replicated obj) throws ReplicationException
	{
		ReplicatedSequence seq = (ReplicatedSequence) obj;
		Replicated elt = null;
		int index;
		ElementChange rej = null;

		System.out.println("eltID to be deleted is " + eltID);
		for (Enumeration e = seq.elements(); e.hasMoreElements(); ){
			Replicated repl = (Replicated) e.nextElement();
			//System.out.println("in SequenceDeleteChange.applyto(), object id is " + repl.getObjectID());
		}

		index = seq.indexOf(eltID);

		if (index >=0){
			seq.removeElementAt(index);
		}
		else{
			System.err.println("Failed attempt to delete element " + eltID + " from " + obj.getObjectID());
			/*
			if (!seq.removeElement(elt) {
			System.err.println("Failed attempt to delete element " + eltID + " from " + obj.getObjectID());
			}
			*/
			try {
				elt = Sync.getObject(eltID);
			} catch (SyncException ex) {
				throw new ReplicationException(ex.toString());
			}

			if (elt.hasChanged()) {
				rej = new SequenceInsertChange(getObjectID(), eltID, elt);
			}
			else {
				rej = null;
			}
		}
		return rej;
	}

	public Object identifier()
	{
		return eltID;
	}

	public void print()
	{
		System.out.println("Deleted ID "+eltID.toString()+"--"+super.toString());
	}
}
