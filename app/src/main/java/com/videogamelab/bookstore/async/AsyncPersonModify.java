package com.videogamelab.bookstore.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.webservices.BookstoreException;
import com.videogamelab.webservices.BookstoreFault;
import com.videogamelab.webservices.Person;
import com.videogamelab.webservices.PersonService;

public class AsyncPersonModify extends AsyncTask<Person, Void, Person> {

    private FragmentCache cache;

    private BookstoreFault fault;

    public AsyncPersonModify(FragmentCache cache) {
        this.cache = cache;
        fault = null;

        cache.progress.setMessage("Modifying account ...");
    }

    @Override
    protected void onPreExecute() {
        cache.progress.show();

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.INVISIBLE);
    }

    @Override
    protected Person doInBackground(Person... params) {
        PersonService service = new PersonService();

        long startTime = System.currentTimeMillis();

        Person object = params[0];

        try {
            service.updatePerson(object);
        } catch (BookstoreException e) {
            fault = e.getFault();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        long delayTime = (elapsedTime > 2000) ? 1 : (2000 - elapsedTime);

        Utility.sleep(delayTime);

        while(cache.activity == null) { }

        return object;
    }

    @Override
    protected void onPostExecute(Person result) {
        cache.progress.dismiss();

        // Check if there was an error
        if (fault == null) {
            cache.sendNotify("Modified account successfully!");

            cache.sendMessage(Utility.MESSAGE_UPDATE_PERSON, result);
        } else {
            String code = fault.getCode();

            // Display error if it is from client
            if (code.compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to modify!", AsyncPersonModify.class, result);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }
        }

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
    }
}