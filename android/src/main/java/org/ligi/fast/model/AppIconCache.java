package org.ligi.fast.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.ligi.fast.App;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

public class AppIconCache {

    private final Context ctx;
    private final AppInfo appInfo;

    private SoftReference<Drawable> cachedIcon;

    public AppIconCache(Context ctx, AppInfo appInfo) {
        this.ctx = ctx;
        this.appInfo = appInfo;
    }

    public void cacheIcon(ResolveInfo ri) {
        final PackageManager packageManager = ctx.getPackageManager();

        if (packageManager == null) {
            Log.w("could not cache the Icon - PM is null");
            return;
        }

        if (tryIconCaching(new IconCacheSpec(), ri, packageManager)) {
            return;
        }

        System.gc(); // try again after GC could help when in mem trouble

        if (tryIconCaching(new IconCacheSpec(), ri, packageManager)) {
            return;
        }

        final IconCacheSpec downScalingImageSpec = new IconCacheSpec() {{
            maxSize = 48;
        }};

        if (tryIconCaching(downScalingImageSpec, ri, packageManager)) {
            return;
        }

        final IconCacheSpec reallySmallImageSpec = new IconCacheSpec() {{
            maxSize = 48;
            quality = 50;
        }};

        tryIconCaching(reallySmallImageSpec, ri, packageManager);

        // too bad - we kind of tried everything ..
    }

    class IconCacheSpec {
        public int maxSize = App.getSettings().getIconResolution();
        public int quality = 100;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored") // as we do not care if it is new or old
    private boolean createIconCacheFile() throws IOException {
        return getIconCacheFile().createNewFile();
    }

    private File getIconCacheFile() {
        final File file = new File(App.getBaseDir() + "/" + appInfo.getHash() + ".png");
        Log.i("returning " + file.exists());
        return file;
    }

    private boolean tryIconCaching(IconCacheSpec iconCacheSpec, ResolveInfo ri, PackageManager pm) {
        if (getIconCacheFile().exists()) {
            return true;
        }

        try {
            final BitmapDrawable icon = (BitmapDrawable) ri.loadIcon(pm);
            if (icon != null) {
                createIconCacheFile();

                final FileOutputStream fos = new FileOutputStream(getIconCacheFile());

                final Point scaledSize = scaleToFitCalc(iconCacheSpec.maxSize, bitmapSizeToPoint(icon.getBitmap()));

                // we want a filter when UpScaling / not when DownScaling
                final boolean filter = icon.getBitmap().getWidth() < scaledSize.x;

                final Bitmap cacheIcon = Bitmap.createScaledBitmap(icon.getBitmap(), scaledSize.x, scaledSize.y, filter);

                cacheIcon.compress(Bitmap.CompressFormat.PNG, iconCacheSpec.quality, fos);

                fos.close();
                return true;
            }

        } catch (Exception e) {
            Log.w(" Could not cache the Icon" + e);
        }
        return false;
    }

    private Point bitmapSizeToPoint(Bitmap bitmap) {
        return new Point(bitmap.getWidth(), bitmap.getHeight());
    }

    public Point scaleToFitCalc(int maxDist, Point point) {
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


    public Drawable getIcon() {
        // return the cached Icon if we have one
        if (cachedIcon != null && cachedIcon.get() != null) {
            return cachedIcon.get();
        }

        try {
            final FileInputStream fileInputStream = new FileInputStream(getIconCacheFile());
            final BitmapDrawable drawable = new BitmapDrawable(ctx.getResources(), fileInputStream);
            cachedIcon = new SoftReference<Drawable>(drawable);
            return cachedIcon.get();
        } catch (Exception e) {
            Log.w("Could not load the cached Icon" + getIconCacheFile().getAbsolutePath() + " reason " + e);
        }

        // if we came here we we could not return the cached Icon - ty to rescue situation
        try {
            return ctx.getResources().getDrawable(android.R.drawable.ic_menu_more);
        } catch (Exception e) {
            // could not load rescue icon - another attempt follows
        }

        // create a image for the very last rescue-attempt
        return new BitmapDrawable(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444));
    }

}
