package mid2019.music;

import java.awt.Graphics;
import java.util.ArrayList;
import mid2019.UC;
import mid2019.reaction.*;

public class Head extends Mass implements Comparable<Head> {

  public Staff staff;
  public int line;
  public Time time;
  public Stem stem = null; //could be null on some occasions
  public boolean wrongSide = false;
  public Glyph forcedGlyph = null; //if not null, using another system of notation

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    this.time = staff.sys.getTime(x);
    this.line = staff.lineOfY(y); //dividing by H: convert the coordinate suitable for pixel to suitable for H. +H/2: rounding for bias
    System.out.println(this.line);
    time.heads.add(this);
    addReaction(new Reaction("S-S") {
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int w = Head.this.w(), hy = Head.this.y();
        if (y1 > hy || y2 < hy) {
          return UC.noBid;
        }
        int hl = Head.this.time.x, hr = hl + w;
        if (x < hl - w || x > hr + w) {
          return UC.noBid;
        }
        if (x < hl + w / 2) {
          return hl - x;
        } else if (x > hr - w / 2) {
          return x - hr;
        } else {
          return UC.noBid;
        }
      }
      public void act(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        Staff staff = Head.this.staff;
        Time t = Head.this.time;
        int w = Head.this.w();
        boolean up = x > (t.x + w / 2);
        if (Head.this.stem == null) {
          //t.stemHeads(staff, up, y1, y2);
          Stem.getStem(staff, t, y1, y2, up);
        } else {
          t.unstemHeads(y1, y2);
        }
      }
    });
    addReaction(new Reaction("DOT") {
      public int bid(Gesture g) {
        int xH = Head.this.x(); // xHead
        int yH = Head.this.y(); // yHead
        int H = Head.this.staff.H();
        int w = Head.this.w();
        int x = g.vs.xM(), y = g.vs.yM();
        if(x < xH || x > xH + 2*w || y < yH - H || y > yH + H) { return UC.noBid; }

        return Math.abs(xH + w - x) + Math.abs(yH - y);
      }
      public void act(Gesture g) {
        if(Head.this.stem != null) {
          Head.this.stem.cycleDot();
        }
      }
    });
  }

  public int w() { return 24 * staff.H() / 10; }

  public void show(Graphics g) {
    int H = staff.H();
    ((forcedGlyph != null) ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
    if(stem != null) {
      int off = 30;
      int sp = 10;
      for(int i = 0; i < stem.nDot; i++) {
        g.fillOval(time.x + off + i * sp, y() - (3 * H  / 2), H*2/3, H*2/3);
      }
    }
  }

  public int y() { return staff.yLine(line);}
  public int x() {
    int res = time.x;
    if(wrongSide) { res += (stem != null && stem.isUp) ? w() : -w(); }
    return res;
  } //a stub
  public Glyph normalGlyph() {
    if(stem == null) { return Glyph.HEAD_Q; }
    if(stem.nFlag == -1) {return Glyph.HEAD_HALF;}
    if(stem.nFlag == -2) {return Glyph.HEAD_W;}
    return Glyph.HEAD_Q;
  }//a stub
  public void deleteHead() { time.heads.remove(this); } // a stub
  public void joinStem(Stem s) {
    if (stem != null) { unstem(); }
    s.heads.add(this);
    stem = s;
  }

  public void unstem() {
    if (stem != null) {
//      System.out.println("head unstem: " + ((Object)stem).toString());
      stem.heads.remove(this);
      if (stem.heads.size() == 0) {
        stem.deleteStem();
      }
      stem = null;
      wrongSide = false;
    }
  }

  @Override
  public int compareTo(Head h) {
    return (staff.iStaff != h.staff.iStaff) ? staff.iStaff - h.staff.iStaff : line - h.line;
  }


  //------------List------------
  public static class List extends ArrayList<Head> {

  }

}
