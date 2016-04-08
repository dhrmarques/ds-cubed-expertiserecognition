/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import static org.fusesource.leveldbjni.JniDBFactory.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.javatuples.Pair;
import org.nustaq.serialization.FSTConfiguration;

/**
 *
 * @author ruben
 */
public class ResultsDB {
    
    private static ResultsDB INSTANCE;
    private DB resultsDB;
    private static FSTConfiguration fstconf;
    
    private ResultsDB() {
        fstconf = FSTConfiguration.createDefaultConfiguration();
    }
    
    public synchronized static ResultsDB getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResultsDB();
            INSTANCE.connectToDB();
        }
        return ResultsDB.INSTANCE;
    }
    
    public void connectToDB() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            resultsDB = factory.open(new File("web/dynamicdb/searchresults"), options);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ResultsDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void closeDB() {
        try {
            if (resultsDB != null) resultsDB.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ResultsDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    synchronized ScopusResults queryFromDB(SearchQuery sq) {
        ScopusResults res = new ScopusResults();
        byte[] value = resultsDB.get(bytes(sq.pack()));
        if ((value != null) && (value.length > 0)) {
            res = (ScopusResults)fstconf.asObject(value);
        }
        return res;
    }
    
    synchronized void addToDB(SearchQuery sq, ScopusResults res) {
        byte barray[] = fstconf.asByteArray(res);
        resultsDB.put(bytes(sq.pack()), barray);
    }
    
    synchronized void deleteFromDB(SearchQuery sq) {
        resultsDB.delete(bytes(sq.pack()));
    }
    
    synchronized ArrayList<Pair<SearchQuery, ScopusResults>> listOfResults() throws IOException {
        DBIterator it = resultsDB.iterator();
        SearchQuery sq;
        ScopusResults res;
        Map.Entry<byte[], byte[]> nxtentry;
        ArrayList<Pair<SearchQuery, ScopusResults>> sqlist = new ArrayList<>();
        try {
          for(it.seekToFirst(); it.hasNext(); it.next()) {
              sq = new SearchQuery();
              nxtentry = it.peekNext();
              sq.unpack(asString(nxtentry.getKey()));
              res = new ScopusResults();
              byte[] value = nxtentry.getValue();
              if ((value != null) && (value.length > 0)) {
                  res = (ScopusResults)fstconf.asObject(value);
              }
              sqlist.add(new Pair<>(sq, res));
          }
        } finally {
          it.close();
        }
        return sqlist;
    }
    
    synchronized String listOfResultsHTML() throws IOException {
        ArrayList<Pair<SearchQuery, ScopusResults>> lsq = listOfResults();
        StringBuilder sb = new StringBuilder();
        sb.append("<script src=\"/static/jquery.tablesorter.min.js\"></script>\n");
        sb.append("<script src=\"/static/cachetablesort.js\"></script>\n");
        sb.append("<table id=\"cachetbl\" class=\"center\">\n");
        sb.append("<thead><tr><th>Search Term</th><th>Number of Years</th><th>Status</th><th>Timestamp</th></tr></thead>\n<tbody>\n");
        Iterator<Pair<SearchQuery, ScopusResults>> it = lsq.iterator();
        Pair<SearchQuery, ScopusResults> sq;
        while (it.hasNext())
        {
            sq = it.next();
            sb.append("<tr><td><a href=\"/?search=");
            sb.append(sq.getValue0().encoded_query());
            sb.append("&amp;years=");
            sb.append(sq.getValue0().years);
            sb.append("\" onclick=\"return gotoUrlFct()\">");
            sb.append(sq.getValue0().query);
            sb.append("</a></td><td>");
            sb.append(sq.getValue0().years);
            sb.append("</td><td>");
            sb.append(convertStatus(sq.getValue1()));
            sb.append("</td><td>");
            sb.append(sq.getValue1().timestampResults);
            sb.append("</td></tr>\n");
        }
        sb.append("</tbody></table>\n");
        return sb.toString();
    }

    private String convertStatus(ScopusResults res) {
        String retstr;
        switch (res.responseCode)
        {
            case 200:
                retstr = "Results ready";
                break;
            case -888:
                retstr = "Search in progress";
                break;
            default:
                retstr = "Error";
                break;
        }
        return retstr;
    }
}
