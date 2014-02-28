package org.ligi.axt.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;

public class ActivityHelper extends ContextHelper {

    private final Activity activity;

    public ActivityHelper(Activity activity) {
        super (activity);
        this.activity =activity;
    }

    public <T extends View> T findById(int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * dynamically disable rotation
     * to be used in onCreate
     * slightly modified from http://stackoverflow.com/a/8765901/322642
     */
    @TargetApi(5)
    public void disableRotation() {
        switch (activity.getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                    if(rotation == android.view.Surface.ROTATION_90|| rotation == android.view.Surface.ROTATION_180){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                    if(rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                }
                break;
        }//switch
    }

    /**
     * dynamically enable rotation
     * counterpart to enableRotation
     * slightly modified from http://stackoverflow.com/a/8765901/322642
     */
    public void enableRotation() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
