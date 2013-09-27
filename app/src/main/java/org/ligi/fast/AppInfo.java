package org.ligi.fast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
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

    public AppInfo(Context _ctx, String pkgname, Boolean bool) {
        this(_ctx);

        // init attributes
    	
    	try {
    		PackageManager  pm = ctx.getPackageManager();
            ApplicationInfo app = pm.getApplicationInfo(pkgname, 0);   
            
            
			
            label = pm.getApplicationLabel(app).toString().replaceAll("ά", "α").replaceAll("έ", "ε").replaceAll("ή", "η").replaceAll("ί", "ι").replaceAll("ό", "ο").replaceAll("ύ", "υ").replaceAll("ώ", "ω").replaceAll("Ά", "Α").replaceAll("Έ", "Ε").replaceAll("Ή", "Η").replaceAll("Ί", "Ι").replaceAll("Ό", "Ο").replaceAll("Ύ", "Υ").replaceAll("Ώ", "Ω");
            
            package_name = pkgname;
            
            activity_name = pm.getLaunchIntentForPackage(pkgname).getComponent().getClassName();
            call_count = 0;
            
            // calculate the hash
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(package_name.getBytes());
                md.update(activity_name.getBytes());
                byte[] messageDigest = md.digest();

                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++)
                    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                hash = hexString.toString();

            } catch (NoSuchAlgorithmException e) {
                Log.w("FastAppSearchTool",
                        "MD5 not found - having a fallback - but really - no MD5 - where the f** am I?");
                hash = package_name; // fallback
            }

            // cache the Icon
            if (!getIconCacheFile().exists()) {

                Bitmap icon = drawableToBitmap(pm.getApplicationIcon(app));
                
                try {
                    getIconCacheFile().createNewFile();
                    FileOutputStream fos = new FileOutputStream(getIconCacheFile());
                    icon.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                    Log.w("FastAppSearchTool", " Could not cache the Icon");
                }
            }
    	} catch (NameNotFoundException e) {
            //Toast toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT);
            //toast.show();
            e.printStackTrace();
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
            try {
                icon = new BitmapDrawable(ctx.getResources(), new FileInputStream(getIconCacheFile()));
            } catch (FileNotFoundException e) {
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
