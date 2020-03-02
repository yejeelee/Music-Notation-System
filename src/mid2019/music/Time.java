package mid2019.music;

import java.util.ArrayList;

import mid2019.UC;

public class Time {
  public int x;
  public Head.List heads = new Head.List();

  private Time(Sys sys, int x) {
    this.x = x;
    sys.times.add(this);
  }

  public void stemHeads(Staff staff, boolean up, int y1, int y2) {
    Stem s = new Stem(staff, heads, up);
//    System.out.println("Layers after add stem: " + AaMusic.NOTE.size());

    for (Head h:heads) {
      int y = h.y();
      if (y > y1 && y < y2) { h.joinStem(s); }
    }
    if (s.heads.size() == 0) {
      System.out.println("Empty head list after stemming.");
    } else {
      s.setWrongSides();
    }
    s.staff.sys.stems.addStem(s);
  }

  public void unstemHeads(int y1, int y2) {
//    System.out.println("unstem heads size:" + this.heads.size());
    for (Head h : heads) {
      int y = h.y();
      if (y > y1 && y < y2) { h.unstem(); }
    }
  }


  // System will keep track of list of time
  //--------------------------TIME.LIST--------------------------
  public static class List extends ArrayList<Time> { //system will track this list


    public Sys sys;

    public List(Sys sys) {
      this.sys = sys;
    }

    public Time getTime(int x) {
      if (this.size() == 0) {
        return new Time(this.sys, x);
      }
      Time t = getClosestTime(x);
      return (Math.abs(x - t.x) < UC.snapTime) ? t : new Time(this.sys, x);
    }

    private Time getClosestTime(int x) {
      Time res = this.get(0);
      int bestSoFar = Math.abs(res.x - x);
      for (Time t : this) {
        int dist = Math.abs(t.x - x);
        if (dist < bestSoFar) {
          bestSoFar = dist;
          res = t;
        }
      }
      return res;
    }

  }

}

