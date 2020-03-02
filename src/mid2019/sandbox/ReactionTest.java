package mid2019.sandbox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import mid2019.UC;
import mid2019.graphicsLib.G;
import mid2019.graphicsLib.Window;
import mid2019.reaction.Gesture;
import mid2019.reaction.Ink;
import mid2019.reaction.Layer;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;

public class ReactionTest extends Window {

  public static Layer back = new Layer("back");
  public static Layer fore = new Layer("fore");

  public ReactionTest() {
    super("ReactionTest", UC.mainWindowWidth, UC.mainWindowHeight);

    Reaction.initialReactions.addReaction( new Reaction("SW-SW") {
      public int bid(Gesture g) { return 5; }
      public void act(Gesture g) { new Box(g.vs);}
    });
  }


  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.WHITE);
    g.setColor(Color.BLUE);
    Ink.BUFFER.show(g);
    Layer.ALL.show(g);
  }

  @Override
  public void mousePressed(MouseEvent me) {
    Gesture.AREA.dn(me.getX(), me.getY());
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    Gesture.AREA.drag(me.getX(), me.getY());
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    Gesture.AREA.up(me.getX(), me.getY());
    repaint();
  }

  //whenever nesting classes always use static
  // Mass insist of G.VS
  // Mass is a reaction list
  public static class Box extends Mass {
    public G.VS vs;
    public Color color = G.rndColor();
    public Box(G.VS vs) {
      super("back");
      this.vs = vs;
      addReaction(new Reaction("S-S") {
        public int bid(Gesture g) {
          int x = g.vs.xM();
          int y = g.vs.yL();
          if(Box.this.vs.hitDetection(x, y)) {
            return Math.abs(x - Box.this.vs.xM());
          } else {
            return UC.noBid;
          }
        }
        public void act(Gesture g) { Box.this.deleteMass(); }
      });


      addReaction(new Reaction("DOT") {
        public int bid(Gesture g) {
          int x = g.vs.xM();
          int y = g.vs.yL();
          if(Box.this.vs.hitDetection(x, y)) {
            return Math.abs(x - Box.this.vs.xM() + Math.abs(y - Box.this.vs.yM()));
          } else {
            return UC.noBid;
          }
        }

        public void act(Gesture g) { Box.this.color = G.rndColor(); }
      });

    }

    public void show(Graphics g) { vs.fill(g, color); }
  }


}
