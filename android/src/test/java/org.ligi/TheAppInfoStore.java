package org.ligi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.AppInfo;
import org.ligi.fast.util.PackageListStore;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.fail;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheAppInfoStore extends AppInfoTestBase {

    private PackageListStore tested;
    private List<AppInfo> appInfoList;

    @Before
    public void setUp() {

        AppInfo appInfo1 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO1);
        AppInfo appInfo2 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO2);
        AppInfo appInfo3 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO3);

        tested = new PackageListStore(Robolectric.application);

        appInfoList = asList(appInfo1, appInfo2, appInfo3);

    }

    @Test
    public void what_goes_in_should_come_out() {

        tested.save(appInfoList);

        assertThat(tested.load()).isEqualTo(appInfoList);
    }

    @Test
    public void save_twice_should_not_blow_up_things() {

        tested.save(appInfoList);
        tested.save(appInfoList);

        assertThat(tested.load()).isEqualTo(appInfoList);
    }


    @Test
    public void empty_lists_should_work() {

        tested.save(Collections.EMPTY_LIST);

        assertThat(tested.load()).isEqualTo(Collections.EMPTY_LIST);
    }
}