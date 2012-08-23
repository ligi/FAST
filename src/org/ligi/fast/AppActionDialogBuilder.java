package org.ligi.fast;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class AppActionDialogBuilder extends AlertDialog.Builder {
	private AppInfo app_info;
	private Context context;
	
	public AppActionDialogBuilder(Context _context,AppInfo _app_info) {
		
		super(_context);
		app_info=_app_info;
		context=_context;
		
		setItems(new CharSequence[] { "Application Details","Open in Market","Open as Notification"} ,new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				switch (which) {
				case 0:
					showInstalledAppDetails(context,app_info.getPackageName());
					break;
					
				case 1:
					try {
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+app_info.getPackageName())));
					} catch (android.content.ActivityNotFoundException anfe) {
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+app_info.getPackageName())));
					}
					
					break;
				case 2:
					

				    CharSequence contentTitle = "FAST Launch";

				    CharSequence contentText = app_info.getLabel();

				    final Notification notifyDetails =
				        new Notification(R.drawable.ic_launcher, "FAST Launch " + app_info.getLabel(), System.currentTimeMillis());
				    Intent notifyIntent = app_info.getIntent();
				    PendingIntent intent =
				          PendingIntent.getActivity(context, 0,
				          notifyIntent,  PendingIntent.FLAG_UPDATE_CURRENT | Notification.FLAG_AUTO_CANCEL);

				    notifyDetails.setLatestEventInfo(context, contentTitle, contentText, intent);
				    notifyDetails.flags|=Notification.FLAG_AUTO_CANCEL;
				    
				    ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify((int)(Math.random()*1000), notifyDetails);
				    
				    break;

				}
			}
			
		});
		
		CheckBox cb=new CheckBox(context);
		cb.setText("Launch if it is the only one left");
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}
			
		});
		/*
		TextView tv=new TextView(context);
		tv.setText("foo");
		setView(cb);
		*/
	}

	
	private static final String SCHEME = "package";

	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

	private static final String APP_PKG_NAME_22 = "pkg";

	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

	public static void showInstalledAppDetails(Context context, String packageName) {
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
