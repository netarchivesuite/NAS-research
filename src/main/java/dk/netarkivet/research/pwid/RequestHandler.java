package dk.netarkivet.research.pwid;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import dk.netarkivet.common.exceptions.ArgumentNotValid;
import dk.netarkivet.common.exceptions.ForwardedToErrorPage;
import dk.netarkivet.common.utils.I18n;

public class RequestHandler {
	
	public static String getPwid(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, I18n I18N)
            throws ArgumentNotValid, ForwardedToErrorPage, ServletException, IOException {
        ArgumentNotValid.checkNotNull(pageContext, "PageContext context");
        ArgumentNotValid.checkNotNull(request, "Page request");
        ArgumentNotValid.checkNotNull(response, "Page response");
        return request.getParameter("PWID");
     }
}

