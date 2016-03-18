/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.SQLiteConfig;

/**
 *
 * @author ruben
 */
public class JournalRankDB {
    
    private static JournalRankDB INSTANCE;
    private Connection connectsjr;
    
    private JournalRankDB() {
    }
    
    public synchronized static JournalRankDB getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JournalRankDB();
            INSTANCE.connectToDB();
        }
        return JournalRankDB.INSTANCE;
    }

    private void connectToDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(true);
            config.setLockingMode(SQLiteConfig.LockingMode.EXCLUSIVE);
            config.setJournalMode(SQLiteConfig.JournalMode.OFF);
            connectsjr = DriverManager.getConnection("jdbc:sqlite:web/staticdb/alldb/alldb.db", config.toProperties());
        } catch (SQLException ex) {
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void closeDB() {
        try {
            connectsjr.close();
        } catch (SQLException ex) {
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized double querySJR(String title, String issn) {
        StringBuilder qryb;
        double sjrval = 0;
        if ((title == null)||(title.length() == 0)) return 0;
        if ((issn == null)||(issn.length() == 0)) {
            qryb = new StringBuilder("SELECT sjr FROM sjrtbl WHERE (title LIKE '");
            qryb.append(title.replaceAll("'", "''"));
            qryb.append("');");
        } else {
            qryb = new StringBuilder("SELECT sjr FROM sjrtbl WHERE (issn=\"");
            qryb.append(issn);
            qryb.append("\");");   
        }
        try {

            Statement statement = connectsjr.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery(qryb.toString());
            if(rs.next()) {
                sjrval = rs.getDouble("sjr");
            }
            return sjrval;
        } catch (SQLException ex) {
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sjrval;
    }
    
    public synchronized double queryIF(String title, String issn) {
        StringBuilder qryb;
        double ifval = 0;
        if ((title == null)||(title.length() == 0)) return 0;
        if ((issn == null)||(issn.length() == 0)) {
            qryb = new StringBuilder("SELECT impactfactor FROM iftbl WHERE (title LIKE '");
            qryb.append(title.replaceAll("'", "''"));
            qryb.append("');");
        } else {
            qryb = new StringBuilder("SELECT impactfactor FROM iftbl WHERE (issn=\"");
            qryb.append(issn);
            qryb.append("\" OR title LIKE '");
            qryb.append(title.replaceAll("'", "''"));
            qryb.append("');");
        }
        try {

            Statement statement = connectsjr.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery(qryb.toString());
            if(rs.next()) {
                ifval = rs.getDouble("impactfactor");
            }
            return ifval;
        } catch (SQLException ex) {            
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, ("Query was ==> " + qryb.toString()));
        }
        return ifval;
    }

        public synchronized double queryEF(String title, String issn) {
        StringBuilder qryb;
        double ifval = 0;
        if ((title == null)||(title.length() == 0)) return 0;
        if ((issn == null)||(issn.length() == 0)) {
            qryb = new StringBuilder("SELECT eigenfactor FROM iftbl WHERE (title LIKE '");
            qryb.append(title.replaceAll("'", "''"));
            qryb.append("');");
        } else {
            qryb = new StringBuilder("SELECT eigenfactor FROM iftbl WHERE (issn=\"");
            qryb.append(issn);
            qryb.append("\" OR title LIKE '");
            qryb.append(title.replaceAll("'", "''"));
            qryb.append("');");
        }
        try {

            Statement statement = connectsjr.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery(qryb.toString());
            if(rs.next()) {
                ifval = rs.getDouble("eigenfactor");
            }
            return ifval;
        } catch (SQLException ex) {            
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, ("Query was ==> " + qryb.toString()));
        }
        return ifval;
    }
    
    public synchronized double queryGSC(String title, String issn) {
        StringBuilder qryb;
        double gscval = 0;
        if ((title == null)||(title.length() == 0)) return 0;
        if ((issn == null)||(issn.length() == 0)) {
            qryb = new StringBuilder("SELECT h5index FROM gsctbl WHERE (title LIKE '");
            qryb.append(title.replaceAll("'", "''"));
            qryb.append("');");
        } else {
            qryb = new StringBuilder("SELECT h5index FROM gsctbl WHERE (issn=\"");
            qryb.append(issn);
            qryb.append("\" OR title LIKE '");
            qryb.append(title.replaceAll("'", "''"));
            qryb.append("');");
        }
        try {

            Statement statement = connectsjr.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery(qryb.toString());
            if(rs.next()) {
                gscval = rs.getDouble("h5index");
            }
            return gscval;
        } catch (SQLException ex) {
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(JournalRankDB.class.getName()).log(Level.SEVERE, ("Query was ==> " + qryb.toString()));
        }
        return gscval;
    }
}
