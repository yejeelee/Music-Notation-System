package mid2019.music;

import java.awt.Graphics;
import java.util.ArrayList;
import mid2019.UC;
import mid2019.reaction.Gesture;
import mid2019.reaction.Mass;
import mid2019.reaction.Reaction;

public class Page extends Mass {
  public Margins margins = new Margins();
  public Sys.Fmt sysFmt;
  public int nSys, sysGap;
  public ArrayList<Sys> sysList = new ArrayList<>();
  public static Page PAGE;
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