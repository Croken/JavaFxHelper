package se.centernode.javafx.helper.components;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConfigTest {
  String testKey = "root.parent1.parent2.leaf";
  
  @Test
  void testSplitKey() {
    assertEquals(4, Config.splitKey(testKey).length);
  }

  @Test
  void testGetKeyRoot() {
    assertEquals("root", Config.getKeyRoot(testKey));
  }

  @Test
  void testGetKeyLeaf() {
    assertEquals("leaf", Config.getKeyLeaf(testKey));
  }

  @Test
  void testGetKeyParent() {
    assertEquals("root.parent1.parent2", Config.getKeyParent(testKey));
  }

}
