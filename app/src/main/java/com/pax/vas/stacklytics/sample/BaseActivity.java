package com.pax.vas.stacklytics.sample;

import androidx.appcompat.app.AppCompatActivity;

import com.pax.vas.stackly.reporter.Stackly;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        Stackly.I.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Stackly.I.onPause(this);
    }
}
