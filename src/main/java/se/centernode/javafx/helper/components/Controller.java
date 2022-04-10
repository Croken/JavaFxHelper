package se.centernode.javafx.helper.components;

import java.io.File;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class Controller {
  private final String DEFAULT_CSS_FILE = "javafx_helper_styles.css";

  protected Config config;
  protected Stage primaryStage;
  protected Scene scene;
  protected SettingsMenu settingsMenu;

  private Logger log;

  public Controller(Stage primaryStage) {
    this.primaryStage = primaryStage;
    log = LogManager.getLogger(this);

    config = new Config();
    settingsMenu = new SettingsMenu(this);
    scene = createScene();
  }

  public void init() {
    reloadAllCss();
    primaryStage.close();
    primaryStage.show();
  }

  public Config getConfig() {
    return config;
  }

  public void showSettingsDialog() {
    settingsMenu.showSettingsDialog(primaryStage);
  }

  public abstract Scene createScene();

  public void close() {
    config.onClose();
    primaryStage.close();
  }

  public void reloadAllCss() {
    ObservableList<String> stylesheets = scene.getStylesheets();
    stylesheets.clear();

    // Default css is loaded first so that others can replace its content.
    URL defaultCssURL = getClass().getResource("/" + DEFAULT_CSS_FILE);
    if (defaultCssURL != null) {
      stylesheets.add(defaultCssURL.toString());
    }

    // Load remaining css files.
    URL cssFloderURL = getClass().getResource("/css");
    if (cssFloderURL != null) {
      File cssDir = new File(cssFloderURL.getFile());
      for (File cssFile : cssDir.listFiles()) {
        String name = cssFile.getName();
        if (!name.endsWith(".css") || name.endsWith(DEFAULT_CSS_FILE)) {
          continue;
        }
        stylesheets.add(cssFile.toURI().toString());
      }
    }


    for (String css : stylesheets) {
      log.info("Css file loaded: " + css);
    }
  }

}
