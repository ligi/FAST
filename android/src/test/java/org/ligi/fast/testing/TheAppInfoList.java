package org.ligi.fast.testing;

import org.junit.Before;
import org.junit.Test;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.DynamicAppInfoList;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class TheAppInfoList {

    protected String SERIALIZED_APPINFO = "hash1;;labelTest;;packageNameTest;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO1 = "hash2;;label1TestBarü;;packageNameTest1;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO2 = "hash3;;label2TestFoo;;packageNameTest2;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO3 = "hash4;;label3TestFoo;;packageNameTest3;;activityNameTest;;42";
    protected String SERIALIZED_APPINFO4 = "hash5;;label4TestFoo;;packageNameTest4;;activityNameTest;;42";


    private DynamicAppInfoList tested;
    private MutableFastSettings settings;

    @Before
    public void setUp() {
        AppInfo appInfo1 = new AppInfo(null, SERIALIZED_APPINFO1);
        AppInfo appInfo2 = new AppInfo(null, SERIALIZED_APPINFO2);
        AppInfo appInfo3 = new AppInfo(null, SERIALIZED_APPINFO3);
        settings = new MutableFastSettings();
        tested = new DynamicAppInfoList(asList(appInfo1, appInfo2, appInfo3), settings);
    }

    @Test
    public void testThatCountIsReturnedCorrectly() {
        assertThat(tested.size()).isEqualTo(3);
    }

    @Test
    public void testShoudQueryLabelCorrectly() {
        // invoke
        tested.setQuery("foo");

        // assert
        assertThat(tested.size()).isEqualTo(2);
    }

    @Test
    public void testShouldNotSearchInPackageNameWhenDisabled() {
        // configure
        settings.searchPackage = false;

        // invoke
        tested.setQuery("packageName");

        // assert
        assertThat(tested.size()).isEqualTo(0);
    }


    @Test
    public void testShouldSearchInPackageNameWhenEnabled() {
        // configure
        settings.searchPackage = true;

        // invoke
        tested.setQuery("packagename");

        // assert
        assertThat(tested.size()).isEqualTo(3);
    }

    @Test
    public void testShouldIgnoreSpaceAfterQueryWhenThisSettingIsActive() {
        // configure
        settings.ignoreSpace = true;

        // invoke
        tested.setQuery("testfoo ");

        // assert
        assertThat(tested.size()).isEqualTo(2);
    }


    @Test
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

    @Test
    public void testShouldConvertQueryToLower() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("TestCase");

        // assert
        assertThat(tested.getCurrentQuery()).isEqualTo("testcase");
    }

    @Test
    public void testShouldNotIgnoreSpaceAfterQueryWhenSettingInactive() {
        // configure
        settings.ignoreSpace = false;

        // invoke
        tested.setQuery("testfoo ");

        // assert
        assertThat(tested.size()).isEqualTo(0);
    }

    @Test
    public void testShouldRespectWhenWeGetNewList() {
        AppInfo appInfo4 = new AppInfo(null, SERIALIZED_APPINFO4);

        // invoke
        tested.update(asList(appInfo4));

        // assert
        assertThat(tested.get(0)).isEqualTo(appInfo4);
    }

    @Test
    public void testShouldNotExplodeWhenIndexesOutOfBounds() {

        for (int i = 0; i < 5; i++) {
            assertThat(tested.get(i)).isNotNull();
        }
    }

    @Test
    public void testShouldSortAlphabeticalIfRequested() throws Exception {
        AppInfo appInfo1 = new AppInfo(null, "hash1;;clabel;;packageNameTest;;activityNameTest;;42");
        AppInfo appInfo2 = new AppInfo(null, "hash2;;aabel1TestBarü;;packageNameTest1;;activityNameTest;;42");
        AppInfo appInfo3 = new AppInfo(null, "hash3;;Fabel2TestFoo;;packageNameTest2;;activityNameTest;;42");
        AppInfo appInfo4 = new AppInfo(null, "hash4;;eabel3TestFoo;;packageNameTest3;;activityNameTest;;42");
        AppInfo appInfo5 = new AppInfo(null, "hash5;;Dbel4TestFoo;;packageNameTest4;;activityNameTest;;42");
        tested = new DynamicAppInfoList(asList(appInfo1, appInfo2, appInfo3, appInfo4, appInfo5), settings);

        tested.setSortMode(DynamicAppInfoList.SortMode.ALPHABETICAL);

        assertThat(tested).containsExactly(appInfo2,appInfo1,appInfo5,appInfo4,appInfo3);
    }

    @Test
    public void testShouldMapWithGapSearch() {
        settings.gapSearch = true;

        tested.setQuery("lbar");

        assertThat(tested.size()).isEqualTo(1);
    }

    @Test
    public void testShouldMatchWithGapSearchEvenThoughPackageSearchIsActive() {
        settings.gapSearch = true;
        settings.searchPackage = true;

        tested.setQuery("lbar");

        assertThat(tested.size()).isEqualTo(1);
    }

    @Test
    public void testShouldMatchWithGapSearch() {
        settings.gapSearch = true;

        tested.setQuery("lte");

        assertThat(tested.size()).isEqualTo(3);
    }

    @Test
    public void testShouldNotMatchWithGapSearch() {
        settings.gapSearch = true;

        tested.setQuery("stl");

        assertThat(tested.size()).isEqualTo(0);
    }

}
