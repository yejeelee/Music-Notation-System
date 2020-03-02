package mid2019.music;

import java.awt.Graphics;
import mid2019.reaction.Mass;


public abstract class Duration extends Mass {

  public int nDot = 0, nFlag = 0;

  public Duration() {
    super("NOTE");
  }

  public abstract void show(Graphics g); //show both on HEAD and RESTS

  public void incFlag() { if (nFlag < 4) { nFlag++; } }

  //quearter head: nFlag = 0,
  // half head: nFlag = -1 (don't show the flag, change the shape of head),
  // whole head: nFlag = -2 (don't show the flag, change the shape of head)
  public void decFlag() { if (nFlag > -2) { nFlag--; } }

  public void cycleDot() { nDot++; if (nDot > 3) { nDot = 0;} }



}