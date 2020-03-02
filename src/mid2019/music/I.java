package mid2019.music;

import java.awt.Graphics;
import mid2019.reaction.Gesture;

public interface I {
  interface Hit { boolean hit(int x, int y);}
  interface Show { void show(Graphics g);}
  interface Area extends Hit {
    void dn(int x, int y); //the mouse go down
    void drag(int x, int y);
    void up(int x, int y);
  }
  interface Act { void act(Gesture g); }
  interface React extends Act { int bid(Gesture g); }
}
