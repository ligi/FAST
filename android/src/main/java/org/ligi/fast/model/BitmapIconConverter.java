package org.ligi.fast.model;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;

import org.ligi.fast.model.AppIconCache.IconCacheSpec;

class BitmapIconConverter {
    Bitmap toScaledBitmap(BitmapDrawable icon, IconCacheSpec iconCacheSpec) {
        final Bitmap inputBitmap = icon.getBitmap();
        final Point scaledSize = IconScalingCalculator.scaleDimensions(iconCacheSpec.maxSize, bitmapSizeToPoint(inputBitmap));

        // we want a filter when UpScaling / not when DownScaling
        final boolean filter = inputBitmap.getWidth() < scaledSize.x;

        return Bitmap.createScaledBitmap(inputBitmap, scaledSize.x, scaledSize.y, filter);
    }

    private Point bitmapSizeToPoint(Bitmap bitmap) {
        return new Point(bitmap.getWidth(), bitmap.getHeight());
    }
}
