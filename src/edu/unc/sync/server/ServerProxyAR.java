package edu.unc.sync.server;

import java.awt.BorderLayout;
import java.util.Vector;

import bus.uigen.attributes.AttributeNames;
import bus.uigen.undo.ExecutableCommand;

import bus.uigen.ObjectEditor;

public class ServerProxyAR implements ExecutableCommand {

	@Override
	public Object execute(Object theFrame) {
		// TODO Auto-generated method stub
		//ObjectEditor.setPropertyAttribute(ServerProxy.class, "objects", AttributeNames.COMPONENT_WIDTH, 700);
		ObjectEditor.setMethodAttribute(ServerProxy.class, "synchronize", AttributeNames.SHOW_BUTTON, true);
		ObjectEditor.setMethodAttribute(ServerProxy.class, "synchronize", AttributeNames.ROW, 0);
		ObjectEditor.setAttribute(ServerProxy.class, AttributeNames.BOUND_PLACEMENT, BorderLayout.NORTH);
		ObjectEditor.setPropertyAttribute(ServerProxy.class, "objects", AttributeNames.CONTAINER_WIDTH, 700);
		ObjectEditor.setPropertyAttribute(ServerProxy.class, "objects", AttributeNames.CONTAINER_HEIGHT, 50);

		//ObjectEditor.setPropertyAttribute(ServerProxy.class, "objects", AttributeNames.COMPONENT_HEIGHT, 50);
		ObjectEditor.setPropertyAttribute(Vector.class, AttributeNames.ANY_ELEMENT, AttributeNames.COMPONENT_WIDTH, 200);
		return null;
	}

}
