package org.ligi.fast;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AppActionDialogBuilder extends AlertDialog.Builder {
	private AppInfo app_info;
	private Context context;

	private boolean isMarketApp() {
		if (app_info.getPackageName()==null)
			return false;
		String installer_pkg=context.getPackageManager().getInstallerPackageName( app_info.getPackageName());
		return installer_pkg!=null && installer_pkg.startsWith(ApplicationContext.STORE_PNAME);
	}
	public AppActionDialogBuilder(Context _context, AppInfo _app_info) {

		super(_context);
		app_info = _app_info;
		context = _context;
		
		CharSequence[] items;
		
		if (new FASTPrefs(context).isMarketForAllActivated()
			|| isMarketApp() )
			items=new CharSequence[] {
					context.getString(R.string.application_details),
					context.getString(R.string.create_shortcut),					
					context.getString(R.string.open_as_notification),
					context.getString(R.string.open_in)+" " + ApplicationContext.STORE_NAME,
					 };
		else
			items=new CharSequence[] {
				context.getString(R.string.application_details),
				context.getString(R.string.create_shortcut),
				context.getString(R.string.open_as_notification) };
		
		setItems(
				items,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							showInstalledAppDetails(context,
									app_info.getPackageName());
							break;

						case 1:

							Intent notifyIntent = app_info.getIntent();

							PendingIntent intent = PendingIntent.getActivity(
									context, 0, notifyIntent,
									PendingIntent.FLAG_UPDATE_CURRENT
											| Notification.FLAG_AUTO_CANCEL);

							final Notification notifyDetails = new NotificationCompat.Builder(
									context)
									.setContentTitle(
											"FAST Launch "
													+ app_info.getLabel())
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentIntent(intent)
									.setAutoCancel(true).getNotification();

							((NotificationManager) context
									.getSystemService(Context.NOTIFICATION_SERVICE))
									.notify((int) (Math.random() * 1000),
											notifyDetails);

							break;
						case 2:
							Intent shortcutIntent = new Intent();
							shortcutIntent.setClassName(app_info.getPackageName(),app_info.getLabel());
							shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							shortcutIntent.addCategory(Intent.ACTION_PICK_ACTIVITY);
							Intent create_shortcut_intent = new Intent();
							create_shortcut_intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
							// Sets the custom shortcut's title
							create_shortcut_intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, app_info.getLabel());

							BitmapDrawable bd=(BitmapDrawable)(app_info.getIcon());
							Bitmap newbit;
							newbit=bd.getBitmap();
							create_shortcut_intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, newbit);

							create_shortcut_intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
							context.sendBroadcast(create_shortcut_intent);
							break;
						case 3:
							try {
								context.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("market://details?id="
												+ app_info.getPackageName())));
							} catch (android.content.ActivityNotFoundException anfe) {
								context.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/details?id="
												+ app_info.getPackageName())));
							}

<<<<<<< HEAD
=======
						case 2:
							try {
								context.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("market://details?id="
												+ app_info.getPackageName())));
							} catch (android.content.ActivityNotFoundException anfe) {
								context.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse(ApplicationContext.getStoreURL4PackageName(app_info.getPackageName()))));
							}

>>>>>>> master
							break;
						}
					}

				});

		CheckBox cb = new CheckBox(context);
		cb.setText("Launch if it is the only one left");
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}

		});
	}

	private static final String SCHEME = "package";

	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

	private static final String APP_PKG_NAME_22 = "pkg";

	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

	public static void showInstalledAppDetails(Context context,
			String packageName) {
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // above 2.3
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else { // below 2.3
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
					: APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME,
					APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);
	}
}
