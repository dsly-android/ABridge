package com.android.dsly.aidlclient;

import android.app.Application;

import com.android.dsly.abridge.ABridge;

/**
 * [description]
 * author: yifei
 * created at 18/6/3 下午8:40
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ABridge.init(this, "com.sjtu.yifei.aidlserver", ABridge.AbridgeType.AIDL);
        ABridge.setLogDebug(BuildConfig.DEBUG);
    }

    @Override
    public void onTerminate() {
        ABridge.recycle();
        super.onTerminate();
    }
}
