package TeamCitySrvrmgr.server;

import com.google.gson.Gson;
import jetbrains.buildServer.controllers.BaseController;
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
        try {
          serverList = mySiebelServerManager.getServers(enterpriseName);
        } catch (SrvrMgrException ex) {
          return ErrorView(ex);
        }
        ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(SERVERS_LIST));
        final Map<String, Object> model = view.getModel();
        model.put("pluginName", Util.PLUGIN_NAME);
        model.put("serverList", serverList);
        return view;
      } else if (action.equals("listComps")) {
        List<Map> paramList;
        try {
          paramList = mySiebelServerManager.getServerComps(enterpriseName, request.getParameter("server"));
        } catch (SrvrMgrException ex) {
          return ErrorView(ex);
        }
        ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(SERVER_COMPONENTS_LIST));
        final Map<String, Object> model = view.getModel();
        model.put("pluginName", Util.PLUGIN_NAME);
        model.put("controllerPath", CONTROLLER_PATH);
        model.put("serverName", request.getParameter("server"));
        model.put("componentsList", paramList);
        return view;
      } else if (action.equals("killComp")) {
        List<Map> listComp;
        try {
          listComp = mySiebelServerManager.killComp(enterpriseName, request.getParameter("server"), request.getParameter("compName"));
        } catch (SrvrMgrException ex) {
          ErrorView(ex);
        }
        response.setHeader("content-type", "application/json");
        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        List<Map> list = new ArrayList<Map>();
        list.add(map);
        response.getWriter().write(new Gson().toJson(map));
        return null;
      } else if (action.equals("startComp")) {
        List<Map> listComp;
        try {
          listComp = mySiebelServerManager.startComp(enterpriseName, request.getParameter("server"), request.getParameter("compName"));
        } catch (SrvrMgrException ex) {
          ErrorView(ex);
        }
        response.setHeader("content-type", "application/json");
        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        List<Map> list = new ArrayList<Map>();
        list.add(map);
        response.getWriter().write(new Gson().toJson(map));
        return null;
      } else if (action.equals("getCompState")) {
        String compState = mySiebelServerManager.getCompState(enterpriseName, request.getParameter("server"), request.getParameter("compName"));
        response.setHeader("content-type", "application/json");
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("state", compState);
        resultMap.put("component", request.getParameter("compName"));
        response.getWriter().write(new Gson().toJson(resultMap));
        return null;
      } else if (action.equals("refreshCompState")) {
        List<Map> listComp;
        try {
          listComp = mySiebelServerManager.getServerComps(enterpriseName, request.getParameter("server"));
        } catch (SrvrMgrException ex) {
          return ErrorView(ex);
        }
        response.setHeader("content-type", "application/json");
        Map<String, String> resultMap = new HashMap<String, String>();
        for (Map<String, String> comp : listComp)
          resultMap.put(comp.get("CC_ALIAS"), comp.get("CP_DISP_RUN_STATE"));
        response.getWriter().write(new Gson().toJson(resultMap));
        return null;
      } else if (action.equals("listPID")) {
        ModelAndView view = new ModelAndView(myPluginDescriptor.getPluginResourcesPath(PID_LIST));
        final Map<String, Object> model = view.getModel();
        model.put("pluginName", Util.PLUGIN_NAME);
        model.put("controllerPath", CONTROLLER_PATH);
        model.put("serverName", request.getParameter("server"));
        model.put("PIDList", mySiebelServerManager.getPIDs());
        return view;
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return new ModelAndView();
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
