package org.ligi.fast;

import android.app.Activity;
import android.app.Application;
import org.ligi.tracedroid.TraceDroid;

public class ApplicationContext extends Application {

    // for google-play

    public final static String STORE_URL = "http://play.google.com/store/apps/details?id=";
    public final static String STORE_NAME = "Google Play";
    public final static String STORE_PNAME = "com.android.vending";

	/*
    // for amazon
	public final static String STORE_URL="http://www.amazon.com/gp/mas/dl/android?p=";
	public final static String STORE_NAME="Amazon Store";
	public final static String STORE_PNAME="com.amazon.venezia";
	*/

    public final static String getStoreURL4PackageName(String pname) {
        return STORE_URL + pname;
    }

    private FASTPrefs mPrefs;

    public FASTPrefs getPrefs() {
        if (mPrefs == null)
            mPrefs = new FASTPrefs(this);
        return mPrefs;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        TraceDroid.init(this);
    }

    public void applyTheme(Activity activity) {

        if (getPrefs().getTheme().equals("light"))
            activity.setTheme(R.style.light);
        else if (getPrefs().getTheme().equals("dark"))
            activity.setTheme(R.style.dark);
            // and transparent dark/light
        else if (getPrefs().getTheme().equals("transparent"))
            activity.setTheme(R.style.transparent_dark);
        else if (getPrefs().getTheme().equals("transparent_light"))
            activity.setTheme(R.style.transparent_light);

    }

}
