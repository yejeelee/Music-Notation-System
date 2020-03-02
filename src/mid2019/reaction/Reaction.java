package mid2019.reaction;

import java.util.ArrayList;
import java.util.HashMap;
import mid2019.I;
import mid2019.UC;

// reason why its abstract: between a class and interface
//abstract class implements some functions but not all of them
// managing the bidding process
public abstract class Reaction implements I.React {

  public Shape shape;

  // to mark the first set of reaction
  public static List initialReactions = new List();
  public static Map byShape = new Map();


  public Reaction(String shapeName) {
    shape = Shape.DB.get(shapeName);
    if (shape == null) { System.out.println("Shape DB doesn't have " + shapeName); }
  }

  public void enable() {
    List list = byShape.getList(shape);
    if (!list.contains(this)) { list.add(this); }
  }

  public void disable() {
    List list = byShape.getList(shape);
    list.remove(this);
    System.out.println("Disabled list size: " + list.size());
  }

  public static Reaction best(Gesture g) {
    return byShape.getList(g.shape).lowBid(g);
  }

  public static void nuke() {
    byShape = new Map();
    initialReactions.enable();
  }

  // ---------------- LIST -----------------
  public static class List extends ArrayList<Reaction> {

    public void addReaction(Reaction r) {
      add(r);
      r.enable();
    }

    //remove the notehead reaction and also remove from the byShape
    public void removeReaction(Reaction r) {
      remove(r);
      r.disable();
    }

    public void clearAll() {
      for (Reaction r : this) {
        r.disable();
      }
      this.clear();
    }

    public Reaction lowBid(Gesture g) {

      // can return null
      Reaction res = null;

      // we are bidding for the 0.
      int bestSoFar = UC.noBid;
      for (Reaction r : this) {
        int bid = r.bid(g);
        if (bid < bestSoFar) {
          bestSoFar = bid;
          res = r;
        }
      }
      System.out.println("Low bid returns " + res);
      return res;
    }

    public void enable() { for(Reaction r : this) { r.enable(); }
    }

  }

  // ----------- MAP ------------
  public static class Map extends HashMap<Shape, List> {

    public List getList(Shape shape) {
      List res = get(shape);
      if (res == null) {
        res = new List();
        put(shape, res);
      }
      return res;
    }
  }


}
