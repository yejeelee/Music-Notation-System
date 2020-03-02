package mid2019.sandbox;

import mid2019.graphicsLib.G;
import mid2019.graphicsLib.G.V;
import mid2019.graphicsLib.G.VS;
import mid2019.graphicsLib.Window;
import mid2019.UC;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Timer;

public class Squares extends Window implements ActionListener {

  // public static VS theVS = new VS(100, 100, 200, 300);
  public static Square theSquare;
  public static boolean dragging;
  public static Color color = G.rndColor();
  public static V mouseDelta = new V(0, 0);
  //public static Square.List theList = new Square.List();
  public static Timer timer;
  public static V pressedLoc = new V(0, 0);

  public static final int WIDTH = UC.mainWindowWidth;
  public static final int HEIGHT = UC.mainWindowHeight;

  public Squares() {
    super("Squares", 1000, 800);
    timer = new Timer(30, this);
    timer.setInitialDelay(3000);
    timer.start();
  }



  @Override
  public void paintComponent(Graphics g) {
    //theVS.fill(g, color);
    Square.ALL.show(g);
  }

  @Override
  public void mousePressed(MouseEvent me) {
//    if (theVS.hitDetection(me.getX(), me.getY())) {
//      color = G.rndColor();
//    }
    theSquare = Square.ALL.hit(me.getX(), me.getY());
    dragging = theSquare != null;
    if (dragging) {
      //mouseDelta = new V(me.getX() - theSquare.loc.x, me.getY() - theSquare.loc.y);
      mouseDelta.set(me.getX() - theSquare.loc.x, me.getY() - theSquare.loc.y);
      pressedLoc.set(me.getX(), me.getY());
    } else {
      theSquare = new Square(me.getX(), me.getY());
    }

    // theList.add(s);
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    if (dragging) {
      theSquare.loc.x = me.getX() - mouseDelta.x;
      theSquare.loc.y = me.getY() - mouseDelta.y;
    } else {
      theSquare.resize(me.getX(), me.getY());
    }
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    theSquare.dv.set(me.getX() - pressedLoc.x, me.getY() - pressedLoc.y);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  public static class Square extends VS {
    public Color color = G.rndColor();
    public G.V dv = new G.V(G.rnd(20) - 10, G.rnd(20) - 10); // delta velocity
    public static List ALL = new List();
    public Square(int x, int y) {
      super(x, y, 100, 100);
      ALL.add(this);
    }
    public void show(Graphics g) {
      this.fill(g, color);
      //loc.add(dv);
      moveAndBounce();
    }
    public void resize(int x, int y) {
      this.size.x = x - this.loc.x >= 0 ? x - this.loc.x : 0;
      this.size.y = y - this.loc.y >= 0 ? y - this.loc.y : 0;
    }
    public void moveAndBounce() {
      loc.add(dv);
      if (xH() >= 1000 && dv.x > 0) {
        dv.x = -dv.x;
      }
      if (yH() >= 800 && dv.y > 0) {
        dv.y = -dv.y;
      }
      if (xL() <= 0 && dv.x < 0) {
        dv.x = -dv.x;
      }
      if (yL() <= 0 && dv.y < 0) {
        dv.y = -dv.y;
      }
    }

    public static class List extends ArrayList<Square> {
      public void show(Graphics g) {
        for (Square s: this) {
          s.show(g);
        }
      }
      public Square hit(int x, int y) {
        for (int i = this.size() - 1; i >= 0; i--) {
          if (get(i).hitDetection(x, y))
            return get(i);
        }
        return null;
      }
    }
  }
}
