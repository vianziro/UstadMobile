package com.ustadmobile.test.core.impl;


import android.support.test.InstrumentationRegistry;

import com.ustadmobile.test.sharedse.impl.TestContext;


/**
 * TestUtil is designed to abstract away the differences between conducting testing on "smart"
 * devices where we can run NanoHTTPD and J2ME where we need to use an external server.
 */

public class PlatformTestUtil {


    static TestContext testContext = new TestContext();

    public static Object getTargetContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    public static Object getTestContext() {
        return InstrumentationRegistry.getContext();
    }

}