import bus.uigen.*;
import java.util.*;
 
public class SyncObjectEditor implements SyncApplication, RT_SyncApplication{
	SyncServer syncServer = null;
	private Vector editors = new Vector();
	public SyncObjectEditor() {
		//Test b = new Test();
		try {
		} catch (Exception e) {e.printStackTrace();}
		uiFrame editor = uiGenerator.generateUIFrame(b,
		null);
		editor.setVisible(true);
		*/
		System.out.println("DelegationTest editor created");
	public static void main(String args[]) {
		/*
		try {
		} catch (Exception e) {e.printStackTrace();}
		uiFrame editor = uiGenerator.generateUIFrame(b);
		editor.setVisible(true);
	}
	
	public void addObject(Replicated o, String name)
	{
		Delegated obj = (Delegated) o;
		
		Sync.delegatedTable.put(test, obj);
		System.out.println("add called with object " + test + "string" + name);		
		uiFrame editor = uiGenerator.generateUIFrame(test);
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
		//DelegatedReplicatedObject replB = new DelegatedReplicatedObject(b);
		//((Delegated)repl).registerAsListener();
		Sync.delegatedTable.put(b, repl);
		uiFrame editor = uiGenerator.generateUIFrame(b);
		System.out.println("new object " +  className + " " + name); 		  
		editor.setVisible(true);
		return repl;
	}
	
	public void doRefresh()
	{
		uiFrame editor;
			try
			{
				editor.doRefresh();
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