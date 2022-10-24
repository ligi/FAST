package org.ligi.fast.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.ligi.fast.App;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;

public class AppIconCache {
    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final String CACHE_FILE_ENDING = ".png";

    private final IconConverter iconConverter = new IconConverter();

    private final Context ctx;
    private final AppInfo appInfo;

    private SoftReference<Drawable> cachedIcon;

    public AppIconCache(Context ctx, AppInfo appInfo) {
        this.ctx = ctx;
        this.appInfo = appInfo;
    }

    private static File getIconCacheFile(AppInfo appInfo) {
        return new File(App.getBaseDir() + "/" + appInfo.getHash() + CACHE_FILE_ENDING);
    }

    private static void invalidateIconCacheFile(File file) {
        if (!file.setLastModified(0)) {
            Log.w("Unable to invalidate " + file.getAbsolutePath());
        }
    }

    /**
     * Notify the icon cache that a cached file needs to be updated the next time {@link #cacheIcon(ResolveInfo)}
     * is called. Until then, the outdated files will be kept as a fallback.
     */
    public static void invalidateIconCache() {
        if (App.getBaseDir().exists()) {
            File[] iconFiles = App.getBaseDir().listFiles(pathname -> pathname.getName().endsWith(CACHE_FILE_ENDING));
            if (iconFiles != null) {
                for (File iconFile : iconFiles) {
                    invalidateIconCacheFile(iconFile);
                }
            }
        }
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

    private File getIconCacheFile() {
        return getIconCacheFile(this.appInfo);
    }

    private boolean tryIconCaching(IconCacheSpec iconCacheSpec, ResolveInfo ri, PackageManager pm) {
        if (getIconCacheFile().exists() && getIconCacheFile().lastModified() > appInfo.getLastUpdateTime()) {
            return true;
        }
        final Drawable icon = ri.loadIcon(pm);
        if (icon == null) {
            Log.w("Could not cache icon: PackageManager.loadIcon() returned null");
            return false;
        }
        final File iconFile = getIconCacheFile();
        final Bitmap cacheIcon = iconConverter.toScaledBitmap(icon, iconCacheSpec);
        try {
            iconFile.createNewFile();
            final FileOutputStream fos = new FileOutputStream(iconFile);
            cacheIcon.compress(COMPRESS_FORMAT, iconCacheSpec.quality, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.w("Could not cache icon: " + e);
        }
        invalidateIconCacheFile(iconFile);
        return false;
    }

    public Drawable getIcon() {
        // return the cached Icon if we have one
        if (cachedIcon != null && cachedIcon.get() != null) {
            return cachedIcon.get();
        }

        try {
            final FileInputStream fileInputStream = new FileInputStream(getIconCacheFile());
            final BitmapDrawable drawable = new BitmapDrawable(ctx.getResources(), fileInputStream);
            fileInputStream.close();
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
