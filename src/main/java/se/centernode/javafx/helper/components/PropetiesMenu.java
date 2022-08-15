package se.centernode.javafx.helper.components;

import static se.centernode.javafx.helper.components.Config.DEVIDER;
import static se.centernode.javafx.helper.components.Config.getKeyLeaf;
import static se.centernode.javafx.helper.components.Config.getKeyParent;
import static se.centernode.javafx.helper.components.Config.splitKey;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PropetiesMenu {
  protected Controller controller;
  protected Config config;
  protected Logger log;

  CustomWindow window;
  Pane fullView;
  Pane menuDisplay;
  Pane propertiesDisplay;

  Map<String, Pane> propertyViews;
  Map<String, TreeItem<String>> menuItems;
  Map<String, TextField> propertyFields;

  Stage stage;
  TreeView<String> menuRoot;
  private Button saveButton, closeButton;
  private HBox buttonContainer;

  public PropetiesMenu(Controller controller) {
    this.controller = controller;
    this.config = controller.getConfig();

    log = LogManager.getLogger(this);
    propertyViews = new HashMap<>();
    propertyFields = new HashMap<>();
    menuItems = new TreeMap<>();

    // Update property fields when config file is updated.
    config.getLastSaveTimeProperty().addListener((obj, oldVal, newVal) -> {
      for (String key : propertyFields.keySet()) {
        propertyFields.get(key).setText(config.getProp().getProperty(key));
      }
    });

    // Mane content
    menuDisplay = createMenu(config);

    propertiesDisplay = new VBox();
    propertiesDisplay.setId("properties-display");
    HBox.setHgrow(propertiesDisplay, Priority.ALWAYS);

    fullView = new HBox();
    fullView.setId("properties-display-window");
    fullView.getChildren().addAll(menuDisplay, propertiesDisplay);

    // Bottom navigation
    saveButton = new Button("Save");
    saveButton.getStyleClass().add("primary");
    saveButton.setOnAction(event -> {
      config.saveConfig();
      onClose();
    });

    closeButton = new Button("Close");
    closeButton.setOnAction(event -> {
      config.loadConfig();
      onClose();
    });

    buttonContainer = new HBox();
    buttonContainer.getStyleClass().add("button-container");
    buttonContainer.getChildren().addAll(closeButton, saveButton);
    
  }

  public Pane getFullContentPane() {
    return fullView;
  }

  public Map<String, Pane> getPropertyViews() {
    return propertyViews;
  }

  public Map<String, TextField> getPropertyFields() {
    return propertyFields;
  }

  public void showSettingsDialog(Stage primaryStage, Modality modality) {
    stage = new Stage();
    stage.initModality(modality);
    stage.initOwner(primaryStage);
    stage.setOnCloseRequest(e -> {
      closeButton.fire();
    });
    
    window = new CustomWindow(stage, controller, "propperties-menu");
    window.setContent(fullView);
    window.addToBottomPane(buttonContainer);
    
    Scene scene = window.getScene();
    stage.setScene(scene);
    stage.show();
    
    scene.getStylesheets().addAll(primaryStage.getScene().getStylesheets());
    primaryStage.getScene().getStylesheets().addListener(new ListChangeListener<String>() {
      @Override
      public void onChanged(Change<? extends String> change) {
        log.debug("Reload settings dialog css");
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(primaryStage.getScene().getStylesheets());
      }
    });
  }

  protected void onClose() {
    window.close();
  }

  protected Pane createMenu(Config config) {
    menuRoot = createTreeView("root");
    VBox view = new VBox();
    view.getChildren().add(menuRoot);

    for (Object key : config.getSortedPropertyKeys()) {
      String keyString = key.toString();
      addMissingMenuBranch(keyString);
      addToSettingsView(keyString);
    }
    return view;
  }

  protected TreeView<String> createTreeView(String name) {
    log.debug("Create new menu: " + name);
    TreeItem<String> root = new TreeItem<String>(name);
    TreeView<String> tv = new TreeView<>(root);
    tv.setId("properties-menu");
    tv.setOnMouseClicked((event) -> {
      actionSelectTreeRoot(tv);
    });
    tv.setShowRoot(false);
    VBox.setVgrow(tv, Priority.ALWAYS);
    menuItems.put("", root);
    return tv;
  }

  protected void actionSelectTreeRoot(TreeView<String> tv) {
    MultipleSelectionModel<TreeItem<String>> selectionModel = tv.getSelectionModel();
    TreeItem<String> selectedItem = selectionModel.getSelectedItem();
    if (selectedItem == null) {
      log.debug("No menu item selected");
      return;
    }
    actionSelectMenuItem(selectedItem);
  }

  protected void actionSelectMenuItem(TreeItem<String> selectedItem) {
    propertiesDisplay.getChildren().clear();
    String key = getKey(selectedItem).substring(5);
    Pane pane = propertyViews.get(key);
    if (pane != null) {
      log.debug("Menu select: " + key);
      propertiesDisplay.getChildren().add(pane);
    } else {
      log.debug(String.format("No property view exists for %s. Existing views are: %s", key,
          propertyViews.keySet()));
    }
  }

  protected String getKey(TreeItem<String> selectedItem) {
    String key = selectedItem.getValue();
    TreeItem<String> parent = selectedItem.getParent();
    while (parent != null) {
      String parentName = parent.getValue();
      if (parent != null) {
        key = parentName.concat(DEVIDER + key);
      }
      parent = parent.getParent();
    }
    return key;
  }

  protected void addMissingMenuBranch(String key) {
    // Add new menu items
    String[] keySplit = splitKey(key);
    String currentKeyEntry = "";
    String menyName = "";
    for (int i = 0; i < keySplit.length - 1; i++) {
      menyName = keySplit[i];
      currentKeyEntry += menyName;
      if (menuItems.containsKey(currentKeyEntry)) {
        currentKeyEntry += DEVIDER;
        continue;
      }

      log.debug("Add missing menu: " + currentKeyEntry);
      TreeItem<String> newEntry = new TreeItem<>(menyName);
      menuItems.put(currentKeyEntry, newEntry);
      String keyParent = getKeyParent(currentKeyEntry);
      menuItems.get(keyParent).getChildren().add(newEntry);

      currentKeyEntry += DEVIDER;
    }
  }

  protected void addToSettingsView(String key) {
    String parentKey = getKeyParent(key);
    String leaf = getKeyLeaf(key);
    log.debug(String.format("Add property '%s' to group '%s'. ", leaf, parentKey));

    // Add settings field
    Pane row = new HBox();
    row.getStyleClass().add("property-row");

    Label propertyName = new Label(leaf);
    propertyName.getStyleClass().add("name");
    propertyName.setPrefWidth(100);

    TextField valueField = new TextField();
    valueField.getStyleClass().add("value-field");
    valueField.setText((String) config.getProp().get(key));
    valueField.textProperty().addListener((obj, oldVal, newVal) -> {
      config.getProp().setProperty(key, newVal);
    });
    propertyFields.put(key, valueField);
    row.getChildren().addAll(propertyName, valueField);


    getPropertyView(parentKey).getChildren().add(row);
  }

  protected Pane getPropertyView(String parentKey) {
    if (!propertyViews.containsKey(parentKey)) {
      log.debug("Create new property view: " + parentKey);
      VBox proppertyGroup = new VBox();
      proppertyGroup.getStyleClass().add("property-group");
      propertyViews.put(parentKey, proppertyGroup);
    }
    return propertyViews.get(parentKey);
  }
}
