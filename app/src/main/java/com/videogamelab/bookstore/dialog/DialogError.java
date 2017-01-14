package com.videogamelab.bookstore.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.activity.ActivityBook;
import com.videogamelab.bookstore.activity.ActivityMain;
import com.videogamelab.bookstore.activity.ActivityPerson;

public class DialogError<T extends AsyncTask, S extends Parcelable> extends DialogFragment {

    private FragmentCache cache;

    private Class clazz;

    private S[] params;

    private String message;

    public static <F extends AsyncTask, E extends Parcelable> DialogError newInstance(String message, Class<F> clazz, E... params) {
        DialogError dialog = new DialogError<F, E>();

        Bundle args = new Bundle();
        args.putParcelableArray("Arguments", params);
        args.putString("Message", message);
        args.putString("Clazz", clazz.getName());

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        params = (S[]) args.getParcelableArray("Arguments");
        message = args.getString("Message");

        try {
            clazz = Class.forName(args.getString("Clazz"));
        } catch (ClassNotFoundException e) {
            Log.e(Utility.BOOKSTORE_APP, e.getMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ActivityBook) {
            cache = ((ActivityBook) activity).getFragmentCache();
        } else if (activity instanceof ActivityMain) {
            cache = ((ActivityMain) activity).getFragmentCache();
        } else if (activity instanceof ActivityPerson) {
            cache = ((ActivityPerson) activity).getFragmentCache();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putParcelableArray("Arguments", params);
        state.putString("Message", message);
        state.putString("Clazz", clazz.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_title_error);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.dialog_button_try_again,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cache.executeTask(clazz, params);
                    }
                });

        builder.setNegativeButton(R.string.dialog_button_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

}
