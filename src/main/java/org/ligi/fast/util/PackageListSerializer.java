package org.ligi.fast.util;

import android.content.Context;
import org.ligi.fast.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class PackageListSerializer {

    public static List<AppInfo> fromString(Context ctx, String inString) {
        List<AppInfo> res = new ArrayList<AppInfo>();

        if (inString == null) {
            return res;
        }

        String[] lines = inString.split("\n");
        for (String line : lines) {
            if (line.length() > 0)
                res.add(new AppInfo(ctx, line));
        }

        return res;
    }
}
