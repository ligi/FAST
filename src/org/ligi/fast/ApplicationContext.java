package org.ligi.fast;

import android.app.Application;

public class ApplicationContext extends Application {
	
	private FASTPrefs mPrefs;
	
	public FASTPrefs getPrefs() {
		if (mPrefs==null)
			mPrefs=new FASTPrefs(this);
		return mPrefs;
	}
		
}
