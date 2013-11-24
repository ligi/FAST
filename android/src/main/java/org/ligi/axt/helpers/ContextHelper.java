package org.ligi.axt.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class ContextHelper {

    private final Context context;

    public ContextHelper(Context context) {
        this.context = context;
    }

    public void startActivityForClass(Class class2start) {
        Intent intent = new Intent(context, class2start);
        context.startActivity(intent);
    }

    /**
    * a little hack because I strongly disagree with the style guide here
    * ;-)
    * not having the Actionbar overfow menu also with devices with hardware
    * key really helps discoverability
    * http://stackoverflow.com/questions/9286822/how-to-force-use-of-overflow-menu-on-devices-with-menu-button
    **/
    public void forceOverFlowMenuEvenThoughDeviceHasPhysical() {

        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore - but at least we tried ;-)
        }
    }
}
