package mid2019.music;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import mid2019.music.Stem.List;
import mid2019.reaction.Mass;

public class Beam extends Mass {
  public Stem.List stems = new List();
  public static int mX1, mX2, mY1, mY2;
  public static Polygon poly;
  static {
    int[] foo = {0, 0, 0, 0};
    poly = new Polygon(foo, foo, 4);
  }

  public Beam(Stem first, Stem last) {
    super("NOTE");
    stems.addStem(first);
    stems.addStem(last);
    first.nFlag = 1;
    last.nFlag = 1;
    first.beam = this;
    last.beam = this;
  }

  public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bX, int bY, int eX, int eY) {
    if (x < bX || x > eX) { return false; }
    int y = yOfX(x, bX, bY, eX, eY);
    return y1 < y2 ? (y1 < y && y < y2) : (y2 < y && y < y1);
  }

  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLACK);
    drawBeamGroup(g);
  }

  public Stem first() {
    return stems.get(0);
  }

  public Stem last() {
    return stems.get(stems.size() - 1);
  }

  public void addStem(Stem s) {
    if (s.beam == null) {
      stems.addStem(s);
      s.beam = this;
      stems.sort();
    }
  }

  public static int yOfX(int x, int x1, int y1, int x2, int y2) {
    int dy = y2 - y1, dx = x2 - x1;
    return y1 + (x - x1) * dy / dx;
  }

  public static void setMasterBeam(int x1, int y1, int x2, int y2) {
    mX1 = x1; mY1 = y1; mX2 = x2; mY2 = y2;
  }

  public static int yOfX(int x) {
    int dy = mY2 - mY1, dx = mX2 - mX1;
    return mY1 + (x - mX1) * dy / dx;
  }

  public void setMasterBeam() {
    setMasterBeam(first().x(), first().yBeamEnd(), last().x(), last().yBeamEnd());
  }

  public static void setPoly(int x1, int y1, int x2, int y2, int h) {
    int[] a = poly.xpoints;
    a[0] = x1;
    a[1] = x2;
    a[2] = x2;
    a[3] = x1;
    a = poly.ypoints;
    a[0] = y1;
    a[1] = y2;
    a[2] = y2 + h;
    a[3] = y1 + h;
  }

  public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h) {
    int y1 = yOfX(x1), y2 = yOfX(x2);
    for (int i = n1; i < n2; i++) {
      setPoly(x1, y1 + i * 2 * h, x2, y2 + i * 2 * h, h);
      g.fillPolygon(poly);
    }
  }

  public void drawBeamGroup(Graphics g) {
    setMasterBeam();
    Stem firstStem = first();
    int h = firstStem.staff.H(), sH = firstStem.isUp ? h : -h;
    int nPrev = 0, nCur = firstStem.nFlag, nNext = stems.get(1).nFlag;
    int pX, cX = firstStem.x(), bX = cX + 3 * h;    // prev x, current x and beam x
    if (nCur > nNext) {
      drawBeamStack(g, nNext, nCur, cX, bX, sH);
    }
    for (int cur = 1; cur < stems.size(); cur++) {
      Stem sCur = stems.get(cur);
      pX = cX;
      cX = sCur.x();
      nPrev = nCur;
      nCur = nNext;
      nNext = (cur < stems.size() - 1) ? stems.get(cur + 1).nFlag : 0;
      int nBack = Math.min(nPrev, nCur);
      drawBeamStack(g, 0, nBack, pX, cX, sH);
      if (nCur > nPrev && nCur > nNext) {
        if (nPrev < nNext) {
          bX = cX + 3 * h;
          drawBeamStack(g, nNext, nCur, cX, bX, sH);
        } else {
          bX = cX - 3 * h;
          drawBeamStack(g, nPrev, nCur, cX, bX, sH);
        }
      }
    }
  }


}
