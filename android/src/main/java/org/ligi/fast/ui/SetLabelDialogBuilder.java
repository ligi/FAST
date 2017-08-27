package org.ligi.fast.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.ligi.axt.helpers.ViewHelper;
import org.ligi.fast.R;
import org.ligi.fast.model.AppInfo;

class SetLabelDialogBuilder extends AlertDialog.Builder {

    SetLabelDialogBuilder(final Context context, final AppInfo app_info) {
        super(context);

        setTitle("" + String.format(context.getString(R.string.change_label_of), app_info.getDisplayLabel()));

        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_set_label, null);
        setView(layout);

        final CheckBox newEntry = (CheckBox) layout.findViewById(R.id.checkBox_newEntry);
        final CheckBox hideOriginalEntry = (CheckBox) layout.findViewById(R.id.checkBox_hideOriginalEntry);
        final EditText label = (EditText) layout.findViewById(R.id.editText_newLabel);
        ImageButton clear = (ImageButton) layout.findViewById(R.id.imageButton_clear);

        newEntry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hideOriginalEntry.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });

        hideOriginalEntry.setChecked(app_info.getPinMode() == -1);

        label.setHint(app_info.getLabel());
        label.setText(app_info.getDisplayLabel());
        label.setSelection(label.length());
        label.requestFocus();
        label.postDelayed(new Runnable() {

            @Override
            public void run() {
                new ViewHelper(label).showKeyboard();
            }
        }, 200);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                label.setText("");
            }
        });

        setPositiveButton(R.string.change_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (newEntry.isChecked()) {
                    AppInfo new_entry = new AppInfo(context, app_info.toCacheString());
                    new_entry.setOverrideLabel(label.getText().toString());
                    new_entry.setLabelMode(2);
                    ((SearchActivity) context).addEntry(new_entry);
                    if (hideOriginalEntry.isChecked()) {
                        app_info.setPinMode(-1);
                    }
                } else {
                    app_info.setOverrideLabel(label.getText().toString());
                    if (app_info.getLabelMode() == 0) {
                        app_info.setLabelMode(1);
                    }
                }

                ((SearchActivity) context).configureAdapter();
            }
        });

        setNeutralButton(R.string.cancel, null);
    }
}
