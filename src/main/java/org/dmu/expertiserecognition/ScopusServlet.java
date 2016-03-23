/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ruben
 */
public class ScopusServlet extends HttpServlet
{
    public ScopusServlet(){}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<!DOCTYPE html>\n<html>\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/expertisecss.css\">");
        response.getWriter().println("<style>");
        response.getWriter().println("table, th, td {");
        response.getWriter().println("    border: 1px solid black;");
        response.getWriter().println("    border-collapse: collapse;");
        response.getWriter().println("}");
        response.getWriter().println("th, td {");
        response.getWriter().println("    padding: 5px;");
        response.getWriter().println("}");
        response.getWriter().println("</style>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<script src=\"//code.jquery.com/jquery-2.1.3.min.js\"></script>");
        response.getWriter().println("<script src=\"/static/spin.min.js\"></script>");
        response.getWriter().println("<script src=\"/static/myspinner.js\"></script>");
        response.getWriter().println("<div id=\"static\">\n" + "<div id=\"header\">\n" +
                "You are currently logged in as "+ request.getUserPrincipal().getName()+
                "<div id=\"wrapper\">" +
                "<ul>"
                + "<li><a href=\"/\">Home</a>-"
                + "<a href=\"/logout\">Logout</a>-"
                + "<a href=\"/changepasswd\">Change Password</a>"
                + "</li>"
                + "</ul>"+"</div>" +
"    </div></div> <br>\n");
        response.getWriter().println("<div id=\"loading\">\n" +
"    <div id=\"loadingcontent\">\n" +
"        <p id=\"loadingspinner\">\n" +
"            Loading ...\n" +
"        </p>\n" +
"    </div>\n" +
"</div><br>");
        //response.getWriter().println("<a href=\"http://www.dmu.ac.uk/research/research-faculties-and-institutes/technology/cci/centre-of-computational-intelligence.aspx\" target=\"_blank\"><img src=\"/static/dmulogo.png\" /></a>");
        response.getWriter().println("</br><h1>Expertise Recognition</h1>");
        response.getWriter().println("<h2>Scopus</h2>");
        //response.getWriter().println("<h3>You are currently logged in as "+ request.getUserPrincipal().getName() +"</h3>");
        //response.getWriter().println("<h3><a href=\"/\">Home</a> - <a href=\"/logout\">Logout</a> - <a href=\"/changepasswd\">Change Password</a></h3>");
        response.getWriter().println("<form action=\"/\" method=\"GET\" id=\"theForm\">");
        if (request.getParameter("search") == null)
        {
            response.getWriter().print("<br>Search: <input name=\"search\" size=50 type=\"text\" value=\"v2x congestion europe\">");
        }
        else
        {
            response.getWriter().print("<br>Search: <input name=\"search\" size=50 type=\"text\" value=\"" + 
            request.getParameter("search") + "\">");
        }
        response.getWriter().println(" - <a href=\"http://api.elsevier.com/documentation/search/SCOPUSSearchTips.htm\" target=\"_blank\">search tips</a>");
        response.getWriter().println(printYears(request.getParameter("years")));
        response.getWriter().println("<br>\n<br><input type=\"submit\" onclick=\"submitFct();\" value=\"Search\"></form>");
        //response.getWriter().println("<br>\n<br><input type=\"submit\" value=\"Search\"></form>");

        if ((request.getParameter("search") == null)||(request.getParameter("search").length() == 0)||
            (request.getParameter("years") == null)||(request.getParameter("years").length() == 0)) {
            response.getWriter().println("<br>\n<div class=\"hr\"><hr /></div>\n<h3>Cached Results</h3>");
            response.getWriter().println(ResultsDB.getInstance().listOfResultsHTML());
            response.getWriter().println("</body></html>");
            return;
        }
        
        SearchQuery sq = new SearchQuery(request.getParameter("search").trim(),
                Integer.parseInt(request.getParameter("years")));
        ResultsDB rdb = ResultsDB.getInstance();
        //System.out.println("Start readout from DB: " + System.currentTimeMillis());
        ScopusResults res = rdb.queryFromDB(sq);
        //System.out.println("End readout from DB: " + System.currentTimeMillis());
        if (res.responseCode == 0)
        {
            ScopusResults res2 = new ScopusResults();
            res2.responseCode = -888;
            res2.response = "The search is still in progress. <a href=\"/\">Please try again later ...</a>";
            ResultsDB.getInstance().addToDB(sq, res2);
            SearchThread.getInstance().put(sq);
            response.getWriter().println("\n<h3>Your request is being processed! The results will be available <a href=\"/\">here</a> later</h3>");
            response.getWriter().println("</body></html>");
            return;
        }

        try {
            if (res.responseCode != 200) {
                response.getWriter().println("<div class=\"hr\"><hr /></div>\n<h2>Search Results</h2>");
                if (request.getParameter("do") != null) {
                    if (request.getParameter("do").compareTo("restart") == 0) {
                        ScopusResults res3 = new ScopusResults();
                        res3.responseCode = -888;
                        res3.response = "The search is still in progress. <a href=\"/\">Please try again later ...</a>";
                        ResultsDB.getInstance().addToDB(sq, res3);
                        SearchThread.getInstance().put(sq);
                        response.getWriter().println("\n<br><h3>Search was restarted for this query</h3>\n");
                    }
                    else if (request.getParameter("do").compareTo("delete") == 0) {
                        ResultsDB.getInstance().deleteFromDB(sq);
                        response.getWriter().println("\n<br><h3>This item was deleted</h3>\n");
                        response.getWriter().println("\n<br><a href=\"/?search=" + sq.encoded_query() + "&amp;years=" + sq.years + "\">Restart search for this query</a>\n");
                    }
                }
                else {
                    response.getWriter().println(printEditDeleteForms(sq));
                }
                response.getWriter().println("<br>\n<br>\nResponse Code: " + res.responseCode);
                response.getWriter().println("<br>\nTime: " + res.timestampResults);
                response.getWriter().println("\n<br>Response:\n<br><pre>" + res.response + "</pre>\n");
                response.getWriter().println("\n<br>\n<br></body></html>");
            }
            else {
                response.getWriter().print(displayResults(res, request.getParameter("search"), 
                        request.getParameter("years"), request.getParameter("content")));
                response.getWriter().println("</body></html>");
            }
        } catch (Exception ex) {
            response.getWriter().println("<br>Exception:\n<br><pre>" + ex + "</pre>\n");
            response.getWriter().println("\n<br>\n<br></body></html>");
            Logger.getLogger(ScopusServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
    private String printEditDeleteForms(SearchQuery sq) {
        StringBuilder sb1 = new StringBuilder("<form id=\"restartform\" action=\"/\" method=\"POST\">\n");
        sb1.append("<a href=\"javascript:;\" onclick=\"document.getElementById('restartform').submit();\">Restart search for this query</a>\n");
        sb1.append("<input type=\"hidden\" name=\"search\" value=\"");
        sb1.append(sq.query);
        sb1.append("\" />\n");
        sb1.append("<input type=\"hidden\" name=\"years\" value=\"");
        sb1.append(sq.years);
        sb1.append("\" />\n");
        sb1.append("<input type=\"hidden\" name=\"do\" value=\"restart\" />\n</form>\n");
        sb1.append("<form id=\"deleteform\" action=\"/\" method=\"POST\">\n");
        sb1.append("<a href=\"javascript:;\" onclick=\"document.getElementById('deleteform').submit();\">Delete this item</a>\n");
        sb1.append("<input type=\"hidden\" name=\"search\" value=\"");
        sb1.append(sq.query);
        sb1.append("\" />\n");
        sb1.append("<input type=\"hidden\" name=\"years\" value=\"");
        sb1.append(sq.years);
        sb1.append("\" />\n");
        sb1.append("<input type=\"hidden\" name=\"do\" value=\"delete\" />\n</form>\n");
        return sb1.toString();
    }
    
    String printYears(String years)
    {
        String res;
        int yearsint = 3;
        if (years != null) {
            yearsint = Integer.parseInt(years);
        }
        switch (yearsint) {
            case 3:
                res = "<br>Number of Years: <select name=\"years\"><option selected value=\"3\">3</option><option value=\"5\">5</option><option value=\"7\">7</option><option value=\"10\">10</option></select>";
                break;
            case 5:
                res = "<br>Number of Years: <select name=\"years\"><option value=\"3\">3</option><option selected value=\"5\">5</option><option value=\"7\">7</option><option value=\"10\">10</option></select>";
                break;
            case 7:
                res = "<br>Number of Years: <select name=\"years\"><option value=\"3\">3</option><option value=\"5\">5</option><option selected value=\"7\">7</option><option value=\"10\">10</option></select>";
                break;
            case 10:
                res = "<br>Number of Years: <select name=\"years\"><option value=\"3\">3</option><option value=\"5\">5</option><option value=\"7\">7</option><option selected value=\"10\">10</option></select>";
                break;
            default:
                res = "<br>Number of Years: <select name=\"years\"><option selected value=\"3\">3</option><option value=\"5\">5</option><option value=\"7\">7</option><option value=\"10\">10</option></select>";
                break;
        }
        return res;
    }
    
    public static String toPrettyFormat(String jsonString) 
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }
    
    public static String toPrettyFormat(JsonObject jobj) 
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(jobj);

        return prettyJson;
    }
    
    public static boolean checkJsonElement(JsonElement jobj) {
        if (jobj == null) return false;
        return !jobj.isJsonNull();
    }
    
    // Display one table at a time on the browser. Default table is the authors one.
    private String displayResults(ScopusResults res, String search, String years, String content) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<script src=\"/static/jquery.tablesorter.min.js\"></script>\n");
        sb.append("<script src=\"/static/mytablesort.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization\"></script>\n");
        sb.append("<div class=\"hr\"><hr /></div>\n<h2>Search Results</h2>\n<p>Search term: \"");
        sb.append(res.searchTerm);
        sb.append("\"\n");
        sb.append("<br>within the years ");
        sb.append(res.yearsRange);
        sb.append("\n<br>Total Results returned is ");
        sb.append(res.totalResults);
        sb.append("\n<br>The results were obtained at ");
        sb.append(res.timestampResults);
        
//        sb.append("</p><p><a href=\"#authortable\">Authors Table</a><br>\n");
//        sb.append("<a href=\"#affiltable\">Affliations Table</a><br>\n");
//        sb.append("<a href=\"#loctable\">Locations Table</a><br>\n");
//        sb.append("<a href=\"#countrytable\">Countries Table</a><br>\n");
//        sb.append("<a href=\"#citationtable\">Citations Table</a><br>\n");
//        sb.append("<a href=\"#map\">Map of locations</a></p>\n");

        // Links to show tables, with the content to be shown
        sb.append("</p><p><a href=\"?search="); 
        sb.append(search);
        sb.append("&years=");
        sb.append(years);
        sb.append("&content=");
        sb.append("authortable\">Authors Table</a><br>\n");
        
        sb.append("</p><p><a href=\"?search="); 
        sb.append(search);
        sb.append("&years=");
        sb.append(years);
        sb.append("&content=");
        sb.append("affiltable\">Affiliations Table</a><br>\n");
        
        sb.append("</p><p><a href=\"?search="); 
        sb.append(search);
        sb.append("&years=");
        sb.append(years);
        sb.append("&content=");
        sb.append("loctable\">Locations Table</a><br>\n");
        
        sb.append("</p><p><a href=\"?search="); 
        sb.append(search);
        sb.append("&years=");
        sb.append(years);
        sb.append("&content=");
        sb.append("countrytable\">Countries Table</a><br>\n");
        
        sb.append("</p><p><a href=\"?search="); 
        sb.append(search);
        sb.append("&years=");
        sb.append(years);
        sb.append("&content=");
        sb.append("citationtable\">Citations Table</a><br>\n");
        
        sb.append("</p><p><a href=\"?search="); 
        sb.append(search);
        sb.append("&years=");
        sb.append(years);
        sb.append("&content=");
        sb.append("heatmap\">Map of locations</a><br>\n");
        
        sb.append("\n<br>\n");
        
        if (content != null) { // The user has clicked in one of the "content" links
            if (content.equals("authortable")) {
                sb.append("<br><div id=\"authortable\"></div><table id=\"authortbl\" class=\"tablesorter\"><thead><tr><th>Author</th><th>Total SJR</th><th>Mean SJR</th><th>Total ImpF</th><th>Mean ImpF</th><th>Total h5index</th><th>Mean h5index</th><th>Total EigenF*1000</th><th>Mean EigenF*1000</th><th>Papers h-index</th><th>HRat</th><th>HSJR</th><th>HIF</th><th>HCit</th><th>HCit2</th><th>g-index</th><th>e-index</th><th>m-index</th><th>Total pubs</th></tr></thead><tbody>\n");
                sb.append(addTable(res.authorList, 100));
                sb.append("</tbody></table>\n<br>\n<br>\n");
                sb.append("<p><a href=\"#\">Back to Top</a></p>\n");
            }

            if (content.equals("affiltable")) {
                sb.append("<br><div id=\"affiltable\"></div><table id=\"affltbl\" class=\"tablesorter\"><thead><tr><th>Affliation</th><th>Total SJR</th><th>Mean SJR</th><th>Total ImpF</th><th>Mean ImpF</th><th>Total h5index</th><th>Mean h5index</th><th>Total EigenF*1000</th><th>Mean EigenF*1000</th><th>Papers h-index</th><th>HRat</th><th>HSJR</th><th>HIF</th><th>HCit</th><th>HCit2</th><th>g-index</th><th>e-index</th><th>m-index</th><th>Total pubs</th></tr></thead><tbody>\n");
                sb.append(addTable(res.affilList, 100));
                sb.append("</tbody></table>\n<br>\n<br>\n");
                sb.append("<p><a href=\"#\">Back to Top</a></p>\n");            
            }

            if (content.equals("loctable")) {
                sb.append("<br><div id=\"loctable\"></div><table id=\"loctbl\" class=\"tablesorter\"><thead><tr><th>Location</th><th>Total SJR</th><th>Mean SJR</th><th>Total ImpF</th><th>Mean ImpF</th><th>Total h5index</th><th>Mean h5index</th><th>Total EigenF*1000</th><th>Mean EigenF*1000</th><th>Papers h-index</th><th>HRat</th><th>HSJR</th><th>HIF</th><th>HCit</th><th>HCit2</th><th>g-index</th><th>e-index</th><th>m-index</th><th>Total pubs</th></tr></thead><tbody>\n");
                sb.append(addTable(res.locationList, 100, Geocode.getInstance()));
                sb.append("</tbody></table>\n<br>\n<br>\n");
                sb.append("<p><a href=\"#\">Back to Top</a></p>\n");
            }

            if (content.equals("countrytable")) {
                sb.append("<br><div id=\"countrytable\"></div><table id=\"countrytbl\" class=\"tablesorter\"><thead><tr><th>Country</th><th>Total SJR</th><th>Mean SJR</th><th>Total ImpF</th><th>Mean ImpF</th><th>Total h5index</th><th>Mean h5index</th><th>Total EigenF*1000</th><th>Mean EigenF*1000</th><th>Papers h-index</th><th>HRat</th><th>HSJR</th><th>HIF</th><th>HCit</th><th>HCit2</th><th>g-index</th><th>e-index</th><th>m-index</th><th>Total pubs</th></tr></thead><tbody>\n");
                sb.append(addTable(res.countryList, 100));
                sb.append("</tbody></table>\n<br>\n<br>\n");
                sb.append("<p><a href=\"#\">Back to Top</a></p>\n");
            }

            if (content.equals("citationtable")) {
                sb.append("<br><div id=\"citationtable\"></div><table id=\"citationtbl\" class=\"tablesorter\"><thead><tr><th>Citation</th><th>Affliations</th><th>SJR</th><th>ImpF</th><th>h5index</th><th>EigenF*1000</th><th>Cited-by</th></tr></thead><tbody>\n");
                sb.append(addCitationTable(res.citationList, 100));
                sb.append("</tbody></table>\n<br>\n<br>\n");
            }

            if (content.equals("heatmap")) {
                sb.append("<div id=\"map\" style=\"width: 80%; height: 80%; position: absolute;\"><div id=\"map-canvas\" style=\"width: 80%; height: 80%; position: absolute;\"></div></div>\n<br>\n<br>\n");
                sb.append("<script>\n");
                sb.append("window.onload = function() {\n");
                sb.append("var map, pointarray, heatmap;\n\n");
                sb.append("var locData = [\n");
                sb.append(createHeatMap(res.locationList, 100));
                sb.append("];\n\n");
                sb.append("var mapOptions = {zoom: 13, center: new google.maps.LatLng(37.774546, -122.433523)};\n");
                sb.append("map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);\n");
                sb.append("var markerBounds = new google.maps.LatLngBounds();\n");
                sb.append(addMarkerBounds(res.locationList, 100));
                sb.append("map.fitBounds(markerBounds);\n");
                sb.append("var pointArray = new google.maps.MVCArray(locData);\n");
                sb.append("heatmap = new google.maps.visualization.HeatmapLayer({data: pointArray});\n");
                sb.append("heatmap.setMap(map);\n");
                sb.append("heatmap.set('radius', 20);\n");
                sb.append("};\n</script>\n");
                sb.append("<p><a href=\"#\">Back to Top</a></p>\n");
            }
        } else { // The user is coming from the search click button, so there's no "content" parameter
            sb.append("<br><div id=\"authortable\"></div><table id=\"authortbl\" class=\"tablesorter\"><thead><tr><th>Author</th><th>Total SJR</th><th>Mean SJR</th><th>Total ImpF</th><th>Mean ImpF</th><th>Total h5index</th><th>Mean h5index</th><th>Total EigenF*1000</th><th>Mean EigenF*1000</th><th>Papers h-index</th><th>HRat</th><th>HSJR</th><th>HIF</th><th>HCit</th><th>HCit2</th><th>g-index</th><th>e-index</th><th>m-index</th><th>Total pubs</th></tr></thead><tbody>\n");
            sb.append(addTable(res.authorList, 100));
            sb.append("</tbody></table>\n<br>\n<br>\n");
            sb.append("<p><a href=\"#\">Back to Top</a></p>\n");
        }
        
        
        return sb.toString();
    }

    private String addTable(ArrayList<RankEntry> list1, int range) {
        return addTable(list1, range, null);
    }
    
    private String addTable(ArrayList<RankEntry> list1, int range, Geocode gci) {
        Iterator<RankEntry> iter = list1.iterator();
        int i = 1;
        RankEntry re;
        GeoCoord gcd;
        StringBuilder sb = new StringBuilder();
        while((iter.hasNext()) && (i <= range)) {
            re = iter.next();
            sb.append("<tr><td>");
            if (gci == null) {
                sb.append(re.name);
            } else {
                gcd = gci.queryFromDB(re.name);
                if (gcd.latd.isEmpty()) {sb.append(re.name);}
                else {
                    sb.append("<a href=\"https://www.google.com/maps/place//@");
                    sb.append(gcd.latd);
                    sb.append(",");
                    sb.append(gcd.longd);
                    sb.append(",14z/data=!4m2!3m1!1s0x0:0x0\" target=\"_blank\">");
                    sb.append(re.name);
                    sb.append("</a>");
                }
            }
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.accSjr)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.meanSjr)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.accImpactFactor)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.meanImpactFactor)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append(re.accH5Index);
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.meanH5Index)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.accEigenFactor)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.meanEigenFactor)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append(re.papersHindex);
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.hrat)).setScale(6, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.hsjr)).setScale(6, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.hif)).setScale(6, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.hcit)).setScale(6, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.hcit2)).setScale(6, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append(re.papersGindex);
            sb.append("</td><td>");
            sb.append((new BigDecimal(re.papersEindex)).setScale(3, RoundingMode.HALF_UP).toString());
            sb.append("</td><td>");
            sb.append(re.papersMindex);
            sb.append("</td><td>");
            sb.append(re.numOfEntries);
            sb.append("</td></tr>\n");
        }
        return sb.toString();
    }
    
    private String addCitationTable(ArrayList<RankEntry> list1, int range) {
        Iterator<RankEntry> iter = list1.iterator();
        int i = 1;
        RankEntry re;
        StringBuilder sb = new StringBuilder();
        while((iter.hasNext()) && (i <= range)) {
            re = iter.next();
            sb.append("<tr><td>");
            sb.append(re.name);
            sb.append("</td><td>");
            sb.append(re.misc);
            sb.append("</td><td>");
            sb.append(re.accSjr);
            sb.append("</td><td>");
            sb.append(re.accImpactFactor);
            sb.append("</td><td>");
            sb.append(re.accH5Index);
            sb.append("</td><td>");
            sb.append(re.accEigenFactor);
            sb.append("</td><td>");
            sb.append(re.accCitedby);
            sb.append("</td></tr>\n");
        }
        return sb.toString();
    }

    private String createHeatMap(ArrayList<RankEntry> locationList, int range) {
        Iterator<RankEntry> iter = locationList.iterator();
        int i = 1;
        RankEntry re;
        Geocode gci = Geocode.getInstance();
        GeoCoord gcd;
        StringBuilder sb = new StringBuilder();
        boolean notfirst = false;
        double val;
        while((iter.hasNext()) && (i <= range)) {
            re = iter.next();
            gcd = gci.queryFromDB(re.name);
            val = re.accSjr + re.accImpactFactor + re.accH5Index + re.accCitedby;
            if ((gcd.latd.isEmpty() == false) && (val > 0)) {
                if (notfirst) {sb.append(",\n");}
                else {notfirst = true;}
                sb.append("  new google.maps.LatLng(");
                sb.append(gcd.latd);
                sb.append(", ");
                sb.append(gcd.longd);
                sb.append(")");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private String addMarkerBounds(ArrayList<RankEntry> locationList, int range) throws Exception {
        Iterator<RankEntry> iter = locationList.iterator();
        int i = 1;
        RankEntry re;
        Geocode gci = Geocode.getInstance();
        GeoCoord gcd;
        StringBuilder sb = new StringBuilder();
        double val;
        while((iter.hasNext()) && (i <= range)) {
            re = iter.next();
            gcd = gci.queryFromDB(re.name);
            val = re.accSjr + re.accImpactFactor + re.accH5Index + re.accCitedby;
            if ((gcd.latd.isEmpty() == false) && (val > 0)) {
                sb.append("markerBounds.extend(new google.maps.LatLng(");
                sb.append(gcd.latd);
                sb.append(", ");
                sb.append(gcd.longd);
                sb.append("));\n");
            }
        }
        return sb.toString();
    }
}
