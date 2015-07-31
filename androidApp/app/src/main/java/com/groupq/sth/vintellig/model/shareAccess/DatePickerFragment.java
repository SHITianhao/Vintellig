package com.groupq.sth.vintellig.model.shareAccess;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by sth on 14/06/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView resultView;
    private int year=0,month=0,day=0;

    public DatePickerFragment(TextView resultView){
        super();
        this.resultView = resultView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        this.year = year;
        this.month = month;
        this.day = day;
        resultView.setText(month+"-"+day+"-"+year);
    }

    public int getYear(){
        return year;
    }
    public int getmonth(){
        return month;
    }
    public int getDay(){
        return day;
    }
}