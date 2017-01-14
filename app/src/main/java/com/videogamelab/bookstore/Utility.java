package com.videogamelab.bookstore;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

public class Utility {

    public static final String BOOKSTORE_APP = "Bookstore";

    public static final String CACHE_MAIN = "MainCache";
    public static final String CACHE_PERSON = "PersonCache";
    public static final String CACHE_BOOK = "BookCache";

    public static final String OBJECT_PERSON = "Person";
    public static final String OBJECT_RENT = "Rent";
    public static final String OBJECT_BOOK = "Book";

    public static final Integer PERSON_ACTIVITY = 1;
    public static final Integer BOOK_ACTIVITY = 2;

    public static final int MESSAGE_DIALOG_BOOK_ADD = 3;
    public static final int MESSAGE_DIALOG_BOOK_EDIT = 4;
    public static final int MESSAGE_DIALOG_BOOK_REMOVE = 5;

    public static final int MESSAGE_DIALOG_PERSON_CONNECT = 6;
    public static final int MESSAGE_DIALOG_PERSON_DISCONNECT = 7;
    public static final int MESSAGE_DIALOG_PERSON_REGISTER = 8;
    public static final int MESSAGE_DIALOG_PERSON_DELETE = 9;
    public static final int MESSAGE_DIALOG_PERSON_MODIFY = 10;

    public static final int MESSAGE_DIALOG_RENT_CREATE= 11;
    public static final int MESSAGE_DIALOG_RENT_EXTEND = 12;
    public static final int MESSAGE_DIALOG_RENT_FINISH = 13;

    public static final int MESSAGE_UPDATE_BOOK = 14;
    public static final int MESSAGE_UPDATE_PERSON = 15;
    public static final int MESSAGE_UPDATE_RENT = 16;
    public static final int MESSAGE_UPDATE_BOOK_LIST = 17;
    public static final int MESSAGE_RENT_STATUS = 18;
    public static final int MESSAGE_LAYOUT_VISIBILITY = 19;
    public static final int MESSAGE_ACTIVITY_FINISH = 20;

    public static final int PERSON_BIRTHDAY_YEAR_MIN = 12;
    public static final int PERSON_BIRTHDAY_DEFAULT_YEAR = 1993;
    public static final int PERSON_BIRTHDAY_DEFAULT_MONTH = 3;
    public static final int PERSON_BIRTHDAY_DEFAULT_DAY = 29;

    public static final int BOOK_PUBLISH_YEAR_MIN = 0;
    public static final int BOOK_PUBLISH_DEFAULT_YEAR = 2010;
    public static final int BOOK_PUBLISH_DEFAULT_MONTH = 1;
    public static final int BOOK_PUBLISH_DEFAULT_DAY = 1;

    public static final int RENT_MAX_DAYS = 7;
    public static final int RENT_MIN_DAYS = 1;

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Log.e(BOOKSTORE_APP, "Failed to find MD5 algorithm!");
        }
    }

    public static Bitmap scaleBitmap(float density, Integer targetW, Integer targetH, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int boundWidth = Math.round((float) targetW * density);
        int boundHeight = Math.round((float) targetH * density);

        float xScale = ((float) boundWidth) / width;
        float yScale = ((float) boundHeight) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Integer numberOfDays(Calendar c1, Calendar c2) {
        int days = 0;

        while (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
            c1.add(Calendar.DAY_OF_MONTH, 1);

            days++;
        }

        return days;
    }

    public static Date addDay(Date d, Integer days) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        while (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, 1);

            days--;
        }

        return c.getTime();
    }

    public static String MD5(String password) {
        digest.update(password.getBytes(), 0, password.length());

        return new BigInteger(1, digest.digest()).toString(16);
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
