package com.videogamelab.bookstore.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.webservices.Book;
import com.videogamelab.webservices.BookstoreException;
import com.videogamelab.webservices.BookstoreFault;
import com.videogamelab.webservices.Person;
import com.videogamelab.webservices.Rent;
import com.videogamelab.webservices.RentService;

public class AsyncRentCreate extends AsyncTask<Rent, Void, Rent> {

    private FragmentCache cache;

    private BookstoreFault fault;

    public AsyncRentCreate(FragmentCache cache) {
        this.cache = cache;
        fault = null;

        cache.progress.setMessage("Creating rent ...");
    }

    @Override
    protected void onPreExecute() {
        cache.progress.show();

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.INVISIBLE);
    }

    @Override
    protected Rent doInBackground(Rent... params) {
        RentService service = new RentService();

        long startTime = System.currentTimeMillis();

        Rent rent = params[0];
        Book book = (Book) rent.getProperty(Rent.BOOK);
        Person person = (Person) rent.getProperty(Rent.PERSON);

        try {
            if ((Integer) rent.getProperty(Rent.ID) < 1) {
                Rent result = service.createRent(
                        (Integer) book.getProperty(Book.ID),
                        (Integer) person.getProperty(Person.ID));

                rent.setProperty(Rent.ID, result.getProperty(Rent.ID));
            }

            service.updateRent(rent);
        } catch (BookstoreException e) {
            fault = e.getFault();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        long delayTime = (elapsedTime > 2000) ? 1 : (2000 - elapsedTime);

        Utility.sleep(delayTime);

        while(cache.activity == null) { }

        return rent;
    }

    @Override
    protected void onPostExecute(Rent result) {
        cache.progress.dismiss();

        // Check if there was an error
        if (fault == null) {
            cache.sendNotify("Rent created successfully!");

            cache.sendMessage(Utility.MESSAGE_UPDATE_RENT, result);
        } else {
            // Display error if it is from client
            if (fault.getCode().compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to create rent!", AsyncRentCreate.class, result);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }

            cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
        }
    }
}

