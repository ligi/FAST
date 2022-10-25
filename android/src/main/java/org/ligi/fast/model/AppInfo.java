package org.ligi.fast.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.ligi.axt.helpers.ResolveInfoHelper;
import org.ligi.fast.util.UmlautConverter;
import org.ligi.tracedroid.logging.Log;

/**
 * Class to Retrieve / Store Application Information needed by this App
 */
public class AppInfo {
    private static final String SEPARATOR = ";;";

    // Static information, identifies this activity
    private String packageName;
    private String activityName;
    private String hash;

    // User generated data
    private String overrideLabel;
    private int callCount = 0;
    private int pinMode = 0;
    private int labelMode = 0;

    // Dynamic information that can be regenerated
    private AppIconCache iconCache;
    private String label;
    private long installTime;
    private long lastUpdateTime;

    // Runtime state
    private String alternateDisplayLabel;
    private boolean isValid = false;

    private AppInfo(Context ctx) {
        iconCache = new AppIconCache(ctx, this);
    }

    public AppInfo(Context ctx, String cache_str) {
        this(ctx);

        String[] app_info_str_split = cache_str.split(SEPARATOR);
        int highest_index = Math.min(app_info_str_split.length - 1, 9);
        try {
            switch (highest_index) {
                case 9: lastUpdateTime = Long.parseLong(app_info_str_split[9]);
                case 8: overrideLabel = app_info_str_split[8];
                case 7: labelMode = Integer.parseInt(app_info_str_split[7]);
                case 6: pinMode = Integer.parseInt(app_info_str_split[6]);
                case 5: installTime = Long.parseLong(app_info_str_split[5]);
                case 4: callCount = Integer.parseInt(app_info_str_split[4]);
                case 3: // Minimal set of values in a valid entry
                    activityName = app_info_str_split[3];
                    packageName = app_info_str_split[2];
                    label = app_info_str_split[1];
                    hash = app_info_str_split[0];
                    isValid = true;
                    break;
            }
        } catch (Exception ignored) {
        }
        if (!isValid) {
            return;
        }
        if (lastUpdateTime < installTime) {
            lastUpdateTime = installTime;
        }
        calculateAlternateLabel();
    }

    public AppInfo(Context _ctx, ResolveInfo ri) {
        this(_ctx);
        label = new ResolveInfoHelper(ri).getLabelSafely(_ctx);
        if (ri.activityInfo == null) {
            return;
        }
        packageName = ri.activityInfo.packageName;
        activityName = ri.activityInfo.name;
        try {
            PackageManager pm = _ctx.getPackageManager();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                String appFile = appInfo.sourceDir;
                lastUpdateTime = new File(appFile).lastModified();
                installTime = lastUpdateTime;
            } else {
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                lastUpdateTime = pi.lastUpdateTime;
                installTime = pi.firstInstallTime;
            }
        } catch (NameNotFoundException e) {
            return; // Package does not exist
        }
        hash = calculateTheHash();
        isValid = true;

        // All static attributes done, call everything that depends on them now
        calculateAlternateLabel();
        iconCache.cacheIcon(ri);
    }

    private void calculateAlternateLabel() {
        alternateDisplayLabel = UmlautConverter.replaceAllUmlautsReturnNullIfEqual(getDisplayLabel());
    }

    public String toCacheString() {
        return hash + SEPARATOR + label + SEPARATOR + packageName +
                SEPARATOR + activityName + SEPARATOR + callCount +
                SEPARATOR + installTime + SEPARATOR + pinMode +
                SEPARATOR + labelMode + SEPARATOR + overrideLabel +
                SEPARATOR + lastUpdateTime;
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

    public boolean isSameActivity(AppInfo appInfo) {
        return this.hash.equals(appInfo.hash);
    }

    public Drawable getIcon() {
        return iconCache.getIcon();
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * The label that is meant to be displayed to the user.
     *
     * @return the user-set label if present, default otherwise
     */
    public String getDisplayLabel() {
        if (labelMode == 0 || overrideLabel == null) {
            return label;
        } else {
            return overrideLabel;
        }
    }

    public String getLabel() {
        return this.label;
    }

    public int getCallCount() {
        return callCount;
    }

    public long getInstallTime() {
        return installTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void incrementCallCount() {
        callCount++;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getHash() {
        return hash;
    }

    /**
     * The sanitized version of {@link #getDisplayLabel()}}
     * Umlauts are converted and accents are stripped from characters.
     *
     * @return sanitized display label. null if it would be equal to {@link #getDisplayLabel()}.
     */
    public String getAlternateDisplayLabel() {
        return alternateDisplayLabel;
    }

    /**
     * Update activity info while preserving user generated data and settings
     *
     * @param currentInfo up to date info about the activity
     */
    public void updateInfo(AppInfo currentInfo) {
        this.iconCache = currentInfo.iconCache;
        // Because we fall back to lastUpdateTime when setting installTime below gingerbread our
        // best bet is to just never update this value to keep it close to the first install time.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            this.installTime = currentInfo.installTime;
        }
        this.lastUpdateTime = currentInfo.lastUpdateTime;
        this.label = currentInfo.label;
        calculateAlternateLabel();
    }

    public int getPinMode() {
        return pinMode;
    }

    public void setPinMode(int pinMode) {
        this.pinMode = pinMode;
    }

    /**
     * The label mode controls which label to display to the user and identifies aliases.
     * An alias is an independent entry for this activity with its own override label, call count
     * and pin mode. An alias uses the user-set label if available.
     * Valid label modes are:
     *  0 - use the default label
     *  1 - use the user-set label if available
     *  2 - this is an alias
     *
     * @return the current label mode for this record
     */
    public int getLabelMode() {
        return this.labelMode;
    }

    /**
     * @see #getLabelMode()
     * @param mode the new label mode for this record
     */
    public void setLabelMode(int mode) {
        this.labelMode = mode;
    }

    public void setOverrideLabel(String label) {
        this.overrideLabel = label;
        calculateAlternateLabel();
    }
}
