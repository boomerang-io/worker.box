package com.ibm.garage.box.vo;

import java.util.Date;
import com.box.sdk.BoxCollaboration.Role;
import lombok.Data;

@Data
public class BoxMember {
  private Collaboration collaboration;

  private String id;
  private String email;
  private String name;
  private Date createdAt;
  private Date modifiedAt;
  private Role role;
  private boolean canViewPath;
  private String inviteEmail;
}
