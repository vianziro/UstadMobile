/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustadmobile.port.j2me.util;

import com.ustadmobile.core.impl.UMLog;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.util.UMIOUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connection;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author mike
 */
public class J2MEIOUtils {
    
    public static final void closeConnection(Connection con) {
        if(con != null) {
            try {
                con.close();
            }catch(IOException e) {
                UstadMobileSystemImpl.getInstance().getLogger().l(UMLog.INFO, 101, 
                    null, e);
            }
        }
    }
    
    public static final void closeRecordStore(RecordStore r) {
        if(r != null) {
            try { 
                r.closeRecordStore();
            }catch(RecordStoreException re) {
                UstadMobileSystemImpl.l(UMLog.ERROR, 169, null, re);
            }
        }
    }
    
    public static ByteArrayInputStream readToByteArrayStream(InputStream in) throws IOException{
        return readToByteArrayStream(in, 1024);
    }
    
    public static ByteArrayInputStream readToByteArrayStream(InputStream in, int bufsize) throws IOException{
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[bufsize];
        int bytesRead;
        IOException ioe = null;
        try {
            while((bytesRead = in.read(buf)) != -1) {
                bout.write(buf, 0, bytesRead);
            }
        }catch(IOException e) {
            ioe = e;
        }finally {
            UMIOUtils.closeInputStream(in);
            UMIOUtils.throwIfNotNullIO(ioe);
        }
        
        return new ByteArrayInputStream(bout.toByteArray());
    }
    
}
