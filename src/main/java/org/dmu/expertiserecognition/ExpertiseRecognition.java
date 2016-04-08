/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.*;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 *
 * @author ruben
 */
public class ExpertiseRecognition {

    /**
     * @param args the command line arguments
     */
    
    private static DefaultServlet loginServlet() {
        return new DefaultServlet() {
          @Override
          protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0); // Proxies.
            response.getWriter().append("<!DOCTYPE html>\n<HTML><form method='POST' action='/j_security_check'>"
                    +"<head>\n <link rel=\"stylesheet\" type=\"text/css\" href=\"static/expertisecss.css\"> \n </head>\n </br>"
              +"<div class=\"login-box\">" + "<div class=\"lb-header\">"
              + "<h1>Login to Expertise Recognition</h1>\n </br> </br>"
              + "<form class=\"email-login\">"+ "<div class=\"u-form-group\">"
              +"Username: <input type='text' name='j_username'/><BR>\n<BR>\n"+"</div>"
              +"<div class=\"u-form-group\">"
              + "Password: <input type='password' name='j_password'/><BR>\n<BR>\n" + "</div>"
              +"\n </br>"
              + "<div class=\"buttonHolder\">"
              +"<div class=\"u-form-group\">" 
              + "<input type='submit' value='Login'/>" +"</div>" + "</div>"      
                    + "</form></HTML>");
            }
        };
    }
    
    private static DefaultServlet logoutServlet() {
        return new DefaultServlet() {
          @Override
          protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.logout();
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0); // Proxies.
            response.setHeader("Location", "/");
            response.getWriter().append("<!DOCTYPE html>\n<HTML><h1>Logged out successfully</h1></HTML>");
            }
        };
    }
    
    private static ConstraintSecurityHandler prepareAuthenticator() {
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__FORM_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.addConstraintMapping(constraintMapping);
        securityHandler.setLoginService(UsersDB.getInstance().getLoginService());

        FormAuthenticator authenticator = new FormAuthenticator("/login", "/login", false);
        securityHandler.setAuthenticator(authenticator);
        return securityHandler;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        Server server = new Server();

        File keystoreFile = new File("web/dynamicdb/keystore.jks");
        if (!keystoreFile.exists())
        {
            try {
                throw new FileNotFoundException(keystoreFile.getAbsolutePath());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExpertiseRecognition.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(61420);
        http_config.setOutputBufferSize(32768);
        
        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(61425);
        http.setIdleTimeout(30000);
        
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword("OBF:1uuu1ytk1w1y1w1c1wg81xtx1u9t1xtv1sox1vnw1vn61sot1xtn1u9p1xtl1wfq1w261w1k1yt21uvc");
        sslContextFactory.setKeyManagerPassword("OBF:1uuu1ytk1w1y1w1c1wg81xtx1u9t1xtv1sox1vnw1vn61sot1xtn1u9p1xtl1wfq1w261w1k1yt21uvc");
        sslContextFactory.setIncludeCipherSuites(new String[] {
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
            "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
            "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
            "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA"
        });
        
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());
        ServerConnector https = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        https.setPort(61420);
        https.setIdleTimeout(500000);
        
        server.setConnectors(new Connector[] { /*http,*/ https });
        
        JournalRankDB.getInstance();
        Geocode.getInstance();
        ResultsDB.getInstance();
        SearchThread.getInstance().start();
 
        ResourceHandler resHandler = new ResourceHandler();
        resHandler.setResourceBase("web/static/");
        ContextHandler ctxStatic = new ContextHandler("/static");
        ctxStatic.setHandler(resHandler);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new ScopusServlet()),"/*");
        context.addServlet(new ServletHolder(loginServlet()), "/login");
        context.addServlet(new ServletHolder(logoutServlet()), "/logout");
        context.addServlet(new ServletHolder(new ChangePasswdServlet()), "/changepasswd");
        context.addServlet(new ServletHolder(new UsersMgmtServlet()), "/usersmgmt");
        context.addServlet(new ServletHolder(new WOKServlet()), "/wok");
        context.setSecurityHandler(prepareAuthenticator());
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, ctxStatic });
 
        server.setHandler(contexts);
 
        try {
            server.start();
            server.join();
            JournalRankDB.getInstance().closeDB();
            //Geocode.getInstance().closeDB();
        } catch (Exception ex) {
            Logger.getLogger(ExpertiseRecognition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
