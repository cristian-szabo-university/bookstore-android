package com.videogamelab.bookstore.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.activity.ActivityMain;
import com.videogamelab.webservices.Person;

public class DialogPersonConnect extends DialogFragment {

    private FragmentCache cache;

    private EditText editUsername;
    private EditText editPassword;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ActivityMain) {
            cache = ((ActivityMain) activity).getFragmentCache();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_connect, null);

        editUsername = (EditText) v.findViewById(R.id.editUsername);
        editPassword = (EditText) v.findViewById(R.id.editPassword);

        builder.setView(v);
        builder.setTitle(R.string.dialog_title_connect);
        builder.setPositiveButton(R.string.dialog_button_connect, new ConnectButtonListener());

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    private class ConnectButtonListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String password = editPassword.getText().toString();
            if (!password.isEmpty()) {
                password = Utility.MD5(password);
            }

            Person newPerson = new Person();
            newPerson.setProperty(Person.USERNAME, editUsername.getText().toString());
            newPerson.setProperty(Person.PASSWORD, password);

            cache.sendMessage(Utility.MESSAGE_DIALOG_PERSON_CONNECT, newPerson);
        }

    }

}