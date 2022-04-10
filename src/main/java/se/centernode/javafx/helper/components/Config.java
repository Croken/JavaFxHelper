package se.centernode.javafx.helper.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
  public final Logger log;
  private final String CONFIG_FILE = "/config.properties";
  Properties prop;

  public Config() {
    log = LogManager.getLogger(this);
    prop = new Properties();
    loadConfig();
    printProperties();
  }

  public Set<Object> getPorpKeys() {
    return prop.keySet();
  }

  public Properties getProp() {
    return prop;
  }

  public double getDoubleProperty(String key, double defaultValue) {
    String stringVal = getProperty(key, String.valueOf(defaultValue));
    return Double.valueOf(stringVal);
  }

  public String getProperty(String key, String defaultValue) {
    if (prop.containsKey(key)) {
      return prop.getProperty(key);
    }
    prop.setProperty(key, defaultValue);
    return defaultValue;
  }

  public String getProperty(String key) {
    return prop.getProperty(key);
  }

  public void setProperty(String key, Double value) {
    setProperty(key, String.valueOf(value));
  }

  public void setProperty(String key, String value) {
    prop.setProperty(key, value);
    saveConfig();
  }

  private void loadConfig() {
    InputStream input = getClass().getResourceAsStream(CONFIG_FILE);
    try {
      prop.load(input);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void onClose() {
    System.out.println("Closing");
    saveConfig();
    printProperties();
  }

  private void printProperties() {
    log.info("\n== Current Config ==");
    for (Object key : getSortedPropertyKeys()) {
      log.info(key.toString() + ": '" + prop.getProperty(key.toString()) + "'");
    }
    log.info("\n");
  }

  public Object[] getSortedPropertyKeys() {
    Set<Object> keySet = prop.keySet();
    Object[] array = keySet.toArray();
    Arrays.sort(array, (o1, o2) -> o1.toString().compareTo(o2.toString()));
    return array;
  }
  
  public void saveConfig() {
    OutputStream fileOut = null;
    OutputStreamWriter out = null;
    try {
      File file = new File(getClass().getResource(CONFIG_FILE).toURI());
      fileOut = new FileOutputStream(file);
      out = new OutputStreamWriter(fileOut);
      for (Object key : getSortedPropertyKeys()) {
        out.append(key + "=" + prop.getProperty(key.toString()));
        out.append(System.lineSeparator());
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (out != null)
          out.close();
        if (fileOut != null)
          fileOut.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
