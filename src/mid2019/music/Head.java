package mid2019.music;

import java.awt.Graphics;
import java.util.ArrayList;
import mid2019.UC;
import mid2019.reaction.Gesture;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;

public class Head extends Mass implements Comparable<Head> {

  public Staff staff;
  public int line;
  public Time time;
  public Stem stem = null;
  public boolean wrongSide = false;
  public Glyph forcedGlyph = null;      // using another notation system

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    time = staff.sys.getTime(x);
    /*
    int H = staff.H();
    int top = staff.yTop() - H;
    line = (y - top + H / 2) / H - 1;
    System.out.println(line);

     */
    this.line = staff.lineOfY(y);
    time.heads.add(this);
    addReaction(new Reaction("S-S") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int w = Head.this.w(), hY = Head.this.y();
        if (y1 > hY || y2 < hY) {
          return UC.noBid;
        }
        int hL = Head.this.time.x, hr = hL + w;
        if (x < hL - w || x > hr + w) {
          return UC.noBid;
        }
        if (x < hL + w / 2) {
          return hL - x;
        }
        else if (x > hr - w / 2) {
          return x - hr;
        }
        else {
          return UC.noBid;
        }
      }

      @Override
      public void act(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        Staff staff = Head.this.staff;
        Time t = Head.this.time;
        int w = Head.this.w();
        boolean up = x > (t.x + w / 2);
        if (Head.this.stem == null) {
          // t.stemHeads(staff, up, y1, y2);
          Stem.getStem(staff, t, y1, y2, up);
        }
        else {
          t.unstemHeads(y1, y2);
        }
      }
    });

    addReaction(new Reaction("DOT") {
      @Override
      public int bid(Gesture g) {
        int xH = Head.this.x(), yH = Head.this.y(), h = Head.this.staff.H(), w = Head.this.w();
        int x = g.vs.xM(), y = g.vs.yM();
        if (x < xH || x > xH + 2 * w || y < yH - h || y > yH + h) {
          return UC.noBid;
        }
        return Math.abs(xH + w - x) + Math.abs(yH - y);
      }

      @Override
      public void act(Gesture g) {
        if (Head.this.stem != null) {
          Head.this.stem.cycleDot();
        }
      }
    });
  }

  public int w() {
    return 24 * staff.H() / 10;
  }

  @Override
  public void show(Graphics g) {
    int H = staff.H();
    ((forcedGlyph != null) ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
    //Glyph.HEAD_Q.showAt(g, H, time.x, staff.yTop() + line * H);
    if (stem != null) {
      int off = 30;
      int space = 10;
      for (int i = 0; i < stem.nDot; i++) {
        g.fillOval(time.x + off + i * space, y() - 3 * H / 2, H * 2 / 3, H * 2 / 3);
      }
    }
  }

  public int y() {
    return staff.yLine(line);
  }
  public int x() {
    int res = time.x;
    if (wrongSide) {
      res += ((stem != null) && stem.isUp) ? w() : -w();
    }
    return res;
  }
  public Glyph normalGlyph() {
    if (stem == null) {
      return Glyph.HEAD_Q;
    }
    if (stem.nFlag == -1) {
      return Glyph.HEAD_HALF;
    }
    if (stem.nFlag == -2) {
      return Glyph.HEAD_W;
    }
    return Glyph.HEAD_Q;
  }
  public void deleteHead() {
    time.heads.remove(this); // stub
  }
  public void unstem() {
    if (stem != null) {
      // System.out.println("head unstem: " + stem.id);
      stem.heads.remove(this);
      if (stem.heads.size() == 0) {
        stem.deleteStem();
      }
      stem = null;
      wrongSide = false;
    }
  }
//  public void joinStem(Stem s) {
//    if (stem != null) {
//      unstem();
//    }
//    s.heads.add(this);
//    stem = s;
//  }

  @Override
  public int compareTo(Head h) {
    return (staff.iStaff != h.staff.iStaff) ? staff.iStaff - h.staff.iStaff : line - h.line;
  }

  //--------------------LIST-----------------------
  public static class List extends ArrayList<Head> {

  }
}
