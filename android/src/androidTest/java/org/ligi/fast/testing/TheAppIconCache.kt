package org.ligi.fast.testing

import android.graphics.Point
import android.support.test.InstrumentationRegistry.getTargetContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.ligi.fast.model.AppIconCache
import org.ligi.fast.model.AppInfo

/*
 * test the AppIconCache
 *
 *  TODO either use BoundBox to access the then hidden scaling calc ( but BoundBox was not working with this gradle setup atm )
 *   or when Robolectric is able to scale images - use real images
 *
 */
class TheAppIconCache {

    private val tested by lazy { AppIconCache(getTargetContext(), AppInfo(getTargetContext(), SERIALIZED_APPINFO)) }
    private val POINT_96_96 = Point(96, 96)

    @Test
    fun testShouldNotScaleForEqualToSize() {

        val point = tested.scaleToFitCalc(96, POINT_96_96)

        assertThat(point).isEqualTo(POINT_96_96)

    }

    @Test
    fun testShouldNotScaleSquareToMaxSquare() {

        val point = tested.scaleToFitCalc(96, Point(128, 128))

        assertThat(point).isEqualTo(POINT_96_96)
    }

    @Test
    fun testShouldScaleWithCorrectAspectRatio() {

        val point = tested.scaleToFitCalc(96, Point(128, 265))

        // aspect ratio should
        assertThat(point).isEqualTo(Point(46, 96))
    }
}
