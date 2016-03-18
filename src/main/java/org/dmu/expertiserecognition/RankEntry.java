/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.Serializable;
import java.util.ArrayList;
import org.javatuples.Triplet;

/**
 *
 * @author ruben
 */
public class RankEntry implements Serializable {
    String name;
    String misc;
    double accSjr;
    double meanSjr;
    double accImpactFactor;
    double meanImpactFactor;
    double accH5Index;
    double meanH5Index;
    double accEigenFactor;
    double meanEigenFactor;
    long accCitedby;
    long meanCitedby;
    long medianCitedby;
    long maxCitedby;
    int papersHindex;
    int papersGindex;
    long papersMindex;
    double papersEindex;
    double hrat;
    double hsjr;
    double hif;
    double hcit;
    double hcit2;
    ArrayList<Triplet<Integer, Double, Double>> citedbyList;
    int numOfEntries;
    //GeoCoord gc;
    
    RankEntry() {
        name = "";
        misc = "";
        accSjr = 0;
        meanSjr = 0;
        accImpactFactor = 0;
        meanImpactFactor = 0;
        accH5Index = 0;
        meanH5Index = 0;
        accEigenFactor = 0;
        meanEigenFactor = 0;
        accCitedby = 0;
        meanCitedby = 0;
        medianCitedby = 0;
        maxCitedby = 0;
        papersHindex = 0;
        papersGindex = 0;
        papersMindex = 0;
        papersEindex = 0;
        hrat = 0;
        hsjr = 0;
        hif = 0;
        hcit = 0;
        hcit2 = 0;
        numOfEntries = 0;
        citedbyList = new ArrayList<>();
    }
    
    RankEntry(String namee) {
        name = namee;
        misc = "";
        accSjr = 0;
        meanSjr = 0;
        accImpactFactor = 0;
        meanImpactFactor = 0;
        accH5Index = 0;
        meanH5Index = 0;
        accEigenFactor = 0;
        meanEigenFactor = 0;
        accCitedby = 0;
        meanCitedby = 0;
        maxCitedby = 0;
        papersHindex = 0;
        papersGindex = 0;
        papersMindex = 0;
        papersEindex = 0;
        hrat = 0;
        hsjr = 0;
        hif = 0;
        hcit = 0;
        hcit2 = 0;
        numOfEntries = 0;
        citedbyList = new ArrayList<>();
    }
}
