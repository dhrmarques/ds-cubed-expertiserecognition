/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmu.expertiserecognition;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.UnixCrypt;
import static org.fusesource.leveldbjni.JniDBFactory.factory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

/**
 *
 * @author ruben
 */
public class UsersDB {
    private static UsersDB INSTANCE;
    private DB usersStoreDB;
    private final HashLoginService loginService;
    
    private UsersDB() {
        loginService = new HashLoginService();
    }
    
    public synchronized static UsersDB getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsersDB();
            INSTANCE.connectToDB();
            INSTANCE.createLoginService();
        }
        return UsersDB.INSTANCE;
    }
    
    public void connectToDB() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
                usersStoreDB = factory.open(new File("web/dynamicdb/users"), options);
                if (usersStoreDB.get("admin".getBytes("UTF-8")) == null) {
                    String digest = "CRYPT:" + UnixCrypt.crypt("admin", "admin");
                    usersStoreDB.put("admin".getBytes("UTF-8"), digest.getBytes("UTF-8"));
                }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void closeDB() {
        try {
            if (usersStoreDB != null) usersStoreDB.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized HashLoginService getLoginService() {
        return loginService;
    }
    
    public synchronized boolean checkPassword(String username, String passwd) {
        String digest = "CRYPT:" + UnixCrypt.crypt(passwd, username);
        byte[] ba = null;
        try {
            ba = usersStoreDB.get(username.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ba == null) {return false;}
        String digest2 = new String(ba);
        return (digest2.compareTo(digest) == 0);
    }
    
    public synchronized void addUser(String username, String passwd) {
        
        String digest = "CRYPT:" + UnixCrypt.crypt(passwd, username);
        try {
            usersStoreDB.put(username.getBytes("UTF-8"), digest.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        loginService.putUser(username, Credential.getCredential(digest), new String[] {"user"});

    }
    
    public synchronized void removeUser(String username) {
        try {
            usersStoreDB.delete(username.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        loginService.remove(username);
    }
    
    public synchronized void changePasswd(String username, String passwd) {
        removeUser(username);
        addUser(username, passwd);
    }
    
    public synchronized ArrayList<String> getUsersList() {
        DBIterator iter = usersStoreDB.iterator();
        iter.seekToFirst();
        ArrayList<String> arr = new ArrayList<>();
        while (iter.hasNext()) {
            try {
                Map.Entry<byte[], byte[]> etry = iter.next();
                String username = new String(etry.getKey(), "UTF-8");
                arr.add(username);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
            }
                
        }
        return arr;
    }
    
    private void createLoginService() {
        DBIterator iter = usersStoreDB.iterator();
        iter.seekToFirst();
        while (iter.hasNext()) {
            try {
                Map.Entry<byte[], byte[]> etry = iter.next();
                String username = new String(etry.getKey(), "UTF-8");
                String passwd = new String(etry.getValue(), "UTF-8");
                loginService.putUser(username,
                        Credential.getCredential(passwd),
                        new String[] {"user"});
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(UsersDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
