package examples;
import bus.uigen.*;import edu.unc.sync.*;import edu.unc.sync.server.*;
import java.util.*;
 
public class SyncObjectEditor implements SyncApplication, RT_SyncApplication{		SyncClient syncClient = null;
	SyncServer syncServer = null;
	private Vector editors = new Vector(1);	Object b; 	
	public SyncObjectEditor() {
		
		System.out.println("SyncObjectEditor created");	}	
	public static void main(String args[]) {
		
	}
	
	public void addObject(Replicated o, String name)
	{
		Delegated obj = (Delegated) o;				Object test = obj.returnObject();
		Sync.delegatedTable.put(test, o);		
		System.out.println("add called with object: " + test + " class " + test.getClass() + "name:" + name);				
		//uiFrame editor = uiGenerator.generateUIFrame(test);
		uiFrame editor = ObjectEditor.edit(test);		editors.addElement(editor);
		editor.setVisible(true);
			}
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
		Sync.delegatedTable.put(b, repl);
		//DelegatedReplicatedObject replB = new DelegatedReplicatedObject(b);
		((Delegated)repl).registerAsListener();		
		//uiFrame editor = uiGenerator.generateUIFrame(b);
		uiFrame editor = ObjectEditor.edit(b);		editors.addElement(editor);
		System.out.println("new object " +  className + " " + name); 		  
		editor.setVisible(true);
		return repl;
	}
	
	public void doRefresh()
	{		System.out.println("SyncObjectEditor:doRefresh ");
		uiFrame editor;		for (Enumeration e=editors.elements(); e.hasMoreElements();)		{						editor = (uiFrame)e.nextElement();
			try
			{				System.out.println("SyncObjectEditor:doRefresh of " + editor);
				editor.doImplicitRefresh();
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
