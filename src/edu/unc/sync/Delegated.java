package edu.unc.sync;

public interface Delegated
{
	public void update();
	public void copyBack();
	public Object returnObject();
	public void registerAsListener();
        public void setObject(Object newObj);
        public void makeSerializedObjectConsistent();
        //public void forceSerializedObjectConsistent();
        //public void recursivelyRegisterAsListener();
        public void copyBack(Change change);
       // public void initalizeSerializedTree();
        public void update (Replicated o, Object arg);
        public void setPropertyName(String prop_name);
        public void setParentClassName(String newVal);
}
