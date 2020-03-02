import mid2019.graphicsLib.Window;

import mid2019.music.AaMusic;
import mid2019.reaction.Shape;
import mid2019.sandbox.Music1;
import mid2019.sandbox.PaintInk;
import mid2019.sandbox.ReactionTest;
import mid2019.sandbox.Squares;

public class Main {


  public static void main(String[] args) {
    // Window.PANEL = new Paint();
    // Window.PANEL = new Squares();
    // Window.PANEL = new PaintInk();

    //Window.PANEL = new Shape.Trainer();

    //Window.PANEL = new ReactionTest();
    //Window.PANEL = new Music1();
    Window.PANEL = new AaMusic();

    Window.PANEL.launch();

  }

}
