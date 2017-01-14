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
import com.videogamelab.bookstore.activity.ActivityBook;
import com.videogamelab.webservices.Book;

import java.util.Calendar;
import java.util.Date;

public class DialogBookEdit extends DialogFragment {

    private FragmentCache cache;

    private BitmapFactory.Options bitmapOptions;

    private Book oldBook;

    private EditText editTitle;
    private EditText editAuthor;
    private DatePicker dpPublish;
    private EditText editDesc;
    private ImageView imgCover;

    public static DialogBookEdit newInstance(Book book) {
        DialogBookEdit dialog = new DialogBookEdit();

        Bundle args = new Bundle();
        args.putParcelable(Utility.OBJECT_BOOK, book);

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        bitmapOptions = new BitmapFactory.Options();

        oldBook = args.getParcelable(Utility.OBJECT_BOOK);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ActivityBook) {
            cache = ((ActivityBook) activity).getFragmentCache();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_book, null);

        editTitle = (EditText) v.findViewById(R.id.editTitle);
        editTitle.setText((String) oldBook.getProperty(Book.TITLE));
        editAuthor = (EditText) v.findViewById(R.id.editAuthor);
        editAuthor.setText((String) oldBook.getProperty(Book.AUTHOR));

        dpPublish = (DatePicker) v.findViewById(R.id.dpPublish);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -Utility.BOOK_PUBLISH_YEAR_MIN);
        dpPublish.setMaxDate(c.getTime().getTime());
        c.setTime((Date) oldBook.getProperty(Book.PUBLISH_DATE));
        dpPublish.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        editDesc = (EditText) v.findViewById(R.id.editDesc);
        editDesc.setText((String) oldBook.getProperty(Book.DESCRIPTION));

        imgCover = (ImageView) v.findViewById(R.id.imgCover);

        byte[] data = (byte[]) oldBook.getProperty(Book.COVER);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);

        imgCover.setImageBitmap(image);
        imgCover.setOnClickListener(new CoverButtonListener());

        builder.setView(v);
        builder.setTitle(R.string.dialog_title_edit);
        builder.setPositiveButton(R.string.dialog_button_edit, new EditButtonListener());

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

    private class EditButtonListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Book newBook = new Book();

            newBook.setProperty(Book.ID, oldBook.getProperty(Book.ID));
            newBook.setProperty(Book.TITLE, editTitle.getText().toString());
            newBook.setProperty(Book.AUTHOR, editAuthor.getText().toString());

            Calendar c = Calendar.getInstance();
            c.set(dpPublish.getYear(), dpPublish.getMonth(), dpPublish.getDayOfMonth());
            newBook.setProperty(Book.PUBLISH_DATE, c.getTime());

            newBook.setProperty(Book.DESCRIPTION, editDesc.getText().toString());

            Bitmap newBmp = ((BitmapDrawable) imgCover.getDrawable()).getBitmap();

            byte[] pixels = (byte[]) oldBook.getProperty(Book.COVER);
            Bitmap oldBmp = BitmapFactory.decodeByteArray(pixels, 0, pixels.length, bitmapOptions);

            if (!oldBmp.sameAs(newBmp)) {
                newBook.setProperty(Book.COVER, newBmp);
            }

            cache.sendMessage(Utility.MESSAGE_DIALOG_BOOK_EDIT, newBook);
        }

    }

}
