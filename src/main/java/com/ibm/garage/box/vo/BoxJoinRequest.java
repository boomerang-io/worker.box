package com.ibm.garage.box.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoxJoinRequest {
  private String email;
  private String role;
  private boolean notify = true;
  private boolean canViewPath;
}
