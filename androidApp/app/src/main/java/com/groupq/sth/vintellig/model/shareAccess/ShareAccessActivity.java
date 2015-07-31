package com.groupq.sth.vintellig.model.shareAccess;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.groupq.sth.vintellig.Controller.MainActivity;
import com.groupq.sth.vintellig.R;
import com.groupq.sth.vintellig.model.manageKey.ReadKeyFromFileThread;
import com.groupq.sth.vintellig.ShareContext;
import com.groupq.sth.vintellig.model.connection.ShareAccessConnection;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by feng on 11/06/2015.
 */
public class ShareAccessActivity extends Activity{

    ShareContext shareContext;
    EditText dstAddress, emailMessage;
    Button share;
    TextView message,endDayTextView,startDayTextView,startTimeTextView,endTimeTextView;
    ImageButton startDaySelector, startTimeSelector, endDaySelector, endTimeSelector;
    File xmlSendFile;
    DatePickerFragment startDatePicker,endDatePicker;
    TimePickerFragment startTimePicker,endTimePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shareContext = (ShareContext)getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shareaccess);

        dstAddress = (EditText) findViewById(R.id.input_email);
        emailMessage = (EditText) findViewById(R.id.input_message);
        share = (Button) findViewById(R.id.share);
        message = (TextView) findViewById(R.id.share_message);

        //setUp selector buttons
        startDaySelector = (ImageButton) findViewById(R.id.select_start_date_button);
        endDaySelector = (ImageButton) findViewById(R.id.select_end_date_button);

        startTimeSelector = (ImageButton) findViewById(R.id.select_start_time_button);
        endTimeSelector = (ImageButton) findViewById(R.id.select_end_time_button);

        //set up textview
        startDayTextView = (TextView) findViewById(R.id.start_date);
        endDayTextView = (TextView) findViewById(R.id.end_date);
        startTimeTextView = (TextView) findViewById(R.id.start_time);
        endTimeTextView = (TextView) findViewById(R.id.end_time);

        // init textview
        startDayTextView.setText("Unlimited");
        endDayTextView.setText("Unlimited");
        startTimeTextView.setText("Unlimited");
        endTimeTextView.setText("Unlimited");

        // set up picker dialog
        startDatePicker = new DatePickerFragment(startDayTextView);
        endDatePicker = new DatePickerFragment(endDayTextView);
        startTimePicker = new TimePickerFragment(startTimeTextView);
        endTimePicker = new TimePickerFragment(endTimeTextView);

        // create send xml file
        xmlSendFile = new File(MainActivity.rootFolder.getAbsoluteFile()+File.separator+"request.xml");
        try {
            xmlSendFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_main) {
            Intent intent = new Intent();
            intent.setClass(ShareAccessActivity.this, MainActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_statement) {
            if(shareContext.getLoginState()) {
                Intent intent = new Intent();
                intent.setClass(ShareAccessActivity.this, ReadKeyFromFileThread.DeconnectActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void shareClick(View v){
        ShareAccessConnection shareAccessConnection = new ShareAccessConnection((ShareContext)getApplicationContext());
        if(shareContext.getLoginState()) {
            if (!dstAddress.getText().toString().matches("") && !emailMessage.getText().toString().matches("")) {
                writeXmlFile();
                boolean shareResult = false;
                try {
                    shareResult = shareAccessConnection.execute(dstAddress.getText().toString()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (shareResult) {
                    message.setText("ShareSuccess");
                    // share to others
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    File sharFile = new File(MainActivity.rootFolder.getPath() + File.separator + "welcome.vg");
                    Uri uri = Uri.fromFile(sharFile);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setType("*/*");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_with_others)));
                } else {
                    message.setText("ShareFail");
                }
            } else {
                message.setText("email address or message cannot be empty!");
            }
        } else {
            message.setText("you need to login first!");
        }
    }

    private void writeXmlFile(){
        FileOutputStream fileos = null;
        try{
            fileos = new FileOutputStream(xmlSendFile);

        }catch(FileNotFoundException e)
        {
            Log.e("FileNotFoundException",e.toString());
        }
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fileos, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "share");
            serializer.startTag(null, "sender");
            serializer.startTag(null, "name");
            serializer.text(shareContext.getUserName());
            serializer.endTag(null, "name");
            serializer.endTag(null, "sender");
            serializer.startTag(null, "receiver");
            serializer.startTag(null, "name");
            serializer.text(dstAddress.getText().toString());
            serializer.endTag(null, "name");
            serializer.startTag(null, "message");
            serializer.text(emailMessage.getText().toString());
            serializer.endTag(null, "message");

            serializer.startTag(null, "startDay");
            serializer.attribute(null, "year", Integer.toString(startDatePicker.getYear()));
            serializer.attribute(null, "month", Integer.toString(startDatePicker.getmonth()));
            serializer.attribute(null, "day", Integer.toString(startDatePicker.getDay()));
            serializer.endTag(null, "startDay");
            serializer.startTag(null, "startTime");
            serializer.attribute(null, "hour", Integer.toString(startTimePicker.getHourOfDay()));
            serializer.attribute(null, "minute", Integer.toString(startTimePicker.getMinute()));
            serializer.endTag(null, "startTime");

            serializer.startTag(null, "endDay");
            serializer.attribute(null, "year", Integer.toString(endDatePicker.getYear()));
            serializer.attribute(null, "month", Integer.toString(endDatePicker.getmonth()));
            serializer.attribute(null, "day", Integer.toString(endDatePicker.getDay()));
            serializer.endTag(null, "endDay");
            serializer.startTag(null, "endTime");
            serializer.attribute(null, "hour", Integer.toString(endTimePicker.getHourOfDay()));
            serializer.attribute(null,"minute",Integer.toString(endTimePicker.getMinute()));
            serializer.endTag(null, "endTime");

            serializer.endTag(null,"receiver");
            serializer.endTag(null,"share");
            serializer.endDocument();
            serializer.flush();
            fileos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showStartDatePickerDialog(View v) {
        startDatePicker.show(getFragmentManager(), "datePicker");
    }

    public void showEndDatePickerDialog(View v) {
        endDatePicker.show(getFragmentManager(), "datePicker");
    }

    public void showStartTimePickerDialog(View v) {
        startTimePicker.show(getFragmentManager(), "timePicker");
    }

    public void showEndTimePickerDialog(View v) {
        endTimePicker.show(getFragmentManager(), "timePicker");
    }

}
