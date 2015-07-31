package com.groupq.sth.vintellig.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by sth on 11/06/15.
 */
public class ReadNewInviteFileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ReadNewInviteFile",getIntent().getAction());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
