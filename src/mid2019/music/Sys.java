package mid2019.music;

import java.awt.Graphics;
import java.util.ArrayList;
import mid2019.UC;
import mid2019.music.Time.List;
import mid2019.reaction.Gesture;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;

public class Sys extends Mass {

  public ArrayList<Staff> staffs = new ArrayList<>();
  public Page page = Page.PAGE;
  public int iSys;
  public Sys.Fmt fmt;
  public Time.List times;
  public Stem.List stems = new Stem.List();

  public Sys(int iSys, Sys.Fmt sysFmt) {
    super("BACK");
    this.iSys = iSys;
    this.fmt = sysFmt;
    this.times = new Time.List(this);
    for (int i = 0; i < fmt.size(); i++) {
      addStaff(new Staff(i, fmt.get(i)));
    }
    times = new Time.List(this);

    // can add W-W to eliminate the beam
    addReaction(new Reaction("E-E") {
      @Override
      public int bid(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        if(stems.fastReject(y1, y2)) { return UC.noBid; }
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        if(temp.size() < 2) {return UC.noBid;}
        System.out.println("Crossed: " + temp.size() + " stems. ");
        Beam b = temp.get(0).beam;
        for(Stem s : temp) {
          if(s.beam != b) { return UC.noBid; }
        }
        System.out.println("All stems share owner.");
        if(b == null && temp.size() != 2) { return UC.noBid; }
        if(b == null && (temp.get(0).nFlag != 0 || temp.get(1).nFlag != 0)) { return UC.noBid;}
        return 50;
      }

      @Override
      public void act(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        Beam b = temp.get(0).beam;
        if(b == null) {
          b = new Beam(temp.get(0), temp.get(1));
        } else {
          for(Stem s : temp) {
            s.incFlag();
          }
        }
      }
    });
  }

  public int yTop() {
    return page.sysTop(iSys); //...
  }

  public int yBot() { return staffs.get(staffs.size() - 1).yBottom(); }

  public Time getTime(int x) { return times.getTime(x); }

  public void show(Graphics g) {
    int y = yTop(); // get Top coordinates
    int x = Page.PAGE.margins.left;
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