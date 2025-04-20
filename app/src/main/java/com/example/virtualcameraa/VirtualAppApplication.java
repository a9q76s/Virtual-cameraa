package com.example.virtualcameraa;

import android.app.Application;
import android.content.Context;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.stub.StubManifest;

public class VirtualAppApplication extends Application {
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            // تهيئة VirtualApp
            VirtualCore.get().startup(base);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // تهيئة الإعدادات والمكونات الإضافية
        Settings.init(this);
        StubManifest.AUTHORITY_PREFIX = getPackageName() + ".provider_";
    }
}
