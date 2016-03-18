/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ruben
 */
public class SearchQuery {
    String query;
    int years;
    
    SearchQuery() {
        
    }
    
    SearchQuery(String querystr, int numofyears) {
        query = querystr;
        years = numofyears;
    }
    
    String pack() {
        return query + "\t" + String.valueOf(years);
    }
    
    void unpack(String packedstr) {
        String[] splitr = packedstr.split("\t");
        query = splitr[0];
        years = Integer.parseInt(splitr[1]);
    } 

    String encoded_query() {
        String res = "";
        if ((query != null)&&(query.length() > 0)) {
            try {
                res = URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SearchQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return res;
    }
}
