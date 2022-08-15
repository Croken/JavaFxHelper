package se.centernode.javafx.helper.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomWindow {
  protected final double DEFAULT_WIDTH = 300;
  protected final double DEFAULT_HEIGHT = 300;

  protected final double DEFAULT_POSITION_X = 0;
  protected final double DEFAULT_POSITION_Y = 0;

  protected StringProperty bottomBarInfo = new SimpleStringProperty();

  private final String xPropertyKey;
  private final String yPropertyKey;
  private final String withPropertyKey;
  private final String heightPropertyKey;
  private final String titlePropertyKey;

  private Stage stage;
  private String id;

  private Scene scene;
  private Config config;
  private BorderPane window;
  private Node content;
  private Pane topBar;
  private StringProperty windowTitle;
  private Runnable onCloseAction;
  private Logger log;
  private Pane bottomBar;


  public CustomWindow(Stage stage, Controller controller, String id) {
    this.stage = stage;
    this.id = id;
    this.config = controller.getConfig();
    log = LogManager.getLogger(this);

    xPropertyKey = id + ".window.x";
    yPropertyKey = id + ".window.y";
    withPropertyKey = id + ".window.width";
    heightPropertyKey = id + ".window.height";
    titlePropertyKey = id + ".window.TitelBarText";

    content = new Pane();
    stage.initStyle(StageStyle.UNDECORATED);
    window = initWindow();
    scene = initScene(window);
    stage.setScene(scene);
    stage.setX(config.getDoubleProperty(xPropertyKey, DEFAULT_POSITION_X));
    stage.setY(config.getDoubleProperty(yPropertyKey, DEFAULT_POSITION_Y));

    windowTitle.addListener(l -> {
      config.setProperty(titlePropertyKey, l.toString());
    });
  }

  private Scene initScene(Parent root) {
    Double width = config.getDoubleProperty(withPropertyKey, DEFAULT_WIDTH);
    Double height = config.getDoubleProperty(heightPropertyKey, DEFAULT_HEIGHT);
    log.debug(String.format("Created new window('%s'): width: %s  height: %s", id, width, height));
    return new Scene(root, width, height);
  }

  public Scene getScene() {
    return scene;
  }

  public void setContent(Node content) {
    this.content = content;
    window.setCenter(content);
  }

  public void setTitle(String title) {
    windowTitle.set(title);
  }

  public Node getContent() {
    return content;
  }

  public void reloadGUI() {
    window.getChildren().clear();
    initWindow();
  }

  public void addMenuBar(MenuBar menuBar) {
    topBar.getChildren().add(menuBar);
  }

  public void addToBottomPane(Node node) {
    bottomBar.getChildren().add(bottomBar.getChildren().size() - 1, node);
  }

  protected BorderPane initWindow() {
    HBox.setHgrow(content, Priority.ALWAYS);
    BorderPane window = new BorderPane();
    window.setId("custom-window");
    topBar = createTopBar();
    window.setTop(topBar);
    bottomBar = createBottomBar();
    window.setBottom(bottomBar);
    window.setLeft(createSidebarLeft());
    window.setRight(createSidebarRight());
    return window;
  }

  protected Node createSidebarRight() {
    HBox sideBar = new HBox();
    sideBar.getStyleClass().add("sidebar");
    sideBar.setOnMousePressed(pressEvent -> {
      sideBar.setOnMouseDragged(dragEvent -> {
        stage.setWidth(dragEvent.getSceneX());
        bottomBarInfo.set("Width: " + dragEvent.getSceneX());
      });
      sideBar.setOnMouseReleased(event -> {
        bottomBarInfo.set("");
        config.setProperty(withPropertyKey, scene.getWidth());
      });
    });
    return sideBar;
  }

  protected Node createSidebarLeft() {
    HBox sideBar = new HBox();
    sideBar.getStyleClass().add("sidebar");
    sideBar.setOnMousePressed(pressEvent -> {
      final double width = stage.getWidth();
      sideBar.setOnMouseDragged(dragEvent -> {
        double newWidth = width + pressEvent.getScreenX() - dragEvent.getScreenX();
        stage.setWidth(newWidth);
        stage.setX(dragEvent.getScreenX() - pressEvent.getX());
        bottomBarInfo.set("Width: " + newWidth);
      });
      sideBar.setOnMouseReleased(event -> {
        bottomBarInfo.set("");
        config.setProperty(withPropertyKey, scene.getWidth());
        config.setProperty(xPropertyKey, stage.getX());
      });
    });
    return sideBar;
  }

  protected Pane createBottomBar() {
    HBox bottomBarInfoPane = new HBox();
    HBox.setHgrow(bottomBarInfoPane, Priority.ALWAYS);
    bottomBarInfoPane.setAlignment(Pos.BOTTOM_LEFT);
    Label infoLable = new Label();
    infoLable.textProperty().bind(bottomBarInfo);
    bottomBarInfoPane.getChildren().add(infoLable);

    VBox resize = new VBox();
    VBox.setVgrow(resize, Priority.ALWAYS);
    resize.setAlignment(Pos.BOTTOM_RIGHT);
    resize.getChildren().add(new Label("  "));
    resize.setOnMousePressed(pressEvent -> {
      resize.setOnMouseDragged(dragEvent -> {
        stage.setWidth(dragEvent.getSceneX());
        stage.setHeight(dragEvent.getSceneY());
        bottomBarInfo.set(dragEvent.getSceneX() + " : " + dragEvent.getSceneY());
      });
      resize.setOnMouseReleased(event -> {
        bottomBarInfo.set("");
        config.setProperty(withPropertyKey, scene.getWidth());
        config.setProperty(heightPropertyKey, scene.getHeight());
      });
    });

    HBox bottomBar = new HBox();
    bottomBar.setId("bottombar");
    bottomBar.setAlignment(Pos.BOTTOM_RIGHT);
    bottomBar.getChildren().addAll(bottomBarInfoPane, resize);
    return bottomBar;
  }

  protected Pane createTopBar() {
    HBox top = new HBox();
    Label title = new Label(config.getProperty(titlePropertyKey, ""));
    windowTitle = title.textProperty();
    title.setAlignment(Pos.BASELINE_LEFT);
    HBox.setHgrow(title, Priority.ALWAYS);
    top.getChildren().add(title);

    Button exit = new Button("X");
    exit.setId("exit-button");
    exit.setOnAction(action -> {
      close();
      
    });
    HBox exitWrapper = new HBox();
    exitWrapper.getChildren().add(exit);
    HBox.setHgrow(exitWrapper, Priority.ALWAYS);
    exitWrapper.setAlignment(Pos.BASELINE_RIGHT);
    top.getChildren().add(exitWrapper);
    top.setOnMousePressed(pressEvent -> {
      boolean isFullScreen = stage.isFullScreen();
      top.setOnMouseDragged(dragEvent -> {
        double newX = Math.ceil(dragEvent.getScreenX() - pressEvent.getSceneX());
        if (isFullScreen) {
          stage.setFullScreen(false);
          newX = dragEvent.getScreenX() - (stage.getWidth() / 2);
        }
        double newY = Math.ceil(dragEvent.getScreenY() - pressEvent.getSceneY());
        stage.setX(newX);
        stage.setY(newY);
        bottomBarInfo.set(newX + " : " + newY);
      });
      top.setOnMouseReleased(event -> {
        bottomBarInfo.set("");
        config.setProperty(xPropertyKey, stage.getX());
        config.setProperty(yPropertyKey, stage.getY());
      });
    });
    top.setOnMouseClicked(event -> {
      if (event.getButton().equals(MouseButton.PRIMARY) & event.getClickCount() == 2) {
        stage.setFullScreen(!stage.isFullScreen());
      }
    });
    VBox topBar = new VBox();
    topBar.setId("topbar");
    topBar.getChildren().add(top);
    return topBar;
  }

  public void setOnCloseActoin(Runnable action) {
    this.onCloseAction = action;
  }
  
  public void close() {
    log.debug(String.format("Close window('%s')", id));
    if (onCloseAction != null) {
      onCloseAction.run();
    }
    stage.close();
  }
}
