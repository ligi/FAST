package org.ligi.fast.model

import android.graphics.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TheIconScalingCalculator {

    private val POINT_96_96 = Point(96, 96)

    @Test
    fun testShouldNotScaleForEqualToSize() {

        val point = IconScalingCalculator.scaleDimensions(96, POINT_96_96)

        assertThat(point).isEqualTo(POINT_96_96)

    }

    @Test
    fun testShouldNotScaleSquareToMaxSquare() {

        val point = IconScalingCalculator.scaleDimensions(96, Point(128, 128))

        assertThat(point).isEqualTo(POINT_96_96)
    }

    @Test
    fun testShouldScaleWithCorrectAspectRatio() {

        val point = IconScalingCalculator.scaleDimensions(96, Point(128, 265))

        // aspect ratio should
        assertThat(point).isEqualTo(Point(46, 96))
    }
}
