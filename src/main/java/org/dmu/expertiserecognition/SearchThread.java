/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ruben
 */
public class SearchThread extends Thread{
    
    private static SearchThread INSTANCE;
    private boolean running;
    private final ArrayBlockingQueue<SearchQuery> queue;
    private String extraMessage;
    
    private SearchThread() {
        running = true;
        queue = new ArrayBlockingQueue<>(100);
        extraMessage = "";
    }
    
    public synchronized static SearchThread getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SearchThread();
        }
        return SearchThread.INSTANCE;
    }
    
    public void stopThread() {
        running = false;
    }
    
    public void put(SearchQuery sq)
    {
        try {
            queue.put(sq);
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String wrapException(Exception ex) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter( writer );
        printWriter.println(extraMessage);
        ex.printStackTrace( printWriter );
        printWriter.flush();
        return writer.toString();
    }
    
    public void setExtraMessage(String msg) {
        extraMessage = msg;
    }
    
    private ScopusResults wrappedsearch(SearchQuery sq) {
        ScopusSearcher searcher;
        ScopusResults res;
        searcher = new ScopusSearcher();
        extraMessage = "";
        try {
            res = searcher.search(sq);
        } catch (Exception ex) {
            res = new ScopusResults();
            res.responseCode = -777;
            res.response = wrapException(ex);
            Logger.getLogger(SearchThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    @Override
    public void run() {
        SearchQuery sq;
        ScopusResults res;
    	while (running) {
            try {
                sq = queue.take();
                res = wrappedsearch(sq);
                ResultsDB.getInstance().addToDB(sq, res);
            } catch (Exception ex) {
                Logger.getLogger(SearchThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
