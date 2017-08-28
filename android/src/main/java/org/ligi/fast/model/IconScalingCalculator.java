package org.ligi.fast.model;

import android.graphics.Point;

class IconScalingCalculator {
    static Point scaleDimensions(int maxDist, Point point) {
        final float scale;
        if (point.x < maxDist && point.y < maxDist) {
            // nothing is over dist px -> we are good with the given value
            scale = 1f;
        } else {

            if (point.x > point.y) {
                scale = (float) maxDist / point.x;
            } else {
                scale = (float) maxDist / point.y;
            }
        }
        return new Point((int) (point.x * scale), (int) (point.y * scale));
    }
}
