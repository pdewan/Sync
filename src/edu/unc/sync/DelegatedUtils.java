package edu.unc.sync;

import bus.uigen.*;
import bus.uigen.introspect.ClassDescriptorCache;
import bus.uigen.introspect.ClassDescriptorInterface;
import bus.uigen.introspect.IntrospectUtility;

import java.lang.*;
import java.lang.reflect.*;
import java.beans.*;
import java.util.*;

import util.models.Hashcodetable;
import util.models.HashtableListener;
import util.models.VectorMethodsListener;

public class DelegatedUtils
{
	//public static Hashtable delegatedHashtable = new Hashtable();

	// this class is not to be initialized, hence the private constructor
	// use only static methods
	private DelegatedUtils(){
	}

	public static boolean isAddPropertyChangeListenerMethod(Method method) {
		String name = method.getName();
		if (name.startsWith("addPropertyChangeListener")) {
			Class[] params = method.getParameterTypes();
			if (params.length == 1 &&
				PropertyChangeListener.class.isAssignableFrom(params[0]))
				return true;
			else
				return false;
		}
		return false;
	}

	public static Method getAddPropertyChangeListenerMethod(Class c) {
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++)
			if (isAddPropertyChangeListenerMethod(methods[i])) return methods[i];
		return null;
	}

	public static boolean isAddVectorMethodsListenerMethod(Method method) {
		String name = method.getName();
		if (name.startsWith("addVectorMethodsListener")) {
			Class[] params = method.getParameterTypes();
			if (params.length == 1 &&
				VectorMethodsListener.class.isAssignableFrom(params[0]))
				return true;
			else
				return false;
		}
		return false;
	}

	public static Method getAddVectorMethodsListenerMethod(Class c) {
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++)
			if (isAddVectorMethodsListenerMethod(methods[i])) return methods[i];
		return null;
	}

	public static boolean isAddHashtableListenerMethod(Method method) {
		String name = method.getName();
		if (name.startsWith("addHashtableListener")) {
			Class[] params = method.getParameterTypes();
			if (params.length == 1 &&
				HashtableListener.class.isAssignableFrom(params[0]))
				return true;
			else
				return false;
		}
		return false;
	}

	public static Method getAddHashtableListenerMethod(Class c) {
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++)
			if (isAddHashtableListenerMethod(methods[i])) return methods[i];
		return null;
	}

	public static boolean isDelegationObject(Class c){
		if (null == c)
			return false;
		else
			return true;
		//(getAddPropertyChangeListenerMethod(c) != null);
	}

	public static boolean isDelegationObject(Object o){
		if (null == o)
			return false;
		else
			return isDelegationObject(o.getClass());
	}

	public static boolean isDelegationVector(Class c){
		if (null == c)
			return false;
		else
			return
				//(getAddVectorMethodsListenerMethod(c) != null) &&
				(IntrospectUtility.getAddElementMethod(c) != null) &&
				 //(uiBean.getInsertElementAtMethod(c) != null) &&
				 //(uiBean.getRemoveElementMethod(c) != null) &&
				 //(uiBean.getRemoveElementAtMethod(c) != null) &&
				 //(uiBean.getSetElementAtMethod(c) != null) &&
				 (IntrospectUtility.getSizeMethod(c) != null) &&
				 //(uiBean.getElementsMethod(c) != null));
		(IntrospectUtility.getElementAtMethod(c, false) != null);
		//(uiBean.getIndexOfMethod(c) != null));
	}

	public static boolean isDelegationVector(Object o){
		if (null == o)
			return false;
		else
			return isDelegationVector(o.getClass());
	}

	public static boolean isDelegationHashtable(Class c){
		if (null == c)
			return false;
		else
			return
				//(getAddHashtableListenerMethod(c) != null) &&
				((IntrospectUtility.getGetMethod(c) != null) &&
				 (IntrospectUtility.getPutMethod(c) != null) &&
				 //(uiBean.getRemoveMethod(c) != null) &&
				 //(uiBean.getSizeMethod(c) != null) &&
				 //(uiBean.getElementsMethod(c) != null) &&
				 (IntrospectUtility.getKeysMethod(c) != null));
	}

	public static boolean isDelegationHashtable(Object o){
		if (null == o)
			return false;
		else
			return isDelegationHashtable(o.getClass());
	}
	public static Object convertReplicated (Replicated replicated) {
		if (replicated instanceof Delegated){
			return((Delegated)replicated).returnObject();
		}
		else if (replicated instanceof ReplicatedAtomic)
				return ((ReplicatedAtomic)replicated).getValue();
		else
				return null;
		
	}
	
	public static Replicated convertObject(int i){
		return new ReplicatedInteger(i);
	}
	public static Replicated convertObject(short i){
		return new ReplicatedShort(i);
	}
	public static Replicated convertObject(long i){
		return new ReplicatedLong(i);
	}
	public static Replicated convertObject(char i){
		return new ReplicatedCharacter(i);
	}
	public static Replicated convertObject(boolean i){
		return new ReplicatedBoolean(i);
	}
	public static Replicated convertObject(double i){
		return new ReplicatedDouble(i);
	}

	public static Replicated convertObject(float x){
		return new ReplicatedFloat(x);
	}
        public static Replicated convertObject(Object o){
        	if (o == null) return null;
        	if (o.getClass().isEnum()) return new ReplicatedEnum(o);
        	if (o instanceof Replicated) return (Replicated) o;
        	//if (o == null) return null;
          //System.out.println("Converting object" + o);
          boolean oldMode = Sync.getSyncMode();
          Sync.setSyncMode(false);
          Replicated retVal = convertObjectHelper(o);
          Sync.setSyncMode(oldMode);
          //System.out.println("Converted object to "+ retVal);
          return retVal;
        }
        public static boolean shareIn (ClassDescriptorInterface cd, String getterName) {
        	if (cd == null) return true;
    		String realPropName = propertyName(getterName);
    		Boolean shareIn = (Boolean) cd.getPropertyAttribute(realPropName, SyncAttributeNames.REPLICATE_IN);
    		if (shareIn == null) return true;
    		return shareIn.booleanValue();
    	}
        public static boolean share (ClassDescriptorInterface cd, String getterName) {
        	return shareIn(cd, getterName) && shareOut(cd, getterName);
    	}
    	public static boolean shareOut (ClassDescriptorInterface cd, String getterName) {
    		if (cd == null) return true;
    		if (getterName == null || !getterName.startsWith("get")) return true;
    		String realPropName = propertyName(getterName);
    		Boolean shareIn = (Boolean) cd.getPropertyAttribute(realPropName, SyncAttributeNames.REPLICATE_OUT);
    		if (shareIn == null) return true;
    		return shareIn.booleanValue();
    	}
    	public static Object getAttribute (Replicated o, ClassDescriptorInterface cd, String propName, String attribute) {
    		if (o == null) return null;
    		Object val = o.getAttribute(attribute);
    		if (val != null) return val;
    		if (cd == null) return null;
    		if (propName == null) return null;
    		return cd.getPropertyAttribute(propName, attribute);  		
    		
    	}
    	public static boolean shareOut (Replicated o, ClassDescriptorInterface cd, String getterName) {
    		if (getterName == null || !getterName.startsWith("get")) return true;
    		Object val = getAttribute (o, cd, propertyName(getterName),SyncAttributeNames.REPLICATE_OUT);
    		if (val == null) return true;
    		else return (Boolean) val;
    		
    		
    	}
    	public static boolean insertOut (Replicated o, ClassDescriptorInterface cd, String getterName) {
    		Object val = getAttribute (o, cd, propertyName(getterName),SyncAttributeNames.INSERT_OUT);
    		if (val == null) return true;
    		else return (Boolean) val;
    		
    		
    	}
    	public static boolean addOut (Replicated o, ClassDescriptorInterface cd, String getterName) {
    		Object val = getAttribute (o, cd, propertyName(getterName),SyncAttributeNames.ADD_OUT);
    		if (val == null) return true;
    		else return (Boolean) val;
    		
    		
    	}
    	public static boolean deleteOut (Replicated o, ClassDescriptorInterface cd, String getterName) {
    		Object val = getAttribute (o, cd, propertyName(getterName),SyncAttributeNames.DELETE_OUT);
    		if (val == null) return true;
    		else return (Boolean) val;    		
    		
    	}
    	public static boolean modifyOut (Replicated o, ClassDescriptorInterface cd, String getterName) {
    		Object val = getAttribute (o, cd, propertyName(getterName),SyncAttributeNames.MODIFY_OUT);
    		if (val == null) return true;
    		else return (Boolean) val;    		
    		
    	}
    	public static boolean putOut (Replicated o, ClassDescriptorInterface cd, String getterName) {
    		Object val = getAttribute (o, cd, propertyName(getterName),SyncAttributeNames.PUT_OUT);
    		if (val == null) return true;
    		else return (Boolean) val;    		
    		
    	}
    	public static String propertyName (String getter) {
    		if (getter.startsWith("get"))
    			return Character.toLowerCase(getter.charAt(3)) + getter.substring(4);
    		else return null;
        }
    	public static ClassDescriptorInterface getParentCd (String parentClassName) {
    		if (parentClassName == null) return null;
    		try {
          		 return ClassDescriptorCache.getClassDescriptor(Class.forName(parentClassName));
          		 } catch (Exception e) {
          			 if (Sync.getSyncClient() != null)
          			 e.printStackTrace();

          			 return null;
          		 }
    		
    	}
	public static Replicated convertObjectHelper(Object o){
		if (o instanceof Replicated) return (Replicated) o;
                if (o == null) return new DelegatedReplicatedObject(null);
                
		if (o instanceof Integer){
			return new ReplicatedInteger((Integer)o);
		}
		if (o instanceof Boolean){
			return new ReplicatedBoolean((Boolean)o);
		}

		if (o instanceof Float){
			return new ReplicatedFloat((Float)o);
		}
		if (o instanceof Double){
			return new ReplicatedDouble((Float)o);
		}
		if (o instanceof String){
			return new ReplicatedString((String)o);
		}
		if (o instanceof Short){
			return new ReplicatedShort((Short)o);
		}
		if (o instanceof Long){
			return new ReplicatedLong((Long)o);
		}
		if (o instanceof Character){
			return new ReplicatedCharacter((Character)o);
		}

		Object value = null;
                /*
                Hashtable test = new Hashtable();
                Character ch1 = new Character('l');
                test.put(ch1, "firstput");
                System.out.println(test.get(ch1));
                Character ch2 = new Character('l');
                System.out.println(test.get(ch2));
                */
                // look at the code above for this special case
                if (!(o instanceof Character))
                  value = findDelegate(o);
		if (null != value){
			return (Replicated) value;
		}
		else{
			Replicated repl;
			if (isDelegationVector(o)){
				repl = new DelegatedReplicatedSequence(o);
                                ((Delegated) repl).registerAsListener();                                
				//Sync.delegatedTable.put(o, repl);
				return repl;
			}

			if (isDelegationHashtable(o)){
				repl = new DelegatedReplicatedDictionary(o);
                                ((Delegated) repl).registerAsListener();
				//Sync.delegatedTable.put(o, repl);
				return repl;
			}

			// Important to do this test last, because it does not
			// check for anything
			if (isDelegationObject(o)){
				repl = new DelegatedReplicatedObject(o);
                                if (repl == null) {
                                  System.out.println("NUll Delegated Replica");
                                } else
                                ((Delegated) repl).registerAsListener();
				//Sync.delegatedTable.put(o, repl);
				System.out.println("Associatng object " + o + " replicated" + repl + "id " + repl.getObjectID());
				return repl;
			}
		}

		return null;
	}

	public static Delegated findDelegate(Object o){
	  Hashcodetable objects = Sync.delegatedTable;
	  return (Delegated) (objects.get(o));
	  /*
	  for (Enumeration e=objects.elements(); e.hasMoreElements();){
	    Replicated repl = (Replicated) e.nextElement();
	    if (repl instanceof Delegated){
	      Delegated del = (Delegated) repl;
	      if (o == del.returnObject()){
	        return del;
	      }
	      else return null;
	    }
	    else return null;
	  }
	  return null;
	  */
	}
}
