package org.ligi.fast.testing;

import android.graphics.Point;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.fast.model.AppIconCache;
import org.ligi.fast.model.AppInfo;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Override
    public void setUp() throws Exception {
        tested = new AppIconCache(getActivity(), new AppInfo(getActivity(), SERIALIZED_APPINFO));
    }


    @SmallTest
    public void testShouldNotScaleForEqualToSize() {

        Point point = tested.scaleToFitCalc(96, POINT_96_96);

        assertThat(point).isEqualTo(POINT_96_96);

    }

    @SmallTest
    public void testShouldNotScaleSquareToMaxSquare() {

        Point point = tested.scaleToFitCalc(96, new Point(128, 128));

        assertThat(point).isEqualTo(POINT_96_96);
    }

    @SmallTest
    public void testShouldScaleWithCorrectAspectRatio() {

        Point point = tested.scaleToFitCalc(96, new Point(128, 265));

        // aspect ratio should
        assertThat(point).isEqualTo(new Point(46, 96));
    }
}
