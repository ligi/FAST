package org.ligi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.AppInfo;
import org.ligi.fast.AppInfoList;
import org.ligi.fast.FASTSettings;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheAppInfoList {

    private String SERIALIZED_APPINFO1="hash;;label1TestBarü;;packageNameTest1;;activityNameTest;;42";
    private String SERIALIZED_APPINFO2="hash;;label2TestFoo;;packageNameTest2;;activityNameTest;;42";
    private String SERIALIZED_APPINFO3="hash;;label3TestFoo;;packageNameTest3;;activityNameTest;;42";
    private AppInfoList tested;
    private MutableFastSettings settings;

    @Before
    public void setUp() {
        AppInfo appInfo1=new AppInfo(Robolectric.application,SERIALIZED_APPINFO1);
        AppInfo appInfo2=new AppInfo(Robolectric.application,SERIALIZED_APPINFO2);
        AppInfo appInfo3=new AppInfo(Robolectric.application,SERIALIZED_APPINFO3);
        settings = new MutableFastSettings();
        tested = new AppInfoList(asList(appInfo1,appInfo2,appInfo3), settings);
    }

    @Test
    public void count_is_returned_correctly() {
        assertThat(tested.getCount()).isEqualTo(3);
    }

    @Test
    public void should_query_label_correctly() {
        // invoke
        tested.setQuery("foo");

        // post assert
        assertThat(tested.getCount()).isEqualTo(2);
    }

    @Test
    public void should_not_search_in_package_name_when_disabled() {
        // configure
        settings.searchPackage=false;

        // invoke
        tested.setQuery("packageName");

        // post assert
        assertThat(tested.getCount()).isEqualTo(0);
    }


    @Test
    public void should_search_in_package_name_when_enabled() {
        // configure
        settings.searchPackage=true;

        // invoke
        tested.setQuery("packagename");

        // post assert
        assertThat(tested.getCount()).isEqualTo(3);
    }

    @Test
    public void should_ignore_space_after_query_when_active() {
        // configure
        settings.ignoreSpace=true;

        // invoke
        tested.setQuery("testfoo ");

        // post assert
        assertThat(tested.getCount()).isEqualTo(2);
    }

    @Test
    public void should_not_ignore_space_after_query_when_inactive() {
        // configure
        settings.ignoreSpace=false;

        // invoke
        tested.setQuery("testfoo ");

        // post assert
        assertThat(tested.getCount()).isEqualTo(0);
    }

}