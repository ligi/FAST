package org.ligi.fast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.ligi.axt.AXT;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to Retrieve / Store Application Information needed by this App
 */
public class AppInfo {
    private String label;
    private String packageName;
    private String activityName;
    private String hash;
    private int callCount;
    private final Context ctx;
    private BitmapDrawable icon; // caching the Icon
    private boolean isValid = true;

    private AppInfo(Context _ctx) {
        ctx = _ctx;
    }

    public AppInfo(Context _ctx, String cache_str) {
        this(_ctx);

        Log.i("trying to parse line: " + cache_str);
        String[] app_info_str_split = cache_str.split(";;");

        if (app_info_str_split.length < 5) {
            isValid = false;
            return;
        }

        hash = app_info_str_split[0];
        label = app_info_str_split[1];
        packageName = app_info_str_split[2];
        activityName = app_info_str_split[3];
        callCount = Integer.parseInt(app_info_str_split[4]);
    }

    public String toCacheString() {
        return hash + ";;" + label + ";;" + packageName + ";;" + activityName + ";;" + callCount;
    }

    public AppInfo(Context _ctx, ResolveInfo ri) {
        this(_ctx);


        // init attributes
        label = AXT.at(ri).getLabelSafely(_ctx);
        label = label.replaceAll("ά", "α").replaceAll("έ", "ε").replaceAll("ή", "η").replaceAll("ί", "ι").replaceAll("ό", "ο").replaceAll("ύ", "υ").replaceAll("ώ", "ω").replaceAll("Ά", "Α").replaceAll("Έ", "Ε").replaceAll("Ή", "Η").replaceAll("Ί", "Ι").replaceAll("Ό", "Ο").replaceAll("Ύ", "Υ").replaceAll("Ώ", "Ω");
        if (ri.activityInfo != null) {
            packageName = ri.activityInfo.packageName;
            activityName = ri.activityInfo.name;
        } else {
            packageName = "unknown";
            activityName = "unknown";
        }
        callCount = 0;


        // calculate the hash
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(packageName.getBytes());
            md.update(activityName.getBytes());

            byte[] messageDigest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte digestByte : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & digestByte));
            }
            hash = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.w("MD5 not found - having a fallback - but really - no MD5 - where the f** am I?");
            hash = packageName; // fallback
        }

        // cache the Icon
        if (!getIconCacheFile().exists()) {
            try {
                cacheIcon(ri);
            } catch (OutOfMemoryError oom) {
                System.gc();
                try {
                    cacheIcon(ri);
                } catch (OutOfMemoryError oom2) {
                    Log.w("could not cache Icon with a final attempt after GC due to Memory issues");
                }
            }
        }

    }

    private void cacheIcon(ResolveInfo ri) {

        PackageManager packageManager = ctx.getPackageManager();

        if (packageManager == null) {
            Log.w("could not cache the Icon - PM is null");
            return;
        }

        BitmapDrawable icon = (BitmapDrawable) ri.loadIcon(packageManager);
        if (icon != null) {
            try {

                createIconCacheFile();

                FileOutputStream fos = new FileOutputStream(getIconCacheFile());
                icon.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (IOException e) {
                Log.w(" Could not cache the Icon");
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") // as we do not care if it is new or old
    private boolean createIconCacheFile() throws IOException {
        return getIconCacheFile().createNewFile();
    }

    private File getIconCacheFile() {
        return new File(ctx.getCacheDir() + "/" + hash + ".png");
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.setClassName(packageName, activityName);
        return intent;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getLabel() {
        return label;
    }

    public int getCallCount() {
        return callCount;
    }

    public Drawable getIcon() {
        if (icon == null) {
            
        	BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inSampleSize = 2;
        	Bitmap downStreamIcon = BitmapFactory.decodeFile(getIconCacheFile().getAbsolutePath(),options);
            
        	if (downStreamIcon != null) {
                icon = new BitmapDrawable(ctx.getResources(),downStreamIcon);
            } else {
                Log.w("Could not load the cached Icon" + getIconCacheFile().getAbsolutePath());
            }
        }
        return icon;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        return toCacheString().equals(toCacheString());
    }

}
