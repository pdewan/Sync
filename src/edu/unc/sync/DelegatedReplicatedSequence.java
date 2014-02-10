package edu.unc.sync;

import bus.uigen.*;
import bus.uigen.introspect.ClassDescriptorCache;
import bus.uigen.introspect.ClassDescriptorInterface;
import bus.uigen.introspect.IntrospectUtility;
import bus.uigen.reflect.MethodProxy;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import util.models.VectorMethodsListener;

public class DelegatedReplicatedSequence extends ReplicatedSequence implements Delegated, VectorMethodsListener
{
	private Object obj;
	
	String propertyName = "";
	public void setPropertyName(String newVal) {
		propertyName = newVal;
		
	}

	String parentClassName;

	 transient ClassDescriptorInterface parentCd;
	 public void setParentClassName(String newVal) {
		 parentClassName= newVal;
		 if (Sync.getSyncClient() != null)
		 parentCd = DelegatedUtils.getParentCd(parentClassName);
		 /*
		 try {
		 parentCd = ClassDescriptorCache.getClassDescriptor(Class.forName(newVal));
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 */
	 }
	public DelegatedReplicatedSequence(Object o){
		super();
                /*
                System.out.println("Delegated Replicated Sequence Called with " + obj );
                printElements();
                */
		obj = o;
		isConsistent = true;
		//treeInitialized = true;
		Sync.delegatedTable.put(o, this);
		if (null != obj){
			update();
		}
	}

        void printElements() {
          System.out.println("Delegated elements");
          Enumeration elements = this.elements();
          while (elements.hasMoreElements())
            System.out.println(elements.nextElement());
        }
        public String toString() {
          String retVal = "";
          Enumeration elements = this.elements();
          while (elements.hasMoreElements())
            retVal += elements.nextElement();
          return retVal;
        }
        transient boolean isConsistent = false;
        public void makeSerializedObjectConsistent()
         {
           if (isConsistent) return;
           parentCd = DelegatedUtils.getParentCd(parentClassName);
           /*
           try {
      		 parentCd = ClassDescriptorCache.getClassDescriptor(Class.forName(parentClassName));
      		 } catch (Exception e) {
      			 e.printStackTrace();
      		 }
      		 */
           Sync.delegatedTable.put(returnObject(), this);
           // moving it down
//   		   IntrospectUtility.invokeInitSerializedObject(returnObject());
//           registerAsListener();
           Enumeration elements = this.elements();
           /*
            removeElementAtMethod = uiBean.getRemoveElementAtMethod(obj.getClass());
   		 insertElementAtMethod = uiBean.getInsertElementAtMethod(obj.getClass());
   		 addElementMethod = uiBean.getAddElementMethod(obj.getClass());
   		 */
           while (elements.hasMoreElements()) {
             Replicated nextElement = (Replicated) elements.nextElement();
             if (nextElement instanceof Delegated)
               ((Delegated) nextElement).makeSerializedObjectConsistent();
           }
           // moved it down to make it post order
           IntrospectUtility.invokeInitSerializedObject(returnObject());
           registerAsListener();
           isConsistent = true;
        }

	public void update(){
          /*
                System.out.println("before update ");
                this.printElements();
                this.printDelegatorElements();
          */
                if (this.size() == 0) {
                  addDelegatorElements();
                  return;
                }
                //Not sure what we are doing below
		Replicated repl;
		for (Enumeration e = this.elements(); e.hasMoreElements(); ){
			repl = (Replicated)e.nextElement();
			if (repl instanceof Delegated){
				((Delegated)repl).update();
			}
			else{
				if (repl instanceof ReplicatedAtomic){
					int index = indexOf((Object)repl);
					//Object o = obj.elementAt(index);
					Object[] args = new Object[1];
					args[0] = new Integer(index);
					Object o = null;
					try{
						Method elementAtMethod = IntrospectUtility.getElementAtMethod(obj.getClass(), false);
						if (null != elementAtMethod){
							o = elementAtMethod.invoke(obj, args);
						}
						else{
							Method elementsMethod = IntrospectUtility.getElementsMethod(this.obj.getClass());
							Enumeration e1 = (Enumeration) elementsMethod.invoke(this.obj, null);
							int i;
							for (i=0; (i != index) && e1.hasMoreElements();){
								i++;
								e1.nextElement();
							}
							if ((i == index) && e1.hasMoreElements()){
								o = e1.nextElement();
							}
							else{
								System.out.println("i != index or no more elements in DelegatedReplicatedSequence.update()");
								return;
							}
						}
					}
					catch(InvocationTargetException tex){
						System.out.println("invocationtargetexception begin");
						Throwable ex1 = tex.getTargetException();
						System.out.println(tex.toString());
						System.out.println(tex.getMessage());
						//tex.printStackTrace();
						System.out.println(ex1.toString());
						System.out.println(ex1.getMessage());
						ex1.printStackTrace();
						System.out.println("invocationtargetexception end");
					}
					catch (Exception ex){
						System.out.println(ex.toString());
						System.out.println(ex.getMessage());
						ex.printStackTrace();
						return;
					}

					ReplicatedAtomic replAtomic = (ReplicatedAtomic) repl;
					if (!o.equals(replAtomic.getValue())){
						if (replAtomic instanceof ReplicatedEnum) 
							((ReplicatedEnum)replAtomic).setEnumValue(o);
						else
						replAtomic.setValue((Serializable)o);
					}
				}
				else{
					return;
				}
			}
		}
                //System.out.println("after update " + this);
	}

        void printDelegatorElements() {
          System.out.println("Delegator elements");
          try {
            Method elementsMethod = IntrospectUtility.getElementsMethod(obj.getClass());
            Enumeration e1 = (Enumeration) elementsMethod.invoke(this.obj, null);
            while (e1.hasMoreElements())
              System.out.println(e1.nextElement());
          } catch (Exception e) {
            System.out.println("PrintDelegatorElements " + e);

          }
        }
        /*
        void addDelegatorElements() {
          //System.out.println("Add Delegator elements");
          try {
            VirtualMethod elementsMethod = uiBean.getElementsMethod(obj.getClass());
            
            Enumeration e1 = (Enumeration) elementsMethod.invoke(this.obj, null);
            while (e1.hasMoreElements())
              this.elementAdded(e1.nextElement(), this.size() + 1);
          } catch (Exception e) {
            System.out.println("AddDelegatorElements " + e);
          }
        }
        */
        void addDelegatorElements() {
            //System.out.println("Add Delegator elements");
            try {
              Method elementsMethod = IntrospectUtility.getElementsMethod(obj.getClass());
              if (elementsMethod != null) {
            	  addDelegatorElements(elementsMethod);
            	  return;
              }
              Method elementAtMethod = IntrospectUtility.getElementAtMethod(obj.getClass(), false);
              Method sizeMethod = IntrospectUtility.getSizeMethod(obj.getClass());
              addDelegatorElements(elementAtMethod, sizeMethod);
            } catch (Exception e) {
              System.out.println("AddDelegatorElements " + e);
              e.printStackTrace();
            }
          }
        void addDelegatorElements(Method elementsMethod) {
        	try {
        		Enumeration e1 = (Enumeration) elementsMethod.invoke(this.obj, null);
        		while (e1.hasMoreElements())
        			this.elementAdded(this, e1.nextElement(), this.size() + 1); 
        	} catch (Exception e) {
        		System.out.println("AddDelegatorElements " + e);
        		e.printStackTrace();
        	}
        	
        }
        void addDelegatorElements(Method elementAtMethod, Method sizeMethod) {
        	try {
        		int size = (Integer) sizeMethod.invoke(obj, null);
        		for (int i = 0; i < size; i++) {
        			Object[] args = {i};        		
        			Object nextElement = elementAtMethod.invoke(obj, args);
        			this.elementAdded (this, nextElement, this.size() + 1); 
        		}
        	} catch (Exception e) {
                System.out.println("AddDelegatorElements " + e);
                e.printStackTrace();
              }
        	
        }
        public void copyBack(Change change) {

    		if (!DelegatedUtils.shareIn(parentCd, propertyName)) return;
    	  	copyBack();
    	  }
	public void copyBack(){
		if (!DelegatedUtils.shareIn(parentCd, propertyName)) return;
		Replicated repl;
                /*
                System.out.println("DRS: copying back into Delegator " + obj);
                System.out.println("Before copy back. Delegate =");
                printElements();
                System.out.println("Before copy back. Delegator =");
                printDelegatorElements();
                */
		for (Enumeration e = this.elements(); e.hasMoreElements(); ){
			repl = (Replicated)e.nextElement();
                        //System.out.println("Delegated Sequence Element: " + repl);
			if (repl instanceof Delegated){
				((Delegated)repl).copyBack();
			}
			else{
				if (repl instanceof ReplicatedAtomic){
					//Serializable value = ((ReplicatedAtomic)repl).getValue();
					Object value = ((ReplicatedAtomic)repl).getValue();
					if (repl instanceof ReplicatedEnum)
						value = ((ReplicatedEnum) repl).getEnumValue();
					int index = indexOf((Object)repl);
					//System.out.println("in copyBack(), index=" + index + " value=" + value);
					//this.seqCopy.setElementAt(value, index);
					//this.obj.setElementAt(value, index);

					try{
						ReplicatedAtomic replAtomic = (ReplicatedAtomic)repl;
						Method elementAtMethod = IntrospectUtility.getElementAtMethod(obj.getClass(), false);
						//Object[] args1 = new Object[1];
						//args1[0] = new Integer(index);
						Object o = null;
						if (null != elementAtMethod){
							Object[] args1 = new Object[1];
							args1[0] = new Integer(index);
							o = elementAtMethod.invoke(obj, args1);
						}
						else{
							Method elementsMethod = IntrospectUtility.getElementsMethod(this.obj.getClass());
							Enumeration e1 = (Enumeration) elementsMethod.invoke(this.obj, null);
							int i;
							for (i=0; (i != index) && e1.hasMoreElements();){
								i++;
								e1.nextElement();
							}
							if ((i == index) && e1.hasMoreElements()){
								o = e1.nextElement();
							}
							else{
								System.out.println("i != index or no more elements in DelegatedReplicatedSequence.copyBack()");
								return;
							}
						}

						if (!replAtomic.getValue().equals(o)){
							Object[] args2 = new Object[2];
							args2[0] = value;
							args2[1] = new Integer(index);
							Method setElementAtMethod = IntrospectUtility.getSetElementAtMethod(obj.getClass());
							setElementAtMethod.invoke(obj, args2);
						}
					}
					catch (Exception ex){
						System.out.println(ex.toString());
						System.out.println(ex.getMessage());
						ex.printStackTrace();
					}
				}
				else{
					return;
				}
			}
		}
                /*
                System.out.println("After copy back:" + this);
                System.out.println("After copy back. Delegate =");
                printElements();
                System.out.println("Afterore copy back. Delegator =");
                printDelegatorElements();
                */
	}
	
        transient boolean registered = false;
	public void registerAsListener(){
		//obj.addVectorMethodsListener(this); 
		if (Sync.getSyncClient() == null) return;
                if (registered) return;
		if (null != obj){

			//uiBean.invokeInitSerializedObject(obj); 
			Method m = DelegatedUtils.getAddVectorMethodsListenerMethod(obj.getClass());

			if (null != m){
				Object[] args = new Object[1];
				args[0] = this;
				try{
					m.invoke(obj, args);
                                        registered = true;
				}
				catch (Exception e){
					System.out.println(e.toString());
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public Object returnObject(){
		return obj;
	}
        public void setObject(Object newObj) {
          obj = newObj;
          registerAsListener();
        }


	public void elementAdded(Object source, Object element, int newSize){
		//System.out.println("reached DelegatedReplicatedSequence.elementAdded() of " + this.returnObject() + " size " + newSize);
		/*
		Object o;
		for (Enumeration e = seqCopy.elements(); e.hasMoreElements(); ){
		o = e.nextElement();
		if (o == element) return;
		}
		*/
		//if (Sync.getSyncClient() == null) return ;
		if (Sync.getSyncClient() != null && !DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if (Sync.getSyncClient() != null && !DelegatedUtils.addOut(this, parentCd, propertyName)) return;
		if (newSize > this.size()){
			//seqCopy.addElement(element);
			//System.out.println("reached after addElement");
			Replicated repl = DelegatedUtils.convertObject(element);
			
			
                        /*
                        if (repl instanceof Delegated) {
                          System.out.println("registering as listener of added object");
                          ((Delegated) repl).registerAsListener();
                        }
                        */
			//System.out.println("repl is ");
			//System.out.println(repl);
			addElement(repl);
			if (repl instanceof Delegated) {
				((Delegated) repl).makeSerializedObjectConsistent();
			}
		}

	}

	public void elementInserted(Object source, Object element, int pos, int newSize){
		//System.out.println("reached DelegatedReplicatedSequence.elementInserted(), pos=" + pos + " size=" + size() + "element: " + element);
		/*
		Object o;
		for (Enumeration e = seqCopy.elements(); e.hasMoreElements(); ){
		o = e.nextElement();
		if (o == element) return;
		}
		*/
		//if (Sync.getSyncClient() == null) return;
		if (Sync.getSyncClient() != null && !DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if (Sync.getSyncClient() != null && !DelegatedUtils.insertOut(this, parentCd, propertyName)) return;
		if (newSize > this.size()){
			//seqCopy.insertElementAt(element, pos);
			Replicated repl = DelegatedUtils.convertObject(element);
			insertElementAt(repl, pos);
		}
	}

	public void elementChanged(Object source, Object element, int pos){
		//if (Sync.getSyncClient() == null) return;
		System.out.println("reached DelegatedReplicatedSequence.elementChanged(), pos = " + pos + " size = " + size());
		//seqCopy.setElementAt(element, pos);
		//Replicated repl = DelegatedUtils.convertObject(element);
		//removeElementAt(pos);
		//insertElementAt(repl, pos);
		if (Sync.getSyncClient() != null && !DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if (Sync.getSyncClient() != null && !DelegatedUtils.modifyOut(this, parentCd, propertyName)) return;
		Replicated repl = (Replicated) this.elementAt(pos);
		if (repl instanceof Delegated){
			((Delegated)repl).update();
		}
		else
		{
			if (repl instanceof ReplicatedAtomic){
				if (repl instanceof ReplicatedEnum) 
					((ReplicatedEnum)repl).setEnumValue(element);
				else
				((ReplicatedAtomic)repl).setValue((Serializable) element);
			}
		}
		//System.out.println("after elementChanged(), size=" + size());
	}

	public void elementRemoved(Object source, int pos, int newSize){
		System.out.println("reached DelegatedReplicatedSequence.elementRemoved(int, int)");
		if (!DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if (!DelegatedUtils.deleteOut(this, parentCd, propertyName)) return;
		if (newSize < this.size()){
			//seqCopy.removeElementAt(pos);
			removeElementAt(pos);
			//System.out.println("removed element (int, int)");
		}
	}
	
	public void elementsCleared(Object source) {
		System.out.println("reached DelegatedReplicatedSequence.elementsCleared(int, int)");
		if (!DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if (!DelegatedUtils.deleteOut(this, parentCd, propertyName)) return;
		for (int i = 0; i < size(); i++)
			removeElementAt(i);
	}

	public void elementRemoved(Object source, Object element, int newSize, int pos){
		//System.out.println("reached DelegatedReplicatedSequence.elementRemoved(Object, int)");
		if (!DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if (!DelegatedUtils.deleteOut(this, parentCd, propertyName)) return;
		if (newSize < this.size()){
			Replicated elt;
			int i = 0;

			for (Enumeration e = this.elements(); e.hasMoreElements(); ){
				elt = (Replicated) e.nextElement();
				if (elt instanceof Delegated){
					if (((Delegated)elt).returnObject() == element){
						//System.out.println("removed element (Object, int)");
						removeElementAt(i);
						break;
					}
				}
				else{
					if (elt instanceof ReplicatedEnum){
						if (((ReplicatedEnum)elt).getEnumValue().equals(element)){
							//System.out.println("removed element (Object, int)");
							removeElementAt(i);
							break;
						}
					}
						
					else if (elt instanceof ReplicatedAtomic){
						if (((ReplicatedAtomic)elt).getValue().equals(element)){
							//System.out.println("removed element (Object, int)");
							removeElementAt(i);
							break;
						}
					}
				}
				i++;
			}
		}
	}

	public Change getChange()
	{
		//update();
		return super.getChange();
	}
	/*
	public Change applyChangeBatch(Change change) throws ReplicationException
	{
                if (false) {
                System.out.println("applying change");
                this.printElements();
                this.printDelegatorElements();
                System.out.println(change);
                 }

		if (change == null || change instanceof NullChange | change instanceof NullSet) return null;

		SequenceChangeSet changeset = (SequenceChangeSet) change;

		Hashtable rej_del_mod = new Hashtable(5);
		Hashtable rej_ins_mov = new Hashtable(5);

		Method removeElementAtMethod = uiBean.getRemoveElementAtMethod(obj.getClass());
		Method insertElementAtMethod = uiBean.getInsertElementAtMethod(obj.getClass());

		// apply modifications and deletions first

		for (Enumeration e = changeset.del_mod.elements(); e.hasMoreElements(); ) {
			ElementChange ch = (ElementChange) e.nextElement();
			// if element to which changes are to be applied does not exist, restore it
			
			//if (ch instanceof ModifyChange & ch.conflicting) {
			//Replicated obj = Sync.getObject(ch.identifier());
			//if (indexOf(obj) == -1) {
			//obj.enableReplicated(this);
			//}
			//}
			
			int index = 0;
			if (ch instanceof SequenceDeleteChange){
				SequenceDeleteChange seqCh = (SequenceDeleteChange)ch;
				index = indexOf((ObjectID)(seqCh.identifier()));
				//System.out.println("in applyChange(), index=" + index + " size=" + size());
			}

			ElementChange reject = ch.applyTo(this);
			if (reject != null && !(reject instanceof NullElementChange))
				rej_del_mod.put(reject.identifier(), reject);
			else{
				if (ch instanceof SequenceDeleteChange){
					//this.obj.removeElementAt(index);
					if (null != removeElementAtMethod){
						Object[] args = new Object[1];
						args[0] = new Integer(index);
						try{
							removeElementAtMethod.invoke(this.obj, args);
						}
						catch (Exception ex){
							System.out.println(ex.toString());
							System.out.println(ex.getMessage());
							ex.printStackTrace();
						}
					}
				}
			}

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
			//server
			for (int i = changeset.final_order.length - 2; i >= 0; i--) {
				ObjectID oid = changeset.final_order[i];
				ElementChange ins_mov = (ElementChange) changeset.ins_mov.get(oid);
				if (ins_mov != null) {
					Replicated obj;
					if (ins_mov instanceof SequenceMoveChange) {
						int index = indexOf(((SequenceMoveChange) ins_mov).eltID);
						obj = elementAt(index);
						removeElementAt(index);
						//this.seqCopy.removeElementAt(index);
						//this.obj.removeElementAt(index);
						if (null != removeElementAtMethod){
							Object[] args = new Object[1];
							args[0] = new Integer(index);
							try{
								removeElementAtMethod.invoke(this.obj, args);
							}
							catch (Exception ex){
								System.out.println(ex.toString());
								System.out.println(ex.getMessage());
								ex.printStackTrace();
							}
						}

						if (obj == null) {
							System.err.println("Error REPSEQ.MISSINGMOVE: element to be moved not found");
							return null;
						}
						if (ins_mov.getConflicting()) rej_ins_mov.put(ins_mov.identifier(), ins_mov);
					} else {
						ObjectID insertedId = (ObjectID)((SequenceInsertChange) ins_mov).eltID;
						obj = Sync.getObject(insertedId);
					}

					int index;
					//System.out.println("in server, replicatedsequence.applyChange()");
					index = 0;
					for (int j = i + 1; j < changeset.final_order.length; j++) {
						index = indexOf(changeset.final_order[j]);
						if (index >= 0) break;
					}

					if (index < 0)
						index = 0;
					//System.out.println("index=" + index);
					insertElementAt(obj, index);
					Object insertedElt;
					if (obj instanceof Delegated){
						insertedElt = ((Delegated)obj).returnObject();
					}
					else{
						if (obj instanceof ReplicatedAtomic){
							insertedElt = ((ReplicatedAtomic)obj).getValue();
						}
						else{
							//System.out.println("Class of inserted element not supported");
							continue;
						}
					}

					//this.seqCopy.insertElementAt(insertedElt, index);
					//this.obj.insertElementAt(insertedElt, index);
					try{
						if (null != insertElementAtMethod){
							Object[] args = new Object[2];
							args[0] = insertedElt;
							args[1] = new Integer(index);
							insertElementAtMethod.invoke(this.obj, args);
						}
						else{
							Method elementsMethod = uiBean.getElementsMethod(this.obj.getClass());
							Enumeration e = (Enumeration) elementsMethod.invoke(this.obj, null);
							int size = 0;
							for (; e.hasMoreElements();){
								size++;
								e.nextElement();
							}
							if (index == size){
								Method addElementMethod = uiBean.getAddElementMethod(this.obj.getClass());
								Object[] args = new Object[1];
								args[0] = insertedElt;
								addElementMethod.invoke(this.obj, args);
							}
							else{
								System.out.println("The object does not have an insertElementAt method, and addElement is not applicable");
							}
						}
					}
					catch(Exception ex){
						System.out.println(ex.toString());
						System.out.println(ex.getMessage());
						ex.printStackTrace();
					}
				}
			}
		}
		else{
			//client
			for (int i = 0; i<= changeset.final_order.length - 2; i++) {
				ObjectID oid = changeset.final_order[i];
				//System.out.println(oid);
				ElementChange ins_mov = (ElementChange) changeset.ins_mov.get(oid);
				if (ins_mov != null) {
					Replicated obj;
					if (ins_mov instanceof SequenceMoveChange) {
						int index = indexOf(((SequenceMoveChange) ins_mov).eltID);
						obj = elementAt(index);
						removeElementAt(index);
						//this.seqCopy.removeElementAt(index);
						//this.obj.removeElementAt(index);
						if (null != removeElementAtMethod){
							Object[] args = new Object[1];
							args[0] = new Integer(index);
							try{
								removeElementAtMethod.invoke(this.obj, args);
							}
							catch (Exception ex){
								System.out.println(ex.toString());
								System.out.println(ex.getMessage());
								ex.printStackTrace();
							}
						}

						if (obj == null) {
							System.err.println("Error REPSEQ.MISSINGMOVE: element to be moved not found");
							return null;
						}
						if (ins_mov.getConflicting()) rej_ins_mov.put(ins_mov.identifier(), ins_mov);
					} else {
						ObjectID insertedId = (ObjectID)((SequenceInsertChange) ins_mov).eltID;
						obj = Sync.getObject(insertedId);
					}

					int index;
					//System.out.println("in client, replicatedsequence.applyChange()");
					index = i;

					if (index < 0)
						index = 0;
					//System.out.println("index=" + index);
					insertElementAt(obj, index);
					Object insertedElt;
					if (obj instanceof Delegated){
						insertedElt = ((Delegated)obj).returnObject();
					}
					else{
						if (obj instanceof ReplicatedAtomic){
							insertedElt = ((ReplicatedAtomic)obj).getValue();
						}
						else{
							System.out.println("Class of inserted element not supported");
							continue;
						}
					}

					//this.seqCopy.insertElementAt(insertedElt, index);
					//this.obj.insertElementAt(insertedElt, index);
					try{
						if (null != insertElementAtMethod){
							Object[] args = new Object[2];
							args[0] = insertedElt;
							args[1] = new Integer(index);
							insertElementAtMethod.invoke(this.obj, args);
						}
						else{
							Method elementsMethod = uiBean.getElementsMethod(this.obj.getClass());
							Enumeration e = (Enumeration) elementsMethod.invoke(this.obj, null);
							int size = 0;
							for (; e.hasMoreElements();){
								size++;
								e.nextElement();
							}
							if (index == size){
								Method addElementMethod = uiBean.getAddElementMethod(this.obj.getClass());
								Object[] args = new Object[1];
								args[0] = insertedElt;
								addElementMethod.invoke(this.obj, args);
							}
							else{
								System.out.println("The object does not have an insertElementAt method, and addElement is not applicable");
							}
						}
					}
					catch(Exception ex){
						System.out.println(ex.toString());
						System.out.println(ex.getMessage());
						ex.printStackTrace();
					}
				}
			}
		}
                if (false) {
                System.out.println("Before  copy back");
                this.printElements();
                this.printDelegatorElements();
                }
		//copyBack();
		copyBack(change);
                if (false) {
                System.out.println("After copy back");
                this.printElements();
                this.printDelegatorElements();
                }
		if (rej_ins_mov.size() == 0 & rej_del_mod.size() == 0) return null;
		else return new SequenceChangeSet(rej_ins_mov, rej_del_mod, getObjectID(), null);
	}
*/


	public Change applyChange(Change change) throws ReplicationException
	{
                /*
                System.out.println("applying change");
                this.printElements();
                this.printDelegatorElements();
                System.out.println(change);
                 */

		if (change == null || change instanceof NullChange | change instanceof NullSet) return null;

		SequenceChangeSet changeset = (SequenceChangeSet) change;

		Hashtable rej_del_mod = new Hashtable(5);
		Hashtable rej_ins_mov = new Hashtable(5);

		Method removeElementAtMethod = IntrospectUtility.getRemoveElementAtMethod(obj.getClass());
		Method removeElementMethod = IntrospectUtility.getRemoveElementMethod(obj.getClass());
		Method insertElementAtMethod = IntrospectUtility.getInsertElementAtMethod(obj.getClass());
		Method addElementMethod = IntrospectUtility.getAddElementMethod(obj.getClass());

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
			int index = 0;
			Object deletedElement = null;
			if (ch instanceof SequenceDeleteChange){
				SequenceDeleteChange seqCh = (SequenceDeleteChange)ch;
				index = indexOf((ObjectID)(seqCh.identifier()));
				if (index != -1)
					deletedElement = DelegatedUtils.convertReplicated(this.elementAt(index));
				//System.out.println("in applyChange(), index=" + index + " size=" + size());
			}
			
			ElementChange reject = ch.applyTo(this);
			if (reject != null && !(reject instanceof NullElementChange))
				rej_del_mod.put(reject.identifier(), reject);
			else{
				if (ch instanceof SequenceDeleteChange){
					//this.obj.removeElementAt(index);
					if (null != removeElementAtMethod){
						Object[] args = new Object[1];
						args[0] = new Integer(index);
						try{
							removeElementAtMethod.invoke(this.obj, args);
						}
						catch (Exception ex){
							System.out.println(ex.toString());
							System.out.println(ex.getMessage());
							ex.printStackTrace();
						}
					}
					else if (removeElementMethod != null && deletedElement != null ) {
						Object[] args = new Object[1];
						args[0] = deletedElement;
						
						try{
							removeElementMethod.invoke(this.obj, args);
						}
						catch (Exception ex){
							System.out.println(ex.toString());
							System.out.println(ex.getMessage());
							ex.printStackTrace();
						}
					}
				}
			}

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
			//server
			for (int i = changeset.final_order.length - 2; i >= 0; i--) {
				ObjectID oid = changeset.final_order[i];
				ElementChange ins_mov = (ElementChange) changeset.ins_mov.get(oid);
				if (ins_mov != null) {
					Replicated obj;
					if (ins_mov instanceof SequenceMoveChange) {
						int index = indexOf(((SequenceMoveChange) ins_mov).eltID);
						obj = elementAt(index);
						removeElementAt(index);
						//this.seqCopy.removeElementAt(index);
						//this.obj.removeElementAt(index);
						if (null != removeElementAtMethod){
							Object[] args = new Object[1];
							args[0] = new Integer(index);
							try{
								removeElementAtMethod.invoke(this.obj, args);
							}
							catch (Exception ex){
								System.out.println(ex.toString());
								System.out.println(ex.getMessage());
								ex.printStackTrace();
							}
						}

						if (obj == null) {
							System.err.println("Error REPSEQ.MISSINGMOVE: element to be moved not found");
							return null;
						}
						if (ins_mov.getConflicting()) rej_ins_mov.put(ins_mov.identifier(), ins_mov);
					} else {
						ObjectID insertedId = (ObjectID)((SequenceInsertChange) ins_mov).eltID;
						obj = Sync.getObject(insertedId);
					}

					int index;
					//System.out.println("in server, replicatedsequence.applyChange()");
					index = 0;
					for (int j = i + 1; j < changeset.final_order.length; j++) {
						index = indexOf(changeset.final_order[j]);
						if (index >= 0) break;
					}

					if (index < 0)
						index = 0;
					//System.out.println("index=" + index);
					
					insertElementAt(obj, index);
					if (obj instanceof Delegated) {
						Delegated delegated = (Delegated) obj;
						delegated.makeSerializedObjectConsistent();
					}
					Object insertedElt;
					if (obj instanceof Delegated){
						insertedElt = ((Delegated)obj).returnObject();
					}
					else{
						/*if (obj instanceof ReplicatedEnum) {
							insertedElt = ((ReplicatedEnum)obj).getEnumValue();
						}
						else*/ if (obj instanceof ReplicatedAtomic){
							insertedElt = ((ReplicatedAtomic)obj).getValue();
						}
						else{
							//System.out.println("Class of inserted element not supported");
							continue;
						}
					}
					
					//this.seqCopy.insertElementAt(insertedElt, index);
					//this.obj.insertElementAt(insertedElt, index);
					//if (Sync.getSyncClient() != null) {
					try{
						//Replicated convertedObject = DelegatedUtils.convertObject(insertedElt);
						//super.addElement(convertedObject);
						/*
						if (convertedObject instanceof Delegated) {
							((Delegated) convertedObject).makeSerializedObjectConsistent();
						}
						*/
						if (Sync.getTrace())
							System.out.println("Sequence size:" + this.size());
						if (index == this.size() -1 
								&& addElementMethod != null ) {
							Object[] args = {insertedElt};
							addElementMethod.invoke(this.obj, args);
							
							
							
							
							
						}
						else if (null != insertElementAtMethod){
							 //convertedObject = DelegatedUtils.convertObject(insertedElt);
							//super.insertElementAt(convertedObject, index);
							Object[] args = new Object[2];
							args[0] = insertedElt;
							args[1] = new Integer(index);
							insertElementAtMethod.invoke(this.obj, args);
						}
						
						else{
							System.out.println("The object does not have an insertElementAt method, and addElement is not applicable");
							/*
							VirtualMethod elementsMethod = uiBean.getElementsMethod(this.obj.getClass());
							Enumeration e = (Enumeration) elementsMethod.invoke(this.obj, null);
							int size = 0;
							for (; e.hasMoreElements();){
								size++;
								e.nextElement();
							}
							if (index == size){
								//VirtualMethod addElementMethod = uiBean.getAddElementMethod(this.obj.getClass());
								Object[] args = new Object[1];
								args[0] = insertedElt;
								addElementMethod.invoke(this.obj, args);
							}
							else{
								System.out.println("The object does not have an insertElementAt method, and addElement is not applicable");
							}
							*/
						}
					}
					catch(Exception ex){
						System.out.println(ex.toString());
						System.out.println(ex.getMessage());
						ex.printStackTrace();
					}
					//}
				}
			}
		}
		else{
			//client
			for (int i = 0; i<= changeset.final_order.length - 2; i++) {
				ObjectID oid = changeset.final_order[i];
				//System.out.println(oid);
				ElementChange ins_mov = (ElementChange) changeset.ins_mov.get(oid);
				if (ins_mov != null) {
					Replicated obj;
					if (ins_mov instanceof SequenceMoveChange) {
						int index = indexOf(((SequenceMoveChange) ins_mov).eltID);
						obj = elementAt(index);
						removeElementAt(index);
						//this.seqCopy.removeElementAt(index);
						//this.obj.removeElementAt(index);
						if (null != removeElementAtMethod){
							Object[] args = new Object[1];
							args[0] = new Integer(index);
							try{
								removeElementAtMethod.invoke(this.obj, args);
							}
							catch (Exception ex){
								System.out.println(ex.toString());
								System.out.println(ex.getMessage());
								ex.printStackTrace();
							}
						}

						if (obj == null) {
							System.err.println("Error REPSEQ.MISSINGMOVE: element to be moved not found");
							return null;
						}
						if (ins_mov.getConflicting()) rej_ins_mov.put(ins_mov.identifier(), ins_mov);
					} else {
						ObjectID insertedId = (ObjectID)((SequenceInsertChange) ins_mov).eltID;
						obj = Sync.getObject(insertedId);
					}

					int index;
					//System.out.println("in client, replicatedsequence.applyChange()");
					index = i;

					if (index < 0)
						index = 0;
					//System.out.println("index=" + index);
					if (obj instanceof Delegated) {
						Delegated delegated = (Delegated) obj;
						delegated.makeSerializedObjectConsistent();
					}
					insertElementAt(obj, index);
					Object insertedElt;
					if (obj instanceof Delegated){
						insertedElt = ((Delegated)obj).returnObject();
					}
					else{
						if (obj instanceof ReplicatedAtomic){
							insertedElt = ((ReplicatedAtomic)obj).getValue();
						}
						else{
							System.out.println("Class of inserted element not supported");
							continue;
						}
					}

					//this.seqCopy.insertElementAt(insertedElt, index);
					//this.obj.insertElementAt(insertedElt, index);
					try{
						if (Sync.getTrace())
							System.out.println("Sequence size:" + this.size());

						//Replicated convertedObject = DelegatedUtils.convertObject(insertedElt);
						//super.addElement(convertedObject);
						/*
						if (convertedObject instanceof Delegated) {
							((Delegated) convertedObject).makeSerializedObjectConsistent();
						}
						*/
						if (index == this.size() -1 
								&& addElementMethod != null ) {
							Object[] args = {insertedElt};
							addElementMethod.invoke(this.obj, args);
							}
							
						
						else if (null != insertElementAtMethod){
							//convertedObject = DelegatedUtils.convertObject(insertedElt);
							//super.insertElementAt(convertedObject, index);
							Object[] args = new Object[2];
							args[0] = insertedElt;
							args[1] = new Integer(index);
							insertElementAtMethod.invoke(this.obj, args);
						}
						else{
							System.out.println("The object does not have an insertElementAt method, and addElement is not applicable");
							/*
							VirtualMethod elementsMethod = uiBean.getElementsMethod(this.obj.getClass());
							Enumeration e = (Enumeration) elementsMethod.invoke(this.obj, null);
							int size = 0;
							for (; e.hasMoreElements();){
								size++;
								e.nextElement();
							}
							if (index == size){
								//VirtualMethod addElementMethod = uiBean.getAddElementMethod(this.obj.getClass());
								Object[] args = new Object[1];
								args[0] = insertedElt;
								addElementMethod.invoke(this.obj, args);
							}
							else{
								System.out.println("The object does not have an insertElementAt method, and addElement is not applicable");
							}
							*/
						}
					}
					catch(Exception ex){
						System.out.println(ex.toString());
						System.out.println(ex.getMessage());
						ex.printStackTrace();
					}
				}
			}
		}
                /*
                System.out.println("Before  copy back");
                this.printElements();
                this.printDelegatorElements();
                */
		//copyBack();
		//copyBack(change);
                /*
                System.out.println("After copy back");
                this.printElements();
                this.printDelegatorElements();
                */
		if (rej_ins_mov.size() == 0 & rej_del_mod.size() == 0) return null;
		else return new SequenceChangeSet(rej_ins_mov, rej_del_mod, getObjectID(), null);
	}
	public void update (Replicated o, Object arg) {
		
	}
	@Override
	public void elementsAdded(Object source, Collection element, int newSize) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopied(Object source, int fromIndex, int toIndex, int newSize) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopied(Object source, int fromIndex, int fromNewSize, Object to, int toIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementMoved(Object source, int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementMoved(Object source, int fromIndex, int fromNewSize, Object to, int toIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementReplaced(Object source, int fromIndex, int toIndex, int newSize) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementReplaced(Object source, int fromIndex, int newFromSize, Object to, int toIndex) {
		// TODO Auto-generated method stub
		
	}
//	@Override
//	public void elementSwapped(int index1, int index2) {
//		elementSwapped(null, index1, index2);
//	}
	@Override
	public void elementSwapped(Object newParam, int index1, int index2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementSwapped(Object source, int index1, Object other, int index2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopiedToUserObject(Object source, int fromIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopiedFromUserObject(Object source, int fromIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void userObjectChanged(Object source, Object newVal) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void elementRead(Object source, Object element, Integer pos) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void tempChanged(Object source, Object newVal) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopiedToTemp(Object source, int fromIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopiedFromTemp(Object source, int fromIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void userObjectCopiedToTemp(Object source, Object copiedValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void tempCopiedToUserObject(Object source, Object copiedValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void userObjectRead(Object source, Object readValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void tempRead(Object source, Object readValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopiedAndInserted(Object source, int fromIndex,
			int toIndex, int newSize) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void elementCopiedAndInserted(Object source, int fromIndex,
			int fromNewSize, Object to, int toIndex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void pointerChanged(Object source, Integer pointerValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void pointer2Changed(Object source, Integer pointerValue) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void userOperationOccured(Object source, Integer aTargetIndex,
			Object anOperation) {
		// TODO Auto-generated method stub
		
	}
}
