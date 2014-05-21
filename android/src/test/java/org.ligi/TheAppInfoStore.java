package org.ligi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.util.AppInfoListStore;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

import static org.fest.assertions.api.Assertions.assertThat;

import org.robolectric.annotation.Config;
@Config(emulateSdk = 18) // robolectric cannot deal with 19 and i do not want to targetSDK--
@RunWith(RobolectricTestRunner.class)
public class TheAppInfoStore extends AppInfoTestBase {

    private AppInfoListStore tested;
    private List<AppInfo> appInfoList;

    @Before
    public void setUp() {

        AppInfo appInfo1 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO1);
        AppInfo appInfo2 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO2);
        AppInfo appInfo3 = new AppInfo(Robolectric.application, SERIALIZED_APPINFO3);

        tested = new AppInfoListStore(Robolectric.application);

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
