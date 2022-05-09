package com.ibm.garage.box.service;

import java.io.IOException;
import java.util.List;
import com.ibm.garage.box.vo.BoxFolderInfoVo;
import com.ibm.garage.box.vo.BoxFolderVo;
import com.ibm.garage.box.vo.BoxJoinRequest;
import com.ibm.garage.box.vo.BoxMember;

public interface BoxService {

  String createBoxFolder(String name, String parentFolderId) throws IOException;

  void deleteFolder(String id) throws IOException;

  boolean addMember(String folderId, BoxJoinRequest request) throws IOException;

  List<BoxMember> getMembers(String folderId) throws IOException;

  boolean removeMember(String folderId, String email) throws IOException;

  boolean removeMember(String collaborationId) throws IOException;

  List<BoxFolderVo> getFolders() throws IOException;

  BoxFolderInfoVo getFolder(String folderId) throws IOException;

  /**
   * Upload a file to the desired Box folder. If a file with the same name already exists, upload a
   * new version.
   * 
   * @param folderId - the ID of the Box folder where we want to upload the file
   * @param name - the name of the uploaded file
   * @param filePath - the file path to the uploaded file
   * @return the ID of the uploaded file
   * @throws IOException
   */
  String upload(String folderId, String name, String filePath) throws IOException;

  void download(String fileId, String filePath) throws IOException;
}
