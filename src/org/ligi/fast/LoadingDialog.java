package org.ligi.fast;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Dialog to make the waiting time for the initial index building nicer for the user
 * inform him which app we are processing and how far we are progressed with that
 *
 * @author Marcus -ligi- Büschleb
 *         <p/>
 *         License GPLv3
 */
public class LoadingDialog extends Dialog {

    private ImageView icon_iv; // we will show the icon of the act app which is processed - makes the time appear shorter
    private TextView label_tv;
    private ProgressBar progressBar;

    public LoadingDialog(Context context) {
        super(context);
        setContentView(R.layout.loading_dialog);
        icon_iv = (ImageView) findViewById(R.id.imageView);
        label_tv = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        setTitle("Caching to serve FAST");
        setCancelable(false);
    }

    public void setIcon(Drawable icon) {
        icon_iv.setImageDrawable(icon);
    }

    public void setText(String text) {
        label_tv.setText(text);
    }

    public ProgressBar getProgess() {
        return progressBar;
    }
}
