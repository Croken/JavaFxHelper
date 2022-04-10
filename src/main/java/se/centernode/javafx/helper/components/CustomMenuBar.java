package se.centernode.javafx.helper.components;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class CustomMenuBar {
  protected final Controller controller;
  protected MenuBar menu;

  public CustomMenuBar(Controller controller) {
    this.controller = controller;
  }

  public MenuBar createMenuBar() {
    menu = new MenuBar();
    menu.getMenus().addAll(createFileMenu(), createEditMenu());
    return menu;
  }

  public MenuBar getMenuBar() {
    return menu;
  }

  Menu createFileMenu() {
    Menu file = new Menu("File");

    MenuItem settings = new MenuItem("Settings");
    settings.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    settings.setOnAction(e -> controller.showSettingsDialog());
    
    MenuItem quit = new MenuItem("quit");
    quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
    quit.setOnAction(e -> {
      controller.close();
    });

    file.getItems().addAll(settings, quit);
    return file;
  }

  Menu createEditMenu() {
    Menu edit = new Menu("Edit");

    MenuItem reloadCss = new MenuItem("Relaod css");
    reloadCss.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
    reloadCss.setOnAction(e -> {
      controller.reloadAllCss();
    });

    edit.getItems().addAll(reloadCss);
    return edit;
  }
}
