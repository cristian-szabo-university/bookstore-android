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
import com.videogamelab.webservices.VectorRent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AsyncRentStatus extends AsyncTask<Rent, Void, Rent> {

    private VectorRent rentList;

    private FragmentCache cache;

    private BookstoreFault fault;

    public AsyncRentStatus(FragmentCache cache) {
        this.cache = cache;
        fault = null;

        cache.progress.setMessage("Calculating rent status ...");
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

        if ((Integer) person.getProperty(Person.ID) < 1) {
            return null;
        }

        if ((Integer) rent.getProperty(Rent.ID) > 0) {
            return rent;
        }

        try {
            rentList = service.findRentByBook((Integer) book.getProperty(Book.ID));

            rent = findRentIfExist(person);
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
        if (cache.progress.isShowing()) {
            cache.progress.dismiss();
        }

        // Check if there was an error
        if (fault == null) {
            String status = computeRentStatus(result);

            cache.sendMessage(Utility.MESSAGE_RENT_STATUS, status);

            if (result != null && rentList != null && (Integer) result.getProperty(Rent.ID) > 0) {
                cache.sendMessage(Utility.MESSAGE_UPDATE_RENT, result);
            }
        } else {
            // Display error if it is from client
            if (fault.getCode().compareTo(BookstoreFault.ERROR_CLIENT) == 0) {
                cache.sendNotify(fault.getError());
            } else { // Show basic message if it is from server
                cache.showDialog("Failed to calculate rent status!", AsyncRentStatus.class, result);

                // Log server errors
                Log.e(Utility.BOOKSTORE_APP, fault.getError());
            }
        }

        cache.sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.VISIBLE);
    }

    private Rent findRentIfExist(Person person) {
        Rent found = new Rent();
        Integer personId = (Integer) person.getProperty(Person.ID);

        for (int i = 0; i < rentList.size(); i++) {
            Rent rent = rentList.get(i);

            Person object = (Person) rent.getProperty(Rent.PERSON);
            Integer index = (Integer) object.getProperty(Person.ID);

            if (index.equals(personId)) {
                found = rent;

                break;
            }
        }

        return found;
    }

    private String computeRentStatus(Rent rent) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (rent == null) {
            return "Connect/Register to rent this book.";
        }

        if ((Integer) rent.getProperty(Rent.ID) < 1) {
            return "Book is available for rent.";
        }

        Calendar startDate = Calendar.getInstance();
        Date currentDate = startDate.getTime();
        startDate.setTime((Date) rent.getProperty(Rent.START_DATE));

        Calendar endDate = Calendar.getInstance();
        endDate.setTime((Date) rent.getProperty(Rent.END_DATE));

        long diff = startDate.getTime().getTime() - currentDate.getTime();

        if (diff > 0) {
            endDate.setTime(currentDate);

            int daysStart = Utility.numberOfDays(endDate, startDate);

            return "Rent starts in " + daysStart + " days.";
        } else {
            diff = endDate.getTime().getTime() - currentDate.getTime();

            if (diff > 0) {
                Date beginDate = startDate.getTime();

                startDate.setTime(currentDate);

                int daysLeft = Utility.numberOfDays(startDate, endDate);

                return "Return in " + daysLeft + " days from " + dateFormat.format(beginDate);
            } else {
                return "Please return book or extend rent.";
            }
        }
    }

}