package com.ibm.garage.box.service.impl;

import static java.util.stream.Collectors.toList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.box.sdk.BoxCollaboration;
import com.box.sdk.BoxCollaboration.Role;
import com.box.sdk.BoxCollaborator.Info;
import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxSharedLink;
import com.box.sdk.BoxUser;
import com.box.sdk.EncryptionAlgorithm;
import com.box.sdk.IAccessTokenCache;
import com.box.sdk.InMemoryLRUAccessTokenCache;
import com.box.sdk.JWTEncryptionPreferences;
import com.ibm.garage.box.config.BoxServiceConfig;
import com.ibm.garage.box.service.BoxService;
import com.ibm.garage.box.vo.BoxFolderInfoVo;
import com.ibm.garage.box.vo.BoxFolderVo;
import com.ibm.garage.box.vo.BoxJoinRequest;
import com.ibm.garage.box.vo.BoxMember;
import com.ibm.garage.box.vo.BoxUserVo;
import com.ibm.garage.box.vo.Collaboration;

@Service
public class BoxServiceImpl implements BoxService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BoxServiceImpl.class);

  private static final Integer MAX_CACHE_ENTRIES = 100;
  private static final String PARENT_FOLDER_ID = "0";

  @Autowired
  private BoxServiceConfig config;

  private BoxDeveloperEditionAPIConnection api;

  private IAccessTokenCache accessTokenCache;

  public BoxServiceImpl() {
    accessTokenCache = new InMemoryLRUAccessTokenCache(MAX_CACHE_ENTRIES);
  }

  private BoxDeveloperEditionAPIConnection getAPI() {
    if (api != null) {
      return api;
    } else {
      try {
        LOGGER.debug("box config file is :{}", config.getJsonFile());
        LOGGER.debug("box config json is :{}", config.getConfigJson());
        BoxConfig boxConfig = null;
        if (config.getConfigJson() != null && !config.getConfigJson().isEmpty()) {
          boxConfig = BoxConfig.readFrom(new StringReader(config.getConfigJson()));
        } else if (config.getJsonFile() != null && !config.getJsonFile().isEmpty()) {
          boxConfig = BoxConfig.readFrom(new FileReader(config.getJsonFile()));
        }
        if (boxConfig != null) {
          api = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig,
              accessTokenCache);
        } else {
          JWTEncryptionPreferences encryptionPref = new JWTEncryptionPreferences();
          encryptionPref.setPublicKeyID(config.getPublicKeyId());
          encryptionPref.setPrivateKey(config.getPrivateKey());
          encryptionPref.setPrivateKeyPassword(config.getPassphrase());
          encryptionPref.setEncryptionAlgorithm(EncryptionAlgorithm.RSA_SHA_256);
          api =
              BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(config.getEnterpriseId(),
                  config.getClientId(), config.getClientSecret(), encryptionPref, accessTokenCache);
        }
        return api;
      } catch (Exception e) {
        LOGGER.error("Box api config error.", e);
        return null;
      }
    }
  }

  @Override
  public String createBoxFolder(String name, String parentFolderId) throws IOException {
    if (parentFolderId == null || parentFolderId.isEmpty()) {
      parentFolderId = PARENT_FOLDER_ID;
    }
    BoxFolder parentFolder = new BoxFolder(getAPI(), parentFolderId);
    BoxFolder.Info childFolderInfo = parentFolder.createFolder(name);
    return childFolderInfo.getID();
  }

  @Override
  public void deleteFolder(String id) throws IOException {
    BoxFolder folder = new BoxFolder(getAPI(), id);
    folder.delete(true);
  }

  @Override
  public boolean addMember(String folderId, BoxJoinRequest request) throws IOException {
    Role role = Stream.of(Role.values()).filter(r -> r.name().equals(request.getRole())).findAny()
        .orElseThrow(() -> new IllegalArgumentException("Unknown role"));
    BoxFolder f = new BoxFolder(getAPI(), folderId);
    f.collaborate(request.getEmail(), role, request.isNotify(), request.isCanViewPath());
    return true;
  }

  @Override
  public List<BoxMember> getMembers(String folderId) throws IOException {
    BoxFolder folder = new BoxFolder(getAPI(), folderId);
    Collection<BoxCollaboration.Info> collaborations = folder.getCollaborations();
    return collaborations.stream().map(this::toBoxMember).collect(toList());
  }

  @Override
  public boolean removeMember(String folderId, String email) throws IOException {
    List<BoxMember> members = getMembers(folderId);
    BoxMember member = members.stream().filter(m -> email.equals(m.getEmail())).findAny()
        .orElseThrow(() -> new RuntimeException(
            "Collaboration for folder " + folderId + " and email = " + email + " not found!"));
    removeMember(member.getCollaboration().getId());
    return true;
  }

  @Override
  public boolean removeMember(String collaborationId) throws IOException {
    BoxCollaboration collaboration = new BoxCollaboration(getAPI(), collaborationId);
    collaboration.delete();
    return true;
  }

  @Override
  public List<BoxFolderVo> getFolders() throws IOException {
    BoxFolder rootFolder = BoxFolder.getRootFolder(getAPI());
    List<BoxFolderVo> folderItems = new ArrayList<>();
    for (BoxItem.Info itemInfo : rootFolder) {
      folderItems.add(toBoxFolderVo(itemInfo));
    }
    return folderItems;
  }

  @Override
  public BoxFolderInfoVo getFolder(String folderId) throws IOException {
    BoxFolder folder = new BoxFolder(getAPI(), folderId);
    return toBoxFolderInfoVo(folder.getInfo());
  }

  @Override
  public String upload(String folderId, String name, String filePath) throws IOException {
    BoxFolder folder = new BoxFolder(getAPI(), folderId);
    BoxFile.Info info = folder.uploadFile(new FileInputStream(filePath), name);
    return info.getID();
  }

  @Override
  public void download(String fileId, String filePath) throws IOException {
    BoxFile file = new BoxFile(getAPI(), fileId);
    file.download(new FileOutputStream(filePath));
  }

  private BoxMember toBoxMember(BoxCollaboration.Info info) {
    BoxMember boxMember = new BoxMember();
    boxMember.setCollaboration(toCollaboration(info));

    Info accessibleBy = info.getAccessibleBy();
    if (accessibleBy != null) {
      boxMember.setId(info.getAccessibleBy().getID());
      boxMember.setEmail(info.getAccessibleBy().getLogin());
      boxMember.setName(info.getAccessibleBy().getName());
      boxMember.setCreatedAt(info.getAccessibleBy().getCreatedAt());
      boxMember.setModifiedAt(info.getAccessibleBy().getModifiedAt());
    } else {
      boxMember.setEmail(info.getInviteEmail());
    }

    boxMember.setRole(info.getRole());
    boxMember.setCanViewPath(info.getCanViewPath());
    boxMember.setInviteEmail(info.getInviteEmail());

    return boxMember;
  }

  private Collaboration toCollaboration(BoxCollaboration.Info info) {
    Collaboration collaboration = new Collaboration();
    collaboration.setId(info.getID());
    collaboration.setStatus(info.getStatus());
    collaboration.setCreatedAt(info.getCreatedAt());
    collaboration.setAcknowledgedAt(info.getAcknowledgedAt());
    collaboration.setExpiresAt(info.getExpiresAt());
    collaboration.setModifiedAt(info.getModifiedAt());

    return collaboration;
  }

  private BoxFolderVo toBoxFolderVo(BoxItem.Info itemInfo) {
    BoxFolderVo boxFolderVo = new BoxFolderVo();
    boxFolderVo.setId(itemInfo.getID());
    boxFolderVo.setName(itemInfo.getName());
    boxFolderVo.setType(itemInfo.getType());

    return boxFolderVo;
  }

  private BoxFolderInfoVo toBoxFolderInfoVo(BoxItem.Info itemInfo) {
    BoxFolderInfoVo boxFolderInfoVo = new BoxFolderInfoVo();
    boxFolderInfoVo.setId(itemInfo.getID());
    boxFolderInfoVo.setName(itemInfo.getName());
    boxFolderInfoVo.setType(itemInfo.getType());
    boxFolderInfoVo.setSequenceID(itemInfo.getSequenceID());
    boxFolderInfoVo.setEtag(itemInfo.getEtag());
    boxFolderInfoVo.setCreatedAt(itemInfo.getCreatedAt());
    boxFolderInfoVo.setModifiedAt(itemInfo.getModifiedAt());
    boxFolderInfoVo.setDescription(itemInfo.getDescription());
    boxFolderInfoVo.setSize(itemInfo.getSize());
    boxFolderInfoVo.setCreator(toBoxUserVo(itemInfo.getCreatedBy()));
    boxFolderInfoVo.setModifier(toBoxUserVo(itemInfo.getModifiedBy()));
    boxFolderInfoVo.setOwner(toBoxUserVo(itemInfo.getOwnedBy()));
    boxFolderInfoVo.setTrashedAt(itemInfo.getTrashedAt());
    boxFolderInfoVo.setPurgedAt(itemInfo.getPurgedAt());
    boxFolderInfoVo.setContentCreatedAt(itemInfo.getContentCreatedAt());
    boxFolderInfoVo.setContentModifiedAt(itemInfo.getContentModifiedAt());
    boxFolderInfoVo.setExpiresAt(itemInfo.getExpiresAt());
    boxFolderInfoVo.setTags(itemInfo.getTags());
    boxFolderInfoVo.setStatus(itemInfo.getItemStatus());
    BoxSharedLink sharedLink = itemInfo.getSharedLink();
    if (sharedLink != null) {
      boxFolderInfoVo.setUrl(sharedLink.getURL());
      boxFolderInfoVo.setDownloadUrl(sharedLink.getDownloadURL());
      boxFolderInfoVo.setPasswordEnabled(sharedLink.getIsPasswordEnabled());
      boxFolderInfoVo.setPreviewCount(sharedLink.getPreviewCount());
      boxFolderInfoVo.setDownloadCount(sharedLink.getDownloadCount());
    }

    return boxFolderInfoVo;
  }

  private BoxUserVo toBoxUserVo(BoxUser.Info boxUser) {
    if (boxUser == null) {
      return null;
    }

    BoxUserVo boxUserVo = new BoxUserVo();
    boxUserVo.setId(boxUser.getID());
    boxUserVo.setName(boxUser.getName());
    boxUserVo.setEmail(boxUser.getLogin());

    return boxUserVo;
  }

}
