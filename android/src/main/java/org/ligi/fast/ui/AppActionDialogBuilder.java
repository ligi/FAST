package org.ligi.fast.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.ligi.fast.App;
import org.ligi.fast.R;
import org.ligi.fast.TargetStore;
import org.ligi.fast.model.AppInfo;
import org.ligi.tracedroid.logging.Log;

public class AppActionDialogBuilder extends AlertDialog.Builder {
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    private final AppInfo app_info;
    private final Context context;

    public AppActionDialogBuilder(Context _context, AppInfo _app_info) {

        super(_context);
        app_info = _app_info;
        context = _context;

        ArrayList<LabelAndCode> fkt_map = new ArrayList<LabelAndCode>();

        fkt_map.add(new LabelAndCode(context.getString(R.string.application_details), new Runnable() {
            @Override
            public void run() {
                showInstalledAppDetails(context, app_info.getPackageName());
            }
        }));

        if (app_info.getPinMode() == 0) {
            fkt_map.add(new LabelAndCode(context.getString(R.string.pin_app), new Runnable() {
                @Override
                public void run() {
                    app_info.setPinMode(1);
                    ((SearchActivity)context).configureAdapter();
                }
            }));
            fkt_map.add(new LabelAndCode(context.getString(R.string.hide_app), new Runnable() {
                @Override
                public void run() {
                    app_info.setPinMode(-1);
                    ((SearchActivity)context).configureAdapter();
                }
            }));
        }
        else if (app_info.getPinMode() == 1) {
            fkt_map.add(new LabelAndCode(context.getString(R.string.unpin_app), new Runnable() {
                @Override
                public void run() {
                    app_info.setPinMode(0);
                    ((SearchActivity)context).configureAdapter();
                }
            }));
            fkt_map.add(new LabelAndCode(context.getString(R.string.hide_app), new Runnable() {
                @Override
                public void run() {
                    app_info.setPinMode(-1);
                    ((SearchActivity)context).configureAdapter();
                }
            }));
        }
        else {
            fkt_map.add(new LabelAndCode(context.getString(R.string.pin_app), new Runnable() {
                @Override
                public void run() {
                    app_info.setPinMode(1);
                    ((SearchActivity)context).configureAdapter();
                }
            }));
            fkt_map.add(new LabelAndCode(context.getString(R.string.unhide_app), new Runnable() {
                @Override
                public void run() {
                    app_info.setPinMode(0);
                    ((SearchActivity)context).configureAdapter();
                }
            }));
        }

        fkt_map.add(new LabelAndCode(context.getString(R.string.open_as_notification), new OpenAsNotificationRunnable()));

        if (App.getSettings().isMarketForAllActivated() || isMarketApp()) {
            fkt_map.add(new LabelAndCode(context.getString(R.string.open_in) + " " + TargetStore.STORE_NAME, new Runnable() {
                @Override
                public void run() {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TargetStore.STORE_URL + app_info.getPackageName())));
                    } catch (android.content.ActivityNotFoundException ignored) {
                    }
                }
            }));
        }

        fkt_map.add(new LabelAndCode(context.getString(R.string.share), new Runnable() {
            @Override
            public void run() {
                try {
                    String message = "check out this app: " + App.getStoreURL4PackageName(app_info.getPackageName());
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);

                    context.startActivity(Intent.createChooser(share, "Share FAST"));
                } catch (android.content.ActivityNotFoundException anfe) {

                }
            }
        }));

        if (hasShortCutPermission()) {
            fkt_map.add(new LabelAndCode(context.getString(R.string.create_shortcut), new CreateShortCutRunnable()));
        }

        CharSequence[] items = new CharSequence[fkt_map.size()];
        final Runnable[] item_code = new Runnable[fkt_map.size()];

        for (int i = 0; i < fkt_map.size(); i++) {
            items[i] = fkt_map.get(i).label;
            item_code[i] = fkt_map.get(i).code;
        }


        setItems(items, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item_code[which].run();
            }
        });

    }

    //  @TargetApi(5)
    // TODO find out why the above is not working and I need to use SupressLint to get rid of the error
    @SuppressLint("newApi")
    private static void showInstalledAppDetails(Context context,

                                                String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    private boolean hasShortCutPermission() {
        try {
            String permission = "com.android.launcher.permission.INSTALL_SHORTCUT";
            int res = context.checkCallingOrSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMarketApp() {
        try {
            if (app_info.getPackageName() == null) return false;
            PackageManager packageManager = context.getPackageManager();

            if (packageManager == null) {
                Log.w("strange - there was no PackageManager - might lie to the user now with false" +
                      "as I cannot determine the correct answer to the question isMarketApp()");
                return false;
            }

            String installer_pkg = packageManager.getInstallerPackageName(app_info.getPackageName());
            return installer_pkg != null && installer_pkg.startsWith(TargetStore.STORE_PNAME);
        } catch (Exception e) {
            return false;
        }
    }

    private class OpenAsNotificationRunnable implements Runnable {
        @Override
        public void run() {
            final Intent notifyIntent = app_info.getIntent();

            PendingIntent intent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            final String title = app_info.getLabel();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            final Notification notification;

            if (Build.VERSION.SDK_INT < 16) {
                notification = new Notification();
                notification.icon = R.drawable.ic_launcher;
                try {
                    Method deprecatedMethod = notification.getClass()
                                                          .getMethod("setLatestEventInfo",
                                                                     Context.class,
                                                                     CharSequence.class,
                                                                     CharSequence.class,
                                                                     PendingIntent.class);
                    deprecatedMethod.invoke(notification, context, title, context.getString(R.string.appActionDialog_title), intent);
                } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    Log.w("Method not found", e);
                }
            } else {
                // Use new API
                Notification.Builder builder = new Notification.Builder(context).setContentIntent(intent)
                                                                                .setSmallIcon(R.drawable.ic_launcher)
                                                                                .setContentTitle(title);
                notification = builder.build();
            }

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify((int) (Math.random() * Integer.MAX_VALUE), notification);
        }
    }

    private class LabelAndCode {
        public String label;
        public Runnable code;

        public LabelAndCode(String label, Runnable code) {
            this.code = code;
            this.label = label;
        }

    }

    private class CreateShortCutRunnable implements Runnable {
        @Override
        public void run() {
            final Intent shortcutIntent = new Intent();
            shortcutIntent.setClassName(app_info.getPackageName(), app_info.getLabel());
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shortcutIntent.addCategory(Intent.ACTION_PICK_ACTIVITY);
            final Intent create_shortcut_intent = new Intent();
            create_shortcut_intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            // Sets the custom shortcut's title
            create_shortcut_intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, app_info.getLabel());

            final BitmapDrawable bd = (BitmapDrawable) (app_info.getIcon());
            Bitmap newBitmap = bd.getBitmap();
            create_shortcut_intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, newBitmap);

            create_shortcut_intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(create_shortcut_intent);
        }
    }
}
