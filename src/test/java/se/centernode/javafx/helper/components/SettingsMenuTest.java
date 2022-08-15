package se.centernode.javafx.helper.components;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.centernode.javafx.helper.JavaFxTestBase;
import se.centernode.javafx.helper.TestBase;

class SettingsMenuTest extends TestBase {

  String testKey1 = "root.parent1.leaf1";
  String testKey2 = "root.parent1.parent2.leaf2";
  String testKey3 = "root.parent1.parent2.leaf3";

  static Stage primaryStage;
  Controller controller;
  Config config;
  PropetiesMenu settingsMenu;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    primaryStage = JavaFxTestBase.setUpBeforeClass();
  }

  @BeforeEach
  public void setUp() {
    System.out.println("setUp");
    controller = new TestController(primaryStage);
    config = controller.getConfig();
  }

  @Test
  public void verifyEmptyConfigForTests() {
    Set<Object> porpKeys = config.getPorpKeys();
    assertEquals(0, porpKeys.size(), "" + porpKeys);
  }

  @Test
  public void verifySettingsViewContent() {
    addTestConfigurations();
    settingsMenu = new PropetiesMenu(controller);
    Set<String> keySet = settingsMenu.propertyViews.keySet();
    assertEquals(2, keySet.size());
    assertTrue(keySet.contains("root.parent1"));
    assertTrue(keySet.contains("root.parent1.parent2"));
    
    Pane pane = settingsMenu.propertyViews.get("root.parent1");
    assertEquals(1, pane.getChildren().size());
    
    Pane pane2 = settingsMenu.propertyViews.get("root.parent1.parent2");
    assertEquals(2, pane2.getChildren().size());
    
  }

  private void addTestConfigurations() {
    config.setProperty(testKey1, "value1");
    config.setProperty(testKey2, "value2");
    config.setProperty(testKey3, 123d);
  }

  private class TestController extends Controller {

    public TestController(Stage primaryStage) {
      super(primaryStage);
      config = new TestConfig();
    }

    @Override
    public Scene createScene() {
      return new Scene(new VBox());
    }

  }

  private class TestConfig extends Config {
    @Override
    public void saveConfig() {
      // Do nothing
    }

    @Override
    public void loadConfig() {
      // Do nothing
    }
  }
}
