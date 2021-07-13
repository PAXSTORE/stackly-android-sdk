package com.pax.vas.stacklytics.sample;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.pax.vas.stacklytics.reporter.Stacklytics;
import com.pax.vas.stacklytics.sender.PackReportData;
import com.pax.vas.stacklytics.sender.ReportSenderListener;

import org.acra.collector.CrashReportData;

import java.lang.reflect.Method;

public class App extends Application implements PackReportData {
    private static final String TAG = App.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Stacklytics.I.install(this)
                .setFormUri("http://xxxxxxx")
                .setReportSenderListener(new ReportSenderListener<CrashReportData>() {
                    @Override
                    public void onSendStart() {
                        Log.d(TAG, "onSendStart");
                    }

                    @Override
                    public boolean bypass(CrashReportData crashReportData) {
                        Log.d(TAG, "bypass");
                        return false;
                    }


                    @Override
                    public void onSendCompleted() {
                        Log.d(TAG, "onSendCompleted");

                    }

                    @Override
                    public void onSendError(Throwable throwable) {
                        Log.d(TAG, "onSendError");
                    }
                })
                .setCustomPackData(this)
                .setSecret("xxxxxxx")//set your secret
                .setAlias(getSN(getApplicationContext()))//set your alias
                .init();

    }

    @SuppressLint({"MissingPermission"})
    public static String getSN(Context context) {
        String serialNumber = null;
        try {
            serialNumber = SystemPropertiesProxy.get(context, "ro.fac.sn");
            if (serialNumber == null || "".equals(serialNumber)) {
                if (Build.VERSION.SDK_INT >= 28) {
                    serialNumber = Build.getSerial();
                } else if (Build.VERSION.SDK_INT > 24) {
                    serialNumber = Build.SERIAL;
                } else {
                    Class<?> c = Class.forName("android.os.SystemProperties");
                    Method get = c.getMethod("get", String.class);
                    serialNumber = (String) get.invoke(c, new Object[]{"ro.serialno"});
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getSN error:" + e);
        }
        return serialNumber;
    }


    @Override
    public String getReportContent(CrashReportData crashReportData) {
        return "{\"REPORT_ID\":\"1234\"}";
    }
}
