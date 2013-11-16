package org.ligi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheAppInfoList extends AppInfoTestBase {

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
    /**
     e.g. important because umlauts are only converted lower case
     */
    public void should_ignore_case() {
        // configure
        settings.ignoreSpace = true;

        // invoke
        tested.setQuery("TeStFoO");

        // assert
        assertThat(tested.getCount()).isEqualTo(2);
    }

    @Test
    public void should_convert_query_to_lower() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("TestCase");

        // assert
        assertThat(tested.getCurrentQuery()).isEqualTo("testcase");
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

    @Test
    public void should_sort_alphabetical_if_requested() throws Exception {
        AppInfo appInfo1 = new AppInfo(Robolectric.application, "hash;;clabel;;packageNameTest;;activityNameTest;;42");
        AppInfo appInfo2 = new AppInfo(Robolectric.application, "hash;;aabel1TestBarü;;packageNameTest1;;activityNameTest;;42");
        AppInfo appInfo3 = new AppInfo(Robolectric.application, "hash;;Fabel2TestFoo;;packageNameTest2;;activityNameTest;;42");
        AppInfo appInfo4 = new AppInfo(Robolectric.application, "hash;;eabel3TestFoo;;packageNameTest3;;activityNameTest;;42");
        AppInfo appInfo5 = new AppInfo(Robolectric.application, "hash;;Dbel4TestFoo;;packageNameTest4;;activityNameTest;;42");
        tested = new AppInfoList(asList(appInfo1, appInfo2, appInfo3, appInfo4, appInfo5), settings);

        tested.setSortMode(AppInfoList.SortMode.ALPHABETICAL);

        assertThat(tested.get(0)).isEqualTo(appInfo2);
        assertThat(tested.get(1)).isEqualTo(appInfo1);
        assertThat(tested.get(2)).isEqualTo(appInfo5);
        assertThat(tested.get(3)).isEqualTo(appInfo4);
        assertThat(tested.get(4)).isEqualTo(appInfo3);

    }

    @Test
    public void should_match_with_fuzzy_search() {
        settings.fuzzySearch = true;

        tested.setQuery("lbar");

        assertThat(tested.getCount()).isEqualTo(1);
    }

    @Test
    public void should_match_all_with_fuzzy_search() {
        settings.fuzzySearch = true;

        tested.setQuery("lte");

        assertThat(tested.getCount()).isEqualTo(3);
    }

    @Test
    public void should_not_match_with_fuzzy_search() {
        settings.fuzzySearch = true;

        tested.setQuery("stl");

        assertThat(tested.getCount()).isEqualTo(0);
    }

}