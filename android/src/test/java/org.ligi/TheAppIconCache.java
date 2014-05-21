package org.ligi;

import android.graphics.Point;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.model.AppIconCache;
import org.ligi.fast.model.AppInfo;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;


import org.robolectric.annotation.Config;
@Config(emulateSdk = 18) // robolectric cannot deal with 19 and i do not want to targetSDK--
@RunWith(RobolectricTestRunner.class)
/*
 * test the AppIconCache
 *
 *  TODO either use BoundBox to access the then hidden scaling calc ( but BoundBox was not working with this gradle setup atm )
 *   or when Robolectric is able to scale images - use real images
 *
 */
public class TheAppIconCache extends AppInfoTestBase {

    private AppIconCache tested;
    private Point POINT_96_96 = new Point(96, 96);

    @Before
    public void setUp() {
        tested = new AppIconCache(Robolectric.application, new AppInfo(Robolectric.application, SERIALIZED_APPINFO));
    }


    @Test
    public void should_not_scale_for_equal_to_size() {

        Point point = tested.scaleToFitCalc(96, POINT_96_96);

        assertThat(point).isEqualTo(POINT_96_96);

    }

    @Test
    public void should_not_scale_quare_to_max_square() {

        Point point = tested.scaleToFitCalc(96, new Point(128, 128));

        assertThat(point).isEqualTo(POINT_96_96);
    }

    @Test
    public void should_scale_with_correct_aspect_ratio() {

        Point point = tested.scaleToFitCalc(96, new Point(128, 265));

        // aspect ratio should
        assertThat(point).isEqualTo(new Point(46, 96));
    }
}
