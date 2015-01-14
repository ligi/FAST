package org.ligi.fast.testing;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.DynamicAppInfoList;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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
    public void testThatCountIsReturnedCorrectly() {
        assertThat(tested.size()).isEqualTo(3);
    }

    @SmallTest
    public void testShoudQueryLabelCorrectly() {
        // invoke
        tested.setQuery("foo");

        // assert
        assertThat(tested.size()).isEqualTo(2);
    }

    @SmallTest
    public void testShouldNotSearchInPackageNameWhenDisabled() {
        // configure
        settings.searchPackage = false;

        // invoke
        tested.setQuery("packageName");

        // assert
        assertThat(tested.size()).isEqualTo(0);
    }


    @SmallTest
    public void testShouldSearchInPackageNameWhenEnabled() {
        // configure
        settings.searchPackage = true;

        // invoke
        tested.setQuery("packagename");

        // assert
        assertThat(tested.size()).isEqualTo(3);
    }

    @SmallTest
    public void testShouldIgnoreSpaceAfterQueryWhenThisSettingIsActive() {
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
    public void testShouldIgnoreCase() {
        // configure
        settings.ignoreSpace = true;

        // invoke
        tested.setQuery("TeStFoO");

        // assert
        assertThat(tested.size()).isEqualTo(2);
    }

    @SmallTest
    public void testShouldConvertQueryToLower() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("TestCase");

        // assert
        assertThat(tested.getCurrentQuery()).isEqualTo("testcase");
    }

    @SmallTest
    public void testShouldNotIgnoreSpaceAfterQueryWhenSettingInactive() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("testfoo ");

        // assert
        assertThat(tested.size()).isEqualTo(0);
    }

    @SmallTest
    public void testShouldRespectWhenWeGetNewList() {
        AppInfo appInfo4 = new AppInfo(getActivity(), SERIALIZED_APPINFO4);

        // invoke
        tested.update(asList(appInfo4));

        // assert
        assertThat(tested.get(0)).isEqualTo(appInfo4);
    }

    @SmallTest
    public void testShouldNotExplodeWhenIndexesOutOfBounds() {

        for (int i = 0; i < 5; i++) {
            assertThat(tested.get(i)).isNotNull();
        }
    }

    @SmallTest
    public void testShouldSortAlphabeticalIfRequested() throws Exception {
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
    public void testShouldMapWithGapSearch() {
        settings.gapSearch = true;

        tested.setQuery("lbar");

        assertThat(tested.size()).isEqualTo(1);
    }

    @SmallTest
    public void testShouldMatchWithGapSearchEvenThoughPackageSearchIsActive() {
        settings.gapSearch = true;
        settings.searchPackage = true;

        tested.setQuery("lbar");

        assertThat(tested.size()).isEqualTo(1);
    }

    @SmallTest
    public void testShouldMatchWithGapSearch() {
        settings.gapSearch = true;

        tested.setQuery("lte");

        assertThat(tested.size()).isEqualTo(3);
    }

    @SmallTest
    public void testShouldNotMatchWithGapSearch() {
        settings.gapSearch = true;

        tested.setQuery("stl");

        assertThat(tested.size()).isEqualTo(0);
    }

}
