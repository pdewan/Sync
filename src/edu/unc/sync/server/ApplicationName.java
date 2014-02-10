package edu.unc.sync.server;

import edu.unc.sync.*;
import bus.uigen.ObjectEditor;
//import budget.DelegationTest;
import java.util.*;
@util.annotations.StructurePattern(util.annotations.StructurePatternNames.ENUM_PATTERN)
public class ApplicationName
{
	private SyncClient client;
	private PropertiesTable properties;
	private String myname;
	Vector appNames;
	String currentName = "";

	public ApplicationName(Vector theAppNames){
		appNames = theAppNames;
		if (appNames.size() > 0)
			currentName = (String) appNames.elementAt(0);
	}
	
	public int choicesSize() {
		return appNames.size();
	}
	public String choiceAt(int i) {
		return (String) appNames.elementAt(i);
	}
	public String getValue() {
		return currentName;
	}
	public void setValue(String newVal) {
		if (!appNames.contains(newVal))
			return;
		currentName = newVal;
	}
	public String toString() {
		return getValue();
	}
	public List<String>  getChoices() {
		return appNames;
	}
}
