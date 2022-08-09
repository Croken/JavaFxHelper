package se.centernode.javafx.helper.components;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
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

public class SettingsMenu {
  protected Controller controller;
  protected Config config;
  
  Map<String, Pane> settingsViews;
  Pane settingsFullView;
  Pane settingsMenu;
  Pane settingsDisplay;
  private Map<String, TreeItem<String>> settings;

  public SettingsMenu(Controller controller) {
    this.controller = controller;
    this.config = controller.getConfig();
    settingsViews = new HashMap<>();
    settings = new TreeMap<>();
    
    settingsDisplay = new VBox();
    settingsDisplay.setId("settings-container");
    settingsDisplay.setMinWidth(200);
    HBox.setHgrow(settingsDisplay, Priority.ALWAYS);
    
    settingsMenu = createMenu(config);
    
    settingsFullView = new HBox();
    settingsFullView.setId("Settings-pane");
    settingsFullView.getChildren().addAll(settingsMenu, settingsDisplay);
  }

  public Pane getSettings() {
    return settingsFullView;
  }

  public void showSettingsDialog(Stage primaryStage) {
    Stage stage = new Stage();
    stage.initModality(Modality.WINDOW_MODAL);
    stage.initOwner(primaryStage);
    
    CustomWindow window = new CustomWindow(stage, controller, "settings");
    window.setContent(settingsFullView);
    
    Scene scene = window.getScene();
    stage.setScene(scene);
    stage.show();
    scene.getStylesheets().addAll(primaryStage.getScene().getStylesheets());
    primaryStage.getScene().getStylesheets().addListener(new ListChangeListener<String>() {
      @Override
      public void onChanged(Change<? extends String> change) {
        System.out.println("reload dialog css");
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(primaryStage.getScene().getStylesheets());
      }
    });
    stage.setOnCloseRequest(e -> {
      config.saveConfig();
      scene.setRoot(new Pane());
    });
  }

  private Pane createMenu(Config config) {
    VBox view = new VBox();
    for (Object key : config.getPorpKeys()) {
      String keyString = key.toString();
      String menuRoot = getKeyRoot(keyString);
      // Add new root menu if first time
      if (!settings.containsKey(menuRoot)) {
        view.getChildren().add(createTreeView(menuRoot));
      }
      addMissingMenuBranch(keyString);
      addToSettingsView(keyString);
    }
    return view;
  }
  
  private TreeView<String> createTreeView(String name){
    System.out.println("New menu root: " + name);
    TreeItem<String> root = new TreeItem<String>(name);
    settings.put(name, root);
    TreeView<String> tv = new TreeView<>(root);
    tv.setOnMouseClicked((event) -> {
      actionSelectTreeRoot(tv);
    });
    tv.setMinWidth(100);
    tv.setPrefWidth(130);
    tv.setPrefHeight(200);
    return tv;
  }

  private void actionSelectTreeRoot(TreeView<String> tv) {
    MultipleSelectionModel<TreeItem<String>> selectionModel = tv.getSelectionModel();
    TreeItem<String> selectedItem = selectionModel.getSelectedItem();
    System.out.println(selectedItem);
    if (selectedItem == null) {
      System.out.println("No item Selected");
      return;
    }

    String key = selectedItem.getValue();
    TreeItem<String> parent = selectedItem.getParent();
    while (parent != null) {
      String parentName = parent.getValue();
      parent = parent.getParent();
      if (parent != null) {
        key = parentName.concat("." + key);
      }
    }
    settingsDisplay.getChildren().clear();
    Pane pane = settingsViews.get(key);
    if (pane != null) {
      settingsDisplay.getChildren().add(pane);
    }
  }
  
  private void addMissingMenuBranch(String key) {
    // Add new menu items
    String[] keySplit = splitKey(key);
    String currentKeyEntry = keySplit[0];
    String menyName = "";
    for (int i = 1; i < keySplit.length-1; i++) {
      menyName = keySplit[i];
      currentKeyEntry += "." + menyName;
      if (settings.containsKey(currentKeyEntry)) {
        continue;
      }
      TreeItem<String> newEntry = new TreeItem<>(menyName);
      System.out.println("Add missing menu: " + currentKeyEntry);
      settings.put(currentKeyEntry, newEntry);
      settings.get(getKeyParent(currentKeyEntry)).getChildren().add(newEntry);
    }
  }

  String[] splitKey(String key) {
    return key.split("\\.");
  }
  
  String getKeyRoot(String key) {
    return splitKey(key)[0];
  }
  
  String getKeyLeaf(String key) {
    String[] keySplit = splitKey(key);
    return keySplit[keySplit.length-1];
  }
  
  String getKeyParent(String key) {
    int leafLength = getKeyLeaf(key).length();
    return key.substring(0, key.length() - leafLength - 2);
  }
  
  
  private void addToSettingsView(String key) {
    String parentKey = getKeyParent(key);
    String leaf = getKeyLeaf(key);
    // Add settings field
    System.out.println("Full key: " + key);
    Pane hBox = new HBox();
    hBox.getStyleClass().add("settings-row");
    
    Label text = new Label(leaf);
    text.setPrefWidth(100);
    TextField textField = new TextField();
    textField.setText((String) config.getProp().get(key));
    textField.textProperty().addListener((obj, oldVal, newVal) -> {
      config.getProp().setProperty(key, newVal);
    });

    hBox.getChildren().addAll(text, textField);
    
    if (!settingsViews.containsKey(parentKey)) {
      System.out.println("Create new settings view: " + parentKey );
      VBox vBox = new VBox();
      vBox.getStyleClass().add("settings-group");
      settingsViews.put(parentKey, vBox);
    }

    settingsViews.get(parentKey).getChildren().add(hBox);
  }
}
