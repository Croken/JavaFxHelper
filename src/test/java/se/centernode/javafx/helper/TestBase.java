package se.centernode.javafx.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import se.centernode.javafx.helper.components.Config;

public class TestBase {
  protected Logger log= LogManager.getLogger(this);
  
  @Before
  public void setUp() {
    log.info("Test setUp");
  }
  

}
