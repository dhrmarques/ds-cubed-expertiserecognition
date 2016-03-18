/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import com.google.gson.*;
import static org.dmu.expertiserecognition.ScopusServlet.checkJsonElement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 *
 * @author ruben
 */

class ResultsInfo {
    int totalResults;
    int startIndex;
    int itemsPerPage;
}

class Affil {
    String affilName;
    String affilCity;
    String affilCountry;
    
    Affil() {
        affilName = "";
        affilCity = "";
        affilCountry = "";
    }
}

class Entry {
    String title;
    String publicationName;
    String issn;
    String doi;
    String citation;
    String year;
    ArrayList<String> authors;
    ArrayList<Affil> affils;
    double sjr;
    double impactFactor;
    double h5index;
    double eigenFactor;
    int citedby;
    
    Entry() {
        affils = new ArrayList<>();
        authors = new ArrayList<>();
        title = "";
        publicationName = "";
        issn = "";
        doi = "";
        citation = "";
    }
}

public class ScopusSearcher {
    
    private static final String scopusApiKey = "43e2792c40dbfd6f3b423840c8f6d82c";
    private static final int resultsLimit = 5000;
    private final DoiFullAuthorList dfal;
    int respCode;
    String respContent;
    int startYear;
    int endYear;
    ResultsInfo resultsInfo;
    JsonArray entries;
    
    public ScopusSearcher() {
        respCode = 0;
        respContent = "";
        endYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        startYear = endYear - 2;
        resultsInfo = new ResultsInfo();
        dfal = new DoiFullAuthorList();
    }
    
    public ScopusResults search(SearchQuery sq) throws Exception {
        String searchstr = sq.query;
        ScopusResults res = new ScopusResults();
        externalGet(sq);
        res.responseCode = respCode;
        res.response = respContent;
        res.searchTerm = searchstr;
        res.yearsRange = (endYear + 1 - sq.years) + " - " + endYear;
        if (respCode != 200) return res;
        extractResults();
        res.totalResults = resultsInfo.totalResults;
        if (resultsInfo.totalResults > resultsLimit) {
            res.responseCode = -999;
            res.response = "Please narrow search. Too many results returned: " + resultsInfo.totalResults;
            return res;
        }
        System.out.println("Search term = " + searchstr);
        printResultsInfo(resultsInfo);
        res.analyzeEntries(extractEntries(entries));
        while (resultsInfo.startIndex + resultsInfo.itemsPerPage < resultsInfo.totalResults) {
            externalGet(sq, resultsInfo.startIndex + resultsInfo.itemsPerPage);
            res.responseCode = respCode;
            res.response = respContent;
            if (respCode != 200) return res;
            extractResults();
            printResultsInfo(resultsInfo);
            res.analyzeEntries(extractEntries(entries));
        }
        res.convertAllMapsToSortedLists();
        System.out.println("Search completed");
        res.updateTimestamp();
        return res;
    }
    
    private static void printResultsInfo(ResultsInfo resultsInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("startIndex = ");
        sb.append(resultsInfo.startIndex);
        sb.append(", itemsPerPage = ");
        sb.append(resultsInfo.itemsPerPage);
        sb.append(", totalResults = ");
        sb.append(resultsInfo.totalResults);
        System.out.println(sb);
    }

    private void extractResults() {
        JsonParser parser = new JsonParser();
        JsonObject jsonMain = parser.parse(respContent).getAsJsonObject();
        JsonObject resultHeader = jsonMain.get("search-results").getAsJsonObject();
        entries = resultHeader.get("entry").getAsJsonArray();
        resultsInfo.totalResults = resultHeader.get("opensearch:totalResults").getAsInt();
        resultsInfo.itemsPerPage = resultHeader.get("opensearch:itemsPerPage").getAsInt();
        resultsInfo.startIndex = resultHeader.get("opensearch:startIndex").getAsInt();
    }
    
    private ArrayList<Entry> extractEntries(JsonArray jsonArr) throws Exception {
        Iterator<JsonElement> iter = jsonArr.iterator();
        JsonObject jsonEntry;
        ArrayList<Entry> entries1 = new ArrayList<>();
        while (iter.hasNext()) {
            jsonEntry = iter.next().getAsJsonObject();
            entries1.add(extractEntry(jsonEntry));
        }
        return entries1;
    }
    
    private Entry extractEntry(JsonObject jsonEntry) throws Exception {
        Entry entry = new Entry();
        if (checkJsonElement(jsonEntry.get("dc:title")))
            entry.title = jsonEntry.get("dc:title").getAsString();
        String creator = "";
        if (checkJsonElement(jsonEntry.get("dc:creator")))
            creator = jsonEntry.get("dc:creator").getAsString();
        if (checkJsonElement(jsonEntry.get("prism:publicationName")))
            entry.publicationName = jsonEntry.get("prism:publicationName").getAsString();
        if (checkJsonElement(jsonEntry.get("prism:issn")))
            entry.issn = jsonEntry.get("prism:issn").getAsString();
        if (checkJsonElement(jsonEntry.get("prism:doi")))
            entry.doi = jsonEntry.get("prism:doi").getAsString();
        if (checkJsonElement(jsonEntry.get("prism:coverDate"))) {
            entry.year = jsonEntry.get("prism:coverDate")
                    .getAsString().substring(0, 4);
        }
        JsonArray jsonAffils = new JsonArray();
        if (checkJsonElement(jsonEntry.get("citedby-count")))
            entry.citedby = jsonEntry.get("citedby-count").getAsInt();
        if (checkJsonElement(jsonEntry.get("affiliation")))
            jsonAffils = jsonEntry.get("affiliation").getAsJsonArray();
        Iterator<JsonElement> iter = jsonAffils.iterator();
        JsonObject jsonAffil;
        Affil aff;
        while (iter.hasNext()) {
            aff = new Affil();
            jsonAffil = iter.next().getAsJsonObject();
            if (checkJsonElement(jsonAffil.get("affilname")))
                aff.affilName = jsonAffil.get("affilname").getAsString();
            if (checkJsonElement(jsonAffil.get("affiliation-city")))
                aff.affilCity = jsonAffil.get("affiliation-city").getAsString();
            if (checkJsonElement(jsonAffil.get("affiliation-country")))
                aff.affilCountry = jsonAffil.get("affiliation-country").getAsString();
            entry.affils.add(aff);
        }
        doiResults dr = dfal.getAuthorListFromDoi(entry.doi, creator);
        entry.authors = dr.authList;
        entry.citation = dr.citation;
        if ((entry.doi != null) && (entry.doi.isEmpty() == false))
            entry.year = dr.year;
        entry.sjr = JournalRankDB.getInstance().querySJR(entry.title, entry.issn);
        entry.impactFactor = JournalRankDB.getInstance().queryIF(entry.title, entry.issn);
        entry.h5index = JournalRankDB.getInstance().queryGSC(entry.title, entry.issn);
        entry.eigenFactor = 1000*JournalRankDB.getInstance().queryEF(entry.title, entry.issn);
        return entry;
    }
    
    private int externalGet(SearchQuery sq) throws Exception {
        return externalGet(sq, 0);
    }
    
    private int externalGet(SearchQuery sq, int startidx) throws Exception {
            respCode = 0;
            respContent = "";
            StringBuilder url = new StringBuilder("http://api.elsevier.com/content/search/scopus");
            url.append("?query=");
            url.append(URLEncoder.encode(sq.query, "UTF-8"));
            url.append("&date=");
            url.append(endYear + 1 - sq.years);
            url.append("-");
            url.append(endYear);
            url.append("&start=");
            url.append(startidx);
            url.append("&suppressNavLinks=true");


            URL obj = new URL(url.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("X-ELS-APIKey", scopusApiKey);

            respCode = con.getResponseCode();

            StringBuffer response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            
            respContent = response.toString();
            return respCode;
    }
    
}
