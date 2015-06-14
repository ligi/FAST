package org.ligi.fast.testing;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
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

        resolveInfo.activityInfo.applicationInfo= new ApplicationInfo();
    }

    @SmallTest
    public void testPackageNameShouldBeCorrectAfterConstructWithResolveInfo() throws Exception {

        AppInfo tested = new AppInfo(getActivity(), resolveInfo);

        assertThat(tested.getPackageName()).isEqualTo(resolveInfo.activityInfo.packageName);
    }

    @SmallTest
    public void testIntentReturnedShouldNotBeNull() throws Exception {

        AppInfo tested = new AppInfo(getActivity(), resolveInfo);

        assertThat(tested.getIntent()).isNotNull();
    }

    @SmallTest
    public void testLabelShouldBeSameAfterDeserialize() {

        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getLabel()).isEqualTo("labelTest");
    }


    @SmallTest
    public void testCallCountShouldBeSameAfterDeserialize() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getCallCount()).isEqualTo(42);
    }


    @SmallTest
    public void testPackageNameShouldBeSameAfterDeserialize() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getPackageName()).isEqualTo("packageNameTest");
    }


    @SmallTest
    public void testHashShoouldBeSameAfterDeserialize() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.getHash()).isEqualTo("hash");
    }

    @SmallTest
    public void testShouldBeValidAfterGoodInput() {
        AppInfo tested = new AppInfo(getActivity(), SERIALIZED_APPINFO);

        assertThat(tested.isValid()).isEqualTo(true);
    }


    @SmallTest
    public void testShouldNotBeValidAfterBadInput() {
        AppInfo tested = new AppInfo(getActivity(), "BAD");

        assertThat(tested.isValid()).isEqualTo(false);
    }

}
