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
public class IndexServlet extends HttpServlet
{
    private String greeting="Hello World";
    public IndexServlet(){}
    public IndexServlet(String greeting)
    {
        this.greeting=greeting;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Expertise Recognition</h1>");
        response.getWriter().println("<br><a href=\"/wok\">ISI Web of Knowledge</a>");
        response.getWriter().println("<br><a href=\"/scopus\">Scopus</a>");
    }
}
