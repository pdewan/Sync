package edu.unc.sync;

import bus.uigen.*;
import bus.uigen.introspect.ClassDescriptorCache;
import bus.uigen.introspect.ClassDescriptorInterface;
import bus.uigen.introspect.IntrospectUtility;
import bus.uigen.reflect.MethodProxy;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import util.models.HashtableListener;

public class DelegatedReplicatedDictionary extends ReplicatedDictionary implements Delegated, HashtableListener
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
	public DelegatedReplicatedDictionary(Object o){
		super();
		obj = o;
		isConsistent = true;
		//treeInitialized = true;
		Sync.delegatedTable.put(o, this);
		if (null != obj){
			update();
		}
	}
        transient boolean isConsistent = false;
        public void makeSerializedObjectConsistent()
        {
          if (isConsistent) return;
          if (Sync.getSyncClient() != null)
          parentCd = DelegatedUtils.getParentCd(parentClassName);
          /*
          try {
 			 parentCd = ClassDescriptorCache.getClassDescriptor(Class.forName(parentClassName));
 			 } catch (Exception e) {
 				 e.printStackTrace();
 			 }
 			 */
//  	    IntrospectUtility.invokeInitSerializedObject(returnObject());
//          registerAsListener();
  		  Sync.delegatedTable.put(returnObject(), this);
          Enumeration elements = this.elements();
          while (elements.hasMoreElements()) {
            Replicated nextElement = (Replicated) elements.nextElement();
            if (nextElement instanceof Delegated)
              ((Delegated) nextElement).makeSerializedObjectConsistent();
          }
          elements = this.keys();           
          while (elements.hasMoreElements()) { 
        	  try {
        		  Object nextElement = elements.nextElement();
        		  //Replicated nextElement = (Replicated) elements.nextElement();
            if (nextElement instanceof Delegated)
              ((Delegated) nextElement).makeSerializedObjectConsistent();
        	  } catch (Exception e) {
        		  e.printStackTrace();
        	  }
          }
          // moved it down to make it post order
          IntrospectUtility.invokeInitSerializedObject(returnObject());
          registerAsListener();
          
          isConsistent = true;
        }
        /*
        transient boolean treeInitialized = false;
        public void initalizeSerializedTree() {
        	if (treeInitialized) return;
            Enumeration elements = this.elements();
            while (elements.hasMoreElements()) {
              Replicated nextElement = (Replicated) elements.nextElement();
              if (nextElement instanceof Delegated)
                ((Delegated) nextElement).makeSerializedObjectConsistent();
            }
            elements = this.keys();
            while (elements.hasMoreElements()) {
              Replicated nextElement = (Replicated) elements.nextElement();
              if (nextElement instanceof Delegated)
                ((Delegated) nextElement).makeSerializedObjectConsistent();
            }
            treeInitialized = true;
        	
        }
        */
        
 
	public void update(){
		try{
			Object key;
			Replicated repl;
			Method keysMethod = IntrospectUtility.getKeysMethod(obj.getClass());
			Method getMethod = IntrospectUtility.getGetMethod(obj.getClass());

			for (Enumeration e = (Enumeration) keysMethod.invoke(obj, null); e.hasMoreElements(); ){
				key = e.nextElement();

				Object[] args = new Object[1];
				args[0] = key;
				Object value = getMethod.invoke(obj, args);
				Replicated repl1 = this.get(key);
				if (null == repl1){
					Replicated replValue = DelegatedUtils.convertObject(value);
					this.put(key, replValue);
					System.out.println("000000000000000000");
				}
				if (repl1 instanceof Delegated){
					if (((Delegated) repl1).returnObject() != value){
						Replicated replValue = DelegatedUtils.convertObject(value);
						this.put(key, replValue);
						System.out.println("111111111111111111111");
					}
				}
				else{
					if (repl1 instanceof ReplicatedEnum){
						if (!value.equals(((ReplicatedEnum)repl1).getEnumValue())){
							//Replicated replValue = DelegatedUtils.convertObject(value);
							//this.put(key, replValue);
							//if (repl1 instanceof ReplicatedEnum) 
								((ReplicatedEnum)repl1).setEnumValue(value);
							
						}
					}
					else if (repl1 instanceof ReplicatedAtomic){
						if (!value.equals(((ReplicatedAtomic)repl1).getValue())){
							//Replicated replValue = DelegatedUtils.convertObject(value);
							//this.put(key, replValue);
							/*
							if (repl1 instanceof ReplicatedEnum) 
								((ReplicatedEnum)repl1).setEnumValue(value);
							else
							*/
							((ReplicatedAtomic)repl1).setValue((Serializable)value);
							System.out.println("22222222222222");
						}
					}
				}
			}

			for (Enumeration e = this.keys(); e.hasMoreElements(); ){
				key = e.nextElement();
				repl = this.get(key);

				Object[] args = new Object[1];
				args[0] = key;
				Object value = getMethod.invoke(obj, args);

				if (null == value){
					this.remove(key);
					System.out.println("calling this.remove() is update()");
					continue;
				}

				if (repl instanceof Delegated){
					((Delegated)repl).update();
				}
				else{
					if (repl instanceof ReplicatedAtomic){
						Object[] args1 = new Object[1];
						args1[0] = key;
						Object o = null;
						try{
							o = getMethod.invoke(obj, args1);
						}
						catch (Exception ex){
							System.out.println(ex.toString());
							System.out.println(ex.getMessage());
							ex.printStackTrace();
						}

						ReplicatedAtomic replAtomic = (ReplicatedAtomic) repl;
						if (!o.equals(replAtomic.getValue())){
							if (replAtomic instanceof ReplicatedEnum) 
								((ReplicatedEnum)replAtomic).setEnumValue(value);
							else
							replAtomic.setValue((Serializable)o);
						}
					}
					else{
						return;
					}
				}
			}
		}
		catch (Exception ex){
			System.out.println(ex.toString());
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void copyBack(){
		try{
			Object key;
			// remove keys first
			Method keysMethod = IntrospectUtility.getKeysMethod(obj.getClass());
			for (Enumeration e = (Enumeration) keysMethod.invoke(obj, null); e.hasMoreElements(); ){
				key = e.nextElement();
				if (null == this.get(key)){
					Method removeMethod = IntrospectUtility.getRemoveMethod(obj.getClass());
					if (null != removeMethod){
					    Object[] args = new Object[1];
					    args[0] = key;
					    removeMethod.invoke(obj, args);
					}
				}
			}

			Replicated repl;
			for (Enumeration e = this.keys(); e.hasMoreElements(); ){
				key = e.nextElement();
				repl = this.get(key);

				Method putMethod = IntrospectUtility.getPutMethod(obj.getClass());
				Object[] args = new Object[2];
				args[0] = key;

				if (repl instanceof Delegated){
					((Delegated)repl).copyBack();
					args[1] = ((Delegated)repl).returnObject();
					putMethod.invoke(obj, args);
				}
				else{
					if (repl instanceof ReplicatedAtomic){
						//Serializable value = ((ReplicatedAtomic)repl).getValue();
						Object value = ((ReplicatedAtomic)repl).getValue();
						if (repl instanceof ReplicatedEnum) 
							value = ((ReplicatedEnum)repl).getEnumValue();
						
						args[1] = value;
						putMethod.invoke(obj,args);
						/*
						try{
						Method getMethod = uiBean.getGetMethod(obj.getClass());
						Object[] args1 = new Object[1];
						args1[0] = key;
						Object o = getMethod.invoke(obj, args1);

						if (!value.equals(o)){
						Object[] args2 = new Object[2];
						args2[0] = key;
						args2[1] = value;
						Method putMethod = uiBean.getPutMethod(obj.getClass());
						putMethod.invoke(obj, args2);
						}
						}
						catch (Exception ex){
						System.out.println(ex.toString());
						System.out.println(ex.getMessage());
						ex.printStackTrace();
						}
						*/
					}
					else{
						return;
					}
				}
			}
		}
		catch (Exception ex){
			System.out.println(ex.toString());
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	public void copyBack(Change change){
		if (parentCd != null && !DelegatedUtils.shareIn(parentCd, propertyName)) return;
		if (change == null || change instanceof NullChange | change instanceof NullSet) return ;
		DictionaryChangeSet changeSet = (DictionaryChangeSet) change;
		for (Enumeration e = changeSet.enumerateChanges(); e.hasMoreElements();) {
			ElementChange ch = (ElementChange) e.nextElement();
			if (ch instanceof DictionaryPutChange) copyBackPut((DictionaryPutChange)ch);
			else if (ch instanceof DictionaryRemoveChange) copyBackRemove((DictionaryRemoveChange)ch);
			else if (ch instanceof ModifyChange ) copyBackModify ((ModifyChange) ch);			
			
		}
	}
	public void copyBackPut(DictionaryPutChange change){
		if (parentCd != null && !DelegatedUtils.shareIn(parentCd, propertyName)) return;
		Object key = change.key;
		Replicated repl = change.value;	
		
		if (repl instanceof Delegated) {
			Delegated delegated = (Delegated) repl;
			delegated.makeSerializedObjectConsistent();
		}
		try{
				
			

			Method putMethod = IntrospectUtility.getPutMethod(obj.getClass());
			Object[] args = new Object[2];
			args[0] = key;

			if (repl instanceof Delegated){
				
				//this copy back seems redundant so am removing it
				//((Delegated)repl).copyBack();
				args[1] = ((Delegated)repl).returnObject();
				putMethod.invoke(obj, args);
			}else {
				if (repl instanceof ReplicatedAtomic){
					//Serializable value = ((ReplicatedAtomic)repl).getValue();
					Object value = ((ReplicatedAtomic)repl).getValue();
					if (repl instanceof ReplicatedEnum) 
						value = ((ReplicatedEnum)repl).getEnumValue();
					args[1] = value;
					putMethod.invoke(obj,args);
					/*
					try{
					Method getMethod = uiBean.getGetMethod(obj.getClass());
					Object[] args1 = new Object[1];
					args1[0] = key;
					Object o = getMethod.invoke(obj, args1);

					if (!value.equals(o)){
					Object[] args2 = new Object[2];
					args2[0] = key;
					args2[1] = value;
					Method putMethod = uiBean.getPutMethod(obj.getClass());
					putMethod.invoke(obj, args2);
					}
					}
					catch (Exception ex){
					System.out.println(ex.toString());
					System.out.println(ex.getMessage());
					ex.printStackTrace();
					}
					*/
				} else{
					return;
				}
			}
		} catch (Exception e) {
			System.out.println ("Could not invoke put " + key + "value" + repl);
		}
	}
	public void copyBackRemove(DictionaryRemoveChange change){
		if (parentCd != null && !DelegatedUtils.shareIn(parentCd, propertyName)) return;
		Object key = change.key;
		try {		
			
					Method removeMethod = IntrospectUtility.getRemoveMethod(obj.getClass());
					if (null != removeMethod){
					    Object[] args = new Object[1];
					    args[0] = key;
					    removeMethod.invoke(obj, args);
					}
				
			
	} catch (Exception e) {
		System.out.println ("Could not invoke remove " + key);
	}

		
	}
	public void copyBackModify(ModifyChange change){
		/*
		Replicated repl = Sync.getObject(change.getObjectID());	
		if (repl instanceof Delegated){			
			
			((Delegated)repl).copyBack(change.change);
		}
		*/
		
	}
	transient boolean registered = false;
	public void registerAsListener(){
                if (registered) return;
		if (null != obj){

			//uiBean.invokeInitSerializedObject(obj); 
			Method m = DelegatedUtils.getAddHashtableListenerMethod(obj.getClass());

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

	public void keyPut(Object source, Object key, Object value, int newSize){
//		if (Sync.getTrace())
//		System.out.println(" DelegatedReplicatedDictionary.keyPut(): key " + key + " value " + value);
		//if (parentCd != null && !DelegatedUtils.shareOut(parentCd, propertyName)) return;
		if ( !DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if ( !DelegatedUtils.putOut(this, parentCd, propertyName)) return;
		Object element = this.get(key);
                if (element != null && element instanceof Delegated &&
                    ((Delegated) element).returnObject() == value )
                  return;
                Replicated repl = DelegatedUtils.convertObject(value);
                //if (parentCd != null && !DelegatedUtils.shareOut(parentCd, propertyName)) return;
		if (repl != element){
			//Replicated repl = DelegatedUtils.convertObject(value);
			this.put(key, repl);
		}
	}

	public void keyRemoved(Object source, Object key, int newSize){
		System.out.println("reached DelegatedReplicatedDictionary.keyRemoved()");
		if (!DelegatedUtils.shareOut(this, parentCd, propertyName)) return;
		if (!DelegatedUtils.deleteOut(this, parentCd, propertyName)) return;
		//if (parentCd != null && !DelegatedUtils.shareOut(parentCd, propertyName)) return;
		if (this.get(key) != null)
			this.remove(key);
	}

	public Change getChange()
	{
                //PD: getting rid of this update
		//update();
		return super.getChange();
	}

	public Change applyChange(Change change) throws ReplicationException{
		Change ch = super.applyChange(change);
		//copyBack();
		copyBack(change);
		return ch;
	}
	public void update (Replicated o, Object arg) {
		
	}
}
