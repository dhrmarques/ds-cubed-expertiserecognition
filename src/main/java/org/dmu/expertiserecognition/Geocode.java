/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import com.google.gson.*;
import org.iq80.leveldb.*;
import static org.fusesource.leveldbjni.JniDBFactory.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Level;

/**
 *
 * @author ruben
 */

public class Geocode {
        
    private static final String MapQuestAppKey = "jGkXbTFxeHRD21rAkESMBlKkqnzIfWAt";
    private static Geocode INSTANCE;
    private DB geocodeDB;
    
    private Geocode() {
    }
    
    public synchronized static Geocode getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Geocode();
            INSTANCE.connectToDB();
        }
        return Geocode.INSTANCE;
    }
    
    public void connectToDB() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            geocodeDB = factory.open(new File("web/dynamicdb/geocache"), options);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Geocode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void closeDB() {
        try {
            if (geocodeDB != null) geocodeDB.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Geocode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized GeoCoord query(String loc) throws Exception {
        GeoCoord gcd = queryFromDB(loc);
        if (gcd.latd.isEmpty()) {
            //gcd = queryFromDataScienceToolkit(loc);
            gcd = queryFromNominatimMapQuest(loc);
            addToDB(gcd);
        }
        return gcd;
    }
    
    public synchronized GeoCoord queryFromDB(String loc) {
        String value = asString(geocodeDB.get(bytes(loc)));
        GeoCoord gcd = new GeoCoord(loc);
        if ((value == null)||(value.isEmpty())) return gcd;
        gcd.location = loc;
        String[] val = value.split(",");
        gcd.latd = val[0];
        gcd.longd = val[1];
        return gcd;
    }
    
    private void addToDB(GeoCoord gcd) {
        if (gcd.latd.isEmpty()) return;
        geocodeDB.put(bytes(gcd.location), bytes(gcd.createLatLongPair()));
    }
    
    private GeoCoord queryFromDataScienceToolkit(String loc) throws Exception {
        boolean GCDDataSuccess;
        GeoCoord gcd = new GeoCoord(loc);
        if (loc == null) return gcd;
        if (loc.isEmpty()) return gcd;
        StringBuilder url = new StringBuilder("http://www.datasciencetoolkit.org/maps/api/geocode/json?sensor=false&address=");
        url.append(URLEncoder.encode(loc, "UTF-8"));
        URL obj = new URL(url.toString());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        GCDDataSuccess = responseCode == 200;
        if (GCDDataSuccess == false) return gcd;
        StringBuffer response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        gcd = extractGeocoordFromDataScienceToolkit(response.toString(), loc);
        return gcd;
    }

    private GeoCoord extractGeocoordFromDataScienceToolkit(String jsonresp, String loc) throws Exception {
        GeoCoord gcd = new GeoCoord(loc);
        JsonElement json = new JsonParser().parse(jsonresp);
        JsonObject mainj = json.getAsJsonObject();
        if (mainj.get("status").getAsString().equals("OK") == false)
            return gcd;
        JsonArray res = mainj.get("results").getAsJsonArray();
        Iterator<JsonElement> iter = res.iterator();
        if (iter.hasNext() == false) return gcd;        
        JsonObject geom = iter.next().getAsJsonObject().get("geometry").getAsJsonObject();
        JsonObject locg = geom.get("location").getAsJsonObject();
        gcd.latd = locg.get("lat").getAsString();
        gcd.longd = locg.get("lng").getAsString();
        return gcd;
    }
    
    private GeoCoord queryFromNominatimMapQuest(String loc) throws Exception {
        boolean GCDDataSuccess;
        GeoCoord gcd = new GeoCoord(loc);
        if (loc == null) return gcd;
        if (loc.isEmpty()) return gcd;
        StringBuilder url = new StringBuilder("http://open.mapquestapi.com/nominatim/v1/search.php?format=json&addressdetails=0&limit=1&q=");
        url.append(URLEncoder.encode(loc, "UTF-8"));
        url.append("&key=");
        url.append(MapQuestAppKey);
        URL obj = new URL(url.toString());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        GCDDataSuccess = responseCode == 200;
        if (GCDDataSuccess == false) return gcd;
        StringBuffer response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        gcd = extractGeocoordFromNominatimMapQuest(response.toString(), loc);
        return gcd;
    }

    private GeoCoord extractGeocoordFromNominatimMapQuest(String jsonresp, String loc) throws Exception {
        GeoCoord gcd = new GeoCoord(loc);
        JsonElement json = new JsonParser().parse(jsonresp);
        JsonArray mainjArr = json.getAsJsonArray();
        if (mainjArr.size() == 0) {
            return gcd;
        }
        JsonObject mainj = mainjArr.get(0).getAsJsonObject();
        gcd.latd = mainj.get("lat").getAsString();
        gcd.longd = mainj.get("lon").getAsString();
        return gcd;
    }
    
}
