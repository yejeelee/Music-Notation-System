package mid2019.music;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import mid2019.UC;
import mid2019.graphicsLib.G;
import mid2019.graphicsLib.Window;
import mid2019.reaction.Gesture;
import mid2019.reaction.Ink;
import mid2019.reaction.Layer;
import mid2019.reaction.Reaction;

public class AaMusic extends Window {
  public static Layer BACK = new Layer("BACK");
  public static Layer NOTE = new Layer("NOTE");
  public static Layer FORE = new Layer("FORE");
  // public static Page PAGE;

  public AaMusic() {
    super("Music1", UC.mainWindowWidth, UC.mainWindowHeight);
    Reaction.initialReactions.addReaction(new Reaction("E-W") {
      public int bid(Gesture g) { return 10; }
      public void act(Gesture g) {
        int y = g.vs.yM();
        Sys.Fmt sysFmt = new Sys.Fmt();
        Page.PAGE = new Page(sysFmt);
        Page.PAGE.margins.top = y;
        Page.PAGE.addNewSys();
        Page.PAGE.addNewStaff(0);
        this.disable();
      }
    });
  }

  //public static int[] xPoly = {100, 200, 200, 100};
  //public static int[] yPoly = {50, 70, 80, 60};
  //public static Polygon poly = new Polygon(xPoly, yPoly, 4);

  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.white);
    Ink.BUFFER.show(g);
    g.setColor(Color.black);
    Layer.ALL.show(g);
    if (Page.PAGE != null) {
      Glyph.CLEF_G.showAt(g, 8, 100, Page.PAGE.margins.top + 4 * 8);
      //Glyph.HEAD_BREVE.showAt(g, 8, 200, Page.PAGE.margins.top + 4 * 8);
    }
  }

  @Override
  public void mousePressed(MouseEvent me) { Gesture.AREA.dn(me.getX(), me.getY()); repaint(); }

  @Override
  public void mouseDragged(MouseEvent me) { Gesture.AREA.drag(me.getX(), me.getY()); repaint(); }

  @Override
  public void mouseReleased(MouseEvent me) { Gesture.AREA.up(me.getX(), me.getY()); repaint(); }
}
