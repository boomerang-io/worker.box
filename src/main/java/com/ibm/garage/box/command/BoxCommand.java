package com.ibm.garage.box.command;

import static com.ibm.garage.box.util.EssentialsUtils.map;
import static com.ibm.garage.box.util.EssentialsUtils.output;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.garage.box.service.BoxService;
import com.ibm.garage.box.util.EssentialsUtils.OutputMap;
import com.ibm.garage.box.vo.BoxFolderInfoVo;
import com.ibm.garage.box.vo.BoxFolderVo;
import com.ibm.garage.box.vo.BoxJoinRequest;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "box")
public class BoxCommand implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(BoxCommand.class);

  private static final String STATUS = "status";
  private static final String ERROR = "error";
  private static final String OK = "OK";

  @Autowired
  private BoxService boxService;

  public CommandLine getCommandLine() {
    return new CommandLine(this);
  }

  @Override
  public void run() {}

  @Command(name = "list")
  public void list() {
    try {
      List<BoxFolderVo> folders = boxService.getFolders();
      ObjectMapper mapper = new ObjectMapper();
      output(map(STATUS, OK).add("folders", mapper.writeValueAsString(folders)));
    } catch (Exception e) {
      errorOutput(e);
    }
  }

  @Command(name = "add")
  public void add(@Parameters(index = "0", paramLabel = "<folderName>") String folderName,
      @Option(names = {"-p", "--parentFolderId"}, paramLabel = "<parentFolderId>",
          defaultValue = "0") String parentFolderId) {
    try {
      String folderId = boxService.createBoxFolder(folderName, parentFolderId);
      output(map(STATUS, OK).add("id", folderId));
    } catch (Exception e) {
      errorOutput(e);
    }
  }

  @Command(name = "join")
  public void join(@Parameters(index = "0", paramLabel = "<folderId>") String folderId,
      @Parameters(index = "1", paramLabel = "<email>") String email,
      @Option(names = {"-r", "--role"}, paramLabel = "<role>", defaultValue = "EDITOR") String role,
      @Option(names = {"-n", "--notify"}, paramLabel = "<notify>",
          defaultValue = "true") String notify,
      @Option(names = {"-vp", "--viewPath"}, paramLabel = "<canViewPath>",
          defaultValue = "false") String canViewPath) {
    try {
      BoxJoinRequest request =
          new BoxJoinRequest(email, role, Boolean.valueOf(notify), Boolean.valueOf(canViewPath));
      boxService.addMember(folderId, request);
      output(map(STATUS, OK));
    } catch (Exception e) {
      errorOutput(e);
    }
  }

  @Command(name = "leave")
  public void leave(@Parameters(index = "0", paramLabel = "<folderId>") String folderId,
      @Parameters(index = "1", paramLabel = "<email>") String email) {
    try {
      boxService.removeMember(folderId, email);
      output(map(STATUS, OK));
    } catch (Exception e) {
      errorOutput(e);
    }
  }

  @Command(name = "remove")
  public void remove(@Parameters(index = "0", paramLabel = "<folderId>") String folderId) {
    try {
      boxService.deleteFolder(folderId);
      output(map(STATUS, OK));
    } catch (Exception e) {
      errorOutput(e);
    }
  }

  @Command(name = "upload")
  public void upload(@Parameters(index = "0", paramLabel = "<folderId>") String folderId,
      @Parameters(index = "1", paramLabel = "<file>") String file) {
    try {
      File f = new File(file);
      String id = boxService.upload(folderId, f.getName(), file);
      output(map(STATUS, OK).add("id", id));
    } catch (Exception e) {
      errorOutput(e);
    }
  }

  @Command(name = "download")
  public void download(@Parameters(index = "0", paramLabel = "<fileId>") String fileId,
      @Parameters(index = "1", paramLabel = "<file>") String file) {
    try {
      boxService.download(fileId, file);
      output(map(STATUS, OK));
    } catch (Exception e) {
      errorOutput(e);
    }
  }

  @Command(name = "info")
  public String info(@Parameters(index = "0", paramLabel = "<folderId>") String folderId) {
    try {
      BoxFolderInfoVo folder = boxService.getFolder(folderId);
      ObjectMapper mapper = new ObjectMapper();
      OutputMap output = map(STATUS, OK).add("folder", mapper.writeValueAsString(folder));
      return mapper.writeValueAsString(output);
    } catch (Exception e) {
      OutputMap output = map(STATUS, "Error").add(ERROR, e.getMessage());
      return output.json();
    }
  }

  private void errorOutput(Exception e) {
    LOGGER.error("Box error", e);
    output(map(STATUS, "Error").add(ERROR, e.getMessage()));
  }

}
