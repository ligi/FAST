package org.ligi.fast.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.ligi.fast.model.AppIconCache.IconCacheSpec;

class DrawableIconConverter {
    Bitmap toScaledBitmap(Drawable icon, IconCacheSpec iconCacheSpec) {
        Point scaledSize = IconScalingCalculator.scaleDimensions(iconCacheSpec.maxSize, drawableSizeToPoint(icon));

        Bitmap bitmap = Bitmap.createBitmap(scaledSize.x, scaledSize.y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);

        return bitmap;
    }

    private Point drawableSizeToPoint(Drawable drawable) {
        return new Point(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
}
