package edu.unc.sync.server;

public class TooManyObjectsException extends Exception
{
   public TooManyObjectsException()
   {
      super("This application supports editing of only a single object");
   }
}