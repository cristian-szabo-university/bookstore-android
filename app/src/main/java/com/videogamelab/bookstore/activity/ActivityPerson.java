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
import com.videogamelab.bookstore.async.AsyncPersonDelete;
import com.videogamelab.bookstore.async.AsyncPersonDisconnect;
import com.videogamelab.bookstore.async.AsyncPersonGet;
import com.videogamelab.bookstore.async.AsyncPersonModify;
import com.videogamelab.bookstore.dialog.DialogPersonDelete;
import com.videogamelab.bookstore.dialog.DialogPersonDisconnect;
import com.videogamelab.bookstore.dialog.DialogPersonModify;
import com.videogamelab.webservices.Person;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ActivityPerson extends Activity {

    private PersonFragmentCache cache;

    private RelativeLayout layoutPerson;
    private TextView txtTitle;
    private TextView txtFullName;
    private TextView txtBirthDate;
    private TextView txtGender;
    private ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle cacheState) {
        super.onCreate(cacheState);
        setContentView(R.layout.activity_person);

        layoutPerson = (RelativeLayout) findViewById(R.id.layoutPerson);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtFullName = (TextView) findViewById(R.id.txtFullName);
        txtBirthDate = (TextView) findViewById(R.id.txtBirthDate);
        txtGender = (TextView) findViewById(R.id.txtGender);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);

        cache = (PersonFragmentCache) getFragmentCache();

        cache.queue = new PersonHandler();
    }

    public FragmentCache getFragmentCache() {
        FragmentManager fm = getFragmentManager();

        PersonFragmentCache frag = (PersonFragmentCache) fm.findFragmentByTag(Utility.CACHE_PERSON);

        if (frag == null) {
            frag = new PersonFragmentCache();

            fm.beginTransaction().add(frag, Utility.CACHE_PERSON).commit();

            Bundle data = getIntent().getExtras();
            frag.person = data.getParcelable(Utility.OBJECT_PERSON);
        }

        return frag;
    }

    @Override
    protected void onStart() {
        super.onStart();

        cache.sendMessage(Utility.MESSAGE_UPDATE_PERSON, null);
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra(Utility.OBJECT_PERSON, cache.person);

        setResult(RESULT_OK, data);

        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_member, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_member_delete:
                DialogPersonDelete deleteDialog = new DialogPersonDelete();
                deleteDialog.show(getFragmentManager(), "PersonDelete");
                break;
            case R.id.action_member_modify:
                DialogPersonModify modifyDialog = DialogPersonModify.newInstance(cache.person);
                modifyDialog.show(getFragmentManager(), "PersonModify");
                break;
            case R.id.action_member_disconnect:
                DialogPersonDisconnect disconnectDialog = new DialogPersonDisconnect();
                disconnectDialog.show(getFragmentManager(), "PersonDisconnect");
                break;
        }

        return true;
    }

    private class PersonHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utility.MESSAGE_UPDATE_PERSON:
                    if (msg.obj != null) {
                        cache.person = (Person) msg.obj;
                    }

                    Person person = cache.person;

                    txtTitle.setText("Welcome, " + (String) person.getProperty(Person.FIRST_NAME) + "!");
                    txtFullName.setText((String) person.getProperty(Person.FIRST_NAME) + " " + (String) person.getProperty(Person.LAST_NAME));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String buffer = dateFormat.format((Date) person.getProperty(Person.BIRTH_DATE));
                    txtBirthDate.setText(buffer);

                    txtGender.setText((String) person.getProperty(Person.GENDER));

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    byte[] data = (byte[]) person.getProperty(Person.AVATAR);
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
                    imgAvatar.setImageBitmap(image);
                    break;

                case Utility.MESSAGE_DIALOG_PERSON_MODIFY:
                    cache.executeTask(AsyncPersonModify.class, (Person) msg.obj);
                    break;

                case Utility.MESSAGE_DIALOG_PERSON_DELETE:
                    cache.executeTask(AsyncPersonDelete.class, cache.person);
                    break;

                case Utility.MESSAGE_DIALOG_PERSON_DISCONNECT:
                    cache.executeTask(AsyncPersonDisconnect.class, null);
                    break;

                case Utility.MESSAGE_LAYOUT_VISIBILITY:
                    layoutPerson.setVisibility((int) msg.obj);
                    break;

                case Utility.MESSAGE_ACTIVITY_FINISH:
                    finish();
                    break;
            }
        }

    };

    private class PersonFragmentCache extends FragmentCache {

        public Person person;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            executeTask(AsyncPersonGet.class, person);
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
