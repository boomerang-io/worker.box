package com.ibm.garage.box.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(EssentialsUtils.class);
  private static final String SEPARATOR = "/";
  private static final String TASK_INPUT_PROPS_FILENAME = "task.input.properties";
  private static final String PROPS_PATH = "props";

  public static void output(OutputMap map) {
    LOGGER.debug("output props: {}", map.content());
    outputCustomTask(map.map);
  }

  private static void outputCustomTask(Map<String, String> props) {
    try {
      File lifecycleDir = new File("/lifecycle");
      if (!lifecycleDir.exists()) {
        lifecycleDir.mkdir();
      }
      File file = new File("/lifecycle/env");

      BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
      for (Map.Entry<String, String> entry : props.entrySet()) {
        out.write("\n" + entry.getKey() + "=" + entry.getValue());
      }
      out.close();
    } catch (IOException e) {
      LOGGER.debug(String.format("failed to save props, reason: %s", e.getMessage()));
    }
  }

  public static Properties readProperties() {
    return readProperties(TASK_INPUT_PROPS_FILENAME);
  }

  private static Properties readProperties(String fileName) {
    Properties p = new Properties();
    try {
      File taskPropertiesFile = new File(SEPARATOR + PROPS_PATH + SEPARATOR + fileName);
      if (!taskPropertiesFile.exists()) {
        taskPropertiesFile = new File(PROPS_PATH + SEPARATOR + fileName);
      }
      p.load(new FileReader(taskPropertiesFile));
    } catch (IOException e) {
      LOGGER.debug("failed to load properties");
    }
    return p;
  }

  public static OutputMap map(String key, String value) {
    OutputMap om = new OutputMap();
    return om.add(key, value);
  }

  public static class OutputMap {
    private Map<String, String> map = new TreeMap<>();

    public OutputMap add(String key, String value) {
      map.put(key, value);
      return this;
    }

    public String content() {
      return map.keySet().stream().map(key -> key + "=" + map.get(key))
          .collect(Collectors.joining(", ", "{", "}"));
    }

    public String json() {
      return map.keySet().stream().map(key -> toJson(key) + ":" + toJson(map.get(key)))
          .collect(Collectors.joining(", ", "{", "}"));
    }

    private String toJson(String s) {
      if (s == null) {
        return null;
      }
      return "\"" + s + "\"";
    }
  }
}
