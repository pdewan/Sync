package examples;
import bus.uigen.*;
import java.util.*;
 
public class SyncObjectEditor implements SyncApplication, RT_SyncApplication{
	SyncServer syncServer = null;
	private Vector editors = new Vector(1);
	public SyncObjectEditor() {
		
		System.out.println("SyncObjectEditor created");
	public static void main(String args[]) {
		
	}
	
	public void addObject(Replicated o, String name)
	{
		Delegated obj = (Delegated) o;
		Sync.delegatedTable.put(test, o);
		System.out.println("add called with object: " + test + " class " + test.getClass() + "name:" + name);		
		//uiFrame editor = uiGenerator.generateUIFrame(test);
		uiFrame editor = ObjectEditor.edit(test);
		editor.setVisible(true);
		
	
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
		uiFrame editor = ObjectEditor.edit(b);
		System.out.println("new object " +  className + " " + name); 		  
		editor.setVisible(true);
		return repl;
	}
	
	public void doRefresh()
	{
		uiFrame editor;
			try
			{
				editor.doImplicitRefresh();
			}
			catch (Exception ex)
			{
			}
	}
	
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
	}