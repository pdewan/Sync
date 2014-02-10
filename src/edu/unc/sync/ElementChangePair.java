package edu.unc.sync;

public class ElementChangePair
{
   public ElementChange apply_to_central;
   public ElementChange apply_to_remote;
   public boolean Conflicting;

   public ElementChangePair(ElementChange c, ElementChange r, boolean conf)
   {
      apply_to_central = c;
      apply_to_remote = r;
      Conflicting = conf;
   }
}
