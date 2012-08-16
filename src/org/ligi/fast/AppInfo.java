package org.ligi.fast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
/**
 * Class to Retrieve / Store Application Infos needed by this App
 * 
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 */
public class AppInfo {
	private String label;
	private String package_name;
	private String activity_name;
	private String hash;
	private int call_count;
	private Context ctx;
	private BitmapDrawable icon; // caching the Icon
	


	
	private AppInfo(Context _ctx) {
		ctx = _ctx;
	}

	public AppInfo(Context _ctx, String cache_str) {
		this(_ctx);

		String[] app_info_str_split = cache_str.split(";;");

		hash= app_info_str_split[0];
		label = app_info_str_split[1];
		package_name = app_info_str_split[2];
		activity_name = app_info_str_split[3];
		call_count = Integer.parseInt(app_info_str_split[4]);
	}

	public String toCacheString() {
		return hash+";;"+label+";;" +package_name+";;"+activity_name+";;"+call_count;
	}
	
	public AppInfo(Context _ctx, ResolveInfo ri) {
		this(_ctx);
		
		// init attributes
		label = ri.loadLabel(ctx.getPackageManager()).toString();
		package_name = ri.activityInfo.packageName;
		activity_name = ri.activityInfo.name;
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
			BitmapDrawable icon = (BitmapDrawable) ri.loadIcon(ctx
					.getPackageManager());
			try {
				getIconCacheFile().createNewFile();
				FileOutputStream fos = new FileOutputStream(getIconCacheFile());
				icon.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (IOException e) {
				Log.w("FastAppSearchTool", " Could not cache the Icon");
			}
		}
	}

	private File getIconCacheFile() {
		return new File(ctx.getCacheDir() + "/" + hash + ".png");
	}

	public Intent getIntent() {
		Intent intent = new Intent();
		intent.setClassName(package_name, activity_name);
		return intent;
	}

	public String getLabel() {
		return label;
	}

	public int getCallCount() {
		return call_count;
	}

	public Drawable getIcon() {
		if (icon==null) {
			try {
				icon=new BitmapDrawable(ctx.getResources(), new FileInputStream(getIconCacheFile()));
			} catch (FileNotFoundException e) {
				Log.w("FastAppSearchTool", "Could not load the cached Icon" + getIconCacheFile().getAbsolutePath());
			}
		}
		return icon;
	}
}
