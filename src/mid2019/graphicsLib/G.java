package mid2019.graphicsLib;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Random;

public class G {
  public static Random RANDOM = new Random();
  public static int rnd(int max) {return RANDOM.nextInt(max);}
  public static Color rndColor() {return new Color(rnd(256), rnd(256), rnd(256));}

  public static void fillBackground(Graphics g, Color c) {
    g.setColor(c);
    g.fillRect(0,0,5000, 5000);
  }

  // vector
  public static class V implements Serializable {
    public static Transform T = new Transform();
    public int x, y;

    public V(int x, int y) {this.set(x, y);}
    public void add(V v) { x += v.x; y += v.y;}
    public void add(int x, int y) { this.x += x; this.y += y;}
    public void set(int x, int y) { this.x = x; this.y = y;}
    public void set(V v) {
      set(v.x, v.y);
    }
    public void setX(int x) {
      this.x = x;
    }
    public void setY(int y) {
      this.y = y;
    }

    public int tx() { return x * T.n / T.d + T.dx; }
    public int ty() { return y * T.n / T.d + T.dy; }

    public void setT(V v) {
      set(v.tx(), v.ty());
    }

    public void blend(V v, int k) { set( (k * x + v.x) / (k + 1), (k * y + v.y) / (k + 1));}
    // isomorphic transform - transform with same scale
    public static class Transform {
      // d stands for denominator, n stans for numerator
      public int n, d = 1, dx, dy; // displacement

      // oVS = old VS and nVS = new VS
      public void set(VS oVS, VS nVS) {

        //size.x == width and size.y == height
        setScale(oVS.size.x, oVS.size.y, nVS.size.x, nVS.size.y);
        dx = setOff(oVS.loc.x, oVS.size.x, nVS.loc.x, nVS.size.x);
        dy = setOff(oVS.loc.y, oVS.size.y, nVS.loc.y, nVS.size.y);


      }

      // oW = old Width, oH = old Height, nW = new Width, nH = new Height
      public void setScale(int oW, int oH, int nW, int nH) {
        // if nW is bigger than nH, pick nW. Otherwise, pick nH.
        n = (nW > nH) ? nW : nH;
        d = (oW > oH) ? oW : oH;
        // zero denominators bad. if d is equal to 0, change to 1. otherwise, pick d.
        d = (d == 0) ? 1 : d;
      }

      public int setOff(int oX, int oW, int nX, int nW) {
        return (-oX - oW / 2) * n / d + nX + nW / 2;
      }

    }
  }


  // vector space
  public static class VS implements Serializable {
    public V loc, size;
    public VS (int x, int y, int w, int h) {loc = new V(x, y); size = new V(w, h);}
    public void fill(Graphics g, Color c) {
      g.setColor(c);
      g.fillRect(loc.x, loc.y, size.x, size.y);
    }
    public boolean hitDetection(int x, int y) {
      return loc.x <= x && loc.y <= y && x <= (loc.x+size.x) && y <= (loc.y+size.y);
    }

    // x low
    public int xL() {
      return loc.x;
    }

    public int xH() {
      return loc.x + size.x;
    }

    public int xM() {
      return loc.x + size.x / 2;
    }

    // y low
    public int yL() {return loc.y; }
    public int yH() { return loc.y + size.y;}
    public int yM() { return loc.y + size.y / 2; }
  }


  public static class LoHi implements Serializable {
    public int lo, hi;
    public LoHi(int lo, int hi) {
      this.lo = lo;
      this.hi = hi;
    }
    public void set(int v) { lo = v; hi = v;}
    public void add(int v) { if(v < lo) { lo = v; } if(v > hi) { hi = v; } }
    public int size() { return hi - lo;}
    public int constraint(int v) {if (v < lo) { return lo;} if ( v > hi) {return hi;}return v;}
  }

  //bounding box
  public static class BBox implements Serializable{
    public LoHi h, v; // horizontal and vertical
    public BBox() {
      h = new LoHi(0, 0);
      v = new LoHi(0, 0);
    }
    public void set(int x, int y) {
      h.set(x);
      v.set(y);
    }
    public void add(int x, int y) {
      h.add(x);
      v.add(y);
    }
    public void add(V v)  {
      add(v.x, v.y);
    }
    public void draw(Graphics g) {
      g.drawRect(h.lo, v.lo, h.size(), v.size());
    }

    public G.VS getVS() {
      return new VS(h.lo, v.lo, h.size(), v.size());
    }
  }

  //Poly line
  public static class PL implements Serializable {
    public V[] points;
    public PL(int n) {
      points = new V[n];
      for (int i = 0; i < n; i++) {
        points[i] = new V(0, 0);
      }
    }
    public int size() {
      return points.length;
    }

    public void drawN(Graphics g, int n) {
      for(int i = 1; i < n; i++) {
        g.drawLine(points[i-1].x, points[i-1].y, points[i].x, points[i].y);
      }
      drawNDot(g, n);

    }

    public void draw(Graphics g) {
      drawN(g, points.length);
    }

    public void drawNDot(Graphics g, int n) {
      g.setColor(Color.BLUE);
      for(int i = 0; i < n; i++) {
        g.drawOval(points[i].x - 3, points[i].y - 3,6, 6);

      }
    }

    //helper for transform function in V
    public void transform() {
      // go through each line of points and transform each point
      for(int i = 0; i < points.length; i++) {
        points[i].setT(points[i]);
      }
    }

  }
}

