package org.niray.drivertime.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TimeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent service = new Intent(TimeActivity.this, FloatService.class);
        startService(service);
        finish();
//      stopService(serviceStop);
    }
}