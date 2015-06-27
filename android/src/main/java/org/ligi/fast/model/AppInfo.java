package org.ligi.fast.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import org.ligi.axt.helpers.ResolveInfoHelper;
import org.ligi.fast.util.UmlautConverter;
import org.ligi.tracedroid.logging.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to Retrieve / Store Application Information needed by this App
 */
public class AppInfo {
    private static final String SEPARATOR = ";;";

    private String label;
    private String alternateLabel;
    private String packageName;
    private String alternatePackageName;
    private String activityName;
    private String hash;
    private int callCount;
    private long lastUsedTime;
    private boolean isValid = true;

    private final AppIconCache iconCache;

    private AppInfo(Context ctx) {
        iconCache = new AppIconCache(ctx, this);
    }

    public AppInfo(Context ctx, String cache_str) {
        this(ctx);

        String[] app_info_str_split = cache_str.split(SEPARATOR);

        if (app_info_str_split.length < 6) {
            isValid = false;
            return;
        }

        hash = app_info_str_split[0];
        label = app_info_str_split[1];
        packageName = app_info_str_split[2];
        activityName = app_info_str_split[3];
        callCount = Integer.parseInt(app_info_str_split[4]);
        lastUsedTime = Long.parseLong(app_info_str_split[5]);

        calculateAlternateLabelAndPackageName();

    }

    public AppInfo(Context _ctx, ResolveInfo ri) {
        this(_ctx);

        // init attributes
        label = new ResolveInfoHelper(ri).getLabelSafely(_ctx);
        label = label.replace("ά", "α").replaceAll("έ", "ε").replaceAll("ή", "η").replaceAll("ί", "ι").replaceAll("ό", "ο").replaceAll("ύ", "υ").replaceAll("ώ", "ω").replaceAll("Ά", "Α").replaceAll("Έ", "Ε").replaceAll("Ή", "Η").replaceAll("Ί", "Ι").replaceAll("Ό", "Ο").replaceAll("Ύ", "Υ").replaceAll("Ώ", "Ω");
        if (ri.activityInfo != null) {
            packageName = ri.activityInfo.packageName;
            activityName = ri.activityInfo.name;
        } else {
            packageName = "unknown";
            activityName = "unknown";
        }
        callCount = 0;
        lastUsedTime = 0;

        hash = calculateTheHash();
        calculateAlternateLabelAndPackageName();
        iconCache.cacheIcon(ri);
    }

    private void calculateAlternateLabelAndPackageName() {
        alternateLabel = UmlautConverter.replaceAllUmlautsReturnNullIfEqual(label);
        alternatePackageName = UmlautConverter.replaceAllUmlautsReturnNullIfEqual(packageName);
    }

    public String toCacheString() {
        return hash + SEPARATOR + label + SEPARATOR + packageName +
                SEPARATOR + activityName + SEPARATOR + callCount + SEPARATOR + lastUsedTime;
    }

    private String calculateTheHash() {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(packageName.getBytes());
            md.update(activityName.getBytes());

            final byte[] messageDigest = md.digest();

            final StringBuilder hexString = new StringBuilder();
            for (byte digestByte : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & digestByte));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.w("MD5 not found - having a fallback - but really - no MD5 - where the f** am I?");
            return packageName; // fallback
        }
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.setClassName(packageName, activityName);
        return intent;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AppInfo)) {
            return false;
        }

        AppInfo other = (AppInfo) o;
        return toCacheString().equals(other.toCacheString());
    }

    public Drawable getIcon() {
        return iconCache.getIcon();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivityName() {
        return packageName;
    }

    public String getLabel() {
        return label;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int count) {
        callCount = count;
    }

    public void incrementCallCount() {
        callCount++;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long usedTime) {
        lastUsedTime = usedTime;
    }

    public void updateLastUsedTime() {
        lastUsedTime = System.currentTimeMillis();
    }

    public boolean isValid() {
        return isValid;
    }

    public String getHash() {
        return hash;
    }

    public String getAlternateLabel() {
        return alternateLabel;
    }

    public String getAlternatePackageName() {
        return alternatePackageName;
    }

    public void mergeSafe(AppInfo appInfo) {
        final int localCallCount = getCallCount();
        final int remoteCallCount = appInfo.getCallCount();
        setCallCount(Math.max(localCallCount,remoteCallCount));

        final long localLastUsedTime = getLastUsedTime();
        final long remoteLastUsedTime = appInfo.getLastUsedTime();
        setLastUsedTime(Math.max(localLastUsedTime, remoteLastUsedTime));

        label=appInfo.getLabel();
        calculateAlternateLabelAndPackageName();
    }
}
