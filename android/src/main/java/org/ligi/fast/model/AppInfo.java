package org.ligi.fast.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
    private int callCount;
    private int pinMode = 0;
    private int labelMode = 0;

    // Dynamic information that can be regenerated
    private final AppIconCache iconCache;
    private String label;
    private long installTime;

    // Runtime state
    private String alternateDisplayLabel;
    private String alternatePackageName;
    private boolean isValid = true;

    private AppInfo(Context ctx) {
        iconCache = new AppIconCache(ctx, this);
    }

    public AppInfo(Context ctx, String cache_str) {
        this(ctx);

        String[] app_info_str_split = cache_str.split(SEPARATOR);

        if (app_info_str_split.length > 4) {
            try {
                hash = app_info_str_split[0];
                label = app_info_str_split[1];
                packageName = app_info_str_split[2];
                activityName = app_info_str_split[3];
                callCount = Integer.parseInt(app_info_str_split[4]);

                if (app_info_str_split.length > 5) {
                    installTime = Long.parseLong(app_info_str_split[5]);
                    if (app_info_str_split.length > 6) {
                        pinMode = Integer.parseInt(app_info_str_split[6]);
                        if (app_info_str_split.length > 7) {
                            labelMode = Integer.parseInt(app_info_str_split[7]);
                            if (app_info_str_split.length > 8) {
                                overrideLabel = app_info_str_split[8];
                            }
                        }
                    }
                }

                calculateAlternateLabelAndPackageName();
                return;
            } catch (Exception ignored) {
            }
        }
        isValid = false;
    }

    public AppInfo(Context _ctx, ResolveInfo ri) {
        this(_ctx);

        // init attributes
        label = new ResolveInfoHelper(ri).getLabelSafely(_ctx);
        label = label.replace("ά", "α")
                     .replaceAll("έ", "ε")
                     .replaceAll("ή", "η")
                     .replaceAll("ί", "ι")
                     .replaceAll("ό", "ο")
                     .replaceAll("ύ", "υ")
                     .replaceAll("ώ", "ω")
                     .replaceAll("Ά", "Α")
                     .replaceAll("Έ", "Ε")
                     .replaceAll("Ή", "Η")
                     .replaceAll("Ί", "Ι")
                     .replaceAll("Ό", "Ο")
                     .replaceAll("Ύ", "Υ")
                     .replaceAll("Ώ", "Ω");
        if (ri.activityInfo != null) {
            packageName = ri.activityInfo.packageName;
            activityName = ri.activityInfo.name;
        } else {
            isValid = false;
            packageName = "unknown";
            activityName = "unknown";
        }
        callCount = 0;

        final PackageManager pmManager = _ctx.getPackageManager();

        installTime = 0;

        try {
            final PackageInfo pi = pmManager.getPackageInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= 9) {
                installTime = pi.lastUpdateTime;
            }
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        hash = calculateTheHash();
        calculateAlternateLabelAndPackageName();
        iconCache.cacheIcon(ri);
    }

    private void calculateAlternateLabelAndPackageName() {
        alternateDisplayLabel = UmlautConverter.replaceAllUmlautsReturnNullIfEqual(getDisplayLabel());
        alternatePackageName = UmlautConverter.replaceAllUmlautsReturnNullIfEqual(packageName);
    }

    public String toCacheString() {
        return hash + SEPARATOR + label + SEPARATOR + packageName +
                SEPARATOR + activityName + SEPARATOR + callCount +
                SEPARATOR + installTime + SEPARATOR + pinMode +
                SEPARATOR + labelMode + SEPARATOR + overrideLabel;
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
        return activityName;
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

    public void setCallCount(int count) {
        callCount = count;
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

    public String getAlternatePackageName() {
        return alternatePackageName;
    }

    public void mergeSafe(AppInfo appInfo) {
        final int localCallCount = getCallCount();
        final int remoteCallCount = appInfo.getCallCount();
        setCallCount(Math.max(localCallCount, remoteCallCount));
        if (appInfo.getPinMode() != 0) {
            setPinMode(appInfo.getPinMode());
        }
        else {
            setPinMode(getPinMode());
        }

        if (appInfo.getLabelMode() == 1) {
            setLabelMode(1);
            setOverrideLabel(appInfo.getOverrideLabel());
        }

        label = appInfo.getLabel();
        calculateAlternateLabelAndPackageName();
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

    public String getOverrideLabel() {
        return overrideLabel;
    }

    public void setOverrideLabel(String label) {
        this.overrideLabel = label;
        calculateAlternateLabelAndPackageName();
    }
}
