package mid2019.sandbox;



import mid2019.graphicsLib.G;
import mid2019.graphicsLib.Window;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class Paint extends Window {

  static int paintedCount = 1;
  public static Path thePath = new Path();
  public static Path.List paths = new Path.List();

  public Paint() {
    super("Paint", 1000, 700);

  }

  @Override
  protected void paintComponent(Graphics g) {
    int x = 600, y = 200;
    G.fillBackground(g, Color.white);

    //g.setColor(Color.blue); //static method in color
    //g.setColor(G.rndColor());
    g.setColor(Color.blue);

    // g.drawString("paintedCount = " + (paintedCount++), 750, 100);
    g.drawString("clickCount = " + (clicks), 750, 100);
    g.fillRect(100, 100, 200, 300); //drawRect - only draw boundaries
    g.drawLine(100, 600, 600, 100);

    String s = "Hello";
    g.drawString(s, 600, 200); // coordinates are located in left bottom corner and straight line
    g.setColor(Color.red);
    g.drawOval(x, y, 3, 3);

    FontMetrics fm = g.getFontMetrics();
    int a = fm.getAscent(); // how far fonts sticks up! font's vertical upward height
    int d = fm.getDescent(); // how far fonts sticks up! font's vertical downward height
    int w = fm.stringWidth(s);

    g.drawRect(x, y - a, w, a + d);

    //thePath.draw(g);
    paths.draw(g);

    G.fillBackground(g, Color.white);
    //Squares s1 = new Squares();
  }

  public static int clicks = 0;

  @Override
  public void mousePressed(MouseEvent me) {
    //clicks++;
    //thePath.clear();
    thePath = new Path();
    thePath.add(me.getPoint());
    paths.add(thePath);
    repaint();

  }

  @Override
  public void mouseDragged(MouseEvent me) {
    thePath.add(me.getPoint());
    repaint();
  }

  public static class Path extends ArrayList<Point> {
    public void draw(Graphics g) {
      for(int i = 1; i < size(); i++) { //size() came from path then ArrayList. ArrayList has size method
        Point p = get(i - 1), n = get(i);
        g.drawLine(p.x, p.y, n.x, n.y);
      }
    }

    public static class List extends ArrayList<Path> {
      public void draw(Graphics g) {
        for(Path p : this) p.draw(g);

      }
    }

  }

}
