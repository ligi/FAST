package org.ligi.fast.testing

import android.support.test.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.fast.model.AppInfo
import org.ligi.fast.util.AppInfoListStore
import java.util.*
import java.util.Arrays.asList

class TheAppInfoStore {

    private val tested: AppInfoListStore by lazy { AppInfoListStore(InstrumentationRegistry.getTargetContext()) }

    val appInfo1 by lazy {  AppInfo(InstrumentationRegistry.getTargetContext(), SERIALIZED_APPINFO1) }
    val appInfo2  by lazy {  AppInfo(InstrumentationRegistry.getTargetContext(), SERIALIZED_APPINFO2) }
    val appInfo3  by lazy {  AppInfo(InstrumentationRegistry.getTargetContext(), SERIALIZED_APPINFO3)}

    val appInfoList by lazy { asList(appInfo1, appInfo2, appInfo3) }

    @Test
    fun testWhatGoesInShouldComeOut() {

        tested.save(appInfoList)

        assertThat(tested.load()).isEqualTo(appInfoList)
    }

    @Test
    fun testSaveTwiceShouldNotExplode() {

        tested.save(appInfoList)
        tested.save(appInfoList)

        assertThat(tested.load()).isEqualTo(appInfoList)
    }


    @Test
    fun testEmptyListsShouldWork() {

        tested.save(emptyList())

        assertThat(tested.load()).isEqualTo(Collections.EMPTY_LIST)
    }
}
