package mid2019.music;

import java.util.ArrayList;
import mid2019.UC;

public class Time {
  public int x;
  public Head.List heads = new Head.List();

  // factory method
  private Time(Sys sys, int x) {
    this.x = x;
    sys.times.add(this);
  }

  public void unstemHeads(int y1, int y2) {
    // System.out.println("unstem heads size: " + this.heads.size());
    for (Head h : heads) {
      int y = h.y();
      if (y > y1 && y < y2) {
        h.unstem();
      }
    }
  }

//  public void stemHeads(Staff staff, boolean up, int y1, int y2) {
//    Stem s = new Stem(staff, up);
//    // System.out.println("Add stem : Layers: " + AaMusic.NOTE.size());
//    // System.out.println("heads size: " + this.heads.size());
//    for (Head h : heads) {
//      int y = h.y();
//      // System.out.println("y: " + y + ", " + y1 + ", " + y2);
//      if (y > y1 && y < y2) {
//        h.joinStem(s);
//      }
//    }
//    if (s.heads.size() == 0) {
//      System.out.println("empty head list after stemming");
//    }
//    else {
//      s.setWrongSides();
//    }
//    s.staff.sys.stems.addStem(s);
//  }

  public static class List extends ArrayList<Time> {
    public Sys sys;

    public List(Sys sys) { this.sys = sys; }

    public Time getTime(int x) {
      if (size() == 0) {
        return new Time(sys, x);
      }
      Time t = getClosestTime(x);
      return Math.abs(x - t.x) < UC.snapTime ? t : new Time(sys, x);
    }

    public Time getClosestTime(int x) {
      Time result = get(0);
      int bestSoFar = Math.abs(x - result.x);
      for (Time t : this) {
        int dist = Math.abs(x - t.x);
        if (dist < bestSoFar) {
          result = t;
          bestSoFar = dist;
        }
      }
      return result;
    }
  }
}
