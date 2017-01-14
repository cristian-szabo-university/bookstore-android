package com.videogamelab.bookstore.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.webservices.Book;
import com.videogamelab.webservices.BookService;
import com.videogamelab.webservices.BookstoreException;
import com.videogamelab.webservices.BookstoreFault;

public class AsyncBookGet extends AsyncTask<Book, Void, Book> {

    private FragmentCache cache;

    private BookstoreFault fault;

    public AsyncBookGet(FragmentCache cache) {
        this.cache = cache;
        fault = null;

        cache.progress.setMessage("Retrieving book details ...");
    }

    @Override
    protected void onPreExecute() {
        cache.progress.show();

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.INVISIBLE);
    }

    @Override
    protected Book doInBackground(Book... params) {
        BookService service = new BookService();

        long startTime = System.currentTimeMillis();

        Book book = params[0];

        try {
            book = service.findBookById((Integer) book.getProperty(Book.ID));
        } catch (BookstoreException e) {
            fault = e.getFault();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        long delayTime = (elapsedTime > 2000) ? 1 : (2000 - elapsedTime);

        Utility.sleep(delayTime);

        while(cache.activity == null) { }

        return book;
    }

    @Override
    protected  void onPostExecute(Book result) {
        cache.progress.dismiss();

        // Check if there was an error
        if (fault == null) {
            cache.sendMessage(Utility.MESSAGE_UPDATE_BOOK, result);

            cache.sendMessage(Utility.MESSAGE_UPDATE_RENT, null);
        } else {
            // Display error if it is from client
            if (fault.getCode().compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to get book!", AsyncBookGet.class, result);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }

            cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
        }
    }
}
