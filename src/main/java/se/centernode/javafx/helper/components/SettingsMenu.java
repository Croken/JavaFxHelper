package se.centernode.javafx.helper.components;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javafx.collections.ListChangeListener;
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
  
  Map<String, Pane> setingsContent;
  Pane settingsList;
  Pane treeView;
  Pane settingsPane;

  public SettingsMenu(Controller controller) {
    this.controller = controller;
    this.config = controller.getConfig();

    setingsContent = new HashMap<>();
    settingsList = new VBox();
    settingsList.setId("settings-container");
    settingsList.setMinWidth(200);
    HBox.setHgrow(settingsList, Priority.ALWAYS);
    treeView = new VBox(createTreeView());
    settingsPane = new HBox();
    settingsPane.setId("Settings-pane");
    settingsPane.getChildren().addAll(treeView, settingsList);
  }

  public Pane getSettings() {
    return settingsPane;
  }

  public void showSettingsDialog(Stage primaryStage) {
    Stage stage = new Stage();
    stage.initModality(Modality.WINDOW_MODAL);
    stage.initOwner(primaryStage);
    
    CustomWindow window = new CustomWindow(stage, controller, "settings");
    window.setContent(settingsPane);
    
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

  
  
  private TreeView<String> createTreeView() {
    
    Map<String, TreeItem<String>> settings = new TreeMap<>();
    
    
    
    TreeItem<String> currentItem;
    for (Object key : config.getPorpKeys()) {
      String s = key.toString();
      String[] split = s.split("\\.");
      String menuRoot = split[0];
      if(!settings.containsKey(menuRoot)) {
        settings.put(menuRoot, new TreeItem<String>(menuRoot));
      } 
      currentItem = settings.get(menuRoot);
      
      
      int i = 1;
      String tempKey = "";
      for (String part : split) {
        TreeItem<String> childWithName = getChildWithName(currentItem, part);
        if (childWithName == null) {
          if (i == split.length) {
            final String keyToPane = tempKey;
            addToPane(keyToPane, part);
          } else {
            TreeItem<String> newTI = new TreeItem<>(part);
            currentItem.getChildren().add(newTI);
            currentItem = newTI;
          }
        } else {
          currentItem = childWithName;
        }
        if (!tempKey.isEmpty()) {
          tempKey = tempKey.concat(".");
        }
        tempKey = tempKey.concat(part);
        i++;
      }
    }

    TreeView<String> tv = new TreeView<>(root);
    root.setExpanded(true);
    tv.setPrefWidth(200);
    tv.setOnMouseClicked((event) -> {
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
      settingsList.getChildren().clear();
      Pane pane = setingsContent.get(key);
      if (pane != null) {
        settingsList.getChildren().add(pane);
      }
    });
    tv.setMinWidth(100);
    tv.setPrefWidth(130);
    tv.setPrefHeight(200);
    return tv;
  }
  
  private Map<String, TreeItem<String>> poppulateSettings(){
    Map<String, TreeItem<String>> settings = new TreeMap<>();
    
    TreeItem<String> currentItem;
    for(Object key : config.getSortedPropertyKeys()) {
      String s = key.toString();
      String[] part = s.split("\\.");
      String menuRoot = part[0];
      
      if(!settings.containsKey(menuRoot)) {
        settings.put(menuRoot, new TreeItem<String>(menuRoot));
      } 
      currentItem = settings.get(menuRoot);
      
      TreeItem<String> childWithName;
      for(int index = 1; index < part.length; index++) {
        childWithName = getChildWithName(currentItem, part[index]);
        if(childWithName != null ) {
          
        }
      }
    }
    return settings;
  }

  private void addToPane(final String propertyKey, String name) {
    String fullKey = propertyKey + "." + name;

    Pane hBox = new HBox();
    hBox.getStyleClass().add("settings-row");
    
    Label text = new Label(name);
    text.setPrefWidth(100);
    TextField textField = new TextField();
    textField.setText((String) config.getProp().get(fullKey));
    textField.textProperty().addListener((obj, oldVal, newVal) -> {
      config.getProp().setProperty(fullKey, newVal);
    });

    hBox.getChildren().addAll(text, textField);
    
    if (setingsContent.containsKey(propertyKey)) {
      setingsContent.get(propertyKey).getChildren().add(hBox);
    } else {
      VBox vBox = new VBox();
      vBox.getStyleClass().add("settings-group");
      vBox.getChildren().add(hBox);
      setingsContent.put(propertyKey, vBox);

    }

  }

  private TreeItem<String> getChildWithName(TreeItem<String> ti, String name) {
    for (TreeItem<String> child : ti.getChildren()) {
      if (child.getValue().equals(name)) {
        return child;
      }
    }
    return null;
  }
}
