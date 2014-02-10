package edu.unc.sync.server;

import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;
import bus.uigen.attributes.AttributeNames;
import bus.uigen.sadapters.RowToRecord;
import bus.uigen.undo.ExecutableCommand;

public class FolderAR implements ExecutableCommand {
	public Object execute(Object theFrame) {
		
		ObjectEditor.setAttribute(Folder.class, AttributeNames.HASHTABLE_CHILDREN, AttributeNames.VALUES_ONLY);
		ObjectEditor.setAttribute(RowToRecord.class, AttributeNames.HASHTABLE_CHILDREN, AttributeNames.VALUES_ONLY);
		
		  return null;
	}

}
