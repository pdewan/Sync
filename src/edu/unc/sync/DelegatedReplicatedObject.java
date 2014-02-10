package edu.unc.sync;

import java.io.*;
import java.util.*;
import java.beans.*;
import java.lang.reflect.*;

import util.models.Hashcodetable;

import bus.uigen.introspect.ClassDescriptorCache;
import bus.uigen.introspect.ClassDescriptorInterface;
import bus.uigen.introspect.IntrospectUtility;

public class DelegatedReplicatedObject extends ReplicatedCollection implements
		Delegated, PropertyChangeListener/* , Observer */
{
	private ReplicatedDictionary replProperties;
	// private transient Hashtable<Replicated, String> replicatedToProperty;
	private transient Hashcodetable<Replicated, String> replicatedToProperty;
	protected static ObjectMergeMatrix class_merge_matrix = new ObjectMergeMatrix();
	protected ObjectMergeMatrix inst_merge_matrix = null;

	static final long serialVersionUID = 7037530784112600988L;

	private Object obj;
	transient ClassDescriptorInterface cd;
	String propertyName = "";

	public void setPropertyName(String newVal) {
		propertyName = newVal;

	}

	String parentClassName;

	transient ClassDescriptorInterface parentCd;

	public void setParentClassName(String newVal) {
		parentClassName = newVal;
		try {
			parentCd = ClassDescriptorCache.getClassDescriptor(Class
					.forName(parentClassName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DelegatedReplicatedObject(Object o) {
		super();
		obj = o;
		cd = ClassDescriptorCache.getClassDescriptor(o.getClass());
		isConsistent = true;
		// treeInitialized = true;
		Sync.delegatedTable.put(o, this);
		initIntrospection(obj);

		replProperties = new ReplicatedDictionary();
		replProperties.setParent(this);
		replicatedToProperty = new Hashcodetable();
		update();
	}

	transient boolean registered = false;

	public void registerAsListener() {
		// obj.addPropertyChangeListener(this);
		if (registered)
			return;
		if (isAtomic())
			return;
		if (Sync.getSyncClient() == null)
			return;
		if (null != obj) {
			// uiBean.invokeInitSerializedObject(obj);
			Method m = DelegatedUtils.getAddPropertyChangeListenerMethod(obj
					.getClass());

			if (null != m) {
				Object[] args = new Object[1];
				args[0] = this;
				try {
					m.invoke(obj, args);
					registered = true;
				} catch (Exception e) {
					System.out.println(e.toString());
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void copyBack(Change change) {
		copyBack();
	}

	String toReadMethodName(String s) {
		return "get" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("this"))
			return;
		String propName = toReadMethodName(evt.getPropertyName());
		/*
		 * String realPropName = propertyName(propName); Boolean shareOut =
		 * (Boolean) cd.getPropertyAttribute(realPropName,
		 * SyncAttributeNames.REPLICATE_OUT); if (shareOut != null &&
		 * !shareOut.booleanValue()) return;
		 */
		// Replicated oldVal = replProperties.get("get" + propName);
		Replicated oldVal = replProperties.get(propName);
		if (!DelegatedUtils.shareOut(oldVal, cd, propName))
			return;
		// if (cd != null && !DelegatedUtils.shareOut(cd, propName)) return;
		if (null != evt.getOldValue()) {
			if (evt.getNewValue() == evt.getOldValue())
				return;
			else {

				update(propName);

			}

		} else {
			String readMethodName = toReadMethodName(evt.getPropertyName());
			Replicated repl = (Replicated) replProperties
			.get(readMethodName);
//			Replicated repl = (Replicated) replProperties
//					.get(toReadMethodName(evt.getPropertyName()));
			/*
			 * if (repl instanceof Delegated) { if
			 * (evt.getNewValue().equals(((Delegated) repl).returnObject()))
			 * return; else update(propName); } else update(propName);
			 */
			if (repl instanceof Delegated) {
				if (evt.getNewValue().equals(((Delegated) repl).returnObject()))
					return;
				//update(propName);
				update(propName, evt.getNewValue(),  repl);
			} else {
				if (repl != null && repl.equals(evt.getNewValue()))
					return;
				//update(propName);
				update(propName, evt.getNewValue(), repl);
			}
		}
		// System.out.println("In DelegatedReplicatedObject, propertyChange() called");
		// setChanged();
		/*
		 * if ( null != evt.getOldValue() ) if (evt.getNewValue() !=
		 * evt.getOldValue()) setChanged(); else return; else { Replicated repl
		 * = (Replicated)
		 * replProperties.get(toReadMethodName(evt.getPropertyName())); if (repl
		 * instanceof Delegated) { if (evt.getNewValue() != ((Delegated)
		 * repl).returnObject()) setChanged(); else return; } else return;
		 */
	}

	public Object returnObject() {
		return obj;
	}

	public void setObject(Object newObj) {
		obj = newObj;
		registerAsListener();
	}

	public void update(String prop_name, Object prop_obj, Replicated repl) {
//		String prop_name = "get" + Character.toUpperCase(propName.charAt(0)) + 
//		propName.substring(1);
		

//		Replicated repl = (Replicated) replProperties
//				.get(prop_name);
		if (repl != null) {			
//			Serializable o = (Serializable) readMethod.invoke(obj,
//					null);
			Serializable o = (Serializable) prop_obj;
			if (repl instanceof ReplicatedEnum) {
				ReplicatedEnum replAtomic = (ReplicatedEnum) repl;
				if (!o.equals(replAtomic.getValue())) {
					// System.out.println("Value of " + prop_name +
					// "changed");
					// if (replAtomic instanceof ReplicatedEnum)
					((ReplicatedEnum) replAtomic).setEnumValue(o);
					// else
					// replAtomic.setValue(o);

				} else {
					// System.out.println("Value of " + prop_name +
					// " not changed");
				}
			} else if (repl instanceof ReplicatedAtomic) {
				ReplicatedAtomic replAtomic = (ReplicatedAtomic) repl;
				if (!o.equals(replAtomic.getValue())) {
					// System.out.println("Value of " + prop_name +
					// "changed");
					/*
					 * if (replAtomic instanceof ReplicatedEnum)
					 * ((ReplicatedEnum)replAtomic).setEnumValue(o);
					 * else
					 */
					replAtomic.setValue(o);

				} else {
					// System.out.println("Value of " + prop_name +
					// " not changed");
				}
			}
			if (repl instanceof Delegated) {
				((Delegated) repl).update();
			}
		} else {
			// System.out.println("prop_obj.class is " +
			// prop_obj.getClass());
			Replicated replObj = DelegatedUtils
					.convertObject(prop_obj);
			replProperties.put(prop_name, replObj);

			replicatedToProperty.put(replObj, prop_name);
			replObj.myAddObserver(this);
			if (replObj instanceof Delegated) {
				((Delegated) replObj).setPropertyName(prop_name);
				((Delegated) replObj).setParentClassName(obj
						.getClass().getName());
			}
		}
		return;
	}

	

	public void update(String propertyName) {
		if (propertyName.equals("getAnnotations"))
			return;
		// System.out.println("Update in DRO called");
		// Class clss = obj.getClass();

		try {
			/*
			 * BeanInfo beanInfo = Introspector.getBeanInfo(clss);
			 * PropertyDescriptor[] properties_arr =
			 * beanInfo.getPropertyDescriptors();
			 */

			for (int i = 0; i < properties_arr.length; i++) {
				Method readMethod = properties_arr[i].getReadMethod();
				Method writeMethod = properties_arr[i].getWriteMethod();

				if ((readMethod != null) /* || */&& (writeMethod != null)) {
					String prop_name = readMethod.getName();
					if (!prop_name.equals(propertyName))
						continue;
					Class prop_class = readMethod.getReturnType();
					String prop_class_name = prop_class.getName();
					Object prop_obj = readMethod.invoke(obj, null);

					// System.out.println("Property class name is: " +
					// prop_class_name);

					Replicated repl = (Replicated) replProperties
							.get(prop_name);
					if (repl != null) {
						Serializable o = (Serializable) readMethod.invoke(obj,
								null);
						if (repl instanceof ReplicatedEnum) {
							ReplicatedEnum replAtomic = (ReplicatedEnum) repl;
							if (!o.equals(replAtomic.getValue())) {
								// System.out.println("Value of " + prop_name +
								// "changed");
								// if (replAtomic instanceof ReplicatedEnum)
								((ReplicatedEnum) replAtomic).setEnumValue(o);
								// else
								// replAtomic.setValue(o);

							} else {
								// System.out.println("Value of " + prop_name +
								// " not changed");
							}
						} else if (repl instanceof ReplicatedAtomic) {
							ReplicatedAtomic replAtomic = (ReplicatedAtomic) repl;
							if (!o.equals(replAtomic.getValue())) {
								// System.out.println("Value of " + prop_name +
								// "changed");
								/*
								 * if (replAtomic instanceof ReplicatedEnum)
								 * ((ReplicatedEnum)replAtomic).setEnumValue(o);
								 * else
								 */
								replAtomic.setValue(o);

							} else {
								// System.out.println("Value of " + prop_name +
								// " not changed");
							}
						}
						if (repl instanceof Delegated) {
							((Delegated) repl).update();
						}
					} else {
						// System.out.println("prop_obj.class is " +
						// prop_obj.getClass());
						Replicated replObj = DelegatedUtils
								.convertObject(prop_obj);
						replProperties.put(prop_name, replObj);

						replicatedToProperty.put(replObj, prop_name);
						replObj.myAddObserver(this);
						if (replObj instanceof Delegated) {
							((Delegated) replObj).setPropertyName(prop_name);
							((Delegated) replObj).setParentClassName(obj
									.getClass().getName());
						}
					}
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}

	public Replicated getPropertyObject(String name) {
		return (Replicated) replProperties.get("get" + name);
	}

	public void update() {
		if (obj == null)
			return;
		initIntrospection(obj);
		// System.out.println("Update in DRO called");
		// Class clss = obj.getClass();

		try {
			// BeanInfo beanInfo = Introspector.getBeanInfo(clss);
			// PropertyDescriptor[] properties_arr =
			// beanInfo.getPropertyDescriptors();

			for (int i = 0; i < properties_arr.length; i++) {
				Method readMethod = properties_arr[i].getReadMethod();
				Method writeMethod = properties_arr[i].getWriteMethod();

				if ((readMethod != null) && (writeMethod != null)) {
					String prop_name = readMethod.getName();
					Class prop_class = readMethod.getReturnType();
					String prop_class_name = prop_class.getName();
					Object prop_obj = readMethod.invoke(obj, null);

					// System.out.println("Property class name is: " +
					// prop_class_name);

					Replicated repl = (Replicated) replProperties
							.get(prop_name);
					if (repl != null) {
						Serializable o = (Serializable) readMethod.invoke(obj,
								null);
						if (repl instanceof ReplicatedEnum) {
							ReplicatedEnum replAtomic = (ReplicatedEnum) repl;
							if (!o.equals(replAtomic.getEnumValue())) {
								// System.out.println("Value of " + prop_name +
								// "changed");
								// if (replAtomic instanceof ReplicatedEnum)
								((ReplicatedEnum) replAtomic).setEnumValue(o);

							} else {
								// System.out.println("Value of " + prop_name +
								// " not changed");
							}
						} else if (repl instanceof ReplicatedAtomic) {
							ReplicatedAtomic replAtomic = (ReplicatedAtomic) repl;
							if (!o.equals(replAtomic.getValue())) {
								// System.out.println("Value of " + prop_name +
								// "changed");
								/*
								 * if (replAtomic instanceof ReplicatedEnum)
								 * ((ReplicatedEnum)replAtomic).setEnumValue(o);
								 * else
								 */
								replAtomic.setValue(o);
							} else {
								// System.out.println("Value of " + prop_name +
								// " not changed");
							}
						}
						if (repl instanceof Delegated) {
							((Delegated) repl).update();
						}
					} else {
						// System.out.println("prop_obj.class is " +
						// prop_obj.getClass());
						Replicated replObj = null;
						if (prop_obj != null) {
							/* Replicated */replObj = DelegatedUtils
									.convertObject(prop_obj);

							replProperties.put(prop_name, replObj);
							replicatedToProperty.put(replObj, prop_name);
							if (replObj instanceof Delegated) {
								((Delegated) replObj)
										.setPropertyName(prop_name);
								((Delegated) replObj).setParentClassName(obj
										.getClass().getName());
							}
							replObj.myAddObserver(this);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.err.println(e);
		}
	}

	public Object getProperty(String name) {
		try {
			// Class clss = obj.getClass();
			// BeanInfo beanInfo = Introspector.getBeanInfo(clss);
			// PropertyDescriptor[] properties_arr =
			// beanInfo.getPropertyDescriptors();
			initIntrospection(obj);
			for (int i = 0; i < properties_arr.length; i++) {
				if (name.equals(properties_arr[i].getName())) {
					Method readMethod = properties_arr[i].getReadMethod();
					return readMethod.invoke(obj, null);
				}
			}
		} catch (Exception e) {
			System.out.println("Could not read property:" + name);
			e.printStackTrace();
		}
		return null;
	}

	transient boolean isConsistent = false;

	boolean isAtomic() {
		return replProperties.size() == 0;
	}

	public boolean shared(String getterName) {
		return DelegatedUtils.share(parentCd, getterName);
	}

	public void makeSerializedObjectConsistent() {

		if (isConsistent)
			return;
		isConsistent = true;
		if (Sync.getSyncClient() != null) {
			parentCd = DelegatedUtils.getParentCd(parentClassName);
			/*
			 * try { parentCd =
			 * ClassDescriptorCache.getClassDescriptor(Class.forName
			 * (parentClassName)); } catch (Exception e) { e.printStackTrace();
			 * }
			 */
			cd = ClassDescriptorCache.getClassDescriptor(returnObject()
					.getClass());
		}
		if (isAtomic())
			return;
		// if (this instanceof ReplicatedAtomic) return;
		Sync.delegatedTable.put(returnObject(), this);
		// moving it down so I it is post order
		// IntrospectUtility.invokeInitSerializedObject(returnObject());
		// registerAsListener();

		// System.out.println("Make serialized object consistent");
		initIntrospection(obj);
		// Class clss = obj.getClass();
		if (replicatedToProperty == null)
			replicatedToProperty = new Hashcodetable();

		try {
			// BeanInfo beanInfo = Introspector.getBeanInfo(clss);
			// PropertyDescriptor[] properties_arr =
			// beanInfo.getPropertyDescriptors();

			for (int i = 0; i < properties_arr.length; i++) {
				Method readMethod = properties_arr[i].getReadMethod();
				Method writeMethod = properties_arr[i].getWriteMethod();

				if ((readMethod != null) && (writeMethod != null)) {

					String prop_name = readMethod.getName();
					if (!DelegatedUtils.shareIn(cd, prop_name))
						;
					Class prop_class = readMethod.getReturnType();
					String prop_class_name = prop_class.getName();
					Object prop_obj = readMethod.invoke(obj, null);
					if (prop_obj != null) {
						Replicated repl = (Replicated) replProperties
								.get(prop_name);
						replicatedToProperty.put(repl, prop_name);
						repl.myAddObserver(this);

						if (repl instanceof Delegated) {

							Delegated delegated = (Delegated) repl;
							// need this here, would need to change it if
							// returnObject changed
							delegated.makeSerializedObjectConsistent();

							// if (prop_obj.equals(delegated.returnObject()))
							// prop_obj will always be later
							if (prop_obj == delegated.returnObject())
								// return;
								continue;
							if (Sync.getTrace())
								if (prop_obj != delegated.returnObject())
									System.out.println("Property Object:"
											+ System.identityHashCode(prop_obj)
											+ " "
											+ System.identityHashCode(delegated
													.returnObject()));
							// System.out.println("INCONSISTENT DELEGATOR");
							// could we not change returnObject? woild have to
							// make new object consistent
							Object[] args = { delegated.returnObject() };
							if (writeMethod != null) {
								writeMethod.invoke(obj, args);

							}
							// cannot be here
							// delegated.makeSerializedObjectConsistent();
							// delegated.setObject(prop_obj);
						}
						/*
						 * ((Delegated)repl).update(); } } else{
						 * System.out.println("prop_obj.class is " +
						 * prop_obj.getClass()); Replicated replObj =
						 * DelegatedUtils.convertObject(prop_obj);
						 * replProperties.put(prop_name, replObj); }
						 */
					}
				}

			}
			IntrospectUtility.invokeInitSerializedObject(returnObject());
			registerAsListener();
			isConsistent = true;

		} catch (Exception e) {
			e.printStackTrace();
			// System.err.println(e);

		}
	}

	/*
	 * transient boolean treeInitialized = false; public void
	 * initalizeSerializedTree() { if (treeInitialized) return; treeInitialized
	 * = true; uiBean.invokeInitSerializedObject(this.returnObject());
	 * registerAsListener(); //System.out.println("Make serialized object
	 * consistent"); initIntrospection(obj); //Class clss = obj.getClass();
	 * 
	 * try{ //BeanInfo beanInfo = Introspector.getBeanInfo(clss);
	 * //PropertyDescriptor[] properties_arr =
	 * beanInfo.getPropertyDescriptors(); Enumeration propEnum =
	 * replProperties.elements(); while (propEnum.hasMoreElements()) { String
	 * nextName = (String) propEnum.nextElement(); Replicated repl =
	 * (Replicated)replProperties.get(nextName);
	 * 
	 * if (repl instanceof Delegated){ Delegated delegated = (Delegated) repl;
	 * delegated.initalizeSerializedTree(); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * } catch (Exception e){ System.err.println(e); } }
	 */

	transient Class clss;
	transient BeanInfo beanInfo;
	transient PropertyDescriptor[] properties_arr;

	void initIntrospection(Object obj) {
		if (clss != null)
			return;
		clss = obj.getClass();

		try {
			beanInfo = Introspector.getBeanInfo(clss);
			properties_arr = beanInfo.getPropertyDescriptors();
			// uiBean.invokeInitSerializedObject(obj);
		} catch (Exception e) {
			System.out.println("DelegatedReplicatedObject:" + e);
			e.printStackTrace();
		}
	}

	Method getReadMethod(String propName) {
		for (int i = 0; i < properties_arr.length; i++) {
			Method readMethod = properties_arr[i].getReadMethod();
			if (readMethod.getName().equals(propName))
				return readMethod;
		}
		return null;
	}

	Method getWriteMethod(String propName) {
		for (int i = 0; i < properties_arr.length; i++) {
			Method readMethod = properties_arr[i].getReadMethod();
			Method writeMethod = properties_arr[i].getWriteMethod();
			if (readMethod.getName().equals(propName))
				return writeMethod;
		}
		return null;
	}

	public void copyBack() {
		this.makeSerializedObjectConsistent();
		// System.out.println("DRO: copying back into Delegator " + obj);
		initIntrospection(obj);
		IntrospectUtility.invokeInitSerializedObject(obj);
		System.out.println("copying back " + obj + "delegated " + this + "id"
				+ this.getObjectID());
		// Class clss = obj.getClass();

		try {
			// BeanInfo beanInfo = Introspector.getBeanInfo(clss);
			// PropertyDescriptor[] properties_arr =
			// beanInfo.getPropertyDescriptors();

			for (int i = 0; i < properties_arr.length; i++) {
				Method readMethod = properties_arr[i].getReadMethod();
				Method writeMethod = properties_arr[i].getWriteMethod();

				if ((readMethod != null) && (writeMethod != null)) {
					String prop_name = readMethod.getName();

					Replicated repl = (Replicated) replProperties
							.get(prop_name);
					if (repl instanceof ReplicatedEnum) {
						ReplicatedEnum replAtomic = (ReplicatedEnum) repl;
						if (!replAtomic.getEnumValue().equals(
								readMethod.invoke(obj, null))) {
							Object[] args = new Object[1];
							args[0] = replAtomic.getEnumValue();
							writeMethod.invoke(obj, args);
						}
					} else if (repl instanceof ReplicatedAtomic) {
						ReplicatedAtomic replAtomic = (ReplicatedAtomic) repl;
						if (!replAtomic.getValue().equals(
								readMethod.invoke(obj, null))) {
							Object[] args = new Object[1];
							args[0] = replAtomic.getValue();
							writeMethod.invoke(obj, args);
						}
					}
					/*
					 * 
					 * if (repl instanceof Delegated){
					 * ((Delegated)repl).copyBack(); }
					 */
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}

	/*
	 * In the nested class FieldEnumeration, "this" of ReplicatedRecord is
	 * required however, "this" corresponds to FieldEnumeration when called
	 * within it that's why I need this function
	 */
	private DelegatedReplicatedObject getthis() {
		return this;
	}

	private class FeatureEnumeration implements Enumeration {

		int i;

		// boolean flag; // Whether field or property should be enumerated
		FeatureEnumeration() {
			i = 0;
			// flag = false;
			// initializeReplicatedFields();

			// PD: commenting out this update
			// update();
		}

		public Object nextElement() {
			switch (i) {
			case 0: {
				i++;
				// return replFields;
				return replProperties;
			}
				/*
				 * case 1: { i++; return replProperties; }
				 */
			default: {
				return null;
			}
			}
		}

		public boolean hasMoreElements() {
			return i < 1;
		}
	}

	public Enumeration elements() {
		// return fields.elements();
		return new FeatureEnumeration();
	}

	public Change getChange() {
		// System.out.println("Within DelegatedReplicatedObject.getChange()");
		// initializeReplicatedFields();

		// PD: not sure we want to call this each time
		// update();

		// if (!hasChanged()) return null;

		GenericChangeSet changes = new GenericChangeSet(getObjectID(), 5);

		try {
			if (replProperties.hasChanged()) {
				Change properties_change = replProperties.getChange();
				if (properties_change != null) {
					changes.addChange(new ModifyChange(getObjectID(),
							new String("properties"), properties_change));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return (!changes.isEmpty()) ? changes : null;
	}

	transient boolean waitingForChanges;

	/*
	 * public void update (Observable o, Object arg) { if (!waitingForChanges)
	 * return; //if (!(o instanceof Delegated)) return; copyBack ((Replicated)
	 * o);
	 * 
	 * }
	 */
	public void update(Replicated o, Object arg) {
		if (!waitingForChanges)
			return;
		// if (!(o instanceof Delegated)) return;
		copyBack((Replicated) o);

	}

	void copyBack(Replicated repl) {
		String propName = replicatedToProperty.get(repl);
		/*
		 * String realPropName = propertyName(propName); Boolean shareIn =
		 * (Boolean) cd.getPropertyAttribute(realPropName,
		 * SyncAttributeNames.REPLICATE_IN); if (shareIn != null &&
		 * !shareIn.booleanValue()) return;
		 */
		if (cd != null && !DelegatedUtils.shareIn(cd, propName))
			return;
		if (propName == null)
			return;
		Replicated prop_obj = replProperties.get(propName);
		if (prop_obj == null)
			return;
		Method writeMethod = getWriteMethod(propName);
		try {
			if (repl instanceof Delegated) {
				Delegated delegated = (Delegated) repl;
				if (((Delegated) prop_obj).returnObject().equals(
						delegated.returnObject()))
					return;
				// System.out.println("INCONSISTENT DELEGATOR");

				Object[] args = { delegated.returnObject() };
				writeMethod.invoke(obj, args);
				// delegated.makeSerializedObjectConsistent();
				// delegated.setObject(prop_obj);
			} else if (repl instanceof ReplicatedEnum) {
				ReplicatedEnum replAtomic = (ReplicatedEnum) repl;
				// if (!replAtomic.getValue().equals(readMethod.invoke(obj,
				// null))){
				Object[] args = new Object[1];
				args[0] = replAtomic.getEnumValue();
				if (writeMethod != null)
					writeMethod.invoke(obj, args);
				// }
			} else if (repl instanceof ReplicatedAtomic) {
				ReplicatedAtomic replAtomic = (ReplicatedAtomic) repl;
				// if (!replAtomic.getValue().equals(readMethod.invoke(obj,
				// null))){
				Object[] args = new Object[1];
				args[0] = replAtomic.getValue();
				if (writeMethod != null)
					writeMethod.invoke(obj, args);
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Change applyChange(Change change) throws ReplicationException {
		// Synchronize the hashtables for fields and properties
		// System.out.println("DRO apply change" + change);
		// if (Sync.getSyncClient() != null)
		waitingForChanges = true;
		Change ch = super.applyChange(change);
		// Now, just use the synchronized hashtables set fields and
		// properties to correct objects

		// setReplicatedFields();

		// removing copy back as not clear what purpose it serves.

		// copyBack();
		waitingForChanges = false;
		// copyBack(change);
		// System.out.println("DRO after copy back");

		return ch;
	}

	public void clearChangeSet() {
		// PD: adding this
		// replProperties.clearChangeSet();

	}

	public ElementChange concatElementChanges(ElementChange first,
			ElementChange second) throws ReplicationException {
		if (first instanceof ModifyChange) {
			if (second instanceof ModifyChange) {
				String name = (String) ((ModifyChange) first).identifier();
				Replicated elt;

				// if (name.equals(new String("fields"))){
				// elt = fields;
				// }
				// else{
				elt = replProperties;
				// }

				Change first_change = ((ModifyChange) first).change;
				Change second_change = ((ModifyChange) second).change;
				Change concat_changes = elt.concatChanges(first_change,
						second_change);
				return new ModifyChange(getObjectID(), name, concat_changes);
			} else
				return first;
		} else {
			return second;
		}
	}

	public ChangeSet newChangeSet(int size) {
		return new GenericChangeSet(getObjectID(), size);
	}

	public ChangePair newChangeSetPair(ChangeSet cs0, ChangeSet csr) {
		ChangeSet A0 = new GenericChangeSet(getObjectID(), csr.size());
		ChangeSet Ar = new GenericChangeSet(getObjectID(), cs0.size());
		return new ChangePair(A0, Ar, false);
	}

	public MergeMatrix getClassMergeMatrix() {
		return class_merge_matrix;
	}

	public MergeMatrix getInstanceMergeMatrix() {
		return inst_merge_matrix;
	}

	public void setInstanceMergeMatrix(MergeMatrix mm) {
		inst_merge_matrix = (ObjectMergeMatrix) mm;
	}

	public boolean instanceMergeMatrixExists() {
		return inst_merge_matrix != null;
	}

	public String toString() {
		String retVal = "";
		Enumeration keys = replProperties.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object val = replProperties.get(key);
			retVal = retVal + "(" + key + "," + val + ")";
		}
		return retVal;
	}
}
/*
 * public TreeNode getChildAt(int childIndex) { Object child =
 * fields[childIndex].get(this); String name = fields[childIndex].getName(); if
 * (child instanceof Replicated) { ((Replicated) child).setName(name) return
 * (Replicated) child; } else { return new ObjectTreeNode(name, this); } return
 * (TreeNode) ; }
 * 
 * public int getChildCount() { return fields.length; }
 * 
 * public int getIndex(TreeNode node) { int i = 0; for (Enumeration e =
 * elements(); e.hasMoreElements(); ) { if (node.equals(e.nextElement())) return
 * i; i++; } return -1; }
 * 
 * public String getElementName(Object object) { for (int i = 0; i <
 * fields.length; i++) { if (object.equals(fields[i].get(this)) return "." +
 * fields[i].getName(); } return "."; }
 */

