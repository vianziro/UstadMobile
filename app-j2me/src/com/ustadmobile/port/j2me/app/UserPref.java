/*
    This file is part of Ustad Mobile.

    Ustad Mobile Copyright (C) 2011-2014 UstadMobile Inc.

    Ustad Mobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version with the following additional terms:

    All names, links, and logos of Ustad Mobile and Toughra Technologies FZ
    LLC must be kept as they are in the original distribution.  If any new
    screens are added you must include the Ustad Mobile logo as it has been
    used in the original distribution.  You may not create any new
    functionality whose purpose is to diminish or remove the Ustad Mobile
    Logo.  You must leave the Ustad Mobile logo as the logo for the
    application to be used with any launcher (e.g. the mobile app launcher).

    If you want a commercial license to remove the above restriction you must
    contact us.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Ustad Mobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 */
package com.ustadmobile.port.j2me.app;

import com.ustadmobile.core.util.UMFileUtil;
import com.ustadmobile.core.util.UMIOUtils;
import com.ustadmobile.core.util.UMUtil;
import com.ustadmobile.port.j2me.impl.UstadMobileSystemImplJ2ME;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author varuna
 */
public class UserPref {
    
    //Default app settings. 
    public static Hashtable userSettings;
    public static String userDataDir = null;
    static final String REC_STORE = "UstadMobileUser";
    
    //Constructor
    public UserPref() {
    }
    
    //set up default settings.
    private static void setDefaultPreferences() {
        Hashtable defaultUserSettings = new Hashtable();
        defaultUserSettings.put("username", "");
        defaultUserSettings.put("password", "");
        defaultUserSettings.put("", "");
        //defaultUserSettings.put("", "");
        
        userSettings = new Hashtable();
        userSettings = defaultUserSettings;
    }
    
    public static void updateSetting(String key, String newValue){
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Get Current configuration
        Hashtable currentSettings = getUserSettings();
        if(currentSettings.containsKey(key)){
            currentSettings.remove(key);
            currentSettings.put(key, newValue);
            
            //Put it back in
            
            //Clear it, Close it
            appRms.deleteRMS();
            appRms.closeRMS();
            
            //Open it again
            appRms.openRMS();
            
            //Generate bytes
            byte[] newSettingsBytes = 
                    SerializedHashtable.hashTabletoStream(currentSettings);
            
            //Insert the data in.
            appRms.insertBytes(newSettingsBytes);
            
            
            
        }
        //close the app RMS
        appRms.closeRMS();
        
    }
    
    public static void addSetting(String key, String newValue){   
        if (newValue == null){
            deleteSetting(key);
        }else{
            //Initiate app RMS..
            RMSUtils appRms = new RMSUtils(REC_STORE);

            //Get Current configuration
            Hashtable currentSettings = getUserSettings();

            if (key.equals("CURRENTUSER")){
                key = "CURRENTUSER";
            }else{
                if (currentSettings.containsKey("CURRENTUSER")){
                    String username = currentSettings.get("CURRENTUSER").toString();
                    key = username + "-" + key;
                }
            }

            if(currentSettings.containsKey(key)){
                currentSettings.remove(key);
                currentSettings.put(key, newValue);

                //Put it back in

                //Clear it, Close it
                appRms.deleteRMS();
                appRms.closeRMS();

                //Open it again
                appRms.openRMS();

                //Generate bytes
                byte[] newSettingsBytes = 
                        SerializedHashtable.hashTabletoStream(currentSettings);

                //Insert the data in.
                appRms.insertBytes(newSettingsBytes);



            }else{
                currentSettings.put(key, newValue);
                //Clear it, Close it
                appRms.deleteRMS();
                appRms.closeRMS();

                //Open it again
                appRms.openRMS();

                //Generate bytes
                byte[] newSettingsBytes = 
                        SerializedHashtable.hashTabletoStream(currentSettings);

                //Insert the data in.
                appRms.insertBytes(newSettingsBytes);

            }
            //close the app RMS
            appRms.closeRMS();
        }
    }
    
    public static void deleteSetting(String key){
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Get Current configuration
        Hashtable currentSettings = getUserSettings();
        
        if (key.equals("CURRENTUSER")){
                key = "CURRENTUSER";
            }else{
                if (currentSettings.containsKey("CURRENTUSER")){
                    String username = currentSettings.get("CURRENTUSER").toString();
                    key = username + "-" + key;
                }
            }
        
        if(currentSettings.containsKey(key)){
            currentSettings.remove(key);
            
            //Clear it, Close it
            appRms.deleteRMS();
            appRms.closeRMS();
            
            //Open it again
            appRms.openRMS();
            
            //Generate bytes
            byte[] newSettingsBytes = 
                    SerializedHashtable.hashTabletoStream(currentSettings);
            
            //Insert the data in.
            appRms.insertBytes(newSettingsBytes);
      
        }
        //close the app RMS
        appRms.closeRMS();
    }
    
    public static String getSetting(String key){       
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        String value = null;
        //Get Current configuration
        Hashtable currentSettings = getUserSettings();
        if (key.equals("CURRENTUSER")){
            key = "CURRENTUSER";
        }else{
            if (currentSettings.containsKey("CURRENTUSER")){
                String username = currentSettings.get("CURRENTUSER").toString();
                key = username + "-" + key;
            }
        }
        if(currentSettings.containsKey(key)){
            value = currentSettings.get(key).toString();
        }
        //close the app RMS
        appRms.closeRMS();
        return value;
    }
    
    public static String[] getAllKeys(){
        RMSUtils appRms = new RMSUtils(REC_STORE);
        Hashtable curretSettings = getUserSettings();
        String allUserKeys[] = 
                UMUtil.enumerationToStringArray(curretSettings.keys());
        
        String username = getActiveUser();
        if (username == null){
            return null;
        }
        Vector userKeysVector = new Vector();
        for( int i = 0; i < allUserKeys.length - 1; i++)
        {
            if(allUserKeys[i].startsWith(username+"-")){
                userKeysVector.addElement(allUserKeys[i]);
            }
        }
        
        String[] userKeys = new String[userKeysVector.size()];
        userKeysVector.copyInto(userKeys);
        String simpleUserKeys[] = new String[userKeys.length];
        int prefixLength = (username + "-").length();
        for ( int i = 0; i < userKeys.length; i++){
            userKeys[i] = userKeys[i].substring(prefixLength);
        }
        return userKeys;
    }
    
    public static Hashtable getUserSettings(){
        
        //getDefault values
        //setDefaultPreferences();
        
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Check if there is anything..
        appRms.openRMS();
        byte[] userSettingsByteArrayRMS = appRms.readBytes();
        Hashtable allUserSettingsRMS = SerializedHashtable.streamToHashtable
                (userSettingsByteArrayRMS);
        Hashtable userSettingRMS = new Hashtable();
        System.out.print("Size is: " + allUserSettingsRMS.size());
        String username="";
        if(allUserSettingsRMS.containsKey("CURRENTUSER") && 
                !allUserSettingsRMS.get("CURRENTUSER").equals("")){
            username = allUserSettingsRMS.get("CURRENTUSER").toString();
        }
        
        if (allUserSettingsRMS.isEmpty() || allUserSettingsRMS.size() < 1 ){
            return userSettingRMS;
        }else{
            userSettings=allUserSettingsRMS;
            //appSettings.equals(appSettingsRMS);
        }
        
        //close the app RMS
        appRms.closeRMS();
        return userSettings;
    }
    
   
    /**
     * Find out where we should put the base folder by finding the root folder
     * with the maximum amount of space (this should be the memory card generally)
     */
    public static String getUserDataDir(){
        UstadMobileSystemImplJ2ME umsij = new UstadMobileSystemImplJ2ME();
        String userData = umsij.getSharedContentDir();
        String userFolder = UMFileUtil.joinPaths(new String[]{userData, 
                userSettings.get("username").toString()});
        try{
            FileConnection bCon = (FileConnection)Connector.open(userFolder);
            if (!bCon.isDirectory()){
                bCon.mkdir();
            }
            bCon.close();
            userDataDir = userFolder;
            return userDataDir;
        }catch (Exception ce){
            return null;
        }
    }
    
    public static boolean addToUserSettings(String key, String value){
       return false; 
    }
    
    public static String getActiveUser(){
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Check if there is anything..
        appRms.openRMS();
        byte[] userSettingsByteArrayRMS = appRms.readBytes();
        Hashtable userSettingsRMS = SerializedHashtable.streamToHashtable
                (userSettingsByteArrayRMS);
        
        //Clear it, Close it
        appRms.deleteRMS();
        appRms.closeRMS();

        
        if (userSettingsRMS.containsKey("CURRENTUSER")){
            return userSettingsRMS.get("CURRENTUSER").toString();
        }
        return null;
    }
    
    public static void setActiveUser(String username){
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Check if there is anything..
        appRms.openRMS();
        byte[] userSettingsByteArrayRMS = appRms.readBytes();
        Hashtable userSettingsRMS = SerializedHashtable.streamToHashtable
                (userSettingsByteArrayRMS);
        
        //Clear it, Close it
        appRms.deleteRMS();
        appRms.closeRMS();
        
        System.out.print("Size is: " + userSettingsRMS.size());
        
        if (userSettingsRMS.containsKey("CURRENTUSER") && 
                userSettingsRMS.get("CURRENTUSER").equals(username)){
            //already set
            int a=0;
        }else{
            userSettingsRMS.put("CURRENTUSER", username);
            userSettingsRMS.put(username+"-username", username);
            userSettingsRMS.put(username+"-password", "");
        }
        
        //Open it again
        appRms.openRMS();

        //Generate bytes
        byte[] newSettingsBytes = 
                SerializedHashtable.hashTabletoStream(userSettingsRMS);

        //Insert the data in.
        appRms.insertBytes(newSettingsBytes);
      
        
        //close the app RMS
        appRms.closeRMS();
        //Check if this username exists
        
    }
    
    public static String getPlatform(){
        return System.getProperty("microedition.platform");
    }
    
    public static String getLocale(){
        return System.getProperty("microedition.locale");
    }

}
