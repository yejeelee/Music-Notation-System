package mid2019.reaction;

import java.util.ArrayList;
import mid2019.I;
import mid2019.graphicsLib.G;
import mid2019.graphicsLib.G.VS;

// shape + Box(vs): similar to Ink(norm + box) but not care about actual points
public class Gesture {
  public Shape shape;
  public VS vs; //box
  public static List UNDO = new List();


  // factory method
  // recognizing shape can fail, use factory method
  private Gesture(Shape shape, G.VS vs) {
    this.shape = shape;
    this.vs = vs;
  }

  public static Gesture getNew(Ink ink) {
    Shape s = Shape.recognize(ink);
    // if s is null return null, if not, create gesture
    return (s == null) ? null : new Gesture(s, ink.vs);
  }

  public void redoGesture() {
    Reaction r = Reaction.best(this);
    if(r != null) { r.act(this); }

  }

  public void doGesture() {
    Reaction r = Reaction.best(this);
    if(r != null) { UNDO.add(this); r.act(this); }

  }

  public static void undo() {
    if(UNDO.size() > 0) {
      UNDO.remove(UNDO.size() - 1);
      Layer.nuke();
      Reaction.nuke();
      UNDO.redo();
    }
  }
  // since I.Area is a interface, this is how to implement
  public static I.Area AREA = new I.Area() {
    public boolean hit(int x, int y) { return true; }

    public void dn(int x, int y) { Ink.BUFFER.dn(x, y);}

    public void drag(int x, int y) { Ink.BUFFER.drag(x, y);}

    // gesture recognition
    public void up(int x, int y) {
      Ink.BUFFER.add(x, y);
      Ink ink = new Ink();
      Ink.BUFFER.clear();
      Gesture g = getNew(ink);
      if (g != null) {
        //System.out.println("Gesture: "+g.shape.name);
        if(g.shape.name.equals("N-N")) {
          undo();
        } else {
          g.doGesture();
        }
      }
    }
  };

  public static class List extends ArrayList<Gesture> {
    public void redo() {
      for(Gesture g : this) { g.redoGesture(); }
    }
  }

}
