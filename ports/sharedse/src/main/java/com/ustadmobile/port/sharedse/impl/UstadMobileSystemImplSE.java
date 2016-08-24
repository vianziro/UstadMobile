/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustadmobile.port.sharedse.impl;

import com.ustadmobile.core.MessageIDConstants;
import com.ustadmobile.core.controller.CatalogController;
import com.ustadmobile.core.impl.HTTPResult;
import com.ustadmobile.core.impl.UMLog;
import com.ustadmobile.core.impl.UMStorageDir;
import com.ustadmobile.core.impl.UstadMobileConstants;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.util.UMFileUtil;
import com.ustadmobile.core.util.UMIOUtils;
import com.ustadmobile.core.view.CatalogView;

import com.ustadmobile.core.impl.ZipFileHandle;
import com.ustadmobile.port.sharedse.impl.zip.*;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author mike
 */
public abstract class UstadMobileSystemImplSE extends UstadMobileSystemImpl {

    private XmlPullParserFactory xmlPullParserFactory;

    /**
     * @inheritDoc
     */
    @Override
    public HTTPResult makeRequest(String httpURL, Hashtable headers, Hashtable postParams, String method, byte[] postBody) throws IOException {
        Class cls2 = getClass();
        if(cls2.equals(CatalogView.class)) {
            System.out.println("well then");
        }
        
        URL url = new URL(httpURL);
        HttpURLConnection conn = null;
        HTTPResult result = null;
        IOException ioe = null;
        
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(15000);
            if(headers != null) {
                Enumeration e = headers.keys();
                while(e.hasMoreElements()) {
                    String headerField = e.nextElement().toString();
                    String headerValue = headers.get(headerField).toString();
                    conn.setRequestProperty(headerField, headerValue);
                }
            }
            //conn.setRequestProperty("Connection", "close");

            conn.setRequestMethod(method);

            if("POST".equals(method)) {
                if(postBody == null && postParams != null && postParams.size() > 0) {
                    //we need to write the post params to the request
                    StringBuilder sb = new StringBuilder();
                    Enumeration e = postParams.keys();
                    boolean firstParam = true;
                    while(e.hasMoreElements()) {
                        String key = e.nextElement().toString();
                        String value = postParams.get(key).toString();
                        if(firstParam) {
                            firstParam = false;
                        }else {
                            sb.append('&');
                        }
                        sb.append(URLEncoder.encode(key, "UTF-8")).append('=');
                        sb.append(URLEncoder.encode(value, "UTF-8"));
                    }

                    postBody = sb.toString().getBytes();
                }else if(postBody == null) {
                    throw new IllegalArgumentException("Cant make a post request with no body and no parameters");
                }

                conn.setDoOutput(true);
                OutputStream postOut = conn.getOutputStream();
                postOut.write(postBody);
                postOut.flush();
                postOut.close();
            }

            conn.connect();

            int statusCode = conn.getResponseCode();
            if(statusCode > 0) {
                InputStream connIn = statusCode < 400 ? conn.getInputStream() : conn.getErrorStream();
                byte[] buf = new byte[1024];
                int bytesRead = 0;

                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                if(!method.equalsIgnoreCase("HEAD")) {
                    while((bytesRead = connIn.read(buf)) != -1) {
                        bout.write(buf, 0, bytesRead);
                    }
                }

                connIn.close();

                Hashtable responseHeaders = new Hashtable();
                Iterator<String> headerIterator = conn.getHeaderFields().keySet().iterator();
                while(headerIterator.hasNext()) {
                    String header = headerIterator.next();
                    if(header == null) {
                        continue;//a null header is the response line not header; leave that alone...
                    }

                    String headerVal = conn.getHeaderField(header);
                    responseHeaders.put(header.toLowerCase(), headerVal);
                }

                byte[] resultBytes = bout.toByteArray();
                result = new HTTPResult(resultBytes, statusCode,
                        responseHeaders);
            }else {
                ioe = new IOException("HTTP Status < 0");
            }
        }finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        UMIOUtils.throwIfNotNullIO(ioe);
        return result;
    }

    @Override
    public boolean isJavascriptSupported() {
        return true;
    }


    @Override
    public boolean queueTinCanStatement(final JSONObject stmt, final Object context) {
        //Placeholder for iOS usage
        return false;
    }

    /**
     * Returns the system base directory to work from
     *
     * @return
     */
    protected abstract String getSystemBaseDir();

    @Override
    public String getCacheDir(int mode, Object context) {
        String systemBaseDir = getSystemBaseDir();
        if(mode == CatalogController.SHARED_RESOURCE) {
            return UMFileUtil.joinPaths(new String[]{systemBaseDir, UstadMobileConstants.CACHEDIR});
        }else {
            return UMFileUtil.joinPaths(new String[]{systemBaseDir, "user-" + getActiveUser(context),
                    UstadMobileConstants.CACHEDIR});
        }
    }

    @Override
    public UMStorageDir[] getStorageDirs(int mode, Object context) {
        List<UMStorageDir> dirList = new ArrayList<>();
        String systemBaseDir = getSystemBaseDir();

        if((mode & CatalogController.SHARED_RESOURCE) == CatalogController.SHARED_RESOURCE) {
            dirList.add(new UMStorageDir(systemBaseDir, getString(MessageIDConstants.device), false, true, false));

            //Find external directories
            String[] externalDirs = findRemovableStorage();
            for(String extDir : externalDirs) {
                dirList.add(new UMStorageDir(UMFileUtil.joinPaths(new String[]{extDir,
                        UstadMobileSystemImpl.CONTENT_DIR_NAME}), getString(MessageIDConstants.memory_card),
                        true, true, false, false));
            }
        }

        if((mode & CatalogController.USER_RESOURCE) == CatalogController.USER_RESOURCE) {
            String userBase = UMFileUtil.joinPaths(new String[]{systemBaseDir, "user-"
                    + getActiveUser(context)});
            dirList.add(new UMStorageDir(userBase, getString(MessageIDConstants.device), false, true, true));
        }




        UMStorageDir[] retVal = new UMStorageDir[dirList.size()];
        dirList.toArray(retVal);
        return retVal;
    }

    /**
     * Provides a list of paths to removable stoage (e.g. sd card) directories
     *
     * @return
     */
    public String[] findRemovableStorage() {
        return new String[0];
    }

    /**
     * Will return language_COUNTRY e.g. en_US
     *
     * @return
     */
    @Override
    public String getSystemLocale(Object context) {
        return Locale.getDefault().toString();
    }


    @Override
    public long fileLastModified(String fileURI) {
        return new File(fileURI).lastModified();
    }

    @Override
    public OutputStream openFileOutputStream(String fileURI, int flags) throws IOException {
        fileURI = UMFileUtil.stripPrefixIfPresent("file://", fileURI);
        return new FileOutputStream(fileURI, (flags & FILE_APPEND) == FILE_APPEND);
    }

    @Override
    public InputStream openFileInputStream(String fileURI) throws IOException {
        fileURI = UMFileUtil.stripPrefixIfPresent("file://", fileURI);
        return new FileInputStream(fileURI);
    }


    @Override
    public boolean fileExists(String fileURI) throws IOException {
        fileURI = UMFileUtil.stripPrefixIfPresent("file://", fileURI);
        return new File(fileURI).exists();
    }

    @Override
    public boolean dirExists(String dirURI) throws IOException {
        dirURI = UMFileUtil.stripPrefixIfPresent("file://", dirURI);
        File dir = new File(dirURI);
        return dir.exists() && dir.isDirectory();
    }

    @Override
    public boolean removeFile(String fileURI)  {
        fileURI = UMFileUtil.stripPrefixIfPresent("file://", fileURI);
        File f = new File(fileURI);
        return f.delete();
    }

    @Override
    public String[] listDirectory(String dirURI) throws IOException {
        dirURI = UMFileUtil.stripPrefixIfPresent("file://", dirURI);
        File dir = new File(dirURI);
        return dir.list();
    }


    @Override
    public boolean renameFile(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(path2);
        return file1.renameTo(file2);
    }

    @Override
    public long fileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    @Override
    public long fileAvailableSize(String fileURI) throws IOException {
        return new File(fileURI).getFreeSpace();
    }

    @Override
    public boolean makeDirectory(String dirPath) throws IOException {
        File newDir = new File(dirPath);
        return newDir.mkdir();
    }

    @Override
    public boolean makeDirectoryRecursive(String dirURI) throws IOException {
        return new File(dirURI).mkdirs();
    }

    @Override
    public boolean removeRecursively(String path) {
        return removeRecursively(new File(path));
    }

    public boolean removeRecursively(File f) {
        if(f.isDirectory()) {
            File[] dirContents = f.listFiles();
            for(int i = 0; i < dirContents.length; i++) {
                if(dirContents[i].isDirectory()) {
                    removeRecursively(dirContents[i]);
                }
                dirContents[i].delete();
            }
        }
        return f.delete();
    }

    public XmlPullParser newPullParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        return parser;
    }

    public XmlSerializer newXMLSerializer() {
        XmlSerializer serializer = null;
        try {
            if(xmlPullParserFactory == null) {
                xmlPullParserFactory = XmlPullParserFactory.newInstance();
            }

            serializer = xmlPullParserFactory.newSerializer();
        }catch(XmlPullParserException e) {
            l(UMLog.ERROR, 92, null, e);
        }

        return serializer;
    }

    /**
     * @inheritDoc
     */
    @Override
    public ZipFileHandle openZip(String name) throws IOException{
        return new ZipFileHandleSharedSE(name);
    }

}
