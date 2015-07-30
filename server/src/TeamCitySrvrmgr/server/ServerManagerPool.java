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
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.*;

/**
 * Created by sbt-voroshilov-ss on 23.07.2015.
 */
public class ServerManagerPool {

  private List<ServerManagerInstance> Pool = new ArrayList<ServerManagerInstance>();
  private static String srvrmgrPath;

  public ServerManagerPool() throws IOException, InterruptedException, SrvrMgrException {
    srvrmgrPath = "c:\\siebel_port\\Siebel\\8.1\\Client_1\\BIN\\srvrmgr.exe";
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        try{
          checkPoolInstances(20000L);
        }
        catch (IOException e){}
        catch (SrvrMgrException e) {}
      }
    };
    timer.schedule(task, 0L, 20000L);
  }

  private void checkPoolInstances(long idleTime) throws IOException, SrvrMgrException {
    for (Iterator<ServerManagerInstance> it = this.Pool.iterator(); it.hasNext();) {
      ServerManagerInstance Instance = it.next();
      if (System.currentTimeMillis() - Instance.getLastCommandDate() > idleTime) {
        Instance.executeCommand("exit");
        Instance.getProcessInput().close();
        Instance.getProcessOutputStream().close();
//        Instance.getProcessOutput().close();
        Instance.getProcess().destroy();
        it.remove();
        System.out.println(this.Pool);
      }
    }
  }

  public List<ServerManagerInstance> getPool() {
    return Pool;
  }

  public ServerManagerInstance addSrvrMgr(String enterpriseName) throws IOException, SrvrMgrException {

    Map<String, String> enterpriseParams = getEnterpriseParameters(enterpriseName);

    String[] runTimeCommand = new String[]{
            srvrmgrPath,
            "/g", enterpriseParams.get("/g"),
//            "/s", "BTST1LD2",
            "/e", enterpriseParams.get("/e"),
            "/u", enterpriseParams.get("/u"),
            "/p", enterpriseParams.get("/p")
    };

    ServerManagerInstance SrvrMgrInstance = new ServerManagerInstance(runTimeCommand, enterpriseName);

    Pool.add(SrvrMgrInstance);

    return SrvrMgrInstance;
  }

  private Map<String, String> getEnterpriseParameters(String enterpriseName) {
    Map<String, String> result = new HashMap<String, String>();

    if (enterpriseName.equals("btst1ld1")) {
      result.put("/s", "btst1ld1");
      result.put("/u", "sadmin");
      result.put("/p", "sadminsbt");
      result.put("/g", "btst1ld1.cgs.sbrf.ru");
      result.put("/e", enterpriseName);
    }
    return result;
  }

  @Nullable
  public ServerManagerInstance getFreeSrvrmgr(String enterpriseName) {

    for (ServerManagerInstance Instance : Pool) {
      if (Instance.getEnterpriseName().equals(enterpriseName) && !Instance.getLocked()) {
        return Instance;
      }
    }
    return null;
  }
}
