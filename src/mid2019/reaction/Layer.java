package mid2019.reaction;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import mid2019.I;

public class Layer extends ArrayList<I.Show> implements I.Show {
  public String name;
  public static HashMap<String, Layer> byName = new HashMap<>();
  public static Layer ALL = new Layer("ALL");

  public Layer(String name) {
    this.name = name;
    if(!name.equals("ALL")) {
      ALL.add(this);
      byName.put(name, this);
    }

  }


  @Override
  public void show(Graphics g) {
    for(I.Show item : this) {
      item.show(g);
    }
  }

  // clear all objects
  public static void nuke() {
    for(I.Show layer : ALL) {
      ((Layer)layer).clear();
    }

  }

  public void betterRemove(I.Show show){
    for(int i = 0; i < size(); i++){
      if(get(i) == show){ remove(i); break; }
    }
  }



}
