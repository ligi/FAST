package org.ligi.fast.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ligi.axt.helpers.ActivityHelper;
import org.ligi.fast.R;
import org.ligi.fast.background.BaseAppGatherAsyncTask;
import org.ligi.fast.model.AppInfo;
import org.ligi.fast.util.PackageListStore;

/**
 * Dialog to make the waiting time for the initial index building nicer for the user
 * inform him which app we are processing and how far we are progressed with that
 */
public class LoadingDialog extends Activity {

    private ImageView iconImageView; // we will show the icon of the act app which is processed - makes the time appear shorter
    private TextView labelTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        iconImageView = (ImageView) findViewById(R.id.imageView);
        labelTextView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        setTitle(getString(R.string.loadingDialogTitle));

        new BaseAppGatherAsyncTask(this) {

            private int actAppIndex = 0;

            @Override
            protected void onProgressUpdate(AppInfo... values) {
                super.onProgressUpdate(values);

                actAppIndex++;
                getProgressBar().setMax(appCount);
                getProgressBar().setProgress(actAppIndex);

                setIcon(values[0].getIcon());
                setText(values[0].getLabel());

            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                new PackageListStore(LoadingDialog.this).save(appInfoList);
                setResult(RESULT_OK);
                finish();
            }

        }.execute();

        setWindowWidth();
        new ActivityHelper(this).disableRotation();
    }

    @SuppressWarnings("deprecation")
    // we cannot use the new getSize function - or we would get a NoSuchMethod error on newer devices
    private void setWindowWidth() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = 3 * getWindowManager().getDefaultDisplay().getWidth() / 4;
        getWindow().setAttributes(params);
    }

    public void setIcon(Drawable icon) {
        iconImageView.setImageDrawable(icon);
    }

    public void setText(String text) {
        labelTextView.setText(text);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

}
