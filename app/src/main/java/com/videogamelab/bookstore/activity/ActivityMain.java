package com.videogamelab.bookstore.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.async.AsyncBookAdd;
import com.videogamelab.bookstore.async.AsyncBookGetAll;
import com.videogamelab.bookstore.async.AsyncPersonConnect;
import com.videogamelab.bookstore.async.AsyncPersonRegister;
import com.videogamelab.bookstore.dialog.DialogBookAdd;
import com.videogamelab.bookstore.dialog.DialogPersonConnect;
import com.videogamelab.bookstore.dialog.DialogPersonRegister;
import com.videogamelab.webservices.Book;
import com.videogamelab.webservices.Person;
import com.videogamelab.webservices.VectorBook;
import com.videogamelab.webservices.VectorByte;

public class ActivityMain extends Activity {

    private MainFragmentCache cache;

    private RelativeLayout layoutBookstore;
    private ListView listBook;

    @Override
    protected void onCreate(Bundle cacheState) {
        super.onCreate(cacheState);
        setContentView(R.layout.activity_bookstore);

        listBook = (ListView) findViewById(R.id.listBook);
        layoutBookstore = (RelativeLayout) findViewById(R.id.layoutMain);

        cache = (MainFragmentCache) getFragmentCache();

        cache.queue = new BookstoreHandler();
    }

    public FragmentCache getFragmentCache() {
        FragmentManager fm = getFragmentManager();

        MainFragmentCache frag = (MainFragmentCache) fm.findFragmentByTag(Utility.CACHE_MAIN);

        if (frag == null) {
            frag = new MainFragmentCache();

            fm.beginTransaction().add(frag, Utility.CACHE_MAIN).commit();
        }

        return frag;
    }

    @Override
    protected void onStart() {
        super.onStart();

        cache.sendMessage(Utility.MESSAGE_UPDATE_BOOK_LIST, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bookstore, menu);

        MenuItem addBookItem = menu.findItem(R.id.action_book_add);

        Boolean adminAccess = (Boolean) cache.person.getProperty(Person.ADMIN);
        if (adminAccess) {
            addBookItem.setVisible(true);
        } else {
            addBookItem.setVisible(false);
        }

        MenuItem connectItem = menu.findItem(R.id.action_member_connect);
        MenuItem registerItem = menu.findItem(R.id.action_member_register);

        Integer personId = (Integer) cache.person.getProperty(Person.ID);
        if (personId > 0) {
            connectItem.setVisible(false);
            registerItem.setVisible(false);
        } else {
            connectItem.setVisible(true);
            registerItem.setVisible(true);
        }

        MenuItem accountItem = menu.findItem(R.id.action_member_account);

        if (personId > 0) {
            accountItem.setVisible(true);
        } else {
            accountItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_member_register:
                DialogPersonRegister registerDialog = new DialogPersonRegister();
                registerDialog.show(getFragmentManager(), "PersonRegister");
                break;
            case R.id.action_member_connect:
                DialogPersonConnect connectDialog = new DialogPersonConnect();
                connectDialog.show(getFragmentManager(), "PersonConnect");
                break;
            case R.id.action_member_account:
                Intent i = new Intent(this, ActivityPerson.class);
                cache.person.setProperty(Person.AVATAR, new VectorByte());
                i.putExtra(Utility.OBJECT_PERSON, cache.person);
                startActivityForResult(i, Utility.PERSON_ACTIVITY);
                break;
            case R.id.action_book_add:
                DialogBookAdd addFrag = new DialogBookAdd();
                addFrag.show(getFragmentManager(), "BookAdd");
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == Utility.PERSON_ACTIVITY ||
                requestCode == Utility.BOOK_ACTIVITY)) {
            cache.person = data.getParcelableExtra(Utility.OBJECT_PERSON);

            invalidateOptionsMenu();

            cache.executeTask(AsyncBookGetAll.class, null);
        }
    }

    public class BookstoreHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utility.MESSAGE_UPDATE_BOOK_LIST:
                    if (msg.obj != null) {
                        cache.bookList = (VectorBook) msg.obj;
                    }

                    BookArrayAdapter adapter = new BookArrayAdapter(ActivityMain.this, cache.bookList);
                    listBook.setAdapter(adapter);
                    break;

                case Utility.MESSAGE_UPDATE_PERSON:
                    if (msg.obj != null) {
                        cache.person = (Person) msg.obj;
                    }

                    invalidateOptionsMenu();
                    break;

                case Utility.MESSAGE_UPDATE_BOOK:
                    cache.bookList.add((Book) msg.obj);

                    cache.sendMessage(Utility.MESSAGE_UPDATE_BOOK_LIST, null);
                    break;

                case Utility.MESSAGE_DIALOG_BOOK_ADD:
                    cache.executeTask(AsyncBookAdd.class, (Book) msg.obj);
                    break;

                case Utility.MESSAGE_DIALOG_PERSON_CONNECT:
                    cache.executeTask(AsyncPersonConnect.class, (Person) msg.obj);
                    break;

                case Utility.MESSAGE_DIALOG_PERSON_REGISTER:
                    cache.executeTask(AsyncPersonRegister.class, (Person) msg.obj);
                    break;

                case Utility.MESSAGE_LAYOUT_VISIBILITY:
                    layoutBookstore.setVisibility((int) msg.obj);
                    break;
            }
        }

    };

    public class MainFragmentCache extends FragmentCache {

        public Person person;

        public VectorBook bookList;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            person = new Person();

            bookList = new VectorBook();

            executeTask(AsyncBookGetAll.class, null);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
                progress.show();

                sendMessage(Utility.MESSAGE_LAYOUT_VISIBILITY, View.INVISIBLE);
            }
        }

    }

    private class BookArrayAdapter extends ArrayAdapter<Book> {

        private BitmapFactory.Options bitmapOptions;

        private class ViewHolder {
            public TextView txtTitle;
            public TextView txtAuthor;
            public ImageView imgCover;
            public Button btnView;
        }

        public BookArrayAdapter(Context context, VectorBook books) {
            super(context, R.layout.listview_book, books);

            bitmapOptions = new BitmapFactory.Options();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ActivityMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_book, null);

                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                holder.txtAuthor = (TextView) convertView.findViewById(R.id.txtAuthor);
                holder.imgCover = (ImageView) convertView.findViewById(R.id.imgCover);
                holder.btnView = (Button) convertView.findViewById(R.id.btnView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Book book = getItem(position);
            holder.txtTitle.setText((String) book.getProperty(Book.TITLE));
            holder.txtAuthor.setText((String) book.getProperty(Book.AUTHOR));
            holder.btnView.setOnClickListener(new ViewButtonListener(position));

            byte[] data = (byte[]) book.getProperty(Book.COVER);
            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
            holder.imgCover.setImageBitmap(image);

            return convertView;
        }

        private class ViewButtonListener implements View.OnClickListener {

            private Integer position;

            ViewButtonListener(Integer position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMain.this, ActivityBook.class);

                Person person = cache.person;
                person.setProperty(Person.AVATAR, new VectorByte());
                i.putExtra(Utility.OBJECT_PERSON, person);

                Book book = getItem(position);
                book.setProperty(Book.COVER, new VectorByte());
                i.putExtra(Utility.OBJECT_BOOK, book);

                ActivityMain.this.startActivityForResult(i, Utility.BOOK_ACTIVITY);
            }
        }

    }

}
