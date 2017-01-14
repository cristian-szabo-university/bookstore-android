package com.videogamelab.bookstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.videogamelab.bookstore.dialog.DialogError;
import com.videogamelab.bookstore.dialog.DialogRentFinish;

import java.lang.reflect.InvocationTargetException;

public class FragmentCache extends Fragment {

    public Activity activity;

    public AsyncTask task;

    public Toast notify;

    public Handler queue;

    public ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        progress = new ProgressDialog(getActivity());
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        notify = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        activity = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        activity = null;

        if (progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void sendNotify(String message) {
        notify.cancel();
        notify = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        notify.show();
    }

    public void sendMessage(int what, Object obj) {
        Message msg = new Message();
        msg.obj = obj;
        msg.what = what;
        queue.sendMessage(msg);
    }

    public <T extends AsyncTask, S> void executeTask(Class<T> clazz, S... params) {
        task = null;

        try {
            task = clazz.getDeclaredConstructor(FragmentCache.class).newInstance(this);
        } catch (java.lang.InstantiationException e) {
            Log.e(Utility.BOOKSTORE_APP, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(Utility.BOOKSTORE_APP, e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e(Utility.BOOKSTORE_APP, e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(Utility.BOOKSTORE_APP, e.getMessage());
        }

        if (task != null) {
            task.execute(params);
        }
    }

    public <T extends AsyncTask, S extends Parcelable> void showDialog(String message, Class<T> clazz, final S... params) {
        DialogError errorDialog = DialogError.newInstance(message, clazz, params);
        errorDialog.show(getFragmentManager(), "Error");
    }

}
