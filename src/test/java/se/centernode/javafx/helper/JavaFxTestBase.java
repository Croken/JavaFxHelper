package se.centernode.javafx.helper;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JavaFxTestBase {
  private static Stage primaryStage;
  
  public static Stage setUpBeforeClass() throws Exception {
    System.out.println("Initiating JavaFx...");
    CountDownLatch latch = new CountDownLatch(1);
    Platform.startup(() -> {
      primaryStage = new Stage();
      System.out.println("JavaFx: enabled"); 
      latch.countDown();
    });
    latch.await();
    return primaryStage;
  }
}
