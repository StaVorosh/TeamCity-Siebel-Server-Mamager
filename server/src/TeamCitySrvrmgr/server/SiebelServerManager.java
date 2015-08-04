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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sbt-voroshilov-ss on 21.07.2015.
 */
public class SiebelServerManager {

  private ServerManagerPool srvrmgrPool;
//  public String srvrmgrPath = "c:\\siebel_port\\Siebel\\8.1\\Client_1\\BIN\\srvrmgr.exe";

  public SiebelServerManager(@NotNull ServerManagerPool srvrmgrPool) {
    this.srvrmgrPool = srvrmgrPool;
  }

  public List<Map> getServers(String enterpriseName) throws IOException, SrvrMgrException {
    String command = "list servers";
    return this.execute(enterpriseName, command);
  }

  public List<Map> getServerComps(String enterpriseName, String serverHostName) throws IOException, SrvrMgrException {
    String command = "list comp for server " + serverHostName;
    return this.execute(enterpriseName, command);
  }

  public List<Map> killComp(String enterpriseName, String serverHostName, String compName) throws IOException, SrvrMgrException {
    String command = "kill comp " + compName + " for server " + serverHostName;
    return this.execute(enterpriseName, command);
  }

  public List<Map> startComp(String enterpriseName, String serverHostName, String compName) throws IOException, SrvrMgrException {
    String command = "start comp " + compName + " for server " + serverHostName;
    return this.execute(enterpriseName, command);
  }

  public List<Map> restartComp(final String enterpriseName, final String serverHostName, final String compName) throws IOException, SrvrMgrException {
    List<Map> killResult = this.killComp(enterpriseName, serverHostName, compName);
    List<Map> listResult = this.listComp(enterpriseName, serverHostName, compName);
    return killResult;
  }

  public String getCompState (String enterpriseName, String serverHostName, String compName) throws IOException, SrvrMgrException {
     return listComp(enterpriseName, serverHostName, compName).get(0).get("CP_DISP_RUN_STATE").toString();
  }

  public List<Map> listComp(String enterpriseName, String serverHostName, String compName) throws IOException, SrvrMgrException {
    String command = "list comp " + compName + " for server " + serverHostName;
    return this.execute(enterpriseName, command);
  }

  public List<Map> execute(String enterpriseName, String command) throws IOException, SrvrMgrException {
    for (ServerManagerInstance Instance : srvrmgrPool.getPool()) {
      if (Instance.getEnterpriseName().equals(enterpriseName) && !Instance.getLocked()) {
        return getData(Instance.executeCommand(command));
      }
    }
    return getData(srvrmgrPool.addSrvrMgr(enterpriseName).executeCommand(command));
  }

  private List<Map> getData(String inString) throws SrvrMgrException{
    List<Map> tmp = new ArrayList<Map>();
    String[] arrString = inString.split("\r\n");
    String headers = arrString[1], separators = arrString[2];

    List<Integer> sepLengths = calcSeparatorsLength(separators);
    List<String> listHeaders = parseLine(headers, sepLengths);

    int i = 3;

    while (i < arrString.length && !arrString[i].equals("")) {
      tmp.add(parseData(arrString[i], listHeaders, sepLengths));
      i++;
    }

    return tmp;
  }

  private List<Integer> calcSeparatorsLength(String line) throws SrvrMgrException{
    List<Integer> columns = new ArrayList<Integer>();
    int colWidth = 0;

    Pattern p = Pattern.compile("[^-\\s]+");
    Matcher m = p.matcher(line);

    if(m.matches() || !(line.trim().length()>0))
      throw new SrvrMgrException("Error. Separators");

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

  private List<String> parseLine(String line, List<Integer> sepLen) {
    int symbSum = 0;
    List<String> listStr = new ArrayList<String>();
    for (int i = 0; i < sepLen.size(); i++) {
      listStr.add(line.substring(symbSum, symbSum + sepLen.get(i)).trim());
      symbSum += sepLen.get(i) + 2;
    }

    return listStr;
  }

  private Map<String, String> parseData(String line, List<String> headers, List<Integer> sepLen) {
    List<String> listData = parseLine(line, sepLen);
    Map<String, String> tmp = new HashMap<String, String>();

    for (int i = 0; i < headers.size(); i++)
      tmp.put(headers.get(i), listData.get(i));

    return tmp;
  }

  public List<Map> getProcesses(){
    List<Map> processList = new ArrayList<Map>();
    for(Iterator<ServerManagerInstance> it = this.srvrmgrPool.getPool().iterator(); it.hasNext();)
    {
      ServerManagerInstance Instance = it.next();
      Map<String, Object> instanceData = new HashMap<String, Object>();
      instanceData.put("PID", Instance.getPID());
      instanceData.put("LastCommandDate", new Date(Instance.getLastCommandDate()));
      instanceData.put("Enterprise", Instance.getEnterpriseName());
      instanceData.put("Locked",Instance.getLocked());
      processList.add(instanceData);
    }
    return processList;
  }

  /*TODO: debug*/
  public List<Map> getProcessLog(int PID)
  {
    for(Iterator<ServerManagerInstance> it = this.srvrmgrPool.getPool().iterator(); it.hasNext();)
    {
      ServerManagerInstance Instance = it.next();
      if(Instance.getPID() == PID)
        return Instance.getProcessLog();
    }
    return null;
  }
}
