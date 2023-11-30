package com.pax.vas.stacklytics.sample;

import android.Manifest;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pax.vas.stackly.Stackly;
import com.pax.vas.stackly.event.bean.EventInfo;
import com.pax.vas.stackly.event.exception.EventFailedException;
import com.pax.vas.stackly.exception.presenter.BreakPad;

import java.util.HashMap;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CrashActivity";
    private static final int PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_LOGS,}, PERMISSION_CODE);
        }
    }

    private void initView() {
        findViewById(R.id.jvm_crash).setOnClickListener(this);
        findViewById(R.id.native_crash).setOnClickListener(this);
        findViewById(R.id.anr_crash).setOnClickListener(this);
        findViewById(R.id.send_event).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jvm_crash:
                int c = 10 / 0;
                break;
            case R.id.native_crash:
                BreakPad.BreakPadTest();
                break;
            case R.id.anr_crash:
                SystemClock.sleep(10000);
                break;
            case R.id.send_event:
                sendEvent();
                break;
        }
    }

    private void sendEvent() {
        Map<String, String> param = new HashMap<>();
        EventInfo eventInfo = EventInfo.newBuilder().setEventId("eventId").setEventTime(System.currentTimeMillis()).setParam(param).build();
        try {
            Stackly.handleEvent(eventInfo);
        } catch (EventFailedException e) {
            e.printStackTrace();
        }
    }
}
