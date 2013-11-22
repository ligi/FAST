package org.ligi.fast.ui;

import android.content.Context;

import org.ligi.fast.App;
import org.ligi.fast.R;

public class IconDimensions {
    public final int outerSizeInPx;
    public final int innerSizeInPx;

    public IconDimensions(Context context) {
        String size = (App.getSettings().getIconSize());

        int outerSizeRes = R.dimen.cell_size;
        int innerSizeRes = R.dimen.icon_size;

        if (size.equals("tiny")) {
            outerSizeRes = R.dimen.cell_size_tiny;
            innerSizeRes = R.dimen.icon_size_tiny;
        } else if (size.equals("small")) {
            outerSizeRes = R.dimen.cell_size_small;
            innerSizeRes = R.dimen.icon_size_small;
        } else if (size.equals("large")) {
            outerSizeRes = R.dimen.cell_size_large;
            innerSizeRes = R.dimen.icon_size_large;
        }


        outerSizeInPx = context.getResources().getDimensionPixelSize(outerSizeRes);
        innerSizeInPx = context.getResources().getDimensionPixelSize(innerSizeRes);


    }
}
