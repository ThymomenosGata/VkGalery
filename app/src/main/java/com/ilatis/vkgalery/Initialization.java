package com.ilatis.vkgalery;

import android.app.Application;

import com.vk.sdk.VKSdk;

public class Initialization extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
