package com.videogamelab.bookstore.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.async.AsyncBookEdit;
import com.videogamelab.bookstore.async.AsyncBookGet;
import com.videogamelab.bookstore.async.AsyncBookRemove;
import com.videogamelab.bookstore.async.AsyncRentCreate;
import com.videogamelab.bookstore.async.AsyncRentExtend;
import com.videogamelab.bookstore.async.AsyncRentFinish;
import com.videogamelab.bookstore.async.AsyncRentNextEnd;
import com.videogamelab.bookstore.async.AsyncRentStartEnd;
import com.videogamelab.bookstore.async.AsyncRentStatus;
import com.videogamelab.bookstore.dialog.DialogBookEdit;
import com.videogamelab.bookstore.dialog.DialogBookRemove;
import com.videogamelab.bookstore.dialog.DialogRentFinish;
import com.videogamelab.webservices.Book;
import com.videogamelab.webservices.Person;
import com.videogamelab.webservices.Rent;
import com.videogamelab.webservices.VectorByte;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityBook extends Activity {

    private BookFragmentCache cache;

    private RelativeLayout layoutBook;
    private TextView txtTitle;
    private TextView txtAuthor;
    private TextView txtPublish;
    private TextView txtDesc;
    private TextView txtStatus;
    private ImageView imgCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        txtPublish = (TextView) findViewById(R.id.txtPublish);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtDesc.setMovementMethod(new ScrollingMovementMethod());
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        layoutBook = (RelativeLayout) findViewById(R.id.layoutBook);

        cache = (BookFragmentCache) getFragmentCache();

        cache.queue = new BookHandler();
    }

    public FragmentCache getFragmentCache() {
        FragmentManager fm = getFragmentManager();

        BookFragmentCache frag = (BookFragmentCache) fm.findFragmentByTag(Utility.CACHE_BOOK);

        if (frag == null) {
            frag = new BookFragmentCache();

            fm.beginTransaction().add(frag, Utility.CACHE_BOOK).commit();

            frag.rent = new Rent();

            Bundle data = getIntent().getExtras();
            frag.rent.setProperty(Rent.BOOK, data.getParcelable(Utility.OBJECT_BOOK));
            frag.rent.setProperty(Rent.PERSON, data.getParcelable(Utility.OBJECT_PERSON));
        }

        return frag;
    }

    @Override
    protected void onStart() {
        super.onStart();

        cache.sendMessage(Utility.MESSAGE_UPDATE_BOOK, null);

        cache.sendMessage(Utility.MESSAGE_RENT_STATUS, null);
    }

    @Override
    public void finish() {
        Intent data = new Intent();

        Person person = (Person) cache.rent.getProperty(Rent.PERSON);
        person.setProperty(Person.AVATAR, new VectorByte());
        data.putExtra(Utility.OBJECT_PERSON, person);

        setResult(RESULT_OK, data);

        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_book, menu);

        MenuItem rentCreateItem = menu.findItem(R.id.action_rent_create);

        Person person = (Person) cache.rent.getProperty(Rent.PERSON);
        if ((Integer) person.getProperty(Person.ID) > 0) {
            rentCreateItem.setVisible(true);
        } else {
            rentCreateItem.setVisible(false);
        }

        MenuItem rentExtendItem = menu.findItem(R.id.action_rent_extend);
        MenuItem rentFinishItem = menu.findItem(R.id.action_rent_finish);

        Integer rentId = (Integer) cache.rent.getProperty(Rent.ID);

        if (rentId > 0) {
            rentCreateItem.setVisible(false);

            rentExtendItem.setVisible(true);
            rentFinishItem.setVisible(true);
        } else {
            rentExtendItem.setVisible(false);
            rentFinishItem.setVisible(false);
        }

        MenuItem bookEditItem = menu.findItem(R.id.action_book_edit);
        MenuItem bookRemoveItem = menu.findItem(R.id.action_book_remove);

        if ((Boolean) person.getProperty(Person.ADMIN)) {
            bookEditItem.setVisible(true);
            bookRemoveItem.setVisible(true);
        } else {
            bookEditItem.setVisible(false);
            bookRemoveItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rent_create:
                cache.executeTask(AsyncRentStartEnd.class, cache.rent);
                break;
            case R.id.action_rent_extend:
                cache.executeTask(AsyncRentNextEnd.class, cache.rent);
                break;
            case R.id.action_rent_finish:
                DialogRentFinish finishDialog = new DialogRentFinish();
                finishDialog.show(getFragmentManager(), "RentFinish");
                break;
            case R.id.action_book_edit:
                DialogBookEdit editDialog = DialogBookEdit.newInstance((Book) cache.rent.getProperty(Rent.BOOK));
                editDialog.show(getFragmentManager(), "BookEdit");
                break;
            case R.id.action_book_remove:
                DialogBookRemove removeDialog = new DialogBookRemove();
                removeDialog.show(getFragmentManager(), "BookRemove");
                break;
        }

        return true;
    }

    private class BookHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utility.MESSAGE_UPDATE_BOOK:
                    if (msg.obj != null) {
                        cache.rent.setProperty(Rent.BOOK, msg.obj);
                    }

                    Book object = (Book) cache.rent.getProperty(Rent.BOOK);

                    txtTitle.setText((String) object.getProperty(Book.TITLE));
                    txtAuthor.setText((String) object.getProperty(Book.AUTHOR));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String buffer = dateFormat.format((Date) object.getProperty(Book.PUBLISH_DATE));
                    txtPublish.setText(buffer);

                    txtDesc.setText((String) object.getProperty(Book.DESCRIPTION));

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    byte[] data = (byte[]) object.getProperty(Book.COVER);
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
                    imgCover.setImageBitmap(image);
                    break;

                case Utility.MESSAGE_UPDATE_RENT:
                    if (msg.obj != null) {
                        cache.rent = (Rent) msg.obj;
                    }

                    cache.executeTask(AsyncRentStatus.class, cache.rent);

                    invalidateOptionsMenu();
                    break;

                case Utility.MESSAGE_RENT_STATUS:
                    if (msg.obj != null) {
                        cache.status = (String) msg.obj;
                    }

                    String status = cache.status;

                    txtStatus.setText(status);
                    break;

                case Utility.MESSAGE_DIALOG_BOOK_EDIT:
                    cache.executeTask(AsyncBookEdit.class, (Book) msg.obj);
                    break;

                case Utility.MESSAGE_DIALOG_BOOK_REMOVE:
                    cache.executeTask(AsyncBookRemove.class, (Book) cache.rent.getProperty(Rent.BOOK));
                    break;

                case Utility.MESSAGE_DIALOG_RENT_CREATE:
                    cache.executeTask(AsyncRentCreate.class, (Rent) msg.obj);
                    break;

                case Utility.MESSAGE_DIALOG_RENT_EXTEND:
                    cache.executeTask(AsyncRentExtend.class, (Rent) msg.obj);
                    break;

                case Utility.MESSAGE_DIALOG_RENT_FINISH:
                    cache.executeTask(AsyncRentFinish.class, cache.rent);
                    break;

                case Utility.MESSAGE_LAYOUT_VISIBILITY:
                    layoutBook.setVisibility((int) msg.obj);
                    break;

                case Utility.MESSAGE_ACTIVITY_FINISH:
                    finish();
                    break;
            }
        }
    }

    private class BookFragmentCache extends FragmentCache {

        public Rent rent;

        public String status;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            executeTask(AsyncBookGet.class, (Book) rent.getProperty(Rent.BOOK));
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (task.getStatus() == AsyncTask.Status.RUNNING) {
                progress.show();

                sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.INVISIBLE);
            }
        }

    }

}
