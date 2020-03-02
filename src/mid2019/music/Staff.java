package mid2019.music;

import java.awt.Graphics;
import mid2019.UC;
import mid2019.reaction.Gesture;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;

public class Staff extends Mass {
  public Sys sys;
  public int iStaff;
  public Staff.Fmt fmt;


  public Staff(int iStaff, Staff.Fmt staffFmt) {
    super("BACK"); this.iStaff = iStaff; fmt = staffFmt;
    addReaction(new Reaction("S-S") {                       // draw a bar line
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        if (x < Page.PAGE.margins.left || x > (Page.PAGE.margins.right + UC.barToMarginSnap)) { return UC.noBid; }
        int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBottom());
        return d < 30 ? d + UC.barToMarginSnap : UC.noBid;
      }

      public void act(Gesture g) {
        new Bar(Staff.this.sys, g.vs.xM());
      }
    });
    addReaction(new Reaction("S-S") {                       // set barContinues
      @Override
      public int bid(Gesture g) {
        if (Staff.this.sys.iSys != 0) { return UC.noBid; }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        int iStaff = Staff.this.iStaff;
        if (iStaff == Staff.this.sys.fmt.size() - 1) { return UC.noBid; }
        if (Math.abs(y1 - Staff.this.yBottom()) > 25) { return UC.noBid; }
        Staff nextStaff = Staff.this.sys.staffs.get(iStaff + 1);
        if (Math.abs(y2 - nextStaff.yTop()) > 25) { return UC.noBid; }
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Staff.this.fmt.barContinues = !Staff.this.fmt.barContinues;
      }
    });

    addReaction(new Reaction("SW-SW") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        int h = Staff.this.H(), top = Staff.this.yTop() - h, bottom = Staff.this.yBottom() + h;
        if (x < Page.PAGE.margins.left || x > Page.PAGE.margins.right) { return UC.noBid; }
        if (y < top || y > bottom) { return UC.noBid; }
        return 10;
      }

      @Override
      public void act(Gesture g) {
        new Head(Staff.this, g.vs.xM(), g.vs.yM());
      }
    });

    addReaction(new Reaction("E-S") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < Page.PAGE.margins.left || x > Page.PAGE.margins.right) { return UC.noBid; }
        int h = Staff.this.H(), top = Staff.this.yTop() - h, bottom = Staff.this.yBottom() + h;
        if (y < top || y > bottom) { return UC.noBid; }
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        new Rest(Staff.this, t).nFlag = 1;
      }
    });

    addReaction(new Reaction("W-S") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < Page.PAGE.margins.left || x > Page.PAGE.margins.right) { return UC.noBid; }
        int h = Staff.this.H(), top = Staff.this.yTop() - h, bottom = Staff.this.yBottom() + h;
        if (y < top || y > bottom) { return UC.noBid; }
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        new Rest(Staff.this, t);
      }
    });
  }

  public int sysOff() { return sys.fmt.staffOffsets.get(iStaff); }
  public int yTop() { return sys.yTop() + sysOff(); }
  public int yBottom() { return yTop() + fmt.height(); }
  public int yLine(int n) { return yTop() + n * H(); }
  public int lineOfY(int y) {
    int h = H(), bias = 100;
    int top = yTop() - h * bias;
    return (y - top + h / 2) / h - bias;
  }
  public int H() {return fmt.H; }


  //-------------------STAFF.FORMAT------------------------
  public static class Fmt {
    public int nLines = 5;    // default
    public int H = UC.defaultStaffH;         // half height of two lines
    public boolean barContinues = false;
    public int height() {
      return (nLines - 1) * H * 2;
    }
    public void showAt(Graphics g, int y) {
      int left = Page.PAGE.margins.left, right = Page.PAGE.margins.right;
      for (int i = 0; i < nLines; i++) {
        int yLine = y + 2 * H * i;
        g.drawLine(left, yLine, right, yLine);
      }
    }

  }
}
