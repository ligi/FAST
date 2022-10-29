package org.ligi.axt.helpers;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ViewHelper {

    private final View view;

    public ViewHelper(View view) {
        this.view = view;
    }

    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            Log.w("AndroidHelper", "could not hide Keyboard as INPUT_METHOD_SERVICE is not available");
        }
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        } else {
            Log.w("AndroidHelper", "could not show Keyboard as INPUT_METHOD_SERVICE is not available");
        }
    }

    public void startIntentOnClick(final Intent intent) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(intent);
            }
        });
    }


}
