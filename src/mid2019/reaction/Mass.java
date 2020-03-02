package mid2019.reaction;

import java.awt.Graphics;
import mid2019.I;

public abstract class Mass extends Reaction.List implements I.Show {
  public Layer layer;

  public Mass(String layerName) {
    this.layer = Layer.byName.get(layerName);
    if(this.layer != null) {
      layer.add(this);
    } else {
      System.out.println("Bad layer name! " + layerName);
    }
  }

  public void deleteMass() {
    clearAll();
//    System.out.println("Remove from layer: " + this);
//    System.out.println("Layer now" + layer);
//    layer.remove(this);
    layer.betterRemove(this);
//    System.out.println("Layer after" + layer);
  }

  //do nothing here.
  public void show(Graphics g) { }

}
