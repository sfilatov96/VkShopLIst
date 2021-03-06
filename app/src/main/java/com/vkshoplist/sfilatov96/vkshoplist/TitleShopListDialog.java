package com.vkshoplist.sfilatov96.vkshoplist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by sfilatov96 on 29.10.16.
 */
public class TitleShopListDialog extends DialogFragment {
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.shop_list_title_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TitleShopListDialog.this.getDialog().cancel();
                        ((CreateListActivity)getActivity()).finish();
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
                    EditText title_input = (EditText) view.findViewById(R.id.title_input);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.isPrivatecheckBox);
                    if( title_input.getText().toString().isEmpty()){
                        ((CreateListActivity)getActivity()).emptyFields();

                    } else {
                        if (checkBox.isChecked()) {
                            ((CreateListActivity) getActivity()).GetShopListTitle(title_input.getText().toString(), true);
                            TitleShopListDialog.this.getDialog().dismiss();
                        } else {
                            ((CreateListActivity) getActivity()).GetShopListTitle(title_input.getText().toString(), false);
                            TitleShopListDialog.this.getDialog().dismiss();
                        }
                    }
                }
            });
        }
    }
}

