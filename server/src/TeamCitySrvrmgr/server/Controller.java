package TeamCitySrvrmgr.server;

import com.google.gson.Gson;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import TeamCitySrvrmgr.common.Util;
import org.springframework.web.servlet.view.RedirectView;
import sun.org.mozilla.javascript.internal.json.JsonParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example custom page controller
 */
public class Controller extends BaseController {
  @NotNull
  private PluginDescriptor myPluginDescriptor;
  private SiebelServerManager mySiebelServerManager;

  private static final String SERVERS_LIST = "ServersList.jsp";
  private static final String SERVER_COMPONENTS_LIST = "ServerComponentsList.jsp";
  private static final String PID_LIST = "PIDList.jsp";
  private static final String PROCESS_LOG = "ProcessLog.jsp";
  private static final String CONTROLLER_PATH = "/TeamCitySrvrmgr.html";
  private static final String ERROR_PAGE = "SrvrMgrErrorPage.jsp";

  public Controller(@NotNull PluginDescriptor pluginDescriptor, @NotNull WebControllerManager manager, SiebelServerManager siebelServerManager) {
    myPluginDescriptor = pluginDescriptor;
    mySiebelServerManager = siebelServerManager;
    // this will make the controller accessible via <teamcity_url>\TeamCitySrvrmgr.html
    manager.registerController("/TeamCitySrvrmgr.html", this);
  }

  @Override
  protected ModelAndView doHandle(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) throws Exception {
    try {
      String action = request.getParameter("action");

      String enterpriseName = "btst1ld1";

      if (action == null || action.trim().length() == 0)
        action = "listServers";
      else
        action = action.trim();

      if (action.equals("listServers")) {
        List<Map> serverList;
        serverList = mySiebelServerManager.getServers(enterpriseName);
        ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(SERVERS_LIST));
        final Map<String, Object> model = view.getModel();
        model.put("pluginName", Util.PLUGIN_NAME);
        model.put("serverList", serverList);
        return view;
      }
      else if (action.equals("listComps")) {
        List<Map> paramList;
        paramList = mySiebelServerManager.getServerComps(enterpriseName, request.getParameter("server"));
        ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(SERVER_COMPONENTS_LIST));
        final Map<String, Object> model = view.getModel();
        model.put("pluginName", Util.PLUGIN_NAME);
        model.put("controllerPath", CONTROLLER_PATH);
        model.put("serverName", request.getParameter("server"));
        model.put("componentsList", paramList);
        return view;
      }
      else if (action.equals("killComp")) {
        List<Map> listComp;
        listComp = mySiebelServerManager.killComp(enterpriseName, preparateParameter(request.getParameter("server")), preparateParameter(request.getParameter("compName")));
        writeListToJSON(response, listComp);
        return null;
      }
      else if (action.equals("startComp")) {
        List<Map> listComp = mySiebelServerManager.startComp(enterpriseName, preparateParameter(request.getParameter("server")), preparateParameter(request.getParameter("compName")));
        writeListToJSON(response, listComp);
        return null;
      }
      else if (action.equals("getCompState")) {
        String compState = mySiebelServerManager.getCompState(enterpriseName, preparateParameter(request.getParameter("server")), preparateParameter(request.getParameter("compName")));
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("state", compState);
        resultMap.put("component", request.getParameter("compName"));
        writeMapToJSON(response, resultMap);
        return null;
      }
      else if (action.equals("refreshCompState")) {
        List<Map> listComp;
        listComp = mySiebelServerManager.getServerComps(enterpriseName, preparateParameter(request.getParameter("server")));
        writeListToJSON(response, listComp);
        return null;
      }
      /*TODO: debug*/
      else if(action.equals("activeProcesses")){
        List<Map> activeProcesses = mySiebelServerManager.getProcesses();
        ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(PID_LIST));
        Map<String, Object> model = view.getModel();
        model.put("processes", activeProcesses);
        model.put("pluginName", Util.PLUGIN_NAME);
        return view;
      }
      /*TODO: debug*/
      else if(action.equals("processLog")){
        ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(PROCESS_LOG));
        Map<String, Object> model = view.getModel();
        model.put("processLog", mySiebelServerManager.getProcessLog(Integer.parseInt(request.getParameter("process"))));
        model.put("processId", request.getParameter("process"));
        model.put("pluginName", Util.PLUGIN_NAME);
        return view;
      }
    } catch (SrvrMgrException ex) {
      Loggers.SERVER.error("QWEQWEQWE " + ex);
      return ErrorView(ex);
    }
    return new ModelAndView();
  }

  private void writeListToJSON(HttpServletResponse response, List<Map> list) throws IOException{
    response.setHeader("content-type", "application/json");
    Map<String, String> resultMap = new HashMap<String, String>();
    for (Map<String, String> listItem : list)
      resultMap.put(listItem.get("CC_ALIAS"), listItem.get("CP_DISP_RUN_STATE"));
    response.getWriter().write(new Gson().toJson(resultMap));
  }

  private void writeMapToJSON(HttpServletResponse response, Map map) throws IOException{
    response.setHeader("content-type", "application/json");
    response.getWriter().write(new Gson().toJson(map));
  }

  private String preparateParameter (String param){
    return "\"" + param.replaceAll("[\n\r]", "").replaceAll("\"", "\\\"") + "\"";
  }

  private ModelAndView ErrorView(SrvrMgrException ex) {
    ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(ERROR_PAGE));
    final Map<String, Object> model = view.getModel();
    model.put("pluginName", Util.PLUGIN_NAME);
    model.put("controllerPath", CONTROLLER_PATH);
    model.put("errorMessage", ex.getMessage());
    return view;
  }
}
