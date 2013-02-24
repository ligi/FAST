package org.ligi.fast;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Dialog to make the aiting time for the initial index building nicer for the user
 *
 * @author Marcus -ligi- Büschleb
 *         <p/>
 *         License GPLv3
 */
public class LoadingDialog extends Dialog {

    private ImageView icon_iv; // we will show the icon of the act app which is processed - makes the time appear shorter
    private TextView label_tv;

    public LoadingDialog(Context context) {
        super(context);
        setContentView(R.layout.loading_dialog);
        icon_iv = (ImageView) findViewById(R.id.imageView);
        label_tv = (TextView) findViewById(R.id.textView);
        setTitle("Caching to serve FAST");
        setCancelable(false);
    }

    public void setIcon(Drawable icon) {
        icon_iv.setImageDrawable(icon);
    }

    public void setText(String text) {
        label_tv.setText(text);
    }
}
