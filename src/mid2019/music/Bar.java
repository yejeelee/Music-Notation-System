package mid2019.music;

import java.awt.Graphics;
import mid2019.UC;
import mid2019.reaction.Gesture;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;

public class Bar extends Mass {
  // use bits to check style
  public Sys sys;
  public int x, barType = 0;
  private static final int LEFT = 8, RIGHT = 4, FAT = 2;
  //LEFT: 1000, RIGHT: 0100, FAT: 0010
  public Bar(Sys sys, int x) {
    super("BACK");
    this.sys = sys;
    this.x = x;
    if (Math.abs(x - Page.PAGE.margins.right) < UC.barToMarginSnap) {
      this.x = Page.PAGE.margins.right;
    }
    addReaction(new Reaction("S-S") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        if (Math.abs(x - Bar.this.x) > UC.barToMarginSnap) { return UC.noBid; }
        if (y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBottom() + 20) { return UC.noBid; }
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
        int x = g.vs.xM(), y = g.vs.yM();
        if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBottom()) {
          return UC.noBid;
        }
        int d = Math.abs(x - Bar.this.x);
        if (d > 3 * UC.defaultStaffH) {
          return UC.noBid;
        }
        return d;
      }

      @Override
      public void act(Gesture g) {
        if (g.vs.xM() < Bar.this.x) {
          Bar.this.toggleLeft();
        }
        else {
          Bar.this.toggleRight();
        }
      }
    });
  }

  public void show(Graphics g) {
    int yTop = sys.yTop(), N = sys.fmt.size();
    int y1 = 0, y2 = 0;
    boolean sawBreak = true;
    for (int i = 0; i < N; i++) {
      Staff.Fmt sf = sys.fmt.get(i);
      int topLine = yTop + sys.fmt.staffOffsets.get(i);
      if (sawBreak) {
        y1 = topLine;
      }
      y2 = topLine + sf.height();
      if (!sf.barContinues) {
        drawLines(g, x, y1, y2);
      }
      sawBreak = !sf.barContinues;
      if (barType > 3) {
        drawDots(g, x, y1);
      }
    }
  }

  public void cycleType() { barType++; if (barType > 2) { barType = 0; } }

  public void toggleLeft() { barType = barType ^ LEFT; }

  public void toggleRight() { barType = barType ^ RIGHT; }

  public static void fatBar(Graphics g, int x, int y1, int y2, int dx) { g.fillRect(x, y1, dx, y2 - y1); }

  public static void thinBar(Graphics g, int x, int y1, int y2) { g.drawLine(x, y1, x, y2); }

  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
    g.drawLine(x, y1, x + dx, y1 - dy);
    g.drawLine(x, y2, x + dx, y2 + dy);
  }

  public void drawLines(Graphics g, int x, int y1, int y2) {
    int h = UC.defaultStaffH;
    if (barType == 0) { thinBar(g, x, y1, y2); }
    if (barType == 1) {
      thinBar(g, x, y1, y2);
      thinBar(g, x - h, y1, y2);
    }
    if (barType == 2) {
      fatBar(g, x - h, y1, y2, h);
      thinBar(g, x - 2 * h, y1, y2);
    }
    if (barType >= 4) {
      fatBar(g, x - h, y1, y2, h);
      if ((barType & LEFT) != 0) {
        thinBar(g, x - 2 * h, y1, y2);
        wings(g, x - 2 * h, y1, y2, -h, h);
      }
      if ((barType & RIGHT) != 0) {
        thinBar(g, x + h, y1, y2);
        wings(g, x + h, y1, y2, h, h);
      }
    }
  }

  public void drawDots(Graphics g, int x, int top) {
    int h = Page.PAGE.sysFmt.maxH;
    if ((barType & LEFT) != 0) {
      g.fillOval(x - 3 * h, top + 11 * h / 4, h / 2, h / 2);
      g.fillOval(x - 3 * h, top + 19 * h / 4, h / 2, h / 2);
    }
    if ((barType & RIGHT) != 0) {
      g.fillOval(x + 3 * h / 2, top + 11 * h / 4, h / 2, h / 2);
      g.fillOval(x + 3 * h / 2, top + 19 * h / 4, h / 2, h / 2);
    }
  }
}