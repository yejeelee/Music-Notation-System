package mid2019.music;

import java.awt.Graphics;
import mid2019.UC;
import mid2019.reaction.Gesture;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;


public class Bar extends Mass {
  public Sys sys;
  public int x, barType = 0;

  // LEFT : 1000, RIGHT : 0100, FAT = 0010
  private static final int LEFT = 8;
  private static final int RIGHT = 4;
  private static final int FAT = 2;

  public Bar(Sys sys, int x) {
    super("BACK");
    this.sys = sys;
    this.x = x;
    if(Math.abs(x - Page.PAGE.margins.right) < UC.barToMarginSnap) {
      this.x = Page.PAGE.margins.right;
    }
    addReaction(new Reaction("S-S") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        // if (x - Bar.this.x)
        if(Math.abs(x - Bar.this.x) > UC.barToMarginSnap) { return UC.noBid; }
        if(y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() + 20) { return UC.noBid; }
        return Math.abs(x - Bar.this.x);
      }

      @Override
      public void act(Gesture g) {
        Bar.this.cycleType();
      }
    });
    addReaction(new Reaction("DOT") {
      @Override
      public int bid(Gesture g) {
        int x= g.vs.xM(), y = g.vs.yM();
        if(y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) {
          return UC.noBid;
        }
        int d = Math.abs(x - Bar.this.x);
        if(d > 3 * UC.defaultStaffH) { return UC.noBid;}
        return d;
      }

      @Override
      public void act(Gesture g) {
        if(g.vs.xM() < Bar.this.x) {Bar.this.toggleLeft();}
        else { Bar.this.toggleRight();}

      }
    });

  }

  public void show(Graphics g) {
    int yTop = sys.yTop(), N = sys.fmt.size(), y1 = 0, y2 = 0;
    boolean sawBreak = true;

    for(int i = 0; i < N; i++) {
      Staff.Fmt sf= sys.fmt.get(i);
      int topLine = yTop + sys.fmt.staffOffsets.get(i);
      if(sawBreak) {y1 = topLine; }
      y2 = topLine + sf.height();
      if (!sf.barContinues) { drawLines(g, x, y1, y2);}
      sawBreak = !sf.barContinues;
      if(barType > 3) {
        drawDots(g, x, topLine);
      }
    }
  }

  public void cycleType() { barType++; if(barType > 2) barType = 0; }

  // ^ means XOR
  //toggleLeft = shift to the left
  public void toggleLeft() { barType = barType ^ LEFT; }

  public void toggleRight() { barType = barType ^ RIGHT; }

  //dx = thickness
  //dx: how thick the bar is, related to the staff dimension
  public static void fatBar(Graphics g, int x, int y1, int y2, int dx) {
    g.fillRect(x, y1, dx, y2 - y1);
  }
  public static void thinBar(Graphics g, int x, int y1, int y2) {
    g.drawLine(x, y1, x, y2);
  }

  //dx: width of the wing, dy: height of the wing, y1: lower y, y2: heigher y. dx > 0: right wing, dx < 0: left wing
  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
    g.drawLine(x, y1, x + dx, y1 - dy);
    g.drawLine(x, y2, x + dx, y2 + dy);
  }

  public void drawLines(Graphics g, int x, int y1, int y2) {
    int H = UC.defaultStaffH;
    if (barType == 0) {
      thinBar(g, x, y1, y2);
    }
    if (barType == 1) {
      thinBar(g, x, y1, y2);
      thinBar(g, x - H, y1, y2);
    }
    if (barType == 2) {
      fatBar(g, x - H, y1, y2, H);
      thinBar(g, x - (2 * H), y1, y2);
    }
    if (barType > 4) {
      fatBar(g, x - H, y1, y2, H);
      if ((barType & LEFT) != 0) {
        thinBar(g, x - 2 * H, y1, y2);
        wings(g, x - 2 * H, y1, y2, -H, H);
      }
      if ((barType & RIGHT) != 0) {
        thinBar(g, x + H, y1, y2);
        wings(g, x + H, y1, y2, H, H);
      }
    }

  }
  public void drawDots(Graphics g, int x, int top) {
    int H = Page.PAGE.sysFmt.maxH;
    if((barType & LEFT) != 0) {
      g.fillOval(x - (3*H), top + 11 * H / 4, H / 2, H/2);
      g.fillOval(x - (3*H), top + 19 * H / 4, H / 2, H/2);

    }
    if((barType & RIGHT) != 0) {
      g.fillOval(x + (3* H / 2), top + 11 * H / 4, H / 2, H/2);
      g.fillOval(x + (3*H / 2), top + 19 * H / 4, H / 2, H/2);
    }
  }


}
