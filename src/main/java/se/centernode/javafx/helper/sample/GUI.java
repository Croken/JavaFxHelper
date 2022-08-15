package se.centernode.javafx.helper.sample;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.centernode.javafx.helper.components.Config;
import se.centernode.javafx.helper.components.Controller;
import se.centernode.javafx.helper.components.CustomMenuBar;
import se.centernode.javafx.helper.components.CustomWindow;

public class GUI {
  private final CustomMenuBar myMenuBar;
  Scene scene;
  VBox root;
  Controller controller;
  Stage stage;
  private CustomWindow window;

  public GUI(Controller controller, Stage primaryStage) {
    this.controller = controller;
    this.stage = primaryStage;
    
    window = new CustomWindow(stage, controller, "main");
    window.setContent(getMainView());
    
    myMenuBar = new CustomMenuBar(controller);
    window.addMenuBar(myMenuBar.createMenuBar());
    scene = window.getScene();
  }

  public Node getMainView() {
    VBox main = new VBox();
    main.setId("main-view");
    main.setAlignment(Pos.TOP_CENTER);
    Label label = new Label("Label");
    
    Button button = new Button("Button");
    button.setOnAction(event -> {
      System.out.println("test");
    });
    TextField textField = new TextField("temp text");
    HBox hBox = new HBox();
    hBox.setId("css-test-hbox");
    hBox.getChildren().addAll(label, button);

    TextArea textArea = new TextArea(
        "TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT TEXT ");
    textArea.setWrapText(true);

    main.getChildren().addAll(textField, hBox, textArea);
    return main;
  }

  public Scene getScene() {
    return scene;
  }

  public Controller getController() {
    return controller;
  }
  
  public void close() {
    window.close();
  }
}
