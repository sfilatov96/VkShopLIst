package com.vkshoplist.sfilatov96.vkshoplist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by sfilatov96 on 29.10.16.
 */
public class AddItemDialog extends DialogFragment {
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_add_shoplist_item, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddItemDialog.this.getDialog().cancel();
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
            //напрямую переопределяем слушателя для кнопки "Отмена" для предотвращения закрытия диалога
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText name_input = (EditText) view.findViewById(R.id.name_input);
                    EditText quanity_input = (EditText) view.findViewById(R.id.quantity_input);
                    Spinner spinner = (Spinner) view.findViewById(R.id.values_spinner);
                    if( name_input.getText().toString().isEmpty()  || quanity_input.getText().toString().isEmpty() ){
                        ((CreateListActivity)getActivity()).emptyFields();

                    } else {
                        ((CreateListActivity) getActivity()).FillShopList(name_input.getText().toString(), quanity_input.getText().toString(),
                                spinner.getSelectedItem().toString());
                        AddItemDialog.this.getDialog().dismiss();
                    }
                }
            });
        }
    }
}