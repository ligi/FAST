package org.ligi.fast.testing;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.fast.model.AppInfo;
import org.ligi.fast.util.AppInfoListStore;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class TheAppInfoStore extends AppInfoTestBase {

    private AppInfoListStore tested;
    private List<AppInfo> appInfoList;

    @Override
    public void setUp() {

        AppInfo appInfo1 = new AppInfo(getActivity(), SERIALIZED_APPINFO1);
        AppInfo appInfo2 = new AppInfo(getActivity(), SERIALIZED_APPINFO2);
        AppInfo appInfo3 = new AppInfo(getActivity(), SERIALIZED_APPINFO3);

        tested = new AppInfoListStore(getActivity());

        appInfoList = asList(appInfo1, appInfo2, appInfo3);

    }

    @SmallTest
    public void testWhatGoesInShouldComeOut() {

        tested.save(appInfoList);

        assertThat(tested.load()).isEqualTo(appInfoList);
    }

    @SmallTest
    public void testSaveTwiceShouldNotExplode() {

        tested.save(appInfoList);
        tested.save(appInfoList);

        assertThat(tested.load()).isEqualTo(appInfoList);
    }


    @SmallTest
    public void testEmptyListsShouldWork() {

        tested.save(Collections.EMPTY_LIST);

        assertThat(tested.load()).isEqualTo(Collections.EMPTY_LIST);
    }
}
