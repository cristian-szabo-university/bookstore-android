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

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.activity.ActivityMain;
import com.videogamelab.webservices.Book;

import java.util.Calendar;

public class DialogBookAdd extends DialogFragment {

    private FragmentCache cache;

    private EditText editTitle;
    private EditText editAuthor;
    private DatePicker dpPublish;
    private EditText editDesc;
    private ImageView imgCover;

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
        View v = inflater.inflate(R.layout.fragment_book, null);

        editTitle = (EditText) v.findViewById(R.id.editTitle);
        editAuthor = (EditText) v.findViewById(R.id.editAuthor);

        dpPublish = (DatePicker) v.findViewById(R.id.dpPublish);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -Utility.BOOK_PUBLISH_YEAR_MIN);
        dpPublish.setMaxDate(c.getTime().getTime());
        dpPublish.updateDate(
            Utility.BOOK_PUBLISH_DEFAULT_YEAR,
            Utility.BOOK_PUBLISH_DEFAULT_MONTH - 1,
            Utility.BOOK_PUBLISH_DEFAULT_DAY);

        editDesc = (EditText) v.findViewById(R.id.editDesc);
        imgCover = (ImageView) v.findViewById(R.id.imgCover);
        imgCover.setOnClickListener(new CoverButtonListener());

        builder.setView(v);
        builder.setTitle(R.string.dialog_title_add);
        builder.setPositiveButton(R.string.dialog_button_add, new AddButtonListener());

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

                    Cursor cursor = getActivity().getContentResolver().query(
                            selectedImage, filePathColumn,
                            null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bmp = BitmapFactory.decodeFile(picturePath);
                    float density = getActivity().getResources().getDisplayMetrics().density;
                    bmp = Utility.scaleBitmap(density, 320, 180, bmp);
                    imgCover.setImageBitmap(bmp);
                }
                break;
        }
    }

    private class CoverButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, 1);
        }

    }

    private class AddButtonListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Calendar c = Calendar.getInstance();
            c.set(dpPublish.getYear(), dpPublish.getMonth(), dpPublish.getDayOfMonth());

            Book newBook = new Book();
            newBook.setProperty(Book.TITLE, editTitle.getText().toString());
            newBook.setProperty(Book.AUTHOR, editAuthor.getText().toString());
            newBook.setProperty(Book.PUBLISH_DATE, c.getTime());
            newBook.setProperty(Book.DESCRIPTION, editDesc.getText().toString());
            newBook.setProperty(Book.COVER, ((BitmapDrawable) imgCover.getDrawable()).getBitmap());

            cache.sendMessage(Utility.MESSAGE_DIALOG_BOOK_ADD, newBook);
        }
    }

}
