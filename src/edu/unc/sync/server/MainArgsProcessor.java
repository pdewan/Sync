/*
 * Created on Feb 9, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.unc.sync.server;

import java.util.Hashtable;
import java.util.Vector;

import bus.uigen.uiGenerator;


/**
 * @author dewan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class MainArgsProcessor {	
	public static final Object NO_VALUE = "";
	public static void printFlags (String[] regFlags, String[] boolFlags) {
		System.out.println ("Boolean Flags Supported:");
		for (int i = 0; i < boolFlags.length; i++) {
			System.out.println (boolFlags[i]);
		}
		System.out.println ("Regular Flags Supported:");
		for (int i = 0; i < regFlags.length; i++) {
			System.out.println (regFlags[i]);
		}
	}

	public static Hashtable toTable (String[] regFlags, String[] boolFlags, String[] args) {
		Hashtable retVal = new Hashtable();
		Vector regFlagVector = uiGenerator.arrayToVector(regFlags);
		Vector boolFlagVector = uiGenerator.arrayToVector(boolFlags);
		Vector argVector = uiGenerator.arrayToVector(args);
		for (int i = 0; i < regFlagVector.size(); i++) {
			String flag = (String) regFlagVector.elementAt(i);
			int flagIndex = argVector.indexOf(flag);
			if (flagIndex < 0) continue;
			if (flagIndex == argVector.size() - 1) {
				missingArgument (flag);
				argVector.remove(flag);
				continue;
			}
			String flagVal = (String) argVector.elementAt(flagIndex + 1);
			if (regFlagVector.contains(flagVal)) {
				missingArgument (flag);
				argVector.remove(flag);
				continue;
			}
			retVal.put(flag, flagVal);
			argVector.remove(flag);
			argVector.remove(flagVal);
		}
		for (int i = 0; i < boolFlagVector.size(); i++) {
			String flag = (String) boolFlagVector.elementAt(i);
			int flagIndex = argVector.indexOf(flag);
			if (flagIndex < 0) continue;
			retVal.put(flag, NO_VALUE);
			argVector.remove(flag);			
		}
		for (int i = 0; i < argVector.size(); i++) {
			System.out.println ("Warning: Unrecognized main argument:" + argVector.elementAt(i));
		}
		if (argVector.size() > 0) printFlags(regFlags, boolFlags);
		return retVal;
				
	}
	
	static void missingArgument (String flag) {
		System.out.println ("Warning: " + flag + " should be followed by an argument. Ignoring flag");
		//flagTable.put(flag, NO_VALUE);
	}
	
}
