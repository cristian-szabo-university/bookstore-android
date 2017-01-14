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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.activity.ActivityMain;
import com.videogamelab.webservices.Person;

import java.util.Calendar;

public class DialogPersonRegister extends DialogFragment {

    private FragmentCache cache;

    private EditText editUsername;
    private EditText editPassword;
    private EditText editFirstName;
    private EditText editLastName;
    private Spinner spnGender;
    private DatePicker dpBirthDate;
    private ImageView imgAvatar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ActivityMain) {
            cache = ((ActivityMain) activity).getFragmentCache();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_person, null);

        editUsername = (EditText) v.findViewById(R.id.editUsername);
        editPassword = (EditText) v.findViewById(R.id.editPassword);
        editFirstName = (EditText) v.findViewById(R.id.editFirstName);
        editLastName = (EditText) v.findViewById(R.id.editLastName);

        dpBirthDate = (DatePicker) v.findViewById(R.id.dpBirthDate);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -Utility.PERSON_BIRTHDAY_YEAR_MIN);
        dpBirthDate.setMaxDate(c.getTime().getTime());
        dpBirthDate.updateDate(
            Utility.PERSON_BIRTHDAY_DEFAULT_YEAR,
            Utility.PERSON_BIRTHDAY_DEFAULT_MONTH - 1,
            Utility.PERSON_BIRTHDAY_DEFAULT_DAY);

        spnGender = (Spinner) v.findViewById(R.id.spnGender);
        imgAvatar = (ImageView) v.findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(new AvatarButtonListener());

        builder.setView(v);
        builder.setTitle(R.string.dialog_title_register);
        builder.setPositiveButton(R.string.dialog_button_register, new RegisterButtonListener());

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

    private class RegisterButtonListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(dpBirthDate.getYear(), dpBirthDate.getMonth(), dpBirthDate.getDayOfMonth());

            String password = editPassword.getText().toString();
            if (!password.isEmpty()) {
                password = Utility.MD5(password);
            }

            Person newPerson = new Person();
            newPerson.setProperty(Person.USERNAME, editUsername.getText().toString());
            newPerson.setProperty(Person.PASSWORD, password);
            newPerson.setProperty(Person.FIRST_NAME, editFirstName.getText().toString());
            newPerson.setProperty(Person.LAST_NAME, editLastName.getText().toString());
            newPerson.setProperty(Person.BIRTH_DATE, calendar.getTime());
            newPerson.setProperty(Person.GENDER, spnGender.getSelectedItem().toString());
            newPerson.setProperty(Person.AVATAR, ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap());

            cache.sendMessage(Utility.MESSAGE_DIALOG_PERSON_REGISTER, newPerson);
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
