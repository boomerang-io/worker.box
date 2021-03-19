package com.ibm.garage.box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import com.ibm.garage.box.base.BaseCommand;
import com.ibm.garage.box.base.BaseOptions;
import com.ibm.garage.box.base.BaseServiceOptions;
import com.ibm.garage.box.command.BoxCommand;
import com.ibm.garage.box.command.BoxOptions;
import com.ibm.garage.box.util.EssentialsUtils;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

@SpringBootApplication(exclude = {JmxAutoConfiguration.class})
public class Application implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  @Autowired
  private BoxCommand boxCommand;

  @Autowired
  private BaseCommand baseCommand;

  public static void main(String[] args) {
    LOGGER.debug("args before options processing: " + Arrays.toString(args));

    args = processInputParameters(args);

    BaseOptions option = new BaseOptions();
    CommandLine cl = new CommandLine(option);
    cl.setUnmatchedOptionsArePositionalParams(true);
    cl.addSubcommand("box", new BoxOptions());
    for (CommandLine sub : cl.getSubcommands().values()) {
      sub.setUnmatchedOptionsArePositionalParams(true);
    }
    cl.parseWithHandler(new CommandLine.RunLast(), args);
    try {
      cl.parseArgs(args);
    } catch (Exception e) {
      System.exit(1);
    }
    ParseResult result = cl.getParseResult();
    if (result.hasSubcommand()) {
      CommandLine sub = result.subcommand().asCommandLineList().get(0);
      BaseServiceOptions options = sub.getCommand();
      List<String> argList = new ArrayList<>();
      argList.add(sub.getCommandName());
      argList.addAll(Arrays.asList(options.getArgs()));
      SpringApplication.run(Application.class, argList.toArray(new String[argList.size()]));
    }
  }

  @Override
  public void run(String... args) throws Exception {
    LOGGER.debug("args after options processing: " + Arrays.toString(args));
    CommandLine cl = new CommandLine(baseCommand);
    cl.addSubcommand("box", boxCommand.getCommandLine());
    cl.parseWithHandler(new CommandLine.RunLast(), args);
  }

  private static String[] processInputParameters(String[] args) {
    if ("-props".equals(args[0])) {
      LOGGER.debug("start template task");

      List<String> argList = new ArrayList<>();
      if (args.length >= 3) {
        argList.add(args[1]);
        argList.add(args[2]);
      }

      Properties props = EssentialsUtils.readProperties();

      for (int i = 3; i < args.length; i++) {
        String value = props.getProperty(args[i], "");
        argList.add(value);
        props.remove(args[i]);
      }

      for (String key : props.stringPropertyNames()) {
        argList.add("--" + key);
        argList.add(props.getProperty(key));
      }

      args = argList.toArray(args);
    }

    return args;
  }

}
