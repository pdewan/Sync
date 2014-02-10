package edu.unc.sync;

import edu.unc.sync.server.SyncException;
import java.util.*;
import java.io.*;

public class ReplicatedSequence extends ReplicatedCollection
{
	private Vector seq;

	public static int DELETE = 3;
	public static int INSERT = 4;

	static SequenceMergeMatrix classMergeMatrix = new SequenceMergeMatrix();

	Hashtable original;
	Hashtable ins_mov;
	Hashtable del_mod;

	static final long serialVersionUID = 7397408430723540840L;

	public Vector getElements() {
		return seq;
	}
	public ReplicatedSequence(int size)
	{
		super();
		seq = new Vector(size < 5 ? 5 : size);

		original = new Hashtable(size < 5 ? 5 : size);
		ins_mov = new Hashtable(5);
		del_mod = new Hashtable(5);
		/*
		for (int i = 0; i < size; i++) {
		Replicated elt = (Replicated) seq.elementAt(i);
		original.put(elt.getObjectID(), elt);
		}
		*/
	}

	public ReplicatedSequence()
	{
		this(10);
	}

	public Replicated elementAt(int index)
	{
		return (Replicated) seq.elementAt(index);
	}

	public int indexOf(Object elt)
	{
		return seq.indexOf(elt);
	}

	public Enumeration elements()
	{
		return seq.elements();
	}

	private void insert(Replicated obj, int index)
	{
                //System.out.println("insert called with replicated" + obj );
		Sync.putObject(obj);
		ObjectID oid = obj.getObjectID();
                //System.out.println("oid: " + oid);
		if (original.containsKey(oid)) {
			if (del_mod.get(oid) instanceof SequenceDeleteChange) del_mod.remove(oid);
                        //System.out.println("Move change");
			ins_mov.put(oid, new SequenceMoveChange(getObjectID(), oid));
		} else {
			obj.setParent(this);
			ins_mov.put(oid, new SequenceInsertChange(getObjectID(), oid, obj));
		}
                //System.out.println("Added insert change" + ins_mov);
		seq.insertElementAt(obj, index);
                //System.out.println("calling set changed");
		setChanged();
                //System.out.println("finished set changed");
	}

	public void insertElementAt(Replicated obj, int index)
	{
               // System.out.println("insertElementAt called" + index + obj );
		insert(obj, index);
	}

	public void addElement(Replicated obj)
	{
                //System.out.println("Add Element called: " + this);
		insert(obj, seq.size());
	}

	private void delete(int index)
	{
		Replicated elt = (Replicated) seq.elementAt(index);
		if (elt.getParentID().equals(getObjectID())) elt.setParentID(null);
		ObjectID oid = elt.getObjectID();
		del_mod.put(oid, new SequenceDeleteChange(getObjectID(), oid));
		seq.removeElementAt(index);
		setChanged();
	}

	public boolean removeElement(Replicated obj)
	{
		int index = seq.indexOf(obj);
		if (index == -1) return false;
		delete(index);
		return true;
	}

	public void removeElementAt(int index)
	{
		delete(index);
	}

	public void removeAllElements()
	{
		for (int i = 0; i < seq.size(); i++) {
			Replicated elt = (Replicated) seq.elementAt(i);
			if (elt.getParentID().equals(getObjectID())) elt.setParentID(null);
			ObjectID oid = ((Replicated) seq.elementAt(i)).getObjectID();
			del_mod.put(oid, new SequenceDeleteChange(getObjectID(), oid));
		}
		seq.removeAllElements();
		setChanged();
	}

	public Replicated lastElement()
	{
		return (Replicated) seq.lastElement();
	}

	public int size()
	{
		return seq.size();
	}

	public void fixEltIds (SequenceChangeSet changeset) {
		ObjectID[] eltIDs = new ObjectID[size() + 1];

		for (int i = 0; i < size(); i++) {
			Replicated elt = elementAt(i);
			ObjectID eltID = elt.getObjectID();
			eltIDs[i] = eltID;
			//System.out.println(eltID);
		}
		// for convenience in applyChange, eltIDs has extra element for ID of sequence
		eltIDs[size()] = getObjectID();
		//return eltIDs;
		changeset.final_order = eltIDs;
	}

	// replication functions

	public Change getChange()
	{
		//if (!hasChanged()) return null;

		ObjectID[] eltIDs = new ObjectID[size() + 1];

		for (int i = 0; i < size(); i++) {
			Replicated elt = elementAt(i);
			ObjectID eltID = elt.getObjectID();
			eltIDs[i] = eltID;
			if (elt.hasChanged() & original.containsKey(eltID)) {
				Change eltChange = elt.getChange();
				if (eltChange != null) del_mod.put(eltID, new ModifyChange(getObjectID(), eltID, eltChange));
			}
		}
		// for convenience in applyChange, eltIDs has extra element for ID of sequence
		eltIDs[size()] = getObjectID();

		if (ins_mov.size() == 0 & del_mod.size() == 0) return null;
		else return new SequenceChangeSet(ins_mov, del_mod, getObjectID(), eltIDs);
	}

	public int indexOf(ObjectID oid)
	{
		if (oid.equals(getObjectID())) return size();

		for (int i = 0; i < seq.size(); i++) {
			if (oid.equals(((Replicated) elementAt(i)).getObjectID())) {
				return i;
			}
		}
		return -1;
	}

	private int correspondingIndex(ObjectID oid, ObjectID[] id_seq_at_replica)
	{
		// iadj is index of nearest element to right of oid
		int iadj;
		for (iadj = 0; iadj < id_seq_at_replica.length; iadj++)
			if (oid.equals(id_seq_at_replica[iadj])) break;
		return correspondingIndex(oid, id_seq_at_replica, iadj++);
	}

	private int correspondingIndex(ObjectID oid, ObjectID[] id_seq_at_replica, int start)
	{
		// iadj is index of nearest element to right of oid
		int iadj = start;
		int index = -1;
		while (index == -1) {
			index = indexOf(id_seq_at_replica[iadj++]);
		}
		return index;
	}

	public Change applyChange(Change change) throws ReplicationException
	{
		if (change == null || change instanceof NullChange | change instanceof NullSet) return null;

		SequenceChangeSet changeset = (SequenceChangeSet) change;

		Hashtable rej_del_mod = new Hashtable(5);
		Hashtable rej_ins_mov = new Hashtable(5);

		// apply modifications and deletions first

		for (Enumeration e = changeset.del_mod.elements(); e.hasMoreElements(); ) {
			ElementChange ch = (ElementChange) e.nextElement();
			// if element to which changes are to be applied does not exist, restore it
			/*
			if (ch instanceof ModifyChange & ch.conflicting) {
			Replicated obj = Sync.getObject(ch.identifier());
			if (indexOf(obj) == -1) {
			obj.enableReplicated(this);
			}
			}
			*/
			ElementChange reject = ch.applyTo(this);
			if (reject != null && !(reject instanceof NullElementChange))
				rej_del_mod.put(reject.identifier(), reject);
		}

		// now add new elements and do moves.

		// an element's position is indicated by the element it is to the left of.
		// we iterate through the final_order array (backwards) to get the final
		// position (in the originating replica) of the element being considered.

		// the element to the right of the inserted element in the source
		// replica's sequence may not exist in the target replica's sequence
		// so we iteratively go up the source sequence until we find an
		// element that exists in the target sequence.

		if (Sync.getSyncClient() == null){
			// server
			for (int i = changeset.final_order.length - 2; i >= 0; i--) {
				ObjectID oid = changeset.final_order[i];
				ElementChange ins_mov = (ElementChange) changeset.ins_mov.get(oid);
				if (ins_mov != null) {
					Replicated obj;
					if (ins_mov instanceof SequenceMoveChange) {
						int index = indexOf(((SequenceMoveChange) ins_mov).eltID);
						obj = elementAt(index);
						removeElementAt(index);
						if (obj == null) {
							System.err.println("Error REPSEQ.MISSINGMOVE: element to be moved not found");
							return null;
						}
						if (ins_mov.getConflicting()) rej_ins_mov.put(ins_mov.identifier(), ins_mov);
					} else {
						obj = ((SequenceInsertChange) ins_mov).insertedElt;
					}

					int index;
					//System.out.println("in server, replicatedsequence.applyChange()");
					index = 0;
					for (int j = i + 1; j < changeset.final_order.length; j++) {
						index = indexOf(changeset.final_order[j]);
						if (index >= 0) break;
					}

					insertElementAt(obj, index);
				}
			}
		}
		else{
			//client
			for (int i = 0; i<= changeset.final_order.length - 2; i++) {
				ObjectID oid = changeset.final_order[i];
				ElementChange ins_mov = (ElementChange) changeset.ins_mov.get(oid);
				if (ins_mov != null) {
					Replicated obj;
					if (ins_mov instanceof SequenceMoveChange) {
						int index = indexOf(((SequenceMoveChange) ins_mov).eltID);
						obj = elementAt(index);
						removeElementAt(index);
						if (obj == null) {
							System.err.println("Error REPSEQ.MISSINGMOVE: element to be moved not found");
							return null;
						}
						if (ins_mov.getConflicting()) rej_ins_mov.put(ins_mov.identifier(), ins_mov);
					} else {
						obj = ((SequenceInsertChange) ins_mov).insertedElt;
					}

					int index;

					//System.out.println("in client, replicatedsequence.applyChange()");
					index = i;

					insertElementAt(obj, index);
				}
			}
		}

		if (rej_ins_mov.size() == 0 & rej_del_mod.size() == 0) return null;
		else return new SequenceChangeSet(rej_ins_mov, rej_del_mod, getObjectID(), null);
	}

	public Change concatChanges(Change first_ch, Change second_ch) throws ReplicationException
	{
		if (first_ch == null || first_ch instanceof NullSet) return second_ch;
		else if (second_ch == null || second_ch instanceof NullSet) return first_ch;


		ChangeSet firstSet = (SequenceChangeSet) first_ch;
		ChangeSet secondSet = (SequenceChangeSet) second_ch;

		// start by copying first set
		SequenceChangeSet concatSet = (SequenceChangeSet) firstSet.copy();

		// add second set
		for (Enumeration e = secondSet.enumerateChanges(); e.hasMoreElements(); ) {
			ElementChange second = (ElementChange) e.nextElement();
			ElementChange first = firstSet.getCorresponding(second);
			if (first instanceof SequenceInsertChange) {
				if (second instanceof SequenceInsertChange) {
					System.err.println("Error REPSEQ.DUPLINS: two consecutive Insert ops with same object ID");
				} else if (second instanceof SequenceDeleteChange) {
					// remove the earlier Insert change
					concatSet.ins_mov.remove(first.identifier());
				} else if (second instanceof ModifyChange) {
					// SequenceInsertChange includes a reference to the element, not a
					// copy of the element.  Thus when the Insert change is transmitted to
					// another site, the value referred to will include the changes
					// contained in ModifyChange.  Thus the Modify change does not need
					// to be added.
				} else  // instanceof NullElementChange
				{}  // don't add a NullElementChange
			} else if (first instanceof SequenceDeleteChange) {
				// shouldn't have any changes after a Delete
				if (second instanceof NullElementChange) {}
				else System.err.println("Error REPSEQ.POSTDEL: should be no changes to an element after it was deleted");
			} else if (first instanceof ModifyChange) {
				if (second instanceof SequenceInsertChange) {
					System.err.println("Error REPSEQ.BADINS: Insert op cannot follow Modify op");
				} else if (second instanceof SequenceDeleteChange) {
					// Delete will replace Modify
					concatSet.addChange(second);
				} else if (second instanceof ModifyChange) {
					ObjectID oid = (ObjectID) ((ModifyChange) first).identifier();
					Replicated elt = null;
					try {
						elt = Sync.getObject(oid);
					} catch (SyncException ex) {
						throw new ReplicationException(ex.getMessage());
					}
					Change firstChange = ((ModifyChange) first).change;
					Change secondChange = ((ModifyChange) second).change;
					Change concatChanges = elt.concatChanges(firstChange, secondChange);
					concatSet.addChange(new ModifyChange(getObjectID(), (ObjectID) first.identifier(), concatChanges));
				} else  // instanceof NullElementChange
				{}
			} else {  // instanceof NullElementChange
				concatSet.addChange(second);
			}
		}
		return concatSet;
	}

	public void clearChangeSet()
	{
		//System.out.println("000000 in clearChangeSet()");
		//original = new Hashtable(size());
		original = new Hashtable(10);
		//System.out.println("111111 in clearChangeSet()");
		ins_mov = new Hashtable(10);
		del_mod = new Hashtable(10);

		for (int i = 0; i < seq.size(); i++) {
			Replicated elt = (Replicated) seq.elementAt(i);
			original.put(elt.getObjectID(), elt);
		}
	}

	public ChangeSet newChangeSet(int size)
	{
		return null;
	}

	public ChangePair newChangeSetPair(ChangeSet cs0, ChangeSet csr)
	{
		ChangeSet A0, Ar;

		if (cs0 instanceof SequenceChangeSet) {
			SequenceChangeSet scs0 = (SequenceChangeSet) cs0;
			int size_im = scs0.ins_mov.size();
			int size_dm = scs0.del_mod.size();
			Ar = new SequenceChangeSet(size_im, size_dm, getObjectID(), scs0.final_order);
		}
		else {
			Ar = new SequenceChangeSet(0, 0, getObjectID(), ((SequenceChangeSet) cs0).final_order);
		}

		if (csr instanceof SequenceChangeSet) {
			SequenceChangeSet scsr = (SequenceChangeSet) csr;
			int size_im = scsr.ins_mov.size();
			int size_dm = scsr.del_mod.size();
			A0 = new SequenceChangeSet(size_im, size_dm, getObjectID(), scsr.final_order);
		}
		else {
			A0 = new SequenceChangeSet(0, 0, getObjectID(), ((SequenceChangeSet) csr).final_order);
		}

		return new ChangePair(A0, Ar, false);
	}
	/*
	public ElementChange newModifyChange(ObjectID id, Object eltID, Change ch)
	{
	return new ModifyChange(id, (ObjectID) eltID, ch);
	}

	public Replicated identifyElement(Object eltID)
	{
	// must be in current; cannot have been deleted
	try {
	return Sync.getObject((ObjectID) eltID);
	} catch (SyncException e) {
	return null;
	}
	}
	*/
	public ElementChange concatElementChanges(ElementChange first, ElementChange second)
	{
		return null;
	}

	public MergeMatrix getClassMergeMatrix()
	{
		return classMergeMatrix;
	}
}
/*
   public TreeNode getChildAt(int childIndex)
   {
      Object child = seq.elementAt(childIndex);
      String name = "[" + String.valueOf(childIndex) + "]";
      if (child instanceof Replicated) {
         ((Replicated) child).setName(name)
         return (Replicated) child;
      } else {
         return new ObjectTreeNode(name, this);
      }
      return ;
   }

   public int getChildCount()
   {
      return seq.size();
   }

   public int getIndex(TreeNode node)
   {
      return seq.indexOf(node);
   }

   public String getElementName(Object object)
   {
      int index = seq.indexOf(object);
      return "[" + String.valueOf(index) + "]";
   }
*/