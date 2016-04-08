/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ruben
 */
public class ChangePasswdServlet extends HttpServlet {
    
    public ChangePasswdServlet() {}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<!DOCTYPE html>\n<html>\n"+"<head>\n <link rel=\"stylesheet\" type=\"text/css\" href=\"static/expertisecss.css\"> \n </head>");
        //response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        //response.getWriter().println("<a href=\"http://www.dmu.ac.uk/research/research-faculties-and-institutes/technology/cci/centre-of-computational-intelligence.aspx\" target=\"_blank\"><img src=\"/static/dmulogo.png\" /></a>");
        response.getWriter().println("<div id=\"static\">\n" + "<div id=\"header\">\n"+
                "<ul>"
                + "<li><a href=\"/Home\">Home</a></li>"
                + "<li><a href=\"/logout\">Logout</a></li>"
                + "</ul>" + "</div>"+ 
"    </div> </br>\n");
        //response.getWriter().println("<h1>Expertise Recognition</h1>")
        response.getWriter().println("<br><h4> You are currently logged in as "+ request.getUserPrincipal().getName() + "</h4>");
        response.getWriter().println("<div class=\"login-box\">" + "<div class=\"lb-header\">");
        response.getWriter().println("<h1>Change Password</h1>\n </br> </br>");
        //response.getWriter().println("<h3><a href=\"/\">Home</a> - <a href=\"/logout\">Logout</a></h3>");
        if (request.getParameter("oldpass") != null) {
            String oldpass = request.getParameter("oldpass");
            String newpass1 = request.getParameter("newpass1");
            String newpass2 = request.getParameter("newpass2");
            if (newpass1.compareTo(newpass2) != 0) {
                response.getWriter().println("<h3>New passwords don't match!</h3>");
            } else if (UsersDB.getInstance().checkPassword(request.getUserPrincipal().getName(), oldpass) == false) {
                response.getWriter().println("<h3>Old password incorrect!</h3>");
            } else {
                UsersDB.getInstance().changePasswd(request.getUserPrincipal().getName(), newpass1);
                response.getWriter().println("<h3>Password successfully changed!</h3>");
            }
        }
        response.getWriter().println("<form class=\"email-login\" action=\"/changepasswd\" method=\"POST\" id=\"chgPasswdForm\">");
        response.getWriter().println("<div class=\"u-form-group\">"
              +"Old password: <input type='password' name='oldpass'/><BR>\n<BR>\n </div>"
              +"<div class=\"u-form-group\">"
              + "New Password: <input type='password' name='newpass1'/><BR>\n<BR>\n </div>"
              +"<div class=\"u-form-group\">"
              + "Re-type Password: <input type='password' name='newpass2'/><BR>\n<BR>\n </div>"
              +"<div class=\"u-form-group\">" 
              + "<input type='submit' value='Change Password'/></div></form><BR>\n<BR>\n");
        response.getWriter().println("</body></html>");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
}
