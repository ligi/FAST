package org.ligi.fast.testing

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.support.test.InstrumentationRegistry.getTargetContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.ligi.fast.model.AppInfo

class TheAppInfo {

    private var resolveInfo = ResolveInfo()

    @Before
    fun setUp() {

        resolveInfo!!.activityInfo = ActivityInfo()
        resolveInfo!!.activityInfo.packageName = "packagename"
        resolveInfo!!.activityInfo.name = "activityname"

        resolveInfo!!.activityInfo.applicationInfo = ApplicationInfo()
    }

    @Test
    fun testPackageNameShouldBeCorrectAfterConstructWithResolveInfo() {

        val tested = AppInfo(getTargetContext(), resolveInfo)

        assertThat(tested.packageName).isEqualTo(resolveInfo!!.activityInfo.packageName)
    }

    @Test
    fun testIntentReturnedShouldNotBeNull() {

        val tested = AppInfo(getTargetContext(), resolveInfo)

        assertThat(tested.intent).isNotNull()
    }

    @Test
    fun testLabelShouldBeSameAfterDeserialize() {

        val tested = AppInfo(getTargetContext(), SERIALIZED_APPINFO)

        assertThat(tested.label).isEqualTo("labelTest")
    }


    @Test
    fun testCallCountShouldBeSameAfterDeserialize() {
        val tested = AppInfo(getTargetContext(), SERIALIZED_APPINFO)

        assertThat(tested.callCount).isEqualTo(42)
    }


    @Test
    fun testPackageNameShouldBeSameAfterDeserialize() {
        val tested = AppInfo(getTargetContext(), SERIALIZED_APPINFO)

        assertThat(tested.packageName).isEqualTo("packageNameTest")
    }


    @Test
    fun testHashShoouldBeSameAfterDeserialize() {
        val tested = AppInfo(getTargetContext(), SERIALIZED_APPINFO)

        assertThat(tested.hash).isEqualTo("hash")
    }

    @Test
    fun testShouldBeValidAfterGoodInput() {
        val tested = AppInfo(getTargetContext(), SERIALIZED_APPINFO)

        assertThat(tested.isValid).isEqualTo(true)
    }


    @Test
    fun testShouldNotBeValidAfterBadInput() {
        val tested = AppInfo(getTargetContext(), "BAD")

        assertThat(tested.isValid).isEqualTo(false)
    }

}
