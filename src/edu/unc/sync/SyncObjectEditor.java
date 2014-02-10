package edu.unc.sync;import bus.uigen.*;import bus.uigen.undo.ListeningUndoer;import bus.uigen.undo.Undoer;import edu.unc.sync.*;import edu.unc.sync.server.*;
import java.util.*;
 
public class SyncObjectEditor implements UndoingSyncApplication{		SyncClient syncClient = null;
	SyncServer syncServer = null;
	private Vector<uiFrame> editors = new Vector();	Hashtable<Object, uiFrame> objectToEditor = new Hashtable();	Object b; 	
	public SyncObjectEditor() {
		//Test b = new Test();		/*
		try {
		} catch (Exception e) {e.printStackTrace();}
		uiFrame editor = uiGenerator.generateUIFrame(b,
		null);
		editor.setVisible(true);
		*/
		System.out.println("DelegationTest editor created");	}	
	public static void main(String args[]) {
		/*		DelegationTest b = new DelegationTest();
		try {
		} catch (Exception e) {e.printStackTrace();}
		uiFrame editor = uiGenerator.generateUIFrame(b);
		editor.setVisible(true);		*/
	}
	
	public void addObject(Replicated o, String name)
	{		Object test = o;		if (o instanceof Delegated) {
		Delegated obj = (Delegated) o;				//Object test = obj.returnObject();
		test = obj.returnObject();
		Sync.delegatedTable.put(test, obj);		
		System.out.println("add called with object " + test + "string" + name);				}		editObject (test);		/*
		uiFrame editor = uiGenerator.generateUIFrame(test);				editors.addElement(editor);	
		editor.setVisible(true);		*/		 
			}	public void addObject(Object model, String name)	{						try {		editObject (model);		} catch (Exception e) {			e.printStackTrace();		}		/*		uiFrame editor = uiGenerator.generateUIFrame(test);				editors.addElement(editor);			editor.setVisible(true);		*/		 			}	void editObject (Object o) {		uiFrame editor = ObjectEditor.edit(o);				editors.addElement(editor);			objectToEditor.put(o, editor);		//editor.setVisible(true);	}
		public Replicated newObject(String className, String name)
	{
		//b = new DelegationTest();
		try{
			Class clss = Class.forName(className);
			b = clss.newInstance();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Replicated repl = DelegatedUtils.convertObject((Object)b);
		//DelegatedReplicatedObject replB = new DelegatedReplicatedObject(b);
		//((Delegated)repl).registerAsListener();
		Sync.delegatedTable.put(b, repl);		
		uiFrame editor = uiGenerator.generateUIFrame(b);		editors.addElement(editor);		objectToEditor.put(b, editor);
		System.out.println("new object " +  className + " " + name); 		  
		editor.setVisible(true);
		return repl;
	}		public ListeningUndoer getUndoer() {		if (editors.size() < 1) return null;		return editors.elementAt(0).getUndoer();	}
	public void setListentingUndoer(ListeningUndoer  theUndoer){		if (editors.size() < 1) return ;		editors.elementAt(0).setUndoer(theUndoer);	}	public ListeningUndoer getUndoer(Object o) {		uiFrame editor = objectToEditor.get(o);		if (editor == null) return null;		return editor.getUndoer();	}	public void setListentingUndoer(Object o, ListeningUndoer  theUndoer){		uiFrame editor = objectToEditor.get(o);		if (editor == null) return ;		 editor.setUndoer(theUndoer);	}
	public void doRefresh()
	{
		uiFrame editor;		for (Enumeration e=editors.elements(); e.hasMoreElements();)		{			editor = (uiFrame)e.nextElement();
			try
			{
				editor.doRefresh();
			}
			catch (Exception ex)
			{
			}		}
	}
		public void init(Object invoker)
	{
		if (invoker instanceof SyncClient) {
			syncClient = (SyncClient) invoker;
			System.out.println("sync client invoked:" + invoker);
			//syncItem.setEnabled(true);
			//syncAllItem.setEnabled(true);
		} else if (invoker instanceof SyncServer) {
			syncServer = (SyncServer) invoker;
			System.out.println("SyncServer invoker:" + invoker);
			
			//syncItem.setEnabled(false);
			//syncAllItem.setEnabled(false);
		}	
	}}
