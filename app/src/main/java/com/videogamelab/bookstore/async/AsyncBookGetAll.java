package com.videogamelab.bookstore.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.webservices.BookService;
import com.videogamelab.webservices.BookstoreException;
import com.videogamelab.webservices.BookstoreFault;
import com.videogamelab.webservices.VectorBook;

public class AsyncBookGetAll extends AsyncTask<Object, Void, VectorBook> {

    private FragmentCache cache;

    private BookstoreFault fault;

    public AsyncBookGetAll(FragmentCache cache) {
        this.cache = cache;
        fault = null;

        cache.progress.setMessage("Getting all books ...");
    }

    @Override
    protected void onPreExecute() {
        cache.progress.show();

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.INVISIBLE);
    }

    @Override
    protected VectorBook doInBackground(Object... params) {
        BookService service = new BookService();

        long startTime = System.currentTimeMillis();

        VectorBook result = null;

        try {
            result = service.findBookAll();
        } catch (BookstoreException e) {
            fault = e.getFault();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        long delayTime = (elapsedTime > 2000) ? 1 : (2000 - elapsedTime);

        Utility.sleep(delayTime);

        while(cache.activity == null) { }

        return result;
    }

    @Override
    protected void onPostExecute(VectorBook result) {
        cache.progress.dismiss();

        // Check if there was an error
        if (fault == null) {
            if (result.size() > 0) {
                cache.sendMessage(Utility.MESSAGE_UPDATE_BOOK_LIST, result);
            } else {
                cache.sendNotify("No books found!");
            }
        } else {
            // Display error if it is from client
            if (fault.getCode().compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to get books!", AsyncBookGetAll.class, null);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }
        }

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
    }

}