package mid2019.music;


import java.awt.Graphics;
import java.awt.Polygon;
import mid2019.reaction.*;

public class Beam extends Mass {
  public Stem.List stems = new Stem.List();
  public static int mX1, mX2, mY1, mY2;
  public static Polygon poly;
  static {
    int[] foo = {0,0,0,0};
    poly = new Polygon(foo, foo, 4);
  }


  public Beam(Stem firstStem, Stem lastStem) {
    super("NOTE");
    stems.addStem(firstStem);
    stems.addStem(lastStem);
    firstStem.nFlag = 1;
    lastStem.nFlag = 1;
    firstStem.beam = this;
    lastStem.beam = this;
  }

  public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bX, int bY, int eX, int eY) {
    if(x < bX || x > eX) {return false;}
    int y = yOfX(x, bX, bY, eX, eY);
    return (y1 < y2) ? (y1 < y && y < y2) : (y2 < y && y < y1);
  }

  @Override
  public void show(Graphics g) {
    drawBeamGroup(g);
  }

  public Stem first() { return stems.get(0); }

  public Stem last() { return stems.get(stems.size() - 1); }

  public void addStem(Stem s) {
    //check if this stem is already in some other beam group
    if(s.beam == null) {
      stems.addStem(s);
      s.beam = this;
      stems.sort();
    }
  }

  public static int yOfX(int x, int x1, int y1, int x2, int y2) {
    int dy = y2 - y1, dx = x2-x1;
    return (x-x1) * dy / dx + y1;
  }

  public static void setMasterBeam(int x1, int y1, int x2, int y2) {
    mX1 = x1;
    mX2 = x2;
    mY1 = y1;
    mY2 = y2;
  }

  public static int yOfX(int x) {
    int dy = mY2 - mY1, dx = mX2 - mX1;
    return (x - mX1) * dy / dx + mY1;
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
    for(int i = n1; i < n2; i++) {
      setPoly(x1, y1 + i * 2 * h, x2, y2 + i * 2 * h, h);
      g.fillPolygon(poly);
    }
  }

  public void drawBeamGroup(Graphics g) {
    setMasterBeam();
    Stem firstStem = first();
    //sH = sign of height
    int h = firstStem.staff.H(), sH = firstStem.isUp ? h : - h;
    int nPrev = 0, nCur = firstStem.nFlag, nNext = stems.get(1).nFlag;
    int pX, curX = firstStem.x(), bX = curX + 3 * h; // prev x, current x, beamlet x
    if(nCur > nNext) {
      drawBeamStack(g, nNext, nCur, curX, bX, sH);
    }

    for(int cur = 1; cur < stems.size(); cur++) {
      Stem sCur = stems.get(cur);
      pX = curX;
      curX = sCur.x();
      nPrev = nCur;
      nCur = nNext;
      nNext = ( cur < stems.size() - 1) ? stems.get(cur + 1).nFlag : 0;
      int nBack = Math.min(nPrev, nCur);
      drawBeamStack(g, 0, nBack, pX, curX, sH);
      if(nCur > nPrev && nCur > nNext) { // draw beamlet
        if(nPrev < nNext) {
          bX = curX + 3 * h;
          drawBeamStack(g, nNext, nCur, curX, bX, sH);
        } else {
          bX = curX - 3 * h;
          drawBeamStack(g, nPrev, nCur, curX, bX, sH);
        }
      }
    }

  }


}
