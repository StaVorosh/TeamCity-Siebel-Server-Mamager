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
    </bs:linkCSS>
    </jsp:attribute>
  <jsp:attribute name="body_include">
    <h1>${pluginName}</h1>
    <div class="grayNote">${errorMessage}</div>
  </jsp:attribute>
</bs:page>
