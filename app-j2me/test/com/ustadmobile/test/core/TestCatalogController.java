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


import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import com.ustadmobile.core.controller.CatalogController;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.opds.UstadJSOPDSFeed;
import java.io.ByteArrayInputStream;
import org.xmlpull.v1.XmlPullParser;


/* $if umplatform == 1 $
        import android.test.ActivityInstrumentationTestCase2;
        import com.toughra.ustadmobile.UstadMobileActivity;
   $endif$ */

/* $if umplatform == 2  $ */
    import org.j2meunit.framework.TestCase;
 /* $else$
    import junit.framework.TestCase;
$endif$ */

/**
 *
 * @author mike
 */

/* $if umplatform == 1  $
public class TestCatalogController extends ActivityInstrumentationTestCase2<UstadMobileActivity>{
 $else$ */
public class TestCatalogController extends TestCase{
/* $endif  */
    
    public TestCatalogController() {
        /* $if umplatform == 1 $
        super("com.toughra.ustadmobile", UstadMobileActivity.class);
        $endif  */
    }
    
    public void testCatalogController() throws IOException, XmlPullParserException {
        UstadMobileSystemImpl impl = UstadMobileSystemImpl.getInstance();
        impl.setActiveUser(TestConstants.LOGIN_USER);
        
        String opdsURL = TestUtils.getInstance().getHTTPRoot() + TestConstants.CATALOG_OPDS_ROOT;
        
        CatalogController controller = CatalogController.makeControllerByURL(
            opdsURL, impl, CatalogController.USER_RESOURCE, 
            TestConstants.LOGIN_USER, TestConstants.LOGIN_PASS, 
            CatalogController.CACHE_ENABLED);
        assertNotNull("Create catalog controller", controller);
        
        
        UstadJSOPDSFeed feedItem = controller.getModel().opdsFeed;
        String feedXML = feedItem.toString();
        ByteArrayInputStream bin = new ByteArrayInputStream(
            feedXML.getBytes("UTF-8"));
        XmlPullParser parser = impl.newPullParser();
        parser.setInput(bin, "UTF-8");
        UstadJSOPDSFeed fromXMLItem = UstadJSOPDSFeed.loadFromXML(parser);
        assertEquals("Same id when reparsed", feedItem.id, fromXMLItem.id);
        CatalogController.cacheCatalog(feedItem, CatalogController.USER_RESOURCE, null);
        UstadJSOPDSFeed cachedFeed = 
            CatalogController.getCachedCatalogByID(feedItem.id, 
            CatalogController.SHARED_RESOURCE | CatalogController.USER_RESOURCE);
        
        assertEquals("Same feed id on cached catalog", feedItem.id, cachedFeed.id);
    }
    
}
