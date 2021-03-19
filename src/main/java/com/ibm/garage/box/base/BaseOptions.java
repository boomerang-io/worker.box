package com.ibm.garage.box.base;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "base")
public class BaseOptions implements Runnable {

  @Override
  public void run() {
    // do nothing for base options
  }

}
