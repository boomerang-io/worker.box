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
      File tektonDir = new File("/tekton/results");
      if (!tektonDir.exists()) {
        tektonDir.mkdirs();
      }

      for (String key : props.keySet()) {
        File file = null;
        BufferedWriter out = null;
        try {
          file = new File(String.format("/tekton/results/%s", key));
          out = new BufferedWriter(new FileWriter(file, true));
          out.write(props.get(key));
        } catch (IOException ioe) {
          LOGGER.error(String.format("unable to save property named %s, reason: %s", key, ioe.getMessage()));
        } finally {
          out.close();
        }
      }
    } catch (IOException e) {
      LOGGER.debug(String.format("failed to save properties, reason: %s", e.getMessage()));
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
  }
}
