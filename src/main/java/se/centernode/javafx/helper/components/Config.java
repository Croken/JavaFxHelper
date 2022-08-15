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
import javafx.beans.property.SimpleLongProperty;

public class Config {
  public final Logger log;
  public final static String DEVIDER = ".";
  private final String CONFIG_FILE = "/config.properties";
  
  Properties prop;
  private SimpleLongProperty lastSaveTime;

  public Config() {
    log = LogManager.getLogger(this);
    prop = new Properties();
    loadConfig();
    lastSaveTime = new SimpleLongProperty();
  }

  public Set<Object> getPorpKeys() {
    return prop.keySet();
  }

  public Properties getProp() {
    return prop;
  }
  
  public SimpleLongProperty getLastSaveTimeProperty() {
    return lastSaveTime;
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
    setProperty(key, String.valueOf(Math.ceil(value)));
  }

  public void setProperty(String key, String value) {
    prop.setProperty(key, value);
    saveConfig();
  }

  public void loadConfig() {
    log.info("Loading propertis from: " + CONFIG_FILE);
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
    log.debug(getFormatedPropertyString());
  }

  public void onClose() {
    log.info("Closing");
    saveConfig();
  }

  private String getFormatedPropertyString() {
    StringBuffer sb = new StringBuffer();
    sb.append("\n== Properties ==\n");
    for (Object key : getSortedPropertyKeys()) {
      sb.append(key.toString());
      sb.append(": '");
      sb.append(prop.getProperty(key.toString()));
      sb.append("'\n");
    }
    return sb.toString();
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
      log.info("Saving properties to: " + file.getCanonicalPath());
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
    lastSaveTime.set(System.currentTimeMillis());
    log.debug(getFormatedPropertyString());
  }
  
  static String[] splitKey(String key) {
    return key.split("\\.");
  }
  
  static String getKeyRoot(String key) {
    return splitKey(key)[0];
  }
  
  static String getKeyLeaf(String key) {
    String[] keySplit = splitKey(key);
    return keySplit[keySplit.length-1];
  }
  
  static String getKeyParent(String key) {
    int leafLength = getKeyLeaf(key).length();
    if (key.length() <= (leafLength )) {
      return "";
    }
    return key.substring(0, key.length() - leafLength - 1);
  }
}
