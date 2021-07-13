package com.pax.vas.stacklytics.sample;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

public class SystemPropertiesProxy {
    /**
     * 根据给定Key获取值.
     *
     * @return 如果不存在该key则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static String get(Context context, String key) throws IllegalArgumentException {
        String ret = "";
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
//参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = SystemProperties.getMethod("get", paramTypes);
//参数
            Object[] params = new Object[1];
            params[0] = new String(key);
            ret = (String) get.invoke(SystemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            Log.e("SystemPropertiesProxy", "e:" + e);
            ret = null;
        }
        return ret;
    }
}
