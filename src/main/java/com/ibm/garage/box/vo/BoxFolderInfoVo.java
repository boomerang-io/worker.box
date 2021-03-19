package com.ibm.garage.box.vo;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BoxFolderInfoVo extends BoxFolderVo {
  private String sequenceID;
  private String etag;
  private Date createdAt;
  private Date modifiedAt;
  private String description;
  private long size;
  private BoxUserVo owner;
  private BoxUserVo creator;
  private BoxUserVo modifier;
  private Date trashedAt;
  private Date purgedAt;
  private Date contentCreatedAt;
  private Date contentModifiedAt;
  private Date expiresAt;
  private String url;
  private String downloadUrl;
  private boolean isPasswordEnabled;
  private List<String> tags;
  private String status;
  private long previewCount;
  private long downloadCount;
}
