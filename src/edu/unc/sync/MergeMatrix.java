package edu.unc.sync;

public abstract class MergeMatrix
{
   MergeAction[][] matrix;

   public MergeMatrix(int size)
   {
      matrix = new MergeAction[size][size];
   }

   public MergeAction[][] getMatrix()
   {
     return matrix;
   }

   public MergeAction getEntry(int remote_opcode, int central_opcode)
   {
       return matrix[remote_opcode][central_opcode];
   }

   // public MergeAction getEntry(Change remote, Change central)
   // {
   //     return matrix[remote.opcode()][central.opcode()];
   // }

   public void setEntry(int remote_opcode, int central_opcode, MergeAction entry)
   {
      matrix[remote_opcode][central_opcode] = entry;
   }

   public void setEntryBothAction(int remote_opcode, int central_opcode, boolean conf)
   {
      matrix[remote_opcode][central_opcode] = new BasicMergeActions.BothAction(conf);
   }

   public void setEntryColumnAction(int remote_opcode, int central_opcode, boolean conf)
   {
      matrix[remote_opcode][central_opcode] = new BasicMergeActions.ColumnAction(conf);
   }

   public void setEntryRowAction(int remote_opcode, int central_opcode, boolean conf)
   {
      matrix[remote_opcode][central_opcode] = new BasicMergeActions.RowAction(conf);
   }

   public void setEntryNeitherAction(int remote_opcode, int central_opcode, boolean conf)
   {
      matrix[remote_opcode][central_opcode] = new BasicMergeActions.NeitherAction(conf);
   }

   public void assignEntryDoMergeAction(int remote_opcode, int central_opcode)
   {
      matrix[remote_opcode][central_opcode] = new BasicMergeActions.DoMergeAction();
   }

   public ChangePair action(Change central, Change remote) throws ReplicationException
   {
      //System.out.println("merging remote op "+remote.opcode()+" and central op "+central.opcode());
      MergeAction action = matrix[remote.opcode()][central.opcode()];
      ChangePair result = action.compute(central, remote);
      //System.out.println(action.description()+" => "+result.toString());
      return result;
   }

   String address(int i, int j)
   {
      return "["+i+"]["+j+"]";
   }

   public void print()
   {
      for (int i = 0; i < matrix.length; i++)
         for (int j = 0; j < matrix[i].length; j++)
            if (matrix[i][j] != null)
               System.out.println(address(i,j)+": "+matrix[i][j].description());
            else
               System.out.println(address(i,j)+": null");
   }
}
