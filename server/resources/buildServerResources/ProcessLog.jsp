<%@ include file="/include.jsp" %>

<c:set var="pluginUrl">/${pluginName}.html?</c:set>

<bs:page>
    <jsp:attribute name="head_include">
    <bs:linkCSS>
        /css/admin/adminMain.css
    </bs:linkCSS>
    </jsp:attribute>
  <jsp:attribute name="body_include">
    <h1>${pluginName}</h1>
    <div class="grayNote">Log for process with PID ${processId}</div>
    <div class="log">
        <c:forEach var="logItem" items="${processLog}">
            <div>

            </div>
        </c:forEach>
    </div>
  </jsp:attribute>
</bs:page>
