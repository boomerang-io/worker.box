package com.ibm.garage.box.vo;

import java.util.Date;
import com.box.sdk.BoxCollaboration.Status;
import lombok.Data;

@Data
public class Collaboration {
  private String id;
  private Status status;
  private Date createdAt;
  private Date modifiedAt;
  private Date expiresAt;
  private Date acknowledgedAt;
}
