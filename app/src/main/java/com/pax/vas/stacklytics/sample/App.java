package com.pax.vas.stacklytics.sample;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.pax.vas.stackly.reporter.Stackly;
import com.pax.vas.stackly.sender.strategy.ReportFlags;

import java.lang.reflect.Method;

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Stackly.I.install(this)
                .setFormUri("http://xxxxxxx")
                .setSecret("xxxxxxx")//set your secret
                .setAlias(getSN(getApplicationContext()))//set your alias
                .setFlags(ReportFlags.FLAG_ANR_WIFI_ONLY | ReportFlags.FLAG_JAVA_NOT_UPLOAD_TWO_HOURS)
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
}
