package com.ibm.garage.box.command;

import com.ibm.garage.box.base.BaseServiceOptions;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "box")
public class BoxOptions implements Runnable, BaseServiceOptions {

  @Option(names = {"-c", "--config"})
  private String configJson;

  @Option(names = {"-e", "--enterpriseId"})
  private String enterpriseId;

  @Option(names = {"--clientId"})
  private String clientId;

  @Option(names = {"--clientSecret"})
  private String clientSecret;

  @Option(names = {"--publicKeyId"})
  private String publicKeyId;

  @Option(names = {"--privateKey"})
  private String privateKey;

  @Option(names = {"--passphrase"})
  private String passphrase;

  @Parameters()
  private String[] args = new String[0];

  @Override
  public void run() {
    if (configJson != null) {
      System.setProperty("spring.box.configJson", configJson);
    }
    if (enterpriseId != null) {
      System.setProperty("spring.box.enterpriseId", enterpriseId);
    }
    if (clientId != null) {
      System.setProperty("spring.box.clientId", clientId);
    }
    if (clientSecret != null) {
      System.setProperty("spring.box.clientSecret", clientSecret);
    }
    if (publicKeyId != null) {
      System.setProperty("spring.box.publicKeyId", publicKeyId);
    }
    if (privateKey != null) {
      System.setProperty("spring.box.privateKey", privateKey);
    }
    if (passphrase != null) {
      System.setProperty("spring.box.passphrase", passphrase);
    }
  }

  @Override
  public String[] getArgs() {
    return args;
  }
}
