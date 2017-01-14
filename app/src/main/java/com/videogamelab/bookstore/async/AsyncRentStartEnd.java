package com.videogamelab.bookstore.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.dialog.DialogRentCreate;
import com.videogamelab.webservices.Book;
import com.videogamelab.webservices.BookstoreException;
import com.videogamelab.webservices.BookstoreFault;
import com.videogamelab.webservices.Rent;
import com.videogamelab.webservices.RentService;
import com.videogamelab.webservices.VectorRent;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncRentStartEnd extends AsyncTask<Rent, Void, Rent> {

    private FragmentCache cache;

    private BookstoreFault fault;

    private VectorRent rentList;

    public AsyncRentStartEnd(FragmentCache cache) {
        this.cache = cache;
        fault = null;

        cache.progress.setMessage("Finding available date ...");
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
            AtomicInteger maxRentDays = new AtomicInteger(0);
            Date availableDate = new Date();
            findFirstAvailableRent(maxRentDays, availableDate);

            DialogRentCreate createDialog = DialogRentCreate.newInstance(result, availableDate, maxRentDays.get());
            createDialog.show(cache.activity.getFragmentManager(), "RentCreate");
        } else {
            // Display error if it is from client
            if (fault.getCode().compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to find available date!", AsyncRentStartEnd.class, result);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }
        }

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
    }

    private void findFirstAvailableRent(AtomicInteger maxRentDays, Date availableDate) {
        Calendar c = Calendar.getInstance();

        availableDate.setTime(c.getTime().getTime());
        maxRentDays.set(Utility.RENT_MAX_DAYS);

        // Check if we have at least two rents for this Book
        if (rentList.size() > 1) {
            int i = 0;

            while (i < (rentList.size() - 1)) {
                // End date of the current rent
                Date endRent = (Date) rentList.get(i).getProperty(Rent.END_DATE);

                // Start date of the next rent
                Date startRent = (Date) rentList.get(i + 1).getProperty(Rent.START_DATE);

                // Calculate time left between two rents
                long diff = startRent.getTime() - endRent.getTime();

                // Check if there is available time (minimum 3 days - excluding the start and end)
                // 86 400 000 milliseconds = 1 day
                if (diff >= (86400000 * (Utility.RENT_MIN_DAYS + 2))) {
                    break;
                }

                i++;
            }

            // No free time found between rents
            if (i == (rentList.size() - 1)) {
                availableDate.setTime(((Date) rentList.get(i).getProperty(Rent.END_DATE)).getTime());

                availableDate.setTime(Utility.addDay(availableDate, 1).getTime());
            } else { // Compute available time between two rents
                Date endDate = (Date) rentList.get(i + 1).getProperty(Rent.START_DATE);
                c.setTime(endDate);
                c.add(Calendar.DAY_OF_MONTH, -1);

                Calendar c1 = Calendar.getInstance();
                c1.setTime((Date) rentList.get(i).getProperty(Rent.END_DATE));
                c1.add(Calendar.DAY_OF_MONTH, 1);

                availableDate.setTime(c1.getTime().getTime());

                maxRentDays.set(Utility.numberOfDays(c1, c));
            }
        } else if (rentList.size() == 1) {
            availableDate.setTime(((Date) rentList.get(0).getProperty(Rent.END_DATE)).getTime());

            availableDate.setTime(Utility.addDay(availableDate, 1).getTime());
        }
    }
}
