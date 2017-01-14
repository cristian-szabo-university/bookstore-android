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

public class AsyncPersonGet extends AsyncTask<Person, Void, Person> {

    private FragmentCache cache;

    private BookstoreFault fault;

    public AsyncPersonGet(FragmentCache cache) {
        this.cache = cache;

        cache.progress.setMessage("Getting account ...");
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

        Person person = params[0];

        try {
            person = service.findPersonById((Integer) person.getProperty(Person.ID));
        } catch (BookstoreException e) {
            fault = e.getFault();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        long delayTime = (elapsedTime > 2000) ? 1 : (2000 - elapsedTime);

        Utility.sleep(delayTime);

        while(cache.activity == null) { }

        return person;
    }

    @Override
    protected  void onPostExecute(Person result) {
        cache.progress.dismiss();

        // Check if there was an error
        if (fault == null) {
            cache.sendMessage(Utility.MESSAGE_UPDATE_PERSON, result);

            cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
        } else {
            // Display error if it is from client
            if (fault.getCode().compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to get account!", AsyncPersonGet.class, result);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }
        }
    }
}