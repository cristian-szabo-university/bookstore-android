package com.videogamelab.bookstore.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.activity.ActivityPerson;
import com.videogamelab.webservices.Person;

import java.util.Calendar;
import java.util.Date;

public class DialogPersonModify extends DialogFragment {

    private FragmentCache cache;

    private Person oldPerson;

    private BitmapFactory.Options bitmapOptions;

    private EditText editUsername;
    private EditText editPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private DatePicker dpBirthDate;
    private Spinner spnGender;
    private ImageView imgAvatar;

    public static DialogPersonModify newInstance(Person person) {
        DialogPersonModify f = new DialogPersonModify();

        Bundle args = new Bundle();
        args.putParcelable(Utility.OBJECT_PERSON, person);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oldPerson = getArguments().getParcelable(Utility.OBJECT_PERSON);

        bitmapOptions = new BitmapFactory.Options();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ActivityPerson) {
            cache = ((ActivityPerson) activity).getFragmentCache();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_person, null);

        editUsername = (EditText) v.findViewById(R.id.editUsername);
        editUsername.setText((String) oldPerson.getProperty(Person.USERNAME));
        editPassword = (EditText) v.findViewById(R.id.editPassword);
        editFirstName = (EditText) v.findViewById(R.id.editFirstName);
        editFirstName.setText((String) oldPerson.getProperty(Person.FIRST_NAME));
        editLastName = (EditText) v.findViewById(R.id.editLastName);
        editLastName.setText((String) oldPerson.getProperty(Person.LAST_NAME));

        dpBirthDate = (DatePicker) v.findViewById(R.id.dpBirthDate);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -Utility.PERSON_BIRTHDAY_YEAR_MIN);
        dpBirthDate.setMaxDate(c.getTime().getTime());
        c.setTime((Date) oldPerson.getProperty(Person.BIRTH_DATE));
        dpBirthDate.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        spnGender = (Spinner) v.findViewById(R.id.spnGender);
        Integer pos = ((ArrayAdapter<CharSequence>) spnGender.getAdapter()).getPosition((String) oldPerson.getProperty(Person.GENDER));
        spnGender.setSelection(pos);

        imgAvatar = (ImageView) v.findViewById(R.id.imgAvatar);
        byte[] data = (byte[]) oldPerson.getProperty(Person.AVATAR);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
        imgAvatar.setImageBitmap(image);
        imgAvatar.setOnClickListener(new AvatarButtonListener());

        builder.setView(v);
        builder.setTitle(R.string.dialog_title_modify);
        builder.setPositiveButton(R.string.dialog_button_modify, new ModifyButtonListener());

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bmp = BitmapFactory.decodeFile(picturePath);
                    float density = getActivity().getResources().getDisplayMetrics().density;
                    bmp = Utility.scaleBitmap(density, 320, 180, bmp);
                    imgAvatar.setImageBitmap(bmp);
                }
                break;
        }
    }

    private class ModifyButtonListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Calendar c = Calendar.getInstance();
            c.set(dpBirthDate.getYear(), dpBirthDate.getMonth(), dpBirthDate.getDayOfMonth());

            String password = editPassword.getText().toString();
            if (!password.isEmpty()) {
                password = Utility.MD5(password);
            } else {
                password = (String) oldPerson.getProperty(Person.PASSWORD);
            }

            Person newPerson = new Person();
            newPerson.setProperty(Person.ID, oldPerson.getProperty(Person.ID));
            newPerson.setProperty(Person.USERNAME, editUsername.getText().toString());
            newPerson.setProperty(Person.PASSWORD, password);
            newPerson.setProperty(Person.FIRST_NAME, editFirstName.getText().toString());
            newPerson.setProperty(Person.LAST_NAME, editLastName.getText().toString());
            newPerson.setProperty(Person.BIRTH_DATE, c.getTime());
            newPerson.setProperty(Person.GENDER, spnGender.getSelectedItem().toString());

            Bitmap newBmp = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();

            byte[] data = (byte[]) oldPerson.getProperty(Person.AVATAR);
            Bitmap oldBmp = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);

            if (!oldBmp.sameAs(newBmp)) {
                newPerson.setProperty(Person.AVATAR, newBmp);
            } else {
                newPerson.setProperty(Person.AVATAR, oldPerson.getProperty(Person.AVATAR));
            }

            cache.sendMessage(Utility.MESSAGE_DIALOG_PERSON_MODIFY, newPerson);
        }

    }

    private class AvatarButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, 1);
        }

    }

}
