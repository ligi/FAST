package org.ligi.fast.util;

import android.content.Context;

import org.ligi.androidhelper.AXT;
import org.ligi.fast.AppInfo;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackageListStore {

    public final Context context;
    public final File file;
    public static final String SEPARATOR = "\n";

    public PackageListStore(Context context) {
        this.context = context;
        file = new File(context.getCacheDir(), "index2.csv");
    }

    public List<AppInfo> load() {
        final String inString;
        try {
            inString = AXT.at(file).loadToString();
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }

        List<AppInfo> res = new ArrayList<AppInfo>();

        if (inString == null) {
            return res;
        }


        String[] lines = inString.split(SEPARATOR);
        for (String line : lines) {
            if (line.length() > 0) {
                AppInfo appInfo = new AppInfo(context, line);

                if (appInfo.isValid()) {
                    res.add(appInfo);
                }
            }

        }

        return res;
    }

    public void save(List<AppInfo> appInfoList) {
        StringBuilder res = new StringBuilder();

        for (AppInfo appInfo : appInfoList) {
            res.append(appInfo.toCacheString());
            res.append("\n");
        }

        try {
            file.createNewFile();
            AXT.at(file).writeString(res.toString());
        } catch (IOException e) {
            Log.w("could not save PackageList");
        }

    }
}
