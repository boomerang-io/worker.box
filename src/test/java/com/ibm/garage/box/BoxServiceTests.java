package com.ibm.garage.box;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.ibm.garage.box.service.BoxService;

@SpringBootTest
class BoxServiceTests {

  @Autowired
  private BoxService boxService;

  @Test
  public void contextLoads() {}

  @Test
  public void testCreateBoxFolder() {
    try {
      boxService.createBoxFolder("test team 4", "0");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetFolder() {
    try {
      boxService.getFolder("0");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetRootFolder() {
    try {
      boxService.getFolders();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetMembers() {
    try {
      boxService.getMembers("117381558596");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
