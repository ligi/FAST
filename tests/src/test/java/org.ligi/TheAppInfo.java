package org.ligi;

import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.AppInfo;
import org.ligi.fast.SearchActivity;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.Setup;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class TheAppInfo {

    private ResolveInfo resolveInfo;
    private String SERIALIZED_APPINFO="hash;;labelTest;;packageNameTest;;activityNameTest;;42";

    @Before
    public void setUp() {
        resolveInfo = new ResolveInfo();

        resolveInfo.activityInfo=new ActivityInfo();
        resolveInfo.activityInfo.packageName="packagename";
        resolveInfo.activityInfo.name="activityname";
    }

    @org.junit.Test
	public void package_name_should_be_correct_after_construc_with_resolveinfo() throws Exception {

        AppInfo tested=new AppInfo(Robolectric.application,resolveInfo);

        assertThat(tested.getPackageName()).isEqualTo(resolveInfo.activityInfo.packageName);
    }

    @org.junit.Test
    public void intent_returned_should_not_be_null() throws Exception {

        AppInfo tested=new AppInfo(Robolectric.application,resolveInfo);

        assertThat(tested.getIntent()).isNotNull();
    }

    @Test
    public void label_should_be_same_after_deserialize() {

        AppInfo tested=new AppInfo(Robolectric.application,SERIALIZED_APPINFO);

        assertThat(tested.getLabel()).isEqualTo("labelTest");
    }


    @Test
    public void callcount_should_be_same_after_deserialize() {
        AppInfo tested=new AppInfo(Robolectric.application,SERIALIZED_APPINFO);

        assertThat(tested.getCallCount()).isEqualTo(42);
    }


    @Test
    public void packagename_should_be_same_after_deserialize() {
        AppInfo tested=new AppInfo(Robolectric.application,SERIALIZED_APPINFO);

        assertThat(tested.getPackageName()).isEqualTo("packageNameTest");
    }


    @Test
    public void hash_should_be_same_after_deserialize() {
        AppInfo tested=new AppInfo(Robolectric.application,SERIALIZED_APPINFO);

        assertThat(tested.getHash()).isEqualTo("hash");
    }

    @Test
    public void should_be_valid_after_good_input() {
        AppInfo tested=new AppInfo(Robolectric.application,SERIALIZED_APPINFO);

        assertThat(tested.isValid()).isEqualTo(true);
    }


    @Test
    public void should_be_valid_after_bad_input() {
        AppInfo tested=new AppInfo(Robolectric.application,"BAD");

        assertThat(tested.isValid()).isEqualTo(false);
    }

}