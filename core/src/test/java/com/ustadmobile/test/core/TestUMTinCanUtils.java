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
package com.ustadmobile.test.core;

import com.ustadmobile.test.core.buildconfig.TestConstants;
import com.ustadmobile.core.util.TestUtils;
import com.ustadmobile.core.util.UMTinCanUtil;


/* $if umplatform == 2  $
    import com.ustadmobile.test.port.j2me.TestCase;
    import org.json.me.*;
 $else$ */
    import junit.framework.TestCase;
    import org.json.*;
/* $endif$ */


/**
 *
 * @author mike
 */
public class TestUMTinCanUtils extends TestCase{
    
    
    public void testUMTinCanUtils() throws Exception{
        TestUtils utils = new TestUtils();
        JSONObject testActor = UMTinCanUtil.makeActorFromUserAccount(
            utils.getTestProperty(TestUtils.PROP_TESTUSER), TestConstants.XAPI_SERVER);
        JSONObject testAcct =testActor.getJSONObject("account");
        String testAcctStr = testAcct.toString();
        assertEquals("Correctly set name on account", testAcct.getString("name"), 
                utils.getTestProperty(TestUtils.PROP_TESTUSER));
        assertEquals("Correctly set homepage", testAcct.getString("homePage"),
            TestConstants.XAPI_SERVER);
        
        long twoMinThirtySecInMs = (60+60+30) * 1000;
        
        
        String tcParent = "tcparent";
        String pageID = "somepage";
        String pageTitle = "Page Title";
        
        JSONObject pageStmt = UMTinCanUtil.makePageViewStmt(tcParent+ "/" + pageID, 
            pageTitle, "en-US", twoMinThirtySecInMs, testActor);
        String pageStmtSTr = pageStmt.toString();
        
        String durationStr = pageStmt.getJSONObject("result").getString("duration");
        
        assertEquals("Correctly format 8601 duration", "PT0H2M30S",
            durationStr);
        
        String objectID = pageStmt.getJSONObject("object").getString("id");
        assertEquals("Statement has expected id", tcParent + '/' + pageID,
            objectID);
    }
    
    public void runTest() throws Exception{
        this.testUMTinCanUtils();
    }
    
    
}
