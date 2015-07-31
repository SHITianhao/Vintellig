package com.groupq.sth.vintellig.model.shareAccess;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by sth on 14/06/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TextView resultView;
    private int hourOfDay=99,minute=99;

    public TimePickerFragment(TextView resultView){
        this.resultView = resultView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        resultView.setText(hourOfDay+":"+minute);
    }

    public int getHourOfDay(){
        return hourOfDay;
    }

    public int getMinute(){
        return minute;
    }
}