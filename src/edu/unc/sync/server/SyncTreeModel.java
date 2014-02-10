/* Decompiled by Mocha from SyncTreeModel.class */

/* Originally compiled from SyncTreeModel.java */



package edu.unc.sync.server;



//import com.sun.java.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeModel;



public  class SyncTreeModel

{

    static DefaultTreeModel tree_model;



    private SyncTreeModel()

    {

    }



    public static void setTreeModel(DefaultTreeModel model)

    {

        tree_model = model;

    }



    public static DefaultTreeModel getTreeModel()

    {

        return tree_model;

    }

}

