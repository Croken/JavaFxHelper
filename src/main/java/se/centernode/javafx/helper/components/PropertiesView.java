package se.centernode.javafx.helper.components;

import javafx.scene.layout.Pane;

/**
 * Used together with PropertiesMenu to create a custom view for a properties group.
 * 
 * @author Anders Orback
 */
public interface PropertiesView {

  public String getPropertyGroupKey();

  public Pane getPropertiesView();


}
