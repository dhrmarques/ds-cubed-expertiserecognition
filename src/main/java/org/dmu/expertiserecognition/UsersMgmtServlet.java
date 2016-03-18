/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ruben
 */
public class UsersMgmtServlet extends HttpServlet {
    
    public UsersMgmtServlet() {}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (request.getUserPrincipal().getName().compareTo("admin") != 0) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0); // Proxies.
            response.getWriter().append("<!DOCTYPE html>\n<HTML><h1>Access Denied</h1></HTML>");
            return;
        }

        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<!DOCTYPE html>\n<html>\n<head>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        //response.getWriter().println("<a href=\"http://www.dmu.ac.uk/research/research-faculties-and-institutes/technology/cci/centre-of-computational-intelligence.aspx\" target=\"_blank\"><img src=\"/static/dmulogo.png\" /></a>");
        response.getWriter().println("<h1>Expertise Recognition</h1>");
        response.getWriter().println("<h2>Users Management</h2>");
        response.getWriter().println("<h3>You are currently logged in as "+ request.getUserPrincipal().getName() +"</h3>");
        response.getWriter().println("<h3><a href=\"/\">Home</a> - <a href=\"/logout\">Logout</a></h3>");
        if (request.getParameter("newuser") != null) {
            String newuser = request.getParameter("newuser");
            String newpass1 = request.getParameter("newpass1");
            String newpass2 = request.getParameter("newpass2");
            if (newpass1.compareTo(newpass2) != 0) {
                response.getWriter().println("<h3>New user passwords don't match!</h3>");
            } else {
                UsersDB.getInstance().addUser(newuser, newpass2);
                response.getWriter().println("<h3>New user added successfully!</h3>");
            }
        }
        if (request.getParameter("removeuser") != null) {
            String user1 = request.getParameter("removeuser");
            UsersDB.getInstance().removeUser(user1);
            response.getWriter().println("<h3>User " + user1 + " removed!</h3>");
        }
        response.getWriter().println("<form action=\"/usersmgmt\" method=\"POST\" id=\"addNewUserForm\">");
        response.getWriter().println("Username: <input type='text' name='newuser'/><BR>\n<BR>\n"
              + "New Password: <input type='password' name='newpass1'/><BR>\n<BR>\n"
              + "New Password (again): <input type='password' name='newpass2'/><BR>\n<BR>\n"
              + "<input type='submit' value='Add new user'/></form><BR>\n<BR>\n");
        ArrayList<String> arr = UsersDB.getInstance().getUsersList();
        response.getWriter().println("<h3>Current Users:</h3>");
        Iterator<String> iter = arr.iterator();
        int i = 0;
        String user1;
        while (iter.hasNext()) {
            i++;
            user1 = iter.next();
            if (user1.compareTo("admin") == 0) {continue;}
            response.getWriter().println("<form id=\"removeuser" + i + "\" action=\"/usersmgmt\" method=\"POST\">"+
                    "<input type=\"hidden\" name=\"removeuser\" value=\""+ user1 + "\" /></form>" );
            response.getWriter().println(user1 + " - <a href=\"javascript:;\" onclick=\"document.getElementById('removeuser"+ i +"').submit();\">Remove</a><BR>");
        }
        response.getWriter().println("</body></html>");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
}
