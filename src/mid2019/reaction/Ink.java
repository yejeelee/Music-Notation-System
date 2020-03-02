package mid2019.reaction;

import java.io.Serializable;
import mid2019.I;
import mid2019.UC;
import mid2019.graphicsLib.G;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import mid2019.graphicsLib.G.BBox;
import mid2019.graphicsLib.G.V;

public class Ink extends G.PL implements I.Show {

  public static final int SAMPLE_SIZE = UC.normSampleSize;
  public static Buffer BUFFER = new Buffer();
  public static G.VS nVS = new G.VS(100, 100, 200, 200);

  public Norm norm;
  public G.VS vs;

  public Ink() {
    super(SAMPLE_SIZE);
    norm = new Norm();
    vs = BUFFER.bBox.getVS();
  }

  /*
  public Ink() {
    super(SAMPLE_SIZE);
    BUFFER.subSample(this);
    G.V.T.set(BUFFER.bBox.getVS(), nVS);
    transform(); */
    /*
    for(int i = 0; i < BUFFER.n; i++) {
      this.points[i].set(BUFFER.points[i]);
    }
    */
  /*
  }
  */

  public void show(Graphics g) {
    /*
    g.setColor(Color.GREEN);
    g.fillRect(100, 100, 100, 100);
    */
    //draw(g);
    norm.drawAt(g, vs);
  }

  public static class List extends ArrayList<Ink> implements I.Show, Serializable {
    public void show(Graphics g) {
      for(Ink ink: this) {
        ink.show(g);
      }
    }
  }

  public static class Buffer extends G.PL implements  I.Show, I.Area, Serializable {
    public int n;
    public G.BBox bBox = new BBox();
    public static int MAX = UC.inkBufferMax;


    // making it private: it is singleton where no body can access except in this class
    private Buffer() {
      super(MAX);
    }

    public void add(int x, int y) {
      if(n < MAX) {
        points[n++].set(x, y);
        bBox.add(x, y);
      }
    }

    public void clear() {
      n = 0;
    }

    // getting the sample 20 points from poly line I drew
    public void subSample(G.PL pl) {
      for(int i = 0; i < UC.normSampleSize; i ++) {
        pl.points[i].set(points[i * (n - 1) / (UC.normSampleSize - 1)]);
      }
    }

    public void show(Graphics g) {
      drawN(g, n);
      // bBox.draw(g);
    }

    public boolean hit(int x, int y) {
      return true;
    }
    public void dn(int x, int y) {
      clear();
      bBox.set(x, y);
      add(x,y);
    }

    public void drag(int x, int y) {
      add(x, y);
    }

    public void up(int x, int y) {}
  }

  public static class Norm extends G.PL implements Serializable {
    public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
    public static final G.VS NCS = new G.VS(0, 0, MAX, MAX);

    public Norm() {
      super(N);
      BUFFER.subSample(this);
      G.V.T.set(BUFFER.bBox.getVS(), NCS);
      transform();
    }

    public void blend(Norm n, int nBlend) {
      for(int i = 0; i < N; i++) {
        points[i].blend(n.points[i], nBlend);
      }
    }
    public void drawAt(Graphics g, G.VS vs) {
      G.V.T.set(NCS, vs);
      for(int i = 1; i < N; i++) {
        g.drawLine(points[i - 1].tx(), points[i - 1].ty(), points[i].tx(), points[i].ty());
      }
    }
    public int distance(Norm n) {
      int res = 0;
      for(int i = 0; i < N; i++) {
        int dx = points[i].x - n.points[i].x, dy = points[i].y - n.points[i].y;
        res += dx * dx + dy+ dy;
      }
      return res;
    }


  }


}

/*
//feature space - choice of my recognition
// matrics space - space of set of points in distance function. how far these points are.
  3 property:
  1) d(a, a) = 0;
  2) d(a, b) > 0
  3) d(a, b) = d(b, a)

  triangle equality d(a, c)  <= d(a, b) + d(b, c)
*/