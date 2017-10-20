<%--
File:       $Id$
Revision:   $Revision$
Author:     $Author$
Date:       $Date$

The Netarchive Suite - Software to harvest and preserve websites
Copyright 2004-2017 The Royal Danish Library,
the National Library of France and the Austrian
National Library.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%><%--
This is a summary page displaying all known schedules. It takes no
parameters.
--%><%@ page import="dk.netarkivet.common.utils.I18n,
                 dk.netarkivet.common.webinterface.HTMLUtils,
                 dk.netarkivet.harvester.Constants,
                 dk.netarkivet.research.webinterface.RequestHandler
"
    pageEncoding="UTF-8"
%><%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%><fmt:setLocale value="<%=HTMLUtils.getLocale(request)%>" scope="page"
/><fmt:setBundle scope="page" basename="<%=Constants.TRANSLATIONS_BUNDLE%>"/><%!
    private static final I18n I18N
            = new I18n(Constants.TRANSLATIONS_BUNDLE);
%><%
    HTMLUtils.setUTF8(request);
    String wpid = RequestHandler.handleWpid(request, response, pageContext, I18N);
    HTMLUtils.generateHeader(pageContext);
    %>
    FOUND wpid: <%=wpid%>	

    <!-- //We only reach this point if no wpid was in the request -->

<h3 class="page_heading"><fmt:message key="pagetitle;wpidresolver"/></h3>

<form method="post" action="WpidResolver-WpidResolver.jsp">
    <!-- <fmt:message key="harvestdefinition.domains.enter"/>  -->
    Enter Wpid to resolve
    <br />
    <span id="focusElement">
        <textarea cols="200" rows="2" name="WPID"></textarea>
    </span><br />
    <input type="submit" value="check"/>
</form>

<% 
HTMLUtils.generateFooter(out);
%>
