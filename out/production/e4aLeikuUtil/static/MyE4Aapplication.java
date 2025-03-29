package com.e4a.runtime.android;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.e4a.runtime.上下文操作;
import com.suiyuan.util.ResourceUtils;

import java.lang.Thread.UncaughtExceptionHandler;

public class MyE4Aapplication extends E4Aapplication {
    private UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            e.printStackTrace();
        }
    };

    public MyE4Aapplication() {
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this.handler);

        上下文操作.置全局上下文(this);


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
