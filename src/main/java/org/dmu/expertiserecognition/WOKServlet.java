/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;


import com.thomsonreuters.wokmws.cxf.woksearch.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author ruben
 */
public class WOKServlet extends HttpServlet
{
    private String greeting="Hello World";
    public WOKServlet(){}
    public WOKServlet(String greeting)
    {
        this.greeting=greeting;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1><a href=\"/\">Expertise Recognition</a></h1>");
        response.getWriter().println("<h2>ISI Web of Knowledge</h2>");
        response.getWriter().println("<br><form action=\"/wok\" method=\"GET\">");
        if (request.getParameter("search") == null)
        {
            response.getWriter().println("<br>Search: <input name=\"search\" type=\"text\">");
        }
        else
        {
            response.getWriter().println("<br>Search: <input name=\"search\" type=\"text\" value=\"" + 
                    request.getParameter("search") + "\">");
        }
        if (request.getParameter("sid") == null)
        {
            response.getWriter().println("<br>WOK Session ID: <input name=\"sid\" type=\"text\">");
        }
        else
        {
            response.getWriter().println("<br>WOK Session ID: <input name=\"sid\" type=\"text\" value=\"" + 
                    request.getParameter("sid") + "\">");
        }
        response.getWriter().println("<br><input type=\"submit\"></form>");
        
        if ((request.getParameter("sid") == null) || (request.getParameter("search") == null))
        {
            return;
        }
        
        if (("".equals(request.getParameter("sid"))) && (request.getParameter("search").length() > 0))
        {
            response.getWriter().println("<br><font color=\"red\" size=\"+1\"><b>Session ID is required!</b></font>");
        }
        if ((request.getParameter("sid").length() > 0) && (request.getParameter("search").length() > 0)) 
        {
            run_search(request.getParameter("search"), request.getParameter("sid"), request, response);
        }
    }
    
    private void run_search(String searchstr, String sid, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        WokSearchService wokssrvc  = new WokSearchService();
        WokSearch port = wokssrvc.getWokSearchPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        Map hdrs;
        hdrs = (Map) ((BindingProvider)port).getRequestContext().get(MessageContext.HTTP_REQUEST_HEADERS);
        if (hdrs != null) {hdrs.remove("Cookie");}
        else {hdrs = new HashMap();}
        String cookiestr = "SID=\"" + sid + "\"";
        hdrs.put("Cookie", Collections.singletonList(cookiestr));
        ((BindingProvider)port).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, hdrs);
        QueryParameters qparam = new QueryParameters();
        qparam.setDatabaseID("WOS");
        qparam.setQueryLanguage("en");
        qparam.setUserQuery(searchstr);
        FullRecordSearchResults res = null;
        try {
            res = port.search(qparam, new RetrieveParameters());
        } catch (AuthenticationException_Exception | ESTIWSException_Exception | InternalServerException_Exception | InvalidInputException_Exception | QueryException_Exception | SessionException_Exception ex) {
            Logger.getLogger(WOKServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.getWriter().println("<br>" + ex.getMessage());
        }
        if (res != null) {
            try {
                response.getWriter().println("<br>" + res.getRecords());
            } catch (IOException ex) {
                Logger.getLogger(WOKServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
