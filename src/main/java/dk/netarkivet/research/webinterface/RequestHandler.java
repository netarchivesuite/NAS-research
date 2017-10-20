package dk.netarkivet.research.webinterface;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.RequestDispatcher;

import dk.netarkivet.common.exceptions.ArgumentNotValid;
import dk.netarkivet.common.exceptions.ForwardedToErrorPage;
import dk.netarkivet.common.utils.I18n;

public class RequestHandler {
	/**
     * Create the domain definition list for the jsp page. Code has been moved from the jsp to here to avoid compile errors at
     * runtime in correlation with the upgrade to java 1.8 and introduction of embedded tomcat to handle jsp pages. This was previously done via jetty 6.
     *
     * @param pageContext the current JSP context
     * @param request the JSP request
     * @param response the JSP response
     * @param I18N internationalization object.
     *
     * @return void
     * @throws ForwardedToErrorPage if an unknown bitarchive or update type is posted, or one of the two required
     * parameters are missing.
     * @throws ArgumentNotValid If the context is null.
     */
    public static String handleWpid(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, I18n I18N)
            throws ArgumentNotValid, ForwardedToErrorPage, ServletException, IOException {
        ArgumentNotValid.checkNotNull(pageContext, "PageContext context");
        ArgumentNotValid.checkNotNull(request, "Page request");
        ArgumentNotValid.checkNotNull(response, "Page response");
        String domains = request.getParameter("WPID");
        return domains;
        
 /*       
        String domains = request.getParameter();
        if (domains != null) {
            String[] domainsList = domains.split("\\s+");
            Set<String> invalidDomainNames = new HashSet<String>(
                    DomainDefinition.createDomains(domainsList));

            if (domainsList.length == 1
                    && DomainDAO.getInstance().exists(domainsList[0])) {
                RequestDispatcher rd =
                        pageContext.getServletContext().
                                getRequestDispatcher(
                                        "/Definitions-edit-domain.jsp?"
                                                + Constants.DOMAIN_PARAM
                                                + "=" + HTMLUtils.encode(
                                                domainsList[0]));
                rd.forward(request, response);

                return;
            } else {
                StringBuilder message = new StringBuilder();
                Set<String> validDomains = new HashSet<String>(Arrays.asList(domainsList));
                validDomains.removeAll(invalidDomainNames);
                if (!validDomains.isEmpty()) {
                    message.append("<h4>");
                    message.append(I18N.getString(response.getLocale(),
                            "harvestdefinition.domains.created"));
                    message.append("</h4><br/>");
                    
                    
                    for (String domain : validDomains) {
                        if (DomainDAO.getInstance().exists(domain)) {
                            message.append(DomainDefinition.makeDomainLink(domain));
                            message.append("<br/>");
                        }
                    }
                }
                if (invalidDomainNames.size() > 0) {
                    message.append("<br/>");
                    message.append(I18N.getString(response.getLocale(),
                            "harvestdefinition.domains.notcreated"));
                    message.append("<br/>");
                    DomainDAO dao = DomainDAO.getInstance();
                    for (String invalid : invalidDomainNames) {
                        if (dao.exists(invalid)) {
                            message.append(
                                    DomainDefinition.makeDomainLink(invalid));
                        } else {
                            message.append(invalid);
                        }
                        message.append("<br/>");
                    }
                }
                request.setAttribute("message", message.toString());
                RequestDispatcher rd = pageContext.getServletContext().
                        getRequestDispatcher("/message.jsp");
                rd.forward(request, response);
                return;
            }
        }
        */
    }

}

