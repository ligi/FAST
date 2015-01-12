package org.ligi.fast.testing;

import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.fast.model.AppInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class TheAppInfo extends AppInfoTestBase {

    private ResolveInfo resolveInfo;

    @Override
    public void setUp() {
        resolveInfo = new ResolveInfo();

        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName = "packagename";
        resolveInfo.activityInfo.name = "activityname";
    }

    @SmallTest
    public void package_name_should_be_correct_after_construct_with_resolveinfo() throws Exception {

        AppInfo tested = new AppInfo(getActivity(), resolveInfo);

        assertThat(tested.getPackageName()).isEqualTo(resolveInfo.activityInfo.packageName);

    }


    @SmallTest
    public void intent_returned_should_not_be_null() throws Exception {

        AppInfo tested = new AppInfo(getActivity(), resolveInfo);

        assertThat(tested.getIntent()).isNotNull();
    }

    @SmallTest
    public void label_should_be_same_after_deserialize() {

        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getLabel()).isEqualTo("labelTest");
    }


    @SmallTest
    public void callcount_should_be_same_after_deserialize() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getCallCount()).isEqualTo(42);
    }


    @SmallTest
    public void packagename_should_be_same_after_deserialize() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getPackageName()).isEqualTo("packageNameTest");
    }


    @SmallTest
    public void hash_should_be_same_after_deserialize() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getHash()).isEqualTo("hash");
    }

    @SmallTest
    public void should_be_valid_after_good_input() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.isValid()).isEqualTo(true);
    }


    @SmallTest
    public void should_be_valid_after_bad_input() {
        AppInfo tested = new AppInfo(getActivity(), "BAD");

        assertThat(tested.isValid()).isEqualTo(false);
    }

}
