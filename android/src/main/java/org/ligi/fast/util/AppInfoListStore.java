package org.ligi.fast.util;

import android.content.Context;
import android.content.Intent;

import org.ligi.axt.helpers.FileHelper;
import org.ligi.fast.App;
import org.ligi.fast.R;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AppInfoListStore {

    public final Context context;
    public final File file;
    public static final String SEPARATOR = "\n";

    public AppInfoListStore(Context context) {
        this.context = context;
        file = new File(App.getBaseDir(), "index.csv");
    }

    public void share() throws IOException {
        final String cache_file_string = new FileHelper(file).loadToString();
        final Intent intent = new Intent(Intent.ACTION_SEND);
        //context.startActivity(intent);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, cache_file_string);
        sendIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"ligi@ligi.de"});
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public AppInfoList load() {

        Log.i(App.LOG_TAG, "Loading package list from file");

        final String inString;
        try {
            inString = new FileHelper(file).loadToString();
        } catch (IOException e) {
            return new AppInfoList();
        }

        final AppInfoList res = new AppInfoList();

        if (inString == null) {
            return res;
        }


        final String[] lines = inString.split(SEPARATOR);
        for (String line : lines) {
            if (line.length() > 0) {
                final AppInfo appInfo = new AppInfo(context, line);

                if (appInfo.isValid()) {
                    res.add(appInfo);
                }
            }

        }

        return res;
    }

    public void save(List<AppInfo> appInfoList) {
        final StringBuilder res = new StringBuilder();

        for (AppInfo appInfo : appInfoList) {
            res.append(appInfo.toCacheString());
            res.append(SEPARATOR);
        }

        try {
            Log.i(App.LOG_TAG, "Saving package list to file");
            file.createNewFile();
            new FileHelper(file).writeString(res.toString());
        } catch (IOException e) {
            Log.w("could not save PackageList");
        }

    }
}
