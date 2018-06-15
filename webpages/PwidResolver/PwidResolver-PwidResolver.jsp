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
                 dk.netarkivet.research.pwid.RequestHandler,
                 dk.netarkivet.research.pwid.PWID,
                 dk.netarkivet.research.webinterface.Constants,
                 dk.netarkivet.research.pwid.PwidParseException,
                 dk.netarkivet.research.pwid.Archive,
                 java.util.Map
"
    pageEncoding="UTF-8"
%><%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%><fmt:setLocale value="<%=HTMLUtils.getLocale(request)%>" scope="page"
/><fmt:setBundle scope="page" basename="<%=Constants.TRANSLATIONS_BUNDLE%>"/><%!
    private static final I18n I18N
            = new I18n(Constants.TRANSLATIONS_BUNDLE);
%><%
    HTMLUtils.setUTF8(request);
    String pwidString = RequestHandler.getPwid(request, response, pageContext, I18N);
    PWID pwid = null;
    Map<String,Archive> archives = Archive.readKnownArchives();
    boolean foundValid = false;
    String reason = null;
    String message = "";
    if (pwidString != null && !pwidString.isEmpty()) {
    	try {
    		pwid = PWID.parsePWID(pwidString);
    		foundValid = true;
    	} catch (PwidParseException e) {
    		reason = e.getMessage();
    	}
    	if (foundValid) {
    		String archiveId = pwid.getArchiveId();
    		Archive a = archives.get(archiveId);
    		if (a == null) {
    			message = "The string '" + pwidString + "' is a valid Pwid, but the archiveid '" + archiveId + "' is not registered";
    		} else {
    			String url = Archive.getUrlFromPwid(a, pwid);
    			message = "The string '" + pwidString + "' is a valid Pwid, and represents the archivaluri: <A HREF=\"" 
    				+ url + "\">" + url + "</A>";
    		}
    	} else {
    		message = "The string '" + pwidString + "' is not a valid Pwid: " +  reason;
    	}
    }
    HTMLUtils.generateHeader(pageContext);
    %>
    	

    <!-- //We only reach this point if no wpid was in the request -->

<h3 class="page_heading"><fmt:message key="pagetitle;pwidresolver"/></h3>

<h4><%=message%></h4>

<form method="post" action="PwidResolver-PwidResolver.jsp">
    <!-- <fmt:message key="harvestdefinition.domains.enter"/>  -->
    Enter Pwid to resolve:
    <br />
    <span id="focusElement">
        <textarea cols="200" rows="2" name="PWID"></textarea>
    </span><br />
    <input type="submit" value="check"/>
</form>

<% 
HTMLUtils.generateFooter(out);
%>
