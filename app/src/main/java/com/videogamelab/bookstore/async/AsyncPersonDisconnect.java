package com.videogamelab.bookstore.async;

import android.os.AsyncTask;
import android.view.View;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.webservices.Person;

public class AsyncPersonDisconnect extends AsyncTask<Object, Void, Void> {

    private FragmentCache cache;

    public AsyncPersonDisconnect(FragmentCache cache) {
        this.cache = cache;

        cache.progress.setMessage("Disconnecting session ...");
    }

    @Override
    protected void onPreExecute() {
        cache.progress.show();

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.INVISIBLE);
    }

    @Override
    protected Void doInBackground(Object... params) {
        long startTime = System.currentTimeMillis();

        Utility.sleep(1000);

        long elapsedTime = System.currentTimeMillis() - startTime;
        long delayTime = (elapsedTime > 2000) ? 1 : (2000 - elapsedTime);

        Utility.sleep(delayTime);

        while(cache.activity == null) { }

        return null;
    }

    @Override
    protected  void onPostExecute(Void result) {
        cache.progress.dismiss();

        cache.sendMessage(Utility.MESSAGE_UPDATE_PERSON, new Person());

        cache.sendMessage(Utility.MESSAGE_ACTIVITY_FINISH, null);
    }

}