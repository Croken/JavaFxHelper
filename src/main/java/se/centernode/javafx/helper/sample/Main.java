package se.centernode.javafx.helper.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import se.centernode.javafx.helper.components.Controller;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Controller controller = new SampleController(primaryStage);
    controller.init();
  }
}
