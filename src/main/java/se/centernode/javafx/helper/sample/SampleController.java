package se.centernode.javafx.helper.sample;

import javafx.scene.Scene;
import javafx.stage.Stage;
import se.centernode.javafx.helper.components.Controller;

public class SampleController extends Controller {
  GUI gui;
  
  public SampleController(Stage primaryStage) {
    super(primaryStage);

  }

  @Override
  public Scene createScene() {
    gui = new GUI(this, primaryStage);
    return gui.getScene();
  }
  
  @Override
  public void close() {
    gui.close();
    super.close();
  }
  
  

}
