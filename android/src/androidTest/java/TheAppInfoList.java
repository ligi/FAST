import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.DynamicAppInfoList;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

public class TheAppInfoList extends AppInfoTestBase {

    private DynamicAppInfoList tested;
    private MutableFastSettings settings;

    @Override
    public void setUp() {
        AppInfo appInfo1 = new AppInfo(getActivity(), SERIALIZED_APPINFO1);
        AppInfo appInfo2 = new AppInfo(getActivity(), SERIALIZED_APPINFO2);
        AppInfo appInfo3 = new AppInfo(getActivity(), SERIALIZED_APPINFO3);
        settings = new MutableFastSettings();
        tested = new DynamicAppInfoList(asList(appInfo1, appInfo2, appInfo3), settings);
    }

    @SmallTest
    public void count_is_returned_correctly() {
        assertThat(tested.size()).isEqualTo(3);
    }

    @SmallTest
    public void should_query_label_correctly() {
        // invoke
        tested.setQuery("foo");

        // assert
        assertThat(tested.size()).isEqualTo(2);
    }

    @SmallTest
    public void should_not_search_in_package_name_when_disabled() {
        // configure
        settings.searchPackage = false;

        // invoke
        tested.setQuery("packageName");

        // assert
        assertThat(tested.size()).isEqualTo(0);
    }


    @SmallTest
    public void should_search_in_package_name_when_enabled() {
        // configure
        settings.searchPackage = true;

        // invoke
        tested.setQuery("packagename");

        // assert
        assertThat(tested.size()).isEqualTo(3);
    }

    @SmallTest
    public void should_ignore_space_after_query_when_active() {
        // configure
        settings.ignoreSpace = true;

        // invoke
        tested.setQuery("testfoo ");

        // assert
        assertThat(tested.size()).isEqualTo(2);
    }


    @SmallTest
    /**
     e.g. important because umlauts are only converted lower case
     */
    public void should_ignore_case() {
        // configure
        settings.ignoreSpace = true;

        // invoke
        tested.setQuery("TeStFoO");

        // assert
        assertThat(tested.size()).isEqualTo(2);
    }

    @SmallTest
    public void should_convert_query_to_lower() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("TestCase");

        // assert
        assertThat(tested.getCurrentQuery()).isEqualTo("testcase");
    }

    @SmallTest
    public void should_not_ignore_space_after_query_when_inactive() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("testfoo ");

        // assert
        assertThat(tested.size()).isEqualTo(0);
    }

    @SmallTest
    public void should_respect_when_we_set_a_new_list() {
        AppInfo appInfo4 = new AppInfo(getActivity(), SERIALIZED_APPINFO4);

        // invoke
        tested.update(asList(appInfo4));

        // assert
        assertThat(tested.get(0)).isEqualTo(appInfo4);
    }

    @SmallTest
    public void should_not_explode_for_indexes_out_of_bounds() {

        for (int i = 0; i < 5; i++) {
            assertThat(tested.get(i)).isNotNull();
        }
    }

    @SmallTest
    public void should_sort_alphabetical_if_requested() throws Exception {
        AppInfo appInfo1 = new AppInfo(getActivity(), "hash;;clabel;;packageNameTest;;activityNameTest;;42");
        AppInfo appInfo2 = new AppInfo(getActivity(), "hash;;aabel1TestBarü;;packageNameTest1;;activityNameTest;;42");
        AppInfo appInfo3 = new AppInfo(getActivity(), "hash;;Fabel2TestFoo;;packageNameTest2;;activityNameTest;;42");
        AppInfo appInfo4 = new AppInfo(getActivity(), "hash;;eabel3TestFoo;;packageNameTest3;;activityNameTest;;42");
        AppInfo appInfo5 = new AppInfo(getActivity(), "hash;;Dbel4TestFoo;;packageNameTest4;;activityNameTest;;42");
        tested = new DynamicAppInfoList(asList(appInfo1, appInfo2, appInfo3, appInfo4, appInfo5), settings);

        tested.setSortMode(DynamicAppInfoList.SortMode.ALPHABETICAL);

        assertThat(tested.get(0)).isEqualTo(appInfo2);
        assertThat(tested.get(1)).isEqualTo(appInfo1);
        assertThat(tested.get(2)).isEqualTo(appInfo5);
        assertThat(tested.get(3)).isEqualTo(appInfo4);
        assertThat(tested.get(4)).isEqualTo(appInfo3);

    }

    @SmallTest
    public void should_match_with_gap_search() {
        settings.gapSearch = true;

        tested.setQuery("lbar");

        assertThat(tested.size()).isEqualTo(1);
    }

    @SmallTest
    public void should_match_with_gap_search_even_though_package_search_is_active() {
        settings.gapSearch = true;
        settings.searchPackage = true;

        tested.setQuery("lbar");

        assertThat(tested.size()).isEqualTo(1);
    }

    @SmallTest
    public void should_match_all_with_gap_search() {
        settings.gapSearch = true;

        tested.setQuery("lte");

        assertThat(tested.size()).isEqualTo(3);
    }

    @SmallTest
    public void should_not_match_with_gap_search() {
        settings.gapSearch = true;

        tested.setQuery("stl");

        assertThat(tested.size()).isEqualTo(0);
    }

}
