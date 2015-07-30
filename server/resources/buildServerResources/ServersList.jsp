<%@ include file="/include.jsp" %>
<%--<link type="text/css" rel="stylesheet" href="/css/forms.css">--%>
<c:set var="pluginUrl">/${pluginName}.html?</c:set>

<%--<jsp:useBean id="name" type="java.lang.String" scope="request"/>--%>

<bs:page>
    <jsp:attribute name="head_include">
    <bs:linkCSS>
        /css/admin/adminMain.css
    </bs:linkCSS>
    </jsp:attribute>
  <jsp:attribute name="body_include">
    <h1>${pluginName}</h1>
    <div class="grayNote">Server list</div>
    <l:tableWithHighlighting className="parametersTable" id="servers">
        <tr>
            <th>Server Name</th>
            <th>State</th>
            <th>Host Name</th>
            <th>Start Time</th>
        </tr>
        <c:forEach var="server" items="${serverList}">
            <c:set var="servListComps">${pluginUrl}action=listComps&server=<c:out value='${server.HOST_NAME}'/></c:set>
            <%--<c:set var="confDeleteUrl"><c:out value='${controllerPath}'/>?action=delete&name=<c:out--%>
                    <%--value='${configuration.name}'/></c:set>--%>
            <c:set var="onclick">BS.openUrl(event, '${servListComps}'); return false;</c:set>
            <tr onclick="${onclick}">
                <td class="name highlight">${server.SBLSRVR_NAME}</td>
                <td class="name highlight">${server.SBLSRVR_STATE}</td>
                <td class="name highlight">${server.HOST_NAME}</td>
                <td class="name highlight">${server.START_TIME}</td>
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
  </jsp:attribute>
</bs:page>
