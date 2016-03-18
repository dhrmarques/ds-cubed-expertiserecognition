/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import org.javatuples.Triplet;



public class ScopusResults implements Serializable {
    int responseCode;
    String response;
    String yearsRange;
    String searchTerm;
    String timestampResults;
    int totalResults;
    HashMap<String, Integer> yearsCount;
    ArrayList<RankEntry> authorList;
    ArrayList<RankEntry> affilList;
    ArrayList<RankEntry> locationList;
    ArrayList<RankEntry> countryList;
    ArrayList<RankEntry> citationList;
    private final HashMap<String, RankEntry> authorMap;
    private final HashMap<String, RankEntry> affilMap;
    private final HashMap<String, RankEntry> locationMap;
    private final HashMap<String, RankEntry> countryMap;
    
    ScopusResults() {
        responseCode = 0;
        response = "";
        totalResults = 0;
        timestampResults = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        
        yearsCount = new HashMap<>();
        
        authorMap = new HashMap<>();
        affilMap = new HashMap<>();
        locationMap = new HashMap<>();
        countryMap = new HashMap<>();
        
        authorList = new ArrayList<>();
        affilList = new ArrayList<>();
        locationList = new ArrayList<>();
        countryList = new ArrayList<>();
        citationList = new ArrayList<>();
    }
    
    static Comparator<RankEntry> RankEntryComparator 
                      = new Comparator<RankEntry>() {
        @Override
        public int compare(RankEntry o1, RankEntry o2) {
            int retval = 0;
            if (o1.accSjr < o2.accSjr) retval = 1;
            else if (o1.accSjr > o2.accSjr) retval = -1;
            return retval;
        }
    };
    
    static Comparator<Integer> IntDescComparator 
                      = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            int retval = 0;
            if (o1 < o2) retval = 1;
            else if (o1 > o2) retval = -1;
            return retval;
        }
    };
    
    static Comparator<Triplet<Integer,Double,Double>> IntDoubDescComparator 
                      = new Comparator<Triplet<Integer,Double,Double>>() {
        @Override
        public int compare(Triplet<Integer,Double,Double> o1, Triplet<Integer,Double,Double> o2) {
            int retval = 0;
            if (o1.getValue0() < o2.getValue0()) retval = 1;
            else if (o1.getValue0() > o2.getValue0()) retval = -1;
            return retval;
        }
    };
    
    public void convertAllMapsToSortedLists() throws Exception {
        convertMapsToSortedLists(authorMap, authorList);
        convertMapsToSortedLists(affilMap, affilList);
        convertMapsToSortedLists(locationMap, locationList);
        convertMapsToSortedLists(countryMap, countryList);
        citationList.sort(RankEntryComparator);
        
        RankEntry re;
        Iterator<RankEntry> rei = citationList.iterator();
        while (rei.hasNext()) {
            re = rei.next();
            re.meanSjr = re.accSjr / re.numOfEntries;
            re.meanImpactFactor = re.accImpactFactor / re.numOfEntries;
            re.meanH5Index = re.accH5Index / re.numOfEntries;
            re.meanEigenFactor = re.accEigenFactor / re.numOfEntries;
            re.meanCitedby = re.accCitedby / re.numOfEntries;
        }
        
//        Set<String> keySet = yearsCount.keySet();
//        Iterator<String> iter;
//        iter = keySet.iterator();
//        String yr;
//        while (iter.hasNext()) {
//            yr = iter.next();
//            System.out.println(yr + " ==> " + yearsCount.get(yr));
//        }
//        System.out.println("Total ==> " + totalResults);
        
        System.out.println("Populating location information");
        Geocode gci = Geocode.getInstance();
        GeoCoord gcd;
        rei = locationList.iterator();
        while (rei.hasNext()) {
            re = rei.next();
//            gcd = new GeoCoord(re.name);
//            re.gc = gcd;
//            gcd = gci.query(re.name);
//            re.gc = gcd;
            gci.query(re.name);
        }
    }
    
    void convertMapsToSortedLists(HashMap<String, RankEntry> sre, ArrayList<RankEntry> rel) {
        RankEntry re;
        rel.clear();
        rel.addAll(sre.values());
        sre.clear();
        Iterator<RankEntry> rei = rel.iterator();
        while (rei.hasNext()) {
            re = rei.next();
            re.meanSjr = re.accSjr / re.numOfEntries;
            re.meanImpactFactor = re.accImpactFactor / re.numOfEntries;
            re.meanH5Index = re.accH5Index / re.numOfEntries;
            re.meanEigenFactor = re.accEigenFactor / re.numOfEntries;
            re.meanCitedby = re.accCitedby / re.numOfEntries;
            re.citedbyList.sort(IntDoubDescComparator);
            re.papersHindex = calculateHindex(re.citedbyList);
            re.papersGindex = calculateGindex(re.citedbyList);
            re.papersEindex = calculateEindex(re.citedbyList);
            re.papersMindex = calculateMedianCit(re.citedbyList, re.papersHindex);
            re.hrat = calculateHRat(re.citedbyList, re.papersHindex);
            re.hsjr = calculateHSJR(re.citedbyList);
            re.hif = calculateHIF(re.citedbyList);
            re.hcit = calculateHCIT(re.citedbyList);
            re.hcit2 = re.papersHindex;
            re.hcit2 += (Math.log10((re.papersMindex * re.papersHindex) + 1)/10);
            re.medianCitedby = calculateMedianCit(re.citedbyList, re.citedbyList.size());
            re.citedbyList.clear();
        }
        rel.sort(RankEntryComparator);
    }
    
    void analyzeEntries(ArrayList<Entry> entries) {
        Iterator<Entry> iter = entries.iterator();
        while (iter.hasNext()) {
            analyzeEntry(iter.next());
        }
    }
    
    private void updateMap(HashMap<String, RankEntry> themap, String name, Entry entry) {
        RankEntry re;
        if (themap.containsKey(name)) {
            re = themap.get(name);
        } else {
            re = new RankEntry(name);
        }
        re.accSjr += entry.sjr;
        re.accImpactFactor += entry.impactFactor;
        re.accH5Index += entry.h5index;
        re.accEigenFactor += entry.eigenFactor;
        re.accCitedby += entry.citedby;
        re.maxCitedby = (entry.citedby > re.maxCitedby) ? entry.citedby : re.maxCitedby;
        re.citedbyList.add(new Triplet<>(entry.citedby, entry.sjr, entry.impactFactor));
        re.numOfEntries++;
        themap.put(name, re);
    }

    private void analyzeEntry(Entry entry) {
        Iterator<String> iterAuth = entry.authors.iterator();
        String str;
        
        if (entry.year != null) {
            if (yearsCount.containsKey(entry.year)) {
                int yrval = yearsCount.get(entry.year);
                yrval++;
                yearsCount.replace(entry.year, yrval);
            } else {
                yearsCount.put(entry.year, 1);
            }
        }
        
        if (entry.citation.length() > 0) {
            RankEntry re = new RankEntry(entry.citation);
            re.numOfEntries = 1;
            re.accSjr = entry.sjr;
            re.accImpactFactor = entry.impactFactor;
            re.accH5Index = entry.h5index;
            re.accEigenFactor = entry.eigenFactor;
            re.accCitedby = entry.citedby;
            re.maxCitedby = entry.citedby;
            if (entry.affils.size() > 0) {
                Iterator<Affil> iter = entry.affils.iterator();
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (iter.hasNext()) {
                    if (i > 0) sb.append(", ");
                    sb.append(iter.next().affilName);
                    i++;
                }
                re.misc = sb.toString();
            }
            citationList.add(re);
        }
        
        while (iterAuth.hasNext()) {
            str = iterAuth.next();
            if (str.length() > 0) {
                updateMap(authorMap, str, entry);
            }
        }
        
        StringBuilder sb;
        Affil aff;
        Iterator<Affil> iterAffil = entry.affils.iterator();
        while (iterAffil.hasNext()) {
            aff = iterAffil.next();
            if (aff.affilName.length() > 0)
                updateMap(affilMap, aff.affilName, entry);
            if (aff.affilCountry.length() > 0)
                updateMap(countryMap, aff.affilCountry, entry);
            sb = new StringBuilder(aff.affilCity);
            sb.append(", ");
            sb.append(aff.affilCountry);
            if (aff.affilCity.length() > 0) {
                updateMap(locationMap, sb.toString(), entry);
            }
        }
   }
    
    public void updateTimestamp() {
        timestampResults = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    private int calculateHindex(ArrayList<Triplet<Integer, Double, Double>> citedbyList) {
        Iterator<Triplet<Integer, Double, Double>> cbi = citedbyList.iterator();
        int numOfPapers = 0;
        int paperHindex = 0;
        int citedby;
        while (cbi.hasNext()) {
            citedby = cbi.next().getValue0();
            numOfPapers++;
            if (citedby >= numOfPapers) {
                paperHindex = numOfPapers;
            } else break;
        }
        return paperHindex;
    }
    
    private int calculateGindex(ArrayList<Triplet<Integer, Double, Double>> citedbyList) {
        Iterator<Triplet<Integer, Double, Double>> cbi = citedbyList.iterator();
        int numOfPapers = 0;
        int paperGindex = 0;
        int citedby;
        while (cbi.hasNext()) {
            citedby = cbi.next().getValue0();
            numOfPapers++;
            if (citedby >= (numOfPapers * numOfPapers)) {
                paperGindex = numOfPapers;
            } else break;
        }
        return paperGindex;
    }
    
    private double calculateEindex(ArrayList<Triplet<Integer, Double, Double>> citedbyList) {
        Iterator<Triplet<Integer, Double, Double>> cbi = citedbyList.iterator();
        int numOfPapers = 0;
        int citedby;
        int hindex=0;
        long totalCit=0;
        double Eindex=0;
        while (cbi.hasNext()) {
            citedby = cbi.next().getValue0();
            numOfPapers++;
            totalCit += citedby;
            if (citedby >= numOfPapers) {
                hindex = numOfPapers;
            } else break;
        }
        Eindex = Math.sqrt(totalCit - (hindex*hindex));
        return Eindex;
    }
    
    private double calculateHRat(ArrayList<Triplet<Integer, Double, Double>> citedbyList, double hindex) {
        Iterator<Triplet<Integer, Double, Double>> cbi = citedbyList.iterator();
        int numOfPapers = 0;
        int citedby;
        double remain=0;
        if (hindex > 0) {
            while (cbi.hasNext()) {
                citedby = cbi.next().getValue0();
                numOfPapers++;
                if (citedby == hindex) {remain++;}
                if (hindex == numOfPapers) {
                    break;
                }
            }
        }
        if (cbi.hasNext()) {
            citedby = cbi.next().getValue0();
        } else  {
            citedby = 0;
        }
        remain += (hindex + 1) - citedby;
        return ((hindex + 1) - (remain / (2*hindex + 1)));
    }
    

    private double calculateHSJR(ArrayList<Triplet<Integer, Double, Double>> citedbyList) {
        Iterator<    Triplet<Integer, Double, Double>> cbi = citedbyList.iterator();
        int numOfPapers = 0;
        double HSJRindex = 0;
        int citedby;
        double totalsjr = 0;
        while (cbi.hasNext()) {
            Triplet<Integer, Double, Double> pr = cbi.next();
            citedby = pr.getValue0();
            numOfPapers++;
            if (citedby >= numOfPapers) {
                HSJRindex = numOfPapers;
                totalsjr += pr.getValue1();
            } else break;
        }
        if (HSJRindex > 0) {
            //HSJRindex += (totalsjr/(100*HSJRindex));
            HSJRindex += Math.log10(totalsjr + 1)/10;
        }
        return HSJRindex;
    }
    
    private double calculateHIF(ArrayList<Triplet<Integer, Double, Double>> citedbyList) {
        Iterator<    Triplet<Integer, Double, Double>> cbi = citedbyList.iterator();
        int numOfPapers = 0;
        double HIFindex = 0;
        int citedby;
        double totalif = 0;
        while (cbi.hasNext()) {
            Triplet<Integer, Double, Double> pr = cbi.next();
            citedby = pr.getValue0();
            numOfPapers++;
            if (citedby >= numOfPapers) {
                HIFindex = numOfPapers;
                totalif += pr.getValue2();
            } else break;
        }
        if (HIFindex > 0) {
            //HIFindex += (totalif/(100*HIFindex));
            HIFindex += Math.log10(totalif + 1)/10;
        }
        return HIFindex;
    }
        
    private double calculateHCIT(ArrayList<Triplet<Integer, Double, Double>> citedbyList) {
        Iterator<    Triplet<Integer, Double, Double>> cbi = citedbyList.iterator();
        int numOfPapers = 0;
        double HCITindex = 0;
        int citedby;
        double totalcit = 0;
        while (cbi.hasNext()) {
            Triplet<Integer, Double, Double> pr = cbi.next();
            citedby = pr.getValue0();
            numOfPapers++;
            if (citedby >= numOfPapers) {
                HCITindex = numOfPapers;
                totalcit += citedby;
            } else break;
        }
        if (HCITindex > 0) {
           //HCITindex += (totalcit/(1000*HCITindex));
           HCITindex += Math.log10(totalcit + 1)/10;
        }
        return HCITindex;
    }
    
    private long calculateMedianCit(ArrayList<Triplet<Integer, Double, Double>> citedbyList, int sizeoflist) {
        long mediancite;
        int listsize = sizeoflist;
        int halflistsize = listsize / 2;
        if (sizeoflist == 0) {
            return 0;
        } else if (sizeoflist == 1) {
            return citedbyList.get(0).getValue0();
        } else if ((listsize % 2) == 0) {
            mediancite = citedbyList.get(halflistsize).getValue0();
            mediancite += citedbyList.get(halflistsize - 1).getValue0();
            mediancite = mediancite / 2;
        } else {
            mediancite = citedbyList.get((listsize - 1)/2).getValue0();
        }
        return mediancite;
    }
    

}
