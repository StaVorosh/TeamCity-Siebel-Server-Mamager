/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package TeamCitySrvrmgr.server;

/**
 * Created by sbt-voroshilov-ss on 21.07.2015.
 */

import java.lang.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerMgr {
  String srvrmgrPath/* = "c:\\siebel_port\\Siebel\\8.1\\Client_1\\BIN\\srvrmgr.exe"*/;

  public List<Map> getServers() throws Exception{
    String srvrmgrCommand = "list servers";
    return ExecuteCommand(srvrmgrCommand);
  }

  public ServerMgr(String srvrmgrPath) {
    this.srvrmgrPath = srvrmgrPath;
  }

  public List<Map> getCompsForServer(String serverHostName) throws Exception {
    String srvrmgrCommand = "list comp for server " + serverHostName;
    return ExecuteCommand(srvrmgrCommand);
  }

  public String killComp(String compName, String serverName) throws Exception {
    String srvrmgrCommand = "kill comp " + compName + " for server " + serverName;
    List<Map> result = ExecuteCommand(srvrmgrCommand);
    return getCompState(compName, serverName);
  }

  public String startComp(String compName, String serverName) throws Exception {
    String srvrmgrCommand = "start comp " + compName + " for server " + serverName;
    List<Map> result = ExecuteCommand(srvrmgrCommand);
    return getCompState(compName, serverName);
  }

  public String getCompState(String compName, String serverName) throws Exception {
    String srvrmgrCommand = "list comp " + compName + " for server " + serverName;
    List<Map> result = ExecuteCommand(srvrmgrCommand);
    Map<String, String> resLine = result.get(0);
    return resLine.get("CP_DISP_RUN_STATE");
  }

  void removeTmpCommFile(String fileName) {
    File f = new File(fileName);
    if (!f.delete())
      System.out.println("Error tmp file deletion");
  }

  Path generateTmpCommFile(String cmd) throws Exception {
    PrintWriter fout = null;
//    String pathToFile = "tmp";
    Path tmpFile = Files.createTempFile("srvrmgr_tc", ".txt");
    System.out.println("TROLOLOLOLO " + tmpFile.toString());
//    String filePath = tmpFile.toString() + "\\" + UUID.randomUUID().toString().replace("-", "") + ".txt";
//    File PATH = new File(pathToFile);
//    if(!PATH.mkdirs())
//      System.out.println("Cant create directory");
    try {
      fout = new PrintWriter(new FileWriter(tmpFile.toString()));
      fout.write(cmd);
    } catch (IOException e) {
      System.out.println("Cant write command" + e);
    } finally {
      if (fout != null)
        fout.close();
    }
    return tmpFile;
  }

  public List<Map> ExecuteCommand(String srvrmgrCommand) throws Exception {
    Path srvrmgrCommandFile = generateTmpCommFile(srvrmgrCommand);
    InputStream inStream = ExecRuntime(srvrmgrCommandFile.toString());
    List<Map> outData = getData(inStream);
    removeTmpCommFile(srvrmgrCommandFile.toString());
    return outData;
  }

  public InputStream ExecRuntime(String srvrmgrCommandFile) {
    InputStream inStream;
    try {
      Runtime runTime = Runtime.getRuntime();
      String[] runTimeCommand = new String[]{
              srvrmgrPath,
              "/g", "btst1ld1.cgs.sbrf.ru",
//                "/s", "BTST1LD2",
              "/e", "BTST1LD1",
              "/u", "sadmin",
              "/p", "sadminsbt",
              "/i", srvrmgrCommandFile,
              "/b"
      };
      Process pr = runTime.exec(runTimeCommand);
      inStream = pr.getInputStream();
    } catch (Exception e) {
      System.out.print("Error" + e);
      inStream = null;
    }
    return inStream;
  }

  public List<Map> getData(InputStream ins) {
    List<Map> tmp = new ArrayList<Map>();
    try {
      String line;
      Pattern pattern = Pattern.compile("srvrmgr[\\s\\S]*");
      BufferedReader bufReader = new BufferedReader(new InputStreamReader(ins));
      String headers = null, separators = null;
      while ((line = bufReader.readLine()) != null) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
          bufReader.readLine();
          headers = bufReader.readLine();
          separators = bufReader.readLine();
          break;
        }
      }

      List<Integer> sepLengths = calcSeparatorsLength(separators);
      List<String> listHeaders = parseLine(headers, sepLengths);

      while ((line = bufReader.readLine()) != null && !line.equals("")) {
        tmp.add(parseData(line, listHeaders, sepLengths));
      }

      while ((line = bufReader.readLine()) != null) {
      }
      bufReader.close();
    } catch (Exception e) {
      System.out.println("ERROR" + e);
    }
    return tmp;
  }

  public List<Integer> calcSeparatorsLength(String line) {
    List<Integer> columns = new ArrayList<Integer>();
    int colWidth = 0;

    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) != ' ')
        colWidth++;
      else {
        columns.add(colWidth);
        colWidth = 0;
        i++;
      }
    }
    return columns;
  }

  public List<String> parseLine(String line, List<Integer> sepLen) {
    int symbSum = 0;
    List<String> listStr = new ArrayList<String>();
    for (int i = 0; i < sepLen.size(); i++) {
      listStr.add(line.substring(symbSum, symbSum + sepLen.get(i)).trim());
      symbSum += sepLen.get(i) + 2;
    }

    return listStr;
  }

  public Map<String, String> parseData(String line, List<String> headers, List<Integer> sepLen) {
    List<String> listData = parseLine(line, sepLen);
    Map<String, String> tmp = new HashMap<String, String>();

    for (int i = 0; i < headers.size(); i++)
      tmp.put(headers.get(i), listData.get(i));

    return tmp;
  }

//  public static void main(String[] args) {
//    ServerMgr srvrmgr = new ServerMgr();
//    List<Map> servers = srvrmgr.getServers();
//    System.out.println(servers);
//    for (Map<String, String> server : servers) {
//      List<Map> components = srvrmgr.getCompsForServer(server.get("HOST_NAME"));
//      for(Map<String, String> comp : components)
//      {
//        System.out.println(comp);
//      }
//    }
//    System.out.println(srvrmgr.getCompState("SBRFOUIWfProcMgr", "btst1ld2"));
//  }
}
