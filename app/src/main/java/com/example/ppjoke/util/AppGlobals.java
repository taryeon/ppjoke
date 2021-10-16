package com.example.ppjoke.util;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppGlobals {

    private static Application mApplication;

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    public static Application getApplication(){
        if (mApplication == null) {
            Method method = null;
            try {
                method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
                mApplication = (Application) method.invoke(null, (Object[]) null);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return mApplication;
    }
}
