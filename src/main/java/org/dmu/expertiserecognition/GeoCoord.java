/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.Serializable;

/**
 *
 * @author ruben
 */
public class GeoCoord implements Serializable {
    String location;
    String latd;
    String longd;
    
    GeoCoord(String loc) {
        location = loc;
        latd = "";
        longd = "";
    }
    
    String createLatLongPair() {
        StringBuilder sb = new StringBuilder(latd);
        sb.append(",");
        sb.append(longd);
        return sb.toString();
    }
}
