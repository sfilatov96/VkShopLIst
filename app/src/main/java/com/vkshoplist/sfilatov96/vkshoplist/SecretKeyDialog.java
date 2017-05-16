package com.vkshoplist.sfilatov96.vkshoplist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by sfilatov96 on 02.04.17.
 */
public class SecretKeyDialog extends DialogFragment {
    View view;
    SharedPreferences sp;
    final String APP_PREFERENCES_KEY = "SECRET_WORD";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.secret_key_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Ok", null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SecretKeyDialog.this.getDialog().cancel();
                        ((ExecuteListActivity) getActivity()).finish();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText title_input = (EditText) view.findViewById(R.id.secret_word_value);
                    if (title_input.getText().toString().isEmpty()) {
                        ((ExecuteListActivity) getActivity()).emptyFields();

                    } else {
                        sp = getActivity().getSharedPreferences(APP_PREFERENCES_KEY,0);
                        Log.d("secret_word",sp.getString("KEY","")+title_input.getText().toString());
                        if (sp.getString("KEY","").compareTo(title_input.getText().toString()) == 0) {

                            ((ExecuteListActivity) getActivity()).continueCreating();
                            SecretKeyDialog.this.getDialog().dismiss();
                        } else {
                            ((ExecuteListActivity) getActivity()).invalidSecretKey();
                        }
                    }
                }
            });
        }
    }
}