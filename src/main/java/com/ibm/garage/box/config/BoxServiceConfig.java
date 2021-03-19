package com.ibm.garage.box.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@ConfigurationProperties(prefix = "spring.box")
@Component
@Data
public class BoxServiceConfig {
  private String jsonFile;
  private String configJson;
  private String enterpriseId;
  private String clientId;
  private String clientSecret;
  private String publicKeyId;
  private String privateKey;
  private String passphrase;
}
