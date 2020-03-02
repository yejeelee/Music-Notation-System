package mid2019.sandbox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import mid2019.UC;
import mid2019.graphicsLib.G;
import mid2019.graphicsLib.Window;
import mid2019.reaction.Gesture;
import mid2019.reaction.Ink;
import mid2019.reaction.Layer;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;


// demo for big picture
public class Music1 extends Window {

  public static Layer BACK = new Layer("BACK");
  public static Layer FORE = new Layer("FORE");
  public static Page PAGE;

  public Music1() {
    super("Music1", UC.mainWindowWidth, UC.mainWindowHeight);
    Reaction.initialReactions.addReaction(new Reaction("E-W") {
      public int bid(Gesture g) { return 10; }

      public void act(Gesture g) {
        int y = g.vs.yM();
        Sys.Fmt sysFmt = new Sys.Fmt();
        PAGE = new Page(sysFmt);
        PAGE.margins.top = y;
        PAGE.addNewSys();
        PAGE.addNewStaff(0);
        this.disable();;
      }

    });
  }

  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.white);
    Ink.BUFFER.show(g);
    g.setColor(Color.black);
    Layer.ALL.show(g);
  }

  @Override
  public void mousePressed(MouseEvent me) { Gesture.AREA.dn(me.getX(), me.getY()); repaint(); }

  @Override
  public void mouseDragged(MouseEvent me) { Gesture.AREA.drag(me.getX(), me.getY()); repaint(); }

  @Override
  public void mouseReleased(MouseEvent me) { Gesture.AREA.up(me.getX(), me.getY()); repaint(); }

  //------------------------PAGE----------------------------
  public static class Page extends Mass {
    public Margins margins = new Margins();
    public Sys.Fmt sysFmt;
    public int nSys, sysGap;
    public ArrayList<Sys> sysList = new ArrayList<>();
    public Page(Sys.Fmt sysFmt) { super("BACK"); this.sysFmt = sysFmt;

      // add new Staff
      addReaction(new Reaction("E-W") {

        public int bid(Gesture g) {
          int y = g.vs.yM();
          if( y < PAGE.margins.top + sysFmt.height() + 30) { return UC.noBid; }
          return 15;
        }

        public void act(Gesture g) {
          int y = g.vs.yM();
          PAGE.addNewStaff(y - PAGE.margins.top);

        }
      });

      // add new System
      addReaction(new Reaction("E-E") {

        public int bid(Gesture g) {
          int y = g.vs.yM();
          int yBottom = PAGE.sysTop(nSys);
          if(y < yBottom) { return UC.noBid; }
          return 15;
        }

        public void act(Gesture g) {
          int y = g.vs.yM();
          if(PAGE.nSys == 1) {
            PAGE.sysGap = y - PAGE.sysTop(1);
          }
          PAGE.addNewSys();
        }
      });
    }

    public int sysTop(int iSys) {
      return margins.top + iSys * (sysGap + sysFmt.height());
    }

    public void show(Graphics g) {
      for(int i =0; i < nSys; i++) {
        sysFmt.showAt(g, sysTop(i));
      }
    }
    public void addNewSys() {
      sysList.add(new Sys(nSys++, sysFmt));
    }

    public void addNewStaff(int yOff) {
      Staff.Fmt sf = new Staff.Fmt();
      int n = sysFmt.size();
      sysFmt.add(sf);
      sysFmt.staffOffsets.add(yOff);
      for(int i = 0; i < nSys; i++) {
        sysList.get(i).addStaff(new Staff(n, sf));
      }

    }
    //--------------------PAGE.MARGINS-----------------------
    public static class Margins {
      public int top = 50, left = 50, right=UC.mainWindowWidth-50, bottom = UC.mainWindowHeight-50;
    }

  }

  //------------------------System---------------------------
  public static class Sys extends Mass {
    public ArrayList<Staff> staffs = new ArrayList<>();
    public Page page = PAGE;
    public int iSys;
    public Sys.Fmt fmt;

    public Sys(int iSys, Sys.Fmt sysFmt) {
      super("BACK");
      this.iSys = iSys;
      this.fmt = sysFmt;
      for(int i = 0; i < fmt.size(); i++) {
        addStaff(new Staff(i, fmt.get(i)));
      }
    }

    public int yTop() {
      return page.sysTop(iSys); //...
    }
    public int yBot() { return staffs.get(staffs.size() - 1).yBottom(); }

    public void show(Graphics g) {
      int y = yTop(); // get Top coordinates
      int x = PAGE.margins.left;
      g.drawLine(x, y, x, y + fmt.height()); //draw vertical line to add system
    }

    //helper function to add staff to system and list
    public void addStaff(Staff s) {
      staffs.add(s);
      s.sys = this;
    }

    //---------------------System.Format---------------------
    public static class Fmt extends ArrayList<Staff.Fmt> {
      public int maxH = UC.defaultStaffH;
      public ArrayList<Integer> staffOffsets = new ArrayList<>();
      public int height() {
        int last = size() - 1;
        return staffOffsets.get(last) + get(last).height();
      }
      public void showAt(Graphics g, int y) {
        for (int i = 0; i < size(); i++) {
          get(i).showAt(g, y + staffOffsets.get(i));
        }
      }

    }


  }

  //-----------------------STAFF-----------------------------
  public static class Staff extends Mass {
    public Sys sys;
    public int iStaff; // this staff's index in system
    public Staff.Fmt fmt;

    public Staff(int iStaff, Staff.Fmt staffFmt) {
      super("BACK");
      this.iStaff = iStaff;
      this.fmt = staffFmt;

      // measure bar reaction
      addReaction(new Reaction("S-S") { // drawing measure bar line
        public int bid(Gesture g) {
          int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
          if (x < PAGE.margins.left || x > (PAGE.margins.right + UC.barToMarginSnap)) {
            return UC.noBid;
          }
          //distance
          int d = Math.abs(y1- Staff.this.yTop()) + Math.abs(y2 -Staff.this.yBottom());
          return d < 30 ? d + UC.barToMarginSnap : UC.noBid;
        }

        @Override
        public void act(Gesture g) {
          new Bar(Staff.this.sys, g.vs.xM());
        }
      });

      addReaction(new Reaction("S-S") { // set bar continues
        @Override
        public int bid(Gesture g) {
          if(Staff.this.sys.iSys != 0) { return UC.noBid; }
          int y1 = g.vs.yL(), y2 = g.vs.yH();
          int iStaff = Staff.this.iStaff;

          //if this staff is the very last staff in the system
          if (iStaff == Staff.this.sys.fmt.size() - 1) { return UC.noBid; }
          if (Math.abs(y1 - Staff.this.yBottom()) > 25) { return UC.noBid; }
          // if this staff ybottom is close to y1, then we compare staff right after to see if this staff yTop is closer to y2.
          Staff nextStaff = Staff.this.sys.staffs.get(iStaff + 1);
          if(Math.abs(y2 - nextStaff.yTop()) > 25) { return UC.noBid; }
          return 10;
        }

        @Override
        public void act(Gesture g) {
          //switching the barContinues from true to false and false to true.
          Staff.this.fmt.barContinues = !Staff.this.fmt.barContinues;
        }
      });
    }
    public int sysOff() {
      return sys.fmt.staffOffsets.get(iStaff);
    }

    public int yTop() {
      return sys.yTop() + sysOff();
    }

    public int yBottom() {
      return yTop() + fmt.height();
    }

    //-------------------STAFF.FORMAT------------------------
    public static class Fmt {
      public int nLines = 5;    // default
      public int H = UC.defaultStaffH;         // half height of two lines
      public boolean barContinues = false;
      public int height() { return (nLines - 1) * H * 2; }
      public void showAt(Graphics g, int y) {
        int left = PAGE.margins.left, right = PAGE.margins.right;
        for (int i = 0; i < nLines; i++) {
          int yLine = y + 2 * H * i;
          g.drawLine(left, yLine, right, yLine);
        }
      }

    }
  }

  //------------------Measure BAR------------------
  //double measure BAR - is to change the key
  // fine(Fat single line) -finishing line
  public static class Bar extends Mass {
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
      if(Math.abs(x - PAGE.margins.right) < UC.barToMarginSnap) {
        this.x = PAGE.margins.right;
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
          drawDots(g, x, yTop);
        }
      }
    }

    public void cycleType() { barType++; if(barType > 2) barType = 0; }

    // ^ means XOR
    //toggleLeft = shift to the left
    public void toggleLeft() { barType = barType ^ LEFT; }

    public void toggleRight() { barType = barType ^ RIGHT; }

    //dx = thickness
    public static void fatBar(Graphics g, int x, int y1, int y2, int dx) {
      g.fillRect(x, y1, dx, y2 - y1);
    }
    public static void thinBar(Graphics g, int x, int y1, int y2) {
      g.drawLine(x, y1, x, y2);
    }

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
      int H = PAGE.sysFmt.maxH;
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
}
