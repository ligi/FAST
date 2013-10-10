package org.ligi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.AppInfo;
import org.ligi.fast.AppInfoList;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheAppInfoList extends BaseAppInfoTest {

    private AppInfoList tested;
    private MutableFastSettings settings;

    @Before
    public void setUp() {
        AppInfo appInfo1 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO1);
        AppInfo appInfo2 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO2);
        AppInfo appInfo3 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO3);
        settings = new MutableFastSettings();
        tested = new AppInfoList(asList(appInfo1, appInfo2, appInfo3), settings);
    }

    @Test
    public void count_is_returned_correctly() {
        assertThat(tested.getCount()).isEqualTo(3);
    }

    @Test
    public void should_query_label_correctly() {
        // invoke
        tested.setQuery("foo");

        // assert
        assertThat(tested.getCount()).isEqualTo(2);
    }

    @Test
    public void should_not_search_in_package_name_when_disabled() {
        // configure
        settings.searchPackage = false;

        // invoke
        tested.setQuery("packageName");

        // assert
        assertThat(tested.getCount()).isEqualTo(0);
    }


    @Test
    public void should_search_in_package_name_when_enabled() {
        // configure
        settings.searchPackage = true;

        // invoke
        tested.setQuery("packagename");

        // assert
        assertThat(tested.getCount()).isEqualTo(3);
    }

    @Test
    public void should_ignore_space_after_query_when_active() {
        // configure
        settings.ignoreSpace = true;

        // invoke
        tested.setQuery("testfoo ");

        // assert
        assertThat(tested.getCount()).isEqualTo(2);
    }

    @Test
    public void should_not_ignore_space_after_query_when_inactive() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("testfoo ");

        // assert
        assertThat(tested.getCount()).isEqualTo(0);
    }

    @Test
    public void should_respect_when_we_set_a_new_list() {
        AppInfo appInfo4 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO4);

        // invoke
        tested.setAppsList(asList(appInfo4));

        // assert
        assertThat(tested.get(0)).isEqualTo(appInfo4);
    }

    @Test
    public void should_not_explode_for_indexes_out_of_bounds() {

        for (int i = 0; i < 5; i++) {
            assertThat(tested.get(i)).isNotNull();
        }
    }

}