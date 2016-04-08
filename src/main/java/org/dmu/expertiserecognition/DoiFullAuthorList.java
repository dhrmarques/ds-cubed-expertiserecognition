/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author ruben
 */

class doiResults {
    ArrayList<String> authList;
    String citation;
    String year;
    
    doiResults() {
        authList = new ArrayList<>();
        citation = "";
        year = "";
    }
    
    doiResults(String alt) {
        authList = new ArrayList<>();
        authList.add(alt);
        citation = "";
        year = "";
    }
}


public class DoiFullAuthorList {
    
    doiResults getAuthorListFromDoi(String doi, String alt) throws Exception {
        boolean DoiDataSuccess;
        doiResults dr = new doiResults(alt);
        if (doi == null) return dr;
        if (doi.isEmpty()) return dr;
        StringBuilder url = new StringBuilder("http://search.crossref.org/dois?q=");
        url.append(URLEncoder.encode(doi, "UTF-8"));
        URL obj = new URL(url.toString());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        DoiDataSuccess = responseCode == 200;
        if (DoiDataSuccess == false) return dr;
        StringBuffer response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        SearchThread.getInstance().setExtraMessage(response.toString());
        dr = extractFullAuthorList(response.toString(), alt);
        SearchThread.getInstance().setExtraMessage("");
        return dr;
    }
    
    private doiResults extractFullAuthorList(String jsonresp, String alt) throws Exception
    {
        doiResults dr = new doiResults(standardizeAuthorName(alt));
        ArrayList<String> strlst = new ArrayList<>();
        JsonElement json = new JsonParser().parse(jsonresp);
        JsonArray array= json.getAsJsonArray();
        if (array.iterator().hasNext() == false) {
            return dr;
        } 
        JsonElement elem = array.iterator().next();
        JsonObject jsonobj = elem.getAsJsonObject();
        String coins = URLDecoder.decode(jsonobj.get("coins").getAsString(), "UTF-8");
        String[] coinlist = coins.split("&amp;");
        int i;
        String[] pair;
        for (i=0; i<coinlist.length; i++) {
            pair = coinlist[i].split("=");
            if ((pair.length >= 2)&&(pair[0].trim().equalsIgnoreCase("rft.au"))) {
                strlst.add(standardizeAuthorName(pair[1]));
            }
        }
        dr.authList = strlst;
        StringBuilder sb = new StringBuilder("<a href=\"");
        sb.append(jsonobj.get("doi").getAsString());
        sb.append("\" target=\"_blank\">");
        sb.append(jsonobj.get("fullCitation").getAsString());
        sb.append("</a>");
        dr.citation = sb.toString();
        
        // Adding "year" field verification; it may come as null 
        // (translated by Gson as a JsonNull field)
        JsonElement year = jsonobj.get("year");
        if (year == null || year.isJsonNull()) {
            dr.year = "N/A";
        } else {
            dr.year = year.getAsString();
        }
        
        return dr;
    }
    
    public static String standardizeAuthorName(String name) {
        String newName = name.trim();
        newName = deAccent(newName);
        newName = WordUtils.capitalizeFully(newName, new char[]{'.', ' ', '-', '\''});
        newName = newName.replaceAll("([A-Za-z0-9-']+)\\.([A-Za-z0-9-']+)", "$1. $2");
        newName = newName.replaceAll("[ ]{2,}", " ");
        newName = newName.replaceAll("^(.*)([^\\.]) ([A-Z]\\.)$", "$3 $1$2");
        String surname;
        String authInitials;
        if (Pattern.matches("^.*\\.$", newName)) {
            surname = newName.replaceAll("^([^ ]+) (.*)$", "$1");
            authInitials = newName.replaceAll("^([^ ]+) (.*)$", "$2");
        } else {
            surname = newName.replaceAll("^(.*) ([^ ]+)$", "$2");
            authInitials = newName.replaceAll("^(.*) ([^ ]+)$", "$1");
        }
        authInitials = WordUtils.initials(authInitials, new char[]{'.', ' '});
        byte[] authInitialsBytes = authInitials.getBytes();
        int i;
        StringBuilder sb = new StringBuilder();
        for (i=0; i < authInitialsBytes.length; i++) {
            sb.append((char)authInitialsBytes[i]);
            sb.append(". ");
        }
        sb.append(surname);
        newName = sb.toString();
        return newName;
    }
    
    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
}
