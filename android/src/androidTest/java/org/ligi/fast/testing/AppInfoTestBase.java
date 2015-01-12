package org.ligi.fast.testing;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import org.ligi.fast.ui.SearchActivity;

public  class AppInfoTestBase extends ActivityInstrumentationTestCase2<SearchActivity>{
    protected String SERIALIZED_APPINFO = "hash;;labelTest;;packageNameTest;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO1 = "hash;;label1TestBarü;;packageNameTest1;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO2 = "hash;;label2TestFoo;;packageNameTest2;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO3 = "hash;;label3TestFoo;;packageNameTest3;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO4 = "hash;;label4TestFoo;;packageNameTest4;;activityNameTest;;42";

    public AppInfoTestBase() {
        super(SearchActivity.class);
    }
}
