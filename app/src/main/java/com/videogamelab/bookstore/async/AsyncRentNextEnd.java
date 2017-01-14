package com.videogamelab.bookstore.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.dialog.DialogRentExtend;
import com.videogamelab.webservices.Book;
import com.videogamelab.webservices.BookstoreException;
import com.videogamelab.webservices.BookstoreFault;
import com.videogamelab.webservices.Rent;
import com.videogamelab.webservices.RentService;
import com.videogamelab.webservices.VectorRent;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncRentNextEnd extends AsyncTask<Rent, Void, Rent> {

    private FragmentCache cache;

    private BookstoreFault fault;

    private VectorRent rentList;

    public AsyncRentNextEnd(FragmentCache cache) {
        this.cache = cache;
        fault = null;

        cache.progress.setMessage("Finding available days ...");
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

        try {
            rentList = service.findRentByBook((Integer) book.getProperty(Book.ID));
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
            Date availableDate = new Date(((Date) result.getProperty(Rent.END_DATE)).getTime());
            AtomicInteger maxRentDays = new AtomicInteger(0);
            findExtendDays(result, 1, maxRentDays);

            DialogRentExtend extendFragment = DialogRentExtend.newInstance(result, availableDate, maxRentDays.get());
            extendFragment.show(cache.activity.getFragmentManager(), "RentExtend");
        } else {
            // Display error if it is from client
            if (fault.getCode().compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to find available days!", AsyncRentNextEnd.class, result);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }
        }

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
    }

    private void findExtendDays(Rent rent, Integer minRentDays, AtomicInteger maxRentDays) {
        Calendar c = Calendar.getInstance();

        boolean last = false;
        int i = 0;

        // Find the next rent bigger than current rent
        while (i < rentList.size()) {
            // Skip check if rent index is equal with this rent index
            if (rent.getProperty(0).equals(rentList.get(i).getProperty(0))) {
                // Because rents are sorted asc by startDate
                if (i == (rentList.size() - 1)) {
                    last = true;
                }

                break;
            }

            i++;
        }

        if (last) {
            maxRentDays.set(Utility.RENT_MAX_DAYS);
        } else {
            // Start date of the next rent
            c.setTime((Date) rentList.get(i + 1).getProperty(Rent.START_DATE));

            // End date of this rent
            Calendar c1 = Calendar.getInstance();
            c1.setTime((Date) rent.getProperty(Rent.END_DATE));

            Integer daysAvailable = Utility.numberOfDays(c1, c);

            if (daysAvailable >= (minRentDays + 1)) {
                daysAvailable--;

                maxRentDays.set(daysAvailable);
            }
        }
    }
}
