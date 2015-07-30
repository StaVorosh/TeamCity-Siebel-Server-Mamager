<%@ include file="/include.jsp" %>
<%--
  ~ Copyright 2000-2015 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%--<link type="text/css" rel="stylesheet" href="/css/forms.css">--%>
<c:set var="pluginUrl">/${pluginName}.html?</c:set>

<%--<jsp:useBean id="name" type="java.lang.String" scope="request"/>--%>

<bs:page>
        <jsp:attribute name="head_include">
    <bs:linkCSS>
        /css/admin/adminMain.css
        ${teamcityPluginResourcesPath}css/ServerManager.css
    </bs:linkCSS>
                <bs:linkScript>${teamcityPluginResourcesPath}js/ServerManager.js</bs:linkScript>
    </jsp:attribute>
  <jsp:attribute name="body_include">
    <h1>${pluginName}</h1>
    <div class="grayNote">Components list for server</div>
    <l:tableWithHighlighting className="parametersTable" id="components">
        <tr>
            <th>Component Name</th>
            <th>Alias</th>
            <th>Comp State</th>
            <th>Start Time</th>
        </tr>
        <c:forEach var="comp" items="${componentsList}">
            <%--<c:set var="servListComps">${pluginUrl}&action=listComps&server=<c:out value='${server.HOST_NAME}'/></c:set>--%>
            <c:set var="compRestartUrl"><c:out value='${controllerPath}'/>?action=restart&compName=<c:out
                    value='${comp.CC_ALIAS}'/>&server=${serverName}</c:set>
            <c:set var="compKillUrl"><c:out value='${controllerPath}'/>?action=killComp&compName=<c:out
                    value='${comp.CC_ALIAS}'/>&server=${serverName}</c:set>
            <c:set var="compStateClass">
                <c:choose>
                    <c:when test="${comp.CP_DISP_RUN_STATE == 'Online' or comp.CP_DISP_RUN_STATE == 'Running'}">
                        siebel-comp-online
                    </c:when>
                    <c:when test="${comp.CP_DISP_RUN_STATE == 'Not Online' or comp.CP_DISP_RUN_STATE == 'Shutting down'}">
                        siebel-comp-not-online
                    </c:when>
                    <c:otherwise>
                        siebel-comp-shutdown
                    </c:otherwise>
                </c:choose>
            </c:set>
            <%--<c:set var="onclick">BS.openUrl(event, '${servListComps}'); return false;</c:set>--%>
            <tr <%--onclick="${onclick}"--%>data-comp-alias="${comp.CC_ALIAS}">
                <td class="name highlight" data-param-name="CC_NAME">${comp.CC_NAME}</td>
                <td class="name highlight" data-param-name="CC_ALIAS">${comp.CC_ALIAS}</td>
                <td class="name highlight" data-param-name="CP_DISP_RUN_STATE">
                    ${comp.CP_DISP_RUN_STATE}</td>
                <td class="name highlight" data-param-name>${comp.CP_START_TIME}</td>
                <td class="actions edit highlight">
                    <a href="${compRestartUrl}">Restart</a>
                </td>
                <td class="actions edit highlight">
                    <a href="#" onclick="ServerManager.AjaxRequest('${compKillUrl}'); return false">Kill</a>
                </td>
                    <%--<td class="name highlight">${server.SBLSRVR_STATE}</td>--%>
                    <%--<td class="name highlight">${server.HOST_NAME}</td>--%>
                    <%--<td class="name highlight">${server.START_TIME}</td>--%>
                    <%--<td class="highlight" onclick="${onclick}"><c:out value="${configuration.templateName}"/></td>--%>
                    <%--<td class="actions edit highlight">--%>
                    <%--<a href="${confEditUrl}">Edit</a>--%>
                    <%--</td>--%>
                    <%--<td class="actions edit highlight last">--%>
                    <%--<a href="${confDeleteUrl}"--%>
                    <%--onclick="return confirm('Are you sure you want to delete server configuration?')">Delete</a>--%>
                    <%--</td>--%>
            </tr>
        </c:forEach>
    </l:tableWithHighlighting>
      <script type="text/javascript">ServerManager.refreshCompState('${controllerPath}','${serverName}')</script>
  </jsp:attribute>
</bs:page>
