package com.videogamelab.bookstore.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.videogamelab.bookstore.FragmentCache;
import com.videogamelab.bookstore.R;
import com.videogamelab.bookstore.Utility;
import com.videogamelab.bookstore.activity.ActivityBook;
import com.videogamelab.webservices.Rent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogRentExtend extends DialogFragment {

    private FragmentCache cache;

    private Rent oldRent;
    private Date availableDate;
    private Integer maxRentDays;

    private TextView txtFromDate;
    private NumberPicker numPickDays;

    public static DialogRentExtend newInstance(Rent rent, Date availableDate, Integer maxRentDays) {
        DialogRentExtend dialog = new DialogRentExtend();

        Bundle args = new Bundle();
        args.putParcelable(Utility.OBJECT_RENT, rent);
        args.putLong("AvailableDate", availableDate.getTime());
        args.putInt("MaxRentDays", maxRentDays);

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        oldRent = args.getParcelable(Utility.OBJECT_RENT);
        availableDate = new Date(args.getLong("AvailableDate"));
        maxRentDays = args.getInt("MaxRentDays");
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
        View v = inflater.inflate(R.layout.fragment_rent, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        txtFromDate = (TextView) v.findViewById(R.id.txtFromDate);
        txtFromDate.setText(dateFormat.format(availableDate));

        numPickDays = (NumberPicker) v.findViewById(R.id.pnDays);
        numPickDays.setMinValue(1);
        numPickDays.setMaxValue(maxRentDays);
        numPickDays.setValue((int) Math.floor((double)maxRentDays / 2.0) + 1);

        builder.setView(v);
        builder.setTitle(R.string.dialog_title_extend);
        builder.setPositiveButton(R.string.dialog_button_extend, new ExtendButtonListener());

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    private class ExtendButtonListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Rent newRent = new Rent();
            newRent.setProperty(Rent.ID, oldRent.getProperty(Rent.ID));
            newRent.setProperty(Rent.START_DATE, oldRent.getProperty(Rent.START_DATE));
            newRent.setProperty(Rent.END_DATE, Utility.addDay(availableDate, numPickDays.getValue()));
            newRent.setProperty(Rent.BOOK, oldRent.getProperty(Rent.BOOK));
            newRent.setProperty(Rent.PERSON, oldRent.getProperty(Rent.PERSON));

            cache.sendMessage(Utility.MESSAGE_DIALOG_RENT_EXTEND, newRent);
        }

    }

}
