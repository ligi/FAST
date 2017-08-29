package org.ligi.fast.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.ligi.fast.model.AppIconCache.IconCacheSpec;

class IconConverter {
    private final BitmapIconConverter bitmapIconConverter = new BitmapIconConverter();
    private final DrawableIconConverter drawableIconConverter = new DrawableIconConverter();

    Bitmap toScaledBitmap(Drawable icon, IconCacheSpec iconCacheSpec) {
        if (icon instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
            return bitmapIconConverter.toScaledBitmap(bitmapDrawable, iconCacheSpec);
        } else {
            return drawableIconConverter.toScaledBitmap(icon, iconCacheSpec);
        }
    }
}
